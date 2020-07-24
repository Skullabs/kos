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
package kos.core;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.concurrent.CompletableFuture;

/**
 *
 */
@RequiredArgsConstructor
public class VertxFutures {

    final Vertx vertx;

    public <T> Future<T> asFuture(Future<T> value) {
        return value;
    }

    public <T> Future<T> asFuture(CompletableFuture<T> value) {
        val promise = Promise.<T>promise();
        value.handleAsync((result, cause) -> {
            if (cause == null)
                promise.complete(result);
            else
                promise.fail(cause);
            return null;
        }, vertx.nettyEventLoopGroup());
        return promise.future();
    }

    public <T> Future<T> asFuture(T value) {
        return Future.succeededFuture(value);
    }
}
