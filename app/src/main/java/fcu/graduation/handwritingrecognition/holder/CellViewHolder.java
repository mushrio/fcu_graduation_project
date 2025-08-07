package fcu.graduation.handwritingrecognition.holder;

import android.view.View;
import android.widget.TextView;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import fcu.graduation.handwritingrecognition.R;

public class CellViewHolder extends AbstractViewHolder {
    public final TextView textView;

    public CellViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.cell_data);
    }
}
