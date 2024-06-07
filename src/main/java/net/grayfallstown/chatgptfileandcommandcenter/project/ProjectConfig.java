package net.grayfallstown.chatgptfileandcommandcenter.project;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Paths;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import lombok.Data;
import net.grayfallstown.chatgptfileandcommandcenter.common.ValidationException;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectConfig implements Serializable {

    @JsonProperty("dir")
    private String dir;

    @JsonProperty("workingDir")
    private String workingDir;

    @JsonProperty("systemDescription")
    private String systemDescription;

    @JsonProperty("restrictToWorkingDir")
    private boolean restrictToWorkingDir;

    @JsonProperty("executeCommands")
    private boolean executeCommands;

    public ProjectConfig() {
        // Default constructor for Jackson
    }

    @JsonSetter(nulls = Nulls.SKIP)
    public void setExecuteCommands(boolean executeCommands) {
        this.executeCommands = executeCommands;
    }

    @JsonSetter(nulls = Nulls.SKIP)
    public void setRestrictToWorkingDir(boolean restrictToWorkingDir) {
        this.restrictToWorkingDir = restrictToWorkingDir;
    }

    public void setWorkingDir(String workingDir) {
        validateDirectoryExists(workingDir, "Working directory");
        this.workingDir = Paths.get(workingDir).normalize().toString();
    }

    public void setDir(String dir) {
        validateDirectoryExists(dir, "Project directory");
        this.dir = Paths.get(dir).normalize().toString();
    }

    private void validateDirectoryExists(String dirPath, String dirName) {
        if (dirPath == null) {
            throw new ValidationException(dirName + " is not set.");
        }
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new ValidationException(dirName + " does not exist or is not a directory: " +
                    dirPath);
        }
    }

    public void validateConfiguration() {
        validateDirectoryExists(this.dir, "Project directory");
        validateDirectoryExists(this.workingDir, "Working directory");
        if (executeCommands && restrictToWorkingDir) {
            throw new ValidationException("Project '" + dir +
                    "' has executeCommands true, but also restrictedToWorkingDir, which is not allowed.");
        }
    }
}
