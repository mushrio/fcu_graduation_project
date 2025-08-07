package fcu.graduation.handwritingrecognition.holder;

import android.view.View;
import android.widget.TextView;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import fcu.graduation.handwritingrecognition.R;

public class ColumnHeaderViewHolder extends AbstractViewHolder {
    public final TextView textView;

    public ColumnHeaderViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.column_header_textView);
    }
}
