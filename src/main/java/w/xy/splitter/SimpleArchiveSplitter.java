package w.xy.splitter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

import w.xy.config.Context;
import lombok.extern.java.Log;

/**
 * split base on bytes size, since java doesn't support standard way for splitting archives.
 *
 * this implementation does not depend on specific compression scheme.
 *
 * @author xiaoye.wxy
 * @date 2019/07/20
 */
@Log
public class SimpleArchiveSplitter implements ArchiveSplitter {

    private Context ctx;

    /**
     * fixed buffer size 1 mb
     */
    private static final int BUFFER_SIZE = 1024 * 1024;

    public SimpleArchiveSplitter(Context ctx) {
        this.ctx = ctx;
    }

    public void split(Path path) {
        int splitCount = 1;

        long splitSizeInBytes = ctx.getSplitSize() * 1024 * 1024;

        byte[] buffer = new byte[BUFFER_SIZE];

        Path newFile = newSplitFile(path, splitCount);
        OutputStream out = null;
        int bytesCount;
        int writtenByteSize = 0;
        try (InputStream fis = Files.newInputStream(path)) {
            out = Files.newOutputStream(newFile);
            while ((bytesCount = fis.read(buffer)) > 0) {
                out.write(buffer, 0, bytesCount);
                writtenByteSize += bytesCount;
                if (writtenByteSize >= splitSizeInBytes) {
                    out.close();
                    newFile = newSplitFile(path, ++splitCount);
                    out = Files.newOutputStream(newFile);
                    writtenByteSize = 0;
                }
            }
            Files.deleteIfExists(ctx.getOutputTmpFilePath());
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private Path newSplitFile(Path path, int splitCount) {
        String filePartName = String.format("%s.part_%d", path.getFileName(), splitCount);
        return path.resolveSibling(filePartName);
    }
}
