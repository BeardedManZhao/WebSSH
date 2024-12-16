package top.lingyuzhao.webSsh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "on-on-web-ssh")
public class OnOnWebSshPro {

    private FileProgressMonitorConfig fileProgressMonitorConfig;

    public FileProgressMonitorConfig getFileProgressMonitorConfig() {
        return fileProgressMonitorConfig;
    }

    public void setFileProgressMonitorConfig(FileProgressMonitorConfig fileProgressMonitorConfig) {
        this.fileProgressMonitorConfig = fileProgressMonitorConfig;
    }
}
