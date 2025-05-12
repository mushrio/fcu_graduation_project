package fcu.graduation.handwritingrecognition;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.processing.SurfaceProcessorNode;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;

public class SaveSuccess extends AppCompatActivity {

    private MaterialButton mbtnSelectOtherFile;
    private MaterialButton mbtnBackToMain;
    private SharedPreferences prefs;
    private JSONArray historyArray;
    private String imageUriString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_success);

        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        imageUriString = getIntent().getStringExtra("image_uri");
        Uri newImageUri = Uri.parse(imageUriString);

        try {
            getContentResolver().takePersistableUriPermission(
                    newImageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        String json = prefs.getString("history", "[]");
        try {
            historyArray = new JSONArray(json);
        } catch (JSONException e) {
            historyArray = new JSONArray();
            e.printStackTrace();
        }

        if (!isDuplicatedImage(this, historyArray, newImageUri)) {
            saveHistory(historyArray, newImageUri.toString());
        }
        SharedPreferences.Editor editor = prefs.edit();

        editor.apply();

        mbtnSelectOtherFile = findViewById(R.id.mbtn_select_other_file);
        mbtnBackToMain = findViewById(R.id.mbtn_back_to_main);

        mbtnSelectOtherFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaveSuccess.this, SelectIdentifyRange.class);
                intent.putExtra("image_uri", getIntent().getStringExtra("image_uri"));
                intent.putExtra("processed_template", getIntent().getStringExtra("processed_template"));
                startActivity(intent);
            }
        });

        mbtnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaveSuccess.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveHistory(JSONArray oldArray, String newUriString) {
        JSONArray newArray = new JSONArray();

        newArray.put(newUriString);

        for (int i = 0; i < oldArray.length(); i++) {
            try {
                newArray.put(oldArray.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("history", newArray.toString());
        editor.apply();
    }

    public static String getRealPathFromUri(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }

    public boolean isDuplicatedImage(Context context, JSONArray historyArray, Uri newUri) {
        if (newUri == null) {
            return false;
        }

        String newUriString = newUri.toString();  // 用 Uri 的字串比較就好

        for (int i = 0; i < historyArray.length(); i++) {
            try {
                String savedUriString = historyArray.getString(i);
                if (newUriString.equals(savedUriString)) {
                    return true;  // 找到一樣的 Uri 字串，代表已存在
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}