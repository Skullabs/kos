package kos.api;

import lombok.Getter;

public class HandledResponseException extends RuntimeException
{
    @Getter
    private final Response response;

    public HandledResponseException(Response response){
        super("Handled Response: " + response);
        this.response = response;
    }
}