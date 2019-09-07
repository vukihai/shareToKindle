package com.vukihai.soft.shareToKindle;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder> {
    List<Uri> fileUriList;
    public FilesAdapter(List<Uri> _fileUriList){
        this.fileUriList = _fileUriList;
    }
    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_file, viewGroup, false);
        return new FileViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder fileViewHolder, int i) {
        fileViewHolder.fileNameTextView.setText(fileUriList.get(i).toString());
    }

    @Override
    public int getItemCount() {
        return fileUriList.size();
    }

    public class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView fileNameTextView;
        ImageView deleteItemImageView;
        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.tv_file_name);
            deleteItemImageView = itemView.findViewById(R.id.img_delete_item);
            deleteItemImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            fileUriList.remove(getAdapterPosition());
            notifyDataSetChanged();
        }
    }
}
