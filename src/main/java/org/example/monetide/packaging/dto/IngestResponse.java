package org.example.monetide.packaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IngestResponse {
    private String object;
    private String model;
    private List<DocumentData> data;

    // Getters and Setters
    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<DocumentData> getData() {
        return data;
    }

    public void setData(List<DocumentData> data) {
        this.data = data;
    }
}
