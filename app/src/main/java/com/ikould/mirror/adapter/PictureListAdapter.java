package com.ikould.mirror.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ikould.mirror.R;
import com.ikould.mirror.data.Picture;

import java.util.List;

/**
 * describe
 * Created by liudong on 2017/5/4.
 */

public class PictureListAdapter extends RecyclerView.Adapter<PictureListAdapter.ViewHolder> {

    private List<Picture> mPictureList;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mPictureList == null ? 0 : mPictureList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView preview;
        private TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            preview = (ImageView) itemView.findViewById(R.id.item_preview);
            name = (TextView) itemView.findViewById(R.id.item_name);
        }
    }
}
