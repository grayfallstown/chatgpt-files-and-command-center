package net.grayfallstown.chatgptfileandcommandcenter.file;

import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectConfig;
import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/{apiKey}/files")
public class FileController {
    @Autowired
    private FileService fileService;
    @Autowired
    private ProjectManager projectManager;

    @PostMapping("/batch")
    public ResponseEntity<List<Object>> handleBatchOperations(@PathVariable String apiKey, @RequestBody BatchFileOperationRequest request) {
        ProjectConfig projectConfig = projectManager.getProjectConfig(apiKey);
        List<Object> responses = new ArrayList<>();
        for (FileOperationRequest operation : request.getFileOperations()) {
            try {
                switch (operation.getFileOperation().toLowerCase()) {
                    case "write":
                        fileService.writeFile(operation.getPath(), operation.getContent(), projectConfig);
                        responses.add("'" + operation.getPath() + "' written");
                        break;
                    case "delete":
                        fileService.deleteFile(operation.getPath(), projectConfig);
                        responses.add("'" + operation.getPath() + "' deleted");
                        break;
                    case "move":
                        fileService.moveFile(operation.getFrom(), operation.getTo(), projectConfig);
                        responses.add("'" + operation.getFrom() + "' moved to '" + operation.getTo() + "'");
                        break;
                    case "read":
                        String content = fileService.readFile(operation.getPath(), projectConfig);
                        responses.add(new FileOperationResponse(operation.getPath(), content, "Read successfully"));
                        break;
                    case "listfiles":
                        String listContent = fileService.listFiles(operation.getPath(),
                            operation.isRecursive(), operation.isIgnoreGitIgnore(),
                            operation.isFoldersOnly(), projectConfig);
                        responses.add(new FileOperationResponse(operation.getPath(), listContent, "Listed files successfully"));
                        break;
                    default:
                        responses.add("ERROR: Unknown operation '" + operation.getFileOperation() + "'");
                }
            } catch (Exception e) {
                responses.add("ERROR: " + e.getClass().getSimpleName() + " " + e.getMessage());
            }
        }
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/write")
    public ResponseEntity<String> writeFile(@PathVariable String apiKey, @RequestBody FileOperationRequest request) {
        ProjectConfig projectConfig = projectManager.getProjectConfig(apiKey);
        try {
            fileService.writeFile(request.getPath(), request.getContent(), projectConfig);
            return ResponseEntity.ok("'" + request.getPath() + "' written");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("ERROR: " + e.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@PathVariable String apiKey, @RequestBody FileOperationRequest request) {
        ProjectConfig projectConfig = projectManager.getProjectConfig(apiKey);
        try {
            fileService.deleteFile(request.getPath(), projectConfig);
            return ResponseEntity.ok("'" + request.getPath() + "' deleted");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("ERROR: " + e.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    @PostMapping("/move")
    public ResponseEntity<String> moveFile(@PathVariable String apiKey, @RequestBody FileOperationRequest request) {
        ProjectConfig projectConfig = projectManager.getProjectConfig(apiKey);
        try {
            fileService.moveFile(request.getFrom(), request.getTo(), projectConfig);
            return ResponseEntity.ok("'" + request.getFrom() + "' moved to '" + request.getTo() + "'");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("ERROR: " + e.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    @GetMapping("/read")
    public ResponseEntity<FileOperationResponse> readFile(@PathVariable String apiKey, @RequestParam String path) {
        ProjectConfig projectConfig = projectManager.getProjectConfig(apiKey);
        try {
            String content = fileService.readFile(path, projectConfig);
            return ResponseEntity.ok(new FileOperationResponse(path, content, "Read successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new FileOperationResponse(path, null, "ERROR: " + e.getClass().getSimpleName() + " " + e.getMessage()));
        }
    }

    @GetMapping("/listfiles")
    public ResponseEntity<FileOperationResponse> listFiles(
        @PathVariable String apiKey,
        @RequestParam String path,
        @RequestParam(required = false) boolean recursive,
        @RequestParam(required = false) boolean ignoreGitIgnore,
        @RequestParam(required = false) boolean foldersOnly
    ) {
        ProjectConfig projectConfig = projectManager.getProjectConfig(apiKey);
        try {
            String content = fileService.listFiles(path, recursive, ignoreGitIgnore, foldersOnly, projectConfig);
            return ResponseEntity.ok(new FileOperationResponse(path, content, "Listed files successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                new FileOperationResponse(path, null,
                    "ERROR: " + e.getClass().getSimpleName() + " " + e.getMessage()));
        }
    }
}
