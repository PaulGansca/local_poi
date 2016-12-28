package com.example.paulgansca.fortech_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener, GoogleMap.OnInfoWindowClickListener {

    protected LocationManager mLocationManager;
    String provider;
    private GoogleMap mMap;
    private Marker mCurrentLocationMarker;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void sendRequest(Marker selectedMarker) {
        try {
            String currentPositionString = mCurrentLocationMarker.getPosition().latitude + "," + mCurrentLocationMarker.getPosition().longitude;
            String destinationPositionString = selectedMarker.getPosition().latitude + "," + selectedMarker.getPosition().longitude;
            new DirectionFinder(this, currentPositionString, destinationPositionString).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = mLocationManager.getBestProvider(criteria, true);

        // mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(this);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (mCurrentLocationMarker == null) {
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.baietas);
                    mCurrentLocationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Current Location").icon(icon));
                }else{
                    mCurrentLocationMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        mLocationManager.requestLocationUpdates(provider, 0, 0, locationListener);
        // Add a marker in Business Center, Cluj-Napoca, and move the camera.
        LatLng fortech = new LatLng(46.751968, 23.575653);
        mMap.addMarker(new MarkerOptions().position(fortech).title("Fortech").icon(BitmapDescriptorFactory.fromResource(R.drawable.fortech)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fortech, 13));
        LatLng scoala = new LatLng(46.764690, 23.624692);
        mMap.addMarker(new MarkerOptions().position(scoala).title("Scoala").icon(BitmapDescriptorFactory.fromResource(R.drawable.school)));
        LatLng mall = new LatLng(46.772388, 23.627900);
        mMap.addMarker(new MarkerOptions().position(mall).title("Mall").icon(BitmapDescriptorFactory.fromResource(R.drawable.mall)));
        LatLng vikingi = new LatLng(46.762956, 23.578335);
        mMap.addMarker(new MarkerOptions().position(vikingi).title("Vikingi").icon(BitmapDescriptorFactory.fromResource(R.drawable.vikingi)));
        LatLng delissima = new LatLng(46.770677, 23.586562);
        mMap.addMarker(new MarkerOptions().position(delissima).title("Delissima").icon(BitmapDescriptorFactory.fromResource(R.drawable.pizza)));

    }

    Marker mSelectedMarker;

    @Override
    public void onInfoWindowClick(Marker marker) {
        mSelectedMarker = marker;
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
        sendRequest(marker);
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));


            polylinePaths.add(mMap.addPolyline(polylineOptions));

        }
        
//        displayDistance(routes.get(0).distance.value);
    }

    BitmapDescriptor flagBitmapDescriptor;

    private void displayDistance(int distanceValue) {
        View distanceMarkerLayout =getLayoutInflater().inflate(R.layout.custom_distance_layout, null);

        distanceMarkerLayout.setDrawingCacheEnabled(true);
        distanceMarkerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        distanceMarkerLayout.layout(0, 0, distanceMarkerLayout.getMeasuredWidth(), distanceMarkerLayout.getMeasuredHeight());
        distanceMarkerLayout.buildDrawingCache();

        TextView positionDistance = (TextView) distanceMarkerLayout.findViewById(R.id.distanceTextView);

        positionDistance.setText(distanceValue + "meters");


        Bitmap flagBitmap = Bitmap.createBitmap(distanceMarkerLayout.getDrawingCache());
        distanceMarkerLayout.setDrawingCacheEnabled(false);
        flagBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(flagBitmap);
        calculateMidPointBetweenCoordinates(mCurrentLocationMarker, mSelectedMarker);
    }

    private void calculateMidPointBetweenCoordinates(Marker originMarker, Marker destinationMarker) {
        double dLon = Math.toRadians(originMarker.getPosition().longitude - destinationMarker.getPosition().longitude);

        double lat1 = Math.toRadians(destinationMarker.getPosition().latitude);
        double lat2 = Math.toRadians(originMarker.getPosition().latitude);
        double lon1 = Math.toRadians(destinationMarker.getPosition().longitude);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        lat3 = Math.toDegrees(lat3);
        lon3 = Math.toDegrees(lon3);
        Marker centerOneMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat3, lon3))
                .icon(flagBitmapDescriptor));
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.paulgansca.fortech_project/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.paulgansca.fortech_project/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}

