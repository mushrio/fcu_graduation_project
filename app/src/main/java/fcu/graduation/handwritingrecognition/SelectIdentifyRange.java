package fcu.graduation.handwritingrecognition;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SelectIdentifyRange extends AppCompatActivity {

    private ImageView ivSelectRangePhoto;
    private MaterialButton mtbnSelectModel;
    private MaterialButton mtbnLoadIdentifiedModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_identify_range);

        ivSelectRangePhoto = findViewById(R.id.iv_select_range_photo);
        mtbnSelectModel = findViewById(R.id.mbtn_select_model);
        mtbnLoadIdentifiedModel = findViewById(R.id.mbtn_load_identified_model);

        String imageUriString = getIntent().getStringExtra("image_uri");
        String templateUriString = getIntent().getStringExtra(("processed_template"));
        Uri templateUri = Uri.parse(templateUriString);
        SharedPreferences prefs = this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean is_only_chars = prefs.getBoolean("is_only_chars", false);

        ivSelectRangePhoto.setImageURI(templateUri);

        if (is_only_chars) {
            mtbnSelectModel.setText("選擇模型:純英文");
        } else {
            mtbnSelectModel.setText("選擇模型:純數字");
        }

        mtbnSelectModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSelectModel selectModel = new BottomSheetSelectModel();
                // 利用callback interface及時更新按鈕字樣
                selectModel.setOnModelSelectedListener(isOnlyChars -> {
                    if (isOnlyChars) {
                        mtbnSelectModel.setText("選擇模型:純英文");
                    } else {
                        mtbnSelectModel.setText("選擇模型:純數字");
                    }
                });
                selectModel.show(getSupportFragmentManager(), selectModel.getTag());
            }
        });

        mtbnLoadIdentifiedModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetPhotoOrImage photoOrImage = new BottomSheetPhotoOrImage();
                Bundle bundle = new Bundle();
                bundle.putString("image_uri", imageUriString);
                bundle.putString("processed_template", templateUriString);
                photoOrImage.setArguments(bundle);
                photoOrImage.show(getSupportFragmentManager(), photoOrImage.getTag());
            }
        });

    }


}