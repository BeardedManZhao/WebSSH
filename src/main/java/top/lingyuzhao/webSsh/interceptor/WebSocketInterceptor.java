package top.lingyuzhao.webSsh.interceptor;

import top.lingyuzhao.webSsh.constant.ConstantPool;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

public class WebSocketInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest serverHttpRequest, @NonNull ServerHttpResponse serverHttpResponse, @NonNull WebSocketHandler webSocketHandler, @NonNull Map<String, Object> map) {
        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            //生成一个UUID
            String uuid = UUID.randomUUID().toString().replace("-", "");
            //将uuid放到websocketsession中
            map.put(ConstantPool.USER_UUID_KEY, uuid);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest serverHttpRequest, @NonNull ServerHttpResponse serverHttpResponse, @NonNull WebSocketHandler webSocketHandler, Exception e) {

    }
}
