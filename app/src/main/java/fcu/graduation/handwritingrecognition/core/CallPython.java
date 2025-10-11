package fcu.graduation.handwritingrecognition.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.chaquo.python.PyException;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.io.ByteArrayOutputStream;

public class CallPython {
    static final String PYTHON_INTERFACE = "java_python_interface"; // py檔案名
    private static PyObject cachedModule = null;

    // 預載模組：在 app 啟動後盡早呼叫（如 MainActivity onCreate）
    public static void preload() {
        if (cachedModule == null) {
            cachedModule = Python.getInstance().getModule(PYTHON_INTERFACE);
        }
    }

    private static PyObject getModule() {
        if (cachedModule == null) {
            cachedModule = Python.getInstance().getModule(PYTHON_INTERFACE);
        }
        return cachedModule;
    }

    private static byte[] encodePng(Bitmap bitmap){ // 將Bitmap編碼成PNG格式的Byte陣列
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
    private static Bitmap decodePng(byte[] pngBytes){
        return BitmapFactory.decodeByteArray(pngBytes, 0, pngBytes.length);
    }

    public static Bitmap locatePhoneImage(Bitmap phoneImg){
        /*
        定位手機拍攝的空白文件圖片，自動計算完整文件的形狀
        參數：
        phoneImg: 手機直接拍攝的Bitmap圖片
        返回：經過定位處理後的Bitmap圖片
         */
        byte[] pngBytes = encodePng(phoneImg);
        PyObject returnArr = getModule().callAttr("locate_template_image", (Object) pngBytes); // 函式名、參數
        if (returnArr == null) {
            return null;
        }
        byte[] outputPngBytes = returnArr.toJava(byte[].class);
        // 從PNG的Byte陣列解碼成Bitmap
        return decodePng(outputPngBytes);
    }
    public static Bitmap[] getCellImage(Bitmap phoneTargetImg, Bitmap templateImg,
                                        int[] colLines, int[] rowLines,
                                        int modelInputSize){
        /*
        (未測試)
        根據先前偵測到的表格線座標將每個欄位進行切割和處理，輸出每個欄位準備給模型的圖片
        參數：
        phoneTargetImg: 未定位的手寫文件Bitmap圖片
        colLines, rowLines: 從模板偵測到的表格線座標
        templateShape: 模板圖片的形狀，內容為{height, width}
        modelInputSize: 給模型輸入的圖片大小，也就是本函式輸出的圖片大小
        返回：此文件對應框選範圍的表格欄位圖片
         */
        byte[] encodedTarget = encodePng(phoneTargetImg);
        byte[] encodedTemplate = encodePng(templateImg);
        int cellsNum = (colLines.length - 1) * (rowLines.length - 1);
        PyObject listObj = getModule().callAttr("crop_cell_images", encodedTarget, encodedTemplate, colLines, rowLines, modelInputSize);
        Bitmap[] resultBitmaps = new Bitmap[cellsNum];
        int i = 0;
        for(PyObject item : listObj.asList()){
            resultBitmaps[i++] = decodePng(item.toJava(byte[].class));
        }
        return resultBitmaps;
    }

    public static int[][] detectLines(Bitmap templateImg, int x1, int x2, int y1, int y2){
        /*
        偵測一張空白文件在框選範圍內的表格線條
        參數：
        templateImg: 空白圖片(定位後)的bitmap
        x1,x2, y1,y2: 框選的左(x1)、右(x2)、上(y1)、下(y2)座標
        返回：
        二維陣列，第一個維度長度有2，分別代表rows陣列和column陣列
        [0] = rows
        [1] = cols
        兩個第二維度陣列長度可能不同，rows陣列代表每條橫向表格線的y座標，column陣列代表豎向表格線的x座標
         */
        byte[] pngBytes = encodePng(templateImg);
        int[] focusCoord = {x1,x2, y1,y2};
        try {
            // 如果框選一個沒有表格的地方會出現錯誤
            PyObject linesResult = getModule().callAttr("detect_lines", pngBytes, focusCoord);
            PyObject rowsObj = linesResult.asList().get(0);
            PyObject colsObj = linesResult.asList().get(1);
            int[] rows = rowsObj.toJava(int[].class);
            int[] cols = colsObj.toJava(int[].class);
            for(int i=0;i<rows.length;i++) rows[i] += y1;
            for(int i=0;i<cols.length;i++) cols[i] += x1;
            int[][] result = new int[2][];
            result[0] = rows;
            result[1] = cols;
            return result;
        } catch (PyException e) {
            return null;
        }
    }

    public static Bitmap drawTableLines(Bitmap templateImg, int[] colLines, int[] rowLines){
        /*
        給一張圖片和偵測到的表格線，在對應位置用藍色畫出表格線
        參數：
        templateImg: 空白圖片(定位後)的bitmap
        colLines: 直向表格線的橫向座標
        rowLines: 橫向表格線的直向座標
        返回：畫上線條後的Bitmap圖片
         */
        byte[] pngBytes = encodePng(templateImg);
        PyObject imgResult = getModule().callAttr("draw_table_lines", pngBytes, colLines, rowLines);
        byte[] outputPngBytes = imgResult.toJava(byte[].class);
        return decodePng(outputPngBytes);
    }
}
