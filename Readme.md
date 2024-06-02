# ChatGPT File And Command Center

🚀 **ChatGPT File And Command Center** let ChatGPT manage project files and execute shell commands directly.

🚧 **Note:** This project is still under development. Features and documentation are subject to change.


## 📚 Table of Contents
- [ChatGPT File And Command Center](#chatgpt-file-and-command-center)
  - [📚 Table of Contents](#-table-of-contents)
  - [💡 Introduction](#-introduction)
  - [✨ Features](#-features)
  - [🛠️ Getting started](#️-getting-started)
  - [Configuration](#configuration)
  - [📚 API Endpoints](#-api-endpoints)
    - [File Operations](#file-operations)
    - [History Operations](#history-operations)
    - [Command Operations](#command-operations)
  - [📄 License](#-license)

## 💡 Introduction

Sometime you actually want ChatGPT to manage files and execute commands on your Computer. Most likely when you co-develop a software project with ChatGPT and you no longer want to copy and paste generated files manually into your project and copy compiler errors, exceptions and logs back to ChatGPT. It would be faster and more convinient to just let it do it on its own.

To archive this, ChatGPT File And Command Center supplies an API compatible with ChatGPT Custom Actions. This API lets ChatGPT list, read, write, move, delete files, as well as execute shell commands on your Computer. It also provides a history of all changes based on git, but not conflicting with your own repository.

The idea is that you setup ChatGPT File And Command Center Projects which know:
- Were the files you want it to manage are located.
- If ChatGPT is restricted to this folder, or can access the entire computers filesystem.
- If ChatGPT is allowed to execute shell Commands.
- The API Key for that particular Project.
- An optional description of your setup for ChatGPT to understand it, eg. what tools are installed.

Once the project folder and config.yaml are created by you, start the ChatGPT File And Command Center and it will generate an apikey.txt in the ChatGPT File And Command Center Projects folder, which you give ChatGPT to access your System.

*Note:* You need ChatGPT Plus to use this.

## ✨ Features
- **File Operations:** Create, read, update, delete, move, and list files that are on your Computer.
- **File History:** File changes are tracked in a seperate git repository.
- **Command Excecution:** Let ChatGPT execute Shell Commands like `npm run build` on your Computer.
- **Multi Project Support:** Supports multiple projects, which you can map to different ChatGPT chats.
- **Security:** API key-based authentication per project.
- **Runs Anywhere:** Linux, Mac, Windows, Rasperry Pi, and whereever else you get Java installed.
- **Not just ChatGPT** This can be used with any LLM that supports OpenAPI Schema.



## 🛠️ Getting started

See the [Installation Guide](./docs/Readme.md)


## Configuration


## 📚 API Endpoints
### File Operations
- **Write File:** `POST /api/{apiKey}/files/write`
- **Delete File:** `DELETE /api/{apiKey}/files/delete`
- **Move File:** `POST /api/{apiKey}/files/move`
- **Read File:** `GET /api/{apiKey}/files/read`
- **List Files:** `GET /api/{apiKey}/files/listfiles`
- **Batch Operations:** `POST /api/{apiKey}/files/batch`

### History Operations
- **Get History Log:** `GET /api/{apiKey}/git/log`

### Command Operations
- **Execute Shell Command:** `POST /api/{apiKey}/command`


## 📄 License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

🚧 **Note:** This project is still under development. Features and documentation are subject to change.
