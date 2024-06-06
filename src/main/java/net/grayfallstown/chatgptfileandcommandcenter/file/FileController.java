package net.grayfallstown.chatgptfileandcommandcenter.file;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectConfig;

@RestController
@RequestMapping("/api/{apiKey}/files")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("/batch")
    public ResponseEntity<List<Object>> handleBatchOperations(
            @AuthenticationPrincipal ProjectConfig projectConfig, @RequestBody BatchFileOperationRequest request) {
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
                        responses.add(new FileOperationResponse(operation.getPath(), listContent,
                                "Listed files successfully"));
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
    public ResponseEntity<String> writeFile(
            @AuthenticationPrincipal ProjectConfig projectConfig, @RequestBody FileOperationRequest request) {
        try {
            fileService.writeFile(request.getPath(), request.getContent(), projectConfig);
            return ResponseEntity.ok("'" + request.getPath() + "' written");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("ERROR: " + e.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(
            @AuthenticationPrincipal ProjectConfig projectConfig, @RequestBody FileOperationRequest request) {
        try {
            fileService.deleteFile(request.getPath(), projectConfig);
            return ResponseEntity.ok("'" + request.getPath() + "' deleted");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("ERROR: " + e.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    @PostMapping("/move")
    public ResponseEntity<String> moveFile(
            @AuthenticationPrincipal ProjectConfig projectConfig, @RequestBody FileOperationRequest request) {
        try {
            fileService.moveFile(request.getFrom(), request.getTo(), projectConfig);
            return ResponseEntity.ok("'" + request.getFrom() + "' moved to '" + request.getTo() + "'");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("ERROR: " + e.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    @GetMapping("/read")
    public ResponseEntity<FileOperationResponse> readFile(
            @AuthenticationPrincipal ProjectConfig projectConfig, @RequestParam String path) {
        try {
            String content = fileService.readFile(path, projectConfig);
            return ResponseEntity.ok(new FileOperationResponse(path, content, "Read successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new FileOperationResponse(path, null,
                    "ERROR: " + e.getClass().getSimpleName() + " " + e.getMessage()));
        }
    }

    @GetMapping("/listfiles")
    public ResponseEntity<FileOperationResponse> listFiles(
            @AuthenticationPrincipal ProjectConfig projectConfig,
            @RequestParam String path,
            @RequestParam(required = false) boolean recursive,
            @RequestParam(required = false) boolean ignoreGitIgnore,
            @RequestParam(required = false) boolean foldersOnly) {
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
