package top.lingyuzhao.webSsh.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import top.lingyuzhao.utils.IOUtils;
import top.lingyuzhao.webSsh.config.OnOnWebSshPro;
import top.lingyuzhao.webSsh.config.SecureConfig;
import top.lingyuzhao.webSsh.config.StorageConfig;
import top.lingyuzhao.webSsh.constant.ConstantPool;
import top.lingyuzhao.webSsh.constant.OperateHandler;
import top.lingyuzhao.webSsh.pojo.WebSSHData;
import top.lingyuzhao.webSsh.service.WebSSHService;

import java.net.InetSocketAddress;

/**
 * 处理websocket请求
 */
@Component
public class WebSSHWebSocketHandler extends AbstractWebSocketHandler {
    private final WebSSHService webSSHService;
    private final Logger logger = LoggerFactory.getLogger(WebSSHWebSocketHandler.class);

    private final SecureConfig secureConfig;
    private final StorageConfig storageConfig;

    @Autowired
    public WebSSHWebSocketHandler(WebSSHService webSSHService, OnOnWebSshPro onOnWebSshPro) {
        logger.info(onOnWebSshPro.toString());
        this.webSSHService = webSSHService;
        this.secureConfig = onOnWebSshPro.getSecureConfig();
        this.storageConfig = onOnWebSshPro.getStorageConfig();
        logger.info("""
                                  
                                  .-') _                   .-') _ \s
                                 ( OO ) )                 ( OO ) )\s
                 .-'),-----. ,--./ ,--,'  .-'),-----. ,--./ ,--,' \s
                ( OO'  .-.  '|   \\ |  |\\ ( OO'  .-.  '|   \\ |  |\\ \s
                /   |  | |  ||    \\|  | )/   |  | |  ||    \\|  | )\s
                \\_) |  |\\|  ||  .     |/ \\_) |  |\\|  ||  .     |/ \s
                  \\ |  | |  ||  |\\    |    \\ |  | |  ||  |\\    |  \s
                   `'  '-'  '|  | \\   |     `'  '-'  '|  | \\   |  \s
                     `-----' `--'  `--'       `-----' `--'  `--'  \s
                                     welcome to OnOn-WebSSH~~~~~
                                """);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        // 检查源IP是否合法
        final InetSocketAddress remoteAddress = webSocketSession.getRemoteAddress();
        if (remoteAddress == null) {
            return;
        }
        final String hostAddress = remoteAddress.getAddress().getHostAddress();
        WebSSHData webSSHData = new WebSSHData();
        if (!secureConfig.srcIpIsValid(hostAddress)) {
            webSSHData.setCommand("不允许您以IP " + hostAddress + " 访问此 WebSSH 服务，OnOnWebSsh 服务器管理员设置了允许访问 OnOnWebSsh的IP规则，您的IP不符合这个规则。");
            OperateHandler.error.handlerText(null, webSSHData, webSocketSession, logger, webSSHData.getCommand());
            IOUtils.close(webSocketSession);
            return;
        }
        logger.info("用户:{},连接WebSSH", webSocketSession.getAttributes().get(ConstantPool.USER_UUID_KEY));
        //调用初始化连接
        webSSHService.initConnection(webSocketSession);
        // uuid 返回
        OperateHandler.show_uuid.handlerText(null, null, webSocketSession, logger, String.valueOf(webSocketSession.getAttributes().get(ConstantPool.USER_UUID_KEY)));
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        webSSHService.recHandle(message.getPayload(), session);
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession webSocketSession, @NonNull Throwable throwable) {
        logger.error("数据传输错误");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, @NonNull CloseStatus closeStatus) {
        logger.info("用户:{}断开webSSH连接", webSocketSession.getAttributes().get(ConstantPool.USER_UUID_KEY));
        //调用service关闭连接
        webSSHService.close(webSocketSession);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
