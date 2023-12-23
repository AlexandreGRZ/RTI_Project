package com.hepl.protocol;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.*;

public class generateCleRSA {
    public static void main(String args[]) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {

        Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator genCles = KeyPairGenerator.getInstance("RSA","BC");
        genCles.initialize(512,new SecureRandom());
        KeyPair deuxCles = genCles.generateKeyPair();
        PublicKey clePublique = deuxCles.getPublic();
        PrivateKey clePrivee = deuxCles.getPrivate();
        System.out.println(" *** Cle publique generee = " + clePublique);
        System.out.println(" *** Cle privee generee = " + clePrivee);

        // Sérialisation des clés dans des fichiers différents

        ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream("../cleServeur/clePubliqueClients.ser"));
        oos1.writeObject(clePublique);
        oos1.close();
        System.out.println("Sérialisation de la clé publique dans le fichier clePubliqueServeur.ser");
        ObjectOutputStream oos2 = new ObjectOutputStream(new FileOutputStream("../cleServeur/clePriveeClients.ser"));
        oos2.writeObject(clePrivee);
        oos2.close();
        System.out.println("Sérialisation de la clé privée dans le fichier clePriveeServeur.ser");
    }


}
