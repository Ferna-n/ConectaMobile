package com.example.conectamobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private Context context;
    private List<Message> messageList;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        holder.messageSender.setText(message.getSenderName());

        if (message.getSenderId() != null && message.getSenderId().equals(currentUserId)) {
            holder.messageText.setBackgroundResource(R.drawable.message_sent_background);
            holder.messageText.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.messageText.setBackgroundResource(R.drawable.message_received_background);
            holder.messageText.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

        holder.messageText.setText(message.getText());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageSender, messageText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageSender = itemView.findViewById(R.id.messageSender);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }
}
