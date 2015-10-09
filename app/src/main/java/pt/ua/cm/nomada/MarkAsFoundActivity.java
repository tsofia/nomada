package pt.ua.cm.nomada;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseSession;

import java.util.ArrayList;
import java.util.List;

public class MarkAsFoundActivity extends Activity {

    private double[] ratingValues = {1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0};
    private String cacheId;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_as_found);

        Typeface type = Typeface.createFromAsset(context.getAssets(), "alrightsans.ttf");
        Button markAsFoundBtn = (Button) findViewById(R.id.markAsFoundBtn);
        markAsFoundBtn.setTypeface(type, Typeface.NORMAL);

        EditText commentEditText = (EditText) findViewById(R.id.markAsFoundCommentEditText);
        commentEditText.setTypeface(type, Typeface.NORMAL);

        TextView commentLabel = (TextView) findViewById(R.id.markAsFoundCommentLabel);
        commentLabel.setTypeface(type, Typeface.NORMAL);

        TextView ratingLabel = (TextView) findViewById(R.id.markAsFoundRatingLabel);
        ratingLabel.setTypeface(type, Typeface.NORMAL);

        TextView titleLabel = (TextView) findViewById(R.id.markAsFoundTitleLabel);
        titleLabel.setTypeface(type, Typeface.NORMAL);


        String[] choices = {"1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0"};

        NumberPicker np = (NumberPicker) findViewById(R.id.markAsFoundRatingPicker);
        np.setMinValue(0);
        np.setMaxValue(choices.length - 1);
        np.setDisplayedValues(choices);
        np.setWrapSelectorWheel(true);

        cacheId = this.getIntent().getExtras().getString("CACHE");
        titleLabel.setText(this.getIntent().getExtras().getString("TITLE"));
    }

    public void markAsFound(View v)    {
        EditText commentEditText = (EditText) findViewById(R.id.markAsFoundCommentEditText);
        NumberPicker np = (NumberPicker) findViewById(R.id.markAsFoundRatingPicker);
        ParseObject newCache = new ParseObject("Found");
        newCache.put("userId", Globals.getUser().getObjectId());
        newCache.put("cacheId", cacheId);
        newCache.put("comment", commentEditText.getText().toString());
        newCache.put("rating", ratingValues[np.getValue()]);
        newCache.saveInBackground();

        this.finish();
    }
}
