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

    public interface OnModelSelectedListener {
        void onModelSelected(String models);
    }

    private OnModelSelectedListener listener;

    public void setOnModelSelectedListener(OnModelSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加載底部彈出選單的佈局
        View view = inflater.inflate(R.layout.activity_bottom_sheet_select_model, container, false);

        // 設置選項的按鈕
        MaterialButton option1 = view.findViewById(R.id.mbtn_only_chars);
        MaterialButton option2 = view.findViewById(R.id.mbtn_only_numbers);
        MaterialButton option3 = view.findViewById(R.id.mbtn_mix);
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String models = prefs.getString("models", "lowerLetter.onnx");

        if (models.equals("lowerLetter.onnx")) {
            option1.setIconTint(ColorStateList.valueOf(Color.BLACK));
            option2.setIconTint(ColorStateList.valueOf(Color.WHITE));
            option3.setIconTint(ColorStateList.valueOf(Color.WHITE));
        } else if (models.equals("digit.onnx")){
            option1.setIconTint(ColorStateList.valueOf(Color.WHITE));
            option2.setIconTint(ColorStateList.valueOf(Color.BLACK));
            option3.setIconTint(ColorStateList.valueOf(Color.WHITE));
        } else {
            option1.setIconTint(ColorStateList.valueOf(Color.WHITE));
            option2.setIconTint(ColorStateList.valueOf(Color.WHITE));
            option3.setIconTint(ColorStateList.valueOf(Color.BLACK));
        }

        option1.setOnClickListener(v -> {
            prefs.edit().putString("models", "lowerLetter.onnx").apply();
            if (listener != null) listener.onModelSelected("lowerLetter.onnx");
            dismiss();
        });

        option2.setOnClickListener(v -> {
            prefs.edit().putString("models", "digit.onnx").apply();
            if (listener != null) listener.onModelSelected("digit.onnx");
            dismiss();  // 關閉底部彈出選單
        });

        option3.setOnClickListener(v -> {
            prefs.edit().putString("models", "mix_digit-lowerLetter.onnx").apply();
            if (listener != null) listener.onModelSelected("mix_digit-lowerLetter.onnx");
            dismiss();  // 關閉底部彈出選單
        });

        return view;
    }
}