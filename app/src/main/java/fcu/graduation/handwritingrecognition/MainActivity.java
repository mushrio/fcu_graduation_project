package fcu.graduation.handwritingrecognition;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fcu.graduation.handwritingrecognition.adapter.ImageAdapter;
import fcu.graduation.handwritingrecognition.core.CallPython;
import fcu.graduation.handwritingrecognition.holder.TemplateDataHolder;
import fcu.graduation.handwritingrecognition.model.History;
import fcu.graduation.handwritingrecognition.model.TableLines;
import fcu.graduation.handwritingrecognition.utils.ImageUtils;
import fcu.graduation.handwritingrecognition.utils.LocalHistoryUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 ViewPager2
        ViewPager2 vp2TemplateRec = findViewById(R.id.vp2_template_record);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("from_main", false);
        editor.putString("models", "lowerLetter.onnx");
        editor.remove("history");
        editor.apply();

        TemplateDataHolder.getInstance().clear();

        List<Uri> historyImages = getHistoryList();

        // 設置適配器
        ImageAdapter imageAdapter = new ImageAdapter(this, historyImages, (uri, position) -> {
            editor.putBoolean("from_main", true);
            editor.apply();

            LocalHistoryUtils localHistoryUtils = new LocalHistoryUtils();
            Map<String, History> historyMap = localHistoryUtils.load(this);
            History history = historyMap.get(uri.toString());
            for (TableLines tableLines : history.getTableLines()) {
                int[] rows = tableLines.getTableLineRows();
                int[] cols = tableLines.getTableLineCols();
                TemplateDataHolder.getInstance().setTableLineRows(rows);
                TemplateDataHolder.getInstance().setTableLineCols(cols);
            }

            Bitmap drawnTemplate = ImageUtils.getBitmapFromUri(this, uri);
            TemplateDataHolder.getInstance().setDrawnTemplate(drawnTemplate);

            Uri processedTemplateUri = Uri.parse(history.getClippedTemplate());
            Bitmap processedTemplate = ImageUtils.getBitmapFromUri(this, processedTemplateUri);
            TemplateDataHolder.getInstance().setProcessedTemplate(processedTemplate);

            Intent intent = new Intent(this, SelectIdentifyModel.class);
            intent.putExtra("history_uri", uri.toString());
            startActivity(intent);
        });
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

        LocalHistoryUtils localHistoryUtils = new LocalHistoryUtils();
        List<Map.Entry<String, History>> sortedHistory = localHistoryUtils.loadSorted(this);

        for (Map.Entry<String, History> entry : sortedHistory) {
            String uriString = entry.getKey();
            Uri uri = Uri.parse(uriString);

            try {
                getContentResolver().openInputStream(uri).close();  // 檢查檔案是否存在
                validHistoryList.add(uri); // 如果有效就加到列表
            } catch (IOException e) {
                // 無法讀取圖片，可能是被刪除了，將資料從 history.json 移除，並移除掉 hidden 的照片，進行被動刪除
                History history = entry.getValue();
                File hiddenFile = new File(getFilesDir() ,history.getClippedTemplate());
                boolean deleted = hiddenFile.delete();
                if (!deleted) {
                    Log.w("History", "刪除 hidden 檔案失敗：" + hiddenFile.getAbsolutePath());
                }

                localHistoryUtils.delete(this, uriString);
            }
        }
        return validHistoryList;
    }
}