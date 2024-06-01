# ChatGPT File and Command Center

🚀 **ChatGPT File and Command Center** let ChatGPT manage project files and execute shell commands directly.

🚧 **Note:** This project is still under development. Features and documentation are subject to change.


## 📚 Table of Contents
- [ChatGPT File and Command Center](#chatgpt-file-and-command-center)
  - [📚 Table of Contents](#-table-of-contents)
  - [💡 Introduction](#-introduction)
  - [✨ Features](#-features)
  - [🛠️ Getting Started](#️-getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Configuration](#configuration)
  - [📚 API Endpoints](#-api-endpoints)
    - [File Operations](#file-operations)
    - [Git Operations](#git-operations)
  - [📄 License](#-license)

## 💡 Introduction

Sometime you actually want ChatGPT to manage files and execute commands on your Computer. Most likely when you co-develop a software project with ChatGPT and you no longer want to copy and paste generated files manually into your project and copy compiler errors, exceptions and logs back to ChatGPT. It would be faster and more convinient to just let it do it on its own.

To archive this, ChatGPT File and Command Center supplies an API compatible with ChatGPT Custom Actions. This API lets ChatGPT list, read, write, move, delete files, as well as execute shell commands on your Computer. I also provides a history of all changes based on git, but not conflicting with your own repository.

The idea is that you setup a ChatGPT File and Command Center Project which is a directory with a config.yaml, that contains the workingDir (path to your project files you want ChatGPT to manage) and a descriptions of your system defined by you to assist ChatGPT in understanding your current setup (what tools you installed, etc).

Once the folder and config.yaml are created by you, start the ChatGPT File and Command Center and it will generate an apikey.txt in the ChatGPT File and Command Center Projects folder, which you give ChatGPT to access your System.

*Note:* You need ChatGPT Plus to use this.

## ✨ Features
- **File Operations:** Create, read, update, delete, move, and list files that are on your Computer.
- **File History:** File changes are tracked in a seperate git repository.
- **Multi Project Support:** Supports multiple projects, which you can map to different ChatGPT chats.
- **Security:** API key-based authentication.
- **Command Excecution:** ❌ (not implemented yet) Let ChatGPT execute Shell Commands like `npm run build` on your Computer.


## 🛠️ Getting Started
### Prerequisites
- Java 11 or higher
- Maven

### Installation
1. Clone the repository:
   ~~~bash
   git clone https://github.com/yourusername/chatgpt-file-and-command-center.git
   cd chatgpt-file-and-command-center
   ~~~

2. Build the project:
   ~~~bash
   mvn clean install
   ~~~

3. Run the application:
   ~~~bash
   mvn spring-boot:run
   ~~~

### Configuration
Configuration is managed through the `application.yml` file located in `src/main/resources`. Here is an example configuration:

~~~yaml
logging:
  level:
    root: INFO
    net.grayfallstown: DEBUG

springdoc:
  api-docs:
    path: /v3/api-docs

projectsDir: /path/to/your/projects
~~~

## 📚 API Endpoints
### File Operations
- **Write File:** `POST /api/{apiKey}/files/write`
- **Delete File:** `DELETE /api/{apiKey}/files/delete`
- **Move File:** `POST /api/{apiKey}/files/move`
- **Read File:** `GET /api/{apiKey}/files/read`
- **List Files:** `GET /api/{apiKey}/files/listfiles`
- **Batch Operations:** `POST /api/{apiKey}/files/batch`

### Git Operations
- **Get Git Log:** `GET /api/{apiKey}/git/log`


## 📄 License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

🚧 **Note:** This project is still under development. Features and documentation are subject to change.
