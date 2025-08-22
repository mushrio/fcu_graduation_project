package fcu.graduation.handwritingrecognition;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import fcu.graduation.handwritingrecognition.holder.TemplateDataHolder;
import fcu.graduation.handwritingrecognition.utils.ImageUtils;
import fcu.graduation.handwritingrecognition.widget.DragRectImageView;

public class SelectIdentifyRange extends AppCompatActivity {

    DragRectImageView ivSelectRange;
    MaterialButton mbtnRangeConfirmed;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_identify_range);

        ivSelectRange = findViewById(R.id.iv_select_range);
        mbtnRangeConfirmed = findViewById(R.id.mbtn_range_confirmed);

        String imageUriString = getIntent().getStringExtra("image_uri");
        Uri imageUri = Uri.parse(imageUriString);
        Bitmap bitmap = TemplateDataHolder.getInstance().getProcessedTemplate();

        double width = bitmap.getWidth(), height = bitmap.getHeight();
        int[] coordinates = new int[4];

        ivSelectRange.setImageBitmap(bitmap);

        ivSelectRange.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                RectF rect = ivSelectRange.getLastRect();
                Log.d("Rect", "選取區域: " + rect.toString());
                coordinates[0] = (int) (width / ivSelectRange.getWidth() * rect.left);
                coordinates[1] = (int) (width / ivSelectRange.getWidth() * rect.right);
                coordinates[2] = (int) (height / ivSelectRange.getHeight() * rect.top);
                coordinates[3] = (int) (height / ivSelectRange.getHeight() * rect.bottom);
                Log.d("Rect", "選取區域: " + coordinates[0] + ", " + coordinates[1] + ", " + coordinates[2] + ", " + coordinates[3]);
            }
            return false; // 讓原本 onTouchEvent 繼續執行
        });

        mbtnRangeConfirmed.setOnClickListener(v -> {
            if (coordinates[0] == 0 && coordinates[1] == 0 && coordinates[2] == 0 && coordinates[3] == 0) {
                Toast.makeText(this, "請先選取範圍", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, IdentifyingTemplate.class);
                intent.putExtra("image_uri", imageUriString);
                intent.putExtra("coordinates", coordinates);
                startActivity(intent);
                finish();
            }
        });

    }
}