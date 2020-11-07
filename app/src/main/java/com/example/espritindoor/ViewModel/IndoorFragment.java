package com.example.espritindoor.ViewModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.espritindoor.Adapters.CommentsAdapter;
import com.example.espritindoor.Adapters.ItemPagerAdapter;
import com.example.espritindoor.Model.Comment;
import com.example.espritindoor.Model.Salle;
import com.example.espritindoor.R;
import com.example.espritindoor.technique.ApiInterface;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPointStyle;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.ui.IconGenerator;
import com.indooratlas.android.sdk.IAGeofence;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IAOrientationListener;
import com.indooratlas.android.sdk.IAOrientationRequest;
import com.indooratlas.android.sdk.IAPOI;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.IARoute;
import com.indooratlas.android.sdk.IAWayfindingListener;
import com.indooratlas.android.sdk.IAWayfindingRequest;
import com.indooratlas.android.sdk.resources.IAFloorPlan;
import com.indooratlas.android.sdk.resources.IALatLng;
import com.indooratlas.android.sdk.resources.IALocationListenerSupport;
import com.indooratlas.android.sdk.resources.IAVenue;
import com.mahc.custombottomsheetbehavior.BottomSheetBehaviorGoogleMapsLike;
import com.mahc.custombottomsheetbehavior.MergedAppBarLayout;
import com.mahc.custombottomsheetbehavior.MergedAppBarLayoutBehavior;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class IndoorFragment extends Fragment implements GoogleMap.OnMapClickListener, OnMapReadyCallback, OnInfoWindowClickListener {

    int[] mDrawables = {
            R.drawable.esprit
    };

    TextView bottomSheetTextView;
    TextView bottomSheetTextViewdes;
    public EditText commentBody ;
    private static final String TAG = "IndoorAtlas";

    /* used to decide when bitmap should be downscaled */
    private static final int MAX_DIMENSION = 2048;

    private static final double GEOFENCE_RADIUS_METERS = 5.0;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private Circle mCircle;
    private IARegion mOverlayFloorPlan = null;
    private GroundOverlay mGroundOverlay = null;
    private IALocationManager mIALocationManager;
    private Target mLoadTarget;
    IALocation mLatestLocation = null;
    private boolean mCameraPositionNeedsUpdating = true; // update on first location
    private Marker mDestinationMarker;
    private Marker mHeadingMarker;
    private IAVenue mVenue;
    private List<Marker> mPoIMarkers = new ArrayList<>();
    private List<Polyline> mPolylines = new ArrayList<>();
    private IARoute mCurrentRoute;
    private boolean mShowIndoorLocation = false;
    private HashMap<LatLng, IAGeofence> mGeofences = new HashMap<>();
    private HashMap<LatLng, Circle> mGeofenceCircles = new HashMap<>();
    private List<IAGeofence> mTriggeredGeofences = new ArrayList<>();
    private UiSettings mUiSettings;
    private View addCommentBtn ;
    private PopupWindow popWindow ;
    private RecyclerView recyclerView ;
    private String commentID ;
    private View bottomSheet ;
    private List<Comment> commentsList = new ArrayList<>() ;
    private CommentsAdapter adapter1 = new CommentsAdapter(getContext(), commentsList);
    private RelativeLayout listlayout ;
    private TextView commentsCount ;
    private int count =0  ;

    private IAWayfindingRequest mWayfindingDestination;
    private IAWayfindingListener mWayfindingListener = new IAWayfindingListener() {
        @Override
        public void onWayfindingUpdate(IARoute route) {
            mCurrentRoute = route;
            if (hasArrivedToDestination(route)) {
                // stop wayfinding
                showInfo("You're there!");
                mCurrentRoute = null;
                mWayfindingDestination = null;
                mIALocationManager.removeWayfindingUpdates();
            }
            updateRouteVisualization();
        }
    };

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

    private int mFloor;

    private void showLocationCircle(LatLng center, double accuracyRadius) {
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
                mHeadingMarker = mMap.addMarker(new MarkerOptions()
                        .position(center)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_blue_dot))
                        .anchor(0.5f, 0.5f)
                        .flat(true));
            }
        } else {
            // move existing markers position to received location
            mCircle.setCenter(center);
            mHeadingMarker.setPosition(center);
            mCircle.setRadius(accuracyRadius);
        }
    }

    private void updateHeading(double heading) {
        if (mHeadingMarker != null) {
            mHeadingMarker.setRotation((float)heading);
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

            Log.d(TAG, "new location received with CoordinateInfos: " + location.getLatitude()
                    + "," + location.getLongitude());

            if (mMap == null) {
                // location received before map is initialized, ignoring update here
                return;
            }

            final LatLng center = new LatLng(location.getLatitude(), location.getLongitude());

            final int newFloor = location.getFloorLevel();
            if (mFloor != newFloor) {
                updateRouteVisualization();
            }
            mFloor = newFloor;

            showLocationCircle(center, location.getAccuracy());

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
        public void onEnterRegion(final IARegion region) {
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
                setupPoIs(mVenue.getPOIs(), region.getFloorPlan().getFloorLevel());
            } else if (region.getType() == IARegion.TYPE_VENUE) {
                mVenue = region.getVenue();
            }
        }

        @Override
        public void onExitRegion(IARegion region) {
        }

    };

    public IndoorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_indoor2, container, false);
        mIALocationManager = IALocationManager.create(getActivity());

       /* ((SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map))
                .getMapAsync(this);*/

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout)view.findViewById(R.id.coordinatorlayout);
        bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        recyclerView =  bottomSheet.findViewById(R.id.commentsListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(adapter1);
        final BottomSheetBehaviorGoogleMapsLike behavior = BottomSheetBehaviorGoogleMapsLike.from(bottomSheet);
        behavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_HIDDEN);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view ;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // remember to clean up after ourselves
        mIALocationManager.destroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        // start receiving location updates & monitor region changes
        mIALocationManager.requestLocationUpdates(IALocationRequest.create(), mListener);
        mIALocationManager.registerRegionListener(mRegionListener);
        mIALocationManager.registerOrientationListener(
                // update if heading changes by 1 degrees or more
                new IAOrientationRequest(1, 0),
                mOrientationListener);

        if (mWayfindingDestination != null) {
            mIALocationManager.requestWayfindingUpdates(mWayfindingDestination, mWayfindingListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // unregister location & region changes
        mIALocationManager.removeLocationUpdates(mListener);
        mIALocationManager.unregisterRegionListener(mRegionListener);
        mIALocationManager.unregisterOrientationListener(mOrientationListener);

        if (mWayfindingDestination != null) {
            mIALocationManager.removeWayfindingUpdates();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // do not show Google's outdoor location
        mMap.setMyLocationEnabled(false);
        mMap.setOnMapClickListener(this);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setOnInfoWindowClickListener(this);
        //mMap.getUiSettings().setZoomControlsEnabled(true);



        try {
            KmlLayer layerKml = new KmlLayer(mMap, R.raw.map, getContext());
            layerKml.addLayerToMap();

            layerKml.setOnFeatureClickListener(new KmlLayer.OnFeatureClickListener() {
                @Override
                public void onFeatureClick(Feature feature) {
                    Log.i("KmlClick", "Feature clicked: " + feature.getId());


                }
            });

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            GeoJsonLayer layer = new GeoJsonLayer(mMap, R.raw.carte,
                    getContext());

            for (GeoJsonFeature feature : layer.getFeatures()) {

                if (feature.getProperty("name").equals("kitchen")){

                    GeoJsonPointStyle pointStyle = new GeoJsonPointStyle();

                    String featurSnippet = feature.getProperty("description");
                    pointStyle.setDraggable(false);
                    pointStyle.setTitle("kitchen");
                    pointStyle.setSnippet(featurSnippet);
                    pointStyle.isFlat();


                    pointStyle.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.kitchen));
                    feature.setPointStyle(pointStyle);



                }else if (feature.getProperty("name").equals("living room")){
                   GeoJsonPointStyle pointStyle1 = new GeoJsonPointStyle();

                    String featurSnippet1 = feature.getProperty("description");
                    pointStyle1.setDraggable(false);

                    pointStyle1.setTitle("Living room");
                    pointStyle1.setSnippet(featurSnippet1);
                    pointStyle1.isFlat();
                    IconGenerator icg = new IconGenerator(getContext());
                    icg.setTextAppearance(R.style.iconGenText);
                    icg.setContentPadding(0,10,0,0);
                    Drawable clusterIcon = getContext().getResources().getDrawable(R.drawable.living);

                    icg.setBackground(clusterIcon);
                    pointStyle1.setIcon(BitmapDescriptorFactory.fromBitmap(icg.makeIcon("louay")));
                    feature.setPointStyle(pointStyle1);

                }
                else if (feature.getProperty("name").equals("bedroom")){
                    GeoJsonPointStyle pointStyle1 = new GeoJsonPointStyle();

                    String featurSnippet1 = feature.getProperty("description");
                    pointStyle1.setDraggable(false);

                    pointStyle1.setTitle("Bedroom");
                    pointStyle1.setSnippet(featurSnippet1);
                    pointStyle1.isFlat();
                    pointStyle1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bedroom));
                    feature.setPointStyle(pointStyle1);
                }
                else if (feature.getProperty("name").equals("bathroom")){
                    GeoJsonPointStyle pointStyle1 = new GeoJsonPointStyle();

                    String featurSnippet1 = feature.getProperty("description");
                    pointStyle1.setDraggable(false);

                    pointStyle1.setTitle("bathroom");
                    pointStyle1.setSnippet(featurSnippet1);
                    pointStyle1.isFlat();
                    pointStyle1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bath));
                    feature.setPointStyle(pointStyle1);
                }
                else if (feature.getProperty("name").equals("Living room 2")){


                    GeoJsonPointStyle pointStyle1 = new GeoJsonPointStyle();

                    String featurSnippet1 = feature.getProperty("description");
                    pointStyle1.setDraggable(false);

                    pointStyle1.setTitle("living room 2");
                    pointStyle1.setSnippet(featurSnippet1);
                    pointStyle1.isFlat();
                    pointStyle1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.track));
                    feature.setPointStyle(pointStyle1);
                }
                else {
                    GeoJsonPointStyle pointStyle1 = new GeoJsonPointStyle();

                    String featurSnippet1 = feature.getProperty("description");
                    pointStyle1.setDraggable(false);

                    pointStyle1.setTitle("louay room");
                    pointStyle1.setSnippet(featurSnippet1);
                    pointStyle1.isFlat();
                    pointStyle1.toMarkerOptions().title("louay");

                    pointStyle1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bed));
                    feature.setPointStyle(pointStyle1);

                }

                }

//            layer.addLayerToMap();

            layer.setOnFeatureClickListener(new GeoJsonLayer.OnFeatureClickListener() {
                @Override
                public void onFeatureClick(Feature feature) {
                    Log.i("GeoJsonClick", "Feature clicked: " + feature.getProperty("name"));
                    listlayout = bottomSheet.findViewById(R.id.listLayout1);
                    commentsCount = bottomSheet.findViewById(R.id.commentsCount) ;
                    commentsList = getComments(feature.getProperty("name"));


                    //getComments(feature.getProperty("name"));
                    final BottomSheetBehaviorGoogleMapsLike behavior = BottomSheetBehaviorGoogleMapsLike.from(bottomSheet);
                    behavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED);

                    FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.goButton);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            double latitude = Double.parseDouble(feature.getProperty("lon"));
                            double longitude = Double.parseDouble(feature.getProperty("lat"));

                            LatLng location = new LatLng(longitude, latitude);
                            setWayfindingTarget(location, false);
                        }
                    });
                    behavior.addBottomSheetCallback(new BottomSheetBehaviorGoogleMapsLike.BottomSheetCallback() {
                        @Override
                        public void onStateChanged(@NonNull View bottomSheet, int newState) {
                            switch (newState) {
                                case BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED:
                                    Log.d("bottomsheet-", "STATE_COLLAPSED");
                                    break;
                                case BottomSheetBehaviorGoogleMapsLike.STATE_DRAGGING:
                                    Log.d("bottomsheet-", "STATE_DRAGGING");
                                    break;
                                case BottomSheetBehaviorGoogleMapsLike.STATE_EXPANDED:
                                    Log.d("bottomsheet-", "STATE_EXPANDED");



                                    break;
                                case BottomSheetBehaviorGoogleMapsLike.STATE_ANCHOR_POINT:
                                    Log.d("bottomsheet-", "STATE_ANCHOR_POINT");
                                    break;
                                case BottomSheetBehaviorGoogleMapsLike.STATE_HIDDEN:
                                    Log.d("bottomsheet-", "STATE_HIDDEN");
                                    break;
                                default:
                                    Log.d("bottomsheet-", "STATE_SETTLING");
                                    break;
                            }
                        }

                        @Override
                        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                        }
                    });

                    MergedAppBarLayout mergedAppBarLayout = getView().findViewById(R.id.mergedappbarlayout);
                    MergedAppBarLayoutBehavior mergedAppBarLayoutBehavior = MergedAppBarLayoutBehavior.from(mergedAppBarLayout);
                    mergedAppBarLayoutBehavior.setToolbarTitle(feature.getProperty("name"));
                    mergedAppBarLayoutBehavior.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            behavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_ANCHOR_POINT);
                        }
                    });

                    bottomSheetTextView = (TextView) bottomSheet.findViewById(R.id.bottom_sheet_title);
                    bottomSheetTextViewdes = (TextView) bottomSheet.findViewById(R.id.text_dummy1);
                    addCommentBtn =  bottomSheet.findViewById(R.id.comment_addbtn);
                    commentBody = bottomSheet.findViewById(R.id.writeComment);
                    addCommentBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            System.out.println("Im here  "+commentBody.getText());

                            String cid = addComment("5eb8c20226ce710df8251619", commentBody.getText().toString(),feature.getProperty("name"));

                            System.out.println("Comment ID  "+cid);
                            System.out.println("salleName  "+feature.getProperty("name"));
                            commentsList = getComments(feature.getProperty("name")) ;

                        }
                    });
                    bottomSheetTextView.setText(feature.getProperty("name"));
                    bottomSheetTextViewdes.setText(feature.getProperty("description"));
                    ItemPagerAdapter adapter = new ItemPagerAdapter(getContext(),mDrawables);
                    ViewPager viewPager = (ViewPager) getView().findViewById(R.id.pager);
                    viewPager.setAdapter(adapter);

                    System.out.println("Size1 "+commentsList.size());
                    //behavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_ANCHOR_POINT);
                    //behavior.setCollapsible(false);

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // disable various Google maps UI elements that do not work indoors
        //mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // ignore clicks to artificial wayfinding target markers
                if (marker == mDestinationMarker) return false;

                setWayfindingTarget(marker.getPosition(), false);
                // do not consume the event so that the popup with marker name is displayed
                return false;
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
               /* ExampleUtils.shareText(getActivity(),
                        mIALocationManager.getExtraInfo().traceId, "traceId");*/



            }
        });
    }

    private void setupPoIs(List<IAPOI> pois, int currentFloorLevel) {
        Log.d(TAG, pois.size() + " PoI(s)");
        // remove any existing markers
        for (Marker m : mPoIMarkers) {
            m.remove();
        }
        mPoIMarkers.clear();
        for (IAPOI poi : pois) {
            if (poi.getFloor() == currentFloorLevel) {
                mPoIMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(poi.getName())
                        .position(new LatLng(poi.getLocation().latitude, poi.getLocation().longitude))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
            }
        }
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
    public void onMapClick(LatLng point) {
        if (mPoIMarkers.isEmpty()) {
            // if PoIs exist, only allow wayfinding to PoI markers
            setWayfindingTarget(point, true);
        }
    }

    private void setWayfindingTarget(LatLng point, boolean addMarker) {
        if (mMap == null) {
            Log.w(TAG, "map not loaded yet");
            return;
        }

        mWayfindingDestination = new IAWayfindingRequest.Builder()
                .withFloor(mFloor)
                .withLatitude(point.latitude)
                .withLongitude(point.longitude)
                .build();

        mIALocationManager.requestWayfindingUpdates(mWayfindingDestination, mWayfindingListener);

        if (mDestinationMarker != null) {
            mDestinationMarker.remove();
            mDestinationMarker = null;
        }

        if (addMarker) {
            mDestinationMarker = mMap.addMarker(new MarkerOptions()
                    .position(point)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
        Log.d(TAG, "Set destination: (" + mWayfindingDestination.getLatitude() + ", " +
                mWayfindingDestination.getLongitude() + "), floor=" +
                mWayfindingDestination.getFloor());

    }

    private boolean hasArrivedToDestination(IARoute route) {
        // empty routes are only returned when there is a problem, for example,
        // missing or disconnected routing graph
        if (route.getLegs().size() == 0) {
            return false;
        }

        final double FINISH_THRESHOLD_METERS = 8.0;
        double routeLength = 0;
        for (IARoute.Leg leg : route.getLegs()) routeLength += leg.getLength();
        return routeLength < FINISH_THRESHOLD_METERS;
    }

    /**
     * Clear the visualizations for the wayfinding paths
     */
    private void clearRouteVisualization() {
        for (Polyline pl : mPolylines) {
            pl.remove();
        }
        mPolylines.clear();
    }

    /**
     * Visualize the IndoorAtlas Wayfinding route on top of the Google Maps.
     */
    private void updateRouteVisualization() {

        clearRouteVisualization();

        if (mCurrentRoute == null) {
            return;
        }

        for (IARoute.Leg leg : mCurrentRoute.getLegs()) {

            if (leg.getEdgeIndex() == null) {
                // Legs without an edge index are, in practice, the last and first legs of the
                // route. They connect the destination or current location to the routing graph.
                // All other legs travel along the edges of the routing graph.

                // Omitting these "artificial edges" in visualization can improve the aesthetics
                // of the route. Alternatively, they could be visualized with dashed lines.
                continue;
            }

            PolylineOptions opt = new PolylineOptions();
            opt.add(new LatLng(leg.getBegin().getLatitude(), leg.getBegin().getLongitude()));
            opt.add(new LatLng(leg.getEnd().getLatitude(), leg.getEnd().getLongitude()));

            // Here wayfinding path in different floor than current location is visualized in
            // a semi-transparent color
            if (leg.getBegin().getFloor() == mFloor && leg.getEnd().getFloor() == mFloor) {
                opt.color(0xFF0000FF);
            } else {
                opt.color(0x300000FF);
            }

            mPolylines.add(mMap.addPolyline(opt));
        }
    }

    public void onShowPopup(View v){

        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        final View inflatedView = layoutInflater.inflate(R.layout.popup_layout, null,false);
        // find the ListView in the popup layout
        ListView listView = (ListView)inflatedView.findViewById(R.id.commentsListView);
        LinearLayout headerView = (LinearLayout)inflatedView.findViewById(R.id.headerLayout);
        // get device size
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        float mDeviceHeight = size.y;
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;


        // fill the data to the list items
        setSimpleList(listView);


        // set height depends on the device size
        popWindow = new PopupWindow(inflatedView, size.x - 100,size.y - 400, true );
        // set a background drawable with rounders corners
        popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.fb_popup));

        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        popWindow.setAnimationStyle(R.style.PopupAnimation);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0,100);
    }

    void setSimpleList(ListView listView){

        ArrayList<String> contactsList = new ArrayList<String>();

        for (int index = 0; index < 10; index++) {
            contactsList.add("I am @ index " + index + " today " + Calendar.getInstance().getTime().toString());
        }


        listView.setAdapter(new ArrayAdapter<String>(getContext(),
                R.layout.popup_list_item, R.id.comment ,contactsList));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //onShowPopup(getView());

    }


    public String addComment (String id ,String contenu , String salleName ){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.23:3000/comments/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<Comment> call = apiInterface.addComment(id , contenu);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {


                commentID = response.body().get_id();
                System.out.println("response "+commentID);

                addCommentToSallee(salleName,commentID);

            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {

                Log.d("***", "************************"+t.getMessage());
                if (t.getMessage().startsWith("Failed")){

                    Toast.makeText(getActivity(), "Could not connect to server ",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
            return commentID ;
    }


    public void addCommentToSallee (String salleName , String cid ){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.23:3000/salles/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<Comment> call = apiInterface.addCommentToSalle(salleName , cid);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {


                Toast.makeText(getActivity(), "Comment added ",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {

                Log.d("***", "************************"+t.getMessage());
            }
        });

    }

    public List<Comment> getComments(String salleName){
        commentsList.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.23:3000/salles/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<Salle> call = apiInterface.getComments(salleName);
        call.enqueue(new Callback<Salle>() {
            @Override
            public void onResponse(Call<Salle> call, Response<Salle> response) {



                commentsList.addAll(response.body().getComments()) ;
                //System.out.println("Size "+commentsList.get(0).getTimestamps());

                if (commentsList.size()== 0){
                    commentsCount.setText("Some One and "+count+" Others commented this");
                }else{
                    count  = commentsList.size()-1 ;
                    commentsCount.setText("Some One and "+count+" Others commented this");
                }

            }

            @Override
            public void onFailure(Call<Salle> call, Throwable t) {

                Log.d("***", "************************"+t.getMessage());
                if (t.getMessage().startsWith("Failed")){

                    Toast.makeText(getActivity(), "Could not connect to server ",
                            Toast.LENGTH_SHORT).show();
                }else {
                    count = 0 ;
                    commentsCount.setText("Some One and "+count+" Others commented this");
                    Toast.makeText(getActivity(), "No comments found",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        System.out.println("here");
        adapter1.notifyDataSetChanged();
       // recyclerView.invalidate();
        listlayout.invalidate();
        return commentsList ;
    }
}
