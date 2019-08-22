package com.vytran.fortest;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback   {

    String chosenId;
    String chosenEmail;
    String chosenName;
    String chosenType;
    String chosenAddress;
    String chosenComment;
    String chosenImage;
    String chosenLatitude;
    String chosenLongitude;

    TextView emailDetail;
    TextView nameDetail;
    TextView typeDetail;
    TextView addressDetail;
    TextView commentDetail;
    ImageView imageDetail;

    private GoogleMap myMap;
    DatabaseReference detailRef, detailPrivateRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        emailDetail = findViewById(R.id.emailDetail);
        nameDetail = findViewById(R.id.nameDetail);
        typeDetail = findViewById(R.id.typeDetail);
        addressDetail = findViewById(R.id.addressDetail);
        commentDetail = findViewById(R.id.commentDetail);
        imageDetail = findViewById(R.id.imageDetail);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDetail);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();

        chosenId = intent.getStringExtra("list_id");

        chosenEmail = intent.getStringExtra("list_email");
        emailDetail.setText(chosenEmail);

        chosenName = intent.getStringExtra("list_name");
        nameDetail.setText(chosenName);

        chosenType = intent.getStringExtra("list_type");
        typeDetail.setText("Type: " + chosenType);

        chosenAddress = intent.getStringExtra("list_address");
        addressDetail.setText("Address: " + chosenAddress);

        chosenComment = intent.getStringExtra("list_comment");
        commentDetail.setText("Review: " + chosenComment);

        chosenImage = intent.getStringExtra("list_image");
        Picasso.get().load(chosenImage).into(imageDetail);

        chosenLatitude = intent.getStringExtra("list_latitude");
        chosenLongitude = intent.getStringExtra("list_longitude");


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        Double latitudeDouble = Double.parseDouble(chosenLatitude);
        Double longitudeDouble = Double.parseDouble(chosenLongitude);
        LatLng chosenLocation = new LatLng(latitudeDouble, longitudeDouble);

        myMap.addMarker(new MarkerOptions().position(chosenLocation).title(chosenName));
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chosenLocation, 17f));
    }

    public void button_ok(View v) {
        Intent intent = new Intent(DetailActivity.this, ListActivity.class);
        startActivity(intent);
    }

    public void button_delete(View v) {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("WARNING")
                .setMessage("Are you sure you want to delete this post?")
                .setIcon(R.drawable.icon)
                .setNegativeButton("NO", null)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        detailRef = FirebaseDatabase.getInstance().getReference("uploads").child(chosenId);
                        detailPrivateRef = FirebaseDatabase.getInstance().getReference("private_uploads").child(chosenId);

                        detailRef.removeValue();
                        detailPrivateRef.removeValue();

                        Intent intent = new Intent(DetailActivity.this, ListActivity.class);
                        startActivity(intent);
                        Toast.makeText(DetailActivity.this, "Successfully delete", Toast.LENGTH_LONG).show();
                    }
                })
                .show();

    }
}
