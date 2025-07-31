package m3.eventplanner.models;

public class CreateCommentDTO {
    private String content;
    private int account;
    private int rating;
    public CreateCommentDTO(String content,int account,int rating){
        this.content=content;
        this.account=account;
        this.rating=rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
