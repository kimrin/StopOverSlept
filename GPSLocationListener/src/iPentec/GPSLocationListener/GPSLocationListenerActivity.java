package iPentec.GPSLocationListener;
 
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.location.*;
import android.content.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

// add tentative comments here
// add comments here

public class GPSLocationListenerActivity extends Activity implements LocationListener,
    SearchView.OnQueryTextListener {
    LocationManager locman;
    
    double latiNow = 0.0;
    double longiNow = 0.0;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpslocation_listener);
        locman = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        SearchView sview = (SearchView) findViewById(R.id.searchView1);
        // SearchViewの初期表示状態を設定
        sview.setIconifiedByDefault(false);
 
        // SearchViewにOnQueryChangeListenerを設定
        sview.setOnQueryTextListener((OnQueryTextListener) this);
 
        // SearchViewのSubmitボタンを使用不可にする
        sview.setSubmitButtonEnabled(true);
 
        // SearchViewに何も入力していない時のテキストを設定
        sview.setQueryHint("検索文字を入力して下さい。");

        if (locman != null){
            locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,5,this);
     
        }
 
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
        TextView textView3 = (TextView)findViewById(R.id.textView3);
        textView3.setText(""+ Double.toString(getDistance(location.getLatitude(), location.getLongitude(), this.latiNow, this.longiNow)).toString()+"Km");
         
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

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(final String query) {
		// TODO Auto-generated method stub
        final GPSLocationListenerActivity gpsla = this;
        TextView textView5 = (TextView)findViewById(R.id.textView5);
        Geocoder geocoder = new Geocoder( gpsla, Locale.getDefault());
                
        try{
        	boolean b = false;
            List<Address> addressList = geocoder.getFromLocationName(query, 1);
            if (addressList.size() == 0) {
                if (Geocoder.isPresent() == false)
                {
                	textView5.setText("background service is not available!");
                }
                else
                {
                	textView5.setText("見つかりませんでした");
                }
                b = true;
            }
            if (!b) {
            	Address address = addressList.get(0);
         
                double lat = address.getLatitude();
                double lng = address.getLongitude();
                String adr=Double.toString(lat)+","+Double.toString(lng);
                textView5.setText(adr);
                
                gpsla.latiNow = lat;
                gpsla.longiNow = lng;
            }
        }catch(IOException e){
        	textView5.setText("IOException 発生");
        }
        return false;
    }
	
	public double getDistance(double lat1, double lon1, double lat2, double lon2) {
		double a_lat = lat1 * Math.PI / 180;
		double a_lon = lon1 * Math.PI / 180;
		double b_lat = lat2 * Math.PI / 180;
		double b_lon = lon2 * Math.PI / 180;

		// 緯度の平均、緯度間の差、経度間の差
		double latave = (a_lat + b_lat) / 2;
		double latidiff = a_lat - b_lat;
		double longdiff = a_lon - b_lon;

		//子午線曲率半径
		//半径を6335439m、離心率を0.006694で設定してます
		double meridian = 6335439 / Math.sqrt(Math.pow(1 - 0.006694 * Math.sin(latave) * Math.sin(latave), 3));    

		//卯酉線曲率半径
		//半径を6378137m、離心率を0.006694で設定してます
		double primevertical = 6378137 / Math.sqrt(1 - 0.006694 * Math.sin(latave) * Math.sin(latave));     

		//Hubenyの簡易式
		double x = meridian * latidiff;
		double y = primevertical * Math.cos(latave) * longdiff;

		return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
	}
}
