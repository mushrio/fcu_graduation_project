package fcu.graduation.handwritingrecognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import fcu.graduation.handwritingrecognition.core.CallPython;
import fcu.graduation.handwritingrecognition.holder.TemplateDataHolder;
import fcu.graduation.handwritingrecognition.utils.ImageUtils;

public class ClippingTemplate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipping_template);

        String imageUriString = getIntent().getStringExtra("image_uri");
        Uri imageUri = Uri.parse(imageUriString);

        new Thread(() -> {
            Bitmap bitmap = ImageUtils.getBitmapFromUri(this, imageUri); // 傳入 context 與 uri

            runOnUiThread(() -> {
                clipTemplate(bitmap);
            });
        }).start();
    }

    public void clipTemplate(Bitmap bitmap){
        if (bitmap == null) {
            Log.e("CallPython", "傳入 detectLines 的 bitmap 為 null");
        }
        new Thread(() -> {
            Bitmap processedBitmap = CallPython.locatePhoneImage(bitmap);
            if (processedBitmap == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "無法定位文件形狀", Toast.LENGTH_SHORT).show();
                    finish();
                });
                return; //跳出thread，以免繼續執行下去
            }
            TemplateDataHolder.getInstance().setProcessedTemplate(processedBitmap);

            Intent intent = new Intent(ClippingTemplate.this, SelectIdentifyRange.class);
            intent.putExtra("image_uri", getIntent().getStringExtra("image_uri"));
            startActivity(intent);
            finish();
        }).start();
    }
}