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

    strcpy(&Cmessage[0],"LOGIN#wagner#abc123");
    
    printf("Message Envoyer ! \n");

    Send(IClientSocket,&Cmessage[0], strlen(&Cmessage[0]));

    printf("En attente de reponse ... \n");

    Receive(IClientSocket, &Cmessage[0]);

    printf("Message recu ! \n");

    sleep(1);

    strcpy(&Cmessage[0],"ACHAT");
    Send(IClientSocket,&Cmessage[0], strlen(&Cmessage[0]));
    Receive(IClientSocket, &Cmessage[0]);

    sleep(1);
    
    strcpy(&Cmessage[0],"CADDIE");
    Send(IClientSocket,&Cmessage[0], strlen(&Cmessage[0]));
    Receive(IClientSocket, &Cmessage[0]);

    sleep(1);
    
    strcpy(&Cmessage[0],"CONSULT");
    Send(IClientSocket,&Cmessage[0], strlen(&Cmessage[0]));
    Receive(IClientSocket, &Cmessage[0]);

    sleep(1);
    
    strcpy(&Cmessage[0],"CANCEL");
    Send(IClientSocket,&Cmessage[0], strlen(&Cmessage[0]));
    Receive(IClientSocket, &Cmessage[0]);

    sleep(1);
    
    strcpy(&Cmessage[0],"CANCELALL");
    Send(IClientSocket,&Cmessage[0], strlen(&Cmessage[0]));
    Receive(IClientSocket, &Cmessage[0]);

    sleep(1);
    
    strcpy(&Cmessage[0],"CONFIRMER");
    Send(IClientSocket,&Cmessage[0], strlen(&Cmessage[0]));
    Receive(IClientSocket, &Cmessage[0]);
    

    strcpy(&Cmessage[0],"LOGOUT");

    printf("Message Envoyer ! \n");

    Send(IClientSocket,&Cmessage[0], strlen(&Cmessage[0]));

    printf("En attente de reponse ... \n");

    Receive(IClientSocket, &Cmessage[0]);

    printf("Message recu ! \n");

    pause();


}

void handlerSIGINT(int sig)
{
  
  close(IClientSocket);

  exit(1);
}

#endif