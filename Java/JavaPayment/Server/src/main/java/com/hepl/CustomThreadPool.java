package com.hepl;

class CustomThreadPool {
    private ThreadClient[] threadClients;
    public boolean[] threadAvailability;
    int nbFreeThread;

    public CustomThreadPool(int nbThreads) throws Exception {
        threadClients = new ThreadClient[nbThreads];
        threadAvailability = new boolean[nbThreads];
        nbFreeThread = 0;
        for (int i = 0; i < nbThreads; i++){
            threadClients[i] = new ThreadClient(i, this);
            threadClients[i].start();
        }

    }

    public synchronized int getAvailableThread(){
        if (nbFreeThread == 0)// No Available thread
            return -1;
        for (int i = 0; i < threadAvailability.length; i++) {
            if (threadAvailability[i]){
                threadAvailability[i] = false;
                nbFreeThread--;
                return i;
            }
        }
        return -1;// Not supposed to get to this statement, but security statement
    }

    public synchronized void setThreadAvailable(int index){
        threadAvailability[index] = true;
        nbFreeThread++;
    }

    public synchronized void setThreadUnavailable(int index){
        threadAvailability[index] = false;
        nbFreeThread--;
    }

}
