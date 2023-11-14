#ifndef SMOP_H
#define SMOP_H

#define NB_MAX_CLIENTS 100

#include <mysql.h>

typedef struct
{
  int id;
  char intitule[20];
  float prix;
  int quantite;

} ARTICLEINPANNIER;

bool SMOP(MYSQL *MysqlBase, char *requete, char *reponse, int socket, int * idUtilisateur, ARTICLEINPANNIER *pCaddie);
bool SMOP_Login(MYSQL *MysqlBase,int * idUtilisateur, const char *user, const char *password, bool newUser);
bool SMOP_Consult(MYSQL *MysqlBase, int IdAliment, char *pCReponse);
bool SMOP_Achat(MYSQL *MysqlBase, int IdAliment, int IQuantite, char *reponse);
bool SMOP_Confirm(MYSQL *MysqlBase, int * idUtilisateur, float MontantAPayer);
bool cancelAll(MYSQL *MysqlBase, ARTICLEINPANNIER *pCaddie);

void SMOP_Close();

#endif