package top.lingyuzhao.webSsh.service.impl;

import top.lingyuzhao.webSsh.constant.ConstantPool;
import top.lingyuzhao.webSsh.pojo.SSHConnectInfo;
import top.lingyuzhao.webSsh.pojo.WebSSHData;
import top.lingyuzhao.webSsh.service.WebSSHService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.JSchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import top.lingyuzhao.utils.SSHClient;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class WebSSHServiceImpl implements WebSSHService {
    //存放ssh连接信息的map
    private static final Map<String, Object> sshMap = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(WebSSHServiceImpl.class);


    @Override
    public void initConnection(WebSocketSession session) {
        SSHConnectInfo sshConnectInfo = new SSHConnectInfo();
        sshConnectInfo.setWebSocketSession(session);
        String uuid = String.valueOf(session.getAttributes().get(ConstantPool.USER_UUID_KEY));
        //将这个ssh连接信息放入map中
        sshMap.put(uuid, sshConnectInfo);
    }

    @Override
    public void recvHandle(String buffer, WebSocketSession session) {
        ObjectMapper objectMapper = new ObjectMapper();
        WebSSHData webSSHData;
        try {
            webSSHData = objectMapper.readValue(buffer, WebSSHData.class);
        } catch (IOException e) {
            logger.error("Json转换异常");
            logger.error("异常信息:{}", e.getMessage());
            return;
        }
        String userId = String.valueOf(session.getAttributes().get(ConstantPool.USER_UUID_KEY));
        switch (webSSHData.getOperate()) {
            case ConstantPool.WEBSSH_OPERATE_CONNECT -> {
                //找到刚才存储的ssh连接对象
                SSHConnectInfo sshConnectInfo = (SSHConnectInfo) sshMap.get(userId);
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
                }
            }
            case ConstantPool.WEBSSH_OPERATE_COMMAND -> {
                String command = webSSHData.getCommand();
                SSHConnectInfo sshConnectInfo = (SSHConnectInfo) sshMap.get(userId);
                if (sshConnectInfo != null) {
                    try {
                        sshConnectInfo.getSshClient().sendCommand(command);
                    } catch (IOException e) {
                        logger.error("web ssh连接异常");
                        logger.error("异常信息:{}", e.getMessage());
                        close(session);
                    }
                }
            }
            default -> {
                logger.error("不支持的操作");
                close(session);
            }
        }
    }

    @Override
    public void close(WebSocketSession session) {
        String userId = String.valueOf(session.getAttributes().get(ConstantPool.USER_UUID_KEY));
        SSHConnectInfo sshConnectInfo = (SSHConnectInfo) sshMap.get(userId);
        if (sshConnectInfo != null) {
            //断开连接
            sshConnectInfo.close();
            //map中移除
            sshMap.remove(userId);
        }
    }

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
}
