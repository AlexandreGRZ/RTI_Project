package com.example.paymentclientsecure;


import com.example.paymentclientsecure.Modele.SharedDataModelSecure;
import com.hepl.model.Facture;
import com.hepl.paymentclient.Modele.SharedDataModel;
import com.hepl.paymentclient.numberCardController;
import com.hepl.protocol.Mycrypto;
import com.hepl.protocol.requests.*;
import com.hepl.protocol.responses.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PayementControllerSecure implements Initializable {
    public Button btnLogout;
    @FXML
    public Button btnLogin;
    @FXML
    public TextField TextFieldPassword;
    @FXML
    public TextField TextFieldLogin;
    @FXML
    public TableView TVFacture;
    @FXML
    public TableColumn TCNom;
    @FXML
    public TableColumn TCMontantAPayer;
    @FXML
    public TableColumn TCDate;
    @FXML
    public Button btnPayer;
    @FXML
    public Button btnRechercher;
    @FXML
    public TextField TxtFieldIdClient;
    public TableColumn TCpaye;
    @FXML
    private Label welcomeText;

    private Socket socket;
    private ObjectInputStream Input;
    private ObjectOutputStream Output;

    private ObservableList<Facture> facturesToDisplay;

    private SharedDataModelSecure sharedDataModel;

    private PublicKey clePubliqueServeur;

    private SecretKey cleSession;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Génération d'une clé de session
        KeyGenerator cleGen = null;
        cleSession = null;
        try {
            Security.addProvider(new BouncyCastleProvider());
            cleGen = KeyGenerator.getInstance("DES","BC");
            cleGen.init(new SecureRandom());
            cleSession = cleGen.generateKey();
            System.out.println("Génération d'une clé de session : " + cleSession);

            clePubliqueServeur = RecupereClePubliqueServeur();
            System.out.println("Récupération clé publique du serveur : " + clePubliqueServeur);


        } catch (NoSuchAlgorithmException | NoSuchProviderException | ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }

        try {
            socket = new Socket("127.0.0.1", 8090);
            System.out.println();
            Output = new ObjectOutputStream(socket.getOutputStream());
            Input = new ObjectInputStream(socket.getInputStream());
            System.out.println(cleSession);
            byte[] cleSessionCrypte;
            cleSessionCrypte = Mycrypto.CryptAsymRSA(clePubliqueServeur,cleSession.getEncoded());
            System.out.println("Cryptage asymétrique de la clé de session : " + new String(cleSessionCrypte));

            requestSecure req = new requestSecure();
            req.setData1(cleSessionCrypte);

            Output.writeObject(req);
        } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 BadPaddingException | NoSuchProviderException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

    }

    public void onLoginClick(ActionEvent actionEvent) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchProviderException {

        String Login = TextFieldLogin.getText();
        String password = TextFieldPassword.getText();

        LoginRequestSecure requestSecure = new LoginRequestSecure(Login, password);

        Output.writeObject(requestSecure);

        LoginResponse response = (LoginResponse) Input.readObject();

        if(response.isSuccess()){
            ChangeLoginInterface();
        }
    }

    public void onLogoutClick(ActionEvent actionEvent) throws IOException {

        LogoutRequest logoutRequest = new LogoutRequest();
        Output.writeObject(logoutRequest);
        ChangeLogOutInterface();
    }
    
    public void onActionRechercherBtn(ActionEvent actionEvent) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnrecoverableKeyException, CertificateException, KeyStoreException {

        int idClientToSearch = Integer.parseInt(TxtFieldIdClient.getText());

        getFactureSecureRequest getFacturesRequest = new getFactureSecureRequest(idClientToSearch, cleSession, RecupereClePrivateClient());

        Output.writeObject(getFacturesRequest);

        getFacturesSecureResponse getFacturesResponse = (getFacturesSecureResponse) Input.readObject();

        byte[] tabFacturesBytedecrypt = Mycrypto.DecryptSymDES(cleSession, getFacturesResponse.getFactureBytesCrypte());

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(tabFacturesBytedecrypt);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        ArrayList<Facture> factures = (ArrayList<Facture>) objectInputStream.readObject();
        objectInputStream.close();


        setFacturesToDisplay(FXCollections.observableArrayList());
        for (Facture f : factures) {
            getFacturesToDisplay().add(f);
        }

        TCNom.setCellValueFactory(new PropertyValueFactory<>("id"));
        TCDate.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TCMontantAPayer.setCellValueFactory(new PropertyValueFactory<>("date"));
        TCpaye.setCellValueFactory(new PropertyValueFactory<>("payed"));

        TVFacture.setItems(getFacturesToDisplay());
        
    }

    public void onActionPayerBtn(ActionEvent actionEvent) {

        if (sharedDataModel == null) {
            sharedDataModel = new SharedDataModelSecure();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("numberCardDemand.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        numberCardControllerSecure numberCardControllerSecure = loader.getController();

        numberCardControllerSecure.setSharedDataModel(sharedDataModel);

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Number Card Request");
        stage.setScene(scene);

        stage.setOnHiding(event -> {
            try {
                handleWindow2Closed();
            } catch (IOException | ClassNotFoundException | NoSuchPaddingException | IllegalBlockSizeException |
                     NoSuchAlgorithmException | BadPaddingException | NoSuchProviderException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        });
        stage.show();
    }

    private void handleWindow2Closed() throws IOException, ClassNotFoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {

        Facture factureAPayer = (Facture) TVFacture.getSelectionModel().getSelectedItem();

        payFactureSecureRequest payFactureRequest = new payFactureSecureRequest(factureAPayer.getId(),sharedDataModel.getCardNumber(), cleSession);

        Output.writeObject(payFactureRequest);

        payFacturesSecureResponse payFactureResponse = (payFacturesSecureResponse) Input.readObject();

        if(payFactureResponse.VerifyAuthenticity(cleSession)){
            if(payFactureResponse.isSuccess()){
                System.out.println("Réussis !");
            }   else{
                System.out.println("Raté !");
            }
        }else{
            System.out.println("Pas le bon envoyeur");
        }
    }
    public void ChangeLoginInterface(){
        btnLogin.setDisable(true);
        btnLogout.setDisable(false);
        btnRechercher.setDisable(false);
        btnPayer.setDisable(false);
    }

    public void ChangeLogOutInterface(){
        btnLogin.setDisable(false);
        btnLogout.setDisable(true);
        btnRechercher.setDisable(true);
        btnPayer.setDisable(true);

    }

    public ObservableList<Facture> getFacturesToDisplay() {
        return facturesToDisplay;
    }

    public void setFacturesToDisplay(ObservableList<Facture> facturesToDisplay) {
        this.facturesToDisplay = facturesToDisplay;
    }

    public void setSharedDataModel(SharedDataModelSecure sharedDataModel) {
        this.sharedDataModel = sharedDataModel;
    }

    public static PublicKey RecupereClePubliqueServeur() throws IOException, ClassNotFoundException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        // Désérialisation de la clé publique
        KeyStore ks = KeyStore.getInstance("JKS");

        ks.load(new FileInputStream("cleServeur/KeystoreClient.jks"),"Papyrusse007".toCharArray());
        X509Certificate certif = (X509Certificate)ks.getCertificate("Serveur");
        PublicKey cle = certif.getPublicKey();
        return cle;
    }

    public static PrivateKey RecupereClePrivateClient() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
    // Récupération de la clé privée de Jean-Marc dans le keystore de Jean-Marc
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("cleServeur/KeystoreClient.jks"),"Papyrusse007".toCharArray());

        PrivateKey cle = (PrivateKey) ks.getKey("Client","Papyrusse007".toCharArray());
        return cle;
    }
}