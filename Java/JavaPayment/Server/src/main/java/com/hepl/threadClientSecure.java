package com.hepl;

import com.hepl.bridge.DbConnection;
import com.hepl.protocol.EndConnectionException;
import com.hepl.protocol.interfaces.Protocol;
import com.hepl.protocol.interfaces.ProtocolSecure;
import com.hepl.protocol.interfaces.Request;
import com.hepl.protocol.interfaces.Response;
import com.hepl.protocol.responses.requestSecureResponse;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.sql.SQLException;

public class threadClientSecure extends Thread{
    private WaitingQ queue;
    private DbConnection connection;
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    ProtocolSecure protocol;

    SecretKey ClientKey;

    threadClientSecure(WaitingQ queue, ProtocolSecure protocol) throws IOException, SQLException {
        this.queue = queue;
        this.protocol = protocol;
    }

    @Override
    public void run() {
        System.out.println("[" + getName() + "]Thread starts...");

        System.out.println("[" + getName() + "]Connection to db...");
        try{

            connection = new DbConnection();
        }catch (SQLException | IOException e){
            System.out.println("[" + getName() + "]DB connection failed. Thread interruption.");
            return;
        }
        while (true) {
            // Waiting for new client
            try {
                System.out.println("[" + getName() + "]Waiting for a new client connection...");
                socket = queue.getConnection();
            } catch (InterruptedException e) {
                System.out.println("[" + getName() + "]Thread interruption.");
                return;
            }
            System.out.println("[" + getName() + "]Handling new client.");

            try {
                System.out.println("input");
                ois = new ObjectInputStream(socket.getInputStream());
                System.out.println("output");
                oos = new ObjectOutputStream(socket.getOutputStream());
                PrivateKey clePriveeServeur = RecupereClePriveeServeur();

                Request request = (Request) ois.readObject();
                System.out.println("Request received.");
                Response response = protocol.handleRequest(request, connection, clePriveeServeur, ClientKey);
                requestSecureResponse response2 = (requestSecureResponse) response;
                ClientKey = response2.getCleSession();
                // Interaction with the client
                while (true){
                    System.out.println("Waiting for request...");
                    request = (Request) ois.readObject();
                    System.out.println("Request received.");
                    response = protocol.handleRequest(request, connection, clePriveeServeur, ClientKey);
                    System.out.println("Sending response...");
                    oos.writeObject(response);
                }
            }
            catch (EndConnectionException e){
                System.out.println("[" + getName() + "]End of connection with client.");
            }
            catch (IOException e) {
                System.out.println("[" + getName() + "]IOException : " + e.getMessage());
            }
            catch (ClassNotFoundException e){
                System.out.println("[" + getName() + "]Invalid request!");
            } catch (NoSuchPaddingException | InvalidKeyException | NoSuchProviderException | BadPaddingException |
                     NoSuchAlgorithmException | IllegalBlockSizeException | SQLException e) {
                throw new RuntimeException(e);
            } catch (SignatureException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("[" + getName() + "IOException while trying to close socket : " + e.getMessage());
                }
            }
        }
    }


    public static PrivateKey RecupereClePriveeServeur() throws IOException, ClassNotFoundException {
        // Désérialisation de la clé privée du serveur
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("../cleServeur/clePriveeServeur.ser"));
        PrivateKey cle = (PrivateKey) ois.readObject(); ois.close();
        return cle;
    }
}
