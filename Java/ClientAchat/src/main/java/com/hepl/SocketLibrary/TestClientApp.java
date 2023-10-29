package com.hepl.SocketLibrary;

public class TestClientApp {
    private static ClientSocket clientSocket;
    public static void main(String[] args) throws Exception{
        clientSocket = new ClientSocket("192.168.1.218", 50000);

        request("LOGIN#Alex#abc123#0");

        request("LOGOUT");

        clientSocket.close();
    }

    public static void request(String request)throws Exception{
        String response;
        System.out.println("Message envoye");
        clientSocket.send(request);
        System.out.println("En attente de reponse...");
        response = clientSocket.receive();
        System.out.println("Message reçu : "+response);
    }
}
