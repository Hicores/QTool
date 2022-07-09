package cc.hicore.Utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import cc.hicore.qtool.HookEnv;

public class TTSHelper {
    private static HashMap<String,TTSHelperConvertResult> callbackCache = new HashMap<>();
    private static TextToSpeech tts = null;
    public static void InitTTS(Context context,TTSInitDoneCallback callback){
        AtomicReference<TextToSpeech> reference = new AtomicReference<>();
        reference.set(new TextToSpeech(context, status -> {
            if (status != TextToSpeech.SUCCESS){
                Utils.ShowToastL("TTS引擎初始化失败!");
            }else {
                tts = reference.get();
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {

                    }

                    @Override
                    public void onDone(String utteranceId) {
                        TTSHelperConvertResult result = callbackCache.get(utteranceId);
                        if (result != null){
                            callbackCache.remove(utteranceId);
                            result.onResult(utteranceId);
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Utils.ShowToastL("TTS发生错误");
                    }
                });
                callback.onDone();
            }
        }));
    }
    public static String getTTSPackage(){
        if (tts != null)return tts.getDefaultEngine();
        return "null";
    }
    public static void startConvert(String text,TTSHelperConvertResult callback){
        if (tts != null){
            String cacheDir = HookEnv.ExtraDataPath + "Cache/"+Math.random();
            HashMap<String, String> myHashRender = new HashMap<>();
            myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,cacheDir);
            tts.synthesizeToFile(text,myHashRender,cacheDir);
            callbackCache.put(cacheDir,callback);
        }
    }
    public interface TTSHelperConvertResult{
        void onResult(String cacheDir);
    }
    public interface TTSInitDoneCallback{
        void onDone();
    }
}
