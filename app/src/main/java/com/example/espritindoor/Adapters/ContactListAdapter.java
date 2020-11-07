package com.example.espritindoor.Adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.espritindoor.Model.Contact;
import com.example.espritindoor.R;
import com.example.espritindoor.ViewModel.ChatFragement;
import com.example.espritindoor.ViewModel.ProfileFragement;
import com.example.espritindoor.ViewModel.TrackFriend;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder>{

    private final List<Contact> mValues;
    ProfileFragement fragment = new ProfileFragement();
    ChatFragement fragementChat = new ChatFragement();//next default fragment
    Context mContext ;

    public ContactListAdapter(Context context, List<Contact> items) {
        mValues = items;
        this.mContext = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate contact list view when view holder is created
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // Called when view holder is ready to be displayed at a position.
        // Here you can set the data fields for each view in the view holder

        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, fragementChat).addToBackStack(null).commit();

            }
        });
        holder.track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, TrackFriend.class);
                String userName = mValues.get(position).getName() ;
                intent.putExtra("name", userName);
                mContext.startActivity(intent);
            }
        });
        holder.mItem = mValues.get(position);
        holder.mContactName.setText(mValues.get(position).getName());
        holder.mContactPicture.setImageResource(R.drawable.walter_white);
        // Load contact picture from the saved contact picture URL
        final Context context = holder.mView.getContext();
        /*Picasso.with(context)
                .load(holder.mItem.getPictureUrl())
                .placeholder(R.drawable.default_contact)
                .error(R.drawable.default_contact)
                .into(holder.mContactPicture); */


        // Go to the contact detail page when a contact is clicked
        holder.mView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent,fragment).addToBackStack(null).commit();



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
        public final CircleImageView mContactPicture;
        public final TextView mContactName;
        public Button message ;
        public Contact mItem;
        public Button track ;

        public ViewHolder(View view) {

            super(view);
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                }
            });
            mView = view;
            message = (Button) view.findViewById(R.id.message);
            track = view.findViewById(R.id.track);
            mContactPicture = (CircleImageView) view.findViewById(R.id.contact_list_image);
            mContactName = (TextView) view.findViewById(R.id.contact_list_name);
        }

    }

}
