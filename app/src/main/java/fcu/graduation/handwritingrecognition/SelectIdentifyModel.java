package fcu.graduation.handwritingrecognition;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class SelectIdentifyModel extends AppCompatActivity {

    private ImageView ivSelectRangePhoto;
    private MaterialButton mtbnSelectModel;
    private MaterialButton mtbnLoadIdentifiedModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_identify_model);

        ivSelectRangePhoto = findViewById(R.id.iv_select_range_photo);
        mtbnSelectModel = findViewById(R.id.mbtn_select_model);
        mtbnLoadIdentifiedModel = findViewById(R.id.mbtn_load_identified_model);

        String imageUriString = getIntent().getStringExtra("image_uri");
        String templateUriString = getIntent().getStringExtra(("processed_template"));
        Uri templateUri = Uri.parse(templateUriString);
        SharedPreferences prefs = this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String models = prefs.getString("models", "lowerLetter.onnx");

        ivSelectRangePhoto.setImageURI(templateUri);

        if (models.equals("lowerLetter.onnx")) {
            mtbnSelectModel.setText("選擇模型:純英文");
        } else if (models.equals("digit.onnx")){
            mtbnSelectModel.setText("選擇模型:純數字");
        } else {
            mtbnSelectModel.setText("選擇模型:英數混合");
        }

        mtbnSelectModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSelectModel selectModel = new BottomSheetSelectModel();
                // 利用callback interface及時更新按鈕字樣
                selectModel.setOnModelSelectedListener(models -> {
                    if (models.equals("lowerLetter.onnx")) {
                        mtbnSelectModel.setText("選擇模型:純英文");
                    } else if (models.equals("digit.onnx")){
                        mtbnSelectModel.setText("選擇模型:純數字");
                    } else {
                        mtbnSelectModel.setText("選擇模型:英數混合");
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