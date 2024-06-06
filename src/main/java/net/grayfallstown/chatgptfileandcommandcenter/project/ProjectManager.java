package net.grayfallstown.chatgptfileandcommandcenter.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import jakarta.annotation.PostConstruct;

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
                    projectConfigs.put(apiKey, projectConfig);
                } catch (RuntimeException e) {
                    logger.error("Error in project setup: {}", e.getMessage());
                    throw e;
                }
            }
        } else {
            throw new RuntimeException("Projects directory not found: " + projectsDir);
        }
        logger.debug("loaded these projects: {}", projectConfigs.values());
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
        for (Map.Entry<String, ProjectConfig> entry : projectConfigs.entrySet()) {
            if (constantTimeCompare(entry.getKey(), apiKey)) {
                logger.info("opening project {}", entry.getValue().getDir());
                return entry.getValue();
            }
        }
        throw new UnknownProjectException("Cannot find Project, Unknown API key");
    }

    private static void restrictToOwner(Path filePath) {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                ProcessBuilder pb = new ProcessBuilder("icacls", filePath.toString(), "/inheritance:r", "/grant",
                        System.getProperty("user.name") + ":(R,W)");
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
        } catch (Exception e) {
            throw new RuntimeException("could not restrict created apikey.txt to owner", e);
        }

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

    public Set<String> getApiKeys() {
        return projectConfigs.keySet();
    }

}
