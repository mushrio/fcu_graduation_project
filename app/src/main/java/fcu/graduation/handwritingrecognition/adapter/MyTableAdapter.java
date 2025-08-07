package fcu.graduation.handwritingrecognition.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import fcu.graduation.handwritingrecognition.R;
import fcu.graduation.handwritingrecognition.holder.CellViewHolder;
import fcu.graduation.handwritingrecognition.holder.ColumnHeaderViewHolder;
import fcu.graduation.handwritingrecognition.holder.RowHeaderViewHolder;
import fcu.graduation.handwritingrecognition.model.Cell;
import fcu.graduation.handwritingrecognition.model.ColumnHeader;
import fcu.graduation.handwritingrecognition.model.RowHeader;

public class MyTableAdapter extends AbstractTableAdapter<ColumnHeader, RowHeader, Cell> {

    private final Context context;

    public MyTableAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateCellViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.table_cell, parent, false);
        return new CellViewHolder(view);
    }

    @Override
    public void onBindCellViewHolder(@NonNull AbstractViewHolder holder, @Nullable Cell cell, int columnPosition, int rowPosition) {
        assert cell != null;
        ((CellViewHolder) holder).textView.setText(cell.getData());
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.table_column_header, parent, false);
        return new ColumnHeaderViewHolder(view);
    }

    @Override
    public void onBindColumnHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable ColumnHeader columnHeader, int columnPosition) {
        assert columnHeader != null;
        ((ColumnHeaderViewHolder) holder).textView.setText(columnHeader.getTitle());
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.table_row_header, parent, false);
        return new RowHeaderViewHolder(view);
    }

    @Override
    public void onBindRowHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable RowHeader rowHeader, int rowPosition) {
        assert rowHeader != null;
        ((RowHeaderViewHolder) holder).textView.setText(rowHeader.getTitle());
    }

    @NonNull
    @Override
    public View onCreateCornerView(@NonNull ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.table_corner_view, parent, false);
    }

}


