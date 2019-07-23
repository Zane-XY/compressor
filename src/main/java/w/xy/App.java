package w.xy;

import java.util.logging.LogManager;

import w.xy.compressor.ConcurrentCompressor;
import w.xy.config.AppOption;
import w.xy.config.Context;
import w.xy.decompressor.ConcurrentDecompressor;
import w.xy.util.CommandLineUtils;

/**
 * execution entrance
 */
public class App {

    private static void configLogger() throws Exception {
        LogManager manager = LogManager.getLogManager();
        manager.readConfiguration(App.class.getClassLoader().getResourceAsStream("logger.properties"));
    }

    public static void main(String[] args) throws Exception {
        configLogger();
        AppOption appOption = CommandLineUtils.parseArgs(args);
        Context ctx = new Context(appOption);
        switch (appOption.getUserAction()) {
            case COMPRESS:
                new ConcurrentCompressor(ctx).compress();
            case DECOMPRESS:
                new ConcurrentDecompressor(ctx).decompress();
        }
    }
}
