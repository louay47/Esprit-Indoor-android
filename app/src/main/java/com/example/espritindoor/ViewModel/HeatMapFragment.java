package com.example.espritindoor.ViewModel;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.espritindoor.Model.Feed;
import com.example.espritindoor.Model.SdkSetupId;
import com.example.espritindoor.R;
import com.example.espritindoor.technique.ApiInterface;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IAOrientationListener;
import com.indooratlas.android.sdk.IAOrientationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.resources.IAFloorPlan;
import com.indooratlas.android.sdk.resources.IALatLng;
import com.indooratlas.android.sdk.resources.IALocationListenerSupport;
import com.indooratlas.android.sdk.resources.IAVenue;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeatMapFragment extends Fragment implements GoogleMap.OnMapClickListener, OnMapReadyCallback {

    private static final int MAX_DIMENSION = 2048;
    private static final String TAG = "IndoorAtlas";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Circle mCircle;
    private Marker mMarker;
    private IAVenue mVenue;
    private IARegion mOverlayFloorPlan = null;
    private GroundOverlay mGroundOverlay = null;
    private IALocationManager mIALocationManager;
    private Target mLoadTarget;
    private boolean mCameraPositionNeedsUpdating = true; // update on first location
    private boolean mShowIndoorLocation = false;
    private HeatmapTileProvider  mProvider;
    private TileOverlay mOverlay;
    private Marker mHeadingMarker;
    private int count ;
    private int sdkIdSize ;

    private List<LatLng> listDataHeat = new ArrayList<>() ;


    public HeatMapFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_heat_map, container, false);
        mIALocationManager = IALocationManager.create(getActivity());


       /* ((SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map))
                .getMapAsync(this);*/
        getHeatmapData();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        return view ;

    }

    public void getHeatmapData () {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://data-api.indooratlas.com/public/v1/sdk-sessions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<List<SdkSetupId>> call = apiInterface.getSdkSetupId();
        call.enqueue(new Callback<List<SdkSetupId>>() {
            @Override
            public void onResponse(Call<List<SdkSetupId>> call, Response<List<SdkSetupId>> response) {

                for (SdkSetupId sd : response.body()) {

                    System.out.println("SDKSETUPID " + sd.getSdkSetupId());
                    getCoordinate(sd.getSdkSetupId());

                }

                sdkIdSize = response.body().size();
                System.out.println("Size  " + sdkIdSize);
                System.out.println("count " + count);


            }

            @Override
            public void onFailure(Call<List<SdkSetupId>> call, Throwable t) {

                System.out.println("Faiiiiilure  " + t.getMessage());
            }
        });
    }

    public  void getCoordinate (String id) {


                Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://data-api.indooratlas.com/public/v1/sdk-sessions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<List<Feed>> call = apiInterface.getCoordinate(id);
        call.enqueue(new Callback<List<Feed>>() {
            @Override
            public void onResponse(Call<List<Feed>> call, Response<List<Feed>> response) {

                count++;
                for (int i=2 ; i < response.body().size();i++){

                    if (response.body().get(i).getContent().getLocation()!= null ){
                        System.out.println("Laaaaaaaaaaaat "+response.body().get(i).getContent().getLocation().getCoordinates().getLat());
                        System.out.println("Loooooooooooon "+response.body().get(i).getContent().getLocation().getCoordinates().getLon());

                        LatLng one = new LatLng(response.body().get(i).getContent().getLocation().getCoordinates().getLat(),response.body().get(i).getContent().getLocation().getCoordinates().getLon());
                        listDataHeat.add(one);
                    }

                }

                if(count==sdkIdSize){
                    addHeatMap();
                }

            }

            @Override
            public void onFailure(Call<List<Feed>> call, Throwable t) {

                Log.d("***", "************************"+t.getMessage());
            }
        });

    }



    /* used to decide when bitmap should be downscaled */


    private void showBlueDot(LatLng center, double accuracyRadius, double bearing) {
        if (mCircle == null) {
            // location can received before map is initialized, ignoring those updates
            if (mMap != null) {
                mCircle = mMap.addCircle(new CircleOptions()
                        .center(center)
                        .radius(accuracyRadius)
                        .fillColor(0x201681FB)
                        .strokeColor(0x500A78DD)
                        .zIndex(1.0f)
                        .visible(true)
                        .strokeWidth(5.0f));
                mMarker = mMap.addMarker(new MarkerOptions()
                        .position(center)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_blue_dot))
                        .anchor(0.5f, 0.5f)
                        .rotation((float)bearing)
                        .flat(true));
            }
        } else {
            // move existing markers position to received location
            mCircle.setCenter(center);
            mCircle.setRadius(accuracyRadius);
            mMarker.setPosition(center);
            mMarker.setRotation((float)bearing);
        }
    }

    /**
     * Listener that handles location change events.
     */
    private IALocationListener mListener = new IALocationListenerSupport() {

        /**
         * Location changed, move marker and camera position.
         */
        @Override
        public void onLocationChanged(IALocation location) {

            Log.d(TAG, "new location received with coordinates: " + location.getLatitude()
                    + "," + location.getLongitude());

            if (mMap == null) {
                // location received before map is initialized, ignoring update here
                return;
            }

            final LatLng center = new LatLng(location.getLatitude(), location.getLongitude());

            if (mShowIndoorLocation) {
               // showBlueDot(center, location.getAccuracy(), location.getBearing());
            }

            // our camera position needs updating if location has significantly changed
            if (mCameraPositionNeedsUpdating) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 17.5f));
                mCameraPositionNeedsUpdating = false;
            }
        }
    };

    /**
     * Listener that changes overlay if needed
     */
    private IARegion.Listener mRegionListener = new IARegion.Listener() {
        @Override
        public void onEnterRegion(IARegion region) {
            if (region.getType() == IARegion.TYPE_FLOOR_PLAN) {
                Log.d(TAG, "enter floor plan " + region.getId());
                mCameraPositionNeedsUpdating = true; // entering new fp, need to move camera
                if (mGroundOverlay != null) {
                    mGroundOverlay.remove();
                    mGroundOverlay = null;
                }
                mShowIndoorLocation = true;
                mOverlayFloorPlan = region; // overlay will be this (unless error in loading)
                fetchFloorPlanBitmap(region.getFloorPlan());

            } else if (region.getType() == IARegion.TYPE_VENUE) {
                mVenue = region.getVenue();
            }
        }

        @Override
        public void onExitRegion(IARegion region) {
          /*  if (mGroundOverlay != null) {
                // Indicate we left this floor plan but leave it there for reference
                // If we enter another floor plan, this one will be removed and another one loaded
                mGroundOverlay.setTransparency(0.5f);
            }

            mShowIndoorLocation = false;
            showInfo("Exit " + (region.getType() == IARegion.TYPE_VENUE
                    ? "VENUE "
                    : "FLOOR_PLAN ") + region.getId());*/
        }

    };



    private void updateHeading(double heading) {
        if (mHeadingMarker != null) {
            mHeadingMarker.setRotation((float)heading);
        }
    }

    private IAOrientationListener mOrientationListener = new IAOrientationListener() {
        @Override
        public void onHeadingChanged(long timestamp, double heading) {
            updateHeading(heading);
        }

        @Override
        public void onOrientationChange(long timestamp, double[] quaternion) {
            // we do not need full device orientation in this example, just the heading
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        // remember to clean up after ourselves
        mIALocationManager.destroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        mIALocationManager.requestLocationUpdates(IALocationRequest.create(), mListener);
        mIALocationManager.registerRegionListener(mRegionListener);
        mIALocationManager.registerOrientationListener(
                // update if heading changes by 1 degrees or more
                new IAOrientationRequest(1, 0),
                mOrientationListener);


        // enable indoor-outdoor mode, required since SDK 3.2
        mIALocationManager.lockIndoors(false);

        IALocationRequest locReq = IALocationRequest.create();

        // --- choose positioning mode

        // default mode
        locReq.setPriority(IALocationRequest.PRIORITY_HIGH_ACCURACY);

        // Low power mode: Uses less power, but has lower accuracy use e.g. for background tracking
        //locReq.setPriority(IALocationRequest.PRIORITY_LOW_POWER);

        // Cart mode: Use when device is mounted to a shopping cart or similar platform with wheels
        //locReq.setPriority(IALocationRequest.PRIORITY_CART_MODE);

        // --- start receiving location updates & monitor region changes



    }

    @Override
    public void onPause() {
        super.onPause();
        // unregister location & region changes
        mIALocationManager.removeLocationUpdates(mListener);
        mIALocationManager.registerRegionListener(mRegionListener);
        mIALocationManager.unregisterOrientationListener(mOrientationListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // do not show Google's outdoor location
        mMap.setMyLocationEnabled(false);
        mMap.setOnMapClickListener(this);


        // Setup long click to share the traceId
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
               /* ExampleUtils.shareText(getActivity(),
                        mIALocationManager.getExtraInfo().traceId, "traceId");*/

            }
        });
    }

    /**
     * Sets bitmap of floor plan as ground overlay on Google Maps
     */
    private void setupGroundOverlay(IAFloorPlan floorPlan, Bitmap bitmap) {

        if (mGroundOverlay != null) {
            mGroundOverlay.remove();
        }

        if (mMap != null) {
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
            IALatLng iaLatLng = floorPlan.getCenter();
            LatLng center = new LatLng(iaLatLng.latitude, iaLatLng.longitude);
            GroundOverlayOptions fpOverlay = new GroundOverlayOptions()
                    .image(bitmapDescriptor)
                    .zIndex(0.0f)
                    .position(center, floorPlan.getWidthMeters(), floorPlan.getHeightMeters())
                    .bearing(floorPlan.getBearing());

            mGroundOverlay = mMap.addGroundOverlay(fpOverlay);
        }
    }

    /**
     * Download floor plan using Picasso library.
     */
    private void fetchFloorPlanBitmap(final IAFloorPlan floorPlan) {

        if (floorPlan == null) {
            Log.e(TAG, "null floor plan in fetchFloorPlanBitmap");
            return;
        }

        final String url = floorPlan.getUrl();
        Log.d(TAG, "loading floor plan bitmap from "+url);

        mLoadTarget = new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d(TAG, "onBitmap loaded with dimensions: " + bitmap.getWidth() + "x"
                        + bitmap.getHeight());
                if (mOverlayFloorPlan != null && floorPlan.getId().equals(mOverlayFloorPlan.getId())) {
                    Log.d(TAG, "showing overlay");
                    setupGroundOverlay(floorPlan, bitmap);
                }
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // N/A
            }

            @Override
            public void onBitmapFailed(Drawable placeHolderDrawable) {
                showInfo("Failed to load bitmap");
                mOverlayFloorPlan = null;
            }
        };

        RequestCreator request = Picasso.with(getActivity()).load(url);

        final int bitmapWidth = floorPlan.getBitmapWidth();
        final int bitmapHeight = floorPlan.getBitmapHeight();

        if (bitmapHeight > MAX_DIMENSION) {
            request.resize(0, MAX_DIMENSION);
        } else if (bitmapWidth > MAX_DIMENSION) {
            request.resize(MAX_DIMENSION, 0);
        }

        request.into(mLoadTarget);
    }

    private void showInfo(String text) {
        final Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), text,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.button_close, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    private void addHeatMap() {
        //List<LatLng> list = null;

        // Get the data: latitude/longitude positions of police stations.
       /* try {
            list = readItems(R.raw.dataexemple);
        } catch (JSONException e) {
            Toast.makeText(getActivity(), "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        } */

       /* List<LatLng> list = new ArrayList<LatLng>();

        LatLng one = new LatLng(36.817806956906786,10.082955662953715);
        list.add(one);
        LatLng two = new LatLng(36.817806956906686,10.082955662953875);
        list.add(two);
        LatLng three = new LatLng(36.81780684049081,10.08295368904136);
        list.add(three);
        LatLng four = new LatLng(36.817806288199336,10.082954374393097);
        list.add(four);
        LatLng five = new LatLng(36.81780586815108,10.082934033076745);
        list.add(five);

        List <LatLng> listTwo = null;*/

        int[] colors = {
                Color.rgb(0, 191, 255), // green
                Color.rgb(255, 64, 0)    // red
        };

        float[] startPoints = {
                0.2f, 1f
        };

        Gradient gradient = new Gradient(colors, startPoints);



        // Create a heat map tile provider, passing it the latlngs of the police stations.
        System.out.println(listDataHeat.size());
        mProvider = new HeatmapTileProvider.Builder()
                .data(listDataHeat)
                .opacity(1)
                .radius(30)
                .gradient(gradient)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
         mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }




}


