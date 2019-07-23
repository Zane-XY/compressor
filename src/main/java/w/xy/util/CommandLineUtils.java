package w.xy.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

import w.xy.config.AppOption;
import w.xy.config.UserAction;
import lombok.extern.java.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * CommandLine parser
 *
 * @author xiaoye.wxy
 * @date 2019/07/22
 */
@Log
public class CommandLineUtils {
    /**
     * parse commandline args
     *
     * @param args
     * @return
     */
    public static AppOption parseArgs(String[] args) {
        Options options = new Options();
        Option input = new Option("i", "input-dir", true, "input dir");
        options.addOption(input);

        Option output = new Option("o", "output-dir", true, "output dir");
        output.setRequired(true);
        options.addOption(output);

        Option compressionSize = new Option("s", "split-size", true, "split archive size (integer in MB)");
        output.setRequired(false);
        options.addOption(compressionSize);

        Option userAction = new Option("a", "action", true, "available actions: c (for compress), d (for decompress)");
        output.setRequired(true);
        options.addOption(userAction);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            log.log(Level.WARNING, e.getMessage());
            formatter.printHelp("Java compressor", options);
            System.exit(1);
        }

        AppOption appOption = new AppOption();
        appOption.setInputDir(cmd.getOptionValue("input-dir"));
        appOption.setOutputDir(cmd.getOptionValue("output-dir"));

        if (!Files.exists(Paths.get(appOption.getInputDir()))
            || !Files.isDirectory(Paths.get(appOption.getInputDir()))) {
            log.log(Level.WARNING, appOption.getInputDir() + " is not a valid directory");
            System.exit(1);
        }

        if (!Files.exists(Paths.get(appOption.getOutputDir()))
            || !Files.isDirectory(Paths.get(appOption.getOutputDir()))) {
            log.log(Level.WARNING, appOption.getOutputDir() + " is not a valid directory");
            System.exit(1);
        }

        if (appOption.getInputDir().equals(appOption.getOutputDir())) {
            log.log(Level.WARNING, "input-dir should not be the same as output-dir");
            System.exit(1);
        }

        if (cmd.hasOption("split-size")) {
            try {
                appOption.setSplitSize(Integer.parseInt(cmd.getOptionValue("split-size")));
            } catch (NumberFormatException e) {
                log.log(Level.WARNING, "split size should specified in integer format");
            }
        }
        if (cmd.getOptionValue("action").equals("c")) {
            appOption.setUserAction(UserAction.COMPRESS);
        }
        if (cmd.getOptionValue("action").equals("d")) {
            appOption.setUserAction(UserAction.DECOMPRESS);
        }
        return appOption;
    }
}
