package mx.com.pendulum.olintareas.location;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import mx.com.pendulum.olintareas.config.util.ContextApplication;

public class MyLocation implements GoogleApiClient.ConnectionCallbacks,
        LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private final long MAX_WAIT_TIME_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS * 2;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    private Context context;
    private Handler mHandler;
    private OnLocationFound onLocationFound;
    private boolean allowUpdates = false;
    private int REQUEST;
    private final Runnable mExpiredRunnable = new Runnable() {
        @Override
        public void run() {
            onLocationChanged(null);
        }
    };

    public MyLocation(Context context, int REQUEST, OnLocationFound onLocationFound) {

        // TODO verify if GPS is already activated

        this.context = context;
        this.onLocationFound = onLocationFound;
        this.REQUEST = REQUEST;


        mHandler = new Handler();
        buildGoogleApiClient();
        mGoogleApiClient.connect();


    }

    public MyLocation(Context context, int REQUEST, OnLocationFound onLocationFound,boolean allowUpdates) {

        // TODO verify if GPS is already activated

        this.context = context;
        this.onLocationFound = onLocationFound;
        this.REQUEST = REQUEST;


        mHandler = new Handler();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        this.allowUpdates = allowUpdates;

    }




    protected void startLocationUpdates() {
        Context context = ContextApplication.getAppContext();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null && mGoogleApiClient.isConnected()) {

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            mHandler.postDelayed(mExpiredRunnable,
                    MAX_WAIT_TIME_IN_MILLISECONDS);
        } else {
            onLocationChanged(mLocation);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setExpirationDuration(MAX_WAIT_TIME_IN_MILLISECONDS);
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    protected void stopLocationUpdates(Location location) {

        if (!allowUpdates) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
        try {
            onLocationFound.onLocationFound(REQUEST, location);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            stopLocationUpdates(location);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public interface OnLocationFound {
        void onLocationFound(int request, Location location);
    }
}