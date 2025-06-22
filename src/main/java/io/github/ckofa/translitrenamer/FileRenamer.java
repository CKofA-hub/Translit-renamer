package io.github.ckofa.translitrenamer;

import java.io.File;
import java.util.List;

/**
 * Interface for renaming files in a specified directory.
 * <p>
 * Provides methods for renaming files that meet certain criteria.
 * </p>
 */
public interface FileRenamer {

    /**
     * Renames files found in the specified folder.
     * The built-in FileFinder is used.
     *
     * @param folderPath path to the directory where files will be searched
     */
    void renameFiles(String folderPath);

    /**
     * Rename a specific list of files.
     * Can be used with any file source.
     *
     * @param files file list for renaming
     */
    void renameFiles(List<File> files);
}
