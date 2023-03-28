package com.rpc.serialize;

import extension.SPI;

@SPI
public interface Serializer {
    <T> byte[] serialize(T object);

    <T> T deserialize(byte[] data, Class<T> tClass);
}
