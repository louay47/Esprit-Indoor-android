package com.example.espritindoor.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.espritindoor.Model.Comment;
import com.example.espritindoor.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>{

    private final List<Comment> mValues;

    Context mContext ;

    public CommentsAdapter(Context context, List<Comment> items) {
        mValues = items;
        this.mContext = context;

    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate contact list view when view holder is created
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popup_list_item, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsAdapter.ViewHolder holder, final int position) {
        // Called when view holder is ready to be displayed at a position.
        // Here you can set the data fields for each view in the view holder



        holder.mItem = mValues.get(position);
        holder.comment.setText(holder.mItem.getContenu());

        holder.comment_date.setText(holder.mItem.getTimestamps().toString());

        //holder.comment_username.setText(holder.mItem.getSender());
        // Load contact picture from the saved contact picture URL

        // Go to the contact detail page when a contact is clicked
        holder.mView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    // ViewHolder class that holds all the inner views for each contact list item
    // Ex: contact picture and contact name
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final CircleImageView comment_profile_image;
        public final TextView comment_username;
        public final TextView comment;
        public final TextView comment_date;
        public Comment mItem;


        public ViewHolder(View view) {

            super(view);
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                }
            });
            mView = view;
            comment_date =  view.findViewById(R.id.comment_date);
            comment =  view.findViewById(R.id.comment);
            comment_profile_image = (CircleImageView) view.findViewById(R.id.comment_profile_image);
            comment_username = (TextView) view.findViewById(R.id.comment_username);
        }

    }

}


