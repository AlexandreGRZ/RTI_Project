EXEC = CreationBD AjoutTableClientsBD

.SILENT:
all:$(EXEC)

CreationBD:
	g++ -o CreationBD CreationBD.cpp -I/usr/include/mysql -m64 -L/usr/lib64/mysql -lmysqlclient -lpthread -lz -lm -lrt -lssl -lcrypto -ldl
	echo Création CreationBD

AjoutTableClientsBD:
	g++ -o AjoutTableClientsBD AjoutTableClientsBD.cpp -I/usr/include/mysql -m64 -L/usr/lib64/mysql -lmysqlclient -lpthread -lz -lm -lrt -lssl -lcrypto -ldl
	echo Création AjoutTableClientsBD

.SILENT:
clean:
	rm -rf $(EXEC)
	echo Suppression des fichiers objets et exécutables