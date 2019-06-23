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

import kos.core.StringConverter.DefaultStringConverter;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DefaultStringConverterTest {

    private final String december3rd2009At2128 = "2009-12-03T21:28:30";
    private final String december3rd2009At2128ParisTime = december3rd2009At2128 + "+01:00";
    private final long december3rd2009At2128AsMillis = 1259872110000L;

    final DefaultStringConverter converter = new DefaultStringConverter();

    @DisplayName("SHOULD be capable to convert String into Integer")
    @Test void convertTo1(){
        val value = converter.convertTo(Integer.class, "1");
        assertEquals(1, (int)value);
    }

    @DisplayName("SHOULD be capable to convert String into Long")
    @Test void convertTo2(){
        val value = converter.convertTo(Long.class, "1");
        assertEquals(1, (long)value);
    }

    @DisplayName("SHOULD be capable to convert String into Double")
    @Test void convertTo3(){
        val value = converter.convertTo(Double.class, "1.0");
        assertEquals(1.0, (double)value);
    }

    @DisplayName("SHOULD be capable to convert String into ZonedLocalDate")
    @Test void convertTo4(){
        val value = converter.convertTo(ZonedDateTime.class, december3rd2009At2128ParisTime);
        assertEquals(december3rd2009At2128AsMillis, value.toInstant().toEpochMilli());
    }

    @DisplayName("SHOULD be capable to convert String into LocalDateTime")
    @Test void convertTo5(){
        val value = converter.convertTo(LocalDateTime.class, december3rd2009At2128);
        assertEquals(december3rd2009At2128AsMillis, value.toInstant(ZoneOffset.ofHours(1)).toEpochMilli());
    }

    @DisplayName("SHOULD be capable to convert String into UUID")
    @Test void convertTo6(){
        val value = converter.convertTo(UUID.class, "00000000-0000-0000-0000-000000000000");
        val expected = UUID.fromString("00000000-0000-0000-0000-000000000000");
        assertEquals(expected, value);
    }
}