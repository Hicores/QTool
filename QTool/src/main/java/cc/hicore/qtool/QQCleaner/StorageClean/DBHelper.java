package cc.hicore.qtool.QQCleaner.StorageClean;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;
import de.robv.android.xposed.XposedBridge;

public class DBHelper {
    private final SQLiteDatabase db;

    public DBHelper(String path) {
        db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public static String decodeData(String str) {
        try {
            MMethod.CallStaticMethod(MClass.loadClass("mqq.database.SecurityUtile"), "setKey", void.class, HookEnv.AppContext);
            return MMethod.CallStaticMethod(MClass.loadClass("mqq.database.SecurityUtile"), "decode", String.class, str);
        } catch (Exception e) {
            XposedBridge.log(e);
            return "";
        }
    }

    @SuppressLint("Range")
    public List<String> getTables() {
        Cursor cursor = db.rawQuery("select * from sqlite_master where type = 'table'", null);
        ArrayList<String> tables = new ArrayList<>();
        while (cursor.moveToNext()) {
            String tableName = cursor.getString(cursor.getColumnIndex("name"));
            tables.add(tableName);
        }
        cursor.close();
        return tables;
    }

    @SuppressLint("Range")
    public String getOneData(String TableName, String ColumnName) {
        try {
            Cursor cursor = db.rawQuery("select " + ColumnName + " from " + TableName, null);
            if (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(ColumnName));
                cursor.close();
                return data;
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public int getCount(String TableName) {
        try {
            Cursor cursor = db.rawQuery("select * from " + TableName, null);
            int count = cursor.getCount();
            cursor.close();
            return count;
        } catch (Exception e) {
            return 0;
        }

    }

    public void Drop(String tableName) {
        try {
            db.execSQL("DROP TABLE " + tableName);

        } catch (Exception e) {

        }
    }
}
