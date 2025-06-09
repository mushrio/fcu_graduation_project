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

public class IdentifyingTemplate extends AppCompatActivity {

    private ProgressBar loadingCircle;

    private String imageUriString;

    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identifying_template);

        loadingCircle = findViewById(R.id.pb_loading_template);
        loadingCircle.setVisibility(View.VISIBLE);

        imageUriString = getIntent().getStringExtra("image_uri");
        Uri imageUri = Uri.parse(imageUriString);

        intent = new Intent(this, SelectIdentifyRange.class);
        intent.putExtra("image_uri", imageUriString);

        new Thread(() -> {
            Bitmap bitmap = ImageUtils.getBitmapFromUri(this, imageUri); // 傳入 context 與 uri

            runOnUiThread(() -> {
                onTemplatePicked(bitmap);
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
    public void onTemplatePicked(Bitmap bitmap){
        // onImagePicked函式是給ImagePickerHelper使用的，在使用者選取圖片後會呼叫此函式，傳入選取的Bitmap圖片。
        if (bitmap == null) {
            Log.e("CallPython", "傳入 detectLines 的 bitmap 為 null");
        }
        TextView status = findViewById(R.id.tv_loading_text);
        new Thread(() -> {
            runOnUiThread(() -> status.setText("處理定位中..."));
            Bitmap processedBitmap = CallPython.locatePhoneImage(bitmap);
            if (processedBitmap == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "無法定位文件形狀", Toast.LENGTH_SHORT).show();
                    finish();
                });
                return; //跳出thread，以免繼續執行下去
            }

            runOnUiThread(() -> status.setText("線段偵測中..."));
            int[][] tableLines = CallPython.detectLines(processedBitmap, 606, 3194, 1586, 2297); // 使用者框選的範圍，這裡是自己手打的座標
            int[] rows = tableLines[0];
            int[] cols = tableLines[1];
            tableLineRows = rows;
            tableLineCols = cols;
            templateBitmap = processedBitmap;
            runOnUiThread(() -> status.setText("模板畫線中..."));
            Bitmap drawnBitmap = CallPython.drawTableLines(processedBitmap, cols, rows);
            saveImage(drawnBitmap);
        }).start();

    }
}