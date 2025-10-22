package fcu.graduation.handwritingrecognition.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import fcu.graduation.handwritingrecognition.R;
import fcu.graduation.handwritingrecognition.utils.ImageUtils;

public class ConfirmPhoto extends AppCompatActivity {

    private ImageView ivPhoto;
    private ImageView ivConfirm;
    private ImageView ivDeny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_photo);

        ivConfirm = findViewById(R.id.iv_confirm);
        ivDeny = findViewById(R.id.iv_deny);
        ivPhoto = findViewById(R.id.iv_photo);

        String imageUriString = getIntent().getStringExtra("image_uri");

        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);

            Bitmap rotatedbitmap = ImageUtils.rotateImageIfRequired(this, imageUri);
            ivPhoto.setImageBitmap(rotatedbitmap);

            ivConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ConfirmPhoto.this, ClippingTemplate.class);
                    intent.putExtra("image_uri", imageUri.toString());
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                    finish();
                }
            });
        }

        ivDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}