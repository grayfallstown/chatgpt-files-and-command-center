package net.grayfallstown.chatgptfileandcommandcenter.system;

import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectConfig;
import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/{apiKey}/sysinfo")
public class SystemInfoController {

    private static final Logger logger = LoggerFactory.getLogger(SystemInfoController.class);

    @Autowired
    private ProjectManager projectManager;

    @GetMapping
    public Map<String, Object> getSystemInfo(@PathVariable String apiKey) {
        ProjectConfig projectConfig = projectManager.getProjectConfig(apiKey);

        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("systemDescription", projectConfig.getSystemDescription());

        String osName = System.getProperty("os.name").toLowerCase();
        String osVersion = System.getProperty("os.version");

        systemInfo.put("os", osName);
        systemInfo.put("version", osVersion);

        List<String> availableShells = new ArrayList<>();
        if (osName.contains("win")) {
            systemInfo.put("distribution", System.getProperty("os.name"));
            systemInfo.put("build", System.getProperty("os.version"));
            systemInfo.put("kernel", System.getProperty("os.arch"));
            availableShells.add("cmd");
            availableShells.add("powershell");
        } else {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("uname", "-a");
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String kernelInfo = reader.readLine();
                systemInfo.put("kernel", kernelInfo);

                processBuilder.command("lsb_release", "-a");
                process = processBuilder.start();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("Description:")) {
                        systemInfo.put("distribution", line.split(":")[1].trim());
                    }
                }

                // Check for available shells
                if (new java.io.File("/bin/bash").exists()) {
                    availableShells.add("/bin/bash");
                }
                if (new java.io.File("/bin/sh").exists()) {
                    availableShells.add("/bin/sh");
                }
                if (new java.io.File("/usr/bin/zsh").exists()) {
                    availableShells.add("/usr/bin/zsh");
                }
            } catch (Exception e) {
                systemInfo.put("error", "Unable to determine distribution or kernel info: " + e.getMessage());
            }
        }

        systemInfo.put("availableShells", availableShells);
        logger.info("Returning system info: {}", systemInfo);
        return systemInfo;
    }
}
