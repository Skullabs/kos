package kos.api;

public class HandledResponseException extends RuntimeException
{
    final Response response;

    public HandledResponseException(Response response){
        super("Handled Response: " + response);
        this.response = response;
    }
}