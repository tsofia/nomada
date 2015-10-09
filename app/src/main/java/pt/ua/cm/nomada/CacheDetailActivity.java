package pt.ua.cm.nomada;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class CacheDetailActivity extends Activity {

    private Context context = this;
    private ParseObject cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_detail);


        Typeface type = Typeface.createFromAsset(context.getAssets(), "alrightsans.ttf");
        TextView categoryEditText = (TextView) findViewById(R.id.cacheDetailCategoryEditText);
        categoryEditText.setTypeface(type, Typeface.NORMAL);

        TextView titleEditText = (TextView) findViewById(R.id.cacheDetailTitleEditText);
        titleEditText.setTypeface(type, Typeface.NORMAL);

        TextView textView = (TextView) findViewById(R.id.cacheDetailDescriptionLabel);
        textView.setTypeface(type, Typeface.NORMAL);

        EditText descriptionEditText = (EditText) findViewById(R.id.cacheDetailDescriptionEditText);
        descriptionEditText.setTypeface(type, Typeface.NORMAL);
        descriptionEditText.setInputType(InputType.TYPE_NULL);

        textView = (TextView) findViewById(R.id.cacheDetailGlobalRatingLabel);
        textView.setTypeface(type, Typeface.NORMAL);

        TextView ratingTextView = (TextView) findViewById(R.id.cacheDetailGlobalRatingTextView);
        textView.setTypeface(type, Typeface.NORMAL);

        textView = (TextView) findViewById(R.id.cacheDetailCommentsLabel);
        textView.setTypeface(type, Typeface.NORMAL);

        String title = this.getIntent().getExtras().getString("TITLE");
        getCacheDetails(title);

        titleEditText.setText(cache.getString("title"));
        descriptionEditText.setText(cache.getString("description"));
        categoryEditText.setText(cache.getString("category"));
        ratingTextView.setText(fillCommentsAndRating() +"/5.0");
    }

    private void getCacheDetails(String title)  {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Cache");
        query.whereEqualTo("title", title);
        try {
            cache = query.getFirst();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

    private double fillCommentsAndRating() {
        double rating = 0;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Found");
        query.whereEqualTo("cacheId", cache.getObjectId());
        List<ParseObject> found = null;
        List<String> comments = new ArrayList<>();

        try {
            found = query.find();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        //Get users
        for(ParseObject o : found)  {
            //Get user
            query = ParseQuery.getQuery("User");
            query.whereEqualTo("objectId", o.getString("userId"));
            ParseObject foundUser = null;

            try {
                foundUser = query.getFirst();
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }

            comments.add(foundUser.getString("username") + ":\n\t" + o.getString("comment"));

            //Add rating to count
            if(!Double.isNaN(o.getNumber("rating").doubleValue()))
                rating += o.getNumber("rating").doubleValue();
        }

        //Fill ListView
        ListView list = (ListView) findViewById(R.id.cacheDetailCommentList);

        list.setAdapter(new ArrayAdapter<>(
                this, R.layout.comment_custom_list_item,
                R.id.commentTextView, comments
        ));

        rating = rating / found.size();

        return rating;
    }

    public void markAsFound(View v)   {
        Intent intent = new Intent(CacheDetailActivity.this, MarkAsFoundActivity.class);
        intent.putExtra("CACHE", cache.getObjectId());
        intent.putExtra("TITLE", cache.getString("title"));
        startActivity(intent);
    }

    public void navigate(View v)    {
        Intent intent = new Intent(CacheDetailActivity.this, NavigateActivity.class);
        intent.putExtra("CACHEID", cache.getObjectId());
        startActivity(intent);
    }

}
