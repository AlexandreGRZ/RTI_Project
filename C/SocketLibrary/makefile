all : testsocket Client

testsocket:	test.cpp SocketLibrary.o
		g++ SocketLibrary.cpp test.cpp -o testsocket

SocketLibrary.o:	SocketLibrary.cpp SocketLibrary.h
		g++ SocketLibrary.cpp -c

Client:	Client.cpp SocketLibrary.o
		g++ SocketLibrary.cpp Client.cpp -o Client