package iPentec.GPSLocationListener;
 
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.location.*;
import android.content.*;
import android.util.Log;
import android.widget.TextView;

// add tentative comments here
// add comments here

public class GPSLocationListenerActivity extends Activity implements LocationListener {
    LocationManager locman;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpslocation_listener);
        locman = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
   }
     
    @Override
    protected void onResume(){
        if (locman != null){
            locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,this);
     
        }
        super.onResume();
    }
     
    @Override
    protected void onPause(){
        if (locman != null){
            locman.removeUpdates(this);
        }
        super.onPause();
    }
    
     
    @Override
    public void onLocationChanged(Location location){
        TextView textView1 = (TextView)findViewById(R.id.textView1);
        textView1.setText("Latitude:Longitude - "
        +String.valueOf(location.getLatitude()) +":"+String.valueOf(location.getLongitude()));
         
        TextView textView5 = (TextView)findViewById(R.id.textView5);
                         
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        
        try{
        	boolean b = false;
          List<Address> addressList = geocoder.getFromLocationName("東京都渋谷区神南２丁目２－１", 1);
          if (addressList.size() == 0) {
        	  textView5.setText("見つかりませんでした");
        	  b = true;
          }
          if (!b) {
        	  Address address = addressList.get(0);
 
        	  double lat = address.getLatitude();
        	  double lng = address.getLongitude();
        	  String adr=Double.toString(lat)+","+Double.toString(lng);
        	  textView5.setText(adr);
          }
        }catch(IOException e){
              textView5.setText("IOException 発生");
        }
labe: 
         
        Log.v("----------", "----------");
        Log.v("Latitude", String.valueOf(location.getLatitude()));
        Log.v("Longitude", String.valueOf(location.getLongitude()));
        Log.v("Accuracy", String.valueOf(location.getAccuracy()));
        Log.v("Altitude", String.valueOf(location.getAltitude()));
        Log.v("Time", String.valueOf(location.getTime()));
        Log.v("Speed", String.valueOf(location.getSpeed())); 
        Log.v("Bearing", String.valueOf(location.getBearing()));
    }
     
    @Override
    public void onProviderDisabled(String provider){
    	Log.e("LOCATION", "disabled");
    }
     
    @Override
    public void onProviderEnabled(String provider){
    	Log.e("LOCATION", "enabled");
    }
     
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
    	Log.e("LOCATION", "status:" + status);
        switch(status){
        case LocationProvider.AVAILABLE:
            Log.v("Status","AVAILABLE");
            break;
        case LocationProvider.OUT_OF_SERVICE:
            Log.v("Status","OUT_OF_SERVICE");
            break;
        case  LocationProvider.TEMPORARILY_UNAVAILABLE:
            Log.v("Status","TEMPORARILY_UNAVAILABLE");
            break;
             
        }
    }
}
