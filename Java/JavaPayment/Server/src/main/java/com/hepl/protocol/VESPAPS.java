package com.hepl.protocol;

import com.hepl.bridge.DbConnection;
import com.hepl.protocol.interfaces.ProtocolSecure;
import com.hepl.protocol.interfaces.Request;
import com.hepl.protocol.interfaces.Response;
import com.hepl.protocol.requests.*;
import com.hepl.protocol.responses.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.sql.SQLException;

import org.bouncycastle.jce.provider.*;

public class VESPAPS implements ProtocolSecure {

    @Override
    public synchronized Response handleRequest(Request request, DbConnection connection,PrivateKey clePriveeServeur, SecretKey ClientCle) throws EndConnectionException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException, IOException, SQLException, SignatureException, ClassNotFoundException {
        if(request instanceof requestSecure)
            return handleRequestSecure((requestSecure) request, connection, clePriveeServeur, ClientCle);
        if (request instanceof LoginRequestSecure)
            return handleLoginRequest((LoginRequestSecure) request, connection);
        if (request instanceof getFactureSecureRequest)
            return handleGetFacturesRequest((getFactureSecureRequest) request, connection, ClientCle);
        if (request instanceof payFactureSecureRequest)
            return handlePayFactureRequest((payFactureSecureRequest) request, connection, ClientCle);
        if (request instanceof LogoutRequest) {
            handleLogoutRequest();
            throw new EndConnectionException(null);
        }
        return null;
    }


    private synchronized LoginResponse handleLoginRequest(LoginRequestSecure request, DbConnection connection) throws NoSuchAlgorithmException, IOException, NoSuchProviderException, SQLException {
        System.out.println("Login request");

        String motDePasse = connection.getMotsDePasse(request.getLogin());
        if (motDePasse == null)
        {
            System.out.println("Client inconnu !");
            System.exit(0);
        }

        if (request.VerifyPassword(motDePasse)){
            System.out.println("Bienvenue " + request.getLogin() + " !");
            return new LoginResponse(true);
        }

        else
        {
            System.out.println("Mauvais mot de passe pour ");
            return new LoginResponse(false);
        }
    }

    private synchronized requestSecureResponse handleRequestSecure(requestSecure request, DbConnection connection, PrivateKey clePriveeServeur, SecretKey ClientCle) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        System.out.println("requestSecure request");

        byte[] cleSessionDecryptee;
        System.out.println("Clé session cryptée reçue = " + new String(request.getData1()));
        Security.addProvider(new BouncyCastleProvider());
        cleSessionDecryptee = Mycrypto.DecryptAsymRSA(clePriveeServeur,request.getData1());
        SecretKey cleSession = new SecretKeySpec(cleSessionDecryptee,"DES");
        System.out.println("Decryptage asymétrique de la clé de session...");

        return new requestSecureResponse(cleSession);
    }


    private synchronized getFacturesSecureResponse handleGetFacturesRequest(getFactureSecureRequest request, DbConnection connection, SecretKey cleSession) throws NoSuchAlgorithmException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, ClassNotFoundException, SQLException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        System.out.println("Get facture request");

        int idClient = request.getIdClient();

        PublicKey cleClient = RecupereClePublicClient();

        if(request.VerifySignature(cleClient)){
            System.out.println("Signature validée !");
            return new getFacturesSecureResponse(connection.getFactures(idClient), cleSession);
        }else {
            System.out.println("Signature invalide...");
        }
        return null;
    }

    private synchronized payFacturesSecureResponse handlePayFactureRequest(payFactureSecureRequest request, DbConnection connection, SecretKey cleSession) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException, IOException {
        System.out.println("Pay facture request");

        byte[] decryptedBytes = Mycrypto.DecryptSymDES(cleSession, request.getResponseCrypt());

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decryptedBytes);
        DataInputStream dis = new DataInputStream(byteArrayInputStream);

        try {

            String numCarteVisa = dis.readUTF();
            int id = dis.readInt();

            if(checkVisaCard(numCarteVisa)){
                if(connection.payFacture(id)){
                    System.out.println("c'est bon");
                    return new payFacturesSecureResponse(true, cleSession);
                }else{
                    System.out.println("pas bon");
                    return new payFacturesSecureResponse(false, cleSession);
                }
            }else{
                System.out.println("Visa pas bonne");
                return new payFacturesSecureResponse(false, cleSession);
            }
        } catch (IOException e) {
            // Gérer les exceptions liées à la lecture des données déchiffrées
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public static boolean checkVisaCard(String number) {
        int sum = 0;
        boolean oddNumber = true;
        int tmp;

        for (int i = number.length() - 1; i >= 0; i--) {
            if (oddNumber) {
                sum += (int) number.charAt(i) - '0';
            } else {
                tmp = (int) number.charAt(i) - '0';
                tmp *= 2;
                if (tmp >= 10)
                    tmp = (tmp / 10) + (tmp % 10);
                sum += tmp;
            }
            oddNumber = !oddNumber;
        }
        return (sum % 10) == 0;
    }

    private synchronized void handleLogoutRequest() throws EndConnectionException {
        System.out.println("Logout request");
        throw new EndConnectionException(null);
    }

    public static PublicKey RecupereClePublicClient() throws IOException, ClassNotFoundException {
        // Désérialisation de la clé privée du serveur
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("../cleServeur/clePubliqueClients.ser"));
        PublicKey cle = (PublicKey) ois.readObject(); ois.close();
        return cle;
    }

    @Override
    public String getName() {
        return null;
    }
}
