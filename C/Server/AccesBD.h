#ifndef ACCESBD_H
#define ACCESBD_H

#include <mysql.h>

MYSQL *ConnexionBD();
bool UserConnexion(MYSQL *ConnexionBD, const char *Login, const char *Password, bool newUser);
bool UserConsult(MYSQL *ConnexionBD, int idAliment, char *pCreponse);
bool UserAchat(MYSQL *ConnexionBD, int idAliment, int IQuantite, char *pCreponse);
bool UserCancel(MYSQL *ConnexionBD, int idAliment, int IQuantite);

#endif