package fcu.graduation.handwritingrecognition;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Arrays;

import fcu.graduation.handwritingrecognition.holder.TemplateDataHolder;

public class IdentifyingTemplate extends AppCompatActivity {

    private ProgressBar loadingCircle;

    private String imageUriString;

    private Intent intent;
    private int[] coordinates = new int[4];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identifying_template);

        loadingCircle = findViewById(R.id.pb_loading_template);
        loadingCircle.setVisibility(View.VISIBLE);

        imageUriString = getIntent().getStringExtra("image_uri");

        coordinates = getIntent().getIntArrayExtra("coordinates");

        intent = new Intent(this, SelectIdentifyModel.class);
        intent.putExtra("image_uri", imageUriString);

        new Thread(() -> {
            Bitmap bitmap = TemplateDataHolder.getInstance().getProcessedTemplate();

            runOnUiThread(() -> {
                identifyTemplate(bitmap);
            });
        }).start();
    }

    void saveImage(Bitmap image){
        // 存到相簿
        try {
            // 建議放背景執行緒，避免 UI 卡頓
            Uri saved = ImageSaver.saveBitmapToGallery(
                    this,                // Activity 實例
                    image,     // 你處理過的 Bitmap
                    "processed_" + System.currentTimeMillis());

            runOnUiThread(() -> {
                Toast.makeText(this,
                        "已存到相簿：" + saved,
                        Toast.LENGTH_SHORT).show();

                intent.putExtra("processed_template", saved.toString());
                TemplateDataHolder.getInstance().setTableLineRows(tableLineRows);
                TemplateDataHolder.getInstance().setTableLineCols(tableLineCols);
                Log.d("DebugTable", "tableLineRows: " + Arrays.toString(tableLineRows));
                Log.d("DebugTable", "tableLineCols: " + Arrays.toString(tableLineCols));
                startActivity(intent);
                finish();
            });




        } catch (IOException e) {
            Log.e("SaveBitmap", "寫入失敗", e);
            runOnUiThread(() ->
                    Toast.makeText(this,
                            "圖片儲存失敗：" + e.getMessage(),
                            Toast.LENGTH_LONG).show());
        }
    }

    Bitmap templateBitmap=null;
    int[] tableLineRows=null;
    int[] tableLineCols=null;
    @SuppressLint("SetTextI18n") // 當寫下TextView.setText("some strings")時，被要求加上去的
    public void identifyTemplate(Bitmap bitmap){
        TextView status = findViewById(R.id.tv_loading_text);
        new Thread(() -> {
            runOnUiThread(() -> status.setText("線段偵測中..."));
            int[][] tableLines = CallPython.detectLines(bitmap, coordinates[0], coordinates[1], coordinates[2], coordinates[3]); // 使用者框選的範圍，這裡是自己手打的座標:606, 3194, 1568, 2297
            int[] rows = tableLines[0];
            int[] cols = tableLines[1];
            tableLineRows = rows;
            tableLineCols = cols;
            templateBitmap = bitmap;
            runOnUiThread(() -> status.setText("模板畫線中..."));
            Bitmap drawnBitmap = CallPython.drawTableLines(bitmap, cols, rows);
            saveImage(drawnBitmap);
        }).start();
    }
}