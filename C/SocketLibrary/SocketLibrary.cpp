#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>

int ServerSocket(int port){
    //Convertion du port de int en char
    char CPort[20];
    snprintf(CPort, sizeof(CPort), "%d", port);

    int IServeurSocket = socket(AF_INET, SOCK_STREAM, 0);

    if(IServeurSocket == -1)
    {
        perror("Erreur de socket()");
        return -1;
    }
    else
    {   
        printf("Socket cree correctement \n");
        printf("Lier la socket a une ip\n");

        // Pour la recherche
        struct addrinfo hints;
        struct addrinfo *results;
        // Pour l'affichage des resultats
        char host[NI_MAXHOST];
        char port[NI_MAXSERV];
        struct addrinfo* info;

        memset(&hints,0,sizeof(struct addrinfo)); // initialisation à 0
        hints.ai_family = AF_INET;
        hints.ai_socktype = SOCK_STREAM;
        hints.ai_flags = AI_PASSIVE | AI_NUMERICSERV;
    
        if (getaddrinfo(NULL,CPort,&hints,&results) != 0)
        {
            printf("Erreur de getaddrinfo\n");
            return -1;
        }   
        else
        {
            // Affichage du contendu des adresses obtenues au format "hote" et "service"
            for (info = results ; info != NULL ; info = info->ai_next)
            {
                getnameinfo(info->ai_addr,info->ai_addrlen,
                host,NI_MAXHOST,
                port,NI_MAXSERV,
                0);
                printf("Hote: %s -- Service: %s\n",host,port);
            }

            if (bind(IServeurSocket,results->ai_addr,results->ai_addrlen) < 0)
            {
                perror("Erreur de bind()");
                return -1;
            }

            freeaddrinfo(results);
            printf("bind() reussi !\n");
        }

    }
    
    
    

    return IServeurSocket;
}

int Accept(int sEcoute,char *ipClient){
    

    if(listen(sEcoute, SOMAXCONN) == -1)
    {
        perror("Erreur de listen()");
        return -1;

    }
    else
    {
        int sService;
        if ((sService = accept(sEcoute,NULL,NULL)) == -1)
        {
            perror("Erreur de accept()");
            return -1;
        }
        printf("accept() reussi !");
        printf("socket de service = %d\n",sService);
        
        // Recuperation d'information sur le client connecte
        struct sockaddr_in adrClient;
        socklen_t adrClientLen = sizeof(struct sockaddr_in); 

        // Affichage du contenu de l'adresse obtenue
        char host[NI_MAXHOST];
        char port[NI_MAXSERV];

        getpeername(sService,(struct sockaddr*)&adrClient,&adrClientLen);
        getnameinfo((struct sockaddr*)&adrClient,adrClientLen,
        host,NI_MAXHOST,port,NI_MAXSERV,NI_NUMERICSERV | NI_NUMERICHOST);

        printf("Client connecte --> Adresse IP: %s -- Port: %s\n",host,port);
        strcpy(ipClient, &host[0]);
        return sService;
    }

    return 0;
}

int ClientSocket(char* ipServeur,int portServeur){
    
    char CPort[20];
    snprintf(CPort, sizeof(CPort), "%d", portServeur);
    
    int sClient;
    printf("pid = %d\n",getpid());
    // Creation de la socket
    if ((sClient = socket(AF_INET, SOCK_STREAM, 0)) == -1)
    {
        perror("Erreur de socket()");
        return -1;
    }
    printf("socket creee = %d\n",sClient);
    
    // Construction de l'adresse du serveur
    struct addrinfo hints;
    struct addrinfo *results;
    
    memset(&hints,0,sizeof(struct addrinfo));
    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_NUMERICSERV;
    
    if (getaddrinfo(ipServeur,CPort,&hints,&results) != 0)
    {   
        perror("Erreur de getaddrinfo()");
        return -1;
    }
        

    if (connect(sClient,results->ai_addr,results->ai_addrlen) == -1)
    {
        perror("Erreur de connect()");
        freeaddrinfo(results);
        return -1;
    }
    printf("connect() reussi !\n");
    
    return sClient;
}

int Send(int sSocket,char* data,int taille){

    int nb;
    char CMessageTemp[200];

    strcpy(&CMessageTemp[0], data);
    strcat(&CMessageTemp[0], "#");

    if ((nb = write(sSocket,CMessageTemp,(taille + 1))) == -1)
    {
        perror("Erreur de write()");
        kill(getpid(), SIGINT);
    }
        printf("nbEcrits = %d\n",nb);
        
    return 0;
}

int Receive(int sSocket,char* data){
    
    int nb;
    char CMessageTemp[1], CMessageServeur[100];

    strcpy(CMessageServeur, "[SERVEUR] ");
    if ((nb = read(sSocket,CMessageTemp,1)) == -1)
    {
        perror("Erreur de read()");
        kill(getpid(), SIGINT);
        return -1;
    }
    while(strcmp(&CMessageTemp[0], "#") == 0)
    {   
        strcat(data, &CMessageTemp[0]);
        if ((nb = read(sSocket,CMessageTemp,1)) == -1)
            {
                perror("Erreur de read()");
                kill(getpid(), SIGINT);
                return -1;
            }
    }
    strcat(&CMessageServeur[0], data);
    strcpy(data, &CMessageServeur[0]);

    printf("--%s--\n",data);

    return 1;
}