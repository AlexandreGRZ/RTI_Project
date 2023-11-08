package com.hepl.purchaseclient.socketlibrary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientSocket {
    private final Socket socket;
    private final DataOutputStream dataOutputStream;
    private final DataInputStream dataInputStream;

    public ClientSocket(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error while trying to close the socket : " + e.getMessage());
        }
    }

    public void send(String data) throws IOException {
        data += "!#";
        dataOutputStream.writeUTF(new String(data.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1));
    }

    public String receive() throws IOException {
        StringBuilder response = new StringBuilder();

        while (true) {
            response.append((char) dataInputStream.readByte());
            int length = response.length();
            if (length >= 2 && response.toString().endsWith("!#")) {
                response = new StringBuilder(response.substring(0, length - 2));
                return response.toString();
            }
        }
    }
}