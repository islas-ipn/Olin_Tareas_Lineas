package mx.com.pendulum.olintareas.ui.activities.tareas;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.location.MyLocation;
import mx.com.pendulum.olintareas.ui.CustomDialog;
import mx.com.pendulum.olintareas.ui.parents.AppCompatActivityParent;
import mx.com.pendulum.utilities.Tools;
import mx.com.pendulum.utilities.Util;

public class GetLocationActivity extends AppCompatActivityParent implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {

    private GoogleMap gMap;
    private LatLng pencelPosition;
    private LatLng selectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);



        initMap();
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.gMap = googleMap;
        UiSettings ui = googleMap.getUiSettings();

//        ui.setMapToolbarEnabled(false);
//        ui.setMyLocationButtonEnabled(true);
//        ui.setScrollGesturesEnabled(false);
//        ui.setZoomGesturesEnabled(false);
//        ui.setRotateGesturesEnabled(false);
//        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnMapClickListener(this);

        googleMap.setOnMarkerDragListener(this);
        googleMap.setMyLocationEnabled(true);


        new MyLocation(this, 0, new MyLocation.OnLocationFound() {
            @Override
            public void onLocationFound(int request, Location location) {
                if (location != null) {

                    LatLng la = new LatLng(location.getLatitude(), location.getLongitude());

                    pencelPosition = la;
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pencelPosition, 17.8f);
                    gMap.moveCamera(cameraUpdate);

                    setMarker(la);
//                    googleMap.setOnMarkerDragListener(GetLocationActivity.this);
                }
            }
        }, true);


    }


    @Override
    public void onMapClick(LatLng la) {


        setMarker(la);

    }

    private void setMarker(LatLng la) {

        double dist = Tools.distance(la, pencelPosition);
        MarkerOptions markerOptions = new MarkerOptions();
        if (dist < 100) {


            markerOptions.position(la);
            selectedPosition = la;

        } else {
            markerOptions.position(pencelPosition);
            selectedPosition = pencelPosition;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pencelPosition, 17.8f);
            gMap.moveCamera(cameraUpdate);
            Toast.makeText(this, "Favor de seleccionar dentro del circulo.", Toast.LENGTH_SHORT).show();
        }


        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin));
        markerOptions.draggable(true);

        gMap.clear();
        gMap.addCircle(new CircleOptions().center(pencelPosition).radius(100).strokeColor(Color.RED).fillColor(Color.TRANSPARENT));
        gMap.addMarker(markerOptions);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ok_cancel_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_ok:

                int position = getArguments().getInt("POSITION", 0);

                Intent intent = getIntent();
                intent.putExtra("LATITUD", selectedPosition.latitude);
                intent.putExtra("LONGITUD", selectedPosition.longitude);
                intent.putExtra("POSITION", position);
                setResult(RESULT_OK, intent);

                getActivity().finish();
                return true;
            case R.id.menu_cancel:
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        CustomDialog.salirSinGuardar(getActivity(), new Interfaces.OnResponse<Object>() {
            @Override
            public void onResponse(int handlerCode, Object o) {
                if ((boolean) o) {
                    Intent intent = getIntent();
                    setResult(RESULT_CANCELED, intent);
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng latLng = marker.getPosition();
        setMarker(latLng);
    }


}
