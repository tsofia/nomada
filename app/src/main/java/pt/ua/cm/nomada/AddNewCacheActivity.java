package pt.ua.cm.nomada;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class AddNewCacheActivity extends Activity {

    private static final String LOG_TAG = "AddNewCacheActivity";
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_cache);

        Typeface type = Typeface.createFromAsset(context.getAssets(), "alrightsans.ttf");
        EditText titleEditText = (EditText) findViewById(R.id.newCacheTitleEditText);
        titleEditText.setTypeface(type, Typeface.NORMAL);

        EditText categoryEditText = (EditText) findViewById(R.id.newCacheCategoryEditText);
        categoryEditText.setTypeface(type, Typeface.NORMAL);

        TextView descriptionLabel = (TextView)findViewById(R.id.newCacheDescriptionLabel);
        descriptionLabel.setTypeface(type, Typeface.NORMAL);

        EditText descriptionEditText = (EditText) findViewById(R.id.newCacheDescriptionEditText);
        descriptionEditText.setTypeface(type, Typeface.NORMAL);

        TextView coordsLabel = (TextView)findViewById(R.id.newCacheCoordsLabel);
        coordsLabel.setTypeface(type, Typeface.NORMAL);

        EditText latitudeEditText = (EditText) findViewById(R.id.newCacheLatitudeEditText);
        latitudeEditText.setTypeface(type, Typeface.NORMAL);

        EditText longitudeEditText = (EditText) findViewById(R.id.newCacheLongitudeEditText);
        longitudeEditText.setTypeface(type, Typeface.NORMAL);

        TextView separatorLabel = (TextView)findViewById(R.id.textView2);
        separatorLabel.setTypeface(type, Typeface.NORMAL);

        Button saveBtn = (Button) findViewById(R.id.newCacheSaveBtn);
        saveBtn.setTypeface(type, Typeface.NORMAL);

        Button currLocationBtn = (Button) findViewById(R.id.newCacheCurrLocationBtn);
        currLocationBtn.setTypeface(type, Typeface.NORMAL);
    }

    public void getMyLocation(View v) {
        Location myLocation = null;
        try {
            // Get location from GPS if it's available
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // Location wasn't found, check the next most accurate place for the current location
            if (myLocation == null) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                // Finds a provider that matches the criteria
                String provider = lm.getBestProvider(criteria, true);
                // Use the provider to get the last known location
                myLocation = lm.getLastKnownLocation(provider);
            }
        }
        catch(SecurityException e)    {
            Log.e(LOG_TAG, e.getMessage());
        }

        EditText latitudeEditText = (EditText) findViewById(R.id.newCacheLatitudeEditText);
        EditText longitudeEditText = (EditText) findViewById(R.id.newCacheLongitudeEditText);
        latitudeEditText.setText(String.valueOf(myLocation.getLatitude()));
        longitudeEditText.setText(String.valueOf(myLocation.getLongitude()));
    }

    public void saveMyCache(View v) {
        EditText titleEditText = (EditText) findViewById(R.id.newCacheTitleEditText);
        EditText categoryEditText = (EditText) findViewById(R.id.newCacheCategoryEditText);
        EditText descriptionEditText = (EditText) findViewById(R.id.newCacheDescriptionEditText);
        EditText latitudeEditText = (EditText) findViewById(R.id.newCacheLatitudeEditText);
        EditText longitudeEditText = (EditText) findViewById(R.id.newCacheLongitudeEditText);

        String title = titleEditText.getText().toString();
        String category = categoryEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String latitude = latitudeEditText.getText().toString();
        String longitude = longitudeEditText.getText().toString();

        if(title.isEmpty() || category.isEmpty() || description.isEmpty()
                || latitude.isEmpty() || longitude.isEmpty())  {
            Toast t = Toast.makeText(context, "Please fill all the fields!", Toast.LENGTH_SHORT );
            t.show();
            return;
        }

        ParseObject cache = new ParseObject("Cache");
        cache.put("author", Globals.getUser().getObjectId());
        cache.put("category", category);
        cache.put("title", title);
        cache.put("description", description);
        cache.put("coordinates", new ParseGeoPoint(Double.valueOf(latitude), Double.valueOf(longitude)));
        cache.saveInBackground();

        this.finish();
    }
}
