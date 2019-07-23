package w.xy.compressor;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import w.xy.config.Context;
import w.xy.splitter.ArchiveSplitter;
import w.xy.splitter.SimpleArchiveSplitter;
import lombok.extern.java.Log;

import static java.util.stream.Collectors.toList;

/**
 * implemented concurrent compressor by utilizing the NIO {@link FileSystem},
 * this enables parallel writing to the destination file system.
 * The compression task is submitted by {@link CompletableFuture#runAsync(Runnable)}, this enables per-file level concurrency for the compression.
 *
 * The implementation of this compressor does not limit to a specific compression scheme.
 *
 * @author xiaoye.wxy
 * @date 2019/07/21
 */
@Log
public class ConcurrentCompressor implements Compressor {

    private Context ctx;

    private ArchiveSplitter splitter;

    public ConcurrentCompressor(Context ctx) {
        this.ctx = ctx;
        this.splitter = new SimpleArchiveSplitter(ctx);
    }

    @Override
    public void compress() {
        try {
            Files.deleteIfExists(ctx.getOutputTmpFilePath());
            ctx.startFileSystem();
            //submit all async compression task
            List<CompletableFuture<Void>> allTasks =
                Files.walk(ctx.getInputDirPath())
                    .filter(path -> !path.getFileName().toString().equals("/"))
                    .map(this::copyToFileSystem)
                    .collect(toList());
            CompletableFuture[] tasksArr = allTasks.toArray(new CompletableFuture[allTasks.size()]);
            //wait concurrent task to finish
            CompletableFuture.allOf(tasksArr).join();
            ctx.closeFileSystem();
            if (Files.exists(ctx.getOutputTmpFilePath())) {
                splitter.split(ctx.getOutputTmpFilePath());
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * async compression task for single file
     *
     * @param src
     * @return
     */
    private CompletableFuture<Void> copyToFileSystem(Path src) {
        return CompletableFuture.runAsync(() -> {
            try {
                FileSystem fs = ctx.getFileSystem();
                Path relativePath = ctx.getInputDirPath().relativize(src);
                Path root = fs.getPath("/");
                if (Files.isDirectory(src)) {
                    Path dirToCreate = fs.getPath(root.toString(), relativePath.toString());
                    if (Files.notExists(dirToCreate)) {
                        Files.createDirectories(dirToCreate);
                    }
                } else {
                    final Path dest = fs.getPath(root.toString(), relativePath.toString());
                    final Path parent = dest.getParent();
                    if (Files.notExists(parent)) {
                        Files.createDirectories(parent);
                    }
                    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        });
    }

}
