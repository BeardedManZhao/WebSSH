package top.lingyuzhao.webSsh.config;

import java.util.regex.Pattern;

/**
 * 安全配置
 */
public class SecureConfig {

    /**
     * 源IP地址 匹配器
     */
    private Pattern srcIpPattern;

    /**
     * 目标IP地址 匹配器
     */
    private Pattern destIpPattern;

    public Pattern getSrcIpPattern() {
        return srcIpPattern;
    }

    public void setSrcIpPattern(Pattern srcIpPattern) {
        this.srcIpPattern = srcIpPattern;
    }

    public Pattern getDestIpPattern() {
        return destIpPattern;
    }

    public void setDestIpPattern(Pattern destIpPattern) {
        this.destIpPattern = destIpPattern;
    }

    public boolean isValid(String srcIp, String destIp) {
        return srcIpIsValid(srcIp) && destIpIsValid(destIp);
    }

    public boolean srcIpIsValid(String srcIp) {
        return srcIpPattern == null || srcIpPattern.matcher(srcIp).matches();
    }

    public boolean destIpIsValid(String destIp) {
        return destIpPattern == null || destIpPattern.matcher(destIp).matches();
    }

    @Override
    public String toString() {
        return "SecureConfig{" +
                "srcIpPattern=" + srcIpPattern +
                ", destIpPattern=" + destIpPattern +
                '}';
    }
}
