package fcu.graduation.handwritingrecognition.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;

import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    public static Bitmap rotateImageIfRequired(Context context, Uri uri) {
        Bitmap img = null;
        ExifInterface exif = null;
        try {
            InputStream imageStream = context.getContentResolver().openInputStream(uri);
            img = BitmapFactory.decodeStream(imageStream);
            imageStream.close();

            InputStream exifStream = context.getContentResolver().openInputStream(uri);
            exif = new ExifInterface(exifStream);
            exifStream.close();

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        } catch (IOException e) {
            Log.e("fcu.graduation.handwritingrecognition.utils.ImageUtils", "Error reading EXIF data", e);
            return img;
        }
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.Source source = ImageDecoder.createSource(resolver, uri);
                bitmap = ImageDecoder.decodeBitmap(source);
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
            }
        } catch (Exception e) {
            Log.e("BitmapUtils", "Failed to load bitmap: " + e.getMessage(), e);
        }
        return bitmap;
    }
}
