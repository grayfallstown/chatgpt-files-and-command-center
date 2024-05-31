package net.grayfallstown.chatgptfileandcommandcenter.service;

import net.grayfallstown.chatgptfileandcommandcenter.domain.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BatchService {

    public List<File> processBatch(Map<String, Object> batchRequest) {
        List<Map<String, Object>> fileOperations = (List<Map<String, Object>>) batchRequest.get("FileOperations");
        List<File> results = new ArrayList<>();

        for (Map<String, Object> operation : fileOperations) {
            String fileOperation = (String) operation.get("FileOperation");
            try {
                switch (fileOperation.toLowerCase()) {
                    case "write":
                        String writePath = (String) operation.get("Path");
                        String content = (String) operation.get("Content");
                        WriteFileOperation writeOp = new WriteFileOperation(writePath, content);
                        writeOp.execute();
                        results.add(writeOp.getFile());
                        break;
                    case "delete":
                        String deletePath = (String) operation.get("Path");
                        DeleteFileOperation deleteOp = new DeleteFileOperation(deletePath);
                        deleteOp.execute();
                        break;
                    case "move":
                        String from = (String) operation.get("From");
                        String to = (String) operation.get("To");
                        MoveFileOperation moveOp = new MoveFileOperation(from, to);
                        moveOp.execute();
                        break;
                    case "read":
                        String readPath = (String) operation.get("Path");
                        ReadFileOperation readOp = new ReadFileOperation(readPath);
                        readOp.execute();
                        results.add(readOp.getFile());
                        break;
                }
            } catch (IOException e) {
                // Log error and continue processing the next operation
                System.err.println("Error processing operation: " + e.getMessage());
            }
        }

        return results;
    }
}
