package fcu.graduation.handwritingrecognition.activities;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.evrencoskun.tableview.TableView;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import fcu.graduation.handwritingrecognition.R;
import fcu.graduation.handwritingrecognition.adapter.MyTableAdapter;
import fcu.graduation.handwritingrecognition.fragment.BottomSheetSaveFile;
import fcu.graduation.handwritingrecognition.holder.TemplateDataHolder;
import fcu.graduation.handwritingrecognition.listener.MyTableListener;
import fcu.graduation.handwritingrecognition.model.Cell;
import fcu.graduation.handwritingrecognition.model.ColumnHeader;
import fcu.graduation.handwritingrecognition.model.RowHeader;

public class IdentifyResult extends AppCompatActivity {

    ImageView ivOriginalImage;
    TextView tvIdentifiedImage;
    TableView tableView;
    MyTableAdapter adapter;
    MaterialButton mbtnLastPage;
    MaterialButton mbtnNextPage;
    MaterialButton mbtnSaveResult;
    ArrayList<Uri> originalImageUris = new ArrayList<>();
    private int imageIndex = 0;
    private int columnCount;
    private int rowCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_result);

        ivOriginalImage = findViewById(R.id.iv_original_image);
        tvIdentifiedImage = findViewById(R.id.tv_identified_image);
        tableView = findViewById(R.id.table_view);
        mbtnLastPage = findViewById(R.id.mbtn_last_page);
        mbtnNextPage = findViewById(R.id.mbtn_next_page);
        mbtnSaveResult = findViewById(R.id.mbtn_save_result);

        columnCount = TemplateDataHolder.getInstance().getTableLineCols().length - 1;
        rowCount = TemplateDataHolder.getInstance().getTableLineRows().length - 1;

        ArrayList<String> uriStrings = getIntent().getStringArrayListExtra("image_uris");
        ArrayList<String> recognizedStrings = getIntent().getStringArrayListExtra("recognized_strings");

        // 產生資料
        List<ColumnHeader> columnHeaders = new ArrayList<>();
        for (int i = 0; i < columnCount; i++) {
            columnHeaders.add(new ColumnHeader("欄 " + (i + 1)));
        }

        List<RowHeader> rowHeaders = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            rowHeaders.add(new RowHeader("列 " + (i + 1)));
        }

        List<List<Cell>> cellList = new ArrayList<>();
        for (int row = imageIndex * rowCount; row < imageIndex * rowCount + rowCount; row++) {

            List<Cell> cellRow = new ArrayList<>();
            for (int col = 0; col < columnCount; col++) {
                String value = recognizedStrings.get(row * columnCount + col);
                cellRow.add(new Cell(value));
            }
            cellList.add(cellRow);
        }

        adapter = new MyTableAdapter(this);
        tableView.setAdapter(adapter);
        tableView.setTableViewListener(new MyTableListener(tableView, recognizedStrings, columnHeaders, rowHeaders));
        adapter.setAllItems(columnHeaders, rowHeaders, cellList);

        if (uriStrings != null) {
            for (String uriStr : uriStrings) {
                originalImageUris.add(Uri.parse(uriStr));
            }
        }

        ivOriginalImage.setOnClickListener(v -> {
            Uri uri = originalImageUris.get(imageIndex);
            showZoomableImage(uri);
        });

        mbtnLastPage.setOnClickListener(v -> {
            if (imageIndex != 0) {
                imageIndex -= 1;
                changeCellData(recognizedStrings);
                tvIdentifiedImage.setText("辨識結果：" + (imageIndex + 1));
            }
        });

        mbtnNextPage.setOnClickListener(v -> {
            if (imageIndex != uriStrings.size() - 1) {
                imageIndex += 1;
                changeCellData(recognizedStrings);
                tvIdentifiedImage.setText("辨識結果：" + (imageIndex + 1));
            }
        });

        mbtnSaveResult.setOnClickListener(v -> {
            BottomSheetSaveFile saveFile = new BottomSheetSaveFile();
            Bundle bundle = new Bundle();
            bundle.putInt("image_count", uriStrings.size());
            bundle.putStringArrayList("recognized_strings", recognizedStrings);

            ArrayList<String> editedColumnHeaders = new ArrayList<>();
            for (int i = 0; i < columnCount; i++) {
                editedColumnHeaders.add(columnHeaders.get(i).getTitle());
            }
            ArrayList<String> editedRowHeaders = new ArrayList<>();
            for (int i = 0; i < rowCount; i++) {
                editedRowHeaders.add(rowHeaders.get(i).getTitle());
            }
            bundle.putStringArrayList("edited_column_headers", editedColumnHeaders);
            bundle.putStringArrayList("edited_row_headers", editedRowHeaders);

            saveFile.setArguments(bundle);
            saveFile.show(getSupportFragmentManager(), saveFile.getTag());
        });
    }

    private void showZoomableImage(Uri uri) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_zoomable_image);

        PhotoView photoView = dialog.findViewById(R.id.photoView);
        photoView.setImageURI(uri);

        photoView.setOnClickListener(v -> dialog.dismiss()); // 點擊圖片關閉

        dialog.show();
    }

    private void changeCellData(ArrayList<String> recognizedStrings) {
        List<List<Cell>> cellList = new ArrayList<>();
        for (int row = imageIndex * rowCount; row < imageIndex * rowCount + rowCount; row++) {

            List<Cell> cellRow = new ArrayList<>();
            for (int col = 0; col < columnCount; col++) {
                String value = recognizedStrings.get(row * columnCount + col);
                cellRow.add(new Cell(value));
            }
            cellList.add(cellRow);
        }

        adapter.setCellItems(cellList);
        adapter.notifyDataSetChanged();
    }

    public int getImageIndex() {
        return imageIndex;
    }
}