package top.lingyuzhao.webSsh.constant;

import com.jcraft.jsch.JSchException;
import org.slf4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import top.lingyuzhao.utils.ConsoleColor;
import top.lingyuzhao.utils.SSHClient;
import top.lingyuzhao.webSsh.pojo.SSHConnectInfo;
import top.lingyuzhao.webSsh.pojo.WebSSHData;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static top.lingyuzhao.utils.IOUtils.close;

/**
 * Operate 处理器
 *
 * @author Lingyuzhao
 */
@SuppressWarnings("unused")
public enum OperateHandler {

    command(false) {
        @Override
        public void handlerText(SSHConnectInfo sshConnectInfo, WebSSHData textMessage, WebSocketSession session, Logger logger, String userId) {
            try {
                final boolean b = sshConnectInfo.getSshClient().sendCommand(textMessage.getCommand());
                if (!b) {
                    close(session);
                    session.close();
                }
            } catch (IOException e) {
                logger.error("web ssh连接异常");
                logger.error("异常信息:{}", e.getMessage());
                close(session);
            }
        }
    },
    connect(false) {
        private void connectToSSH(SSHConnectInfo sshConnectInfo, WebSSHData webSSHData, WebSocketSession webSocketSession) throws JSchException, IOException {
            final SSHClient sshClient = new SSHClient(
                    webSSHData.getUsername(),
                    webSSHData.getHost(),
                    webSSHData.getPassword(),
                    webSSHData.getPort(),
                    null,
                    buffer -> {
                        try {
                            webSocketSession.sendMessage(new TextMessage(buffer));
                        } catch (IllegalStateException | IOException ignored) {
                            // 这里出现错误一般是因为 客户端断开了连接 并非大错误
                        }
                    },
                    Charset.defaultCharset()
            );
            sshClient.connect(3000);
            sshConnectInfo.setSshClient(sshClient);
        }

        @Override
        public void handlerText(SSHConnectInfo sshConnectInfo, WebSSHData webSSHData, WebSocketSession session, Logger logger, String userId) {
            if (sshConnectInfo.getSshClient() != null) {
                close(session);
            }
            //启动线程异步处理
            try {
                connectToSSH(sshConnectInfo, webSSHData, session);
            } catch (JSchException | IOException e) {
                logger.error("web ssh连接异常");
                logger.error("异常信息:{}", e.getMessage());
                close(session);
                try {
                    session.close();
                } catch (IOException ignored) {

                }
            }
        }

    },
    sftp_upload(false) {
        @Override
        public void handlerText(SSHConnectInfo sshConnectInfo, WebSSHData textMessage, WebSocketSession session, Logger logger, String userId) {
            // 直接回复当前的操作的响应
            try {
                session.sendMessage(this.responseTextMessage);
            } catch (IOException e) {
                logger.warn(session.getId() + " " + this.name() + " error~ ", e);
            }
        }
    },
    show_uuid(true) {
        @Override
        public void handlerText(SSHConnectInfo sshConnectInfo, WebSSHData textMessage, WebSocketSession session, Logger logger, String userId) {
            try {
                session.sendMessage(new TextMessage(this.responseStrMessage + userId));
            } catch (IOException e) {
                logger.warn(session.getId() + " " + this.name() + " error~ ", e);
            }
        }
    };

    protected final String responseStrMessage;
    protected final TextMessage responseTextMessage;
    private final boolean allowSshNull;

    OperateHandler(boolean allowSshNull) {
        this.allowSshNull = allowSshNull;
        this.responseStrMessage = "OnOnWebSsh-20030806-" + this.name();
        this.responseTextMessage = new TextMessage(this.responseStrMessage);
    }

    public abstract void handlerText(SSHConnectInfo sshConnectInfo, WebSSHData textMessage, WebSocketSession session, Logger logger, String userId);

    public void handlerBinary(SSHConnectInfo sshConnectInfo, InputStream inputStream, WebSocketSession session, Logger logger, String userId) {
        try {
            session.sendMessage(new TextMessage(ConsoleColor.ANSI_RED + "[OnOn-WebSsh] 不支持二进制消息 来自：" + this.name() + "\r\n"));
        } catch (IOException e) {
            logger.warn(session.getId() + " " + this.name() + " error~ ", e);
        }
    }

    /**
     * 是否允许 ssh 为空
     *
     * @return boolean 返回 true 不允许为空 false 允许为空
     */
    public boolean isNotAllowSshNull() {
        return !this.allowSshNull;
    }
}
