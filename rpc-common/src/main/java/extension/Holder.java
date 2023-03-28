package extension;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Holder<T> {
    /**
     * 加载的实例
     */
    private volatile T value;
}
