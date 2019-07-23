package w.xy.splitter;

import java.nio.file.Path;

/**
 * split archive
 *
 * @author xiaoye.wxy
 * @date 2019/07/21
 */
public interface ArchiveSplitter {
    /**
     * split archive for given path
     *
     * @param path
     */
    void split(Path path);
}
