package w.xy.config;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * @author xiaoye.wxy
 * @date 2019/07/21
 */
public class ContextTest {

    @Test
    public void optionsShouldBeCopiedToContext() {
        AppOption option = new AppOption();
        option.setSplitSize(2);
        option.setInputDir("input");
        option.setOutputDir("output");

        Context context = new Context(option);
        assertEquals("input", context.getInputDir());
        assertEquals("output", context.getOutputDir());
        assertTrue(2 == context.splitSize);
    }
}