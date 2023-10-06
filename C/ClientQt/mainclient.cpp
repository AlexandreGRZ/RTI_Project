#include "windowclient.h"

#include <QApplication>
#include <signal.h>

WindowClient *w;

void handlerSIGINT(int sig);

int main(int argc, char *argv[])
{
    struct sigaction A;
    A.sa_handler = handlerSIGINT;
    A.sa_flags = 0;
    sigaction(SIGINT, &A, NULL);

    QApplication a(argc, argv);
    w = new WindowClient();
    w->show();
    return a.exec();
}

void handlerSIGINT(int sig)
{
    w->close();
}
