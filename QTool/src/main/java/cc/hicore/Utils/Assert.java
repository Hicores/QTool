package cc.hicore.Utils;

public class Assert {
    public static void notNull(Object obj,String message){
        if (obj == null)throw new NullPointerException(message);
    }
}
