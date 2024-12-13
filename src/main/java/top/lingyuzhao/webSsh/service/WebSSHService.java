package top.lingyuzhao.webSsh.service;

import org.springframework.web.socket.WebSocketSession;


public interface WebSSHService {

    void initConnection(WebSocketSession session);

    void recvHandle(String buffer, WebSocketSession session);

    void close(WebSocketSession session);
}
