/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.mongodb.converters;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.FallbackConverter;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spi.TypeConverterRegistry;

@Converter
public final class MongoDbFallbackConverter {

    // Jackson's ObjectMapper is thread-safe, so no need to create a pool nor synchronize access to it
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MongoDbFallbackConverter() {
    }

    @FallbackConverter
    public static Object convertTo(Class<?> type, Exchange exchange, Object value, TypeConverterRegistry registry)
        throws InvalidPayloadException {

        // if the source is a string and we attempt to convert to one of the known mongodb json classes then try that
        if (String.class == value.getClass()) {

            if (type == DBObject.class) {
                Object out = JSON.parse(value.toString());
                if (out instanceof DBObject) {
                    return out;
                } else {
                    throw new InvalidPayloadException(exchange, type);
                }
            } else if (type == BasicDBList.class) {
                Object out = JSON.parse(value.toString());
                if (out instanceof BasicDBList) {
                    return out;
                } else {
                    throw new InvalidPayloadException(exchange, type);
                }
            } else if (type == BasicDBObject.class) {
                Object out = JSON.parse(value.toString());
                if (out instanceof BasicDBObject) {
                    return out;
                } else {
                    throw new InvalidPayloadException(exchange, type);
                }
            }
        }

        // okay then fallback and use jackson
        if (type == DBObject.class) {
            Map<?, ?> m = OBJECT_MAPPER.convertValue(value, Map.class);
            return new BasicDBObject(m);
        }

        return null;
    }

}
