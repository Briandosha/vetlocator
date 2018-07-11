package com.example.brian.vetlocator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;


public class HeatmapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,RoutingListener {
    public Query query;
   public  DatabaseReference historyRef;
   private TextView testdata;
   private double lat1;
   private double lng1;
    private ClusterManager<MyItem> mClusterManager;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private SupportMapFragment mapFragment;
    private Spinner spinner1, spinner2;
    private Button btnSubmit;
    private FrameLayout frameLayout;
    private LinearLayout heatmapslinearlayout;
 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmaps);

        addItemsOnSpinner2();
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();

        frameLayout = (FrameLayout) findViewById(R.id.framelayout);
        heatmapslinearlayout = (LinearLayout) findViewById(R.id.heatmapslinearlayout);


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.hmap);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(HeatmapsActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }else {
            mapFragment.getMapAsync(this);
        }

//        testdata = findViewById(R.id.testdata);

//        historyRef = FirebaseDatabase.getInstance().getReference("history");
        //        historyRef.addListenerForSingleValueEvent(valueEventListener);
//        query = historyRef.orderByChild("diseases_treated").equalTo(String.valueOf(spinner2.getSelectedItem()));



//        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                  @Override
//                  public void onDataChange(DataSnapshot dataSnapshot) {
//                      History htmaps = dataSnapshot.getValue(History.class);
//                     Log.d("htmps",dataSnapshot.getClass().toString());
//                      testdata.setText(htmaps.getLat());
////                      testdata.setText("test");
//
//
//
//
//                  }
//
//                  @Override
//                  public void onCancelled(DatabaseError databaseError) {
//
//                  }
//              });

//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//            int count = 0;
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    History htmaps = postSnapshot.getValue(History.class);
////                   testdata.setText(htmaps.getLat().toString());
//                    Log.d("htmps",htmaps.toString());
//                    lat1=htmaps.getLat();
//                    lng1=htmaps.getLng();
//                    //MyItem myitem = postSnapshot.getValue(MyItem.class);
//                    MyItem myItem = new MyItem(lat1,lng1);
//                    mClusterManager.addItem(myItem);
//                    count+=1;
//                    Log.e("HeatMapsActivity", "onDataChange: lat"+lat1+"long:"+lng1);
//                }
//                Toast.makeText(HeatmapsActivity.this, ""+count, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

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
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                Toast.makeText(HeatmapsActivity.this,
//                        "OnClickListener : " +
//                                "\nSpinner 1 : "+ String.valueOf(spinner1.getSelectedItem()) +
//                                "\nSpinner 2 : "+ String.valueOf(spinner2.getSelectedItem()),
//                        Toast.LENGTH_SHORT).show();
                Toast.makeText(HeatmapsActivity.this,
                        "OnClickListener : " +
                                "\nSpinner 2 : "+ String.valueOf(spinner2.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();

                historyRef = FirebaseDatabase.getInstance().getReference("history");
                //        historyRef.addListenerForSingleValueEvent(valueEventListener);
                query = historyRef.orderByChild("diseases_treated").equalTo(String.valueOf(spinner2.getSelectedItem()));

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int count = 0;
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            History htmaps = postSnapshot.getValue(History.class);
//                   testdata.setText(htmaps.getLat().toString());
                            Log.d("htmps",htmaps.toString());
                            lat1=htmaps.getLat();
                            lng1=htmaps.getLng();
                            //MyItem myitem = postSnapshot.getValue(MyItem.class);
                            MyItem myItem = new MyItem(lat1,lng1);
                            mClusterManager.addItem(myItem);
                            count+=1;
                            Log.e("HeatMapsActivity", "onDataChange: lat"+lat1+"long:"+lng1);
                        }
                        Toast.makeText(HeatmapsActivity.this, ""+count, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                heatmapslinearlayout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
            }

        });
    }




    private void setUpClusterer() {
        // Position the map.
//        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-1.3806363, 36.7679705), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

        // Set some lat/lng coordinates to start with.
//        double lat = 51.5145160;
//        double lng = -0.1270060;
//        double lat = -1.3806156 ;
//        double lng = 36.7680109;
        double lat = lat1 ;
        double lng = lng1;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MyItem offsetItem = new MyItem(lat, lng);
            mClusterManager.addItem(offsetItem);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HeatmapsActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
        setUpClusterer();

    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {

    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
    final int LOCATION_REQUEST_CODE = 1;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_REQUEST_CODE:{
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mapFragment.getMapAsync(this);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }

                break;
            }
        }
    }


}
