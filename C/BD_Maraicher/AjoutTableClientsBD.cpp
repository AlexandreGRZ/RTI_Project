#include <stdio.h>
#include <stdlib.h>
#include <mysql.h>
#include <time.h>
#include <string.h>

int main()
{
   // Connexion à MySQL
   printf("Connexion à la BD...\n");
   MYSQL *connexion = mysql_init(NULL);
   mysql_real_connect(connexion, "localhost", "Student", "Papyrusse0021", "PourStudent", 0, NULL, 0);

   // Vérification de la connexion
   if (connexion == NULL)
   {
      fprintf(stderr, "Échec de la connexion à la base de données : %s\n", mysql_error(connexion));
      return 1;
   }

   // Création de la table clients
   printf("Création de la table clients...\n");
   mysql_query(connexion, "DROP TABLE IF EXISTS clients;");
   mysql_query(connexion, "CREATE TABLE clients(id INT AUTO_INCREMENT PRIMARY KEY, pseudo VARCHAR(64), password VARCHAR(64));");

   // Création de la table employees
   printf("Création de la table employees...\n");
   mysql_query(connexion, "DROP TABLE IF EXISTS employees;");
   mysql_query(connexion, "CREATE TABLE employees(id INT AUTO_INCREMENT PRIMARY KEY, pseudo VARCHAR(64), password VARCHAR(64));");

   // Création de la table factures
   printf("Création de la table factures...\n");
   mysql_query(connexion, "DROP TABLE IF EXISTS factures;");
   mysql_query(connexion, "CREATE TABLE factures(id INT AUTO_INCREMENT PRIMARY KEY, idClient INT, date DATE, montant FLOAT, paye BOOLEAN);");

   // Création de la table ventes
   printf("Création de la table ventes...\n");
   mysql_query(connexion, "DROP TABLE IF EXISTS ventes;");
   mysql_query(connexion, "CREATE TABLE ventes(idFacture INT, idArticle INT, quantite INT, PRIMARY KEY (idFacture, idArticle), FOREIGN KEY (idFacture) REFERENCES factures(id), FOREIGN KEY (idArticle) REFERENCES articles(id));");

   // Insertion de données
   printf("Insertions de 2 clients de test\n");
   mysql_query(connexion, "INSERT INTO clients (pseudo, password) VALUES ('Alex', 'abc123')");
   mysql_query(connexion, "INSERT INTO clients (pseudo, password) VALUES ('Cyril', 'abc123')");
   printf("Insertions de 2 employees de test\n");
      mysql_query(connexion, "INSERT INTO employees (pseudo, password) VALUES ('Alex', 'abc123')");
      mysql_query(connexion, "INSERT INTO employees (pseudo, password) VALUES ('Cyril', 'abc123')");

   // Déconnexion de la base de données
   mysql_close(connexion);

   return 0;
}
