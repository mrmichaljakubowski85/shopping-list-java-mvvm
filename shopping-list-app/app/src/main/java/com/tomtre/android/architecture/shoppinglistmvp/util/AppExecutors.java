package com.tomtre.android.architecture.shoppinglistmvp.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.concurrent.Executor;

import static com.tomtre.android.architecture.shoppinglistmvp.util.CommonUtils.isNull;

public class AppExecutors {

    private static final Object LOCK = new Object();
    private static AppExecutors INSTANCE;
    private final Executor diskIOExecutor;
    private final Executor mainThreadExecutor;

    @VisibleForTesting
    public AppExecutors(Executor diskIOExecutor, Executor mainThreadExecutor) {
        this.diskIOExecutor = diskIOExecutor;
        this.mainThreadExecutor = mainThreadExecutor;
    }

    public static AppExecutors getInstance(Executor diskIOExecutor, Executor mainThreadExecutor) {
        if (isNull(INSTANCE)) {
            synchronized (LOCK) {
                if (isNull(INSTANCE)) {
                    INSTANCE = new AppExecutors(diskIOExecutor, mainThreadExecutor);
                }
            }
        }
        return INSTANCE;
    }

    public Executor getDiskIOExecutor() {
        return diskIOExecutor;
    }

    public Executor getMainThreadExecutor() {
        return mainThreadExecutor;
    }

    public static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    @VisibleForTesting
    public static void clearInstance() {
        INSTANCE = null;
    }

}
