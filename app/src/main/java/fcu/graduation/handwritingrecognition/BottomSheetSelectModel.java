package fcu.graduation.handwritingrecognition;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class BottomSheetSelectModel extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加載底部彈出選單的佈局
        View view = inflater.inflate(R.layout.activity_bottom_sheet_select_model, container, false);

        // 設置選項的按鈕
        MaterialButton option1 = view.findViewById(R.id.mbtn_only_chars);
        MaterialButton option2 = view.findViewById(R.id.mbtn_only_numbers);
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean is_only_chars = prefs.getBoolean("is_only_chars", false);

        if (is_only_chars) {
            option1.setIconTint(ColorStateList.valueOf(Color.BLACK));
            option2.setIconTint(ColorStateList.valueOf(Color.WHITE));
        } else {
            option1.setIconTint(ColorStateList.valueOf(Color.WHITE));
            option2.setIconTint(ColorStateList.valueOf(Color.BLACK));
        }

        option1.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("is_only_chars", true).apply();
            dismiss();
        });

        option2.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("is_only_chars", false).apply();
            dismiss();  // 關閉底部彈出選單
        });

        return view;
    }
}