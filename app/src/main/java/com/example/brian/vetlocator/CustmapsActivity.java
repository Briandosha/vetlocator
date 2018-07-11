package com.example.brian.vetlocator;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//public class CustmapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, RoutingListener {
public class CustmapsActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener {
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;

    private Button flogout, frequest, fSettings, fHistory;
    public LatLng pickuplocation;
    private SupportMapFragment mapFragment;
    private Boolean requestBol = false;
    private Marker pickupMarker;


    private LinearLayout mVetInfo;

    private ImageView mVetProfileImage;

    private TextView mVetName, mVetPhone, mVetCert;

    private RatingBar mRatingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custmaps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//         mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(CustmapsActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
//        }else {
//            mapFragment.getMapAsync(this);
//        }

        polylines = new ArrayList<>();



        mVetInfo = (LinearLayout) findViewById(R.id.vetInfo);

        mVetProfileImage = (ImageView) findViewById(R.id.vetProfileImage);

        mVetName = (TextView) findViewById(R.id.vetName);
        mVetPhone = (TextView) findViewById(R.id.vetPhone);
        mVetCert = (TextView) findViewById(R.id.vetCert);


        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);



        flogout = (Button) findViewById(R.id.flogout);
        frequest = (Button) findViewById(R.id.frequest);
        fSettings = (Button) findViewById(R.id.fsettings);
        fHistory = findViewById(R.id.fhistory);

        fHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustmapsActivity.this, HistoryActivity.class);
                intent.putExtra("customerOrVet", "Farmers");
                startActivity(intent);
                return;
            }
        });

        flogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustmapsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        fSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustmapsActivity.this, CustsettingsActivity.class);
                startActivity(intent);
                return;
            }
        });

        frequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (requestBol){
                    requestBol = false;
                    geoQuery.removeAllListeners();
                    vetLocationRef.removeEventListener(vetLocationRefListener);
                    if (vetFoundID !=null){
                        DatabaseReference vetRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Vets").child(vetFoundID).child("customerReqId");
//                          vetRef.setValue(true);
                        vetRef.removeValue();
                          vetFoundID = null;



                    }
                    vetFound = false;
                    radius = 1;

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.removeLocation(userId);

                    if (pickupMarker != null) {
                        pickupMarker.remove();
                    }
                    if (mVetMarker != null){
                        mVetMarker.remove();
                    }
                    frequest.setText("call Vet");


                    mVetInfo.setVisibility(View.GONE);
                    mVetName.setText("");
                    mVetPhone.setText("");
                    mVetCert.setText("");
                    mVetProfileImage.setImageResource(R.mipmap.ic_launcher2);

                }else {
                    requestBol = true;

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
//                Toast.makeText(CustmapsActivity.this, userId, Toast.LENGTH_SHORT).show();

                    pickuplocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickuplocation).title("Pickup Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher3)));

                    frequest.setText("Finding a vet for you....");

                    getClosestVet();
                }


            }
        });
    }

    private int radius = 1;
    private Boolean vetFound = false;
    private String vetFoundID;

    GeoQuery geoQuery;

    private void getClosestVet(){
        DatabaseReference vetLocation = FirebaseDatabase.getInstance().getReference().child("vetsAvailable");

        GeoFire geoFire = new GeoFire(vetLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickuplocation.latitude, pickuplocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!vetFound && requestBol)
                {
                    vetFound = true;
                    vetFoundID = key;

                    DatabaseReference vetRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Vets").child(vetFoundID);
                    String customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    HashMap map = new HashMap();
                    map.put("customerReqId", customerID);
                    vetRef.updateChildren(map);

                    getVetLocation();
                    getVetInfo();
                    getHasRideEnded();
                    frequest.setText("Looking for Vet location");


                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!vetFound)
                {
                    radius++;
                    getClosestVet();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private Marker mVetMarker;
    private DatabaseReference vetLocationRef;
    private ValueEventListener vetLocationRefListener;

    private void getVetLocation (){

        vetLocationRef = FirebaseDatabase.getInstance().getReference().child("vetsWorking").child(vetFoundID).child("l");
        vetLocationRefListener = vetLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && requestBol){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                   // frequest.setText("Vet found");

                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }


                    if (map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng vetLatLng = new LatLng(locationLat,locationLng);
                    if (mVetMarker !=null) {
                        mVetMarker.remove();

                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickuplocation.latitude);
                    loc1.setLongitude(pickuplocation.longitude);


                    Location loc2 = new Location("");
                    loc2.setLatitude(vetLatLng.latitude);
                    loc2.setLongitude(vetLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);

                    getRouteToMarker(vetLatLng);

                    if (distance<100){
                        frequest.setText("Vet's Here");
                    }else {

                        frequest.setText("Vet found:" + String.valueOf(distance));
                    }

//                    mVetMarker = mMap.addMarker(new MarkerOptions().position(vetLatLng).title("Your vet").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher2)));
                    mVetMarker = mMap.addMarker(new MarkerOptions().position(vetLatLng).title("Your vet"));

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getRouteToMarker(LatLng vetLatLng) {

        if (vetLatLng != null && mLastLocation != null){
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), vetLatLng)
                    .build();
            routing.execute();
        }

    }


    private void getVetInfo(){
        mVetInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Vets").child(vetFoundID);

        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
//                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//                    if (map.get("name") != null){
//                        mVetName.setText(map.get("name").toString());
//                    }
//                    if (map.get("phone") != null){
//                        mVetPhone.setText(map.get("phone").toString());
//                    }
//
//                    if (map.get("cert") != null){
//                        mVetCert.setText(map.get("cert").toString());
//                    }
//
//                    if (map.get("profileImageUrl") != null){
//                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mVetProfileImage);
//                    }


                    if(dataSnapshot.child("name")!=null){
                        mVetName.setText(dataSnapshot.child("name").getValue().toString());
                    }
                    if(dataSnapshot.child("phone")!=null){
                        mVetPhone.setText(dataSnapshot.child("phone").getValue().toString());
                    }
                    if(dataSnapshot.child("car")!=null){
                        mVetCert.setText(dataSnapshot.child("cert").getValue().toString());
                    }
                    if(dataSnapshot.child("profileImageUrl").getValue()!=null){
                        Glide.with(getApplication()).load(dataSnapshot.child("profileImageUrl").getValue().toString()).into(mVetProfileImage);
                    }

                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingsAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if(ratingsTotal!= 0){
                        ratingsAvg = ratingSum/ratingsTotal;
                        mRatingBar.setRating(ratingsAvg);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;
    private void getHasRideEnded(){
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Vets").child(vetFoundID).child("customerReqId");
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                }else{
                    endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void endRide(){
        requestBol = false;
        geoQuery.removeAllListeners();
        vetLocationRef.removeEventListener(vetLocationRefListener);
        driveHasEndedRef.removeEventListener(driveHasEndedRefListener);

        if (vetFoundID != null){
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Vets").child(vetFoundID).child("customerReqId");
            driverRef.removeValue();
            vetFoundID = null;

        }
        vetFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (mVetMarker != null){
            mVetMarker.remove();
        }
        frequest.setText("call Vet");

        mVetInfo.setVisibility(View.GONE);
        mVetName.setText("");
        mVetPhone.setText("");
        mVetCert.setText("");
        mVetProfileImage.setImageResource(R.mipmap.ic_launcher2);
    }

    /*-------------------------------------------- Map specific functions -----
       |  Function(s) onMapReady, buildGoogleApiClient, onLocationChanged, onConnected
       |
       |  Purpose:  Find and update user's location.
       |
       |  Note:
       |	   The update interval is set to 1000Ms and the accuracy is set to PRIORITY_HIGH_ACCURACY,
       |      If you're having trouble with battery draining too fast then change these to lower values
       |
       |
       *-------------------------------------------------------------------*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else{
                checkLocationPermission();
            }
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){
                if(getApplicationContext()!=null){
                    mLastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    if(!getVetsAroundStarted)
                        getVetsAround();
                }
            }
        }
    };

    /*-------------------------------------------- onRequestPermissionsResult -----
    |  Function onRequestPermissionsResult
    |
    |  Purpose:  Get permissions for our app if they didn't previously exist.
    |
    |  Note:
    |	requestCode: the nubmer assigned to the request that we've made. Each
    |                request has it's own unique request code.
    |
    *-------------------------------------------------------------------*/

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(CustmapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(CustmapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


//
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(CustmapsActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
//        }
//        buildGoogleApiClient();
//        mMap.setMyLocationEnabled(true);
//
//    }
//
//    protected synchronized void buildGoogleApiClient(){
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();
//
//    }
//
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(1000);
//        mLocationRequest.setFastestInterval(1000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(CustmapsActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//
//
//
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//
//        if(location!=null) {
//            mLastLocation = location;
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//
//            if(!getVetsAroundStarted)
//                getVetsAround();
//
//        }
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//
//    final int LOCATION_REQUEST_CODE = 1;
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        switch (requestCode) {
//            case LOCATION_REQUEST_CODE:{
//                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    mapFragment.getMapAsync(this);
//                }
//                else{
//                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
//                }
//
//                break;
//            }
//        }
//    }

    @Override
    protected void onStop() {
        super.onStop();
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

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
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

    boolean getVetsAroundStarted = false;
    List<Marker> markers = new ArrayList<Marker>();
    private void getVetsAround(){
        getVetsAroundStarted = true;
        DatabaseReference vetLocation = FirebaseDatabase.getInstance().getReference().child("vetsAvailable");

        GeoFire geoFire = new GeoFire(vetLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLongitude(), mLastLocation.getLatitude()), 999999999);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key))
                        return;
                }

                LatLng vetLocation = new LatLng(location.latitude, location.longitude);

                Marker mVetMarker = mMap.addMarker(new MarkerOptions().position(vetLocation).title(key).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                mVetMarker.setTag(key);

                markers.add(mVetMarker);


            }

            @Override
            public void onKeyExited(String key) {
                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key)){
                        markerIt.remove();
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key)){
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

}
