package fcu.graduation.handwritingrecognition.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 將 Bitmap 儲存到系統相簿的工具類。
 */
public final class ImageSaver {

    private ImageSaver() { }   // 不允許產生實例

    /**
     * @param context      呼叫端 Context（通常是 Activity）
     * @param bitmap       要寫入的圖片
     * @param displayName  存到相簿名稱（不含副檔名）
     * @return 實際寫入後的 Uri
     * @throws IOException 寫檔或插入 MediaStore 失敗會拋例外
     */
    public static Uri saveBitmapToGallery(Context context,
                                          Bitmap bitmap,
                                          String displayName) throws IOException {

        ContentResolver resolver = context.getContentResolver();
        Uri imageUri;
        OutputStream outStream;

        // 1. 建立影像檔案的中繼資料
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ 使用相對路徑，系統自動放到相簿
            values.put(MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/MyApp");
            values.put(MediaStore.Images.Media.IS_PENDING, 1);

            imageUri = resolver.insert(
                    MediaStore.Images.Media
                            .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                    values);

            if (imageUri == null)
                throw new IOException("無法建立 MediaStore 紀錄");

            outStream = resolver.openOutputStream(imageUri);

        } else {
            // Android 9 以下：自行寫入公共目錄，再讓 MediaScanner 掃描
            File dir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "MyApp");
            if (!dir.exists() && !dir.mkdirs())
                throw new IOException("建立資料夾失敗：" + dir.getAbsolutePath());

            File imageFile = new File(dir, displayName + ".jpg");
            outStream = new FileOutputStream(imageFile);
            imageUri = Uri.fromFile(imageFile);
        }

        if (outStream == null)
            throw new IOException("無法取得輸出串流");

        try {
            // 2. 寫入位元圖資料
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outStream))
                throw new IOException("Bitmap 壓縮失敗");
        } finally {
            outStream.flush();
            outStream.close();
        }

        // 3. Android 10+ 需要把檔案從「暫停」狀態轉為可見
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(imageUri, values, null, null);
        } else {
            // Android 9-：通知 MediaScanner 更新
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri);
            context.sendBroadcast(scanIntent);
        }

        return imageUri;
    }

    public static Uri saveBitmapToApp(Context context, Bitmap bitmap, String displayName) {
        File dir = new File(context.getFilesDir(), "hidden_images");
        if (!dir.exists())
            dir.mkdirs();

        File file = new File(dir, displayName + ".png");
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return Uri.fromFile(file);
    }
}

