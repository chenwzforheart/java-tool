package common;

/**
 * @author csh9016
 * @date 2020/6/9
 */
public class ShardParamContext {

    private static final ThreadLocal<Object> PARAM_HOLDER = new ThreadLocal<>();

    private ShardParamContext() {
    }

    public static void remove() {
        PARAM_HOLDER.remove();
    }

    public static Object get() {
        return PARAM_HOLDER.get();
    }

    public static void set(Object param) {
        PARAM_HOLDER.set(param);
    }
}
