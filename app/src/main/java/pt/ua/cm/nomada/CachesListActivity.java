package pt.ua.cm.nomada;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class CachesListActivity extends ListActivity {

    private String choice;
    private ParseObject user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchUser(this.getIntent().getExtras().getString("EMAIL"));

        choice = this.getIntent().getExtras().getString("CHOICE");

        final String[] caches = getCaches();

        this.setListAdapter(new ArrayAdapter<String>(
                this, R.layout.custom_list_item,
                R.id.cacheTitle, caches
        ));

        ListView lView = getListView();

        lView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CachesListActivity.this, CacheDetailActivity.class);
                intent.putExtra("TITLE", caches[position]);
                startActivity(intent);
            }
        });

    }

    private void fetchUser(String email)    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo("email", email);
        try {
            user = query.getFirst();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

    private String[] getCaches()    {
        List<ParseObject> objs = new ArrayList<>();
        ParseQuery<ParseObject> query;

        switch(choice)  {
            case "mine":
                this.setTitle("My caches");
                //List my collection
                query = ParseQuery.getQuery("Cache");
                query.whereEqualTo("author", user.getObjectId());
                try {
                    objs = query.find();
                } catch (com.parse.ParseException e) {
                    e.printStackTrace();
                }
                break;
            case "found":
                this.setTitle("My found caches");
                //List my found items
                query = ParseQuery.getQuery("Found");
                query.whereEqualTo("userId", user.getObjectId());
                List<ParseObject> found = null;
                try {
                    found = query.find();
                } catch (com.parse.ParseException e) {
                    e.printStackTrace();
                }

                for(ParseObject o : found)  {
                    query = ParseQuery.getQuery("Cache");
                    query.whereEqualTo("objectId", o.getString("cacheId"));
                    ParseObject temp = null;
                    try {
                        temp = query.getFirst();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(temp != null)
                        objs.add(temp);
                }

                break;

            case "all":
            default:
                this.setTitle("Caches list");
                //List all caches
                query = ParseQuery.getQuery("Cache");
                query.whereNotEqualTo("objectId", null);
                try {
                    objs = query.find();
                } catch (com.parse.ParseException e) {
                    e.printStackTrace();
                }
                break;
        }

        String[] cachesTitles = new String[objs.size()];
        int i = 0;

        for(ParseObject o : objs)   {
            cachesTitles[i] = o.getString("title");
            i++;
        }

        return cachesTitles;
    }


}
