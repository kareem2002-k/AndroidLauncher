package com.example.launcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {

    private List<AppInfo> appInfoList;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(AppInfo appInfo);
    }

    public AppAdapter(List<AppInfo> appInfoList, OnItemClickListener itemClickListener) {
        this.appInfoList = appInfoList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item_layout, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppInfo appInfo = appInfoList.get(position);
        holder.bind(appInfo, itemClickListener);
    }

    @Override
    public int getItemCount() {
        return appInfoList.size();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconImageView;
        private TextView labelTextView;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            labelTextView = itemView.findViewById(R.id.labelTextView);
        }

        public void bind(final AppInfo appInfo, final OnItemClickListener listener) {
            iconImageView.setImageDrawable(appInfo.getIcon());
            labelTextView.setText(appInfo.getLabel());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(appInfo);
                }
            });
        }
    }

    public void setData(List<AppInfo> data) {
        appInfoList.clear();
        appInfoList.addAll(data);
        notifyDataSetChanged();
    }
}