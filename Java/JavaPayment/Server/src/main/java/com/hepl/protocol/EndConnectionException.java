package com.hepl.protocol;

public class EndConnectionException extends Exception{
    private Response response;
    public EndConnectionException(Response response)
    {
        super("End of connection decided by the protocol");
        this.response = response;
    }
    public Response getResponse()
    {
        return response;
    }
}
