package fcu.graduation.handwritingrecognition;

import static android.app.Activity.RESULT_OK;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetPhotoOrImage extends BottomSheetDialogFragment {

    private ActivityResultLauncher<Intent> getResult;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        getResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        List<String> uriList = new ArrayList<>();

                        // ✅ 檢查是否是多選
                        if (data.getClipData() != null) {
                            ClipData clipData = data.getClipData();
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri imageUri = clipData.getItemAt(i).getUri();
                                uriList.add(imageUri.toString());
                                Log.d("BottomSheetFragment", "Selected Image URI[" + i + "]: " + imageUri);
                            }
                        } else if (data.getData() != null) {
                            // ✅ 單選情況
                            Uri imageUri = data.getData();
                            uriList.add(imageUri.toString());
                            Log.d("BottomSheetFragment", "Selected Image URI: " + imageUri);
                        }

                        // ✅ 傳 ArrayList<String> 到下個 activity
                        Intent intent = new Intent(getActivity(), IdentifyingImages.class);
                        intent.putStringArrayListExtra("image_uris", new ArrayList<>(uriList));
                        startActivity(intent);

                        if (isAdded()) {
                            dismissAllowingStateLoss();
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加載底部彈出選單的佈局
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        // 設置選項的按鈕
        MaterialButton option1 = view.findViewById(R.id.mbtn_photo);
        MaterialButton option2 = view.findViewById(R.id.mbtn_image_box);

        option1.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TakeSeveralPhotos.class);
            startActivity(intent);
            dismiss();
        });

        option2.setOnClickListener(v -> {
            // 使用 SAF 打開圖片選擇器
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*"); // 選擇圖片類型
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addCategory(Intent.CATEGORY_OPENABLE); // 打開可以選擇的文件
            getResult.launch(intent);
        });

        return view;
    }
}
