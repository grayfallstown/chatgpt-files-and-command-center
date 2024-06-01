package net.grayfallstown.chatgptfileandcommandcenter.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProjectManager {

    @Value("${projectsDir}")
    private String projectsDir;

    private Map<String, ProjectConfig> projectConfigs;

    @PostConstruct
    public void loadProjects() {
        projectConfigs = new HashMap<>();
        File projectsFolder = new File(projectsDir);
        if (projectsFolder.exists() && projectsFolder.isDirectory()) {
            for (File projectDir : projectsFolder.listFiles(File::isDirectory)) {
                String apiKey = loadApiKeyFromProjectDir(projectDir);
                ProjectConfig projectConfig = loadProjectConfig(projectDir);
                String apiKeyHash = hashApiKey(apiKey);
                projectConfigs.put(apiKeyHash, projectConfig);
            }
        } else {
            throw new RuntimeException("Projects directory not found: " + projectsDir);
        }
    }

    private String loadApiKeyFromProjectDir(File projectDir) {
        try {
            String apiKeyPath = Paths.get(projectDir.getPath(), "apikey.txt").toString();
            return Files.readString(Paths.get(apiKeyPath)).trim();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read API key from project directory: " + projectDir, e);
        }
    }

    private ProjectConfig loadProjectConfig(File projectDir) {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Map<String, Object> configData = mapper.readValue(new File(projectDir, "config.yaml"), Map.class);
            ProjectConfig projectConfig = new ProjectConfig();
            projectConfig.setDir(projectDir.getAbsolutePath());
            projectConfig.setWorkingDir((String) configData.get("workingDir"));
            return projectConfig;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load project configuration from project directory: " + projectDir, e);
        }
    }

    public ProjectConfig getProjectConfig(String apiKey) {
        // some resistance against timing attacks
        String apiKeyHash = hashApiKey(apiKey);
        for (Map.Entry<String, ProjectConfig> entry : projectConfigs.entrySet()) {
            if (constantTimeCompare(entry.getKey(), apiKeyHash)) {
                return entry.getValue();
            }
        }
        throw new UnknownProjectException("Cannot find Project, Unknown API key");
    }

    private String hashApiKey(String apiKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(apiKey.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash API key", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

     private boolean constantTimeCompare(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
