package com.example.dayout_organizer.ui.fragments.trips.myTrip;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.example.dayout_organizer.R;
import com.example.dayout_organizer.adapter.pager.MyTripPagerAdapter;
import com.example.dayout_organizer.adapter.recyclers.myTrips.ActiveTripAdapter;
import com.example.dayout_organizer.adapter.recyclers.myTrips.OldTripAdapter;
import com.example.dayout_organizer.adapter.recyclers.myTrips.UpComingTripAdapter;
import com.example.dayout_organizer.helpers.view.FN;
import com.example.dayout_organizer.ui.activities.MainActivity;
import com.example.dayout_organizer.ui.fragments.trips.createTrip.CreateTripFragment;
import com.example.dayout_organizer.ui.fragments.trips.myTrip.tab.ActiveTripFragment;
import com.example.dayout_organizer.ui.fragments.trips.myTrip.tab.OldTripFragment;
import com.example.dayout_organizer.ui.fragments.trips.myTrip.tab.UpComingTripFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.dayout_organizer.config.AppConstants.MAIN_FRC;


@SuppressLint("NonConstantResourceId")
public class MyTripsFragment extends Fragment {

    View view;

    @BindView(R.id.my_trips_tab_layout)
    TabLayout myTripsTabLayout;

    @BindView(R.id.my_trips_action_button)
    FloatingActionButton myTripsActionButton;

    @BindView(R.id.my_trips_back_arrow)
    ImageButton myTripsBackArrow;

    @BindView(R.id.my_trips_filter)
    ImageButton myTripsFilter;

    int TRIP_FCR;

    ActiveTripAdapter activeTripAdapter;
    UpComingTripAdapter upComingTripAdapter;
    OldTripAdapter oldTripAdapter;


    // = 3 because when 'MyTrips' is first opened, it is set to 'Active' tab.
    int type = 3;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_trips, container, false);
        ButterKnife.bind(this, view);
        TRIP_FCR = R.id.trip_fcr;
        initViews();
        return view;
    }

    @Override
    public void onStart() {
        ((MainActivity) requireActivity()).hideBottomBar();
        super.onStart();
    }


    private void initTabLayout() {
        FN.addFixedNameFadeFragment(TRIP_FCR, requireActivity(), new ActiveTripFragment(activeTripAdapter));
        myTripsTabLayout.addOnTabSelectedListener(onTabSelectedListener);

    }

    private void initViews() {
        initAdapter();
        initTabLayout();
        myTripsActionButton.setOnClickListener(onCreateTripClicked);
        myTripsBackArrow.setOnClickListener(onBackClicked);
        myTripsFilter.setOnClickListener(onFilterClicked);
    }

    private void initAdapter() {
        activeTripAdapter = new ActiveTripAdapter(new ArrayList<>(), requireContext());
        oldTripAdapter = new OldTripAdapter(new ArrayList<>(), requireContext());
        upComingTripAdapter = new UpComingTripAdapter(new ArrayList<>(), requireContext());
    }


    private final View.OnClickListener onCreateTripClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FN.addFixedNameFadeFragment(MAIN_FRC, requireActivity(), new CreateTripFragment());
        }
    };

    private final View.OnClickListener onBackClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FN.popStack(requireActivity());
        }
    };

    private final View.OnClickListener onFilterClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FilterFragment.isFilterOpen = true;

            if (type == 1) {
                FN.addToStackSlideUDFragment(MAIN_FRC, requireActivity(), new FilterFragment(oldTripAdapter, type), "filter");
            } else if (type == 2) {
                FN.addToStackSlideUDFragment(MAIN_FRC, requireActivity(), new FilterFragment(upComingTripAdapter, type), "filter");
            } else if (type == 3) {
                FN.addToStackSlideUDFragment(MAIN_FRC, requireActivity(), new FilterFragment(activeTripAdapter, type), "filter");
            }


        }
    };

    private final TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            myTripsTabLayout.setEnabled(false);
            if (tab.getPosition() == 0) {
                FN.replaceSlideFragmentLTR(TRIP_FCR, requireActivity(), new ActiveTripFragment(activeTripAdapter));
                type = 3;
            } else if (tab.getPosition() == 1) {
                FN.replaceSlideFragmentLTR(TRIP_FCR, requireActivity(), new UpComingTripFragment(upComingTripAdapter));
                type = 2;
            } else if (tab.getPosition() == 2) {
                FN.replaceSlideFragmentLTR(TRIP_FCR, requireActivity(), new OldTripFragment(oldTripAdapter));
                type = 1;
            }

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                myTripsTabLayout.setEnabled(true);
            }, 40);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };


}