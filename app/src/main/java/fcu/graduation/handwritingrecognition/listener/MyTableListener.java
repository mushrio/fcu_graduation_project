package fcu.graduation.handwritingrecognition.listener;

import android.content.Context;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.listener.ITableViewListener;

import java.util.ArrayList;

import fcu.graduation.handwritingrecognition.IdentifyResult;
import fcu.graduation.handwritingrecognition.adapter.MyTableAdapter;
import fcu.graduation.handwritingrecognition.holder.TemplateDataHolder;
import fcu.graduation.handwritingrecognition.model.Cell;

public class MyTableListener implements ITableViewListener {

    private final Context context;
    private final TableView tableView;
    private final ArrayList<String> recognizedStrings;
    private int imageIndex;
    private final int columnCount = TemplateDataHolder.getInstance().getTableLineCols().length - 1;
    private final int rowCount = TemplateDataHolder.getInstance().getTableLineRows().length - 1;

    public MyTableListener(TableView tableView, ArrayList<String> recognizedStrings) {
        this.context = tableView.getContext();
        this.tableView = tableView;
        this.recognizedStrings = recognizedStrings;
        this.imageIndex = imageIndex;
    }

    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
        MyTableAdapter adapter = (MyTableAdapter) tableView.getAdapter();
        Cell cell = (Cell) adapter.getCellItem(column, row);
        showEditDialog(cell.getData(), column, row);
    }

    @Override
    public void onCellDoubleClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {

    }

    @Override
    public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {

    }

    @Override
    public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

    }

    @Override
    public void onColumnHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

    }

    @Override
    public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

    }

    @Override
    public void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {

    }

    @Override
    public void onRowHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {

    }

    @Override
    public void onRowHeaderLongPressed(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {

    }

    private void showEditDialog(String currentValue, int column, int row) {
        EditText input = new EditText(context);
        input.setText(currentValue);
        IdentifyResult activity = (IdentifyResult) context;
        imageIndex = activity.getImageIndex();

        new AlertDialog.Builder(context)
                .setTitle("編輯內容")
                .setView(input)
                .setPositiveButton("確認", (dialog, which) -> {
                            String newValue = input.getText().toString();
                            // 更新 TableView 資料
                            MyTableAdapter adapter = (MyTableAdapter) tableView.getAdapter();
                            Cell cell = adapter.getCellItem(column, row);
                            cell.setData(newValue);
                            adapter.notifyDataSetChanged();

                            recognizedStrings.set(imageIndex * rowCount * columnCount + row * columnCount + column, newValue);
                        })
                .setNegativeButton("取消", (dialog, which) -> dialog.cancel())
                .show();
    }
}
