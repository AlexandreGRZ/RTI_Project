package com.hepl;

import com.hepl.protocol.interfaces.Protocol;
import com.hepl.protocol.interfaces.ProtocolSecure;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.SQLException;

public class threadServerSecu extends Thread {
    private ThreadGroup poolGroup;
    private int port;
    private ServerSocket serverSocket;
    private ProtocolSecure protocol;

    protected threadServerSecu(int port, ProtocolSecure protocol) throws IOException {
        this.port = port;
        this.protocol = protocol;
        poolGroup = new ThreadGroup("POOL");
        serverSocket = new ServerSocket(port);
    }

    @Override
    public synchronized void run() {

        // Awaits for connection
        while (!this.isInterrupted()) {
            Socket cSocket;
            try {
                serverSocket.setSoTimeout(2000);
                cSocket = serverSocket.accept();
                System.out.println("[THREAD(server)]Connection accepted, added to the queue.");
                Thread th = new threadClientSecure(protocol, cSocket);
                th.start();
            } catch (SocketTimeoutException e) {
            } catch (IOException e) {
                System.out.println("[THREAD(server)]IOException : " + e.getMessage());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        // Close Server
        System.out.println("[THREAD(server)]Interrupt request received : pool interruption...");
        poolGroup.interrupt();
    }

}
