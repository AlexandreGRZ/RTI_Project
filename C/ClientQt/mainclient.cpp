#include "windowclient.h"

#include <QApplication>
#include <signal.h>

WindowClient *w;

void handlerSIGINT(int sig);

int main(int argc, char *argv[])
{
    if (argc != 3)
    {
        printf("Ip et port serveur requise pour lancer le client\n./Client <ip-serveur> <port>\n");
        return -1;
    }

    struct sigaction A;
    A.sa_handler = handlerSIGINT;
    A.sa_flags = 0;
    sigaction(SIGINT, &A, NULL);

    QApplication a(argc, argv);
    w = new WindowClient(argc, argv);
    w->show();
    return a.exec();
}

void handlerSIGINT(int sig)
{
    w->close();
}
