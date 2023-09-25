#include "SMOP.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>


int clients[NB_MAX_CLIENTS];
int nbClients = 0;

int estPresent(int socket);

void ajoute(int socket);
void retire(int socket);


pthread_mutex_t mutexClients = PTHREAD_MUTEX_INITIALIZER;


bool SMOP(char* requete, char* reponse,int socket)
{
    char *ptr = strtok(requete,"#");

    if (strcmp(ptr,"LOGIN") == 0) 
    {   
        
        char *Login = strtok(NULL,"#");
        char *Password = strtok(NULL,"#");

        if(SMOP_Login(Login, Password))
        {
            sprintf(reponse, "LOGIN#%s#%s", Login, Password);
        }
        else
        {
            sprintf(reponse, "LOGIN#ERR");
        }

    }

    if (strcmp(ptr,"CONSULT") == 0) 
    {
        sprintf(reponse, "CONSULT#OK");
    }

    if (strcmp(ptr,"ACHAT") == 0) 
    {
        sprintf(reponse, "ACHAT#OK");
    }

    if (strcmp(ptr,"CADDIE") == 0) 
    {
        sprintf(reponse, "CADDIE#OK");
    }

    if (strcmp(ptr,"CANCEL") == 0) 
    {
       sprintf(reponse, "CANCEL#OK");
    }

    if (strcmp(ptr,"CANCELALL") == 0) 
    {
        sprintf(reponse, "CANCELALL#OK");
    }

    if (strcmp(ptr,"CONFIRMER") == 0) 
    {
        sprintf(reponse, "CONFIRMER#OK");
    }

    if (strcmp(ptr,"LOGOUT") == 0)
    {
        printf("\t[THREAD %p] LOGOUT\n",pthread_self());
        retire(socket);
        sprintf(reponse,"LOGOUT#ok");
        return false;

    }

    return true;
}


bool SMOP_Login(const char* user,const char* password){
    if (strcmp(user,"wagner")==0 && strcmp(password,"abc123")==0) return true;
    if (strcmp(user,"charlet")==0 && strcmp(password,"xyz456")==0) return true;
 return false;
}

int estPresent(int socket)
{
    int indice = -1;

    pthread_mutex_lock(&mutexClients);
    for(int i=0 ; i<nbClients ; i++)
    {
        if (clients[i] == socket) 
        { 
            indice = i; break; 
        }
    }  

    pthread_mutex_unlock(&mutexClients);
    return indice;
}
void ajoute(int socket)
{
    pthread_mutex_lock(&mutexClients);

    clients[nbClients] = socket;
    nbClients++;

    pthread_mutex_unlock(&mutexClients);
}
void retire(int socket)
{
    int pos = estPresent(socket);

    if (pos == -1) 
        return;
    
    pthread_mutex_lock(&mutexClients);
    
    for (int i=pos ; i<=nbClients-2 ; i++)
        clients[i] = clients[i+1];

    nbClients--;
    
    pthread_mutex_unlock(&mutexClients);    
}


void SMOP_Close()
{
    pthread_mutex_lock(&mutexClients);

    for (int i=0 ; i<nbClients ; i++)
        close(clients[i]);

    pthread_mutex_unlock(&mutexClients);
}
