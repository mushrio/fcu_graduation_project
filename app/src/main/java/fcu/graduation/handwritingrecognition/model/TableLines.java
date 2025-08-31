package fcu.graduation.handwritingrecognition.model;

public class TableLines {
    int[] tableLineRows;
    int[] tableLineCols;

    public TableLines(int[] tableLineRows, int[] tableLineCols) {
        this.tableLineRows = tableLineRows;
        this.tableLineCols = tableLineCols;
    }

    public int[] getTableLineRows() {
        return tableLineRows;
    }

    public int[] getTableLineCols() {
        return tableLineCols;
    }
}
