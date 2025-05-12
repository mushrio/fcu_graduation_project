package fcu.graduation.handwritingrecognition;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 ViewPager2
        ViewPager2 vp2TemplateRec = findViewById(R.id.vp2_template_record);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("is_only_chars", true);
        // editor.remove("history");
        editor.apply();

        List<Uri> historyImages = getHistoryList();

        // 設置適配器
        ImageAdapter imageAdapter = new ImageAdapter(this, historyImages);
        vp2TemplateRec.setAdapter(imageAdapter);

        MaterialButton mtbnLoadinNewTemplate = findViewById(R.id.mbtn_loading_new_template);
        mtbnLoadinNewTemplate.setOnClickListener(v -> {
            // 顯示底部彈出選單
            BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });
    }

    private List<Uri> getHistoryList() {
        List<Uri> historyList = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String json = prefs.getString("history", "[]");

        try {
            JSONArray historyArray = new JSONArray(json);

            for (int i = 0; i < historyArray.length(); i++) {
                String uriString = historyArray.getString(i);
                Uri uri = Uri.parse(uriString);
                historyList.add(uri);  // 加到list裡
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return historyList;
    }
}