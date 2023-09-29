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


MYSQL* ConnexionBD()
{
    MYSQL* MysqlBase = mysql_init(NULL);

    if (mysql_real_connect(MysqlBase,"localhost","Student","PassStudent1_","PourStudent",0,0,0) == NULL)
    {
        fprintf(stderr,"(ACCESBD) Erreur de connexion à la base de données...\n");
        exit(1);  
    }

    return MysqlBase;
}

bool UserConnexion(MYSQL * ConnexionBD, const char* Login, const char* Password)
{
    char query[255];
    MYSQL_RES *res;
    MYSQL_ROW row;
    
    snprintf(query, sizeof(query), "SELECT * FROM clients WHERE pseudo = '%s'", Login);

    if (mysql_query(ConnexionBD, query)) {
        printf("Erreur lors de l'execution de la requete : %s\n", mysql_error(ConnexionBD));
        return false;
    }

    //recuperer les données de la requete
    res = mysql_store_result(ConnexionBD);

    if (res) {
        
        if ((row = mysql_fetch_row(res))) {
            printf("Pseudo : %s, Password : %s\n", row[0], row[1]);
            if(strcmp(row[1], Password) == 0){
                printf("Le mots de passe correspond \n");
                mysql_free_result(res);
                return true;
            } else {
                printf("le mots de passe n'est pas le bon");
                mysql_free_result(res);
                return false;
            }

        } else {
            printf("Aucun enregistrement trouvé pour le pseudo %s\n", Login);
            mysql_free_result(res);
            return false;
        }
        
    }
    return false;
}

bool    UserConsult(MYSQL * ConnexionBD, int idAliment, char * pCreponse)
{

    char query[255];
    MYSQL_RES *res;
    MYSQL_ROW row;
    
    snprintf(query, sizeof(query), "SELECT * FROM articles WHERE id = '%d'", idAliment);
 
    if (mysql_query(ConnexionBD, query)) {
        printf("Erreur lors de l'execution de la requete : %s\n", mysql_error(ConnexionBD));
        return false;
    }

    res = mysql_store_result(ConnexionBD);

    if (res) {
        
        if ((row = mysql_fetch_row(res))) {
            printf("id : %d, intitule : %s, Prix : %f, Stock : %d\n", atoi(row[0]), row[1], atof(row[2]), atoi(row[3]));

            sprintf(pCreponse,"%d#%s#%f#%d#%s", atoi(row[0]), row[1], atof(row[2]), atoi(row[3]), row[4]);
            mysql_free_result(res);
            return true;
            
        } else {
            printf("Aucun enregistrement trouvé pour l'id de l'aliment %d\n", idAliment);
            mysql_free_result(res);
            return false;
        }
    }
    return false;

}

bool    UserAchat(MYSQL *   ConnexionBD, int idAliment, int IQuantite, char * pCreponse)
{

    char query[255];
    MYSQL_RES *res;
    MYSQL_ROW row;
    
    snprintf(query, sizeof(query), "SELECT * FROM articles WHERE id = '%d'", idAliment);
 
    if (mysql_query(ConnexionBD, query)) {
        printf("Erreur lors de l'execution de la requete : %s\n", mysql_error(ConnexionBD));
        return false;
    }

    res = mysql_store_result(ConnexionBD);

    if (res) {
        
        if ((row = mysql_fetch_row(res))) {
            printf("Stock : %d\n", atoi(row[3]));
            if(atoi(row[3]) >= IQuantite) {
                int INewStock = atoi(row[3]) - IQuantite;
                snprintf(query, sizeof(query), "UPDATE articles SET stock = %d WHERE id = %d",INewStock ,idAliment);
                sprintf(pCreponse, "ACHAT#%i#%s#%f#%d", atoi(row[0]), row[1], atof(row[2]), IQuantite);
                if (mysql_query(ConnexionBD, query)) {
                    printf("Erreur lors de l'execution de la requete : %s\n", mysql_error(ConnexionBD));
                    return false;
                }
                
                mysql_free_result(res);
                return true;
            }   
            else {

                printf("Pas assez de stock \n");
                sprintf(pCreponse, "ACHAT#%i#%s#%f#%d", atoi(row[0]), row[1], atof(row[2]),0);
                mysql_free_result(res);
                return false;
            }
            
            mysql_free_result(res);
            return false;
            
        } else {
            printf("Aucun enregistrement trouvé pour l'id de l'aliment %d\n", idAliment);
            sprintf(pCreponse, "ACHAT#%i", -1);
            mysql_free_result(res);
            return false;
        }
    }



    return false;
}