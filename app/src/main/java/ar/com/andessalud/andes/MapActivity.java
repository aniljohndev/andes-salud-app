package ar.com.andessalud.andes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlusCode;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.location.LocationManager.NETWORK_PROVIDER;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener


{
    Activity actActual=this;
    LatLng latLng;
    ImageButton mylocation_btn;
    Button search_btn, btn_sendLocation;
    public static String LATITUDE = "lat", LONGITUDE = "lng", COUNTRY = "country", ZIPCODE = "zipcode", CITY = "city", STATE = "state";
    GoogleMap googleMapObj;
    AutocompleteSupportFragment location_search;
    GoogleApiClient apiClient;
    LocationRequest locationRequest;
    Marker marker, Save;
    com.google.android.libraries.places.api.model.Place selectedPlace;
    Geocoder geocoder;
    PlacesClient placesClient;
    List<Address> addresses;
    AutocompleteSupportFragment mapFragment;
    String place_m, city, placename, zip_s, city_s;
    Display display;
    int screen_width, screen_height;
    private final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-48, -168), new LatLng(71, 136));
    final String  TAG = "MapActivity";
     Point size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actActual.overridePendingTransition(R.anim.entrar_derecha, R.anim.salir_izquierda);
        setContentView(R.layout.map_layout);
        init_Places();
        setupAutoSuggestComplete();
        setIds();
        initialize();
        getDisplaySize();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_mapFragmentId_activity_get_location_filter);
        assert mapFragment != null;
        mapFragment.getMapAsync(MapActivity.this);
//        setToolbar();
        setListener();
        setSearchOnKeyboardButton();
        blink();
//        logEvent();
        setPlaceByDefault(place_m, latLng);

    }
    private void blink() {
        final Handler handler = new Handler();
        new Thread(() -> {
            int timeToBlink = 1000;    //in milissegunds
            try {
                Thread.sleep(timeToBlink);
            } catch (Exception ignored) {
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    TextView txt = findViewById(R.id.tv_currentlocation2_activity_get_location_filter);
//                        TextView txt2 = (TextView) findViewById(R.id.tv_currentlocation2_activity_get_location_filter);
                    if (txt.getVisibility() == View.VISIBLE) {
                        txt.setVisibility(View.INVISIBLE);
//                            txt2.setVisibility(View.INVISIBLE);
                    } else {
                        txt.setVisibility(View.VISIBLE);
//                            txt2.setVisibility(View.VISIBLE);
                    }
                    blink();
                }
            });
        }).start();
    }

    public void setSearchOnKeyboardButton() {
    }

    private void getDisplaySize() {
        display.getSize(size);
        screen_width = size.x;
        screen_height = size.y;
    }

    void initialize() {
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        geocoder = new Geocoder(this, Locale.getDefault());
//        customProgressDialog = new CustomProgressDialog(Activity_Select_Location.this);

        if (checkGpsStatus())
            getcurrentLocationAndMarker();
        else {
            showDialog_Without_Listener(
                    getString(R.string.location_gps_dialog_title),
                    getString(R.string.location_gps_turn_on_dialog_message),
                    getString(R.string.m_ok)
            );
        }
    }

    private void setupAutoSuggestComplete()
    {
        mapFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        mapFragment.setPlaceFields(Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS
                , Place.Field.LAT_LNG
        ));

        mapFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place place) {
//                Toast.makeText(MapActivity.this,place.getName(),Toast.LENGTH_LONG).show();
                Log.d(TAG,"Checking LatLng"+place.getLatLng());
                Log.d(TAG,"Checking Zipcode"+place.getAddressComponents());
                PerformSearchByClick(place.getName());
                setListener();

            }

            @Override
            public void onError(@NotNull Status status) {

                Toast.makeText(MapActivity.this,status.getStatusMessage(),Toast.LENGTH_LONG).show();

            }
        });

    }
    private void init_Places() {
        if (!com.google.android.libraries.places.api.Places.isInitialized()) {
            com.google.android.libraries.places.api.Places.initialize(getApplicationContext(),"AIzaSyAclXh0bDnSojLk8KeEPMx6IgXTIpDZv8Y");
        }
        placesClient = com.google.android.libraries.places.api.Places.createClient(this);


    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View f = activity.getCurrentFocus();
            if (null != f && null != f.getWindowToken() && EditText.class.isAssignableFrom(f.getClass()))
                imm.hideSoftInputFromWindow(f.getWindowToken(), 0);
            else
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setPlaceByDefault(final String address, final LatLng latLng_) {
        Log.d(TAG, "setPlaceByDefault: ");

        selectedPlace = new com.google.android.libraries.places.api.model.Place() {
            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {

            }

            @Override
            public String getId() {
                return null;
            }


            @Nullable
            @Override
            public String getAddress() {
                return null;
            }

            @Nullable
            @Override
            public AddressComponents getAddressComponents() {
                return null;
            }

            @Nullable
            @Override
            public BusinessStatus getBusinessStatus() {
                return null;
            }

            @Nullable
            @Override
            public List<String> getAttributions() {
                return null;
            }


            @Nullable
            @Override
            public OpeningHours getOpeningHours() {
                return null;
            }

            @Nullable
            @Override
            public String getPhoneNumber() {
                return null;
            }

            @Override
            public LatLng getLatLng() {
                return latLng_;
            }

            @Nullable
            @Override
            public String getName() {
                return null;
            }

            @Override
            public LatLngBounds getViewport() {
                return null;
            }

            @Override
            public Uri getWebsiteUri() {
                return null;
            }

            @Nullable
            @Override
            public List<PhotoMetadata> getPhotoMetadatas() {
                return null;
            }

            @Nullable
            @Override
            public PlusCode getPlusCode() {
                return null;
            }

            @Nullable
            @Override
            public Integer getPriceLevel() {
                return null;
            }

            @Nullable
            @Override
            public Double getRating() {
                return null;
            }

            @Nullable
            @Override
            public List<Type> getTypes() {
                return null;
            }

            @Nullable
            @Override
            public Integer getUserRatingsTotal() {
                return null;
            }

            @Nullable
            @Override
            public Integer getUtcOffsetMinutes() {
                return null;
            }

        };
    }



    public void gotolocationzoom(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        googleMapObj.animateCamera(update);
    }
    private void markermethod(String locale, double lat, double lng) {

        if (marker != null) {
            marker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions()
                .title(locale)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.andes_logo_24))
                .position(new LatLng(lat, lng))
                .snippet("I am here");

        marker = googleMapObj.addMarker(markerOptions);
    }


    private void PerformSearchByClick(String suggestion) {

        try {
            if (suggestion == null) {
                return;
            }
            geocoder = new Geocoder(MapActivity.this);

            if (suggestion.equals("")) {
                Toast.makeText(MapActivity.this, R.string.location_toast_enter_name, Toast.LENGTH_SHORT).show();
            } else {
                List<Address> addressList;
                try {
                    //get location name from edittext and added into geocoder
                    addressList = geocoder.getFromLocationName(suggestion, 1);
                    //get latitude and longitude from location name
                    double latitude = addressList.get(0).getLatitude();
                    double longitude = addressList.get(0).getLongitude();
                    //convert fetch data and added into new geocoder
                    geocoder = new Geocoder(MapActivity.this);
                    List<Address> newlist;
                    //get new location from geocoder_location
                    newlist = geocoder.getFromLocation(latitude, longitude, 1);
//                    city_s = addressList.get(0).getLocality();
////                    zip_s = addressList.get(0).getPostalCode();
////                    placename = addressList.get(0).getAddressLine(0);


                    double lat = newlist.get(0).getLatitude();
                    double lng = newlist.get(0).getLongitude();
                    place_m = newlist.get(0).getAddressLine(0);
                    zip_s = newlist.get(0).getPostalCode();
                    city_s = newlist.get(0).getLocality();
                    gotolocationzoom(lat, lng, 15);
                    markermethod(place_m, lat, lng);
                    latLng = new LatLng(lat, lng);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*
                if (zip_s == null) {
                    zip_s = "";
                } else {
                    zip_s = ": " + zip_s;
                }
                //convert if zipcode null then show empty
                String placeAndZip;
                placeAndZip = place_m + zip_s;
                */

                location_search.setText(suggestion);
                //set place
                setPlaceByDefault(place_m, latLng);
                hideKeyboard(MapActivity.this);
            }
        } catch (Exception e) {
//                    e.getMessage();
//            Toast.makeText(MapActivity.this, R.string.location_toast_for_corrent_name, Toast.LENGTH_SHORT).show();
//            Log.d(TAG,"HandingException"+e);
            e.printStackTrace();
        }
    }


    public void showDialog_Without_Listener(final String title, final String message, final String buttonText) {
        try {//This try catch is because of this error
            //    android.view.WindowManager$BadTokenException: Unable to add window -- token android.os.BinderProxy@c05f3c0 is not valid; is your activity running?

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                AlertDialog.Builder adbb = new AlertDialog.Builder(MapActivity.this);
                adbb.setIcon(R.drawable.andeslogo);
                adbb.setTitle(title);
                if (message != null)
                    adbb.setMessage(message);
                //adb.setNegativeButton("Cancel", null);
                adbb.setPositiveButton(buttonText, null);
                try {
                    adbb.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void setIds() {

        btn_sendLocation = findViewById(R.id.btn_sendLocation_activity_get_location_filter);
        mylocation_btn = findViewById(R.id.btn_mylocation_activity_get_location_filter);
//        toolbar = findViewById(R.id.toolbar);
        search_btn = findViewById(R.id.btn_search_activity_get_location_filter);

    }
    public boolean checkGpsStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    private void setListener() {
        btn_sendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkGpsStatus())  {
//            getcurrentLocationAndMarker(); // No need to call this function here because onMapReady will be called and call this function

                    showDialog_Without_Listener(
                            getString(R.string.location_gps_dialog_title),
                            getString(R.string.location_gps_turn_on_dialog_messagesm),
                            getString(R.string.m_ok)
                    );
                }

                if (selectedPlace == null) {
                    showDialog_Without_Listener(
                            getString(R.string.share_location),
                            getString(R.string.location_select_dialog_message),
                            getString(R.string.m_ok)
                    );
                    return;
                }
                try {
                    double lattt, longtt;

                        latLng = selectedPlace.getLatLng();

                    Intent returnIntent = new Intent();
                    lattt = latLng.latitude;
                    longtt = latLng.longitude;

                    geocoder = new Geocoder(MapActivity.this);
                    List<Address> newlist;
                    //get new location from geocoder_location
                    newlist = geocoder.getFromLocation(lattt, longtt, 1);

                    double lat = newlist.get(0).getLatitude();
                    double lng = newlist.get(0).getLongitude();
                    String address = newlist.get(0).getAddressLine(0);
                    String zip_s = newlist.get(0).getPostalCode();
                    String city_s = newlist.get(0).getLocality();
                    String country = newlist.get(0).getCountryName();
                    String state = newlist.get(0).getAdminArea();


                    //                    city_s = addressList.get(0).getLocality();
////                    zip_s = addressList.get(0).getPostalCode();
////                    placename = addressList.get(0).getAddressLine(0);


                    if (country == null)
//                        country = selectedPlace.getName();
                    {
                        showDialog_Without_Listener(
                                getString(R.string.activity_location_title),
                                getString(R.string.activty_country_not_f),
                                getString(R.string.got_it)
                        );
                        return;
                    }
//                String country = selectedPlace.getLocale().getCountry();
                    Log.d(TAG, "onClick: address:" + address);
                    Log.d(TAG, "onClick: 1:" + place_m);
                    Log.d(TAG, "onClick: 2:" + zip_s);
                    Log.d(TAG, "onClick: 3:" + city_s);
                    Log.d(TAG, "onClick: 4:" + country);
                    Log.d(TAG, "onClick: 5:" + state);
//                Log.d(TAG, "onClick: 5:"+selectedPlace.getLocale().getDisplayName());
                    returnIntent.putExtra("addressLine",address+"");
                    returnIntent.putExtra(LATITUDE, lat + "");
                    returnIntent.putExtra(LONGITUDE, lng + "");
                    returnIntent.putExtra(COUNTRY, country);
                    returnIntent.putExtra(ZIPCODE, zip_s);
                    returnIntent.putExtra(CITY, city_s);
                    returnIntent.putExtra(STATE, state);
                    setResult(100, returnIntent);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        mylocation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkGpsStatus())
                    getcurrentLocationAndMarker();

                else {
                    showDialog_Without_Listener(
                            getString(R.string.location_gps_dialog_title),
                            getString(R.string.location_gps_turn_on_dialog_message),
                            getString(R.string.m_ok)
                    );
                }
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String editTextlocation = location_search.getText().toString();
//                Place place = selectedPlace;
                PerformSearchByClick(selectedPlace.getName());
            }
        });
    }

    public void getcurrentLocationAndMarker() {

        Log.d(TAG, "getcurrentLocationAndMarker: ");
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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

        try {
            long minTimeForUpdate = 30 * (60 * 1000);
            float minDistance = 100;
            locationManager.requestLocationUpdates(NETWORK_PROVIDER, minTimeForUpdate, minDistance, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    final double lng = location.getLongitude();
                    final double lat = location.getLatitude();

                    latLng = new LatLng(lat, lng);
                    try {
                        addresses = geocoder.getFromLocation(lat, lng, 1);
                        city = addresses.get(0).getLocality();
                        placename = addresses.get(0).getAddressLine(0);
                        mapFragment.setText(placename);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setPlaceByDefault(placename, latLng);
                    markermethod(placename, lat, lng);
                    gotolocationzoom(lat, lng, 16);
                    googleMapObj.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker marker) {

                        }

                        @Override
                        public void onMarkerDrag(Marker marker) {

                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {
                            geocoder = new Geocoder(MapActivity.this);
                            List<Address> addressList = null;
                            final LatLng latLng = marker.getPosition();
                            double lat = latLng.latitude;
                            double lng = latLng.longitude;

                            try {
                                addressList = geocoder.getFromLocation(lat, lng, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (addressList !=  null && addressList.size() != 0) {
                                placename = addressList.get(0).getAddressLine(0);
                                marker.setTitle(addressList.get(0).getLocality());
                                marker.showInfoWindow();
                                //set place
                                setPlaceByDefault(placename, latLng);
                            } else {
                                Toast.makeText(MapActivity.this, R.string.location_toast_select_known_location, Toast.LENGTH_SHORT).show();
                            }


                        }
                    });


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
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMapObj = googleMap;
        googleMapObj.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(@NotNull Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(@NotNull Marker marker) {

                Save = marker;
                View v = getLayoutInflater().inflate(R.layout.location_info_window, null);
                TextView tv_locality = v.findViewById(R.id.location_name);
                TextView tv_spinnet = v.findViewById(R.id.location_current);
                LatLng ll = marker.getPosition();

                try {
                    List<Address> list_ad = geocoder.getFromLocation(ll.latitude, ll.longitude, 1);
                    tv_locality.setText(list_ad.get(0).getLocality());
                    tv_spinnet.setText(list_ad.get(0).getAddressLine(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return v;
            }
        });

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}