package fcu.graduation.handwritingrecognition;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
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
        editor.putString("models", "lowerLetter.onnx");
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

        // 啟動 Python（只需一次）
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        // 在背景執行緒預載 Python 模組
        new Thread(CallPython::preload).start();
    }

    private List<Uri> getHistoryList() {
        List<Uri> validHistoryList = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String json = prefs.getString("history", "[]");
        JSONArray validHistoryArray = new JSONArray();

        try {
            JSONArray historyArray = new JSONArray(json);

            for (int i = 0; i < historyArray.length(); i++) {
                String uriString = historyArray.getString(i);
                Uri uri = Uri.parse(uriString);

                // 嘗試開啟圖片來檢查是否有效
                try {
                    getContentResolver().openInputStream(uri).close();  // 檢查檔案是否存在
                    validHistoryList.add(uri);                         // 如果有效就加到列表
                    validHistoryArray.put(uriString);                  // 同步保留在新 JSONArray 中
                } catch (Exception e) {
                    // 無法讀取圖片，可能是被刪除了，不加入 list
                    e.printStackTrace();
                }
            }

            // 如果列表有變動（移除了壞的 URI），就寫回 SharedPreferences
            if (validHistoryArray.length() != historyArray.length()) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("history", validHistoryArray.toString());
                editor.apply();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return validHistoryList;
    }
}