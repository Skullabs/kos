/*
 * Copyright © 2020 Secure Code Warrior BVBA. All rights reserved.
 * <p>
 * See your license agreement for the terms and conditions of use.
 * Any other usage without prior written consent of Secure Code Warrior BVBA is prohibited.
 * <p>
 * If there is no license agreement, this material may not be reproduced, displayed, printed, modified
 * or distributed in whole or in part in any manner or on any media without the prior written permission
 * of Secure Code Warrior BVBA. This material is provided 'as is' and 'as available'.
 * Secure Code Warrior BVBA expressly disclaims all warranties of any kind, whether express or implied,
 * including (without limitation) warranties of merchantability, fitness for a particular purpose and
 * non-infringement. Secure Code Warrior BVBA reserves the right to make changes or updates to this
 * material at any time without notice. In no event shall Secure Code Warrior BVBA be liable for any
 * damage (whether indirect, incidental, special, consequential, direct, loss of revenue or profit, loss
 * or corruption of data, loss of use, the cost of procuring replacement goods, opportunity loss, loss
 * of anticipated savings, resulting from or in connection with your use of this material.
 */
package kos.api;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultExceptionHandlerTest
{
    KosConfiguration kosConfiguration = new MutableKosConfiguration();
    DefaultExceptionHandler handler = new DefaultExceptionHandler(kosConfiguration);

    @Mock HttpServerResponse response;
    @Mock HttpServerRequest request;

    @DisplayName("SHOULD return the wrapped response WHEN handling HandledResponseExceptions")
    @Test void handle(){
        val cause = new HandledResponseException(Response.UNAUTHORIZED);
        val handledResponse = handler.handle(request, response, cause);
        assertEquals(Response.UNAUTHORIZED, handledResponse);
    }

    @DisplayName("SHOULD wrapped a 500 response WHEN handling unknown exceptions")
    @Test void handle2(){
        val cause = new NullPointerException("id is null");
        val handledResponse = handler.handle(request, response, cause);
        assertEquals(500, handledResponse.statusCode());
        assertEquals(
            Map.of(HttpHeaders.CONTENT_TYPE, "text/plain"),
            handledResponse.headers());
    }
}