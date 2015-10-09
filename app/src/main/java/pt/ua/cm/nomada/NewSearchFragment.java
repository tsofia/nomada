package pt.ua.cm.nomada;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by tania on 02/09/2015.
 */
public class NewSearchFragment extends Fragment {

    protected Activity mActivity;
    Context context;

    public NewSearchFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        context = mActivity.getApplicationContext();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_search, container, false);

        Typeface type=Typeface.createFromAsset(context.getAssets(), "alrightsans.ttf");
        TextView textView = (TextView) rootView.findViewById(R.id.musicCacheButton);
        textView.setTypeface(type, Typeface.NORMAL);

        textView = (TextView) rootView.findViewById(R.id.mediaCacheButton);
        textView.setTypeface(type, Typeface.NORMAL);

        textView = (TextView) rootView.findViewById(R.id.bookCachesButton);
        textView.setTypeface(type, Typeface.NORMAL);

        textView = (TextView) rootView.findViewById(R.id.randomCachesButton);
        textView.setTypeface(type, Typeface.NORMAL);

        return rootView;
    }



}
