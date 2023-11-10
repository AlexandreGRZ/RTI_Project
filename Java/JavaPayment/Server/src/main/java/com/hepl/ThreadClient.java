package com.hepl;

import com.hepl.bridge.DbConnection;

import java.net.Socket;

class ThreadClient extends Thread {
    private int clientNumber;
    private int threadIndex;
    private Socket clientSocket;
    private CustomThreadPool threadPool;
    DbConnection connection;

    public ThreadClient(int threadIndex, CustomThreadPool threadPool)throws Exception{
        connection = new DbConnection();
        this.threadIndex = threadIndex;
        this.threadPool = threadPool;
    }

    public synchronized void setClientSocket(Socket clientSocket){
        this.clientSocket = clientSocket;
    }
    @Override
    public void run() {
        while (true){
            threadPool.setThreadAvailable(threadIndex);
            try{
                wait();
            }catch (InterruptedException e){
                return;
            }
            threadPool.setThreadUnavailable(threadIndex);



        }
    }
}