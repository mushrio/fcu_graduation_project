package fcu.graduation.handwritingrecognition.holder;

public class TemplateDataHolder {

    private static TemplateDataHolder instance;

    private String processedTemplateUri;  // 圖片 URI 字串
    private int[] tableLineRows;
    private int[] tableLineCols;

    private TemplateDataHolder() {
        // 私有建構子，防止外部 new
    }

    public static TemplateDataHolder getInstance() {
        if (instance == null) {
            instance = new TemplateDataHolder();
        }
        return instance;
    }

    // Getter 和 Setter for processedTemplateUri
    public void setProcessedTemplateUri(String uri) {
        this.processedTemplateUri = uri;
    }

    public String getProcessedTemplateUri() {
        return this.processedTemplateUri;
    }

    // Getter 和 Setter for tableLineRows
    public void setTableLineRows(int[] rows) {
        this.tableLineRows = rows;
    }

    public int[] getTableLineRows() {
        return this.tableLineRows;
    }

    // Getter 和 Setter for tableLineCols
    public void setTableLineCols(int[] cols) {
        this.tableLineCols = cols;
    }

    public int[] getTableLineCols() {
        return this.tableLineCols;
    }

    // 清除所有資料（可選）
    public void clear() {
        this.processedTemplateUri = null;
        this.tableLineRows = null;
        this.tableLineCols = null;
    }
}