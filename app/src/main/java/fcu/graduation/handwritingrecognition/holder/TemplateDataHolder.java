package fcu.graduation.handwritingrecognition.holder;

import android.graphics.Bitmap;

public class TemplateDataHolder {

    private static TemplateDataHolder instance;

    private Bitmap processedTemplate;  // 剪裁後的圖片
    private Bitmap drawnTemplate;  // 畫線後的圖片
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
    public void setProcessedTemplate(Bitmap processedTemplate) {
        this.processedTemplate = processedTemplate;
    }

    public Bitmap getProcessedTemplate() {
        return this.processedTemplate;
    }

    public void setDrawnTemplate(Bitmap drawnTemplate) {
        this.drawnTemplate = drawnTemplate;
    }

    public Bitmap getDrawnTemplate() {
        return this.drawnTemplate;
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
        this.processedTemplate = null;
        this.tableLineRows = null;
        this.tableLineCols = null;
    }
}