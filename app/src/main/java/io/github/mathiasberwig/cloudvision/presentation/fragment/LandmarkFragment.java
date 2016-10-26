package io.github.mathiasberwig.cloudvision.presentation.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import io.github.mathiasberwig.cloudvision.R;
import io.github.mathiasberwig.cloudvision.controller.service.CloudVisionUploader;
import io.github.mathiasberwig.cloudvision.data.model.LandmarkInfo;
import io.github.mathiasberwig.cloudvision.presentation.activity.MainActivity;

public class LandmarkFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = LandmarkFragment.class.getName();

    // UI References
    private TextView txtLandmarkName;
    private TextView txtLandmarkDescription;
    private AppCompatButton btnLandmarkInfoMore;
    private MapView mapLandmarkLocation;

    private LandmarkInfo landmarkInfo;

    /**
     * OnClickListener for button "More" on Landmark's Info. It will open the Wikipedia Article in
     * the default browser.
     */
    private View.OnClickListener btnLandmarkInfoMoreOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(landmarkInfo.getWikipediaArticleUrl()));
            startActivity(intent);
        }
    };

    public static LandmarkFragment newInstance() {
        return new LandmarkFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get the extras from MainActivity
        Bundle extras = ((MainActivity) getActivity()).getExtras();
        landmarkInfo = extras.getParcelable(CloudVisionUploader.EXTRA_RESULT_LANDMARK);

        return inflater.inflate(R.layout.fragment_landmark, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup the ScrollView and register it with MaterialViewPager
//        ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.scrollView);
//        MaterialViewPagerHelper.registerScrollView(getActivity(), scrollView, null);

        findInfoViews(view);
        setupInfoViews();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Create the options to show a marker
        final MarkerOptions markerOptions = new MarkerOptions()
                .title(landmarkInfo.getName())
                .position(landmarkInfo.getLatLng())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        if (landmarkInfo.getAddress() != null) markerOptions.snippet(landmarkInfo.getAddress());

        // Add marker with landmark position
        googleMap.addMarker(markerOptions);

        // Move camera to show all markers and locations
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(landmarkInfo.getLatLng(), 10f));
    }

    /**
     * Find all views used to show info about a Landmark.
     *
     * @param v the layout view.
     */
    private void findInfoViews(View v) {
        txtLandmarkName = (TextView) v.findViewById(R.id.txt_article_name);
        txtLandmarkDescription = (TextView) v.findViewById(R.id.txt_article_description);
        btnLandmarkInfoMore = (AppCompatButton) v.findViewById(R.id.btn_open_in_wikipedia);
        mapLandmarkLocation = (MapView) v.findViewById(R.id.map_landmark_location);
    }

    /**
     * Set the text {@link #txtLandmarkName}, {@link #txtLandmarkDescription}; the OnClickListener
     * event of {@link #btnLandmarkInfoMore} and initializes the lifecyle of {@link #mapLandmarkLocation}.
     */
    private void setupInfoViews() {
        txtLandmarkName.setText(landmarkInfo.getName());
        txtLandmarkDescription.setText(landmarkInfo.getDescription());
        btnLandmarkInfoMore.setOnClickListener(btnLandmarkInfoMoreOnClick);

        // Initialises the MapView by calling its lifecycle methods.
        if (mapLandmarkLocation != null) {
            // Initialise the MapView
            mapLandmarkLocation.onCreate(null);
            // Set the map ready callback to receive the GoogleMap object
            mapLandmarkLocation.getMapAsync(this);
        }
    }
}
