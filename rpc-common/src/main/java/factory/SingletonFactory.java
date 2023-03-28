package factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例对象工厂
 */
public class SingletonFactory {
    private static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();

    private SingletonFactory() {
    }

    public static <T> T getInstance(Class<T> tClass) {
        if (tClass == null) {
            throw new IllegalArgumentException("单例对象不存在");
        }
        String key = tClass.toString();
        if (OBJECT_MAP.containsKey(key)) {
            return tClass.cast(OBJECT_MAP.get(key));
        }
        return tClass.cast(OBJECT_MAP.computeIfAbsent(key, k -> {
            try {
                return tClass.getDeclaredConstructor().newInstance();
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

        }));
    }
    public static <T> T getInstance(Class<T> cls, Object ... args) {
        if (cls == null) {
            throw new IllegalArgumentException();
        }
        String key = cls.toString();
        if (OBJECT_MAP.containsKey(key)) {
            return cls.cast(OBJECT_MAP.get(key));
        } else {
            return cls.cast(OBJECT_MAP.computeIfAbsent(key, k -> {
                try {
                    return cls.getConstructors()[0].newInstance(args);
                } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }));
        }
    }

}
