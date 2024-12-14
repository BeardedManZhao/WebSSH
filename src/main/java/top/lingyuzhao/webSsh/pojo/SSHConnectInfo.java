package top.lingyuzhao.webSsh.pojo;

import org.springframework.web.socket.WebSocketSession;
import top.lingyuzhao.utils.SSHClient;

/**
 * SSH连接信息包装类
 */
public class SSHConnectInfo implements AutoCloseable {
    private WebSocketSession webSocketSession;

    private SSHClient sshClient;

    /**
     * @return WebSocketSession
     */
    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    /**
     * @return SSHClient
     */
    public SSHClient getSshClient() {
        return sshClient;
    }

    public void setSshClient(SSHClient sshClient) {
        this.sshClient = sshClient;
    }

    @Override
    public void close() {
        if (sshClient != null) {
            sshClient.close();
            sshClient = null;
        }
    }
}
