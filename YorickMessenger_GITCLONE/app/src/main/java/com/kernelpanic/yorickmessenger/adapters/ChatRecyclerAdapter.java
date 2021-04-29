package com.kernelpanic.yorickmessenger.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.kernelpanic.yorickmessenger.R;
import com.kernelpanic.yorickmessenger.util.Constants;
import com.kernelpanic.yorickmessenger.util.Message;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ChatRecyclerAdapter extends RecyclerView.Adapter {

    private ArrayList<Message> messagesData;
    Context context;

    private final int SENT = 0;
    private final int RECEIVED = 1;
    private final int SENT_IMAGE = 2;
    private final int RECEIVED_IMAGE = 3;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
    private Bitmap bitmap;


    public static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView    username;
        TextView    message;
        TextView    timestamp;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.username   = (TextView) itemView.findViewById(R.id.usernameLabel);
            this.timestamp  = (TextView) itemView.findViewById(R.id.timestampLabel);
            this.message   = (TextView) itemView.findViewById(R.id.messageLabel);
        }
    }

    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView    username;
        TextView    message;
        TextView    timestamp;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.username   = (TextView) itemView.findViewById(R.id.usernameLabel);
            this.timestamp  = (TextView) itemView.findViewById(R.id.timestampLabel);
            this.message   = (TextView) itemView.findViewById(R.id.messageLabel);
        }
    }

    public static class SentFileViewHolder extends RecyclerView.ViewHolder {
        TextView usernameLabel;
        TextView timestampLabel;
        TextView filenameLabel;
        TextView filesizeLabel;

        public SentFileViewHolder(@NonNull View itemView) {
            super(itemView);
            this.usernameLabel   = (TextView) itemView.findViewById(R.id.usernameLabel);
            this.timestampLabel  = (TextView) itemView.findViewById(R.id.timestampLabel);
            this.filenameLabel  = (TextView) itemView.findViewById(R.id.filenameLabel);
            this.filesizeLabel  = (TextView) itemView.findViewById(R.id.fileSizeLabel);
            itemView.setClickable(true);
        }
    }

    public static class ReceivedFileViewHolder extends RecyclerView.ViewHolder {
        TextView usernameLabel;
        TextView timestampLabel;
        TextView filenameLabel;
        TextView filesizeLabel;

        public ReceivedFileViewHolder(@NonNull View itemView) {
            super(itemView);
            this.usernameLabel   = (TextView) itemView.findViewById(R.id.usernameLabel);
            this.timestampLabel  = (TextView) itemView.findViewById(R.id.timestampLabel);
            this.filenameLabel  = (TextView) itemView.findViewById(R.id.filenameLabel);
            this.filesizeLabel  = (TextView) itemView.findViewById(R.id.fileSizeLabel);
            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent viewFile = new Intent(Intent.ACTION_VIEW);
                    v.getContext().startActivity(viewFile);
                }
            });
        }
    }


    public ChatRecyclerAdapter(ArrayList<Message> messagesData, Context context) {
        this.messagesData = messagesData;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LinearLayout fileContainer;

        View view;
        switch (viewType) {
            case SENT:
                Log.d("Y.Messenger", "Sent View Holder Created");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent,
                        parent, false);
                return new SentMessageViewHolder(view);
            case RECEIVED:
                Log.d("Y.Messenger", "Received View Holder Created");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received,
                        parent, false);
                return new ReceivedMessageViewHolder(view);
            case SENT_IMAGE:
                Log.d("Y.Messenger", "Sent Image View Holder Created");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_message_sent,
                        parent, false);
                return new SentFileViewHolder(view);
            case RECEIVED_IMAGE:
                Log.d("Y.Messenger", "Received Image View Holder Created");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_message_received,
                        parent, false);
                return new ReceivedFileViewHolder(view);
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        switch (messagesData.get(position).getType()) {
            case Constants.MESSAGE_TYPE_SENT:
                return SENT;
            case Constants.MESSAGE_TYPE_RECEIVED:
                return RECEIVED;
            case Constants.MESSAGE_TYPE_FILE_SENT:
                return SENT_IMAGE;
            case Constants.MESSAGE_TYPE_FILE_RECEIVED:
                return RECEIVED_IMAGE;
        }
        return -1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message object = messagesData.get(position);

            switch (holder.getItemViewType()) {
                case Constants.MESSAGE_TYPE_SENT:
                    ((SentMessageViewHolder) holder).username.setText(object.getUsername());
                    ((SentMessageViewHolder) holder).message.setText(object.getContent());
                    ((SentMessageViewHolder) holder).timestamp.setText(dateFormat.format(object.getTimestamp()));
                    break;
                case Constants.MESSAGE_TYPE_RECEIVED:
                    ((ReceivedMessageViewHolder) holder).username.setText(object.getUsername());
                    ((ReceivedMessageViewHolder) holder).message.setText(object.getContent());
                    ((ReceivedMessageViewHolder) holder).timestamp.setText(dateFormat.format(object.getTimestamp()));
                    break;
                case Constants.MESSAGE_TYPE_FILE_SENT:
                    ((SentFileViewHolder) holder).usernameLabel.setText(object.getUsername());
                    ((SentFileViewHolder) holder).timestampLabel.setText(dateFormat.format(object.getTimestamp()));
                    ((SentFileViewHolder) holder).filenameLabel.setText(object.getFilename());
                    ((SentFileViewHolder) holder).filesizeLabel.setText(object.getFilesize());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent viewFile = new Intent(Intent.ACTION_VIEW);
                            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                            String mimeType = mimeTypeMap.getMimeTypeFromExtension(object.getPath()
                                    .substring(object.getPath().lastIndexOf(".")));
                            Uri fileURI = FileProvider.getUriForFile(
                                    context,
                                    context.getApplicationContext().getPackageName() + ".provider",
                                    new File(object.getPath()));
                            viewFile.setDataAndType(fileURI, mimeType);
                            viewFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            context.startActivity(viewFile);
                        }
                    });
                    break;
                case Constants.MESSAGE_TYPE_FILE_RECEIVED:
                    ((ReceivedFileViewHolder) holder).usernameLabel.setText(object.getUsername());
                    ((ReceivedFileViewHolder) holder).timestampLabel.setText(dateFormat.format(object.getTimestamp()));
                    ((ReceivedFileViewHolder) holder).filenameLabel.setText(object.getFilename());
                    ((ReceivedFileViewHolder) holder).filesizeLabel.setText(object.getFilesize());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent viewFile = new Intent(Intent.ACTION_VIEW);
                            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                            String mimeType = mimeTypeMap.getMimeTypeFromExtension(object.getPath()
                                    .substring(object.getPath().lastIndexOf(".")));
                            Uri fileURI = FileProvider.getUriForFile(
                                    context,
                                    context.getApplicationContext().getPackageName() + ".provider",
                                    new File(object.getPath()));
                            viewFile.setDataAndType(fileURI, mimeType);
                            viewFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            context.startActivity(viewFile);
                        }
                    });
                    break;
            }

    }

    @Override
    public int getItemCount() {
        return messagesData.size();
    }
}
