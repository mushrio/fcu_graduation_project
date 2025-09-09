package fcu.graduation.handwritingrecognition;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.Objects;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private ActivityResultLauncher<Intent> getResult;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // 獲取圖片的 Uri
                        Uri selectedImageUri = result.getData().getData();
                        Log.d("BottomSheetFragment", "Selected Image URI: " + selectedImageUri);

                        // 傳遞 Uri 到下個 Activity
                        Intent intent = new Intent(getActivity(), ClippingTemplate.class);
                        intent.putExtra("image_uri", selectedImageUri.toString()); // 傳遞 Uri 字符串
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

        SharedPreferences prefs = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean cameraPermissionGranted = prefs.getBoolean("camera_permission_granted", false);

        // 設置選項的按鈕
        MaterialButton option1 = view.findViewById(R.id.mbtn_photo);
        MaterialButton option2 = view.findViewById(R.id.mbtn_image_box);

        option1.setOnClickListener(v -> {
            if (!cameraPermissionGranted) {
                Toast.makeText(getActivity(), "權限未授予，拍照功能無法使用", Toast.LENGTH_SHORT).show();
                dismiss();
                return;
            }
            Intent intent = new Intent(getActivity(), TakeOnePhoto.class);
            startActivity(intent);
            dismiss();
        });

        option2.setOnClickListener(v -> {
            // 使用 SAF 打開圖片選擇器
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*"); // 選擇圖片類型
            intent.addCategory(Intent.CATEGORY_OPENABLE); // 打開可以選擇的文件
            getResult.launch(intent);
        });

        return view;
    }
}
