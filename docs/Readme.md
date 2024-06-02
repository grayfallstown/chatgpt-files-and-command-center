# Installation Guide

## Required Tools and How to Get Them

### Java

1. Download and install the latest version of Java Development Kit (JDK) from the [Oracle website](https://www.oracle.com/java/technologies/javase-downloads.html).
2. Follow the installation instructions for your operating system.
3. Verify the installation by running `java -version` in your terminal or command prompt.

### Maven

1. Download Apache Maven from the [Maven website](https://maven.apache.org/download.cgi).
2. Follow the installation instructions for your operating system.
3. Verify the installation by running `mvn -version` in your terminal or command prompt.

### Git

1. Download and install Git from the [Git website](https://git-scm.com/downloads).
2. Follow the installation instructions for your operating system.
3. Verify the installation by running `git --version` in your terminal or command prompt.

## Setting Up Projects

### Projects Folder in `application.yml`

1. Define the path to your projects folder in `application.yml`:
    ```yaml
    projectsDir: /path/to/projects
    ```

### `config.yaml` Structure

1. Create a `config.yaml` file in each project's directory with the following structure:
    ```yaml
    workingDir: /path/to/workingDir
    systemDescription: Description of the system
    restrictToWorkingDir: true/false
    executeCommands: true/false
    ```
    - `workingDir`: Path to the working directory. This is where the files you want ChatGPT to manage are located.
    - `systemDescription`: A brief description of your setup, like specific tools you installed and ChatGPT should use.
    - `restrictToWorkingDir`: If true, restricts file api operations to the working directory.
    - `executeCommands`: If true, allows command execution.

### `apikey.txt`

1. Create an `apikey.txt` file in each project's directory containing a UUIDv4 string.
    - You can generate a UUIDv4 using various online tools or libraries.
2. Tell ChatGPT about this API Key once you use it (it will ask).

## Configuring the Application

1. Modify the `application.yml` file to suit your environment and preferences.
2. Ensure the `projectsDir` path is correctly set to the location where your projects are stored.
3. Other configurations can be added as necessary.

## Running the Application

1. Navigate to the project root directory.
2. Run the application using Maven Wrapper:
    ```bash
    ./mvnw spring-boot:run
    ```

## Troubleshooting Guide

- **Java Not Found**: Ensure Java is installed and the `JAVA_HOME` environment variable is set correctly.
- **Maven Not Found**: Ensure Maven is installed and the `MAVEN_HOME` environment variable is set correctly.
- **Git Not Found**: Ensure Git is installed and added to your system's PATH.
- **Configuration Errors**: Double-check the `application.yml` and `config.yaml` files for correct syntax and paths.
- **Port Issues**: Ensure required ports are open and not used by other applications.
- **File Permissions**: Ensure the application has read/write permissions for the necessary directories.
- **Logs**: Check the application logs for detailed error messages and stack traces.

## Make ChatGPT File And Command Center available to ChatGPT

- **[Windows Installation Guide](installation-windows.md)**
- **[macOS Installation Guide](installation-mac.md)**
- **[Ubuntu Installation Guide](installation-ubuntu.md)**
- **[Fedora Installation Guide](installation-fedora.md)**
- **[Arch Linux Installation Guide](installation-archlinux.md)**

## Create a CutomGPT with access to your ChatGPT File And Command Center

**[Custom GPT Setup Guide](installation-customgpt.md)**
