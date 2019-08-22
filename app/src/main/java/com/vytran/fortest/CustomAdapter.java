package com.vytran.fortest;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ImageViewHolder> {

    private Context context;
    private List<Upload> uploads;
    Upload uploadCurrent;

    public CustomAdapter(Context context, List<Upload> uploads) {
        this.context = context;
        this.uploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_view, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        uploadCurrent = this.uploads.get(position);

        holder.emailCustom.setText(uploadCurrent.getUserEmail());
        holder.nameCustom.setText(uploadCurrent.getLocationName());
        holder.commentCustom.setText("Review: " + uploadCurrent.getUserComment());
        holder.typeCustom.setText("Type: "+ uploadCurrent.getLocationType());
        holder.addressCustom.setText("Address: " + uploadCurrent.getLocationAddress());
        Picasso.get().load(uploadCurrent.getDownloadUrl()).fit().centerCrop().into(holder.imageCustom);

    }

    @Override
    public int getItemCount() {
        return this.uploads.size(); //how many items in our uploads list
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {

        TextView emailCustom;
        TextView nameCustom;
        TextView commentCustom;
        TextView typeCustom;
        TextView addressCustom;
        ImageView imageCustom;
        Button saveCustomButton;


        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            emailCustom = itemView.findViewById(R.id.emailCustom);
            nameCustom = itemView.findViewById(R.id.nameCustom);
            typeCustom = itemView.findViewById(R.id.typeCustom);
            addressCustom = itemView.findViewById(R.id.addressCustom);
            commentCustom = itemView.findViewById(R.id.commentCustom);
            imageCustom = itemView.findViewById(R.id.imageCustom);
            saveCustomButton = itemView.findViewById(R.id.saveCustomButton);


            saveCustomButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadCurrent = uploads.get(getAdapterPosition());

                    Intent intent = new Intent(context, SaveActivity.class);
                    intent.putExtra("adap_name", uploadCurrent.getLocationName());
                    intent.putExtra("adap_address", uploadCurrent.getLocationAddress());
                    intent.putExtra("adap_type", uploadCurrent.getLocationType());
                    intent.putExtra("adap_comment", uploadCurrent.getUserComment());
                    intent.putExtra("adap_image", uploadCurrent.getDownloadUrl());
                    intent.putExtra("adap_latitude", uploadCurrent.getUserLatitude());
                    intent.putExtra("adap_longitude", uploadCurrent.getUserLongitude());

                    context.startActivity(intent);
                }
            });

        }
    }

}
