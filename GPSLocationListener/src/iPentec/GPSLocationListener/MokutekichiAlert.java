package iPentec.GPSLocationListener;
 
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.MapFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.location.*;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;

// add tentative comments here
// add comments here

public class MokutekichiAlert extends Activity implements LocationListener,
    SearchView.OnQueryTextListener {
    LocationManager locman;
    
    double latiNow = 0.0;
    double longiNow = 0.0;
    Ringtone mRingtone;
    boolean isRing = false;
    boolean isEnableVibe = true;
    String  targetLocation = "";
    double  okiro_km = 1.0;
    MediaPlayer mp;
    
    /** Called when the activity is first created. */
    @SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles" })
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpslocation_listener);
        locman = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locman.getAllProviders();
        Log.v("Providers",providers.toString());
        
        SearchView sview = (SearchView) findViewById(R.id.searchView1);
        // SearchViewの初期表示状態を設定
        sview.setIconifiedByDefault(false);
 
        // SearchViewにOnQueryChangeListenerを設定
        sview.setOnQueryTextListener((OnQueryTextListener) this);
 
        // SearchViewのSubmitボタンを使用不可にする
        sview.setSubmitButtonEnabled(true);
 
        // SearchViewに何も入力していない時のテキストを設定
        sview.setQueryHint("検索文字を入力して下さい。");

    	SharedPreferences pref =
    			getSharedPreferences("pref",MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
    	String str = pref.getString("target","");
    	Log.v("SAVE",str);
    	if (! str.equals("")) {
    		sview.setQuery(str,true);
    	}

        if (locman != null){
            locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,5,this);
        }
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mRingtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
        final MokutekichiAlert gpsla = this;

        final Button button = (Button) findViewById(R.id.button1);
        button.setText("ストップ！");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                if (locman != null){
                    locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,5,gpsla);
                }
            	if (isRing == false)
            	{
                    gpsla.isEnableVibe = false;
            		isRing = true;
                    button.setText("リセット！");
                    Log.v("Button", "On");
                    if (mp.isPlaying()) {
                    	mp.stop();
                    }
            		//mRingtone.play();
            	}
            	else
            	{
            		isRing = false;
            		gpsla.isEnableVibe = true;
                    button.setText("ストップ");
                    Log.v("Button", "Off");
            		//mRingtone.stop();
            	}
            }
        });
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         // アイテムを追加します
        adapter.add("0.5");
        adapter.add("1.0");
        adapter.add("2.0");
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        // アダプターを設定します
        spinner1.setAdapter(adapter);
        // リストビューのアイテムがクリックされた時に呼び出されるコールバックリスナーを登録します
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                 Spinner sp = (Spinner) parent;
                 String item = (String) sp.getSelectedItem();
                gpsla.okiro_km = Double.parseDouble(item);
            }

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        });
        this.mp = MediaPlayer.create(this, R.raw.morning);
//        MapFragment mMapFragment = MapFragment.newInstance();
//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.view1, mMapFragment);
//        fragmentTransaction.commit();
    }
     
    @Override
    protected void onResume(){
        super.onResume();
        if (locman != null){
            locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,this);
     
        }
    }
     
    @SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles" })
	@Override
    protected void onPause(){
        super.onPause();
        if (locman != null){
            locman.removeUpdates(this);
        }
        SharedPreferences pref =
        		getSharedPreferences("pref",MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
        Editor e = pref.edit();
        e.putString("target", this.targetLocation);
        e.commit();
        Log.v("SAVE","SAVE "+this.targetLocation);
    }
    
    @SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles" })
	@Override
    protected void onRestart() {
    	super.onRestart();
    	SharedPreferences pref =
    			getSharedPreferences("pref",MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
    	String str = pref.getString("target","");
    	Log.v("SAVE",str);
    	this.onQueryTextChange(str);
    }
    
    
   	@Override
    public void onLocationChanged(Location location){
        TextView textView3 = (TextView)findViewById(R.id.textView3);
        double distance = getDistance(location.getLatitude(), location.getLongitude(), this.latiNow, this.longiNow) / 1000.00;
        
        textView3.setText(((this.latiNow == 0.0) ? "Z ": "" )+ Double.toString(distance).toString()+"Km");
        
        if ((distance < okiro_km)&&(this.isEnableVibe))
        {
			ringSoundAndVibe();
        }
        
        Log.v("----------", "----------");
        Log.v("Latitude", String.valueOf(location.getLatitude()));
        Log.v("Longitude", String.valueOf(location.getLongitude()));
        Log.v("Accuracy", String.valueOf(location.getAccuracy()));
        Log.v("Altitude", String.valueOf(location.getAltitude()));
        Log.v("Time", String.valueOf(location.getTime()));
        Log.v("Speed", String.valueOf(location.getSpeed())); 
        Log.v("Bearing", String.valueOf(location.getBearing()));
    }

	/**
	 * 
	 */
	public void ringSoundAndVibe() {
        mp.start();
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern = {1000,2500,1000,2500,1000,2500}; // OFF/ON/OFF/ON...
        vibrator.vibrate(pattern, 0);
        this.isEnableVibe = false;
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
        final MokutekichiAlert gpsla = this;
        
        this.targetLocation = String.copyValueOf(query.toCharArray());
        
        TextView textView5 = (TextView)findViewById(R.id.textView5);
        Geocoder geocoder = new Geocoder( gpsla, Locale.getDefault());
        //List<String> providers = locman.getProviders(true);
        locman.requestLocationUpdates("network", 5000, 10, this);
                
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
	@Override
	  protected void onDestroy() {
	    super.onDestroy();
	    // 重要：requestLocationUpdatesしたままアプリを終了すると挙動がおかしくなる。
	    locman.removeUpdates(this);
	    //locman.removeGpsStatusListener(this);
	  }
}
