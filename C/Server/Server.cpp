#include "SocketLibrary.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <signal.h>
#include <pthread.h>
#include "AccesBD.h"
#include "SMOP.h"

void HandlerSIGINT(int s);
void TraitementConnexion(int sService);
void *FctThreadClient(void *p);

int sEcoute;
MYSQL *MysqlBase = ConnexionBD();

#define NB_THREADS_POOL 2
#define TAILLE_FILE_ATTENTE 20

int socketsAcceptees[TAILLE_FILE_ATTENTE];
int indiceEcriture = 0, indiceLecture = 0;
pthread_mutex_t mutexSocketsAcceptees;
pthread_mutex_t mutexBDAcces;
pthread_cond_t condSocketsAcceptees;

#define MAX_CLIENT 10

int main(int argc, char *argv[])
{
   pthread_mutex_init(&mutexSocketsAcceptees, NULL);
   pthread_mutex_init(&mutexBDAcces, NULL);
   pthread_cond_init(&condSocketsAcceptees, NULL);

   for (int i = 0; i < TAILLE_FILE_ATTENTE; i++)
   {
      socketsAcceptees[i] = -1;
   }

   // Armement des signaux
   struct sigaction A;
   A.sa_flags = 0;
   sigemptyset(&A.sa_mask);
   A.sa_handler = HandlerSIGINT;

   if (sigaction(SIGINT, &A, NULL) == -1)
   {
      perror("Erreur de sigaction");
      exit(1);
   }

   if ((sEcoute = ServerSocket(atoi(argv[1]))) == -1)
   {
      perror("Erreur de ServeurSocket");
      exit(1);
   }

   printf("Création du pool de threads.\n");

   pthread_t th;

   for (int i = 0; i < NB_THREADS_POOL; i++)
   {
      pthread_create(&th, NULL, FctThreadClient, NULL);
   }

   // Mise en boucle du serveur
   int sService;
   char ipClient[50];

   printf("Demarrage du serveur.\n");

   while (1)
   {
      printf("Attente d'une connexion...\n");
      if ((sService = Accept(sEcoute, ipClient)) == -1)
      {
         perror("Erreur de Accept");
         close(sEcoute);
         SMOP_Close();
         exit(1);
      }
      printf("Connexion acceptée : IP=%s socket=%d\n", ipClient, sService);

      pthread_mutex_lock(&mutexSocketsAcceptees);

      socketsAcceptees[indiceEcriture] = sService;
      indiceEcriture++;
      if (indiceEcriture == TAILLE_FILE_ATTENTE)
      {
         indiceEcriture = 0;
      }
      pthread_mutex_unlock(&mutexSocketsAcceptees);
      pthread_cond_signal(&condSocketsAcceptees);
   }
   return 0;
}

void *FctThreadClient(void *p)
{
   int sService;

   while (1)
   {
      printf("\t[THREAD %p] Attente socket...\n", pthread_self());

      pthread_mutex_lock(&mutexSocketsAcceptees);
      while (indiceEcriture == indiceLecture)
      {
         pthread_cond_wait(&condSocketsAcceptees, &mutexSocketsAcceptees);
      }

      sService = socketsAcceptees[indiceLecture];
      socketsAcceptees[indiceLecture] = -1;
      indiceLecture++;

      if (indiceLecture == TAILLE_FILE_ATTENTE)
      {
         indiceLecture = 0;
      }
      pthread_mutex_unlock(&mutexSocketsAcceptees);

      printf("\t[THREAD %p] Je m'occupe de la socket %d\n",
             pthread_self(), sService);
      TraitementConnexion(sService);
   }
}

void TraitementConnexion(int sService)
{
   char requete[1000], reponse[1000], CTempon[200], CaddieReponse[1000];
   int nbLus, nbEcrits, nbarticle, idUtilisateur;
   bool onContinue = true;
   bool CheckLogin = false;
   ARTICLEINPANNIER Caddie[10];

   for (int i = 0; i < 10; i++)
   {
      Caddie[i].id = -1;
   }

   while (onContinue)
   {
      printf("%d\n\n\n", CheckLogin);
      printf("\t[THREAD %p] Attente requete...\n", pthread_self());

      if ((nbLus = Receive(sService, requete)) < 0)
      {
         perror("Erreur de Receive");
         close(sService);
         HandlerSIGINT(0);
      }
      printf("nbLus:%d\n", nbLus);

      if (nbLus == 0)
      {
         printf("\t[THREAD %p] Fin de connexion du client.\n", pthread_self());
         cancelAll(MysqlBase, Caddie);
         close(sService);
         return;
      }

      requete[nbLus] = 0;

      printf("\t[THREAD %p] Requete recue = %s\n", pthread_self(), requete);

      pthread_mutex_lock(&mutexBDAcces);
      onContinue = SMOP(MysqlBase, requete, reponse, sService, &idUtilisateur, &Caddie[0]);
      pthread_mutex_unlock(&mutexBDAcces);
      printf("%d\n", idUtilisateur);

      if ((nbEcrits = Send(sService, reponse, strlen(reponse))) < 0)
      {
         perror("Erreur de Send");
         close(sService);
         HandlerSIGINT(0);
      }
      printf("\t[THREAD %p] Reponse envoyee = %s\n", pthread_self(), reponse);

      if (!onContinue)
         printf("\t[THREAD %p] Fin de connexion de la socket %d\n", pthread_self(), sService);
   }
}

void HandlerSIGINT(int s)
{
   printf("\nArret du serveur.\n");
   close(sEcoute);
   pthread_mutex_lock(&mutexSocketsAcceptees);

   for (int i = 0; i < TAILLE_FILE_ATTENTE; i++)
      if (socketsAcceptees[i] != -1)
         close(socketsAcceptees[i]);

   pthread_mutex_unlock(&mutexSocketsAcceptees);
   SMOP_Close();
   exit(0);
}
