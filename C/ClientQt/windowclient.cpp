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
    Receive(IClientSocket, requete);
    if (strstr(requete, "#OK") != NULL)
    {
        loginOK();
        consult_mtx.lock();
        char requete[1024];
        idArticle = 1;

        sprintf(requete, "CONSULT#%d", idArticle);
        Send(IClientSocket, requete, strlen(requete));

        printf("En attente de reponse ... \n");
        Receive(IClientSocket, requete);

        if (strstr(requete, "#ERR") != NULL)
            idArticle++;
        else
            setNewArticle(requete);
        consult_mtx.unlock();
    }

    // printf("%s taille:%ld\n", username, strlen(username));
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonLogout_clicked()
{
    char requete[256];
    strcpy(requete, "LOGOUT");

    Send(IClientSocket, requete, strlen(requete));

    printf("En attente de reponse ... \n");
    Receive(IClientSocket, requete);
    if (strstr(requete, "#OK") != NULL)
        logoutOK();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonSuivant_clicked()
{
    consult_mtx.lock();
    char requete[1024];
    int id_temp = idArticle + 1;

    sprintf(requete, "CONSULT#%d", id_temp);
    Send(IClientSocket, requete, strlen(requete));

    printf("En attente de reponse ... \n");
    Receive(IClientSocket, requete);

    if (strstr(requete, "#OK") != NULL)
    {
        idArticle = id_temp;
        setNewArticle(requete);
    }
    consult_mtx.unlock();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonPrecedent_clicked()
{
    consult_mtx.lock();
    char requete[1024];
    int id_temp = idArticle - 1;

    sprintf(requete, "CONSULT#%d", id_temp);
    Send(IClientSocket, requete, strlen(requete));

    printf("En attente de reponse ... \n");
    Receive(IClientSocket, requete);

    if (strstr(requete, "#OK") != NULL)
    {
        idArticle = id_temp;
        setNewArticle(requete);
    }
    consult_mtx.unlock();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonAcheter_clicked()
{
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonSupprimer_clicked()
{
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonViderPanier_clicked()
{
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonPayer_clicked()
{
}

void WindowClient::createClientSocket()
{
    char ip[32];
    sprintf(ip, "127.0.0.1");
    IClientSocket = ClientSocket(ip, 50000);
}

void WindowClient::setNewArticle(char *requete)
{
    char nom[64], image[64];
    char *temp;
    float prix;
    int stock;

    temp = strtok(requete, "#"); // CONSULT
    temp = strtok(NULL, "#");    // OK
    temp = strtok(NULL, "#");    // ID
    temp = strtok(NULL, "#");    // nom
    strcpy(nom, temp);
    temp = strtok(NULL, "#"); // prix
    prix = atof(temp);
    temp = strtok(NULL, "#"); // stock
    stock = atoi(temp);
    temp = strtok(NULL, "#"); // image
    strcpy(image, temp);
    setArticle(nom, prix, stock, image);
}