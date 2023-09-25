#include <stdio.h>
#include <stdlib.h>
#include <mysql.h>
#include <time.h>
#include <string.h>

int main()
{
   // Connexion a MySql
   printf("Connection a la BD...\n");
   MYSQL *connexion = mysql_init(NULL);
   mysql_real_connect(connexion, "localhost", "Student", "PassStudent1_", "PourStudent", 0, 0, 0);

   // Creation d'une table UNIX_FINAL
   printf("Creation de la table articles...\n");
   mysql_query(connexion, "drop table clients;");
   mysql_query(connexion, "create table clients(pseudo varchar(64) primary key, password varchar(64));");

   mysql_query(connexion, "insert into clients values('Alex', 'abc123')");
   mysql_query(connexion, "insert into clients values('Cyril', 'abc123')");

   // Déconnexion de la BD
   mysql_close(connexion);

   return 0;
}