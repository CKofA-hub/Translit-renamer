package io.github.ckofa.translitrenamer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * An implementation of the {@link FileRenamer} interface designed to rename files with cyrillic characters in the name.
 * <p>
 * Uses transliteration to convert file names to Latin.
 * </p>
 */
public class TransliterationFileRenamer implements FileRenamer{

    private static final Logger log = LoggerFactory.getLogger(TransliterationFileRenamer.class);
    private final FileFinder fileFinder;

    /**
     * Creates an instance of {@code TransliterationFileRenamer} with the specified {@code FileFinder}.
     *
     * @param fileFinder interface implementation {@code FileFinder}
     */
    public TransliterationFileRenamer(FileFinder fileFinder) {
        this.fileFinder = fileFinder;
    }

    @Override
    public void renameFiles(String folderPath) {
        List<File> files;
        try {
            files = fileFinder.findFilesOrThrow(folderPath);
        } catch (RuntimeException e) {
            log.error("Skipping folder due to error: {}", e.getMessage());
            return;
        }

        renameFiles(files);
    }

    @Override
    public void renameFiles(List<File> files) {
        for (File file : files) {
            String originalName = null;
            try {
                originalName = file.getName();
                String newName = TransliteratorUtils.transliterate(originalName);

                Path source = file.toPath();
                Path target = source.resolveSibling(newName);

                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                log.info("Renamed: {} -> {}", originalName, newName);
            } catch (IOException e) {
                log.error("Failed to rename file '{}' due to an I/O error, skipping.", originalName != null ? originalName : file.toString(), e);
            } catch (Exception e) {
                log.error("An unexpected error occurred while processing file '{}', skipping.", file.toString(), e);
            }
        }
    }

}
