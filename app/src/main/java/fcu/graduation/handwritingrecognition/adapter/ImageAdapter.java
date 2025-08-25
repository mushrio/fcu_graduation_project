package fcu.graduation.handwritingrecognition.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fcu.graduation.handwritingrecognition.R;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_IMAGE = 1;
    private static final int VIEW_TYPE_NO_HISTORY = 2;
    private List<Uri> imageList;
    private Context context;

    public ImageAdapter(Context context, List<Uri> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public int getItemViewType(int position) {
        if (imageList.isEmpty()) {
            return VIEW_TYPE_NO_HISTORY;
        } else {
            return VIEW_TYPE_IMAGE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        if (viewType == VIEW_TYPE_NO_HISTORY) {
            imageView.setImageResource(R.drawable.nohistory);
            return new NoHistoryViewHolder(imageView);
        } else {
            return new ImageViewHolder(imageView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder) {
            Uri imageUri = imageList.get(position);
            ((ImageViewHolder) holder).imageView.setImageURI(imageUri);
        }
        // NoHistoryViewHolder 不需要 onBind，因為一開始就設好圖片了
    }

    @Override
    public int getItemCount() {
        return imageList.isEmpty() ? 1 : imageList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }

    public static class NoHistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public NoHistoryViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}
