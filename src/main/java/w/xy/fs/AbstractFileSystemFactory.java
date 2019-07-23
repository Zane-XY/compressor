package w.xy.fs;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;

/**
 * @author xiaoye.wxy
 * @date 2019/07/21
 */
public abstract class AbstractFileSystemFactory {

    public abstract FileSystem createFileSystem(Path path) throws IOException;
}
