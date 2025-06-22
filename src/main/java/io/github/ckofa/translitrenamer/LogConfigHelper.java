package io.github.ckofa.translitrenamer;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Utility class for configuring system logging properties.
 * Automatically determines the path to the directory where the JAR file is located.
 */
public final class LogConfigHelper {

    private static final String PROJECT_NAME = "translitrenamer";
    private static final String LOG_PATH_PROPERTY = PROJECT_NAME + ".log.filePath";

    private LogConfigHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Initializes the LOG_PATH_PROPERTY system property with the path where the JAR file is located.
     * If the path cannot be determined, sets the default value to “logs”.
     * If the property is already set (e.g. via -D), it is not overwritten.
     *
     * @param anchorClass The class against which the path to the JAR is defined
     */
    public static void initLogFilePath(Class<?> anchorClass) {
        if (System.getProperty(LOG_PATH_PROPERTY) != null) {
            return; // already established externally
        }

        try {
            URI uri = anchorClass.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI();

            Path jarDir = Paths.get(uri).getParent();
            System.setProperty(LOG_PATH_PROPERTY, jarDir.toString());

        } catch (Exception e) {
            System.err.println("WARNING: Failed to initialize log file path. Falling back to default.");
            e.printStackTrace();
            System.setProperty(LOG_PATH_PROPERTY, "logs");
        }
    }

    /**
     * Returns the path to the log directory, setting it the first time it is called.
     * Throws an exception if the path is left unset after initialization.
     *
     * @return absolute path to the log directory
     */
    public static String getLogFilePath() {
        if (System.getProperty(LOG_PATH_PROPERTY) == null) {
            initLogFilePath(LogConfigHelper.class);
        }
        return Objects.requireNonNull(System.getProperty(LOG_PATH_PROPERTY),
                "System property '" + LOG_PATH_PROPERTY + "' is not set");
    }
}
