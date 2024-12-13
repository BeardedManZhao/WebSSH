package top.lingyuzhao.webSsh.pojo;

import org.springframework.web.socket.WebSocketSession;
import top.lingyuzhao.utils.SSHClient;

public class SSHConnectInfo implements AutoCloseable {
    private WebSocketSession webSocketSession;

    private SSHClient sshClient;

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

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
