package m3.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.fragments.comment.CommentApprovalFragment;
import m3.eventplanner.fragments.comment.CommentApprovalViewModel;
import m3.eventplanner.models.GetCommentDTO;

public class CommentApprovalListAdapter extends RecyclerView.Adapter<CommentApprovalListAdapter.CommentApprovalViewHolder> {

    private List<GetCommentDTO> commentList;
    private CommentApprovalViewModel commentApprovalViewModel;

    public CommentApprovalListAdapter(List<GetCommentDTO> commentList, CommentApprovalFragment fragment) {
        this.commentList = commentList;
        this.commentApprovalViewModel = new ViewModelProvider(fragment).get(CommentApprovalViewModel.class);
    }

    public static class CommentApprovalViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        RatingBar ratingBar;
        TextView commentTextView;
        ImageButton acceptButton;
        ImageButton denyButton;

        public CommentApprovalViewHolder(View view) {
            super(view);
            usernameTextView = view.findViewById(R.id.comment_username);
            ratingBar = view.findViewById(R.id.comment_rating);
            commentTextView = view.findViewById(R.id.comment_text);
            acceptButton = view.findViewById(R.id.acceptButton);
            denyButton = view.findViewById(R.id.denyButton);
        }
    }

    @NonNull
    @Override
    public CommentApprovalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment_approval, parent, false);
        return new CommentApprovalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentApprovalViewHolder holder, int position) {
        GetCommentDTO comment = commentList.get(position);

        holder.usernameTextView.setText(comment.getUser());
        holder.ratingBar.setRating(comment.getRating());
        holder.commentTextView.setText(comment.getContent());

        holder.acceptButton.setOnClickListener(v -> {
            commentApprovalViewModel.approveComment(comment.getId());
        });

        holder.denyButton.setOnClickListener(v -> {
            commentApprovalViewModel.denyComment(comment.getId());
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
