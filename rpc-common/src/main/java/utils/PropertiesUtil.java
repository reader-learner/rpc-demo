package utils;

import enums.ConfigEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class PropertiesUtil {
    private static final Map<String, Properties> propertiesMap = new HashMap<>();

    public static void main(String[] args) {
        Properties properties = readProperties(ConfigEnum.RPC_CONFIG_PATH.getValue());
        System.out.println(properties);
    }

    /**
     * 从指定的配置文件中读取属性。
     *
     * @param fileName 配置文件名
     * @return 属性列表
     */
    public static Properties readProperties(String fileName) {
        if (propertiesMap.containsKey(fileName)) {
            return propertiesMap.get(fileName);
        }
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String rpcConfigPath = "";
        if (url != null) {
            rpcConfigPath = url.getPath() + fileName;
            log.debug("read properties-path: {}", rpcConfigPath);
        }
        Properties properties = null;
        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream(URLDecoder.decode(rpcConfigPath, "UTF-8")), StandardCharsets.UTF_8)) {

            properties = new Properties();
            properties.load(reader);
        } catch (IOException e) {
            log.error("未配置rpc.properties文件");
        }
        if (properties != null) {
            propertiesMap.put(fileName, properties);
        }
        return properties;
    }
}
