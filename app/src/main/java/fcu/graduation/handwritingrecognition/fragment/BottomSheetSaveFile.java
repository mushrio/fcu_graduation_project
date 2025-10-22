package fcu.graduation.handwritingrecognition.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import fcu.graduation.handwritingrecognition.R;
import fcu.graduation.handwritingrecognition.activities.SaveSuccess;
import fcu.graduation.handwritingrecognition.utils.CsvUtils;
import fcu.graduation.handwritingrecognition.utils.ExcelUtils;

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
                    int imageCount = getArguments() != null ? getArguments().getInt("image_count") : 0;
                    ArrayList<String> recognizedStrings = getArguments() != null ? getArguments().getStringArrayList("recognized_strings") : null;
                    ArrayList<String> editedColumnHeaders = getArguments() != null ? getArguments().getStringArrayList("edited_column_headers") : null;
                    ArrayList<String> editedRowHeaders = getArguments() != null ? getArguments().getStringArrayList("edited_row_headers") : null;

                    if (fileType.equals("CSV")) {
                        new CsvUtils().writeCsv(requireContext(), recognizedStrings, editedColumnHeaders, editedRowHeaders, new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + "辨識結果.csv", imageCount);
                    } else {
                        new ExcelUtils().writeExcel(requireContext(), recognizedStrings, editedColumnHeaders, editedRowHeaders, new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + "辨識結果.xlsx", imageCount);
                    }

                    Intent intent = new Intent(getActivity(), SaveSuccess.class);
                    startActivity(intent);

                    if (isAdded()) {
                        dismissAllowingStateLoss();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}