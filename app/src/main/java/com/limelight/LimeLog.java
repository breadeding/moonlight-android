package com.limelight;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class LimeLog {
    private static final Logger LOGGER = Logger.getLogger(LimeLog.class.getName());

    // 开关：BuildConfig.DEBUG 会在打正式包(Release)时自动变为 false
    private static final boolean ENABLE_LOG = BuildConfig.DEBUG;

    public static void info(String msg) {
        if (ENABLE_LOG) {
            LOGGER.info(msg);
        }
    }
    
    public static void warning(String msg) {
        LOGGER.warning(msg);
    }
    
    public static void severe(String msg) {
        LOGGER.severe(msg);
    }
    
    public static void setFileHandler(String fileName) throws IOException {
        LOGGER.addHandler(new FileHandler(fileName));
    }
}
