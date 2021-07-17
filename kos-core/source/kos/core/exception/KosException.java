/*
 * Copyright 2019 Skullabs Contributors (https://github.com/skullabs)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kos.core.exception;

/**
 * Convenient {@link Exception} class.
 */
public class KosException extends RuntimeException {

    public KosException(Throwable cause ) {
        super(cause);
    }

    public KosException(Throwable cause, String message, Object...params ) {
        super(String.format(message, params), cause);
    }

    public KosException(String message, Object...params ) {
        super(String.format(message, params));
    }
}
