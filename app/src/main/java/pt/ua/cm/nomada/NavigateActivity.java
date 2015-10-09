package pt.ua.cm.nomada;

import android.app.Activity;
import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class NavigateActivity extends Activity implements SensorEventListener {

    private ImageView mPointer, wPointer;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f, wCurrentDirection = 0f;
    private Context context = this;
    private Location myLocation, cacheLocation;
    private LocationListener locationListener;
    private long minTime = 2 * 1000; // Minimum time interval for update in seconds, i.e. 5 seconds.
    private long minDistance = 2; // Minimum distance change for update in meters, i.e. 10 meters.
    private LocationManager locationManager;
    GeomagneticField geoField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mPointer = (ImageView) findViewById(R.id.mPointer);
        wPointer = (ImageView) findViewById(R.id.wPointer);

        myLocation = getMyLocation();

        // Don't initialize location manager, retrieve it from system services.
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(context,
                        "Provider enabled: " + provider, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(context,
                        "Provider disabled: " + provider, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onLocationChanged(Location location) {
                // Do work with new location. Implementation of this method will be covered later.

                myLocation = location;
                float distance = myLocation.distanceTo(cacheLocation);
                TextView distanceLabel = (TextView) findViewById(R.id.distanceLabel);
                distanceLabel.setText(Float.toString(distance));
            }
        };

        try {
            locationManager.requestLocationUpdates(getProviderName(), minTime,
                    minDistance, locationListener);

            String cacheId = getIntent().getExtras().getString("CACHEID");
            cacheLocation = getCacheLocation(cacheId);

            Log.i("NavigateActivity.java", "CACHELOCATION" + cacheLocation.getLatitude() + ", " + cacheLocation.getLongitude());
            Log.i("NavigateActivity.java", "MYLOCATION:" + myLocation.getLatitude() + ", " + myLocation.getLongitude());


            GeomagneticField geoField = new GeomagneticField(Double
                    .valueOf(myLocation.getLatitude()).floatValue(), Double
                    .valueOf(myLocation.getLongitude()).floatValue(),
                    Double.valueOf(myLocation.getAltitude()).floatValue(),
                    System.currentTimeMillis());

            float angle = myLocation.bearingTo(cacheLocation) + geoField.getDeclination();
            Log.i("NavigateActivity.java", "ANGLE: " + angle);

            /*RotateAnimation ra = new RotateAnimation(
                    wCurrentDirection,
                    angle,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            wPointer.startAnimation(ra);*/
            wPointer.setRotation(angle);

            float distance = myLocation.distanceTo(cacheLocation);
            TextView distanceLabel = (TextView) findViewById(R.id.distanceLabel);
            distanceLabel.setText(Float.toString(distance));
        } catch(SecurityException e)  {
            Log.e("NavigateActivity.java", e.getMessage());
        }

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
        try {
            locationManager.requestLocationUpdates(getProviderName(), minTime,
                    minDistance, locationListener);
        } catch(SecurityException e) {
            Log.e("NavigateActivity.java", e.getMessage());
        }
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);


        try {
            locationManager.removeUpdates(locationListener);
        } catch(SecurityException e)    {
            Log.e("NavigateActivity.java", e.getMessage());
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);

            ra.setFillAfter(true);

            mPointer.startAnimation(ra);
            wPointer.startAnimation(ra);
            mCurrentDegree = -azimuthInDegress;


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private Location getMyLocation() {
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
            Log.e("NavigateActivity.java", e.getMessage());
        }
        return myLocation;
    }

    String getProviderName() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
        return locationManager.getBestProvider(criteria, true);
    }

    private Location getCacheLocation(String cacheId)   {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Cache");
        query.whereEqualTo("objectId", cacheId);
        ParseObject obj = null;
        try {
            obj = query.getFirst();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        ParseGeoPoint point = obj.getParseGeoPoint("coordinates");
        Location current = new Location("reverseGeocoded");
        current.setLatitude(point.getLatitude());
        current.setLongitude(point.getLongitude());

        return current;
    }
}
