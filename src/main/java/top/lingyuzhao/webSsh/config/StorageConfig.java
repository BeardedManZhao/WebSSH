package top.lingyuzhao.webSsh.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingyuzhao.webSsh.pojo.WebSSHData;
import top.lingyuzhao.webSsh.pojo.WebSSHDataList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * 存储设置
 *
 * @author 赵凌宇
 */
public class StorageConfig {

    private final static Logger logger = LoggerFactory.getLogger(StorageConfig.class);

    private WebSSHDataList webSSHDataList;
    private String storageDir;

    private Consumer<WebSSHData> handler = data -> {
    };

    private boolean enWriter = false;

    public void setStorageDir(String storageDir) {
        this.storageDir = storageDir;
        if (!storageDir.endsWith("/")) {
            this.storageDir += '/';
        }
    }

    /**
     * 获取存储的 WebSSH 数据列表。
     *
     * @param objectMapper 用于将 JSON 字符串反序列化为 Java 对象的对象映射器 初始化阶段不得为空 其他时候无妨
     * @return 包含 WebSSH 数据的列表
     */
    public WebSSHDataList getWebSSHDataList(ObjectMapper objectMapper) {
        if (webSSHDataList == null) {
            if (objectMapper == null) {
                throw new IllegalArgumentException("初始化阶段 objectMapper 不能为空...");
            }
            webSSHDataList = new WebSSHDataList();
            if (storageDir == null) {
                return webSSHDataList;
            }
            Path filePath = Paths.get(storageDir, "WebSSHData.list");
            if (!Files.exists(filePath)) {
                // 如果目录不存在就不加载
                return webSSHDataList;
            }
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    WebSSHData data = objectMapper.readValue(line, WebSSHData.class);
                    webSSHDataList.add(data);
                }
            } catch (IOException e) {
                throw new RuntimeException("读取 WebSSHData.list 文件失败", e);
            }
        }
        return webSSHDataList;
    }

    /**
     * 设置是否启用 WebSSH 数据的持久化。
     *
     * @param enable 是否启用
     */
    public void setEnableWriter(boolean enable) {
        if (enable) {
            handler = this::saveWebSSHDataMap;
            logger.info("启用 WebSSH 数据持久化, 登录成功的ssh配置将会被自动记录!");
        } else {
            handler = data -> {
            };
            logger.info("关闭 WebSSH 数据持久化, 登录成功的ssh配置将不会被自动记录! 但您依旧可以读取/删除已经保存的ssh配置!");
        }
        enWriter = enable;
    }

    /**
     * 保存 WebSSH 数据
     *
     * @param webSSHData 要保存的数据
     */
    public void saveWebSSHDataMap(WebSSHData webSSHData) {
        // 先删除 再追加
        WebSSHDataList webSSHDataList1 = this.getWebSSHDataList(null);
        webSSHDataList1.remove(webSSHData.toString());
        webSSHDataList1.add(webSSHData);
    }

    /**
     * 将 WebSSH 数据列表保存到磁盘
     *
     * @param objectMapper 用于将 Java 对象序列化为 JSON 字符串的对象映射器
     */
    public void saveToDiskWebSSHDataList(ObjectMapper objectMapper) {
        if (!this.enWriter) {
            return;
        }
        Path filePath = Paths.get(storageDir, "WebSSHData.list");
        if (!Files.exists(filePath)) {
            try {
                Files.createDirectories(filePath.getParent());
            } catch (IOException e) {
                throw new RuntimeException("创建目录失败", e);
            }
        }
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(filePath)) {
            this.saveToStreamWebSSHDataList(objectMapper, bufferedWriter);
        } catch (IOException e) {
            throw new RuntimeException("持久化用户ssh 数据失败！", e);
        }
    }

    /**
     * 将 WebSSH 数据列表保存到流中
     *
     * @param objectMapper   用于将 Java 对象序列化为 JSON 字符串的对象映射器
     * @param bufferedWriter 用于写入数据的输出流
     * @throws IOException 如果在写入数据时发生错误
     */
    public void saveToStreamWebSSHDataList(ObjectMapper objectMapper, BufferedWriter bufferedWriter) throws IOException {
        for (WebSSHData data : webSSHDataList) {
            String jsonString = objectMapper.writeValueAsString(data);
            bufferedWriter.write(jsonString);
            bufferedWriter.newLine();
        }
    }

    /**
     * 这是一个生命周期函数
     * 处理和使用资源
     */
    public void handler(WebSSHData webSSHData) {
        handler.accept(webSSHData);
    }

    /**
     * 这是一个生命周期函数
     * 释放资源
     *
     * @param objectMapper 用于将 Java 对象序列化为 JSON 字符串的对象映射器
     */
    public void end(ObjectMapper objectMapper) {
        saveToDiskWebSSHDataList(objectMapper);
    }
}
