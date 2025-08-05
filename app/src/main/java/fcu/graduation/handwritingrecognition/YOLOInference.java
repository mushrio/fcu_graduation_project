package fcu.graduation.handwritingrecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.Map;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;

/*
使用YOLO方法:
1. 到build.gradle的dependencies 放入implementation 'com.microsoft.onnxruntime:onnxruntime-android:1.16.3'，按自動修正
2. 在main資料夾按右鍵新增Directories，打名字的地方點下面的Assets
3. 把onnx檔放進assets資料夾
4. 使用此YOLOInference檔案
5. 如果要使用某一個模型，先使用此兩程式碼載入模型
String modelPath = YOLOInference.copyModelFromAssets(this, "digit.onnx"); (如果寫在MainActivity)
yoloInference = new YOLOInference(modelPath);
6. 呼叫yoloInference.runInference方法，傳入Bitmap，返回float[][][]
7. 假設result = yoloInference.RunInference(Bitmap)
result[0]是這張圖片的結果
result[0][i]是第i筆辨識結果的眶
result[0][i][0~5]代表x(px), y(px), w(px), h(px), conf, cls(類別id從0開始，digit.onnx代表0~小數點，lowerLetter.onnx代表a~z)

 */
public class YOLOInference {

    private OrtEnvironment env;
    private OrtSession session;

    public YOLOInference(String modelPath) throws Exception {
        // 初始化 ONNX Runtime 環境並建立 Session
        env = OrtEnvironment.getEnvironment();
        session = env.createSession(modelPath, new OrtSession.SessionOptions());
    }

    // 執行推論，輸入為經過前處理後的 float 陣列 (形狀為 (1,3,256,256))，
    // 輸出假設為 float[1][300][6] 的陣列，每一筆資料包含 [x, y, w, h, conf, cls]
    public float[][][] runInference(Bitmap inputImage) throws Exception {

        float[] inputTensor =  preprocessImage(inputImage);
        long[] inputShape = {1, 3, 256, 256}; // (N, C, H, W)
        OnnxTensor tensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(inputTensor), inputShape);
        Map<String, OnnxTensor> inputs = Collections.singletonMap("images", tensor);
        Log.d("toOnnx", "alive2");
        OrtSession.Result result = session.run(inputs);
        Log.d("toOnnx", "alive3");
        float[][][] output = (float[][][]) result.get(0).getValue();
        tensor.close();
        return output;
    }

    public void close() throws Exception {
        session.close();
        env.close();
    }

    // 輔助函式：將 assets 中的模型檔案複製到內部儲存
    public static String copyModelFromAssets(Context context, String fileName) throws Exception {
        File file = new File(context.getFilesDir(), fileName);
        if (!file.exists()) {
            try (InputStream is = context.getAssets().open(fileName);
                 FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, length);
                }
            }
        }
        return file.getAbsolutePath();
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
}