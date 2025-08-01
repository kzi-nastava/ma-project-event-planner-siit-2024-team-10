package m3.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.models.GetCommentDTO;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<GetCommentDTO> comments = new ArrayList<>();

    public void setComments(List<GetCommentDTO> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        GetCommentDTO comment = comments.get(position);
        holder.username.setText(comment.getUser());
        holder.commentText.setText(comment.getContent());
        holder.ratingBar.setRating(comment.getRating());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView username, commentText, date;
        RatingBar ratingBar;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.comment_username);
            commentText = itemView.findViewById(R.id.comment_text);
            ratingBar = itemView.findViewById(R.id.comment_rating);
            date = itemView.findViewById(R.id.comment_date);
        }
    }
}
