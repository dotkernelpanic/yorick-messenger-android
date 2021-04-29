package com.kernelpanic.yorickmessenger.util;


import java.io.File;
import java.text.DecimalFormat;

public class Message {
    String content;
    Long timestamp;
    int type;
    String username;
    File file;
    String filename;
    String filesize;
    String path;

    public Message(String content, Long timestamp, int type,
                   String username, boolean isFile) {
        this.content = content;
        this.timestamp = timestamp;
        this.type = type;
        this.username = username;

        if (isFile) {
            this.path = content;
            this.timestamp = timestamp;
            this.type = type;
            File file = new File(path);
            this.filename = file.getName();
            this.filesize = setStringFileSize(file.length());
        }

    }

    private String setStringFileSize(long filesize) {
        DecimalFormat df = new DecimalFormat("0.0");

        float sizeKB = 1024.0f;
        float sizeMB = sizeKB * sizeKB;
        float sizeGB = sizeMB * sizeKB;

        if (filesize < sizeMB) {
            return df.format(filesize / sizeKB) + " KB";
        } else if (filesize < sizeGB) {
            return df.format(filesize / sizeMB) + " MB";
        }

        return "";
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
