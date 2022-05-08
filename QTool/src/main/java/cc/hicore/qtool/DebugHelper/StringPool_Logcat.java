package cc.hicore.qtool.DebugHelper;

import java.util.concurrent.atomic.AtomicBoolean;

public class StringPool_Logcat {
    private static long Size = 0;
    private static StringPool_Logcat FirstPos;
    private static StringPool_Logcat CurrentPos;



    public StringPool_Logcat(String Text, StringPool_Logcat Before){
        Line = Text;
        BeforePool = Before;
        Size += Text.length();

        while (Size >= 256 * 1024){
            StringPool_Logcat poolFirst = FirstPos;
            poolFirst = poolFirst.AfterPool;
            Size -= FirstPos.Line.length();
            FirstPos = poolFirst;
            FirstPos.BeforePool = null;
        }
    }
    private StringPool_Logcat BeforePool;
    private StringPool_Logcat AfterPool;
    private String Line;
    static AtomicBoolean Lock = new AtomicBoolean();


    private static void Init(){
        FirstPos = new StringPool_Logcat("StringPoolStart",null);
        CurrentPos = FirstPos;
    }

    public static void Add(String Line){
        if (Line == null)return;
        if (Lock.get())return;
        if (FirstPos == null)Init();
        StringPool_Logcat NewPool = new StringPool_Logcat(Line,CurrentPos);
        CurrentPos.AfterPool = NewPool;

        CurrentPos = NewPool;
    }
    public static String getAll(){
        try{
            Lock.getAndSet(true);
            StringPool_Logcat CheckPoolaa = FirstPos;
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
