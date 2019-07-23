package w.xy.decompressor;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

import w.xy.config.Context;
import w.xy.merger.ArchiveMerger;
import w.xy.merger.SimpleArchiveMerger;
import lombok.extern.java.Log;
import w.xy.compressor.ConcurrentCompressor;

/**
 * Decompress the split archives within a directory back to the original directory structure.
 *
 * This class supports restoration of different batches of compression.
 * It first using merger to merge split parts back to original archive, then concurrently extract to files within the archive
 * to the destination.
 * This basically works as the reverse order of {@link ConcurrentCompressor}.
 *
 * @author xiaoye.wxy
 * @date 2019/07/21
 */
@Log
public class ConcurrentDecompressor implements Decompressor {

    private Context ctx;

    private ArchiveMerger merger;

    public ConcurrentDecompressor(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void decompress() {
        merger = new SimpleArchiveMerger();
        List<Path> mergedPaths = merger.merge(ctx.getInputDirPath());

        for (Path path : mergedPaths) {
            ctx.startFileSystem(path);
            FileSystem fs = ctx.getFileSystem();
            Path root = fs.getPath("/");
            try {
                List<CompletableFuture<Void>> allTasks =
                    Files.walk(root)
                        .sorted()
                        .map(p -> copyToFileSystem(ctx, root, p))
                        .collect(Collectors.toList());
                CompletableFuture.allOf(allTasks.toArray(new CompletableFuture[allTasks.size()])).thenAccept(__ -> {
                    try {
                        //ctx.closeFileSystem();
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        log.log(Level.SEVERE, e.getMessage(), e);
                    }
                }).join();
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * the async task for copy files from archive file system to destination.
     *
     * @param ctx
     * @param root
     * @param p
     */
    private CompletableFuture<Void> copyToFileSystem(Context ctx, Path root, Path p) {
        return CompletableFuture.runAsync(() -> {
            Path destPath = ctx.getOutputDirPath().resolve(root.relativize(p).toString());
            log.log(Level.INFO, "copying " + p + " to " + destPath);
            try {
                if (Files.isDirectory(p)) {
                    Files.createDirectories(destPath);
                } else {
                    if (!Files.exists(destPath.getParent())) {
                        Files.createDirectories(destPath.getParent());
                    }
                    Files.copy(p, destPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        });
    }
}
