package w.xy.fs;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoye.wxy
 * @date 2019/07/21
 */
public class ZipFileSystemFactory extends AbstractFileSystemFactory {

    @Override
    public FileSystem createFileSystem(Path path) throws IOException {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        env.put("useTempFile", "true");
        URI zipURI = URI.create(String.format("jar:%s", path.toUri()));
        return FileSystems.newFileSystem(zipURI, env);
    }

}
