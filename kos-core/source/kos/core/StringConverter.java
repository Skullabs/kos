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

package kos.core;

import lombok.*;

import java.time.*;
import java.util.*;
import java.util.function.*;

/**
 * Abstracts how {@code kos} can convert strings (like Headers
 * and query parameters) into objects.
 */
public interface StringConverter {

    <T> StringConverter register(Class<T> type, Function<String, T> converter);

    <T> T convertTo(Class<T> type, String value);

    default String asString(char v){ return String.valueOf(v); }
    default String asString(boolean v){ return String.valueOf(v); }
    default String asString(long v){ return String.valueOf(v); }
    default String asString(double v){ return String.valueOf(v); }
    default String asString(int v){ return String.valueOf(v); }

    default String asString(Object object){
        return String.valueOf(object);
    }

    /**
     * Default implementation of {@link StringConverter}.
     */
    class DefaultStringConverter implements StringConverter {

        private final Map<Class, Function> converters = new HashMap<>();

        DefaultStringConverter() {
            register(String.class, Function.identity());
            register(Character.TYPE, s -> s.charAt(0));
            register(Character.class, s -> s.charAt(0));
            register(Boolean.TYPE, Boolean::valueOf);
            register(Boolean.class, Boolean::valueOf);
            register(Long.TYPE, Long::valueOf);
            register(Long.class, Long::valueOf);
            register(Double.TYPE, Double::valueOf);
            register(Double.class, Double::valueOf);
            register(Integer.TYPE, Integer::valueOf);
            register(Integer.class, Integer::valueOf);
            register(LocalDate.class, LocalDate::parse);
            register(LocalDateTime.class, LocalDateTime::parse);
            register(ZonedDateTime.class, ZonedDateTime::parse);
            register(UUID.class, UUID::fromString);
        }

        public <T> StringConverter register(Class<T> type, Function<String, T> converter) {
            converters.put(type, converter);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> T convertTo(Class<T> type, String value) {
            val converter = converters.computeIfAbsent(type, s -> {
                throw new UnsupportedOperationException("Can't convert String to " + type);
            });

            return (T) converter.apply(value);
        }
    }
}
