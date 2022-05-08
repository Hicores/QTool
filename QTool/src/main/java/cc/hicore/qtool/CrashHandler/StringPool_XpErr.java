package cc.hicore.qtool.CrashHandler;

import java.util.concurrent.atomic.AtomicBoolean;

public class StringPool_XpErr {
    private static long Size = 0;
    private static StringPool_XpErr FirstPos;
    private static StringPool_XpErr CurrentPos;



    public StringPool_XpErr(String Text, StringPool_XpErr Before){
        Line = Text;
        BeforePool = Before;
        Size += Text.length();

        while (Size >= 128 * 1024){
            StringPool_XpErr poolFirst = FirstPos;
            poolFirst = poolFirst.AfterPool;
            Size -= FirstPos.Line.length();
            FirstPos = poolFirst;
            FirstPos.BeforePool = null;
        }
    }
    private StringPool_XpErr BeforePool;
    private StringPool_XpErr AfterPool;
    private String Line;
    static AtomicBoolean Lock = new AtomicBoolean();


    private static void Init(){
        FirstPos = new StringPool_XpErr("StringPoolStart",null);
        CurrentPos = FirstPos;
    }

    public static void Add(String Line){
        if (Line == null)return;
        if (Lock.get())return;
        if (FirstPos == null)Init();
        StringPool_XpErr NewPool = new StringPool_XpErr(Line,CurrentPos);
        CurrentPos.AfterPool = NewPool;

        CurrentPos = NewPool;
    }
    public static String getAll(){
        try{
            Lock.getAndSet(true);
            StringPool_XpErr CheckPoolaa = FirstPos;
            StringBuilder builder = new StringBuilder();

            while (CheckPoolaa!= null){
                builder.append(CheckPoolaa.Line).append("\n");

                CheckPoolaa = CheckPoolaa.AfterPool;
            }
            return builder.toString();
        }catch (Exception e){
            return "Error,Can't get StringPool Data";
        }finally {
            Lock.getAndSet(false);
        }
    }

}
