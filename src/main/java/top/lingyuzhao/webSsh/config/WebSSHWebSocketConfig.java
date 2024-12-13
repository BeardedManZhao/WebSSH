package top.lingyuzhao.webSsh.config;

import top.lingyuzhao.webSsh.interceptor.WebSocketInterceptor;
import top.lingyuzhao.webSsh.websocket.WebSSHWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 配置类
 */
@Configuration
@EnableWebSocket
public class WebSSHWebSocketConfig implements WebSocketConfigurer {
    final
    WebSSHWebSocketHandler webSSHWebSocketHandler;

    public WebSSHWebSocketConfig(WebSSHWebSocketHandler webSSHWebSocketHandler) {
        this.webSSHWebSocketHandler = webSSHWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //socket通道
        //指定处理器和路径
        webSocketHandlerRegistry.addHandler(webSSHWebSocketHandler, "/webssh")
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins("*");
    }
}
