package fcu.graduation.handwritingrecognition.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import fcu.graduation.handwritingrecognition.R;
import fcu.graduation.handwritingrecognition.holder.TemplateDataHolder;

public class SaveSuccess extends AppCompatActivity {

    private MaterialButton mbtnSelectOtherFile;
    private MaterialButton mbtnBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_success);

        mbtnSelectOtherFile = findViewById(R.id.mbtn_select_other_file);
        mbtnBackToMain = findViewById(R.id.mbtn_back_to_main);

        mbtnSelectOtherFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaveSuccess.this, SelectIdentifyModel.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 清除掉 SelectIdentifyModel 之上的所有 activity
                startActivity(intent);
                finish();
            }
        });

        mbtnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TemplateDataHolder.getInstance().clear();
                Intent intent = new Intent(SaveSuccess.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // 清掉 MainActivity 之上的所有 activity，以及以新的 task 開始(避免舊的 stack 殘留)
                startActivity(intent);
                finish();
            }
        });
    }
}