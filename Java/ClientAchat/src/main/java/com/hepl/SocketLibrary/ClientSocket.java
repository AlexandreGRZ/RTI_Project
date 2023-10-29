package com.hepl.SocketLibrary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientSocket {
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    public ClientSocket(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Erreur lors de la fermeture de la socket : " + e.getMessage());
        }
    }

    public void send(String data) throws IOException {
        data += "!#";
        dataOutputStream.writeUTF(new String(data.getBytes("ISO-8859-1"), "ISO-8859-1"));
    }

    public String receive() throws IOException {
        String response = "";

        while (true) {
            response += (char)dataInputStream.readByte();
            if (response.length() >= 2 && response.endsWith("!#")) {
                response = response.substring(0, response.length() - 2);
                return response;
            }
        }
    }
}