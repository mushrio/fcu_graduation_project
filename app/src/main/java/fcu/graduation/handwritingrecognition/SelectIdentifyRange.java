package fcu.graduation.handwritingrecognition;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SelectIdentifyRange extends AppCompatActivity {

    private ImageView ivSelectRangePhoto;
    private MaterialButton mtbnSelectModel;
    private MaterialButton mtbnLoadIdentifiedModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_identify_range);

        ivSelectRangePhoto = findViewById(R.id.iv_select_range_photo);
        mtbnSelectModel = findViewById(R.id.mbtn_select_model);
        mtbnLoadIdentifiedModel = findViewById(R.id.mbtn_load_identified_model);

        String imageUriString = getIntent().getStringExtra("image_uri");
        String templateUriString = getIntent().getStringExtra(("processed_template"));
        Uri templateUri = Uri.parse(templateUriString);

        ivSelectRangePhoto.setImageURI(templateUri);

        mtbnSelectModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSelectModel selectModel = new BottomSheetSelectModel();
                selectModel.show(getSupportFragmentManager(), selectModel.getTag());
            }
        });

        mtbnLoadIdentifiedModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetPhotoOrImage photoOrImage = new BottomSheetPhotoOrImage();
                Bundle bundle = new Bundle();
                bundle.putString("image_uri", imageUriString);
                bundle.putString("processed_template", templateUriString);
                photoOrImage.setArguments(bundle);
                photoOrImage.show(getSupportFragmentManager(), photoOrImage.getTag());
            }
        });

    }


}