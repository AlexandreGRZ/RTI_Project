#ifndef TEST_CPP
#define TEST_CPP


#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <unistd.h>
#include <string.h>
#include "SocketLibrary.h"

void handlerSIGINT(int sig);
int IServerSocket, IServiceSocket;
int main()
{
    struct sigaction A;
    A.sa_handler = handlerSIGINT;
    A.sa_flags = 0;
    sigaction(SIGINT,&A,NULL);

    IServerSocket = ServerSocket(50000);
    char CipClient[20];
    IServiceSocket = Accept(IServerSocket, &CipClient[0]);

    char CMessage[200];

    sleep(5);

    printf("En attente de message ... \n");

    Receive(IServiceSocket, &CMessage[0]);

    printf("Message recu ! \n");

    Send(IServiceSocket,&CMessage[0], strlen(&CMessage[0]));

    printf("Message Envoyer ! \n");

    pause();
}


void handlerSIGINT(int sig)
{
  
  close(IServerSocket);
  close(IServiceSocket);

  exit(1);
}
#endif