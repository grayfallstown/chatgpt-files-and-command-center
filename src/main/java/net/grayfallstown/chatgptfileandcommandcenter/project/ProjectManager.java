package net.grayfallstown.chatgptfileandcommandcenter.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class ProjectManager {

    private static final Logger logger = LoggerFactory.getLogger(ProjectManager.class);

    @Value("${projectsDir}")
    private String projectsDir;

    private Map<String, ProjectConfig> projectConfigs;

    @PostConstruct
    public void loadProjects() {
        projectConfigs = new HashMap<>();
        File projectsFolder = new File(projectsDir);
        if (projectsFolder.exists() && projectsFolder.isDirectory()) {
            for (File projectDir : projectsFolder.listFiles(File::isDirectory)) {
                try {
                    String apiKey = loadApiKeyFromProjectDir(projectDir);
                    ProjectConfig projectConfig = loadProjectConfig(projectDir);
                    String apiKeyHash = hashApiKey(apiKey);
                    projectConfigs.put(apiKeyHash, projectConfig);
                } catch (RuntimeException e) {
                    logger.error("Error in project setup: {}", e.getMessage());
                    throw e;
                }
            }
        } else {
            throw new RuntimeException("Projects directory not found: " + projectsDir);
        }
    }

    private String loadApiKeyFromProjectDir(File projectDir) {
        try {
            Path apiKeyPath = Paths.get(projectDir.getPath(), "apikey.txt");
            if (!Files.exists(apiKeyPath)) {
                logger.info("creating missing api key for project {}", apiKeyPath.toString());
                Files.write(apiKeyPath, UUID.randomUUID().toString().getBytes());
                restrictToOwner(apiKeyPath);
            }
            return Files.readString(apiKeyPath).trim();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read API key from project directory: " + projectDir, e);
        }
    }

    private ProjectConfig loadProjectConfig(File projectDir) {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            ProjectConfig projectConfig = mapper.readValue(new File(projectDir, "config.yaml"), ProjectConfig.class);
            projectConfig.setDir(projectDir.getAbsolutePath());
            projectConfig.validateConfiguration();
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

    private static void restrictToOwner(Path filePath) {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                ProcessBuilder pb = new ProcessBuilder("icacls", filePath.toString(), "/inheritance:r", "/grant", System.getProperty("user.name") + ":(R,W)");
                Process p = pb.start();
                p.waitFor();
            } else {
                // Define the file permissions to restrict access to the owner only
                Set<PosixFilePermission> permissions = new HashSet<>();
                permissions.add(PosixFilePermission.OWNER_READ);
                permissions.add(PosixFilePermission.OWNER_WRITE);
    
                // Set the file permissions
                Files.setPosixFilePermissions(filePath, permissions);
            }
        } catch(Exception e) {
            throw new RuntimeException("could not restrict created apikey.txt to owner", e);
        }
        
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
