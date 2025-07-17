package m3.eventplanner.fragments.comment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetCommentDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentApprovalViewModel extends ViewModel {

    private final MutableLiveData<List<GetCommentDTO>> comments = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private ClientUtils clientUtils;

    public void initialize(ClientUtils clientUtils) {
        this.clientUtils = clientUtils;
    }

    public LiveData<List<GetCommentDTO>> getComments() {
        return comments;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void loadPendingComments() {
        clientUtils.getCommentService().getPendingComments().enqueue(new Callback<List<GetCommentDTO>>() {
            @Override
            public void onResponse(Call<List<GetCommentDTO>> call, Response<List<GetCommentDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    comments.setValue(response.body());
                } else {
                    error.setValue("Failed to load pending comments");
                }
            }

            @Override
            public void onFailure(Call<List<GetCommentDTO>> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading comments");
            }
        });
    }

    public void approveComment(int commentId) {
        clientUtils.getCommentService().approveComment(commentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Comment approved");
                    loadPendingComments();
                } else {
                    error.setValue("Failed to approve comment");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error approving comment");
            }
        });
    }

    public void denyComment(int commentId) {
        clientUtils.getCommentService().rejectComment(commentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Comment denied");
                    loadPendingComments();
                } else {
                    error.setValue("Failed to deny comment");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error denying comment");
            }
        });
    }
}
