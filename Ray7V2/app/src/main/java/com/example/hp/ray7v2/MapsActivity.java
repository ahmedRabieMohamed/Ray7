package com.example.hp.ray7v2;

import android.Manifest;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener {



    private GoogleMap mMap;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    ImageButton GpsButton,removeOrigin,removeDestenation;
    Button request,takeMyCar;
    int flag = 0;
    String dateFormatted;
    int mounth,day,year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        final View dialogView = View.inflate(this, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());

                 mounth=datePicker.getMonth();
                 day=datePicker.getDayOfMonth();
                 year=datePicker.getYear();
               long  time = calendar.getTimeInMillis();
                Date date = new Date(time);
                DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
                 dateFormatted = formatter.format(date);
                alertDialog.dismiss();
                Toast.makeText(getApplicationContext(),Integer.toString(day)+"/"+Integer.toString(mounth+1)+"/"
                        +Integer.toString(year)+"\n"+dateFormatted,Toast.LENGTH_LONG).show();
            }});



        takeMyCar = (Button) findViewById(R.id.takeMyCar);
        request = (Button) findViewById(R.id.Request);

        takeMyCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);

                alertDialog.setView(dialogView);
                alertDialog.show();

            }
        });
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
                alertDialog.setView(dialogView);
                alertDialog.show();

            }
        });

        etOrigin = (EditText) findViewById(R.id.editText);
        etOrigin.setCursorVisible(false);
        etOrigin.setFocusable(false);
        etOrigin.setSelected(false);

        etDestination = (EditText) findViewById(R.id.editText2);
       etDestination.setCursorVisible(false);
       etDestination.setFocusable(false);
       etDestination.setSelected(false);
        etOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etOrigin.setInputType(InputType.TYPE_NULL);

                openAutocompleteActivity(1);

            }
        });
        etDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etDestination.setInputType(InputType.TYPE_NULL);

                openAutocompleteActivity(2);

            }
        });
         removeOrigin = (ImageButton) findViewById(R.id.imageButton2);
        removeDestenation = (ImageButton) findViewById(R.id.imageButton4);
        GpsButton = (ImageButton) findViewById(R.id.imageButton);

        removeOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originMarkers != null) {
                    for (Marker marker : originMarkers) {
                        marker.remove();
                    }
                    if (polylinePaths != null) {
                        for (Polyline polyline : polylinePaths) {
                            polyline.remove();
                        }
                    }
                }
                removeOrigin.setVisibility(View.GONE);
                etOrigin.setText("");

            }
        });
        removeDestenation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originMarkers != null) {
                    for (Marker marker : destinationMarkers) {
                        marker.remove();
                    }
                    if (polylinePaths != null) {
                        for (Polyline polyline : polylinePaths) {
                            polyline.remove();
                        }
                    }
                }
                removeDestenation.setVisibility(View.GONE);

                etDestination.setText("");


            }
        });



        GpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (originMarkers != null) {
                    for (Marker marker : originMarkers) {
                        marker.remove();
                    }
                    if (polylinePaths != null) {
                        for (Polyline polyline : polylinePaths) {
                            polyline.remove();
                        }
                    }
                }

                GPSTracker Myplace = new GPSTracker(getBaseContext());
                Location loction = Myplace.getLocation();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loction.getLatitude(), loction.getLongitude()), 15));
                String Adr = "";
                try {
                    Adr = getCompleteAddressString(loction.getLatitude(), loction.getLongitude());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(loction.getLatitude(), loction.getLongitude()))
                        .title(Adr)
                        .snippet("from")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)


                        )));
                etOrigin.setText(Adr);
                removeOrigin.setVisibility(View.VISIBLE);
                if ((!etDestination.getText().toString().equals("")) && !etOrigin.getText().toString().equals("")) {
                    sendRequest();
                }
            }
        });

    }

    private void sendRequest() {
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                String to = null;
                try {
                    to = getCompleteAddressString(latLng.latitude, latLng.longitude);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(to)
                        .snippet("To")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                        )));
                removeDestenation.setVisibility(View.VISIBLE);
                etDestination.setText(to);
                sendRequest();
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        GPSTracker Myplace = new GPSTracker(this);
        Location loction = Myplace.getLocation();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loction.getLatitude(), loction.getLongitude()), 15));
        String Adr = "";
        try {
            Adr = getCompleteAddressString(loction.getLatitude(), loction.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .position(new LatLng(loction.getLatitude(), loction.getLongitude()))
                .title(Adr)
                .snippet("from")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )));
        removeOrigin.setVisibility(View.VISIBLE);

        etOrigin.setText(Adr);
        mMap.setMyLocationEnabled(false);
    }

    @Override
    public void onDirectionFinderStart() {


        //if (originMarkers != null) {
        //    for (Marker marker : originMarkers) {
        //        marker.remove();
        //    }
        //}

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
        mMap.clear();
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
        mMap.setMyLocationEnabled(false);

        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 18));


            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .position(route.startLocation)
                    .title(route.startAddress)
                    .snippet("From")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )));


            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .position(route.endLocation)
                    .title(route.endAddress)
                    .snippet("To")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    )));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.RED).
                    width(7);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    private void openAutocompleteActivity(int n) {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(this);
            startActivityForResult(intent, n);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                removeOrigin.setVisibility(View.VISIBLE);

                etOrigin.setText(place.getAddress());
                // TODO call location based filter


                LatLng latLong;
                latLong = place.getLatLng();
                if (originMarkers != null) {
                    for (Marker marker : originMarkers) {
                        marker.remove();
                    }
                    if (polylinePaths != null) {
                        for (Polyline polyline : polylinePaths) {
                            polyline.remove();
                        }
                    }
                }

                //mLocationText.setText(place.getName() + "");

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLong).zoom(15f).tilt(70).build();
                originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(place.getAddress().toString())
                        .snippet("From")
                        .position(place.getLatLng())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        )
                ));


                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if ((!etDestination.getText().toString().equals("")) && !etOrigin.getText().toString().equals("")) {
                    sendRequest();
                }
                mMap.setMyLocationEnabled(true);
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

            }


        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                etDestination.setText(place.getAddress());
                // TODO call location based filter
                removeDestenation.setVisibility(View.VISIBLE);


                LatLng latLong;
                latLong = place.getLatLng();
                if (destinationMarkers != null) {
                    for (Marker marker : destinationMarkers) {
                        marker.remove();
                    }
                    if (polylinePaths != null) {
                        for (Polyline polyline : polylinePaths) {
                            polyline.remove();
                        }
                    }
                }

                //mLocationText.setText(place.getName() + "");

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLong).zoom(15f).tilt(70).build();
                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(place.getAddress().toString())
                        .snippet("From")
                        .position(place.getLatLng())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                        )));

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if ((!etDestination.getText().toString().equals("")) && !etOrigin.getText().toString().equals("")) {
                    sendRequest();
                }
                mMap.setMyLocationEnabled(true);
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

            }


        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) throws IOException {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<android.location.Address> addresses;
        addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 4); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        if (addresses.size() > 0 || addresses != null) {
            for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++)
                strAdd += addresses.get(0).getAddressLine(i) + ",";
        }


        return strAdd;
    }

}
