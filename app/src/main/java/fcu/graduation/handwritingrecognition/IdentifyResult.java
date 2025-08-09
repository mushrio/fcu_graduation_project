package fcu.graduation.handwritingrecognition;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.evrencoskun.tableview.TableView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import fcu.graduation.handwritingrecognition.adapter.MyTableAdapter;
import fcu.graduation.handwritingrecognition.holder.TemplateDataHolder;
import fcu.graduation.handwritingrecognition.listener.MyTableListener;
import fcu.graduation.handwritingrecognition.model.Cell;
import fcu.graduation.handwritingrecognition.model.ColumnHeader;
import fcu.graduation.handwritingrecognition.model.RowHeader;

public class IdentifyResult extends AppCompatActivity {

    TableView tableView;
    MyTableAdapter adapter;
    MyTableListener listener;
    MaterialButton mbtnLastPage;
    MaterialButton mbtnNextPage;
    MaterialButton mbtnSaveResult;
    ArrayList<Uri> originalImageUris = new ArrayList<>();
    private int imageIndex = 0;
    private final int columnCount = TemplateDataHolder.getInstance().getTableLineCols().length - 1;
    private final int rowCount = TemplateDataHolder.getInstance().getTableLineRows().length - 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_result);

        tableView = findViewById(R.id.table_view);
        mbtnLastPage = findViewById(R.id.mbtn_last_page);
        mbtnNextPage = findViewById(R.id.mbtn_next_page);
        mbtnSaveResult = findViewById(R.id.mbtn_save_result);

        ArrayList<String> uriStrings = getIntent().getStringArrayListExtra("image_uris");
        ArrayList<String> recognizedStrings = getIntent().getStringArrayListExtra("recognized_strings");

        adapter = new MyTableAdapter(this);
        tableView.setAdapter(adapter);
        tableView.setTableViewListener(new MyTableListener(tableView, recognizedStrings));

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

        adapter.setAllItems(columnHeaders, rowHeaders, cellList);

        if (uriStrings != null) {
            for (String uriStr : uriStrings) {
                originalImageUris.add(Uri.parse(uriStr));
            }
        }

        mbtnLastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageIndex != 0) {
                    imageIndex -= 1;
                    changeCellData(recognizedStrings);
                }
            }
        });

        mbtnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageIndex != uriStrings.size() - 1) {
                    imageIndex += 1;
                    changeCellData(recognizedStrings);
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