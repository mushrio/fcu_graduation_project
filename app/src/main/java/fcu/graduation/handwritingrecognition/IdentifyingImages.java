package fcu.graduation.handwritingrecognition;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class IdentifyingImages extends AppCompatActivity {

    ProgressBar loadingCircle;
    ArrayList<Uri> imageUris = new ArrayList<>();
    ArrayList<String> processedUris = new ArrayList<>();
    TextView loadingNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identifying_images);

        loadingNum = findViewById(R.id.tv_loading_num);
        loadingCircle = findViewById(R.id.pb_loading);
        loadingCircle.setVisibility(View.VISIBLE);

        ArrayList<String> uriStrings = getIntent().getStringArrayListExtra("image_uris");
        if (uriStrings != null) {
            for (String uriStr : uriStrings) {
                imageUris.add(Uri.parse(uriStr));
            }

            imageProcessing(0); // 從第 0 張開始模擬
        }
    }

    @SuppressLint("SetTextI18n")
    private void imageProcessing(int index) {
        if (index >= imageUris.size()) {
            // 所有圖片都處理完了
            Intent intent = new Intent(IdentifyingImages.this, IdentifyResult.class);
            intent.putExtra("image_uri", getIntent().getStringExtra("image_uri"));
            intent.putExtra("processed_template", getIntent().getStringExtra("processed_template"));
            intent.putStringArrayListExtra("processed_uris", processedUris);
            intent.putStringArrayListExtra("image_uris", getIntent().getStringArrayListExtra("image_uris"));
            startActivity(intent);
            finish();
            return;
        }
        loadingNum.setText((index+1) + "/" + imageUris.size() + "張圖片...");
        Uri currentUri = imageUris.get(index);

        // 模擬延遲（例如識別一張圖片需要 1 秒）
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // 處理完這張，再處理下一張
            processedUris.add(currentUri.toString());
            imageProcessing(index + 1);
        }, 3000);
    }
}