package m3.eventplanner.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import m3.eventplanner.R;

public class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.ImageViewHolder> {
    private List<Uri> imageUris = new ArrayList<>();
    private OnImageRemoveListener onImageRemoveListener;

    public interface OnImageRemoveListener {
        void onImageRemove(int position);
    }

    public void setOnImageRemoveListener(OnImageRemoveListener listener) {
        this.onImageRemoveListener = listener;
    }

    public void setImages(List<Uri> uris) {
        imageUris = new ArrayList<>(uris);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);
        holder.imageView.setImageURI(imageUri);
        holder.removeButton.setOnClickListener(v -> {
            if (onImageRemoveListener != null) {
                onImageRemoveListener.onImageRemove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton removeButton;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
}