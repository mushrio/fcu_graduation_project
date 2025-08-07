package fcu.graduation.handwritingrecognition.holder;

import android.view.View;
import android.widget.TextView;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import fcu.graduation.handwritingrecognition.R;

public class RowHeaderViewHolder extends AbstractViewHolder {
    public final TextView textView;

    public RowHeaderViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.row_header_textView);
    }
}
