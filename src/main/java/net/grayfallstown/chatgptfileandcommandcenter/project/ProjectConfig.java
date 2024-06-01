package net.grayfallstown.chatgptfileandcommandcenter.project;

import lombok.Data;

import java.io.File;

@Data
public class ProjectConfig {
    private String dir;
    private String workingDir;
    private String systemDescription;

    public void setWorkingDir(String workingDir) {
        validateDirectoryExists(workingDir, "Working directory");
        this.workingDir = workingDir;
    }

    public void setDir(String dir) {
        validateDirectoryExists(dir, "Project directory");
        this.dir = dir;
    }

    private void validateDirectoryExists(String dirPath, String dirName) {
        if (dirPath == null) {
            throw new IllegalArgumentException(dirName + " is not set.");
        }
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException(dirName + " does not exist or is not a directory: " + dirPath);
        }
    }

    public void validateConfiguration() {
        System.out.println("Validating configuration:");
        System.out.println("Project directory: " + this.dir);
        System.out.println("Working directory: " + this.workingDir);
        validateDirectoryExists(this.dir, "Project directory");
        validateDirectoryExists(this.workingDir, "Working directory");
    }
}
