package pt.ua.cm.nomada;

import com.parse.ParseObject;

/**
 * Created by tania on 10/09/2015.
 */
public class Globals {
    private static ParseObject user;

    public static ParseObject getUser() {
        return user;
    }

    public static void setUser(ParseObject newUser) {
        user = newUser;
    }
}
