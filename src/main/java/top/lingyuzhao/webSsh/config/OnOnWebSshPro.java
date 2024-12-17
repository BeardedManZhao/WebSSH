package top.lingyuzhao.webSsh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import top.lingyuzhao.webSsh.constant.OperateHandler;

@Configuration
@ConfigurationProperties(prefix = "on-on-web-ssh")
public class OnOnWebSshPro {

    private FileProgressMonitorConfig fileProgressMonitorConfig;

    private SecureConfig secureConfig;

    public OnOnWebSshPro() {
        OperateHandler.setOnOnWebSshPro(this);
    }

    public FileProgressMonitorConfig getFileProgressMonitorConfig() {
        return fileProgressMonitorConfig;
    }

    public void setFileProgressMonitorConfig(FileProgressMonitorConfig fileProgressMonitorConfig) {
        this.fileProgressMonitorConfig = fileProgressMonitorConfig;
    }

    public SecureConfig getSecureConfig() {
        return secureConfig;
    }

    public void setSecureConfig(SecureConfig secureConfig) {
        this.secureConfig = secureConfig;
    }

    @Override
    public String toString() {
        return "OnOnWebSshPro{" +
                "fileProgressMonitorConfig=" + fileProgressMonitorConfig +
                ", secureConfig=" + secureConfig +
                '}';
    }
}
