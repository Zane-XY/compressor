package w.xy.util;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author xiaoye.wxy
 * @date 2019/07/22
 */
public class FileNameUtilsTest {

    @Test
    public void normalName() {
        String[] parts = FileNameUtils.getSplitFileName(Paths.get("test-input.part_1"));
        assertEquals("test-input", parts[0]);
        assertEquals("1", parts[1]);
    }

    @Test
    public void nameWithEscapingKeyword() {
        String[] parts = FileNameUtils.getSplitFileName(Paths.get("test-input.part.part_1"));
        assertEquals("test-input.part", parts[0]);
        assertEquals("1", parts[1]);
    }

    @Test(expected = InvalidPathException.class)
    public void nameWithOtherCharactersShouldThrowException() {
        FileNameUtils.getSplitFileName(Paths.get("test-input%()@#.*+_.part_1"));
    }

    @Test
    public void namesWithLongIndex() {
        assertEquals("10", FileNameUtils.getSplitFileName(Paths.get("test-input.part_10"))[1]);
    }
}