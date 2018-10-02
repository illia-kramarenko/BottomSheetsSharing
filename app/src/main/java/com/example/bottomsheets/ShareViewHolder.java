package com.example.bottomsheets;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ShareViewHolder extends RecyclerView.ViewHolder {

    private ImageView image;
    private TextView text;

    public ShareViewHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.image);
        text = (TextView) itemView.findViewById(R.id.text);
    }

    public void bind(ShareModel shareModel, int position) {
        image.setImageDrawable(shareModel.getIcon());
        text.setText(shareModel.getName());
    }
}
