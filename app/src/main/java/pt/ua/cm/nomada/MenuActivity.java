package pt.ua.cm.nomada;

import android.app.Activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Iterator;
import java.util.List;

public class MenuActivity extends Activity {

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    ActionBarDrawerToggle mDrawerToggle;
    private Context context = this;
    private String currUser;
    private ParseObject user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        String email = getIntent().getExtras().getString("EMAIL");
        user = getUser(email);

        TextView greeting = (TextView) findViewById(R.id.greetingLabel);
        Typeface type=Typeface.createFromAsset(context.getAssets(), "alrightsans.ttf");
        greeting.setTypeface(type, Typeface.NORMAL);

        currUser = user.getString("username");

        greeting.setText("Hello\n" + currUser + "!");

        mNavigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[5];

        drawerItem[0] = new ObjectDrawerItem(R.drawable.ic_action_flag, "New Search");
        drawerItem[1] = new ObjectDrawerItem(R.drawable.ic_action_profile, "Profile");
        drawerItem[2] = new ObjectDrawerItem(R.drawable.ic_action_object, "Caches");
        drawerItem[3] = new ObjectDrawerItem(R.drawable.ic_action_about, "About");
        drawerItem[4] = new ObjectDrawerItem(R.drawable.ic_action_logout, "Logout");


        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.listview_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mTitle = mDrawerTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);


    }

    private class DrawerItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private void selectItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new NewSearchFragment();
                break;
            case 1:
                fragment = new ProfileFragment();
                break;

            case 2:
                fragment = new ListFragment();
                break;

            case 3:
                fragment = new AboutFragment();
                break;

            case 4:
                fragment = new LogoutFragment();
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    public ParseObject getUser(String email)    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo("email", email);
        ParseObject obj = null;
        try {
            obj = query.getFirst();
        } catch (com.parse.ParseException e) {
            return null;
        }

        return obj;
    }

    public void startMapWithRandom(View v)  {
        double rnd = Math.random();

        if(rnd < 0.33)
            startMapWithBook(v);
        else if(rnd < 0.66)
            startMapWithMedia(v);
        else
            startMapWithMusic(v);
    }

    public void startMapWithMusic(View v)  {
        Intent intent = new Intent(MenuActivity.this, MapsActivity.class);
        intent.putExtra("CATEGORY", "music");
        startActivity(intent);
    }

    public void startMapWithBook(View v)  {
        Intent intent = new Intent(MenuActivity.this, MapsActivity.class);
        intent.putExtra("CATEGORY", "book");
        startActivity(intent);
    }

    public void startMapWithMedia(View v)  {
        Intent intent = new Intent(MenuActivity.this, MapsActivity.class);
        intent.putExtra("CATEGORY", "media");
        startActivity(intent);
    }

    public void changePassword(View v)    {
        showPwdChangePopup(MenuActivity.this);
    }

    private void showPwdChangePopup(final MenuActivity context) {

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.change_pwd_dialog);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.change_password_dialog, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(200);
        popup.setHeight(150);
        popup.setFocusable(true);

        // Getting a reference to Close button, and close the popup when clicked.
        Button change = (Button) layout.findViewById(R.id.changePwdBtn);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
                query.whereEqualTo("username", currUser);
                ParseObject obj = null;
                try {
                    obj = query.getFirst();
                } catch (com.parse.ParseException e) {
                    e.printStackTrace();
                    return;
                }

                String password = obj.getString("password");

                EditText currPwdEdit = (EditText) findViewById(R.id.changePwdCurrPwdInsert);
                if(!currPwdEdit.getText().toString().equals(password))   {
                    Toast t = Toast.makeText(context, "Current password incorrect!", Toast.LENGTH_SHORT );
                    t.show();
                    return;
                }

                EditText newPwd = (EditText) findViewById(R.id.changePwdNewPwdInsert);
                EditText confPwd = (EditText) findViewById(R.id.changePwdConfInsert);
                if(!newPwd.getText().toString().equals(confPwd.getText().toString())) {
                    Toast t = Toast.makeText(context, "Password mismatch!", Toast.LENGTH_SHORT );
                    t.show();
                    return;
                }

                obj.put("password", newPwd.getText().toString());
                obj.saveInBackground();

                Toast t = Toast.makeText(context, "Password changed!", Toast.LENGTH_SHORT );
                t.show();

                popup.dismiss();
            }
        });

        popup.showAtLocation(popup.getContentView(), Gravity.CENTER, 0, 0);
    }

    public void logout(View v)  {
            //super.onBackPressed();
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
    }

    public void listAllCaches(View v)   {
        Intent intent = new Intent(MenuActivity.this, CachesListActivity.class);
        intent.putExtra("EMAIL", user.getString("email"));
        intent.putExtra("CHOICE", "all");
        startActivity(intent);
    }

    public void listFoundCaches(View v)   {
        Intent intent = new Intent(MenuActivity.this, CachesListActivity.class);
        intent.putExtra("EMAIL", user.getString("email"));
        intent.putExtra("CHOICE", "found");
        startActivity(intent);
    }

    public void listMyCaches(View v)   {
        Intent intent = new Intent(MenuActivity.this, CachesListActivity.class);
        intent.putExtra("EMAIL", user.getString("email"));
        intent.putExtra("CHOICE", "mine");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void addNewCache(View v) {
        Intent intent = new Intent(MenuActivity.this, AddNewCacheActivity.class);
        startActivity(intent);
    }
}

