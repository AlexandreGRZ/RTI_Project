package com.hepl.protocol.requests;

import com.hepl.protocol.interfaces.Request;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;

public class getFactureSecureRequest implements Request {
    private int idClient;

    SecretKey CleSession;
    private byte[] signature;

    public getFactureSecureRequest(int idClient, SecretKey cleSession, PrivateKey clepriverClient) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException, SignatureException, ClassNotFoundException {
        this.idClient = idClient;
        this.CleSession = cleSession;

        Signature s = Signature.getInstance("SHA1withRSA","BC");
        PrivateKey clePriveClient = clepriverClient;
        s.initSign(clePriveClient);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(idClient);
        s.update(baos.toByteArray());
        signature = s.sign();
    }

    public boolean VerifySignature(PublicKey clePubliqueClient) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException, SignatureException {
        // Construction de l'objet Signature
        Signature s = Signature.getInstance("SHA1withRSA","BC");
        s.initVerify(clePubliqueClient);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(idClient);
        s.update(baos.toByteArray());

        // Vérification de la signature reçue
        return s.verify(signature);
    }


    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

}
