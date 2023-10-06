.SILENT:
all : testsocket Client

testsocket:	test.cpp SocketLibrary.o
		g++ SocketLibrary.cpp test.cpp -o testsocket
		echo Création testsocket

SocketLibrary.o:	SocketLibrary.cpp SocketLibrary.h
		g++ SocketLibrary.cpp -c
		echo Compilation SocketLibrary

Client:	Client.cpp SocketLibrary.o
		g++ SocketLibrary.cpp Client.cpp -o Client
		echo Création Client

.SILENT:		
clean:
	rm -rf *.o Client testsocket test
	echo Suppression des fichiers objets et exécutables	