package m3.eventplanner.clients;

import java.util.List;

import m3.eventplanner.models.GetCommentDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CommentService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("offerings/comments/pending")
    Call<List<GetCommentDTO>> getPendingComments();

    @PUT("offerings/comments/{commentId}/approve")
    Call<Void> approveComment(@Path("commentId") int commentId);

    @PUT("offerings/comments/{commentId}/reject")
    Call<Void> rejectComment(@Path("commentId") int commentId);

}
