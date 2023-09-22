#ifndef TEST_CPP
#define TEST_CPP


#include <stdio.h>
#include <stdlib.h>
#include "SocketLibrary.h"

int main()
{
   
    int IServerSocket = ServerSocket(50000);
    char CipClient[20];
    Accept(IServerSocket, &CipClient[0]);
}
#endif