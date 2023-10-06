#include "windowclient.h"
#include "ui_windowclient.h"
#include "SocketLibrary.h"
#include <QMessageBox>
#include <string>
#include <unistd.h>
using namespace std;

extern WindowClient *w;

#define REPERTOIRE_IMAGES "images/"

WindowClient::WindowClient(QWidget *parent) : QMainWindow(parent), ui(new Ui::WindowClient)
{
    ui->setupUi(this);

    // Configuration de la table du panier (ne pas modifer)
    ui->tableWidgetPanier->setColumnCount(3);
    ui->tableWidgetPanier->setRowCount(0);
    QStringList labelsTablePanier;
    labelsTablePanier << "Article"
                      << "Prix à l'unité"
                      << "Quantité";
    ui->tableWidgetPanier->setHorizontalHeaderLabels(labelsTablePanier);
    ui->tableWidgetPanier->setSelectionMode(QAbstractItemView::SingleSelection);
    ui->tableWidgetPanier->setSelectionBehavior(QAbstractItemView::SelectRows);
    ui->tableWidgetPanier->horizontalHeader()->setVisible(true);
    ui->tableWidgetPanier->horizontalHeader()->setDefaultSectionSize(160);
    ui->tableWidgetPanier->horizontalHeader()->setStretchLastSection(true);
    ui->tableWidgetPanier->verticalHeader()->setVisible(false);
    ui->tableWidgetPanier->horizontalHeader()->setStyleSheet("background-color: lightyellow");

    ui->pushButtonPayer->setText("Confirmer achat");
    setPublicite("!!! Bienvenue sur le Maraicher en ligne !!!");

    // Exemples à supprimer
    // ajouteArticleTablePanier("cerises", 8.96, 2);

    createClientSocket();
}

WindowClient::~WindowClient()
{
    delete ui;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Fonctions utiles : ne pas modifier /////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setNom(const char *Text)
{
    if (strlen(Text) == 0)
    {
        ui->lineEditNom->clear();
        return;
    }
    ui->lineEditNom->setText(Text);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
const char *WindowClient::getNom()
{
    strcpy(nom, ui->lineEditNom->text().toStdString().c_str());
    return nom;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setMotDePasse(const char *Text)
{
    if (strlen(Text) == 0)
    {
        ui->lineEditMotDePasse->clear();
        return;
    }
    ui->lineEditMotDePasse->setText(Text);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
const char *WindowClient::getMotDePasse()
{
    strcpy(motDePasse, ui->lineEditMotDePasse->text().toStdString().c_str());
    return motDePasse;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setPublicite(const char *Text)
{
    if (strlen(Text) == 0)
    {
        ui->lineEditPublicite->clear();
        return;
    }
    ui->lineEditPublicite->setText(Text);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setImage(const char *image)
{
    // Met à jour l'image
    char cheminComplet[80];
    sprintf(cheminComplet, "%s%s", REPERTOIRE_IMAGES, image);
    QLabel *label = new QLabel();
    label->setSizePolicy(QSizePolicy::Ignored, QSizePolicy::Ignored);
    label->setScaledContents(true);
    QPixmap *pixmap_img = new QPixmap(cheminComplet);
    label->setPixmap(*pixmap_img);
    label->resize(label->pixmap()->size());
    ui->scrollArea->setWidget(label);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
int WindowClient::isNouveauClientChecked()
{
    if (ui->checkBoxNouveauClient->isChecked())
        return 1;
    return 0;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setArticle(const char *intitule, float prix, int stock, const char *image)
{
    ui->lineEditArticle->setText(intitule);
    if (prix >= 0.0)
    {
        char Prix[20];
        sprintf(Prix, "%.2f", prix);
        ui->lineEditPrixUnitaire->setText(Prix);
    }
    else
        ui->lineEditPrixUnitaire->clear();
    if (stock >= 0)
    {
        char Stock[20];
        sprintf(Stock, "%d", stock);
        ui->lineEditStock->setText(Stock);
    }
    else
        ui->lineEditStock->clear();
    setImage(image);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
int WindowClient::getQuantite()
{
    return ui->spinBoxQuantite->value();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setTotal(float total)
{
    if (total >= 0.0)
    {
        char Total[20];
        sprintf(Total, "%.2f", total);
        ui->lineEditTotal->setText(Total);
    }
    else
        ui->lineEditTotal->clear();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::loginOK()
{
    ui->pushButtonLogin->setEnabled(false);
    ui->pushButtonLogout->setEnabled(true);
    ui->lineEditNom->setReadOnly(true);
    ui->lineEditMotDePasse->setReadOnly(true);
    ui->checkBoxNouveauClient->setEnabled(false);

    ui->spinBoxQuantite->setEnabled(true);
    ui->pushButtonPrecedent->setEnabled(true);
    ui->pushButtonSuivant->setEnabled(true);
    ui->pushButtonAcheter->setEnabled(true);
    ui->pushButtonSupprimer->setEnabled(true);
    ui->pushButtonViderPanier->setEnabled(true);
    ui->pushButtonPayer->setEnabled(true);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::logoutOK()
{
    ui->pushButtonLogin->setEnabled(true);
    ui->pushButtonLogout->setEnabled(false);
    ui->lineEditNom->setReadOnly(false);
    ui->lineEditMotDePasse->setReadOnly(false);
    ui->checkBoxNouveauClient->setEnabled(true);

    ui->spinBoxQuantite->setEnabled(false);
    ui->pushButtonPrecedent->setEnabled(false);
    ui->pushButtonSuivant->setEnabled(false);
    ui->pushButtonAcheter->setEnabled(false);
    ui->pushButtonSupprimer->setEnabled(false);
    ui->pushButtonViderPanier->setEnabled(false);
    ui->pushButtonPayer->setEnabled(false);

    setNom("");
    setMotDePasse("");
    ui->checkBoxNouveauClient->setCheckState(Qt::CheckState::Unchecked);

    setArticle("", -1.0, -1, "");

    w->videTablePanier();
    w->setTotal(-1.0);
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Fonctions utiles Table du panier (ne pas modifier) /////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::ajouteArticleTablePanier(const char *article, float prix, int quantite)
{
    char Prix[20], Quantite[20];

    sprintf(Prix, "%.2f", prix);
    sprintf(Quantite, "%d", quantite);

    // Ajout possible
    int nbLignes = ui->tableWidgetPanier->rowCount();
    nbLignes++;
    ui->tableWidgetPanier->setRowCount(nbLignes);
    ui->tableWidgetPanier->setRowHeight(nbLignes - 1, 10);

    QTableWidgetItem *item = new QTableWidgetItem;
    item->setFlags(Qt::ItemIsSelectable | Qt::ItemIsEnabled);
    item->setTextAlignment(Qt::AlignCenter);
    item->setText(article);
    ui->tableWidgetPanier->setItem(nbLignes - 1, 0, item);

    item = new QTableWidgetItem;
    item->setFlags(Qt::ItemIsSelectable | Qt::ItemIsEnabled);
    item->setTextAlignment(Qt::AlignCenter);
    item->setText(Prix);
    ui->tableWidgetPanier->setItem(nbLignes - 1, 1, item);

    item = new QTableWidgetItem;
    item->setFlags(Qt::ItemIsSelectable | Qt::ItemIsEnabled);
    item->setTextAlignment(Qt::AlignCenter);
    item->setText(Quantite);
    ui->tableWidgetPanier->setItem(nbLignes - 1, 2, item);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::videTablePanier()
{
    ui->tableWidgetPanier->setRowCount(0);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
int WindowClient::getIndiceArticleSelectionne()
{
    QModelIndexList liste = ui->tableWidgetPanier->selectionModel()->selectedRows();
    if (liste.size() == 0)
        return -1;
    QModelIndex index = liste.at(0);
    int indice = index.row();
    return indice;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Fonctions permettant d'afficher des boites de dialogue (ne pas modifier ////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::dialogueMessage(const char *titre, const char *message)
{
    QMessageBox::information(this, titre, message);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::dialogueErreur(const char *titre, const char *message)
{
    QMessageBox::critical(this, titre, message);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////// CLIC SUR LA CROIX DE LA FENETRE /////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::closeEvent(QCloseEvent *event)
{
    printf("close\n");
    ::close(IClientSocket);
    exit(0);
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Fonctions clics sur les boutons ////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonLogin_clicked()
{
    const char *username = getNom();
    const char *password = getMotDePasse();
    char requete[256];
    int nbLus;
    if (strlen(username) > 64 || strlen(password) > 64)
    {
        dialogueErreur("login error", "Your username and password can't exceed 64 characters!");
        return;
    }
    if (strchr(username, '#') != NULL || strchr(username, ' ') != NULL || strchr(password, '#') != NULL || strchr(password, ' ') != NULL)
    {
        dialogueErreur("login error", "Your username and password can't contain \"#\" characters or spaces!");
        return;
    }

    sprintf(requete, "LOGIN#%s#%s", username, password);
    Send(IClientSocket, requete, strlen(requete));

    printf("En attente de reponse ... \n");
    if ((nbLus = Receive(IClientSocket, requete)) <= 0)
        serverError();

    if (strstr(requete, "#OK") != NULL)
    {
        loginOK();
        consultMtx.lock();
        char requete[1024];
        idArticle = 1;

        sprintf(requete, "CONSULT#%d", idArticle);
        Send(IClientSocket, requete, strlen(requete));

        printf("En attente de reponse ... \n");
        if ((nbLus = Receive(IClientSocket, requete)) <= 0)
            serverError();

        if (strstr(requete, "#ERR") != NULL)
            idArticle++;
        else
            setNewArticle(requete);
        consultMtx.unlock();
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonLogout_clicked()
{
    char requete[256];
    int nbLus;

    strcpy(requete, "LOGOUT");
    Send(IClientSocket, requete, strlen(requete));

    printf("En attente de reponse ... \n");
    if ((nbLus = Receive(IClientSocket, requete)) <= 0)
        serverError();

    if (strstr(requete, "#OK") != NULL)
        logoutOK();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonSuivant_clicked()
{
    char requete[1024];
    int nbLus;
    consultMtx.lock();
    int id_temp = idArticle + 1;

    sprintf(requete, "CONSULT#%d", id_temp);
    Send(IClientSocket, requete, strlen(requete));

    printf("En attente de reponse ... \n");
    if ((nbLus = Receive(IClientSocket, requete)) <= 0)
        serverError();

    if (strstr(requete, "#OK") != NULL)
    {
        idArticle = id_temp;
        setNewArticle(requete);
    }
    consultMtx.unlock();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonPrecedent_clicked()
{
    char requete[1024];
    int nbLus;
    consultMtx.lock();
    int id_temp = idArticle - 1;

    sprintf(requete, "CONSULT#%d", id_temp);
    Send(IClientSocket, requete, strlen(requete));

    printf("En attente de reponse ... \n");
    if ((nbLus = Receive(IClientSocket, requete)) <= 0)
        serverError();

    if (strstr(requete, "#OK") != NULL)
    {
        idArticle = id_temp;
        setNewArticle(requete);
    }
    consultMtx.unlock();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonAcheter_clicked()
{
    int nbLus, quantite, id;
    char requete[1024];

    consultMtx.lock();
    int id_temp = idArticle;
    consultMtx.unlock();

    achatMtx.lock();

    sprintf(requete, "ACHAT#%d#%d", id_temp, getQuantite());
    Send(IClientSocket, requete, strlen(requete));

    printf("En attente de reponse ... \n");
    if ((nbLus = Receive(IClientSocket, requete)) <= 0)
        serverError();

    strtok(requete, "#");         // ACHAT
    id = atoi(strtok(NULL, "#")); // ID
    if (id == -1)
    {
        dialogueErreur("Achat error", "Article introuvable. Achat impossible.");
        achatMtx.unlock();
        return;
    }
    strtok(NULL, "#");                  // Nom
    atof(strtok(NULL, "#"));            // Prix
    quantite = atoi(strtok(NULL, "#")); // Quantité
    if (quantite == 0)
    {
        dialogueErreur("Achat error", "Le stock n'est pas suffisant. Achat impossible.");
        achatMtx.unlock();
        return;
    }

    // Update table panier
    updateCaddie();

    achatMtx.unlock();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonSupprimer_clicked()
{
    int nbLus = 0;
    char requete[1024];
    int indice = getIndiceArticleSelectionne();

    if (indice == -1)
    {
        dialogueMessage("Aucun article sélectionné", "Veuillez sélectionner un article à supprimer du panier");
        return;
    }

    achatMtx.lock();

    sprintf(requete, "CANCEL#%d", idsPanier[indice]);
    Send(IClientSocket, requete, strlen(requete));

    printf("En attente de reponse ... \n");
    if ((nbLus = Receive(IClientSocket, requete)) <= 0)
        serverError();

    if (strstr(requete, "#OK") != NULL)
        updateCaddie();
    else
        dialogueErreur("Suppresion error", "Une erreur est survenue lors de la tentative de suppression de l'article dans le panier");

    achatMtx.unlock();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonViderPanier_clicked()
{
    achatMtx.lock();
    int nbLus;
    char requete[256];
    strcpy(requete, "CANCELALL");
    Send(IClientSocket, requete, strlen(requete));

    printf("En attente de reponse ... \n");
    if ((nbLus = Receive(IClientSocket, requete)) <= 0)
        serverError();

    if (strstr(requete, "#OK") != NULL)
        videTablePanier();
    else
        dialogueErreur("Server error", "Une erreur est survenue lors de la tentative du vidage du panier.");

    achatMtx.unlock();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonPayer_clicked()
{
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::createClientSocket()
{
    char ip[32];
    sprintf(ip, "127.0.0.1");
    IClientSocket = ClientSocket(ip, 50000);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setNewArticle(char *requete)
{
    char *nom, *image;
    float prix;
    int stock;

    strtok(requete, "#");            // CONSULT
    strtok(NULL, "#");               // OK
    strtok(NULL, "#");               // Id
    nom = strtok(NULL, "#");         // Nom
    prix = atof(strtok(NULL, "#"));  // Prix
    stock = atoi(strtok(NULL, "#")); // Stock
    image = strtok(NULL, "#");       // Image
    setArticle(nom, prix, stock, image);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::serverError()
{
    dialogueErreur("Serveur Error", "La connexion avec le serveur semble interrompue");
    close();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::updateCaddie()
{
    char requete[1024];
    char *nom, *temp;
    float prix;
    int quantite, nbLus;

    videTablePanier();

    sprintf(requete, "CADDIE");
    Send(IClientSocket, requete, strlen(requete));

    printf("En attente de reponse ... \n");
    if ((nbLus = Receive(IClientSocket, requete)) <= 0)
        serverError();

    strtok(requete, "#");     // CADDIE
    temp = strtok(NULL, "#"); // first Id
    idsPanier.clear();
    while (temp != NULL)
    {
        idsPanier.push_back(atoi(temp));
        nom = strtok(NULL, "#");            // Nom
        prix = atof(strtok(NULL, "#"));     // Prix
        quantite = atoi(strtok(NULL, "#")); // Quantité
        ajouteArticleTablePanier(nom, prix, quantite);
        temp = strtok(NULL, "#"); // Id
    }
}
