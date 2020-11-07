package com.example.espritindoor.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.espritindoor.Model.Notification;
import com.example.espritindoor.R;

import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder>{

    ImageView img;
    Button accept;
    Button refuse;
    RelativeLayout parentLayout;

    private List<Notification> notif;
    private Context context;

    public NotificationListAdapter(List<Notification> notif, Context context) {
        this.notif = notif;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Notification n = notif.get(position);
    holder.notificationName.setText(n.getNameNotification());
    holder.notificationDescription.setText(n.getDescriptionNotification());
    }

    @Override
    public int getItemCount() {
        return notif.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView notificationName;
        public  TextView notificationDescription;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.notifImage);
            notificationName = (TextView) itemView.findViewById(R.id.newNotification);
            notificationDescription = (TextView) itemView.findViewById(R.id.notifDescription);
            accept = itemView.findViewById(R.id.btnAccept);
            refuse = itemView.findViewById(R.id.btnRefuse);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }
}
