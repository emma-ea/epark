package com.example.appark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.appark.Model.User3;
import com.example.appark.Model.googleresponse.GooglePlaceModel;
import com.example.appark.Model.googleresponse.GoogleResponseModel;
import com.example.appark.webservices.RetrofitAPI;
import com.example.appark.webservices.RetrofitClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static java.lang.String.valueOf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@RequiresApi(api = Build.VERSION_CODES.M)
@SuppressLint("MissingPermission")  // annoying :)
public class MapsActs extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMarkerClickListener, OnInfoWindowClickListener {

    private static final String TAG = "MapsActs";
    private GoogleMap mMap;
    public GoogleApiClient client;
    private DatabaseReference mUsers;
    Marker marker;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(6.679401, -1.571987);
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    boolean doubleBackToExitPressedOnce = false;

    String[] permissions = {
      Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static final int REQUEST_CODE = 111;

    public static final String CurrentLocationTitle = "Current Location";

    SupportMapFragment mapFragment;

    private List<String> paidCarParks = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_acts);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mUsers = FirebaseDatabase.getInstance().getReference().child("Location");
        mUsers.push().setValue(marker);

        addPaidParks();

    }

    private void addPaidParks() {
        paidCarParks.add("K Poly Car Park");
        paidCarParks.add("Dufie Towers Car Park");
        paidCarParks.add("Accra Financial Centre");
        paidCarParks.add("Pension House Car Park");
        paidCarParks.add("City Car Park");
        paidCarParks.add("Madina Car Park");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        mapFragment.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapFragment.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapFragment.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapFragment.onLowMemory();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnected(@Nullable Bundle bundle) {}
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
    @Override
    public void onLocationChanged(Location location) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setOnInfoWindowClickListener(this);

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            // assume user accepts requests
            getDeviceLocation();
        } else{
            // send rationale to user

        }
    }


    private void getDeviceLocation() {

        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED){
                // get permission
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();

                locationResult.addOnCompleteListener(Objects.requireNonNull(this), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();

                            assert mLastKnownLocation != null;
                            LatLng currentLocation = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                            mMap.addMarker(new MarkerOptions().position(currentLocation).title(CurrentLocationTitle));

                            getParkingLotsNearBy();
                            mUsers.keepSynced(true);

                            Log.v(TAG,String.valueOf(mLastKnownLocation.getLatitude()));
                            Log.v(TAG,String.valueOf(mLastKnownLocation.getLongitude()));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Toast.makeText(MapsActs.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 13));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            } else {
                requestPermissions(permissions, REQUEST_CODE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MapsActs.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    /*
    1. get user's loc
    2. query google maps for car parks around user.
    3. on click of a parking spot. show directions from user's loc
    to car park.
     */

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(MapsActs.this, ParkingActivity.class);
        if (marker.getTitle().contains("K Poly Car Park") ) {
            startActivity(intent);
        }else if (marker.getTitle().contains("Dufie Towers Car Park") ) {
            startActivity(intent);
        }else if (marker.getTitle().contains("Accra Financial Centre") ) {
            startActivity(intent);
        }else if (marker.getTitle().contains("Pension House Car Park") ) {
            startActivity(intent);
        }else if (marker.getTitle().contains("Madina Car Park") ) {
            startActivity(intent);
        } else if (marker.getTitle().contains("City Car Park") ) {
            startActivity(intent);
        }else {
            buildDialog();
            Toast.makeText(getApplicationContext(), "Free parking lot", Toast.LENGTH_LONG).show();
        }
    }

    public void buildDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MapsActs.this);
        builder1.setTitle(getString(R.string.park_reservation_header));
        builder1.setMessage(getString(R.string.park_reservation_msg));
        builder1.setCancelable(true);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.ic_map_directions);
        builder1.setView(imageView);

        builder1.setPositiveButton(
                "Go back to map",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Toast.makeText(this, "Click on the text", Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MapsActs.this, user1.class);
        startActivity(intent);
            super.onBackPressed();
    }

    public void getParkingLotsNearBy() {

        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete[sbMethod]: location for getPlaces obtained");
                    LatLng location = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());

                    double lon = task.getResult().getLongitude();
                    double lat = task.getResult().getLatitude();

                    StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                    sb.append("location=" + lat + "," + lon);
                    sb.append("&radius=5000");
                    sb.append("&types=" + "parking");
                    sb.append("&sensor=true");
                    sb.append("&key=" + getString(R.string.google_maps_key));

                    Log.d(TAG, "api: " + sb.toString());

                    PlacesTask placesTask = new PlacesTask();
                    placesTask.execute(sb.toString());

                }
            }
        });

    }

    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d(TAG, "Background Task "+e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParserTask
            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d(TAG, "Exception while downloading url: "+e.toString());
        } finally {
            try {
                iStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            Place_JSON placeJson = new Place_JSON();

            try {
                jObject = new JSONObject(jsonData[0]);

                places = placeJson.parse(jObject);

            } catch (Exception e) {
                Log.d(TAG, "Exception"+ e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            Log.d(TAG, "Map"+ " list size: " + list.size());
            // Clears all the existing markers;
            mMap.clear();

            for (int i = 0; i < list.size(); i++) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);


                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                // Getting name
                String name = hmPlace.get("place_name");

                Log.d(TAG, "Map"+ "place: " + name);

                // Getting vicinity
                String vicinity = hmPlace.get("vicinity");

                LatLng latLng = new LatLng(lat, lng);

                // Setting the position for the marker
                markerOptions.position(latLng);

                markerOptions.title(name + " : " + vicinity);

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                // Placing a marker on the touched position
                Marker m = mMap.addMarker(markerOptions);

            }
        }
    }

    public class Place_JSON {

        /**
         * Receives a JSONObject and returns a list
         */
        public List<HashMap<String, String>> parse(JSONObject jObject) {

            JSONArray jPlaces = null;
            try {
                /** Retrieves all the elements in the 'places' array */
                jPlaces = jObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /** Invoking getPlaces with the array of json object
             * where each json object represent a place
             */
            return getPlaces(jPlaces);
        }

        private List<HashMap<String, String>> getPlaces(JSONArray jPlaces) {
            int placesCount = jPlaces.length();
            List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> place = null;

            /** Taking each place, parses and adds to list object */
            for (int i = 0; i < placesCount; i++) {
                try {
                    /** Call getPlace with place JSON object to parse the place */
                    place = getPlace((JSONObject) jPlaces.get(i));
                    placesList.add(place);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return placesList;
        }

        /**
         * Parsing the Place JSON object
         */
        private HashMap<String, String> getPlace(JSONObject jPlace) {

            HashMap<String, String> place = new HashMap<String, String>();
            String placeName = "-NA-";
            String vicinity = "-NA-";
            String latitude = "";
            String longitude = "";
            String reference = "";

            try {
                // Extracting Place name, if available
                if (!jPlace.isNull("name")) {
                    placeName = jPlace.getString("name");
                }

                // Extracting Place Vicinity, if available
                if (!jPlace.isNull("vicinity")) {
                    vicinity = jPlace.getString("vicinity");
                }

                latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");
                reference = jPlace.getString("reference");

                place.put("place_name", placeName);
                place.put("vicinity", vicinity);
                place.put("lat", latitude);
                place.put("lng", longitude);
                place.put("reference", reference);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return place;
        }
    }


}
