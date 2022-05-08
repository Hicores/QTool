package cc.hicore.qtool.DebugHelper;

import java.util.concurrent.atomic.AtomicBoolean;

public class StringPool_XpLog {
    private static long Size = 0;
    private static StringPool_XpLog FirstPos;
    private static StringPool_XpLog CurrentPos;



    public StringPool_XpLog(String Text, StringPool_XpLog Before){
        Line = Text;
        BeforePool = Before;
        Size += Text.length();

        while (Size >= 128 * 1024){
            StringPool_XpLog poolFirst = FirstPos;
            poolFirst = poolFirst.AfterPool;
            Size -= FirstPos.Line.length();
            FirstPos = poolFirst;
            FirstPos.BeforePool = null;
        }
    }
    private StringPool_XpLog BeforePool;
    private StringPool_XpLog AfterPool;
    private String Line;
    static AtomicBoolean Lock = new AtomicBoolean();


    private static void Init(){
        FirstPos = new StringPool_XpLog("StringPoolStart",null);
        CurrentPos = FirstPos;
    }

    public static void Add(String Line){
        if (Line == null)return;
        if (Lock.get())return;
        if (FirstPos == null)Init();
        StringPool_XpLog NewPool = new StringPool_XpLog(Line,CurrentPos);
        CurrentPos.AfterPool = NewPool;

        CurrentPos = NewPool;
    }
    public static String getAll(){
        try{
            Lock.getAndSet(true);
            StringPool_XpLog CheckPoolaa = FirstPos;
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
