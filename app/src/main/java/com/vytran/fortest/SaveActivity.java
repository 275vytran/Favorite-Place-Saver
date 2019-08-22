package com.vytran.fortest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.UUID;

public class SaveActivity extends AppCompatActivity {

    DatabaseReference myReference;
    TextView nameSave, typeSave, commentSave;
    Button mapButtonSave;
    ImageView imageSave;
    FirebaseAuth myAuth;
    String save_email, save_name, save_type, save_address, save_comment, save_image, save_latitude, save_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        myReference = FirebaseDatabase.getInstance().getReference("private_uploads");
        myAuth = FirebaseAuth.getInstance();

        nameSave = findViewById(R.id.nameSave);
        mapButtonSave = findViewById(R.id.mapButtonSave);
        typeSave = findViewById(R.id.typeSave);
        commentSave = findViewById(R.id.commentSave);
        imageSave = findViewById(R.id.imageSave);

        Intent intent = getIntent();
        save_email = myAuth.getCurrentUser().getEmail();
        save_name = intent.getStringExtra("adap_name");
        save_address = intent.getStringExtra("adap_address");
        save_type = intent.getStringExtra("adap_type");
        save_comment = intent.getStringExtra("adap_comment");
        save_image = intent.getStringExtra("adap_image");
        save_latitude = intent.getStringExtra("adap_latitude");
        save_longitude = intent.getStringExtra("adap_longitude");

        nameSave.setText(save_name);
        mapButtonSave.setText(save_address);
        typeSave.setText(save_type);
        commentSave.setText(save_comment);
        Picasso.get().load(save_image).into(imageSave);


    }

    public void button_save(View v) {
        String uuid = UUID.randomUUID().toString();
        Upload upload = new Upload(uuid, save_email, nameSave.getText().toString(), typeSave.getText().toString(), save_address, commentSave.getText().toString(), save_image, save_latitude, save_longitude);
        myReference.child(uuid).setValue(upload);
        Intent intent = new Intent(SaveActivity.this, ListActivity.class);
        startActivity(intent);
        Toast.makeText(SaveActivity.this, "Successfully save to list!", Toast.LENGTH_LONG).show();
    }

    public void button_cancel(View v) {
        Intent intent = new Intent(SaveActivity.this, FeedActivity.class);
        startActivity(intent);
    }
}
