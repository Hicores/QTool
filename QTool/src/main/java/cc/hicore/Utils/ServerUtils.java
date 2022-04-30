package cc.hicore.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerUtils {
    public static String getNewestCIBuildVer(){
        try{
            String result = HttpUtils.getContent("https://github.com/Hicores/QTool/actions");
            Pattern pattern = Pattern.compile("Run [0-9]{3} of Build CI");
            Matcher matcher = pattern.matcher(result);
            while (matcher.find()){
                int start = matcher.start();
                int end = matcher.end();
                String match2 = result.substring(start,end);
                Pattern pattern2 = Pattern.compile("[0-9]{3}");
                matcher = pattern2.matcher(match2);

                if (matcher.find()){
                    start = matcher.start();
                    end = matcher.end();
                    return match2.substring(start,end);
                }
            }
        }catch (Exception e){

        }
        return "null";


    }
}
