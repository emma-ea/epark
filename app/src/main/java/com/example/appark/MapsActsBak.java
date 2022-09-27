package com.example.appark;

import static java.lang.String.valueOf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.appark.Model.User3;
import com.example.appark.Model.googleresponse.GooglePlaceModel;
import com.example.appark.Model.googleresponse.GoogleResponseModel;
import com.example.appark.webservices.RetrofitAPI;
import com.example.appark.webservices.RetrofitClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.M)
@SuppressLint("MissingPermission")  // annoying :)
public class MapsActsBak extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
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

    public static final String CAR_PARKS = "car parks ghana";

    // car parks
    private final LatLng africaCarPark = new LatLng(6.6847165211438595, -1.574053136715041);
    private final LatLng engineeringCarPark = new LatLng(6.678578685151812, -1.5647834229457926);
    private final LatLng kntChemistryCarPark = new LatLng(6.677555704999224, -1.567873327535542);

    // places client
    private PlacesClient placesClient;
    List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
    final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

    RetrofitAPI retrofitAPI;
    private List<GooglePlaceModel> googlePlaceModelList;
    private ArrayList<String> userSavedLocationId;

    private Location currentLocationv2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_acts);

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);

        retrofitAPI = RetrofitClient.getRetrofitClient().create(RetrofitAPI.class);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mUsers = FirebaseDatabase.getInstance().getReference().child("Location");
        mUsers.push().setValue(marker);

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

    /*private boolean getUserLocPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED){
            // get permission
            requestPermissions(permissions, REQUEST_CODE);
        }
        return true;
    }*/

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

    private void drawCarParks(final String placeId) {
        FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
        placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();
                Log.i(TAG, "Place found: " + place.getName());
                LatLng latLngOfPlace = place.getLatLng();
                if (latLngOfPlace != null) {
                    mMap.addMarker(new MarkerOptions().position(latLngOfPlace).title(place.getName()))
                            .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    apiException.printStackTrace();
                    int statusCode = apiException.getStatusCode();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.i(TAG, "place not found: " + e.getMessage());
                    Log.i(TAG, "status code: " + statusCode);
                }
            }
        });
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
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));
                            mMap.addMarker(new MarkerOptions().position(currentLocation).title(CurrentLocationTitle));

                            // getPlacesWithLoc("parking lots");
                            sbMethod();

                            // add car park markers
                            /*mMap.addMarker(new MarkerOptions().position(africaCarPark).title("Africa Car Park"))
                                    .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                            mMap.addMarker(new MarkerOptions().position(engineeringCarPark).title("Engineering Car Park"))
                                    .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                            mMap.addMarker(new MarkerOptions().position(kntChemistryCarPark).title("KNUST Chemistry Car Park"))
                                    .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));*/

                            FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest
                                    .builder()
                                    .setSessionToken(token)
                                    .setTypeFilter(TypeFilter.ADDRESS)
                                    .setQuery(CAR_PARKS)
                                    .build();



//
                            placesClient.findAutocompletePredictions(predictionsRequest)
                                    .addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                                @Override
                                public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                                    if (task.isSuccessful()) {
                                        FindAutocompletePredictionsResponse responses =  task.getResult();
                                        Log.d(TAG, "onComplete: task successful. predictions");
                                        if (responses != null) {
                                            Log.d(TAG, "onComplete: response obtained");
                                            List<AutocompletePrediction> predictions =  responses.getAutocompletePredictions();
                                            for (int i = 0; i < predictions.size(); i++) {
                                                Log.d(TAG, "onComplete: adding predictions +" + (i + 1));
                                                final String placeId = predictions.get(i).getPlaceId();
                                                Toast.makeText(getApplicationContext(), placeId, Toast.LENGTH_LONG).show();
                                                // drawCarParks(placeId);
                                            }
                                        } else {
                                            Log.d(TAG, "onComplete: task response is null");
                                        }
                                    } else {
                                        Log.d(TAG, "onComplete: task not successful");
                                    }
                                }



                            });


                            mUsers.keepSynced(true);

                            mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Toast.makeText(MapsActsBak.this, "in onDataChange", Toast.LENGTH_LONG).show();

                                    for (DataSnapshot s : dataSnapshot.getChildren()){
                                        User3 user = s.getValue(User3.class);
                                        Toast.makeText(MapsActsBak.this, "snapshot: " + user.getName(), Toast.LENGTH_LONG).show();
                                        // assert user != null;
                                        LatLng location=new LatLng(Double.parseDouble(valueOf(user.latitude)),Double.parseDouble(valueOf(user.longitude)));
                                        mMap.addMarker(new MarkerOptions().position(location).title(user.locname))
                                                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            Log.v("Location",String.valueOf(mLastKnownLocation.getLatitude()));
                            Log.v("Location",String.valueOf(mLastKnownLocation.getLongitude()));
                        } else {
                            Log.d("MapsActs", "Current location is null. Using defaults.");
                            Toast.makeText(MapsActsBak.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(MapsActsBak.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
        if (!marker.getTitle().equals(CurrentLocationTitle)) {
            Intent intent = new Intent(MapsActsBak.this, ParkingActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), marker.getTitle(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(this, "Click on the text", Toast.LENGTH_LONG).show();
        return false;
    }





    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MapsActsBak.this, user1.class);
        startActivity(intent);
            super.onBackPressed();
    }


    private void getPlacesWithLoc(String placeName) {
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete[getPlacesWithLoc]: location for getPlaces obtained");
                    LatLng location = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                    getPlaces(placeName, location);
                }
            }
        });
    }

    int radius = 5000;

    private void getPlaces(String placeName, LatLng loc) {

        Toast.makeText(getApplicationContext(), "getPlaces started.", Toast.LENGTH_LONG).show();

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                    + loc.latitude + "," + loc.longitude
                    + "&radius=" + radius + "&type=" + placeName + "&key=" +
                    getResources().getString(R.string.google_maps_key);


        Toast.makeText(getApplicationContext(), "user location found.", Toast.LENGTH_LONG).show();


        retrofitAPI.getNearByPlaces(url).enqueue(new Callback<GoogleResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<GoogleResponseModel> call, @NonNull Response<GoogleResponseModel> response) {
                Gson gson = new Gson();
                String res = gson.toJson(response.body());
                Log.d(TAG, "onResponse: " + res);
                Toast.makeText(getApplicationContext(), "onResponse started. " + res, Toast.LENGTH_LONG).show();
                if (response.errorBody() == null) {
                    if (response.body() != null) {
                        if (response.body().getGooglePlaceModelList() != null && response.body().getGooglePlaceModelList().size() > 0) {

                            mMap.clear();
                            for (int i = 0; i < response.body().getGooglePlaceModelList().size(); i++) {

                                if (userSavedLocationId.contains(response.body().getGooglePlaceModelList().get(i).getPlaceId())) {
                                    // response.body().getGooglePlaceModelList().get(i).setSaved(true);
                                    Toast.makeText(getApplicationContext(), "response.body().getGooglePlaceModelList().get(i).setSaved(true);", Toast.LENGTH_LONG).show();
                                }
                                googlePlaceModelList.add(response.body().getGooglePlaceModelList().get(i));
                                addMarker(response.body().getGooglePlaceModelList().get(i), i);
                            }

                            // googlePlaceAdapter.setGooglePlaceModels(googlePlaceModelList);

                        } else if (response.body().getError() != null) {
                            Toast.makeText(getApplicationContext(), response.body().getError(), Toast.LENGTH_LONG).show();
                        } else {

                            mMap.clear();
                            googlePlaceModelList.clear();
                            //googlePlaceAdapter.setGooglePlaceModels(googlePlaceModelList);
                            radius += 1000;
                            Log.d(TAG, "onResponse: " + radius);
                            getPlaces(placeName, loc);

                        }
                    }

                } else {
                    Log.d(TAG, "onResponse: " + response.errorBody());
                }


            }

            @Override
            public void onFailure(Call<GoogleResponseModel> call, Throwable t) {

                Log.d(TAG, "onFailure: " + t);

            }
        });

    }

    private void addMarker(GooglePlaceModel googlePlaceModel, int position) {

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(googlePlaceModel.getGeometry().getLocation().getLat(),
                        googlePlaceModel.getGeometry().getLocation().getLng()))
                .title(googlePlaceModel.getName());
        mMap.addMarker(markerOptions).setTag(position);
    }

    public void sbMethod() {

        //use your current location here
        double mLatitude = 37.77657;
        double mLongitude = -122.417506;

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
