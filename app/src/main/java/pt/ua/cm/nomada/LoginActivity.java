package pt.ua.cm.nomada;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Iterator;
import java.util.List;

public class LoginActivity extends Activity {

    Context context = this;
    String loginType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Typeface type = Typeface.createFromAsset(context.getAssets(), "alrightsans.ttf");
        TextView textView = (TextView) findViewById(R.id.loginEmailLabel);
        textView.setTypeface(type, Typeface.NORMAL);

        textView = (TextView) findViewById(R.id.loginEmailInsert);
        textView.setTypeface(type, Typeface.NORMAL);

        textView = (TextView) findViewById(R.id.loginPasswordLabel);
        textView.setTypeface(type, Typeface.NORMAL);

        textView = (TextView) findViewById(R.id.loginPwdInsert);
        textView.setTypeface(type, Typeface.NORMAL);

        if(!getIntent().getExtras().getString("LOGINTYPE").equals(null)
                && !getIntent().getExtras().getString("LOGINTYPE").isEmpty())
            loginType = getIntent().getExtras().getString("LOGINTYPE");

        // Enable Local Datastore.
        //Parse.enableLocalDatastore(this);
        //Parse.initialize(this, "djAp7mlJJU0uLQJaovV24yt474Irp8UrHQpCZ8Pw", "EdTaTmWfNfjp0c76vuWzYewvH0OwqHC0AIt1Ex0A");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void login(View v) {
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if(!cd.isConnectingToInternet()) {
            Toast t = Toast.makeText(context, "Please connect to the internet to use Nomada!", Toast.LENGTH_SHORT );
            t.show();
            return;
        }


        EditText user = (EditText) findViewById(R.id.loginEmailInsert);
        EditText pwd = (EditText) findViewById(R.id.loginPwdInsert);

        String username = user.getText().toString();
        String password = pwd.getText().toString();

        boolean isValid = false;

        if(loginType.equalsIgnoreCase("facebook"))
            isValid = facebookLogin(username, password);
        else
            isValid = checkCredentials(username, password);

        if(isValid) {
            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            intent.putExtra("EMAIL", username);
            startActivity(intent);
        }
        else    {
            Toast t = Toast.makeText(context, "Login has failed. Try again!", Toast.LENGTH_SHORT );
            t.show();
        }
    }

    private boolean checkCredentials(String user, String password)  {
        boolean ret = false;

        if(user.isEmpty() || password.isEmpty())
            return ret;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo("email", user);
        List<ParseObject> obj = null;
        try {
            obj = query.find();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        Iterator i = obj.iterator();
        while(i.hasNext())  {
            ParseObject o = (ParseObject) i.next();
            if(o.getString("password").equals(password)) {
                ret = true;
                Globals.setUser(o);
                break;
            }
        }

        return ret;
    }

    private boolean facebookLogin(String user, String password)  {
        boolean ret = false;

        return ret;
    }

    @Override
    protected void onResume()   {
        super.onResume();
        TextView textView = (TextView) findViewById(R.id.loginPwdInsert);
        textView.setText("");

        textView = (TextView) findViewById(R.id.loginEmailInsert);
        textView.setText("");
    }

    @Override
    protected void onStart()    {
        super.onStart();
        onResume();
    }
}
