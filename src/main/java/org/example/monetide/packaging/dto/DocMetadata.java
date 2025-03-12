package org.example.monetide.packaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocMetadata {
    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("page_label")
    private String pageLabel;

    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPageLabel() {
        return pageLabel;
    }

    public void setPageLabel(String pageLabel) {
        this.pageLabel = pageLabel;
    }
}
