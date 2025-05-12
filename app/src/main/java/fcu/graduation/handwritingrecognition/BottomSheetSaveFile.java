package fcu.graduation.handwritingrecognition;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class BottomSheetSaveFile extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加載底部彈出選單的佈局
        View view = inflater.inflate(R.layout.activity_bottom_sheet_save_file, container, false);

        // 設置選項的按鈕
        MaterialButton option1 = view.findViewById(R.id.mbtn_save_as_excel);
        MaterialButton option2 = view.findViewById(R.id.mbtn_save_as_csv);

        option1.setOnClickListener(v -> {
            showConfirmDialog("Excel");
        });

        option2.setOnClickListener(v -> {
            showConfirmDialog("CSV");
        });

        return view;
    }

    private void showConfirmDialog(String fileType) {
        new AlertDialog.Builder(requireContext())
                .setTitle("儲存確認")
                .setMessage("確定要儲存為 " + fileType + " 格式嗎？")
                .setPositiveButton("確定", (dialog, which) -> {
                    // 執行儲存邏輯
                    // 例如呼叫 Activity 的方法來處理儲存
                    String imageUriString = getArguments() != null ? getArguments().getString("image_uri") : null;
                    String templateUriString = getArguments() != null ? getArguments().getString("processed_template") : null;

                    Intent intent = new Intent(getActivity(), SaveSuccess.class);
                    intent.putExtra("image_uri", imageUriString);
                    intent.putExtra("processed_template", templateUriString);
                    startActivity(intent);

                    if (isAdded()) {
                        dismissAllowingStateLoss();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}