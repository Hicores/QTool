package com.hicore.ReflectUtils;

import com.hicore.Utils.Assert;

import java.lang.reflect.Field;
import java.util.HashMap;

public class MField {
    private static HashMap<String, Field> FieldCache = new HashMap<>();
    public static void SetField(Object CheckObj,String FieldName,Object Value)throws Exception{
        SetField(CheckObj,CheckObj.getClass(),FieldName,Value.getClass(),Value);
    }
    public static void SetField(Object CheckObj,String FieldName,Class CheckClass,Object Value)throws Exception{
        SetField(CheckObj,CheckObj.getClass(),FieldName,CheckClass,Value);
    }
    public static  <T> T GetField(Object CheckObj,String FieldName)throws Exception{
        Assert.notNull(CheckObj,"obj is null when invoke GetField");
        Class clz = CheckObj.getClass();
        StringBuilder builder = new StringBuilder();
        builder.append(clz.getName()).append(":").append(FieldName);
        String SignText = builder.toString();
        if (FieldCache.containsKey(SignText)){
            Field f = FieldCache.get(SignText);
            return (T) f.get(CheckObj);
        }
        Class Check = clz;
        while (Check != null){
            for (Field f : Check.getDeclaredFields()){
                if (f.getName().equals(FieldName)){
                        f.setAccessible(true);
                        FieldCache.put(SignText,f);
                        return (T) f.get(CheckObj);
                }
            }
            Check = Check.getSuperclass();
        }
        throw new RuntimeException("Can't find field "+ FieldName+" in class "+clz.getName());
    }
    public static <T> T GetField(Object CheckObj,String FieldName,Class FieldType)throws Exception{
        return GetField(CheckObj,CheckObj.getClass(),FieldName,FieldType);
    }
    public static void SetField(Object CheckObj,Class CheckClass,String FieldName,Class FieldClass,Object Value)throws Exception{
        StringBuilder builder = new StringBuilder();
        builder.append(CheckClass.getName()).append(":").append(FieldName).append("(").append(FieldClass.getName()).append(")");
        String SignText = builder.toString();
        if (FieldCache.containsKey(SignText)){
            Field f = FieldCache.get(SignText);
            f.set(CheckObj,Value);
            return;
        }

        Class Check = CheckClass;
        while (Check != null){
            for (Field f : Check.getDeclaredFields()){
                if (f.getName().equals(FieldName)){
                    if (MClass.CheckClass(f.getType(),FieldClass)){
                        f.setAccessible(true);
                        FieldCache.put(SignText,f);
                        f.set(CheckObj,Value);
                        return;
                    }
                }
            }
            Check = Check.getSuperclass();
        }
        throw new RuntimeException("Can't find field "+ FieldName + "(" + FieldClass.getName()+") in class "+CheckClass.getName());
    }
    public static <T> T GetField(Object CheckObj,Class CheckClass,String FieldName,Class FieldClass) throws Exception{
        StringBuilder builder = new StringBuilder();
        builder.append(CheckClass.getName()).append(":").append(FieldName).append("(").append(FieldClass.getName()).append(")");
        String SignText = builder.toString();
        if (FieldCache.containsKey(SignText)){
            Field f = FieldCache.get(SignText);
            return (T) f.get(CheckObj);
        }

        Class Check = CheckClass;
        while (Check != null){
            for (Field f : Check.getDeclaredFields()){
                if (f.getName().equals(FieldName)){
                    if (MClass.CheckClass(f.getType(),FieldClass)){
                        f.setAccessible(true);
                        FieldCache.put(SignText,f);
                        return (T) f.get(CheckObj);
                    }
                }
            }
            Check = Check.getSuperclass();
        }
        throw new RuntimeException("Can't find field "+ FieldName + "(" + FieldClass.getName()+") in class "+CheckClass.getName());
    }
    public static <T> T GetFirstField(Object CheckObj,Class CheckClass,Class FieldClass) throws Exception{
        StringBuilder builder = new StringBuilder();
        builder.append(CheckClass.getName()).append(":!NoName!").append("(").append(FieldClass.getName()).append(")");
        String SignText = builder.toString();
        if (FieldCache.containsKey(SignText)){
            Field f = FieldCache.get(SignText);
            return (T) f.get(CheckObj);
        }

        Class Check = CheckClass;
        while (Check != null){
            for (Field f : Check.getDeclaredFields()){
                if (MClass.CheckClass(f.getType(),FieldClass)){
                    f.setAccessible(true);
                    FieldCache.put(SignText,f);
                    return (T) f.get(CheckObj);
                }
            }
            Check = Check.getSuperclass();
        }
        throw new RuntimeException("Can't find field " + "(" + FieldClass.getName()+") in class "+CheckClass.getName());
    }
    public static <T> T GetFirstField(Object CheckObj,Class FieldClass) throws Exception{
        return GetFirstField(CheckObj,CheckObj.getClass(),FieldClass);
    }
}
