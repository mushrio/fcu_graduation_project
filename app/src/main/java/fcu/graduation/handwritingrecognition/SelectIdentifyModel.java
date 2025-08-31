package fcu.graduation.handwritingrecognition;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Map;

import fcu.graduation.handwritingrecognition.holder.TemplateDataHolder;
import fcu.graduation.handwritingrecognition.model.History;
import fcu.graduation.handwritingrecognition.utils.LocalHistoryUtils;

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

        Bitmap drawnTemplate = TemplateDataHolder.getInstance().getDrawnTemplate();
        SharedPreferences prefs = this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String models = prefs.getString("models", "lowerLetter.onnx");

        boolean isFromMain = prefs.getBoolean("from_main", false);
        if (isFromMain) {
            // 更新歷史紀錄的時間
            String historyUriString = getIntent().getStringExtra("history_uri");
            if (historyUriString != null) {
                LocalHistoryUtils localHistoryUtils = new LocalHistoryUtils();
                Map<String, History> historyMap = localHistoryUtils.load(this);

                History history = historyMap.get(historyUriString);
                if (history != null) {
                    history.setTimestamp(System.currentTimeMillis());
                    localHistoryUtils.save(this, historyMap);
                }
            }
            prefs.edit().putBoolean("from_main", false).apply();
        }

        ivSelectRangePhoto.setImageBitmap(drawnTemplate);;

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
                photoOrImage.show(getSupportFragmentManager(), photoOrImage.getTag());
            }
        });

    }
}