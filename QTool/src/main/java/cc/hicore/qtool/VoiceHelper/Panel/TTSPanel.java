package cc.hicore.qtool.VoiceHelper.Panel;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import cc.hicore.Utils.FileUtils;
import cc.hicore.Utils.MediaUtils;
import cc.hicore.Utils.TTSHelper;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQMessage.QQMsgSender;
import cc.hicore.qtool.R;

public class TTSPanel {
    public static View initView(Context context){
        LinearLayout ttsLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.voice_tts,null);
        TextView inputText = ttsLayout.findViewById(R.id.Convert_TTS_Input);

        Button btnPreListen = ttsLayout.findViewById(R.id.Convert_TTS_Listen);
        Button btnSend = ttsLayout.findViewById(R.id.Convert_TTS_Send);
        Button btnSendRaw = ttsLayout.findViewById(R.id.Convert_TTS_Send_RAW);
        TextView showInfo = ttsLayout.findViewById(R.id.Convert_To_TTS_Engine);
        TTSHelper.InitTTS(context, () -> Utils.PostToMain(()->showInfo.setText("当前TTS引擎:"+TTSHelper.getTTSPackage())));

        btnPreListen.setOnClickListener(v->{
            String text = inputText.getText().toString();
            TTSHelper.startConvert(text, cacheDir -> PlayVoice(cacheDir));
        });
        btnSend.setOnClickListener((v->{
            String text = inputText.getText().toString();
            TTSHelper.startConvert(text, cacheDir -> {
                MediaUtils.MP3ToPCM(cacheDir,cacheDir+".pcm");
                byte[] silkData = MediaUtils.ConvertDataToSilk(FileUtils.ReadFile(new File(cacheDir+".pcm")));
                FileUtils.WriteToFile(cacheDir+".silk",silkData);
                QQMsgSender.sendVoice(HookEnv.SessionInfo,cacheDir+".silk");
            });
        }));
        btnSendRaw.setOnClickListener(v->{
            String text = inputText.getText().toString();
            TTSHelper.startConvert(text, cacheDir -> QQMsgSender.sendVoice(HookEnv.SessionInfo,cacheDir));
        });

        return ttsLayout;
    }
    public static void PlayVoice(String Path) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(Path);
            mediaPlayer.setLooping(false);;
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
