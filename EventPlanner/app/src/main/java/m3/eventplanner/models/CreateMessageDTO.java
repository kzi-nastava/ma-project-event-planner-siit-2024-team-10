package m3.eventplanner.models;

public class CreateMessageDTO {
    private int sender;
    private int receiver;
    private String content;

    public CreateMessageDTO(int sender,int receiver,String content){
        this.sender = sender;
        this.receiver=receiver;
        this.content=content;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
