package w.xy.util;

import java.nio.file.Path;

/**
 * file name op utils
 *
 * @author xiaoye.wxy
 * @date 2019/07/21
 */
public class FileNameUtils {
    /**
     * split archive parts into basename and index
     *
     * @param p
     * @return
     */
    public static String[] getSplitFileName(Path p) {
        return p.getFileName().toString().split("\\.part_");
    }
}
