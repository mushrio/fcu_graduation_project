package fcu.graduation.handwritingrecognition.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.util.ArrayList;

import fcu.graduation.handwritingrecognition.holder.TemplateDataHolder;

public class ExcelUtils {
    private final int columnCount = TemplateDataHolder.getInstance().getTableLineCols().length - 1;
    private final int rowCount = TemplateDataHolder.getInstance().getTableLineRows().length - 1;

    public void writeExcel(Context context, ArrayList<String> data, ArrayList<String> columnHeaders, ArrayList<String> rowHeaders, String fileName, int imageCount) {
        new Thread(() -> {
            Uri fileUri = null;
            OutputStream outputStream = null;

            try {
                // 判斷 Android 版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Downloads.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                    values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                    fileUri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                    if (fileUri != null) {
                        outputStream = context.getContentResolver().openOutputStream(fileUri);
                    }
                } else {
                    // Android 9 以下直接存到 Downloads
                    String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .getAbsolutePath() + "/" + fileName;
                    java.io.File file = new java.io.File(filePath);
                    outputStream = new java.io.FileOutputStream(file);
                }

                if (outputStream != null) {
                    // 使用 XSSFWorkbook 寫入 Excel
                    XSSFWorkbook workbook = new XSSFWorkbook();
                    for (int i = 0; i < imageCount; i++) {
                        Sheet sheet = workbook.createSheet("Image_" + i);

                        // 寫入標題列
                        Row headerRow = sheet.createRow(0);
                        int colIndex = 0;
                        headerRow.createCell(colIndex++).setCellValue("id" + i);
                        for (String colHeader : columnHeaders) {
                            headerRow.createCell(colIndex++).setCellValue(colHeader);
                        }

                        // 寫入每一行資料
                        for (int j = 0; j < rowCount; j++) {
                            Row row = sheet.createRow(j + 1);
                            colIndex = 0;
                            row.createCell(colIndex++).setCellValue(rowHeaders.get(j));

                            for (int k = 0; k < columnCount; k++) {
                                int dataIndex = i * rowCount * columnCount + j * columnCount + k;
                                row.createCell(colIndex++).setCellValue(data.get(dataIndex));
                            }
                        }
                        sheet.setColumnWidth(0, 20 * 256);
                    }

                    workbook.write(outputStream);
                    workbook.close();
                    outputStream.flush();
                    outputStream.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
