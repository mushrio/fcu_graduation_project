package fcu.graduation.handwritingrecognition;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class IdentifyResult extends AppCompatActivity {

    MaterialButton mbtnLastPage;
    MaterialButton mbtnNextPage;
    MaterialButton mbtnSaveResult;
    ImageView ivOriginalPhoto;
    ImageView ivIdentifiedPhoto;
    ArrayList<Uri> originalImageUris = new ArrayList<>();
    ArrayList<Uri> processedImageUris = new ArrayList<>();
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_result);

        mbtnLastPage = findViewById(R.id.mbtn_last_page);
        mbtnNextPage = findViewById(R.id.mbtn_next_page);
        mbtnSaveResult = findViewById(R.id.mbtn_save_result);
        ivOriginalPhoto = findViewById(R.id.iv_original_photo);
        ivIdentifiedPhoto = findViewById(R.id.iv_identified_photo);

        ArrayList<String> uriStrings = getIntent().getStringArrayListExtra("image_uris");
        ArrayList<String> processedUriStings = getIntent().getStringArrayListExtra("processed_uris");

        if (uriStrings != null) {
            for (String uriStr : uriStrings) {
                originalImageUris.add(Uri.parse(uriStr));
            }
        }
        if (processedUriStings != null) {
            for (String uriStr : processedUriStings) {
                processedImageUris.add(Uri.parse(uriStr));
            }
        }

        showImage(index);

        mbtnLastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index != 0) {
                    index -= 1;
                    showImage(index);
                }
            }
        });

        mbtnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index != uriStrings.size() - 1) {
                    index += 1;
                    showImage(index);
                }
            }
        });

        mbtnSaveResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSaveFile saveFile = new BottomSheetSaveFile();
                Bundle bundle = new Bundle();
                bundle.putString("image_uri", getIntent().getStringExtra("image_uri"));
                bundle.putString("processed_template", getIntent().getStringExtra("processed_template"));
                saveFile.setArguments(bundle);
                saveFile.show(getSupportFragmentManager(), saveFile.getTag());
            }
        });
    }

    public void showImage(int index) {
        ivOriginalPhoto.setImageURI(originalImageUris.get(index));
        ivIdentifiedPhoto.setImageURI((processedImageUris.get(index)));
    }
}