package cc.hicore.qtool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cc.hicore.qtool.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //使窗口全透明
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );

        findViewById(R.id.ClickToGithub)
                .setOnClickListener(v->{
                    Uri u = Uri.parse("https://github.com/Hicores/QTool");
                    Intent in = new Intent(Intent.ACTION_VIEW,u);
                    startActivity(in);
                });
        findViewById(R.id.OpenOpenSource)
                .setOnClickListener(v-> startActivity(new Intent(this,OpenSource.class)));

        ((TextView)findViewById(R.id.Show_Version)).setText("模块版本:"+BuildConfig.VERSION_NAME);


    }
}