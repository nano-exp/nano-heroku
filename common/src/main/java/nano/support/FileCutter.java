package nano.support;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * File cutter
 * <p>
 * Split file
 * Merge files
 */
public class FileCutter {

    private static final int BUFFER_SIZE = 8192;
    private static final String TEMP_FILE_PREFIX = "file-cutter";

    private final Options options;

    public FileCutter(@NotNull Options options) {
        this.options = options;
    }

    public FileCutter() {
        this.options = createDefaultOptions();
    }

    private static @NotNull Options createDefaultOptions() {
        return new Options(getId(), ".part", 50_000_000);
    }

    /**
     * split files
     */
    public @NotNull Map<String, InputStreamSource> split(@NotNull InputStreamSource source) throws IOException {
        var is = source.getInputStream();
        try (is) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            var split = new HashMap<String, InputStreamSource>();
            for (int i = 1; ; i++) {
                var partFilename = this.options.filename() + this.options.partSuffix() + i;
                var tempFile = Files.createTempFile(TEMP_FILE_PREFIX, partFilename);
                var os = Files.newOutputStream(tempFile);
                try (os) {
                    long transferred = 0;
                    while ((read = is.read(buffer, 0, BUFFER_SIZE)) >= 0) {
                        os.write(buffer, 0, read);
                        transferred += read;
                        if (transferred + BUFFER_SIZE > this.options.unitSize()) {
                            break;
                        }
                    }
                }
                tempFile.toFile().deleteOnExit();
                split.put(partFilename, new FileSystemResource(tempFile));
                if (read <= 0) {
                    // All bytes read, break loop
                    break;
                }
            }
            return split;
        }
    }

    /**
     * merge files
     */
    public @NotNull Pair<String, InputStreamSource> merge(@NotNull Map<String, InputStreamSource> partSourceMap) throws IOException {
        var filename = this.options.filename();
        var tempFile = Files.createTempFile(TEMP_FILE_PREFIX, filename);
        tempFile.toFile().deleteOnExit();
        var os = Files.newOutputStream(tempFile);
        try (os) {
            var partFilenameList = new ArrayList<>(partSourceMap.keySet());
            partFilenameList.sort(String::compareTo);
            for (String partFilename : partFilenameList) {
                var is = partSourceMap.get(partFilename).getInputStream();
                try (is) {
                    is.transferTo(os);
                }
            }
            return Pair.of(filename, new FileSystemResource(tempFile));
        }

    }

    /**
     * build zip file
     */
    public @NotNull InputStreamSource zip(@NotNull Map<String, InputStreamSource> sourceMap) throws IOException {
        var filename = this.options.filename();
        var zipFilename = filename + ".zip";
        var tempFile = Files.createTempFile(TEMP_FILE_PREFIX, zipFilename);
        tempFile.toFile().deleteOnExit();
        var zipOutputStream = new ZipOutputStream(Files.newOutputStream(tempFile));
        try (zipOutputStream) {
            for (var source : sourceMap.entrySet()) {
                zipOutputStream.putNextEntry(new ZipEntry(source.getKey()));
                var is = source.getValue().getInputStream();
                try (is) {
                    is.transferTo(zipOutputStream);
                    zipOutputStream.closeEntry();
                }
            }
            return new FileSystemResource(tempFile);
        }
    }

    private static @NotNull String getId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * FileCutter Options
     */
    public static record Options(
            String filename,
            String partSuffix,
            Integer unitSize
    ) {
    }
}
