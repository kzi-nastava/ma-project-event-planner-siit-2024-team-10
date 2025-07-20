package m3.eventplanner.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import m3.eventplanner.R;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private List<String> photoPaths;
    private final Context context;
    private final OnPhotoRemoveListener listener;

    public interface OnPhotoRemoveListener {
        void onPhotoRemoved(int position);
    }

    public PhotoAdapter(Context context, List<String> photoPaths, OnPhotoRemoveListener listener) {
        this.context = context;
        this.photoPaths = photoPaths;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String path = photoPaths.get(position);
        Glide.with(context)
                .load(new File(path))
                .into(holder.photoImage);

        holder.deleteButton.setOnClickListener(v -> {
            photoPaths.remove(position);
            notifyItemRemoved(position);
            listener.onPhotoRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return photoPaths.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImage;
        ImageButton deleteButton;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImage = itemView.findViewById(R.id.image_view);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public void updatePhotos(List<String> newPaths) {
        this.photoPaths = newPaths;
        notifyDataSetChanged();
    }
}
