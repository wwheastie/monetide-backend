package org.example.monetide.packaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentData {
    private String object;

    @JsonProperty("doc_id")
    private String docId;

    @JsonProperty("doc_metadata")
    private DocMetadata docMetadata;

    // Getters and Setters
    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public DocMetadata getDocMetadata() {
        return docMetadata;
    }

    public void setDocMetadata(DocMetadata docMetadata) {
        this.docMetadata = docMetadata;
    }
}
