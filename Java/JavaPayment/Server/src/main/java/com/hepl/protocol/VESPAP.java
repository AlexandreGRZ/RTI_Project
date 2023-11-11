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
            handleLogoutRequest((LogoutRequest) request);
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
        ArrayList<Facture> factures;

        try {
            factures = connection.getFactures(request.getIdClient());
        } catch (SQLException e) {
            factures = null;
        }

        return new GetFacturesResponse(factures);
    }

    private synchronized PayFactureResponse handlePayFactureRequest(PayFactureRequest request, DbConnection connection) {
        boolean success;

        try {
            success = connection.payFacture(request.getIdFacture());
        } catch (SQLException e) {
            success = false;
        }

        return new PayFactureResponse(success);
    }

    private synchronized void handleLogoutRequest(LogoutRequest request) throws EndConnectionException {
        throw new EndConnectionException(null);
    }
}
