package com.hepl.protocol.interfaces;

import com.hepl.bridge.DbConnection;
import com.hepl.protocol.EndConnectionException;

public interface Protocol {
    Response handleRequest(Request request, DbConnection connection) throws EndConnectionException;

    public String getName();
}
