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

bool SMOP(MYSQL *MysqlBase, char *requete, char *reponse, int socket, int *idUtilisateur, ARTICLEINPANNIER *pCaddie)
{
    // Skip char taille trame quand envoyé depuis client java
    if (strlen(requete) == (int)requete[0] - 1)
        requete = requete + 1;

    char *ptr = strtok(requete, "#");

    if (strcmp(ptr, "LOGIN") == 0)
    {
        if (estPresent(socket) >= 0)
        {
            sprintf(reponse, "LOGIN#ALREADY");
            return true;
        }
        else
        {
            char *Login = strtok(NULL, "#");
            char *Password = strtok(NULL, "#");
            bool newUser = atoi(strtok(NULL, "#"));
            if (SMOP_Login(MysqlBase, idUtilisateur,Login, Password, newUser))
            {
                sprintf(reponse, "LOGIN#%s#%s#OK", Login, Password);
                ajoute(socket);
                return true;
            }
            else
            {
                sprintf(reponse, "LOGIN#ERR");
                return true;
            }
        }
    }

    if (strcmp(ptr, "CONSULT") == 0)
    {
        char *CidAliment = strtok(NULL, "#");
        char CReponse[150];

        if (SMOP_Consult(MysqlBase, atoi(CidAliment), &CReponse[0]))
        {
            strcpy(reponse, "CONSULT#OK#");
            strcat(reponse, CReponse);
            return true;
        }
        else
        {
            strcpy(reponse, "CONSULT#ERR");
            return true;
        }
    }

    if (strcmp(ptr, "ACHAT") == 0)
    {
        char *CidAliment = strtok(NULL, "#");
        char *CQuantite = strtok(NULL, "#");
        char CTempon[200];

        if (SMOP_Achat(MysqlBase, atoi(CidAliment), atoi(CQuantite), reponse))
        {

            strcpy(CTempon, reponse);
            char *ptr = strtok(CTempon, "#");

            int id = atoi(strtok(NULL, "#"));
            char *pCIntitule = strtok(NULL, "#");
            float FPrix = atof(strtok(NULL, "#"));
            int IQuantite = atoi(strtok(NULL, "#"));

            bool check = false;

            while ((pCaddie->id != -1) && (check == false))
            {
                if (strcmp(pCaddie->intitule, pCIntitule) == 0)
                    check = true;
                pCaddie++;
            }
            if (check)
            {
                // pCaddie - 1 car dans le boucle précédente on a été un cran trop loin donc on fait un pas en arriere pour récupéré le bon élément
                (pCaddie - 1)->quantite += IQuantite;
            }
            else
            {
                pCaddie->id = id;

                strcpy(pCaddie->intitule, pCIntitule);

                pCaddie->prix = FPrix;
                pCaddie->quantite = IQuantite;
            }

            return true;
        }
        else
        {
            return true;
        }
        return false;
    }

    if (strcmp(ptr, "CADDIE") == 0)
    {
        strcpy(reponse, "CADDIE");
        char CTempon[200];
        while (pCaddie->id != -1)
        {
            strcat(reponse, "#");
            sprintf(CTempon, "%i#%s#%f#%d", pCaddie->id, pCaddie->intitule, pCaddie->prix, pCaddie->quantite);
            strcat(reponse, CTempon);
            pCaddie++;
        }
    }

    if (strcmp(ptr, "CANCEL") == 0)
    {
        int idAliment = atoi(strtok(NULL, "#"));

        ARTICLEINPANNIER *pCaddieTemp;

        while (pCaddie->id != -1)
        {
            if (pCaddie->id == idAliment)
                pCaddieTemp = pCaddie;
            pCaddie++;
        }

        if (pCaddieTemp == pCaddie)
        {
            printf("pas compris dans le caddie \n");
            sprintf(reponse, "CANCEL#ERR");
            return true;
        }
        else
        {
            if (UserCancel(MysqlBase, idAliment, pCaddieTemp->quantite))
            {
                // pour revenir au dernière éléments de la liste
                pCaddie--;

                // prendre le dernière éléments et le remetre a celui qui a été supprimer
                // pour garder l'intégrité du vecteur
                pCaddieTemp->id = pCaddie->id;
                strcpy(pCaddieTemp->intitule, pCaddie->intitule);
                pCaddieTemp->prix = pCaddie->prix;
                pCaddieTemp->quantite = pCaddie->quantite;

                // supprimer le dernier pour évité les doublon
                pCaddie->id = -1;

                sprintf(reponse, "CANCEL#OK");
                return true;
            }
            else
            {
                sprintf(reponse, "CANCEL#ERR");
                return true;
            }
        }
    }

    if (strcmp(ptr, "CANCELALL") == 0)
    {
        bool check = cancelAll(MysqlBase, pCaddie);

        if (!check)
        {
            sprintf(reponse, "CANCELALL#ERR");
            return true;
        }
        else
        {
            sprintf(reponse, "CANCELALL#OK");
            return true;
        }
    }

    if (strcmp(ptr, "CONFIRM") == 0)
    {
        float MontantAPayer = 0.0;
        while (pCaddie->id != -1)
        {
            printf("interne Boucle\n");
            MontantAPayer += pCaddie->prix;
            pCaddie++;
        }

        if(SMOP_Confirm(MysqlBase, idUtilisateur, MontantAPayer))
        {
            sprintf(reponse, "CONFIRM#OK");
        }
        else
        {
            sprintf(reponse, "CONFIRM#ERR");
        }
    }

    if (strcmp(ptr, "LOGOUT") == 0)
    {
        printf("\t[THREAD %p] LOGOUT\n", pthread_self());
        cancelAll(MysqlBase, pCaddie);
        retire(socket);
        sprintf(reponse, "LOGOUT#OK");
        return true;
    }

    return true;
}

bool SMOP_Login(MYSQL *MysqlBase,int * idUtilisateur, const char *user, const char *password, bool newUser)
{
    return UserConnexion(MysqlBase, idUtilisateur, user, password, newUser);
}

bool SMOP_Consult(MYSQL *MysqlBase, int idAliment, char *pCReponse)
{
    return UserConsult(MysqlBase, idAliment, pCReponse);
}

bool SMOP_Achat(MYSQL *MysqlBase, int IdAliment, int IQuantite, char *pCreponse)
{
    return UserAchat(MysqlBase, IdAliment, IQuantite, pCreponse);
}

bool cancelAll(MYSQL *MysqlBase, ARTICLEINPANNIER *pCaddie)
{
    bool check = true;
    while (pCaddie->id != -1 && check == true)
    {
        check = UserCancel(MysqlBase, pCaddie->id, pCaddie->quantite);
        pCaddie->id = -1;
        pCaddie++;
    }
    return check;
}

bool SMOP_Confirm(MYSQL *MysqlBase, int * idUtilisateur, float MontantAPayer){
    return UserConfirm(MysqlBase, *idUtilisateur, MontantAPayer);
}
int estPresent(int socket)
{
    int indice = -1;

    pthread_mutex_lock(&mutexClients);
    for (int i = 0; i < nbClients; i++)
    {
        if (clients[i] == socket)
        {
            indice = i;
            break;
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

    for (int i = pos; i <= nbClients - 2; i++)
        clients[i] = clients[i + 1];

    nbClients--;

    pthread_mutex_unlock(&mutexClients);
}

void SMOP_Close()
{
    pthread_mutex_lock(&mutexClients);

    for (int i = 0; i < nbClients; i++)
        close(clients[i]);

    pthread_mutex_unlock(&mutexClients);
}
