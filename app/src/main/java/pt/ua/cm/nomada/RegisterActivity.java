package pt.ua.cm.nomada;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class RegisterActivity extends Activity {

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Typeface type=Typeface.createFromAsset(context.getAssets(), "alrightsans.ttf");
        TextView textView = (TextView) findViewById(R.id.registerRegBtn);
        textView.setTypeface(type, Typeface.NORMAL);

        textView = (TextView) findViewById(R.id.registerUserInsert);
        textView.setTypeface(type, Typeface.NORMAL);

        textView = (TextView) findViewById(R.id.registerEmailInsert);
        textView.setTypeface(type, Typeface.NORMAL);

        textView = (TextView) findViewById(R.id.registerPwdInsert);
        textView.setTypeface(type, Typeface.NORMAL);

        textView = (TextView) findViewById(R.id.registerPwdConfInsert);
        textView.setTypeface(type, Typeface.NORMAL);

        textView = (TextView) findViewById(R.id.registerBdayLabel);
        textView.setTypeface(type, Typeface.NORMAL);

        // Enable Local Datastore.
        //Parse.enableLocalDatastore(this);
        //Parse.initialize(this, "djAp7mlJJU0uLQJaovV24yt474Irp8UrHQpCZ8Pw", "EdTaTmWfNfjp0c76vuWzYewvH0OwqHC0AIt1Ex0A");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    public void register(View v)  {
        TextView nameView = (TextView) findViewById(R.id.registerUserInsert);
        TextView emailView = (TextView) findViewById(R.id.registerEmailInsert);
        TextView pwdView = (TextView) findViewById(R.id.registerPwdInsert);
        TextView pwdConfView = (TextView) findViewById(R.id.registerPwdConfInsert);
        DatePicker bdayView = (DatePicker) findViewById(R.id.registerBdayDatePicker);

        String name = nameView.getText().toString();
        String email = emailView.getText().toString();
        String password = pwdView.getText().toString();
        String passwordConf = pwdConfView.getText().toString();
        String birthdate = bdayView.getDayOfMonth() + "/" + bdayView.getMonth() + "/" + bdayView.getYear();

        if(name.isEmpty() || email.isEmpty() ||
                password.isEmpty() || passwordConf.isEmpty() ||
                birthdate.isEmpty())    {
            Toast t = Toast.makeText(context, "Please fill all the fields.", Toast.LENGTH_SHORT );
            t.show();
            return;
        }

        if(!password.equals(passwordConf))   {
            Toast t = Toast.makeText(context, "Password mismatch, please try again!", Toast.LENGTH_SHORT );
            t.show();
            return;
        }

        if(!isUnique(email))     {
            Toast t = Toast.makeText(context, "Email already exists, please try again!", Toast.LENGTH_SHORT );
            t.show();
        }
        else    {
            ParseObject newUser = new ParseObject("User");
            newUser.put("username", name);
            newUser.put("password", password);
            newUser.put("email", email);
            newUser.put("birthdate", birthdate);
            //newUser.put("photo", new File("photo"));
            newUser.saveInBackground();

            Toast t = Toast.makeText(context, "Register successful!", Toast.LENGTH_SHORT );
            t.show();

            this.finish();
        }
    }

    public void clearUsernameText(View v)   {
        EditText username = (EditText) findViewById(R.id.registerUserInsert);
        username.setText("");
    }

    public void clearEmailText(View v)   {
        EditText email = (EditText) findViewById(R.id.registerEmailInsert);
        email.setText("");
    }

    private boolean isUnique(String email)  {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo("email", email);
        ParseObject obj = null;
        try {
            obj = query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(obj == null)
            return true;
        else
            return false;
    }
}
