package top.lingyuzhao.webSsh.websocket;

import top.lingyuzhao.webSsh.constant.ConstantPool;
import top.lingyuzhao.webSsh.service.WebSSHService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 处理websocket请求
 */
@Component
public class WebSSHWebSocketHandler extends TextWebSocketHandler {
    private final WebSSHService webSSHService;
    private final Logger logger = LoggerFactory.getLogger(WebSSHWebSocketHandler.class);

    public WebSSHWebSocketHandler(WebSSHService webSSHService) {
        this.webSSHService = webSSHService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        logger.info("用户:{},连接WebSSH", webSocketSession.getAttributes().get(ConstantPool.USER_UUID_KEY));
        //调用初始化连接
        webSSHService.initConnection(webSocketSession);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        webSSHService.recvHandle(message.getPayload(), session);
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession webSocketSession, @NonNull Throwable throwable) {
        logger.error("数据传输错误");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, @NonNull CloseStatus closeStatus) {
        logger.info("用户:{}断开webssh连接", webSocketSession.getAttributes().get(ConstantPool.USER_UUID_KEY));
        //调用service关闭连接
        webSSHService.close(webSocketSession);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
