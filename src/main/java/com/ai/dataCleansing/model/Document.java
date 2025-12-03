package com.ai.dataCleansing.model;

import java.util.Date;
import java.util.UUID;

public class Document {
    private String id;
    private String content;
    private String source;
    private Date uploadDate;

    // 构造函数
    public Document(String content) {
        this.id = UUID.randomUUID().toString();
        this.content = content;
        this.uploadDate = new Date();
    }

    // Getter和Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Date getUploadDate() { return uploadDate; }
    public void setUploadDate(Date uploadDate) { this.uploadDate = uploadDate; }
}
