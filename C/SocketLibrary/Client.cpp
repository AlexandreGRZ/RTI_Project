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

int main(int argc, char *argv[])
{
  struct sigaction A;
  A.sa_handler = handlerSIGINT;
  A.sa_flags = 0;
  sigaction(SIGINT, &A, NULL);

  int num = atoi(argv[2]);
  IClientSocket = ClientSocket(argv[1], num);

  char Cmessage[1000];

  strcpy(&Cmessage[0], "LOGIN#Alex#abc123");

  printf("Message Envoyé ! \n");

  Send(IClientSocket, &Cmessage[0], strlen(&Cmessage[0]));

  printf("En attente de reponse ... \n");

  Receive(IClientSocket, &Cmessage[0]);

  printf("Message recu ! \n");

  sleep(2);

  strcpy(&Cmessage[0], "ACHAT#8#1");

  printf("Message Envoyé ! \n");

  Send(IClientSocket, &Cmessage[0], strlen(&Cmessage[0]));

  printf("En attente de reponse ... \n");

  Receive(IClientSocket, &Cmessage[0]);

  printf("Message recu ! \n");

  sleep(2);

  strcpy(&Cmessage[0], "ACHAT#10#2");

  printf("Message Envoyé ! \n");

  Send(IClientSocket, &Cmessage[0], strlen(&Cmessage[0]));

  printf("En attente de reponse ... \n");

  Receive(IClientSocket, &Cmessage[0]);

  printf("Message recu ! \n");

  sleep(2);

  strcpy(&Cmessage[0], "ACHAT#2#1");

  printf("Message Envoyé ! \n");

  Send(IClientSocket, &Cmessage[0], strlen(&Cmessage[0]));

  printf("En attente de reponse ... \n");

  Receive(IClientSocket, &Cmessage[0]);

  printf("Message recu ! \n");

  sleep(2);

  strcpy(&Cmessage[0], "CADDIE");

  printf("Message Envoyé ! \n");

  Send(IClientSocket, &Cmessage[0], strlen(&Cmessage[0]));

  printf("En attente de reponse ... \n");

  Receive(IClientSocket, &Cmessage[0]);

  printf("Message recu ! \n");

  sleep(2);

  strcpy(&Cmessage[0], "CANCELALL");

  printf("Message Envoyé ! \n");

  Send(IClientSocket, &Cmessage[0], strlen(&Cmessage[0]));

  printf("En attente de reponse ... \n");

  Receive(IClientSocket, &Cmessage[0]);

  printf("Message recu ! \n");

  sleep(2);

  strcpy(&Cmessage[0], "LOGOUT");

  printf("Message Envoyé ! \n");

  Send(IClientSocket, &Cmessage[0], strlen(&Cmessage[0]));

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