package com.hepl;

import java.net.Socket;
import java.util.LinkedList;

class WaitingQ {
    private LinkedList<Socket> queue;

    WaitingQ() {
        queue = new LinkedList<>();
    }

    synchronized void addConnection(Socket socket) {
        queue.addLast(socket);
        // Notify any threads, currently using getConnection
        // and waiting, that a new connection is available
        notify();
    }

    synchronized Socket getConnection() throws InterruptedException {
        // Wait for a new connection is queue is empty
        while (queue.isEmpty()) wait();

        // Return a connection and remove it from the queue
        return queue.remove();
    }
}
