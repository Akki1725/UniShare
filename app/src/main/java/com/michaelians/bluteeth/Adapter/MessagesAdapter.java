package com.michaelians.bluteeth.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelians.bluteeth.Model.Message;
import com.michaelians.bluteeth.R;
import com.michaelians.bluteeth.Singleton.ThisDevice;
import com.michaelians.bluteeth.databinding.ItemReceiveBinding;
import com.michaelians.bluteeth.databinding.ItemSendBinding;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message> messages;
    String senderBlueId;
    String receiverBlueId;
    String lastMessageId, nextMessageId;
    float density;
    public RecyclerView.ViewHolder viewHolder;

    public MessagesAdapter(Context context, ArrayList<Message> messages, String senderBlueId, String receiverBlueId) {
        this.context = context;
        this.messages = messages;
        this.senderBlueId = senderBlueId;
        this.receiverBlueId = receiverBlueId;
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

        density = context.getResources().getDisplayMetrics().density;

        lastMessageId = message.getLastMsgId();
        nextMessageId = message.getNextMsgId();

        if (holder.getClass().equals(SendViewHolder.class)) {
            SendViewHolder viewHolder = (SendViewHolder) holder;
            LinearLayout container = viewHolder.binding.msgContainer;
            TextView box = viewHolder.binding.sendBox;

            if (!receiverBlueId.equals(senderBlueId)) {

                if (lastMessageId.equals("") && nextMessageId.equals("")) {
                    container.setBackgroundResource(R.drawable.send_layout_conventional);
                }

                else if (lastMessageId.equals("") && nextMessageId.equals(receiverBlueId)) {
                    container.setBackgroundResource(R.drawable.send_layout_conventional);
                }

                else if (lastMessageId.equals(receiverBlueId) && nextMessageId.equals(receiverBlueId)) {
                    container.setBackgroundResource(R.drawable.send_layout_conventional);
                }

                else if (lastMessageId.equals("") && nextMessageId.equals(senderBlueId)) {
                    container.setBackgroundResource(R.drawable.send_layout_start);
                }

                else if (lastMessageId.equals(receiverBlueId) && nextMessageId.equals(senderBlueId)) {
                    container.setBackgroundResource(R.drawable.send_layout_start);
                }

                else if (lastMessageId.equals(senderBlueId) && nextMessageId.equals(receiverBlueId)) {
                    container.setBackgroundResource(R.drawable.send_layout_end);
                }

                else if (lastMessageId.equals(senderBlueId) && nextMessageId.equals(senderBlueId)) {
                    container.setBackgroundResource(R.drawable.send_layout_middle);
                }

                else if (lastMessageId.equals(senderBlueId) && nextMessageId.equals("")) {
                    container.setBackgroundResource(R.drawable.send_layout_end);
                }

                else {
                    container.setBackgroundResource(R.drawable.send_layout_conventional);
                }
            }

            RecyclerView.LayoutParams layout = (RecyclerView.LayoutParams) viewHolder.binding.marginLayout.getLayoutParams();
            int dMarginPx = context.getResources().getDimensionPixelSize(R.dimen.msgMargin);
            int nMarginPx = context.getResources().getDimensionPixelSize(R.dimen.sMsgMargin);

            if (lastMessageId.equals(receiverBlueId)) {
                layout.topMargin = dMarginPx;
            }

            else {
                layout.topMargin = nMarginPx;
            }

            box.setText(message.getText());
        }

        else  {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            LinearLayout container = viewHolder.binding.msgContainer;
            TextView box = viewHolder.binding.receiveBox;

            if (!receiverBlueId.equals(senderBlueId)) {

                if (lastMessageId.equals("") && nextMessageId.equals("")) {
                    container.setBackgroundResource(R.drawable.receive_layout_conventional);
                }

                else if (lastMessageId.equals("") && nextMessageId.equals(senderBlueId)) {
                    container.setBackgroundResource(R.drawable.receive_layout_conventional);
                }

                else if (lastMessageId.equals(senderBlueId) && nextMessageId.equals(senderBlueId)) {
                    container.setBackgroundResource(R.drawable.receive_layout_conventional);
                }

                else if (lastMessageId.equals("") && nextMessageId.equals(receiverBlueId)) {
                    container.setBackgroundResource(R.drawable.receive_layout_start);
                }

                else if (lastMessageId.equals(senderBlueId) && nextMessageId.equals(receiverBlueId)) {
                    container.setBackgroundResource(R.drawable.receive_layout_start);
                }

                else if (lastMessageId.equals(receiverBlueId) && nextMessageId.equals(senderBlueId)) {
                    container.setBackgroundResource(R.drawable.receive_layout_end);
                }

                else if (lastMessageId.equals(receiverBlueId) && nextMessageId.equals(receiverBlueId)) {
                    container.setBackgroundResource(R.drawable.receive_layout_middle);
                }

                else if (lastMessageId.equals(receiverBlueId) && nextMessageId.equals("")) {
                    container.setBackgroundResource(R.drawable.receive_layout_end);
                }

                else {
                    container.setBackgroundResource(R.drawable.receive_layout_conventional);
                }
            }

            RecyclerView.LayoutParams layout = (RecyclerView.LayoutParams) viewHolder.binding.marginLayout.getLayoutParams();
            int dMarginPx = context.getResources().getDimensionPixelSize(R.dimen.msgMargin);
            int nMarginPx = context.getResources().getDimensionPixelSize(R.dimen.sMsgMargin);

            if (lastMessageId.equals(senderBlueId)) {
                layout.topMargin = dMarginPx;
            }

            else {
                layout.topMargin = nMarginPx;
            }

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
