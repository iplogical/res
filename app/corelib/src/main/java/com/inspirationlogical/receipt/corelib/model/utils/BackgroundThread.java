package com.inspirationlogical.receipt.corelib.model.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by TheDagi on 2017. 07. 17..
 */
public class BackgroundThread {
    private final static Logger logger = LoggerFactory.getLogger(BackgroundThread.class);

    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    public static void shutdown() {
        try {
            executor.shutdown();
            executor.awaitTermination(3, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            logger.error("termination interrupted", e);
        }
        finally {
            if (!executor.isTerminated()) {
                logger.error("killing non-finished tasks");
            }
            executor.shutdownNow();
        }
    }}
