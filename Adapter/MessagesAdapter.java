package com.michaelians.bluteeth.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelians.bluteeth.Model.Message;
import com.michaelians.bluteeth.R;
import com.michaelians.bluteeth.databinding.ItemReceiveBinding;
import com.michaelians.bluteeth.databinding.ItemSendBinding;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message> messages;

    String senderBlueId;
    String receiverBlueId;

    public RecyclerView.ViewHolder viewHolder;

    public MessagesAdapter(Context context, ArrayList<Message> messages, String senderId) {
        this.context = context;
        this.messages = messages;
        this.senderBlueId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Message message = messages.get(viewType);

        if (senderBlueId.equals(message.getSenderId())) {
            view = LayoutInflater.from(context).inflate(R.layout.item_send, parent, false);
            viewHolder = new SendViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_receive, parent, false);
            viewHolder = new ReceiverViewHolder(view);
        }

        return viewHolder;
    }

    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (holder.getClass().equals(SendViewHolder.class)) {
            SendViewHolder viewHolder = (SendViewHolder) holder;
            TextView box = viewHolder.binding.sendBox;

            box.setText(message.getText());
        }

        else  {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            TextView box = viewHolder.binding.receiveBox;

            box.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class SendViewHolder extends RecyclerView.ViewHolder {

        ItemSendBinding binding;

        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSendBinding.bind(itemView);
        }

    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {

        ItemReceiveBinding binding;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
        }
    }
}
