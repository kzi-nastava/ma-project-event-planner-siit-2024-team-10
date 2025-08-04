package m3.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.models.GetChatContact;

public class ChatContactAdapter extends RecyclerView.Adapter<ChatContactAdapter.ContactViewHolder> {

    private List<GetChatContact> contacts = new ArrayList<>();
    private final OnContactClickListener listener;

    public interface OnContactClickListener {
        void onContactClick(GetChatContact contact);
    }

    public ChatContactAdapter(OnContactClickListener listener) {
        this.listener = listener;
    }

    public void setContacts(List<GetChatContact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        GetChatContact contact = contacts.get(position);
        holder.bind(contact);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, previewText;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.contactName);
            previewText = itemView.findViewById(R.id.contactPreview);
        }

        public void bind(GetChatContact contact) {
            nameText.setText(contact.getName());
            previewText.setText(contact.getContent());
            itemView.setOnClickListener(v -> listener.onContactClick(contact));
        }
    }
}
