package w.xy.config;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import w.xy.fs.AbstractFileSystemFactory;
import w.xy.fs.ZipFileSystemFactory;
import lombok.Getter;
import lombok.extern.java.Log;

/**
 * Context data while app execution
 *
 * @author xiaoye.wxy
 * @date 2019/07/21
 */
@Getter
@Log
public class Context extends AppOption {
    /**
     * file system factory
     */
    private AbstractFileSystemFactory abstractFileSystemFactory;
    /**
     * underlying file system
     */
    private FileSystem fileSystem;
    /**
     * input directory path
     */
    private Path inputDirPath;
    /**
     * output directory path
     */
    private Path outputDirPath;
    /**
     * input file path
     */
    private Path inputFilePath;
    /**
     * tmp file path for compressed archive
     */
    private Path outputTmpFilePath;

    public Context(AppOption option) {
        super.setInputDir(option.getInputDir());
        super.setOutputDir(option.getOutputDir());
        super.setSplitSize(option.getSplitSize());

        this.inputDirPath = Paths.get(getInputDir());
        this.outputDirPath = Paths.get(getOutputDir());
        this.outputTmpFilePath = outputDirPath.resolve(inputDirPath.getFileName());
    }

    /**
     * create file system for compression
     */
    public void startFileSystem() {
        startFileSystem(this.outputTmpFilePath);
    }

    /**
     * create file system for given path
     *
     * @param path
     */
    public void startFileSystem(Path path) {
        if (fileSystem == null && getCompressionScheme().equals(CompressionScheme.ZIP)) {
            abstractFileSystemFactory = new ZipFileSystemFactory();
            try {
                fileSystem = abstractFileSystemFactory.createFileSystem(path);
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * close file system
     */
    public void closeFileSystem() {
        if (fileSystem.isOpen()) {
            try {
                fileSystem.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
}
