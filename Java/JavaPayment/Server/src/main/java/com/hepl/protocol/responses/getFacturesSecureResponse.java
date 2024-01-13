package com.hepl.protocol.responses;

import com.hepl.model.Facture;
import com.hepl.protocol.interfaces.Response;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;

import static com.hepl.protocol.Mycrypto.CryptSymDES;

public class getFacturesSecureResponse implements Response {
    private SecretKey cleSession;

    private byte[] factureBytesCrypte ;

    public getFacturesSecureResponse(ArrayList<Facture> factures, SecretKey cleSession) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {

        this.cleSession = cleSession;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(factures);
        objectOutputStream.close();
        byte[] facturesBytes = byteArrayOutputStream.toByteArray();

        factureBytesCrypte = CryptSymDES(cleSession, facturesBytes);
    }

    public byte[] getFactureBytesCrypte() {
        return factureBytesCrypte;
    }
}
