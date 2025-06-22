package io.github.ckofa.translitrenamer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An implementation of the {@link FileFinder} interface designed to find files with cyrillic characters in the name.
 */
public class CyrillicFileFinder implements FileFinder {

    private static final Logger log = LoggerFactory.getLogger(CyrillicFileFinder.class);

    public CyrillicFileFinder() {
    }

    @Override
    public List<File> findFiles(String folderPath) {
        try {
            return findFilesOrThrow(folderPath);
        } catch (IllegalArgumentException e) {
            log.error("Invalid folder path: {}", e.getMessage());
        } catch (IllegalStateException e) {
            log.error("Could not read folder contents: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public List<File> findFilesOrThrow(String folderPath) {
        File folder = new File(folderPath);

        if(!folder.isDirectory()) {
            throw new IllegalArgumentException("Path is not a valid directory: " + folderPath);
        }

        File[] files = folder.listFiles();
        if (files == null) {
            throw new IllegalStateException("Unable to list files in folder: " + folderPath);
        }

        List<File> result = new ArrayList<>();
        for (File file : files) {
            if (file.isFile() && containsCyrillic(file.getName())) {
                result.add(file);
            }
        }

        return result;
    }

    /**
     * Checks if the file name contains Cyrillic characters.
     *
     * @param fileName file name
     * @return {@code true} if the name contains cyrillic characters; {@code false} otherwise
     */
    private boolean containsCyrillic(String fileName) {
        return fileName.chars()
                .mapToObj(Character.UnicodeBlock::of)
                .anyMatch(Character.UnicodeBlock.CYRILLIC::equals);
    }
}
