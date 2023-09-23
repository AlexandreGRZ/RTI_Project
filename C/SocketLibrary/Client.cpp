#ifndef CLIENT_CPP
#define CLIENT_CPP


#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <signal.h>
#include "SocketLibrary.h"


void handlerSIGINT(int sig);
int IClientSocket;

int main(int argc,char* argv[])
{
    struct sigaction A;
    A.sa_handler = handlerSIGINT;
    A.sa_flags = 0;
    sigaction(SIGINT,&A,NULL);


    int num = atoi(argv[2]);
    IClientSocket = ClientSocket(argv[1],num);

    char Cmessage[100];

    strcpy(&Cmessage[0],"Bonjour Je suis Alexandre");
    
    printf("Message Envoyer ! \n");

    Send(IClientSocket,&Cmessage[0], strlen(&Cmessage[0]));

    printf("En attente de reponse ... \n");

    Receive(IClientSocket, &Cmessage[0]);

    printf("Message recu ! \n");
}

void handlerSIGINT(int sig)
{
  
  close(IClientSocket);

  exit(1);
}

#endif