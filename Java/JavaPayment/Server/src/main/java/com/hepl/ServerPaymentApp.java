package com.hepl;

import com.hepl.bridge.DbConnection;

public class ServerPaymentApp {
    public static void main(String[] args) throws Exception{
        DbConnection connection = new DbConnection();

        connection.close();
    }
}