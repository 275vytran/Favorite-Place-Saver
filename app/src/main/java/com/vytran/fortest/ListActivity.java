package com.vytran.fortest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListActivity extends AppCompatActivity {


    ArrayList<String> emailArray = new ArrayList<>();
    ArrayList<String> nameArray = new ArrayList<>();
    ArrayList<String> typeArray = new ArrayList<>();
    ArrayList<String> addressArray = new ArrayList<>();
    ArrayList<String> commentArray = new ArrayList<>();
    ArrayList<String> imageArray = new ArrayList<>();
    ArrayList<String> latitudeArray = new ArrayList<>();
    ArrayList<String> longitudeArray = new ArrayList<>();
    ArrayList<String> idArray = new ArrayList<>();

    DatabaseReference myRef, myPrivateRef;
    private ListView listView;
    FirebaseAuth listAuth;
    String currentUser;

    //Create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.navigation_upload) {
            Intent intent = new Intent(ListActivity.this, UploadActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.navigation_newfeed) {
            Intent intent = new Intent(ListActivity.this, FeedActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.navigation_signout) {
            Intent intent = new Intent(ListActivity.this, MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = (ListView)findViewById(R.id.listView);

        myRef = FirebaseDatabase.getInstance().getReference("uploads");
        myPrivateRef = FirebaseDatabase.getInstance().getReference("private_uploads");
        listAuth = FirebaseAuth.getInstance();

        currentUser = listAuth.getCurrentUser().getEmail();
        System.out.println("Current user is" + currentUser);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ListActivity.this, android.R.layout.simple_list_item_1, nameArray);
        listView.setAdapter(arrayAdapter);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap = (HashMap<String, String>) snapshot.getValue();
                    if (hashMap.size() > 0){

                        String email = hashMap.get("userEmail");

                        if (email.equals(currentUser)) {
                            emailArray.add(email);

                            String name = hashMap.get("locationName");
                            nameArray.add(name);

                            String type = hashMap.get("locationType");
                            typeArray.add(type);

                            String comment = hashMap.get("userComment");
                            commentArray.add(comment);

                            String image = hashMap.get("downloadUrl");
                            imageArray.add(image);

                            String address = hashMap.get("locationAddress");
                            addressArray.add(address);

                            String latitude = hashMap.get("userLatitude");
                            latitudeArray.add(latitude);

                            String longitude = hashMap.get("userLongitude");
                            longitudeArray.add(longitude);

                            String id = hashMap.get("trackId");
                            idArray.add(id);
                        }
                    }

                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        myPrivateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap = (HashMap<String, String>) snapshot.getValue();
                    if (hashMap.size() > 0){

                        String email = hashMap.get("userEmail");

                        if (email.equals(currentUser)) {
                            emailArray.add(email);

                            String name = hashMap.get("locationName");
                            nameArray.add(name);

                            String type = hashMap.get("locationType");
                            typeArray.add(type);

                            String comment = hashMap.get("userComment");
                            commentArray.add(comment);

                            String image = hashMap.get("downloadUrl");
                            imageArray.add(image);

                            String address = hashMap.get("locationAddress");
                            addressArray.add(address);

                            String latitude = hashMap.get("userLatitude");
                            latitudeArray.add(latitude);

                            String longitude = hashMap.get("userLongitude");
                            longitudeArray.add(longitude);

                            String id = hashMap.get("trackId");
                            idArray.add(id);
                        }
                    }

                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //i stand for index
                Intent intent = new Intent(ListActivity.this, DetailActivity.class);
                intent.putExtra("list_id", idArray.get(i));
                intent.putExtra("list_email", emailArray.get(i));
                intent.putExtra("list_name", nameArray.get(i));
                intent.putExtra("list_type", typeArray.get(i));
                intent.putExtra("list_comment", commentArray.get(i));
                intent.putExtra("list_address", addressArray.get(i));
                intent.putExtra("list_image", imageArray.get(i));
                intent.putExtra("list_latitude", latitudeArray.get(i));
                intent.putExtra("list_longitude", longitudeArray.get(i));
                startActivity(intent);

            }
        });
    }


}
