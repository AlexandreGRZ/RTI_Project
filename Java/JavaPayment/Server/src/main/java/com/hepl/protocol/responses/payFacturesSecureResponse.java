package com.hepl.protocol.responses;

import com.hepl.protocol.interfaces.Response;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class payFacturesSecureResponse implements Response {
    private boolean success;
    private byte[] hmac;



    public payFacturesSecureResponse(boolean success, SecretKey cleSession) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        this.success = success;
        Mac hm = Mac.getInstance("HMAC-MD5","BC");
        hm.init(cleSession);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeBoolean(success);

        hm.update(baos.toByteArray());
        hmac = hm.doFinal();
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean VerifyAuthenticity(SecretKey cleSession) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        // Construction du HMAC local
        Mac hm = Mac.getInstance("HMAC-MD5","BC");
        hm.init(cleSession);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeBoolean(success);
        hm.update(baos.toByteArray());
        byte[] hmacLocal = hm.doFinal();

        // Comparaison HMAC reçu et HMAC local
        return MessageDigest.isEqual(hmac,hmacLocal); }
}
