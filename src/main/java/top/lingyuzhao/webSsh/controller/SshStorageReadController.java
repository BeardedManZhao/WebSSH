package top.lingyuzhao.webSsh.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import top.lingyuzhao.webSsh.config.OnOnWebSshPro;
import top.lingyuzhao.webSsh.config.StorageConfig;
import top.lingyuzhao.webSsh.pojo.UrlNameKv;
import top.lingyuzhao.webSsh.pojo.WebSSHData;
import top.lingyuzhao.webSsh.pojo.WebSSHDataList;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

/**
 * 读取存储的 ssh 连接信息
 *
 * @author 赵凌宇
 */
@RestController
@RequestMapping("/api/storage")
public class SshStorageReadController {

    private final static Base64.Encoder encoder = Base64.getEncoder();
    private final StorageConfig storageConfig;
    private final ObjectMapper objectMapper;

    @Autowired
    public SshStorageReadController(OnOnWebSshPro onOnWebSshPro, ObjectMapper objectMapper) {
        this.storageConfig = onOnWebSshPro.getStorageConfig();
        // 初始化 objectMapper
        WebSSHDataList webSSHDataList = this.storageConfig.getWebSSHDataList(objectMapper);
        Logger logger = org.slf4j.LoggerFactory.getLogger(SshStorageReadController.class);
        logger.info("加载到 {} 个已知的 ssh 信息项目!", webSSHDataList.size());
        this.objectMapper = objectMapper;
    }

    /**
     * @return 当前所有可以被直接读取的 控制器
     */
    @GetMapping("/seeSsh")
    @ResponseBody
    public ArrayList<UrlNameKv> toUrl() {
        WebSSHDataList webSSHDataList = this.storageConfig.getWebSSHDataList(objectMapper);
        ArrayList<UrlNameKv> arrayList = new ArrayList<>(webSSHDataList.size());
        for (WebSSHData webSSHData : webSSHDataList) {
            arrayList.add(new UrlNameKv(buildRedirectUrl(webSSHData), webSSHData.toString()));
        }
        return arrayList;
    }

    /**
     * 删除指定名称的 WebSSH 数据
     *
     * @param name 要删除的 WebSSH 数据的名称
     * @return 重定向到 /seeSsh.html
     */
    @GetMapping("/remove")
    public RedirectView remove(@RequestParam String name) {
        this.storageConfig.getWebSSHDataList(objectMapper).remove(name);
        // 重定向到 /seeSsh.html
        return new RedirectView("/seeSsh.html");
    }

    private String buildRedirectUrl(WebSSHData webSSHData) {
        return "/webssh.html?" + "host=" + webSSHData.getHost() + "&" +
                "port=" + webSSHData.getPort() + "&" +
                "username=" + webSSHData.getUsername() + "&" +
                "password=" + encoder.encodeToString(webSSHData.getPassword().getBytes(StandardCharsets.UTF_8)) + "#noUrlCode";
    }

}
