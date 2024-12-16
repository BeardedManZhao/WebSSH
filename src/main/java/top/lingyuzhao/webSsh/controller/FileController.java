package top.lingyuzhao.webSsh.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.TextMessage;
import top.lingyuzhao.utils.SSHClient;
import top.lingyuzhao.webSsh.config.OnOnWebSshPro;
import top.lingyuzhao.webSsh.constant.FileProgressMonitor;
import top.lingyuzhao.webSsh.pojo.SSHConnectInfo;
import top.lingyuzhao.webSsh.service.WebSSHService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件上传
 *
 * @author 赵凌宇
 */
@RestController
public class FileController {

    private final WebSSHService webSSHService;
    private final Logger logger = LoggerFactory.getLogger(FileController.class);

    private final OnOnWebSshPro onOnWebSshPro;

    @Autowired
    public FileController(WebSSHService webSSHService, OnOnWebSshPro onOnWebSshPro) {
        this.webSSHService = webSSHService;
        this.onOnWebSshPro = onOnWebSshPro;
    }

    @PostMapping("/upload")
    public void upload(@RequestParam("file") MultipartFile file, @RequestParam("wordDir") String wordDir, @RequestParam("uuid") String uuid) {
        if (file.isEmpty()) {
            logger.warn("上传的文件为空");
            return;
        }

        // 提取文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            logger.warn("文件名为空");
            return;
        }

        // 获取到用户的ssh连接信息
        SSHConnectInfo sshConnectInfo = webSSHService.getSSHClient(uuid);
        if (sshConnectInfo == null || sshConnectInfo.getWebSocketSession() == null) {
            logger.warn("SSH 连接信息为空或无效");
            return;
        }
        SSHClient sshClient = sshConnectInfo.getSshClient();

        try (InputStream inputStream = file.getInputStream()) {
            sshClient.sendFile(wordDir, inputStream, originalFilename, new FileProgressMonitor(sshConnectInfo.getWebSocketSession(), file.getSize(), this.onOnWebSshPro), null, null);
            // 调出命令行
            sshClient.sendCommand("\r\n");
        } catch (IOException e) {
            logger.error("文件上传失败", e);
            try {
                sshConnectInfo.getWebSocketSession().sendMessage(new TextMessage("文件上传失败: " + e.getMessage() + "\r\n"));
            } catch (IOException e1) {
                logger.error("发送 WebSocket 消息失败", e1);
            }
        }
    }

    @GetMapping("/download")
    public void download(HttpServletResponse httpServletResponse, @RequestParam String uuid, @RequestParam String dirPath, @RequestParam String fileName) {
        SSHConnectInfo sshConnectInfo = webSSHService.getSSHClient(uuid);
        if (sshConnectInfo == null) {
            logger.warn("SSH 连接信息为空或无效");
            // 给个 404
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // 设置文件名
        String encodedFileName;
        encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
        // 设置内容类型（根据文件扩展名设置）
        String contentType = getContentType(fileName);
        httpServletResponse.setContentType(contentType);
        SSHClient sshClient = sshConnectInfo.getSshClient();
        try (ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
            sshClient.downloadFile(dirPath, fileName, outputStream, null,
                    r -> {
                        try {
                            sshConnectInfo.getWebSocketSession().sendMessage(new TextMessage("成功下载: " + fileName + "\r\n"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, e -> {
                        try {
                            sshConnectInfo.getWebSocketSession().sendMessage(new TextMessage("文件下载失败: " + e.getMessage() + "\r\n"));
                        } catch (IOException ex) {
                            logger.error("发送 WebSocket 消息失败", ex);
                        }
                    });
            sshClient.sendCommand("\r\n");
        } catch (IOException e) {
            try {
                sshConnectInfo.getWebSocketSession().sendMessage(new TextMessage("文件下载失败: " + e.getMessage() + "\r\n"));
            } catch (IOException ex) {
                logger.error("发送 WebSocket 消息失败", ex);
            }
        }
    }

    private String getContentType(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            // 文件名没有后缀或后缀为空
            return "application/octet-stream";
        }

        String extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        return switch (extension) {
            case "txt" -> "text/plain";
            case "pdf" -> "application/pdf";
            case "doc", "docx" -> "application/msword";
            case "xls", "xlsx" -> "application/vnd.ms-excel";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            default -> "application/octet-stream";
        };
    }

}
