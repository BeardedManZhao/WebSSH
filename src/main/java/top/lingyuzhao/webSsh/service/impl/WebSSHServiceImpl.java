package top.lingyuzhao.webSsh.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import top.lingyuzhao.webSsh.constant.ConstantPool;
import top.lingyuzhao.webSsh.constant.OperateHandler;
import top.lingyuzhao.webSsh.pojo.SSHConnectInfo;
import top.lingyuzhao.webSsh.pojo.WebSSHData;
import top.lingyuzhao.webSsh.service.WebSSHService;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class WebSSHServiceImpl implements WebSSHService {
    /**
     * 存放每个会话上一次执行的操作记录
     */
    private static final Map<String, String> optionMap = new HashMap<>();
    //存放ssh连接信息的map
    private final Map<String, Object> sshMap = new ConcurrentHashMap<>();
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
    public void recHandle(String buffer, WebSocketSession session) {
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
        final OperateHandler operateHandler;
        try {
            String operate = webSSHData.getOperate();
            operateHandler = OperateHandler.valueOf(operate);
            optionMap.put(userId, operate);
        } catch (IllegalArgumentException e) {
            logger.error("不支持的操作类型 " + e.getMessage());
            return;
        }

        Object o = sshMap.get(userId);
        // 首先检查当前的是否允许为 null
        if (operateHandler.isNotAllowSshNull() && o == null) {
            // 直接返回 不处理
            return;
        }
        operateHandler.handlerText((SSHConnectInfo) o, webSSHData, session, logger, userId);
    }

    @Override
    public void recHandle(InputStream inputStream, WebSocketSession session) {
        // 获取到当前会话上一次的操作
        String userId = String.valueOf(session.getAttributes().get(ConstantPool.USER_UUID_KEY));
        String lastCommand = optionMap.get(userId);
        if (lastCommand == null) {
            logger.error("没有上一次的操作记录 " + userId);
            // 不操作
            return;
        }
        // 获取到操作处理器
        final OperateHandler operateHandler;
        try {
            operateHandler = OperateHandler.valueOf(lastCommand);
        } catch (IllegalArgumentException e) {
            logger.error("不支持的操作类型 " + e.getMessage());
            return;
        }

        // 获取到当前会话的ssh连接信息
        Object o = sshMap.get(userId);
        // 查是否允许为null
        if (operateHandler.isNotAllowSshNull() && o == null) {
            logger.error("ssh连接为null");
            return;
        }
        operateHandler.handlerBinary((SSHConnectInfo) o, inputStream, session, logger, userId);
    }

    @Override
    public SSHConnectInfo getSSHClient(String uuid) {
        Object o = sshMap.get(uuid);
        if (o == null) {
            return null;
        }
        return (SSHConnectInfo) o;
    }

    @Override
    public void close(WebSocketSession session) {
        String userId = String.valueOf(session.getAttributes().get(ConstantPool.USER_UUID_KEY));
        SSHConnectInfo sshConnectInfo = this.getSSHClient(userId);
        if (sshConnectInfo != null) {
            //断开连接
            sshConnectInfo.close();
            //map中移除
            sshMap.remove(userId);
        }
    }
}
