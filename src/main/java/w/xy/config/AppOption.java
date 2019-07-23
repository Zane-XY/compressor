package w.xy.config;

import lombok.Data;

/**
 * @author xiaoye.wxy
 * @date 2019/07/20
 */
@Data
public class AppOption {
    private String inputDir;
    private String outputDir;
    /**
     * choose zip scheme by default
     */
    private CompressionScheme compressionScheme = CompressionScheme.ZIP;

    private UserAction userAction;

    int splitSize = 1;
}
