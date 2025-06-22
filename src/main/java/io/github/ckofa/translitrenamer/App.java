package io.github.ckofa.translitrenamer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for renaming files with cyrillic characters in the name.
 * <p>
 * Starts the renaming process using the specified implementations of {@link FileFinder} and {@link FileRenamer}.
 * </p>
 *
 */
public class App {

    static {
        LogConfigHelper.initLogFilePath(App.class);
    }

    private static final Logger log = LoggerFactory.getLogger(App.class);

    /**
     * Application Entry Point.
     * <p>
     * Expects a single command line argument - the directory path.
     * </p>
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar MyApp.jar <folder-path>");
            System.out.println("Note: if the folder path contains spaces, enclose it in double quotes.");
            System.out.println("Example (Windows): java -jar MyApp.jar \"C:\\Users\\Ivan\\My Files\\ToRename\"");
            System.out.println("Example (Linux):   java -jar MyApp.jar \"/home/ivan/My Files/ToRename\"");
            return;
        }

        String folderPath = args[0];
        log.info("Program start, folder for processing files: {}", folderPath);

        FileFinder fileFinder = new CyrillicFileFinder();
        FileRenamer fileRenamer = new TransliterationFileRenamer(fileFinder);

        fileRenamer.renameFiles(folderPath);
    }
}
