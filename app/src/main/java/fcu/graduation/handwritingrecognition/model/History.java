package fcu.graduation.handwritingrecognition.model;

import java.util.List;

public class History {
    List<TableLines> tableLines;
    String clippedTemplate;
    long timestamp;

    public History(List<TableLines> tableLines, String clippedTemplate, long timestamp) {
        this.tableLines = tableLines;
        this.clippedTemplate = clippedTemplate;
        this.timestamp = timestamp;
    }

    public List<TableLines> getTableLines() {
        return tableLines;
    }

    public String getClippedTemplate() {
        return clippedTemplate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
