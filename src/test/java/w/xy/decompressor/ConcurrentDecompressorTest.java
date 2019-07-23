package w.xy.decompressor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import w.xy.compressor.ConcurrentCompressor;
import w.xy.config.AppOption;
import w.xy.config.Context;
import w.xy.config.UserAction;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

/**
 * @author xiaoye.wxy
 * @date 2019/07/21
 */
public class ConcurrentDecompressorTest {

    @Test
    public void decompress() throws IOException {
        Path target = Paths.get(System.getProperty("user.dir"), "target");

        if (Files.exists(target)) {
            Path input = target.resolve("classes/");
            Path output = target.resolve("output/");
            if (!Files.exists(output)) {
                Files.createDirectories(output);
            }

            AppOption option = new AppOption();
            option.setUserAction(UserAction.COMPRESS);
            option.setInputDir(input.toString());
            option.setOutputDir(output.toString());
            option.setSplitSize(1);
            Context context = new Context(option);
            ConcurrentCompressor compressor = new ConcurrentCompressor(context);
            compressor.compress();
            option.setInputDir(output.toString());
            option.setUserAction(UserAction.DECOMPRESS);
            ConcurrentDecompressor decompressor = new ConcurrentDecompressor(new Context(option));
            decompressor.decompress();
            assertTrue(Files.exists(output.resolve("com/agoda/it/App.class")));
        }
    }
}