package io.github.ckofa.translitrenamer;

import java.io.File;
import java.util.List;

/**
 * Interface to search for files in a specified directory.
 * <p>
 * Provides methods to search for files matching certain criteria.
 * </p>
 */
public interface FileFinder {

    /**
     * Finds files in a specified directory that match certain criteria.
     *
     * @param folderPath directory path for searching files
     * @return list of found files
     */
    List<File> findFiles(String folderPath);

    /**
     * Finds files in a specified directory that match certain criteria.
     * <p>
     * Throws an exception if the directory does not exist or is not a directory.
     * </p>
     *
     * @param folderPath directory path for searching files
     * @return list of found files
     * @throws IllegalArgumentException if the path is invalid
     * @throws IllegalStateException if could not read the contents of the folder.
     */
    List<File> findFilesOrThrow(String folderPath);
}
