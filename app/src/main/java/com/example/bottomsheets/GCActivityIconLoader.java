package com.example.bottomsheets;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GCActivityIconLoader {

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static int KEEP_ALIVE_TIME = 1;
    private static TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    List<Future> runningTaskList = new ArrayList<>();
    BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    Handler uiHandler = new Handler(Looper.getMainLooper());

    ExecutorService executorService = new ThreadPoolExecutor(NUMBER_OF_CORES,
            NUMBER_OF_CORES * 2,
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            taskQueue,
            new BackgroundThreadFactory());

    // Add a callable to the queue, which will be executed by the next available thread in the pool
    public void addCallable(Callable callable) {
        Future future = executorService.submit(callable);
        runningTaskList.add(future);
    }

    /**
     * Remove all tasks in the queue and stop all running threads
     * Notify UI thread about the cancellation
     */
    public void cancelAllTasks() {
        synchronized (this) {
            taskQueue.clear();
            for (Future task : runningTaskList) {
                if (!task.isDone()) {
                    task.cancel(true);
                }
            }
            runningTaskList.clear();
        }
    }

    private static class BackgroundThreadFactory implements ThreadFactory {
        private static int sTag = 1;

        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("CustomThread" + sTag);

            // A exception handler is created to log the exception from threads
            thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    Log.e("tag", thread.getName() + " encountered an error: " + ex.getMessage());
                }
            });
            return thread;
        }
    }
}
