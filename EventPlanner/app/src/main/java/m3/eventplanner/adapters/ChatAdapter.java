package m3.eventplanner.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import m3.eventplanner.R;
import m3.eventplanner.models.GetMessageDTO;

import java.util.List;
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private final List<GetMessageDTO> messages;
    private final int currentUserId;

    public ChatAdapter(List<GetMessageDTO> messages, int currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    public void setMessages(List<GetMessageDTO> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    public void addMessage(GetMessageDTO message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSenderId() == currentUserId ? 1 : 0;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == 1) ? R.layout.item_message_sent : R.layout.item_message_received;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        GetMessageDTO message = messages.get(position);
        holder.messageText.setText(message.getContent());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        MessageViewHolder(View view) {
            super(view);
            messageText = view.findViewById(R.id.messageText);

            if (messageText == null) {
                Log.e("ChatAdapter", "messageText TextView not found in layout!");
            }
        }
    }
}