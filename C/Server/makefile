all : Server

Server: Server.cpp ../SocketLibrary/SocketLibrary.o SMOP.o AccesBD.o
	g++ Server.cpp ../SocketLibrary/SocketLibrary.o SMOP.o AccesBD.o -o Server -L/usr/lib64/mysql -I/usr/include/mysql -I../SocketLibrary -lpthread -lmysqlclient

SMOP.o: SMOP.cpp SMOP.h AccesBD.h
	g++ -c SMOP.cpp -I/usr/include/mysql

AccesBD.o: AccesBD.cpp AccesBD.h
	g++ -c AccesBD.cpp -I/usr/include/mysql

clean:
	rm -rf *.o Server