package com.hicore.qtool.EmoHelper.CloudSync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncCore {
    private static ExecutorService syncThread = Executors.newFixedThreadPool(16);
}
