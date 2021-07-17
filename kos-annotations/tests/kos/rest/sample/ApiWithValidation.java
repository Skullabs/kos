package kos.rest.sample;

import kos.rest.*;
import kos.validation.Valid;

@RestApi("/events")
public class ApiWithValidation {

    @PUT(":id")
    void updateEvent(
        @Param String id,
        @Body @Valid Event receivedEvent
    ) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

class Event {

}