package com.example.brian.vetlocator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistorySingleActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {
    private String treatmentId, currentUserId, customerId, vetId, userVetOrCustomer;

//    private TextView treatmentLocation;
//    private TextView treatmentDistance;

    private TextView treatmentLat;
    private TextView treatmentLng;


    private double loclat;
    private double loclng;

    private Button mBack, mConfirm;



    private TextView treatmentDate;
    private TextView userName;
    private TextView userPhone;
    private TextView animals_treated;
    private TextView diseases_treated;

    private Marker treatmentloc;

    private ImageView userImage;

    private RatingBar mRatingBar;

    public RadioGroup mRadioGroup1;

    private Button mPay;

    private DatabaseReference historyTreatmentInfoDb;

    private LatLng destinationLatLng, pickupLatLng, custLatLng;

    private String distance;
    public String selectedAnimal;

    private Double treatmentPrice;

    private Spinner spinner1, spinner2;

    private Boolean customerPaid = false;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_single);


        addItemsOnSpinner2();
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();

        polylines = new ArrayList<>();

        treatmentId = getIntent().getExtras().getString("treatmentId");

//        mConfirm = (Button) findViewById(R.id.confirm);





        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

//        treatmentLocation = (TextView) findViewById(R.id.treatmentLocation);
//        treatmentDistance = (TextView) findViewById(R.id.treatmentDistance);

        treatmentLat = (TextView) findViewById(R.id.treatmentLat);
        treatmentLng = (TextView) findViewById(R.id.treatmentLng);
        animals_treated = (TextView) findViewById(R.id.Animals_treated);
        diseases_treated = (TextView) findViewById(R.id.diseases_treated);

        treatmentDate = (TextView) findViewById(R.id.treatmentDate);
        userName = (TextView) findViewById(R.id.userName);
        userPhone = (TextView) findViewById(R.id.userPhone);

        userImage = (ImageView) findViewById(R.id.userImage);

        mRadioGroup1 = findViewById(R.id.radioGroup1);

        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);

//        mPay = findViewById(R.id.pay);

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = mRadioGroup1.getCheckedRadioButtonId();

                final RadioButton radioButton = (RadioButton) findViewById(selectedId);

        if (radioButton.getText() == null){
          return;


        }


        selectedAnimal = radioButton.getText().toString();

                historyTreatmentInfoDb.child("animals_treated").setValue(selectedAnimal);
                historyTreatmentInfoDb.child("diseases_treated").setValue(String.valueOf(spinner2.getSelectedItem()));

//                Toast.makeText(HistorySingleActivity.this,
//                        "OnClickListener : " +
//                                "\nSpinner 2 : "+ String.valueOf(spinner2.getSelectedItem()),
//                        Toast.LENGTH_SHORT).show();
                mConfirm.setVisibility(View.GONE);
                mRadioGroup1.setVisibility(View.GONE);
                spinner2.setVisibility(View.GONE);

            }
        });


        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        historyTreatmentInfoDb = FirebaseDatabase.getInstance().getReference().child("history").child(treatmentId);
        getTreatmentInformation();


    }



    // add items into spinner dynamically
    public void addItemsOnSpinner2() {

//        spinner2 = (Spinner) findViewById(R.id.spinner2);
//        List<String> list = new ArrayList<String>();
//        list.add("list 1");
//        list.add("list 2");
//        list.add("list 3");
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, list);
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner2.setAdapter(dataAdapter);
    }

    public void addListenerOnSpinnerItemSelection() {
//        spinner1 = (Spinner) findViewById(R.id.spinner1);
//        spinner1.setOnItemSelectedListener(new SpinnerListener());
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(new SpinnerListener());
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {

//        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        mConfirm = (Button) findViewById(R.id.confirm);

        mConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                Toast.makeText(HeatmapsActivity.this,
//                        "OnClickListener : " +
//                                "\nSpinner 1 : "+ String.valueOf(spinner1.getSelectedItem()) +
//                                "\nSpinner 2 : "+ String.valueOf(spinner2.getSelectedItem()),
//                        Toast.LENGTH_SHORT).show();
//                Toast.makeText(HistorySingleActivity.this,
//                        "OnClickListener : " +
//                                "\nSpinner 2 : "+ String.valueOf(spinner2.getSelectedItem()),
//                        Toast.LENGTH_SHORT).show();
                mConfirm.setVisibility(View.GONE);


            }

        });
    }





    private void getTreatmentInformation() {
        historyTreatmentInfoDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                        double locationLat = 0;
//                        double locationLng = 0;


                        if (child.getKey().equals("customer")) {
                            customerId = child.getValue().toString();
                            if (!customerId.equals(currentUserId)) {
                                userVetOrCustomer = "Vets";
                                getUserInformation("Farmers", customerId);
                                getAnimalTreated();

                            }
                        }
                        if (child.getKey().equals("vet")) {
                            vetId = child.getValue().toString();
                            if (!vetId.equals(currentUserId)) {
                                userVetOrCustomer = "Farmers";
                                getUserInformation("Vets", vetId);
                                displayCustomerRelatedObjects();
                            }
                        }
                        if (child.getKey().equals("timestamp")) {
                            treatmentDate.setText(getDate(Long.valueOf(child.getValue().toString())));
                        }
                        if (child.getKey().equals("rating")) {
                            mRatingBar.setRating(Integer.valueOf(child.getValue().toString()));

                        }
                        if (child.getKey().equals("animals_treated")) {
                            animals_treated.setText(child.getValue().toString());

                        }
                        if (child.getKey().equals("diseases_treated")) {
                            diseases_treated.setText(child.getValue().toString());

                        }



//                        if (child.getKey().equals("location")){
//                             pickupLatLng = new LatLng(Double.valueOf(child.child("from").child("lat").getValue().toString()), Double.valueOf(child.child("from").child("lng").getValue().toString()));
//                            destinationLatLng = new LatLng(Double.valueOf(child.child("customer_location").child("lat").getValue().toString()), Double.valueOf(child.child("customer_location").child("lng").getValue().toString()));
//                            if(destinationLatLng != new LatLng(0,0)){
//                                getRouteToMarker();
//                            }
//                        }

                        if (child.getKey().equals("lat")){
//                          pickupLatLng = new LatLng(Double.valueOf(child.child("from").child("lat").getValue().toString()), Double.valueOf(child.child("from").child("lng").getValue().toString()));
//                            treatmentloc = mMap.addMarker(new MarkerOptions().position(custLatLng));
                            loclat= Double.parseDouble(child.getValue().toString());
                            treatmentLat.setText(child.getValue().toString());
                             

                        }

                        if (child.getKey().equals("lng")){
//                          pickupLatLng = new LatLng(Double.valueOf(child.child("from").child("lat").getValue().toString()), Double.valueOf(child.child("from").child("lng").getValue().toString()));
//                            treatmentloc = mMap.addMarker(new MarkerOptions().position(custLatLng));
                            loclng = Double.parseDouble(child.getValue().toString());
                            treatmentLng.setText(child.getValue().toString());

                        }

                    }

                    custLatLng = new LatLng(loclat,loclng);
                    treatmentloc = mMap.addMarker(new MarkerOptions().position(custLatLng).title("treatment location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(custLatLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayCustomerRelatedObjects() {

        mRatingBar.setVisibility(View.VISIBLE);
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                historyTreatmentInfoDb.child("rating").setValue(rating);
                DatabaseReference mVetRatingDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Vets").child(vetId).child("rating");
                mVetRatingDb.child(treatmentId).setValue(rating);

            }
        });

    }

    private void getAnimalTreated(){
        mRadioGroup1.setVisibility(View.VISIBLE);
        spinner2.setVisibility(View.VISIBLE);
        mConfirm.setVisibility(View.VISIBLE);
//        int selectedId = mRadioGroup1.getCheckedRadioButtonId();
//
//        final RadioButton radioButton = (RadioButton) findViewById(selectedId);
//
//        if (radioButton.getText() == null){
//          return;
//
//
//        }
//
//
//        selectedAnimal = radioButton.getText().toString();
//


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;

    }

    private void getRouteToMarker() {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(pickupLatLng, destinationLatLng)
                .build();
        routing.execute();
    }




    private void getUserInformation(String otherUserVetOrCustomer, String otherUserId) {
        DatabaseReference mOtherUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserVetOrCustomer).child(otherUserId);
        mOtherUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name") != null){
                        userName.setText(map.get("name").toString());
                    }
                    if(map.get("phone") != null){
                        userPhone.setText(map.get("phone").toString());
                    }
                    if(map.get("profileImageUrl") != null){
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(userImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getDate(Long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time*1000);
        String date = DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();
        return date;
    }


    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRoutingStart() {
    }
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(pickupLatLng);
        builder.include(destinationLatLng);
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int padding = (int) (width*0.2);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cameraUpdate);

        mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("pickup location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher2)));
        mMap.addMarker(new MarkerOptions().position(destinationLatLng).title("destination"));

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onRoutingCancelled() {
    }
    private void erasePolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }
}
