package w.xy.merger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;

import lombok.extern.java.Log;
import w.xy.splitter.ArchiveSplitter;

import static w.xy.util.FileNameUtils.getSplitFileName;

/**
 * Merge split files back to the original archive.
 * It scans the files within a directory, only files split by {@link ArchiveSplitter} will be merged,
 * this is recognized by the patterns in the split file names.
 *
 * While merging the split files, the index in the files should be consecutive and consistent.
 *
 * @author xiaoye.wxy
 * @date 2019/07/21
 */
@Log
public class SimpleArchiveMerger implements ArchiveMerger {

    @Override
    public List<Path> merge(Path path) {
        List<Path> mergedPaths = new ArrayList<>();
        try {
            Map<String, List<Path>> mergePaths = Files.list(path)
                .filter(p -> !Files.isDirectory(p))
                .filter(p -> p.getFileName().toString().matches(".+?\\.part_\\d+"))
                .collect(Collectors.groupingBy(p -> getSplitFileName(p)[0]));

            for (Entry<String, List<Path>> pathEntry : mergePaths.entrySet()) {
                List<Path> paths = pathEntry.getValue();
                Collections.sort(paths, Comparator.comparingInt(p -> Integer.parseInt(getSplitFileName(p)[1])));
                Path originalPath = path.resolve(pathEntry.getKey() + "_" + System.currentTimeMillis() + ".tmp");
                Files.deleteIfExists(originalPath);
                try (OutputStream outputStream = Files.newOutputStream(originalPath)) {
                    int partIndex = 0;
                    for (Path p : paths) {
                        int nextPartIndex = Integer.parseInt(getSplitFileName(p)[1]);
                        log.log(Level.INFO, "merging part " + nextPartIndex);
                        if (nextPartIndex - partIndex == 1) {
                            Files.copy(p, outputStream);
                            partIndex = nextPartIndex;
                        } else {
                            throw new RuntimeException(p.getFileName() + " previous part is missing.");
                        }
                    }
                    mergedPaths.add(originalPath);
                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return mergedPaths;
    }
}
