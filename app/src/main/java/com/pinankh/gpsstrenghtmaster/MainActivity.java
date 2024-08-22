package com.pinankh.gpsstrenghtmaster;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.pinankh.gpssignalstrenght.GpsStatusImageView;
import com.pinankh.gpssignalstrenght.GpsStatusListener;
import com.pinankh.gpssignalstrenght.GpsStatusProxy;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager = null;
    private GpsStatusProxy proxy;
    private final int CODE_LOCATION_PERMISSION = 1;
    private GpsStatusImageView gpsImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        proxy = GpsStatusProxy.getInstance(getApplicationContext());
        gpsImage= (GpsStatusImageView) findViewById(R.id.gpsImage);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            List<String> permissionsNeeded = new ArrayList<String>();
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), CODE_LOCATION_PERMISSION);
            return;
        } else {
            proxy.register();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, listener);
        }
        gpsImage.addListener(new GpsStatusListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onStop() {

            }

            @Override
            public void onFixed() {

            }

            @Override
            public void onUnFixed() {

            }

            @Override
            public void onSignalStrength(int inUse, int count) {
                float avgSnr = (inUse > 0) ? count / inUse: 0.0f;
                Log.d(TAG, "Signal Strength: " + inUse + " SNR: " + count+"avg SNR: "+avgSnr);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(listener);
        proxy.unRegister();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_LOCATION_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                proxy.register();
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, listener);
            }
        }
    }

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            proxy.notifyLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}