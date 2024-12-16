package top.lingyuzhao.webSsh.constant;

import com.jcraft.jsch.SftpProgressMonitor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import top.lingyuzhao.utils.ConsoleColor;
import top.lingyuzhao.webSsh.config.OnOnWebSshPro;

import java.io.IOException;

/**
 * 文件上传进度监控器
 *
 * @author lingyuzhao
 */
public class FileProgressMonitor implements SftpProgressMonitor {

    private static final top.lingyuzhao.utils.transformation.ManyTrans<String, String> stringManyTrans = (inputType1, inputType2) -> "文件《" + inputType1 + "》正在上传，进度：" + inputType2;
    private final WebSocketSession session;
    private final long max;
    private final long UPDATE_THRESHOLD;
    private String dest;
    private long accumulatedCount = 0;
    private long allAccumulatedCount = 0;

    public FileProgressMonitor(WebSocketSession session, long max, OnOnWebSshPro onOnWebSshPro) {
        this.session = session;
        this.max = max;
        UPDATE_THRESHOLD = onOnWebSshPro.getFileProgressMonitorConfig().getUpdateThreshold();
    }

    @Override
    public void init(int op, String src, String dest, long max) {
        this.dest = dest;
        if (max == -1) {
            try {
                session.sendMessage(new TextMessage(ConsoleColor.ANSI_GREEN + " 文件《" + dest + "》正在上传...\r\n" + ConsoleColor.ANSI_RESET));
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public boolean count(long count) {
        accumulatedCount += count;
        allAccumulatedCount += count;
        if (max != -1 && accumulatedCount >= UPDATE_THRESHOLD) {
            try {
                double progress = ((double) allAccumulatedCount / max) * 100;
                session.sendMessage(new TextMessage(stringManyTrans.function(dest, String.format("%.2f%%...\r\n", progress))));
                accumulatedCount = 0; // 重置累积计数
            } catch (IOException ignored) {

            }
        }
        return true;
    }

    @Override
    public void end() {
        try {
            session.sendMessage(new TextMessage(ConsoleColor.ANSI_GREEN + " 文件《" + dest + "》上传完成！\r\n" + ConsoleColor.ANSI_RESET));
        } catch (IOException ignored) {
        }
    }
}