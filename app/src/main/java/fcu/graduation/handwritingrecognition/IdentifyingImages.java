package fcu.graduation.handwritingrecognition;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fcu.graduation.handwritingrecognition.core.CallPython;
import fcu.graduation.handwritingrecognition.core.YOLOInference;
import fcu.graduation.handwritingrecognition.holder.TemplateDataHolder;
import fcu.graduation.handwritingrecognition.utils.ImageUtils;

public class IdentifyingImages extends AppCompatActivity {

    ProgressBar loadingCircle;
    ArrayList<Uri> imageUris = new ArrayList<>();
    ArrayList<String> recognizedStrings = new ArrayList<>();
    TextView loadingNum;
    Bitmap templateBitmap;
    int[] tableLineRows;
    int[] tableLineCols;
    YOLOInference yoloInference;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identifying_images);

        loadingNum = findViewById(R.id.tv_loading_num);
        loadingCircle = findViewById(R.id.pb_loading);
        loadingCircle.setVisibility(View.VISIBLE);

        new Thread(() -> {
            templateBitmap = TemplateDataHolder.getInstance().getProcessedTemplate();
            tableLineRows = TemplateDataHolder.getInstance().getTableLineRows();
            tableLineCols = TemplateDataHolder.getInstance().getTableLineCols();

            prefs = this.getSharedPreferences("MyPrefs", MODE_PRIVATE);

            // 載入模型 (假設模型檔案放在 assets 資料夾，檔名為 digit.onnx)
            try {
                String model = prefs.getString("models", "lowerLetter.onnx");
                String modelPath = YOLOInference.copyModelFromAssets(this, model);
                yoloInference = new YOLOInference(modelPath);
            } catch (Exception e) {
                String errMessage = "模型載入失敗: " + e.getMessage();
            }

            runOnUiThread(() -> {
                ArrayList<String> uriStrings = getIntent().getStringArrayListExtra("image_uris");
                Log.d("DebugCheck", "uriStrings: " + uriStrings);
                if (uriStrings != null) {
                    for (String uriStr : uriStrings) {
                        imageUris.add(Uri.parse(uriStr));
                    }
                    loadingNum.setText(1 + "/" + imageUris.size() + "張圖片...");

                    imageProcessing(0); // 從第 0 張開始模擬
                }
            });
        }).start();
    }

    @SuppressLint("SetTextI18n")
    private void imageProcessing(int index) {
        if (index >= imageUris.size()) {
            // 所有圖片都處理完了
            Log.d("DebugCheck", "All images processed. Moving to IdentifyResult...");
            Intent intent = new Intent(IdentifyingImages.this, IdentifyResult.class);
            intent.putExtra("image_uri", getIntent().getStringExtra("image_uri"));
            intent.putExtra("processed_template", getIntent().getStringExtra("processed_template"));
            intent.putStringArrayListExtra("image_uris", getIntent().getStringArrayListExtra("image_uris"));
            intent.putStringArrayListExtra("recognized_strings", recognizedStrings);
            startActivity(intent);
            finish();
            return;
        }
        Log.d("DebugCheck", "Processing image " + index + ": " + imageUris.get(index));
        loadingNum.setText((index+1) + "/" + imageUris.size() + "張圖片...");

        new Thread(() -> {
            try {
                int stringCounts = 0;
                Uri currentUri = imageUris.get(index);
                Bitmap pickedBitmap = ImageUtils.getBitmapFromUri(this, currentUri);
                Log.d("DebugCheck", "Bitmap loaded for index " + index);

                if (templateBitmap != null && tableLineCols != null && tableLineRows != null) {

                    Bitmap[] cellImages = CallPython.getCellImage(pickedBitmap, templateBitmap, tableLineCols, tableLineRows, 256);
                    Log.d("DebugCheck", "Cell images count: " + cellImages.length);

                    StringBuilder finalResult = new StringBuilder();
                    for (Bitmap cell : cellImages) {
                        float[][][] modelOutput = yoloInference.runInference(cell);
                        String identifiedOutput = parseOutput(modelOutput);
                        Log.d("result", identifiedOutput);

                        List<CellResult> cells = extractValidCells(modelOutput, 0.25f);

                        for (CellResult cr : cells) {
                            char ch  = classToChar(cr.cls, prefs.getString("models", "lowerLetter.onnx"));
                            finalResult.append(ch);
                        }
                        recognizedStrings.add(finalResult.toString());
                        finalResult.setLength(0);
                        stringCounts++;
                    }
                    Log.d("FinalString", "Recognized text: " + finalResult.toString());
                    Log.d("recognizedStrings", "Recognized text: " + recognizedStrings);

                    // 當一張圖的字串數量未滿表格數量時，繼續加空字串進去，方便後面多張結果顯示處理
                    while (stringCounts < (tableLineCols.length - 1) * (tableLineRows.length - 1)) {
                        recognizedStrings.add("");
                        stringCounts++;
                    }
                }

                // 回到主執行緒繼續處理下一張
                new Handler(Looper.getMainLooper()).post(() -> {
                    imageProcessing(index + 1);
                });
            } catch (Exception e) {
                Log.e("ErrorCheck", "Exception in imageProcessing: ", e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    // 跳過這張
                    imageProcessing(index + 1);
                });
            }

        }).start();
    }

    // 將 Bitmap 調整尺寸為 256x256，並轉換為 (1,3,256,256) 的 float 陣列（RGB 順序，每個像素正規化到 [0,1]）
    private float[] preprocessImage(Bitmap bitmap) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        // Step 1: Resize bitmap to 256x256
        Bitmap resized = Bitmap.createScaledBitmap(mutableBitmap, 256, 256, true);

        // Step 2: Prepare float array of size C * H * W = 3 * 256 * 256
        int width = 256;
        int height = 256;
        int channels = 3;
        float[] result = new float[channels * width * height];

        // Step 3: Normalize and reshape to CHW format
        // Bitmap format is in ARGB, get RGB and normalize

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = resized.getPixel(x, y);

                // Extract RGB components (ignore Alpha)
                float r = (Color.red(pixel) / 255.0f);
                float g = (Color.green(pixel) / 255.0f);
                float b = (Color.blue(pixel) / 255.0f);

                // CHW format: channel-first (R -> G -> B)
                result[0 * width * height + y * width + x] = r;
                result[1 * width * height + y * width + x] = g;
                result[2 * width * height + y * width + x] = b;
            }
        }

        return result;
    }

    // 解析模型輸出：假設輸出格式為 (1, 300, 6)，每個偵測結果包含 [x, y, w, h, conf, cls]
    private String parseOutput(float[][][] output) {
        StringBuilder sb = new StringBuilder();
        float confThreshold = 0.25f;
        float[][] detections = output[0];
        String title = "=== 預測結果 (conf >= " + confThreshold + ") ===\n";
        sb.append(title);
        for (float[] det : detections) {
            float x = det[0];
            float y = det[1];
            float w = det[2];
            float h = det[3];
            float conf = det[4];
            float cls = det[5];
            if(conf >= confThreshold){
                @SuppressLint("DefaultLocale") String resultStr = String.format("x=%.2f, y=%.2f, w=%.2f, h=%.2f, conf=%.2f, cls=%.0f\n", x, y, w, h, conf, cls);
                sb.append(resultStr);
            }
        }
        return sb.toString();
    }

    private List<CellResult> extractValidCells(float[][][] output, float threshold) {
        List<CellResult> results = new ArrayList<>();
        for (float[] det : output[0]) {
            float x = det[0];
            float y = det[1];
            float w = det[2];
            float h = det[3];
            float conf = det[4];
            int cls = (int) det[5];

            if (conf >= threshold) {
                results.add(new CellResult(x, y, w, h, conf, cls));
            }
        }
        // 按 x 座標排序
        results.sort(Comparator.comparingDouble(r -> r.x));
        return results;
    }

    private char classToChar(int cls, String modelName) {
        if (modelName.contains("digit")) {
            if (cls == 10) return '.'; // 小數點編號為10
            return (char) ('0' + cls);
        } else if (modelName.contains("lowerLetter")) {
            return (char) ('a' + cls);
        }
        return '?'; // fallback
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(yoloInference != null) {
            try {
                yoloInference.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}