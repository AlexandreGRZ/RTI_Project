#include "SMOP.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <mysql.h>
#include "AccesBD.h"


int clients[NB_MAX_CLIENTS];
int nbClients = 0;

int estPresent(int socket);

void ajoute(int socket);
void retire(int socket);


pthread_mutex_t mutexClients = PTHREAD_MUTEX_INITIALIZER;


bool SMOP(MYSQL* MysqlBase, char* requete, char* reponse,int socket, bool * CheckLogin)
{
    char *ptr = strtok(requete,"#");

    if (strcmp(ptr,"LOGIN") == 0) 
    {   
        printf("%d\n\n\n", *CheckLogin);
        if(!*CheckLogin){
            char *Login = strtok(NULL,"#");
            char *Password = strtok(NULL,"#");

            if(SMOP_Login(MysqlBase, Login, Password))
            {
                sprintf(reponse, "LOGIN#%s#%s#OK", Login, Password);
                *CheckLogin = true;
                printf("%d\n\n\n", *CheckLogin);
                return true;
            }
            else
            {
                sprintf(reponse, "LOGIN#ERR");
                return true;
            }
        }
        else{
            sprintf(reponse, "LOGIN#ALREADY");
            return true;
        }
        

    }

    if (strcmp(ptr,"CONSULT") == 0) 
    {
        char *CidAliment = strtok(NULL,"#");
        char CReponse[150];
        
        
        if(SMOP_Consult(MysqlBase, atoi(CidAliment), &CReponse[0])){
            strcpy(reponse, "CONSULT#OK#");
            strcat(reponse, CReponse);
            return true;
        }
        else
        {
            strcpy(reponse, "CONSULT#ERR");
            return false;
        }
    }

    if (strcmp(ptr,"ACHAT") == 0) 
    {   
        char *CidAliment = strtok(NULL,"#");
        char *CQuantite = strtok(NULL,"#");
        
        if(SMOP_Achat(MysqlBase, atoi(CidAliment), atoi(CQuantite), reponse)){
            return true;
        }
        else
        {
            return false;
        }
        return false;
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
        *CheckLogin = false;
        sprintf(reponse,"LOGOUT#ok");
        return false;

    }

    return true;
}


bool SMOP_Login(MYSQL* MysqlBase, const char* user,const char* password){
    
    return UserConnexion(MysqlBase, user, password);
}

bool SMOP_Consult(MYSQL * MysqlBase, int idAliment, char * pCReponse)
{
    return UserConsult(MysqlBase, idAliment, pCReponse);
}

bool SMOP_Achat(MYSQL * MysqlBase, int IdAliment, int IQuantite, char * pCreponse)
{
    return UserAchat(MysqlBase, IdAliment, IQuantite, pCreponse);
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
