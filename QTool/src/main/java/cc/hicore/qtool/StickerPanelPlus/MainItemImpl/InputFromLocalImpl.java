package cc.hicore.qtool.StickerPanelPlus.MainItemImpl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.util.List;

import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.FileUtils;
import cc.hicore.Utils.NameUtils;
import cc.hicore.Utils.ThreadUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.R;
import cc.hicore.qtool.StickerPanelPlus.ICreator;
import cc.hicore.qtool.StickerPanelPlus.LocalDataHelper;
import cc.hicore.qtool.StickerPanelPlus.MainPanelAdapter;

public class InputFromLocalImpl implements MainPanelAdapter.IMainPanelItem{
    @Override
    public View getView(ViewGroup parent) {
        ViewGroup vgInput = (ViewGroup) View.inflate(parent.getContext(), R.layout.sticker_panel_impl_input_from_local, null);
        EditText edPath = vgInput.findViewById(R.id.input_path);
        Button btnPath = vgInput.findViewById(R.id.btn_confirm_input);
        btnPath.setOnClickListener(v->{
            String path = edPath.getText().toString();
            File[] f = new File(path).listFiles();
            if (f == null){
                new AlertDialog.Builder(parent.getContext(),3)
                        .setTitle("错误")
                        .setMessage("路径无效")
                        .setPositiveButton("确定", null)
                        .show();
            }else {
                EditText ed = new EditText(parent.getContext());
                new AlertDialog.Builder(parent.getContext(),3)
                        .setTitle("输入显示的名字")
                        .setView(ed)
                        .setNegativeButton("确定导入", (dialog, which) -> {
                            inputWorker(parent.getContext(), path,ed.getText().toString());
                        }).show();
            }

        });
        return vgInput;
    }
    private static void inputWorker(Context context,String path,String name){
        if (TextUtils.isEmpty(name)){
            new AlertDialog.Builder(context,3)
                    .setTitle("错误")
                    .setMessage("名字不能为空")
                    .setPositiveButton("确定", null)
                    .show();
            return;
        }
        ProgressDialog progressDialog = new ProgressDialog(context,3);
        progressDialog.setTitle("正在导入...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ThreadUtils.PostCommonTask(()->{
            File[] f = new File(path).listFiles();
            if (f == null){
                progressDialog.dismiss();
                new AlertDialog.Builder(context,3)
                        .setTitle("错误")
                        .setMessage("路径无效")
                        .setPositiveButton("确定", null)
                        .show();
                return;
            }
            String ID = NameUtils.getRandomString(8);
            LocalDataHelper.LocalPath newPath = new LocalDataHelper.LocalPath();
            newPath.storePath = ID;
            newPath.coverName = "";
            newPath.Name = name;
            LocalDataHelper.addPath(newPath);

            int size = f.length;
            int finish = 0;
            int available = 0;
            for (File file : f) {
                if (file.isFile()){
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.getAbsolutePath(),options);
                    if (options.outWidth > 0 && options.outHeight > 0){
                        LocalDataHelper.LocalPicItems localItem = new LocalDataHelper.LocalPicItems();
                        localItem.url = "";
                        localItem.type = 1;
                        localItem.MD5 = DataUtils.getFileMD5(file);
                        localItem.addTime = System.currentTimeMillis();
                        localItem.fileName = localItem.MD5;
                        localItem.thumbUrl = "";


                        FileUtils.copy(file.getAbsolutePath(),LocalDataHelper.getLocalItemPath(newPath,localItem));

                        LocalDataHelper.addPicItem(ID,localItem);
                        available++;
                    }
                }
                finish++;
                int finalFinish = finish;
                int finalAvailable = available;
                Utils.PostToMain(()->{
                    progressDialog.setMessage("已完成"+ finalFinish +"/"+size+"个文件,有效文件"+ finalAvailable +"个");
                });
            }

            List<LocalDataHelper.LocalPicItems> list =  LocalDataHelper.getPicItems(ID);
            if (list.size() > 0){
                LocalDataHelper.setPathCover(newPath,list.get(0));
            }
            Utils.PostToMain(()->{
                progressDialog.dismiss();
                new AlertDialog.Builder(context,3)
                        .setTitle("导入完成")
                        .setMessage("导入完成")
                        .setPositiveButton("确定", null)
                        .show();

                ICreator.dismissAll();
            });



        });
    }
    private static boolean isImageFile(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        if (options.outWidth == -1) {
            return false;
        }
        return true;
    }

    @Override
    public void onViewDestroy(ViewGroup parent) {

    }

    @Override
    public long getID() {
        return 8888;
    }

    @Override
    public void notifyViewUpdate0() {

    }
}
