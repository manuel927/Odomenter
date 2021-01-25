package co.edu.unipiloto.odomenter;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.Binder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

//import java.util.Random;


public class OdometerService extends Service {

    private static double distanceInMeters;
    private static Location lastLocation = null;
    private LocationListener listener;
    private LocationManager locationManager;
    public static final String PERMISSION_STRING = Manifest.permission.ACCESS_FINE_LOCATION;
    private final IBinder binder = new OdometerBinder();

    public void onCreate(){
        super.onCreate();
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (lastLocation == null){
                    lastLocation = location;
                }
                distanceInMeters +=location.distanceTo(lastLocation);
                lastLocation = location;
            }
        };
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, PERMISSION_STRING)
                == PackageManager.PERMISSION_GRANTED) {
            String provider = locationManager.getBestProvider(new Criteria(),true);
            if (provider != null){
                locationManager.requestLocationUpdates(provider,1000,1,listener);
            }
        }

    }


    //private final Random random = new Random();

    public class OdometerBinder extends Binder{
        OdometerService getOdometer() {
            return OdometerService.this;
        }
    }

    public OdometerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public double getDistance(){
        //return random.nextDouble();
       // return this.distanceInMeters / 1609.344;
        return this.distanceInMeters;
    }

    public void onDestroy(){
        super.onDestroy();
        if (locationManager != null && listener != null){
            if (ContextCompat.checkSelfPermission(this, PERMISSION_STRING)
            == PackageManager.PERMISSION_GRANTED){
                locationManager.removeUpdates(listener);
            }
                locationManager = null;
            listener = null;
        }
    }


}