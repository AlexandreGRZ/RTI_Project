#include "AccesBD.h"
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <sys/shm.h>
#include <signal.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <signal.h>
#include <mysql.h>

MYSQL *ConnexionBD()
{
    MYSQL *MysqlBase = mysql_init(NULL);

    if (mysql_real_connect(MysqlBase, "localhost", "Student", "Papyrusse0021", "PourStudent", 0, 0, 0) == NULL)
    {
        fprintf(stderr, "(ACCESBD) Erreur de connexion à la base de données...\n");
        exit(1);
    }

    return MysqlBase;
}

bool UserConnexion(MYSQL *ConnexionBD,int * idUtilisateur, const char *Login, const char *Password, bool newUser)
{
    char query[255];
    MYSQL_RES *res;
    MYSQL_ROW row;

    snprintf(query, sizeof(query), "SELECT * FROM clients WHERE pseudo = '%s'", Login);

    if (mysql_query(ConnexionBD, query))
    {
        printf("Erreur lors de l'execution de la requete : %s\n", mysql_error(ConnexionBD));
        return false;
    }

    // recuperer les données de la requete
    res = mysql_store_result(ConnexionBD);

    if (res)
    {

        if ((row = mysql_fetch_row(res))) // Client trouvé dans la BD
        {
            *idUtilisateur = atoi(row[0]);
            printf("Pseudo : %s, Password : %s\n", row[1], row[2]);
            if (strcmp(row[2], Password) == 0)
            {
                printf("Le mots de passe correspond \n");
                mysql_free_result(res);
                return true;
            }
            else
            {
                printf("le mots de passe n'est pas le bon");
                mysql_free_result(res);
                return false;
            }
        }
        else if (newUser) // Login inexistant dans BD, donc insertion nouveau client
        {
            printf("Nouvel utilisateur possible, pseudo disponible\n");
            MYSQL_STMT *stmt = mysql_stmt_init(ConnexionBD); // Initialisation d'une variable pour requête préparée
            MYSQL_BIND bind[2];
            if (stmt == NULL)
            {
                fprintf(stderr, "mysql_stmt_init() failed\n");
                return false;
            }

            // Création de la requête préparée, insertion du pseudo et mdp dans la table clients
                strcpy(query, "INSERT INTO clients (NULL, pseudo, password) VALUES (?, ?)");
            if (mysql_stmt_prepare(stmt, query, strlen(query)) != 0)
            {
                fprintf(stderr, "mysql_stmt_prepare() failed\n");
                mysql_stmt_close(stmt);
                return false;
            }

            // Association des variables à la requête préparée
            memset(bind, 0, sizeof(bind));

            bind[0].buffer_type = MYSQL_TYPE_STRING;
            bind[0].buffer = (char *)Login; // Conversion const char* -> char*, buffer accepte uniquement char*
            bind[0].buffer_length = strlen(Login);

            bind[1].buffer_type = MYSQL_TYPE_STRING;
            bind[1].buffer = (char *)Password;
            bind[1].buffer_length = strlen(Password);

            // Binding des paramètres
            if (mysql_stmt_bind_param(stmt, bind) != 0)
            {
                fprintf(stderr, "mysql_stmt_bind_param() failed\n");
                mysql_stmt_close(stmt);
                return false;
            }

            // Exécution de la requête préparée avec les paramètres bindés
            if (mysql_stmt_execute(stmt) != 0)
            {
                fprintf(stderr, "mysql_stmt_execute() failed\n");
                mysql_stmt_close(stmt);
                return false;
            }

            mysql_stmt_close(stmt);
            return true;
        }
        else // Client inexistant dans la BD, login refusé
        {
            printf("Aucun enregistrement trouvé pour le pseudo %s\n", Login);
            mysql_free_result(res);
            return false;
        }
    }
    return false;
}

bool UserConsult(MYSQL *ConnexionBD, int idAliment, char *pCreponse)
{

    char query[255];
    MYSQL_RES *res;
    MYSQL_ROW row;

    snprintf(query, sizeof(query), "SELECT * FROM articles WHERE id = '%d'", idAliment);

    if (mysql_query(ConnexionBD, query))
    {
        printf("Erreur lors de l'execution de la requete : %s\n", mysql_error(ConnexionBD));
        return false;
    }

    res = mysql_store_result(ConnexionBD);

    if (res)
    {

        if ((row = mysql_fetch_row(res)))
        {
            printf("id : %d, intitule : %s, Prix : %f, Stock : %d\n", atoi(row[0]), row[1], atof(row[2]), atoi(row[3]));

            sprintf(pCreponse, "%d#%s#%f#%d#%s", atoi(row[0]), row[1], atof(row[2]), atoi(row[3]), row[4]);
            mysql_free_result(res);
            return true;
        }
        else
        {
            printf("Aucun enregistrement trouvé pour l'id de l'aliment %d\n", idAliment);
            mysql_free_result(res);
            return false;
        }
    }
    return false;
}

bool UserAchat(MYSQL *ConnexionBD, int idAliment, int IQuantite, char *pCreponse)
{

    char query[255];
    MYSQL_RES *res;
    MYSQL_ROW row;

    snprintf(query, sizeof(query), "SELECT * FROM articles WHERE id = '%d'", idAliment);

    if (mysql_query(ConnexionBD, query))
    {
        printf("Erreur lors de l'execution de la requete : %s\n", mysql_error(ConnexionBD));
        return false;
    }

    res = mysql_store_result(ConnexionBD);

    if (res)
    {

        if ((row = mysql_fetch_row(res)))
        {
            printf("Stock : %d\n", atoi(row[3]));
            if (atoi(row[3]) >= IQuantite)
            {
                int INewStock = atoi(row[3]) - IQuantite;
                snprintf(query, sizeof(query), "UPDATE articles SET stock = %d WHERE id = %d", INewStock, idAliment);
                sprintf(pCreponse, "ACHAT#%i#%s#%f#%d", atoi(row[0]), row[1], atof(row[2]), IQuantite);
                if (mysql_query(ConnexionBD, query))
                {
                    printf("Erreur lors de l'execution de la requete : %s\n", mysql_error(ConnexionBD));
                    return false;
                }

                mysql_free_result(res);
                return true;
            }
            else
            {

                printf("Pas assez de stock \n");
                sprintf(pCreponse, "ACHAT#%i#%s#%f#%d", atoi(row[0]), row[1], atof(row[2]), 0);
                mysql_free_result(res);
                return false;
            }

            mysql_free_result(res);
            return false;
        }
        else
        {
            printf("Aucun enregistrement trouvé pour l'id de l'aliment %d\n", idAliment);
            sprintf(pCreponse, "ACHAT#%i", -1);
            mysql_free_result(res);
            return false;
        }
    }
    return false;
}

bool UserCancel(MYSQL *ConnexionBD, int idAliment, int IQuantite)
{
    char query[255];
    MYSQL_RES *res;
    MYSQL_ROW row;

    snprintf(query, sizeof(query), "SELECT stock FROM articles WHERE id = '%d'", idAliment);

    if (mysql_query(ConnexionBD, query))
    {
        printf("Erreur lors de l'execution de la requete : %s\n", mysql_error(ConnexionBD));
        return false;
    }

    res = mysql_store_result(ConnexionBD);

    if (res)
    {

        if ((row = mysql_fetch_row(res)))
        {
            printf("Stock : %d\n", atoi(row[0]));
            printf("IQantite : %d\n", IQuantite);
            int INewStock = atoi(row[0]);
            INewStock += IQuantite;
            printf("Stock : %d\n", INewStock);

            snprintf(query, sizeof(query), "UPDATE articles SET stock = %d WHERE id = %d", INewStock, idAliment);

            if (mysql_query(ConnexionBD, query))
            {
                printf("Erreur lors de l'execution de la requete : %s\n", mysql_error(ConnexionBD));
                return false;
            }

            mysql_free_result(res);
            return true;
        }
    }
    return false;
}

bool UserConfirm(MYSQL *ConnexionBD, int idUtilisateur, float MontantAPayer){


       char query[255];
       MYSQL_RES *res;
       MYSQL_ROW row;

       snprintf(query, sizeof(query), "INSERT INTO factures (idClient, `DATE`, montant, paye) VALUES (%d, NOW(), %f, false)", idUtilisateur, MontantAPayer);

       if (mysql_query(ConnexionBD, query))
       {
           printf("Erreur lors de l'execution de la requete : %s\n", mysql_error(ConnexionBD));
           return false;
       }
       else{
           return true;
       }

}