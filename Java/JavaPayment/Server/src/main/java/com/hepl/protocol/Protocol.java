package com.hepl.protocol;

import java.net.Socket;

public interface Protocol {
    Response handleRequest(Request request, Socket socket)throws EndConnectionException;
}
