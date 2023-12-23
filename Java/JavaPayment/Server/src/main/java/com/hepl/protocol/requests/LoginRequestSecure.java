package com.hepl.protocol.requests;

import com.hepl.protocol.interfaces.Request;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;

public class LoginRequestSecure implements Request {

    private String login;
    private long temps;
    private double alea;
    private byte[] digest;


    public LoginRequestSecure(String login,String password) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        this.login = login;

        this.temps = new Date().getTime();
        this.alea = Math.random();
        MessageDigest md = MessageDigest.getInstance("SHA-1","BC");
        md.update(login.getBytes());
        md.update(password.getBytes());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeLong(temps);
        dos.writeDouble(alea);
        md.update(baos.toByteArray());
        digest = md.digest();
    }
    public String getLogin() { return login; }
    public boolean VerifyPassword(String password) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        // Construction du digest local
        MessageDigest md = MessageDigest.getInstance("SHA-1","BC");
        md.update(login.getBytes());
        md.update(password.getBytes());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeLong(temps);
        dos.writeDouble(alea);
        md.update(baos.toByteArray());

        byte[] digestLocal = md.digest();
        // Comparaison digest reçu et digest local
        return MessageDigest.isEqual(digest,digestLocal);
    }

}
