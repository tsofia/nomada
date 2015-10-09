package pt.ua.cm.nomada;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Iterator;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private String category;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static final String LOG_TAG = "MapsActivity.java";
    protected GoogleApiClient mGoogleApiClient;
    private Location myLocation;

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        buildGoogleApiClient();
        setCategory();
        setUpMapIfNeeded();

        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void setCategory() {
        Bundle extras = getIntent().getExtras();
        String value = "Category";

        if (extras != null)
            value = extras.getString("CATEGORY");

        this.category = value;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        Location location = myLocation;

        if (location != null)
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));

            Log.i(LOG_TAG, location.getLatitude() + ", " + location.getLongitude());
        }

        List<ParseObject> parsedDataSet = getCaches();

        Iterator i = parsedDataSet.iterator();

        while(i.hasNext())  {
            ParseObject c = (ParseObject)i.next();
            Log.i(LOG_TAG, "Checking: " + c.getString("title") + ", " + c.getString("category"));

            //Add Markers
            if(category.equals("all"))  {
                Log.i(LOG_TAG, "Adding: " + c.getString("title") + ", " + c.getString("category"));
                LatLng pt = new LatLng(c.getParseGeoPoint("coordinates").getLatitude(), c.getParseGeoPoint("coordinates").getLongitude());
                mMap.addMarker(new MarkerOptions().position(pt).title(c.getString("title")).snippet("" + c.getObjectId()));
            }
            else if(c.getString("category").equals(category)) {
                Log.i(LOG_TAG, "Adding: " + c.getString("title") + ", " + c.getString("category"));
                LatLng pt = new LatLng(c.getParseGeoPoint("coordinates").getLatitude(), c.getParseGeoPoint("coordinates").getLongitude());
                mMap.addMarker(new MarkerOptions().position(pt).title(c.getString("title")).snippet("" + c.getObjectId()));
            }
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                Intent intent = new Intent(MapsActivity.this, CacheDetailActivity.class);
                intent.putExtra("TITLE", arg0.getTitle());
                startActivity(intent);
                return true;
            }

        });
    }

    private List<ParseObject> getCaches() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Cache");
        query.whereEqualTo("category", category);
        List<ParseObject> obj = null;
        try {
            obj = query.find();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        return obj;
    }

    /*private Location getMyLocation() {
        Location myLocation = null;
        try {
            // Get location from GPS if it's available

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // Location wasn't found, check the next most accurate place for the current location
            if (myLocation == null) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                // Finds a provider that matches the criteria
                String provider = lm.getBestProvider(criteria, true);
                // Use the provider to get the last known location
                myLocation = lm.getLastKnownLocation(provider);
            }
        }
        catch(SecurityException e)    {
            Log.e(LOG_TAG, e.getMessage());
        }
        return myLocation;
    }*/

    @Override
    public void onConnected(Bundle bundle) {
        myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
