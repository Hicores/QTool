# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class bsh.** { *; }
-keep class androidx.lifecycle.**{ *; }
-keep class com.lxj.xpopup.widget.**{*;}
-keep class cc.hicore.qtool.XposedInit.**{*;}
-keep class * implements cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem { }
-keep class * extends cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem { }
-keep class * extends android.app.Activity { }
-keep class cc.hicore.qtool.JavaPlugin.Controller.PluginInfo$GroupInfo{ *;}
-keep class cc.hicore.qtool.JavaPlugin.Controller.PluginInfo$RequestInfo{ *;}
-keep class cc.hicore.qtool.JavaPlugin.Controller.PluginInfo$MessageData{ *;}
-keep class cc.hicore.qtool.JavaPlugin.Controller.PluginInfo$GroupBanInfo{ *;}
-keep class cc.hicore.qtool.JavaPlugin.Controller.PluginInfo$GroupMemberInfo{ *;}
-keep class cc.hicore.qtool.JavaPlugin.Controller.PluginMethod{*;}
-keep class cc.hicore.qtool.QQTools.QQSelectHelper$RoundImageView{*;}
-keep class com.tencent.** {*;}
-keep class me.iacn.biliroaming.**{*;}
-keep @cc.hicore.HookItemLoader.Annotations.XPItem class * { *;}
-keep class cc.hicore.HookItemLoader.**{*;}
-keepattributes SourceFile,LineNumberTable

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keep class * implements java.io.Serializable { *; }

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

-flattenpackagehierarchy ''