package com.hepl;

import com.hepl.bridge.DbConnection;
import com.hepl.protocol.EndConnectionException;
import com.hepl.protocol.interfaces.Protocol;
import com.hepl.protocol.interfaces.Request;
import com.hepl.protocol.interfaces.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

class ThreadClient extends Thread {
    private WaitingQ queue;
    private DbConnection connection;
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    Protocol protocol;

    ThreadClient(WaitingQ queue, Protocol protocol) throws IOException, SQLException {
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

                // Interaction with the client
                while (true){
                    System.out.println("Waiting for request...");
                    Request request = (Request) ois.readObject();
                    System.out.println("Request received.");
                    Response response = protocol.handleRequest(request, connection);
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
            }
            finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("[" + getName() + "IOException while trying to close socket : " + e.getMessage());
                }
            }
        }
    }
}