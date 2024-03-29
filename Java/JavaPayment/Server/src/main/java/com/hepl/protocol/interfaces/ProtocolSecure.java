package com.hepl.protocol.interfaces;

import com.hepl.bridge.DbConnection;
import com.hepl.protocol.EndConnectionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.SQLException;

public interface ProtocolSecure {
    Response handleRequest(Request request, DbConnection connection, SecretKey ClientCle) throws EndConnectionException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException, IOException, SQLException, SignatureException, ClassNotFoundException, CertificateException, KeyStoreException, UnrecoverableKeyException;
    public String getName();
}
