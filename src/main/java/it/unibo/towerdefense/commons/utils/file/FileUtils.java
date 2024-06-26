package it.unibo.towerdefense.commons.utils.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.Objects;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import it.unibo.towerdefense.commons.Constants;

/**
 * Class for file utilities.
 */
public final class FileUtils {

    /**
     * Creates the folder for the game.
     * 
     * @throws IOException if the folder cannot be created
     */
    public static void createGameFolder() throws IOException {
        FileUtils.createFolder(Constants.GAME_FOLDER);
    }

    /**
     * Create a folder at the specified path.
     * 
     * @param folderPath the path of the folder to create
     * @throws IOException if the folder cannot be created
     */
    public static void createFolder(final String folderPath) throws IOException {
        final File folder = new File(folderPath);
        if (!folder.exists()) {
            try {
                final boolean folderCreated = folder.mkdirs();

                // if dir wasn't created and doesn't exist
                if (!folderCreated && !folder.exists()) {
                    throw new IOException("Unable to create folder" + folderPath);
                }
            } catch (final SecurityException e) {
                throw new IOException("Unable to create folder " + folderPath, e);
            }
        }
    }

    /**
     * Read the content of a file.
     * 
     * @param file the path string of the file to read
     * @throws IOException if the file cannot be read
     * @return the content of the file
     */
    public static String readFile(final String file) throws IOException {
        final Path path = stringToPath(file);
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    /**
     * Read the content of a file.
     * 
     * @param path the path of the file to read
     * @throws IOException if the file cannot be read
     * @return the content of the file
     */
    public static String readFile(final Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    /**
     * Read the content of the resource with given name, which should contain chars
     * encoded as UTF_8.
     *
     * @param resourceName the name of the resource to read
     * @return the content as a String
     * @throws IOException if the resource cannot be found or read
     */
    public static String readResource(final String resourceName) throws IOException {
        Objects.requireNonNull(resourceName);
        final InputStream resource = ClassLoader.getSystemResourceAsStream(resourceName);
        if (resource == null) {
            throw new IOException("Couldn't locate resource with name: " + resourceName);
        }
        return readInputStream(resource);
    }

    /**
     * Read the content of an InputStream which should contain chars encoded as
     * UTF_8.
     *
     * @param inputStream the input stream
     * @throws IOException if the file cannot be read
     * @return the content of the file
     */
    public static String readInputStream(final InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream);
        return CharStreams.toString(
                new InputStreamReader(inputStream, Charsets.UTF_8));
    }

    /**
     * Read the content of a file without throwing exceptions.
     * 
     * @param path the path of the file to read
     * @return the content of the file, optional empty if the file cannot be read
     */
    public static Optional<String> readFileOptional(final Path path) {
        try {
            return Optional.of(readFile(path));
        } catch (final IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Write content to a file.
     * 
     * @param filePath the path of the file to write
     * @param content  the content to write
     * @throws IOException if the file cannot be written
     */
    public static void writeFile(final String filePath, final String content) throws IOException {
        final Path path = stringToPath(filePath);
        // write to path
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }

    /**
     * Create a file if it does not exist.
     * 
     * @param filePath the path of the file to create
     * @throws IOException if the file cannot be created
     */
    public static void createFile(final String filePath) throws IOException {
        final Path path = stringToPath(filePath);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    private FileUtils() {
    }

    private static Path stringToPath(final String filePath) throws IOException {
        try {
            return Paths.get(filePath);
        } catch (final InvalidPathException e) {
            throw new IOException("Error converting file to Path " + filePath, e);
        }
    }
}
