package com.hepl;

import com.hepl.protocol.interfaces.Protocol;
import com.hepl.protocol.interfaces.ProtocolSecure;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.SQLException;

public class threadServerSecu extends Thread {
    private WaitingQ queue;
    private int poolSize;
    private ThreadGroup poolGroup;
    private int port;
    private ServerSocket serverSocket;
    private ProtocolSecure protocol;

    protected threadServerSecu(int port, ProtocolSecure protocol, int poolSize) throws IOException {
        this.port = port;
        this.protocol = protocol;
        this.poolSize = poolSize;
        poolGroup = new ThreadGroup("POOL");
        queue = new WaitingQ();
        serverSocket = new ServerSocket(port);
    }

    @Override
    public synchronized void run() {
        // Pool creation
        try {
            ThreadPoolCreation();
        } catch (Exception e) {
            return;
        }

        // Awaits for connection
        while (!this.isInterrupted()) {
            Socket cSocket;
            try {
                serverSocket.setSoTimeout(2000);
                cSocket = serverSocket.accept();
                System.out.println("[THREAD(server)]Connection accepted, added to the queue.");
                queue.addConnection(cSocket);
            } catch (SocketTimeoutException e) {
            } catch (IOException e) {
                System.out.println("[THREAD(server)]IOException : " + e.getMessage());
            }
        }
        // Close Server
        System.out.println("[THREAD(server)]Interrupt request received : pool interruption...");
        poolGroup.interrupt();
    }

    private void ThreadPoolCreation() throws Exception {
        System.out.println("[THREAD(server)]Creation of a thread pool of " + poolSize + " threads...");
        try {
            for (int i = 0; i < poolSize; i++)
                new threadClientSecure(queue, protocol).start();// Protocole needs to be added
        } catch (IOException e) {
            System.out.println("[THREAD(server)]Error in the pool creation");
            throw e;
        } catch (SQLException e) {
            System.out.println("[THREAD(server)]Error in the pool creation : Database connection failed");
            throw e;
        }
    }
}
