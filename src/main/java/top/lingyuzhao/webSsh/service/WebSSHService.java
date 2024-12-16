package top.lingyuzhao.webSsh.service;

import org.springframework.web.socket.WebSocketSession;
import top.lingyuzhao.webSsh.pojo.SSHConnectInfo;

import java.io.InputStream;


public interface WebSSHService {

    void initConnection(WebSocketSession session);

    /**
     * 文本处理
     *
     * @param buffer  输入文本
     * @param session 源会话
     */
    void recHandle(String buffer, WebSocketSession session);

    /**
     * 二进制处理
     *
     * @param inputStream 输入流
     * @param session     源会话
     */
    void recHandle(InputStream inputStream, WebSocketSession session);

    /**
     * 获取连接
     *
     * @param uuid uuid
     * @return ssh 客户端对象
     */
    SSHConnectInfo getSSHClient(String uuid);

    void close(WebSocketSession session);
}
