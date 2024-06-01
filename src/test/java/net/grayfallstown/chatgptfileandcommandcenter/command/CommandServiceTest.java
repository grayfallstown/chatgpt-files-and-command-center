package net.grayfallstown.chatgptfileandcommandcenter.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

class CommandServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(CommandServiceTest.class);

    private CommandService commandService;

    @BeforeEach
    void setUp() {
        commandService = new CommandService();
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testExecuteCommandWindows() throws Exception {
        runWindowsTestSet();
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void testExecuteCommandLinux() throws Exception {
        runUnixTestSet();
    }

    @Test
    @EnabledOnOs(OS.MAC)
    void testExecuteCommandMac() throws Exception {
        runUnixTestSet();
    }

    void runWindowsTestSet() throws Exception {
        String command = "echo Hello, World!";
        String shell = "cmd";
        String workingDir = System.getProperty("user.dir");
        int timeout = 10;

        OutputStream out = new ByteArrayOutputStream();
        commandService.executeShellCommand(shell, command, workingDir, timeout, out);
        String result = out.toString().trim();

        assertTrue(result.contains("Hello, World!"), "Expected output to contain 'Hello, World!' result: '" + result + "");
    }

    void runUnixTestSet() throws Exception {
        String command = "echo Hello, World!";
        String shell = "bash";
        String workingDir = System.getProperty("user.dir");
        int timeout = 10;

        OutputStream out = new ByteArrayOutputStream();
        commandService.executeShellCommand(shell, command, workingDir, timeout, out);
        String result = out.toString().trim();

        assertTrue(result.contains("Hello, World!"), "Expected output to contain 'Hello, World!'");
    }

    @Test
    void testValidateShell() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            assertEquals("cmd.exe", commandService.validateShell("cmd"));
        } else {
            assertEquals("/bin/bash", commandService.validateShell("bash"));
        }
    }

    @Test
    void testSanitizeCommand() {
        String command = "echo Hello $USER";
        String sanitizedCommand = commandService.sanitizeCommand(command);
        assertEquals("echo Hello $USER", sanitizedCommand);
    }
}
