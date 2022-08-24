package cc.hicore.qtool;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cc.hicore.Utils.Utils;

public class OpenSource extends AppCompatActivity {
    private LinearLayout mLists;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensource);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
        mLists = findViewById(R.id.OpenSourceList);

        createItem("beanshell@beanshell(Apache2.0)", "https://github.com/beanshell/beanshell");
        createItem("EzXHelper@KyuubiRan(Apache2.0)", "https://github.com/KyuubiRan/EzXHelper");
        createItem("XPopup@li-xiaojun(Apache2.0)", "https://github.com/li-xiaojun/XPopup");
        createItem("EasyAdapter@li-xiaojun", "https://github.com/li-xiaojun/EasyAdapter");
        createItem("glide@bumptech(Apache2.0)", "https://github.com/bumptech/glide");
        createItem("appcenter@microsoft(MIT)", "https://github.com/microsoft/appcenter-sdk-android");
        createItem("BiliRoaming@yujincheng08(GPL3.0)", "https://github.com/yujincheng08/BiliRoaming");
        createItem("DexBuilder@LSPosed(LGPL3.0)", "https://github.com/LSPosed/DexBuilder");
        createItem("DexKit@LuckyPray(LGPL3.0)", "https://github.com/LSPosed/DexBuilder");
        createItem("dx@google", "https://androidsdkmanager.azurewebsites.net/Buildtools");
        createItem("XposedBridgeApi@rovo89", "https://api.xposed.info/reference/packages.html");
    }

    private void createItem(String title, String url) {

        View vItem = getLayoutInflater().inflate(R.layout.open_source_item, null);

        TextView titleView = vItem.findViewById(R.id.open_title);
        titleView.setText(title);

        TextView urlView = vItem.findViewById(R.id.open_url);
        urlView.setText(url);

        vItem.setOnClickListener(v -> {
            Uri u = Uri.parse(url);
            Intent in = new Intent(Intent.ACTION_VIEW, u);
            startActivity(in);
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, Utils.dip2px(this, 30), 0, 0);
        params.height = Utils.dip2px(this, 90);
        params.width = Utils.dip2px(this, 300);
        mLists.addView(vItem, params);

    }
}
