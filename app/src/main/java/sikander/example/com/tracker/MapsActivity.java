package sikander.example.com.tracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;


    TextView info;

    GoogleMap gm;
    Marker testMarker;
    GoogleDirection gd;

    //private HoverInterceptorView mInterceptor;

    public static final int MAP_TYPE_HYBRID = 4;
    public static final int MAP_TYPE_TERRAIN = 3;
    public static final int MAP_TYPE_SATELLITE = 2;
    public static final int MAP_TYPE_NORMAL = 1;
    public static final int MAP_TYPE_NONE = 0;

    private Location mLastLocation;
    public LocationManager mLocationManager;

    private final IntentFilter intentFilter = new IntentFilter();

    private String[] contacts;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        info = (TextView) findViewById(R.id.info);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gm = googleMap;
        CameraUpdate cu = null;

        double latitude = 62.083333;
        double longitude = 9.133333;

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //Do what you need if enabled...
            gm.setMyLocationEnabled(true);
            Location desP = gm.getMyLocation();
            if(desP != null){
                LatLng StartP = new LatLng(desP.getLatitude(), desP.getLongitude());
                cu = CameraUpdateFactory.newLatLngZoom(StartP, 10F);
            }
            else cu = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 5F);
        }else{
            //Do what you need if not enabled...
            cu = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 5F);
            buildAlertMessageNoGps();
        }



        googleMap.animateCamera(cu);

        gm.setOnMarkerClickListener(this);
        gm.setOnMapClickListener(this);
        double latitudeT = 63.083333;
        double longitudeT = 10.133333;
        testMarker = gm.addMarker(new MarkerOptions()
                .position(new LatLng(latitudeT, longitudeT))
                .title("Trondheim"));

        double latitudeA = 59.8333300;
        double longitudeA = 10.4372100;
        testMarker = gm.addMarker(new MarkerOptions()
                .position(new LatLng(latitudeA, longitudeA))
                .title("Asker"));


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

    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius=6371;//radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        double km=valueResult/1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec =  Integer.valueOf(newFormat.format(km));
        double meter=valueResult%1000;
        int  meterInDec= Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);

        return Radius * c;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

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
    public boolean onMarkerClick(final Marker marker) {
        Location desP = gm.getMyLocation();
        LatLng StartP = new LatLng(desP.getLatitude(), desP.getLongitude());
        double distance = CalculationByDistance(StartP,marker.getPosition());
        distance = (int)((double) Math.round(distance * 1000));
        if(info.getVisibility() == View.INVISIBLE) {
            info.setText("     " + marker.getTitle() + "\n" + "     " + distance * 1000 + " meters from you");
            info.startAnimation(AnimationUtils.loadAnimation(MapsActivity.this, android.R.anim.fade_in));
            info.setVisibility(View.VISIBLE);
        }

        GoogleDirection.withServerKey("AIzaSyD7Bt0ILWBqhi3JV8RloYgcgIhTqr2TRUU").from(StartP)
                .to(marker.getPosition())
                .avoid(AvoidType.FERRIES)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction) {
                        if (direction.isOK()) {
                            // ArrayList<LatLng> directionPositionList =
                            //gm.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.RED));
                            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.RED);
                            gm.addPolyline(polylineOptions);
                        } else {
                            String status = direction.getStatus();
                            Toast.makeText(getApplicationContext(),
                                    "Unable to find directions to " + marker.getTitle() + ". " + status,
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Toast.makeText(getApplicationContext(),
                                "Unable to find directions to " + marker.getTitle(),
                                Toast.LENGTH_LONG).show();

                    }
                });

        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(info.getVisibility() == View.VISIBLE){
            info.startAnimation(AnimationUtils.loadAnimation(MapsActivity.this, android.R.anim.fade_out));
            Log.i("VISIBILITY: ", "" + info.getVisibility());
            info.setVisibility(View.INVISIBLE);
            Log.i("VISIBILITY: ", ""+info.getVisibility());

        }
        //info.setVisibility(View.GONE);
    }

    public void goToSettings(View view){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }


}
