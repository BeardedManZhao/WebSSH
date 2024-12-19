package top.lingyuzhao.webSsh.config;

/**
 * 文件进度配置
 */
public class FileProgressMonitorConfig {

    /**
     * 文件进度消息更新阈值 传输量达到此数值就更新一次日志
     */
    private long updateThreshold = 8388608;


    public long getUpdateThreshold() {
        return updateThreshold;
    }

    public void setUpdateThreshold(long updateThreshold) {
        this.updateThreshold = updateThreshold;
    }

    @Override
    public String toString() {
        return "FileProgressMonitorConfig{" +
                "updateThreshold=" + updateThreshold +
                '}';
    }
}
