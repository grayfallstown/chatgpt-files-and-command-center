package net.grayfallstown.chatgptfileandcommandcenter.web;

import net.grayfallstown.chatgptfileandcommandcenter.git.GitSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private GitSyncService gitSyncService;

    @PostMapping("/batch")
    public ResponseEntity<List<Object>> handleBatchOperations(@RequestBody BatchFileOperationRequest request) {
        List<Object> responses = new ArrayList<>();

        for (FileOperationRequest operation : request.getFileOperations()) {
            try {
                switch (operation.getFileOperation().toLowerCase()) {
                    case "write":
                        fileService.writeFile(operation.getPath(), operation.getContent());
                        responses.add("'" + operation.getPath() + "' written");
                        break;
                    case "delete":
                        fileService.deleteFile(operation.getPath());
                        responses.add("'" + operation.getPath() + "' deleted");
                        break;
                    case "move":
                        fileService.moveFile(operation.getFrom(), operation.getTo());
                        responses.add("'" + operation.getFrom() + "' moved to '" + operation.getTo() + "'");
                        break;
                    case "read":
                        String content = fileService.readFile(operation.getPath());
                        responses.add(new FileOperationResponse(operation.getPath(), content, "Read successfully"));
                        break;
                    case "listfiles":
                        String listContent = fileService.listFiles(operation.getPath(), operation.isRecursive(), operation.isIgnoreGitIgnore());
                        responses.add(new FileOperationResponse(operation.getPath(), listContent, "Listed files successfully"));
                        break;
                    default:
                        responses.add("ERROR: Unknown operation '" + operation.getFileOperation() + "'");
                }
            } catch (Exception e) {
                responses.add("ERROR: "+ e.getClass().getSimpleName() + " " + e.getMessage());
            }
        }

        try {
            gitSyncService.addAllAndCommit(request.getCommitMessage());
        } catch (Exception e) {
            responses.add("ERROR: Commit failed - "+ e.getClass().getSimpleName() + " " + e.getMessage());
        }

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/write")
    public ResponseEntity<String> writeFile(@RequestBody FileOperationRequest request) {
        try {
            fileService.writeFile(request.getPath(), request.getContent());
            gitSyncService.addAllAndCommit(request.getCommitMessage());
            return ResponseEntity.ok("'" + request.getPath() + "' written");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("ERROR: "+ e.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestBody FileOperationRequest request) {
        try {
            fileService.deleteFile(request.getPath());
            gitSyncService.addAllAndCommit(request.getCommitMessage());
            return ResponseEntity.ok("'" + request.getPath() + "' deleted");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("ERROR: "+ e.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    @PostMapping("/move")
    public ResponseEntity<String> moveFile(@RequestBody FileOperationRequest request) {
        try {
            fileService.moveFile(request.getFrom(), request.getTo());
            gitSyncService.addAllAndCommit(request.getCommitMessage());
            return ResponseEntity.ok("'" + request.getFrom() + "' moved to '" + request.getTo() + "'");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("ERROR: "+ e.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    @GetMapping("/read")
    public ResponseEntity<FileOperationResponse> readFile(@RequestParam String path) {
        try {
            String content = fileService.readFile(path);
            return ResponseEntity.ok(new FileOperationResponse(path, content, "Read successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new FileOperationResponse(path, null, "ERROR: " + e.getClass().getSimpleName() + " " + e.getMessage()));
        }
    }

    @GetMapping("/listfiles")
    public ResponseEntity<FileOperationResponse> listFiles(@RequestParam String path, @RequestParam boolean recursive, @RequestParam boolean ignoreGitIgnore) {
        try {
            String content = fileService.listFiles(path, recursive, ignoreGitIgnore);
            return ResponseEntity.ok(new FileOperationResponse(path, content, "Listed files successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new FileOperationResponse(path, null, "ERROR: "+ e.getClass().getSimpleName() + " " + e.getMessage()));
        }
    }
}
