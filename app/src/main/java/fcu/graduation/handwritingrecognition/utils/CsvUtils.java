package fcu.graduation.handwritingrecognition.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import fcu.graduation.handwritingrecognition.holder.TemplateDataHolder;

public class CsvUtils {

    private final int columnCount = TemplateDataHolder.getInstance().getTableLineCols().length - 1;
    private final int rowCount = TemplateDataHolder.getInstance().getTableLineRows().length - 1;
    public void writeCsv(Context context, ArrayList<String> data, ArrayList<String> columnHeaders, ArrayList<String> rowHeaders, String fileName, int imageCount) {
        new Thread(() -> {
            Uri fileUri = null;
            OutputStream outputStream = null;

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
                    values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                    fileUri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                    if (fileUri != null) {
                        outputStream = context.getContentResolver().openOutputStream(fileUri);
                    }
                } else {
                    // Android 9 以下直接存到 Downloads 資料夾
                    String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName;
                    java.io.File file = new java.io.File(filePath);
                    outputStream = new java.io.FileOutputStream(file);
                }

                if (outputStream != null) {
                    // 加入 UTF-8 BOM 確保中文可以正常顯示
                    outputStream.write(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});

                    for (int i = 0; i < imageCount; i++) {
                        outputStream.write(("id" + i + ",").getBytes(StandardCharsets.UTF_8));
                        for (String header : columnHeaders) {
                            outputStream.write(header.getBytes(StandardCharsets.UTF_8));
                            outputStream.write(",".getBytes(StandardCharsets.UTF_8));
                        }
                        outputStream.write("\n".getBytes(StandardCharsets.UTF_8));

                        for (int j = 0; j < rowCount; j++) {
                            outputStream.write(rowHeaders.get(j).getBytes(StandardCharsets.UTF_8));
                            outputStream.write(",".getBytes(StandardCharsets.UTF_8));

                            for (int k = 0; k < columnCount; k++) {
                                outputStream.write(data.get(i * rowCount * columnCount + j * columnCount + k).getBytes());
                                if (k < columnCount - 1)
                                    outputStream.write(",".getBytes(StandardCharsets.UTF_8));
                            }
                            outputStream.write("\n".getBytes(StandardCharsets.UTF_8));
                        }
                        outputStream.write("\n".getBytes(StandardCharsets.UTF_8));
                    }

                    outputStream.flush();
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
