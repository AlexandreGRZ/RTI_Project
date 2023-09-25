EXEC = CreationBD AjoutTableClientsBD

all:$(EXEC)

CreationBD:
	g++ -o CreationBD CreationBD.cpp -I/usr/include/mysql -m64 -L/usr/lib64/mysql -lmysqlclient -lpthread -lz -lm -lrt -lssl -lcrypto -ldl

AjoutTableClientsBD:
	g++ -o AjoutTableClientsBD AjoutTableClientsBD.cpp -I/usr/include/mysql -m64 -L/usr/lib64/mysql -lmysqlclient -lpthread -lz -lm -lrt -lssl -lcrypto -ldl

clean:
	rm -rf $(EXEC)