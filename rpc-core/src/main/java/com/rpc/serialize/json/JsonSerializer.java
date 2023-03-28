package com.rpc.serialize.json;

import com.alibaba.fastjson.JSON;
import com.rpc.serialize.Serializer;
import exception.SerializeException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T object) {
        try {
            return JSON.toJSONBytes(object);
        } catch (Exception e) {
            log.error("error while serializing:", e);
            throw new SerializeException(e.getMessage());
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> tClass) {
        try {
            return JSON.parseObject(data, tClass);
        }catch (Exception e){
            log.error("error while deserializing:", e);
            throw new SerializeException(e.getMessage());
        }
    }
}
