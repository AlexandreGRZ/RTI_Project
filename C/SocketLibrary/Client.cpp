#ifndef CLIENT_CPP
#define CLIENT_CPP


#include <stdio.h>
#include <stdlib.h>
#include "SocketLibrary.h"



int main(int argc,char* argv[])
{
    int num = atoi(argv[2]);
    ClientSocket(argv[1],num);

}


#endif