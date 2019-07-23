package w.xy.merger;

import java.nio.file.Paths;

import org.junit.Test;

/**
 * @author xiaoye.wxy
 * @date 2019/07/23
 */
public class SimpleArchiveMergerTest {

    @Test
    public void merge() {
        SimpleArchiveMerger merger = new SimpleArchiveMerger();
        merger.merge(Paths.get("C:\\Users\\xiaoye.wxy\\Downloads\\test-output"));
    }
}