package data;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by benyi on 12.12.2017.
 */

public class CityPreference{
    SharedPreferences prefs;

    public CityPreference(Activity activity) {
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }
    public String getCity(){
        String defaultStr = "Helsinki,FI";
        return prefs.getString("city", defaultStr);
    }

    public void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }
}
