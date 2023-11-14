package com.hepl.protocol;

import com.hepl.bridge.DbConnection;
import com.hepl.model.Facture;
import com.hepl.protocol.interfaces.Protocol;
import com.hepl.protocol.interfaces.Request;
import com.hepl.protocol.interfaces.Response;
import com.hepl.protocol.requests.GetFacturesRequest;
import com.hepl.protocol.requests.LoginRequest;
import com.hepl.protocol.requests.LogoutRequest;
import com.hepl.protocol.requests.PayFactureRequest;
import com.hepl.protocol.responses.GetFacturesResponse;
import com.hepl.protocol.responses.LoginResponse;
import com.hepl.protocol.responses.PayFactureResponse;

import java.sql.SQLException;
import java.util.ArrayList;

public class VESPAP implements Protocol {
    public VESPAP() {
    }

    @Override
    public synchronized Response handleRequest(Request request, DbConnection connection) throws EndConnectionException {
        if (request instanceof LoginRequest)
            return handleLoginRequest((LoginRequest) request, connection);
        if (request instanceof GetFacturesRequest)
            return handleGetFacturesRequest((GetFacturesRequest) request, connection);
        if (request instanceof PayFactureRequest)
            return handlePayFactureRequest((PayFactureRequest) request, connection);
        if (request instanceof LogoutRequest) {
            handleLogoutRequest();
            throw new EndConnectionException(null);
        }
        return null;
    }

    @Override
    public String getName() {
        return "VESPAP";
    }

    private synchronized LoginResponse handleLoginRequest(LoginRequest request, DbConnection connection) {
        boolean success;
        System.out.println("Login request");
        if (request.getLogin() == null || request.getPassword() == null)
            return new LoginResponse(false);

        try {
            success = connection.login(request.getLogin(), request.getPassword());
        } catch (SQLException e) {
            success = false;
        }

        if (success)
            System.out.println("Connection successful");
        else
            System.out.println("Connection failed");

        return new LoginResponse(success);
    }

    private synchronized GetFacturesResponse handleGetFacturesRequest(GetFacturesRequest request, DbConnection connection) {
        System.out.println("Get facture request");
        ArrayList<Facture> factures;

        try {
            factures = connection.getFactures(request.getIdClient());
        } catch (SQLException e) {
            factures = null;
        }

        if (factures != null)
            System.out.println("Get factures successful");
        System.out.println("Get factures failed");

        return new GetFacturesResponse(factures);
    }

    private synchronized PayFactureResponse handlePayFactureRequest(PayFactureRequest request, DbConnection connection) {
        System.out.println("Pay facture request");
        boolean success;

        if (!checkVisaCard(request.getNumCard()))
            return new PayFactureResponse(false);

        try {
            success = connection.payFacture(request.getIdFacture());
        } catch (SQLException e) {
            System.out.println("Payment request failed");
            success = false;
        }

        if (success)
            System.out.println("Payment request successful");
        return new PayFactureResponse(success);
    }

    public static boolean checkVisaCard(String number) {
        int sum = 0;
        boolean oddNumber = true;
        int tmp;

        for (int i = number.length() - 1; i >= 0; i--) {
            if (oddNumber) {
                sum += (int) number.charAt(i) - '0';
            } else {
                tmp = (int) number.charAt(i) - '0';
                tmp *= 2;
                if (tmp >= 10)
                    tmp = (tmp / 10) + (tmp % 10);
                sum += tmp;
            }
            oddNumber = !oddNumber;
        }
        return (sum % 10) == 0;
    }

    private synchronized void handleLogoutRequest() throws EndConnectionException {
        System.out.println("Logout request");
        throw new EndConnectionException(null);
    }
}
