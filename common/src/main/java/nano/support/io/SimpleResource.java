package nano.support.io;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * A simple resource
 *
 * @author cbdyzj
 * @since 2020.9.12
 */
public class SimpleResource implements Resource {

    private static final Charset utf8 = StandardCharsets.UTF_8;

    private final Resource delegate;

    public SimpleResource(@NotNull Resource delegate) {
        this.delegate = delegate;
    }

    public Reader getAsReader(Charset charset) {
        var inputStream = this.getInputStream();
        return new InputStreamReader(inputStream, charset);
    }

    public Reader getAsReader() {
        return this.getAsReader(utf8);
    }

    public byte[] getAllBytes() {
        var inputStream = this.getInputStream();
        try (inputStream) {
            return inputStream.readAllBytes();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public String getAsString() {
        return this.getAsString(utf8);
    }

    public String getAsString(Charset charset) {
        var bytes = this.getAllBytes();
        return new String(bytes, charset);
    }

    public String getAsString(String charset) {
        try {
            var bytes = this.getAllBytes();
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public boolean exists() {
        return this.delegate.exists();
    }

    @Override
    public @NotNull URL getURL() {
        try {
            return this.delegate.getURL();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public @NotNull URI getURI() {
        try {
            return this.delegate.getURI();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);

        }
    }

    @Override
    public @NotNull File getFile() {
        try {
            return this.delegate.getFile();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);

        }
    }

    @Override
    public long contentLength() {
        try {
            return this.delegate.contentLength();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public long lastModified() {
        try {
            return this.delegate.lastModified();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public @NotNull Resource createRelative(@NotNull String relativePath) {
        try {
            return this.delegate.createRelative(relativePath);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public String getFilename() {
        return this.delegate.getFilename();
    }

    @Override
    public @NotNull String getDescription() {
        return this.delegate.getDescription();
    }

    @Override
    public @NotNull InputStream getInputStream() {
        try {
            return this.delegate.getInputStream();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

}
