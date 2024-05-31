package net.grayfallstown.chatgptfileandcommandcenter.domain;

public class File {
    private String path;
    private String content;

    // Getter and Setter methods
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
