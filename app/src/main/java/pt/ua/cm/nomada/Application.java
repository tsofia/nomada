package pt.ua.cm.nomada;

import com.parse.Parse;

/**
 * Created by tania on 04/09/2015.
 */
public class Application extends android.app.Application    {

    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "djAp7mlJJU0uLQJaovV24yt474Irp8UrHQpCZ8Pw", "EdTaTmWfNfjp0c76vuWzYewvH0OwqHC0AIt1Ex0A");
    }

}
