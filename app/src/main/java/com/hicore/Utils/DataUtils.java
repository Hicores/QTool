package com.hicore.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataUtils {
    public static byte[] readAllBytes(InputStream inp) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = inp.read(buffer))!=-1)out.write(buffer,0,read);
        return out.toByteArray();
    }
}
