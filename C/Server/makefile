all : Server

Server: Server.cpp ../SocketLibrary/SocketLibrary.o SMOP.o AccesBD.o
	g++ Server.cpp ../SocketLibrary/SocketLibrary.o SMOP.o AccesBD.o -L/usr/lib64/mysql -I/usr/include/mysql -I../SocketLibrary -o Server -lpthread -lmysqlclient

SMOP.o: SMOP.cpp SMOP.h
	g++ -c SMOP.cpp -o SMOP.o 

AccesBD.o:	AccesBD.cpp AccesBD.h
		g++ -c AccesBD.cpp AccesBD.h -I/usr/include/mysql

clean:
	rm -rf *.o Server