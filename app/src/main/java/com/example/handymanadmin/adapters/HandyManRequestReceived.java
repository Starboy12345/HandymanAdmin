package com.example.handymanadmin.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.handymanadmin.AcceptOrRejectActivity;
import com.example.handymanadmin.R;
import com.example.handymanadmin.activities.ChatActivity;
import com.example.handymanadmin.models.RequestHandyMan;
import com.example.handymanadmin.utils.GetDateTime;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.String.format;

public class HandyManRequestReceived extends FirebaseRecyclerAdapter<RequestHandyMan, HandyManRequestReceived.HandyManRequestViewHolder> {
    private Intent intent;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public HandyManRequestReceived(@NonNull FirebaseRecyclerOptions<RequestHandyMan> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final HandyManRequestViewHolder holder, int position,
                                    @NonNull final RequestHandyMan model) {
        holder.showName(model.getSenderName());
        holder.showUserPhoto(model.getSenderPhoto());
        holder.showResponse(model.getResponse());
        holder.showDate(model.getDate());
        holder.showReason(model.getReason());
        holder.showRating(model.getSenderName(), model.getRating());

        final String getAdapterPosition = getRef(position).getKey();

        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(v.getContext(), AcceptOrRejectActivity.class);
                intent.putExtra("position", getAdapterPosition);
                intent.putExtra("name", model.getSenderName());
                intent.putExtra("image", model.getSenderPhoto());
                intent.putExtra("startDate", model.getDate());
                intent.putExtra("reason", model.getReason());

                v.getContext().startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


            }
        });

        holder.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("position", getAdapterPosition);
                intent.putExtra("senderPhoto", model.getSenderPhoto());
                intent.putExtra("senderName", model.getSenderName());
                intent.putExtra("content", model.getReason());
                intent.putExtra("handyManName", model.getHandyManName());
                intent.putExtra("handyManPhoto", model.getHandyManPhoto());
                v.getContext().startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            }
        });


        holder.btnShowRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo create a map to show the route from customer to handy man
                holder.makeToast("Working on maps");
            }
        });
    }

    @NonNull
    @Override
    public HandyManRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HandyManRequestViewHolder((LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_handyman_request_received, viewGroup, false)));
    }


    //an inner class to hold the views to be inflated
    public static class HandyManRequestViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private ImageButton btnView, btnChat, btnShowRoute;
        public ConstraintLayout viewForeground;
        RelativeLayout viewBackground;
        private RatingBar ratingBar;
        private TextView contentRating;


        HandyManRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            btnView = view.findViewById(R.id.btnView);
            btnChat = view.findViewById(R.id.btnChat);
            ratingBar = view.findViewById(R.id.ratedResults);
            btnShowRoute = view.findViewById(R.id.imgRoute);
            contentRating = view.findViewById(R.id.txtNameOfRater);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }


        void showDate(String date) {
            TextView txtDate = view.findViewById(R.id.txtRequestDate);
            try {
                txtDate.setText("On " + date);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        //display the rating
        void showRating(String name, float rating) {
            if (!String.valueOf(rating).isEmpty() && rating > 0) {
                ratingBar.setVisibility(View.VISIBLE);
                contentRating.setVisibility(View.VISIBLE);
                contentRating.setText(name + " rated your work");
                ratingBar.setRating(rating);
            } else if (rating == 0) {
                ratingBar.setVisibility(View.INVISIBLE);
            }

        }

        //display the user photo
        void showUserPhoto(String urlOfImage) {
            CircleImageView profile = view.findViewById(R.id.imgUserPhotooo);

            Glide.with(view).load(urlOfImage).into(profile);
        }


        //display the Name
        void showName(String s) {
            TextView name = view.findViewById(R.id.txtNameOfCustomer);
            name.setText(s);
        }


        //display the details
        void showResponse(String s) {
            TextView loc = view.findViewById(R.id.txtResultsHandyMan);
            if (s.equals("Request Accepted")) {
                // btnRateHandyMan.setVisibility(View.VISIBLE);
                btnChat.setVisibility(View.VISIBLE);
                btnShowRoute.setVisibility(View.VISIBLE);
                loc.setTextColor(view.getResources().getColor(R.color.colorGreen));

            } else if (s.equals("Request Rejected")) {
                btnChat.setVisibility(View.INVISIBLE);
                btnShowRoute.setVisibility(View.INVISIBLE);
                // btnRateHandyMan.setVisibility(View.INVISIBLE);
                loc.setTextColor(view.getResources().getColor(R.color.colorRed));
            }


            loc.setText(s);
        }

        //display the reason
        void showReason(String s) {

            TextView reason = view.findViewById(R.id.txtReason);
            reason.setText(s);
        }


        void makeToast(String text){
            Toast toast = Toast.makeText(view.getContext(), text, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }

}
