package cc.hicore.Utils;

import java.util.ArrayList;

public class StringUtils {
    public static String[] GetStringMiddleMix(String source,String before,String last) {
        if(source==null)return new String[0];
        int index = -1;
        int index2 = -1;
        ArrayList<String> result = new ArrayList<>();
        try{
            index = source.indexOf(before);
            while (index>-1)
            {
                index2 = source.indexOf(last,index+before.length());
                if(index2==-1)break;
                String searchEnd = source.substring(index+before.length(),index2);
                result.add(searchEnd);


                index = source.indexOf(before,index2+last.length());
            }
        }finally {
            return result.toArray(new String[0]);
        }
    }
    public static String GetOneStringMiddleMix(String source,String before,String last) {
        if(source==null)return null;
        int index = -1;
        int index2 = -1;
        ArrayList<String> result = new ArrayList<>();
        try{
            index = source.indexOf(before);
            if (index>-1)
            {
                index2 = source.indexOf(last,index+before.length());
                if(index2>-1){
                    String searchEnd = source.substring(index+before.length(),index2);
                    return searchEnd;
                }
            }
        }finally {

        }
        return null;
    }
    public static String Repeat(String re,int count){
        StringBuilder builder = new StringBuilder();
        for (int i=0;i< count;i++)builder.append(re);
        return builder.toString();
    }
    public static int Count(String raw,String target){
        int find = raw.indexOf(target);
        int Count = 0;
        while (find != -1){
            Count++;
            find = raw.indexOf(target,find + target.length());
        }
        return Count;
    }
}
