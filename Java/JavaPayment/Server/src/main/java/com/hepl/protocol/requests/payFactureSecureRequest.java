package com.hepl.protocol.requests;

import com.hepl.protocol.Mycrypto;
import com.hepl.protocol.interfaces.Request;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class payFactureSecureRequest implements Request {
    private byte[] responseCrypt;

    public payFactureSecureRequest(int idFacture, String numCard, SecretKey cleSession) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(numCard);
        dos.writeInt(idFacture);

        byte[] dataToEncrypt = baos.toByteArray();

        responseCrypt = Mycrypto.CryptSymDES(cleSession, dataToEncrypt);

    }

    public byte[] getResponseCrypt() {
        return responseCrypt;
    }
}
