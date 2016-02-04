package sikander.example.com.tracker;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import sikander.example.com.tracker.R;

public class MainActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback {

    TextView mainLabel;
    ImageButton myLoc,send,get;

    GoogleMap gm;
    MotionEvent me;

    //private HoverInterceptorView mInterceptor;

    public static final int MAP_TYPE_HYBRID = 4;
    public static final int MAP_TYPE_TERRAIN = 3;
    public static final int MAP_TYPE_SATELLITE = 2;
    public static final int MAP_TYPE_NORMAL = 1;
    public static final int MAP_TYPE_NONE = 0;

    private Location mLastLocation;
    public LocationManager mLocationManager;
    private double latitude = 62.083333;
    private double longitude = 9.133333;

    private final IntentFilter intentFilter = new IntentFilter();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new GcmRegistration(this).execute();

        //myLoc= (ImageButton) findViewById(R.id.myLoc);



        int LOCATION_REFRESH_TIME = 1000;
        int LOCATION_REFRESH_DISTANCE = 5;

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        View conta = findViewById(R.id.textView);
        conta.setOnHoverListener(new View.OnHoverListener()

        {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_HOVER_ENTER:
                        Toast.makeText(getApplicationContext(), "hover enter", Toast.LENGTH_LONG).show();
                        System.out.println("hover enter");
                        break;
            /*case MotionEvent.ACTION_HOVER_MOVE:
                Toast.makeText(getApplicationContext(),"hover", Toast.LENGTH_LONG).show();
                break;*/
                    case MotionEvent.ACTION_HOVER_EXIT:
                        Toast.makeText(getApplicationContext(), "hover exit", Toast.LENGTH_LONG).show();
                        System.out.println("hover exit");
                        break;
                    default:
                        break;
                }
                Log.v("Something", "I'm Being Hovered" + event.getAction());

                return false;

            }
        });
    }

    public void get(){

    }

    public void send(){
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //code
            System.out.println("onLocationChanged");

            mLastLocation = location;
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            /*Toast.makeText(getApplicationContext(),"Latitude: " + String.valueOf(longitude)+"\n" +
                    "Longitude: " + String.valueOf(latitude), Toast.LENGTH_LONG).show();

            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("Here I am!");
            gm.addMarker(marker);*/
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            System.out.println("onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            System.out.println("onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            System.out.println("onProviderDisabled");
            //turns off gps services
        }
    };
    //---- Below here is from what the class implements
    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }




    @Override
    public void onMapReady(final GoogleMap googleMap) {
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 5F);

        googleMap.animateCamera(cu);
        gm = googleMap;
        gm.setMyLocationEnabled(true);
        //gm.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

    }



}