package w.xy.merger;

import java.nio.file.Path;
import java.util.List;

/**
 * Merge archives back to original archive.
 *
 * @author xiaoye.wxy
 * @date 2019/07/21
 */
public interface ArchiveMerger {
    /**
     * return merged files' paths
     *
     * @param path
     * @return
     */
    List<Path> merge(Path path);
}
