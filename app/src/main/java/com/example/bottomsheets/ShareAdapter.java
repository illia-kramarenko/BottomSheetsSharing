package com.example.bottomsheets;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ShareAdapter extends RecyclerView.Adapter<ShareViewHolder> {

    private List<ShareModel> shareModels = new ArrayList<>();

    @Override
    public ShareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_item, parent, false);
        return new ShareViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ShareViewHolder holder, int position) {
        holder.bind(shareModels.get(position), position);
    }

    @Override
    public int getItemCount() {
        return shareModels.size();
    }

    public void setData(List<ShareModel> shareModels) {
        this.clearData();
        this.shareModels.addAll(shareModels);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.shareModels.clear();
        notifyDataSetChanged();
    }
}
