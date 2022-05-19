package com.example.dayout_organizer.ui.fragments.trips.EditTrip;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.dayout_organizer.R;
import com.example.dayout_organizer.helpers.view.FN;
import com.example.dayout_organizer.helpers.view.NoteMessage;
import com.example.dayout_organizer.models.trip.Trip;
import com.example.dayout_organizer.models.trip.TripModel;
import com.example.dayout_organizer.ui.activities.MainActivity;
import com.example.dayout_organizer.ui.dialogs.ErrorDialog;
import com.example.dayout_organizer.ui.dialogs.LoadingDialog;
import com.example.dayout_organizer.ui.fragments.trips.CreateTrip.CreateTripPlaceFragment;
import com.example.dayout_organizer.viewModels.TripViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.dayout_organizer.config.AppConstants.MAIN_FRC;


public class EditTripFragment extends Fragment {





    View view;
    @BindView(R.id.title)
    TextInputEditText title;
    @BindView(R.id.title_textlayout)
    TextInputLayout titleTextlayout;
    @BindView(R.id.description)
    TextInputEditText description;
    @BindView(R.id.description_textlayout)
    TextInputLayout descriptionTextlayout;
    @BindView(R.id.start_date)
    TextView startDate;
    @BindView(R.id.end_date)
    TextView endDate;
    @BindView(R.id.price)
    TextInputEditText price;
    @BindView(R.id.price_textlayout)
    TextInputLayout priceTextlayout;
    @BindView(R.id.next_btn)
    Button nextButton;
    @BindView(R.id.end_booking_date)
    TextView endBookingDate;

    String startDateValue;
    String endDateValue;
    String endBookingValue;

    LoadingDialog loadingDialog;

    Trip trip;
   public EditTripFragment(Trip trip){
        this.trip = trip;
    }

    public EditTripFragment(TripModel.Data data) {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }
    @Override
    public void onStart() {
        ((MainActivity)requireActivity()).hideBottomBar();
        super.onStart();
    }

    private void initView() {
       initInfo();
        loadingDialog = new LoadingDialog(requireContext());
        startDate.setOnClickListener(onStartDateClicked);
        endDate.setOnClickListener(onEndDateClicked);
        endBookingDate.setOnClickListener(onEndBookingDateClicked);
        nextButton.setOnClickListener(onNextClicked);
    }

    private void initInfo(){
       title.setText(trip.data.title);
       description.setText(trip.data.description);
       startDate.setText(trip.data.begin_date);
       endDate.setText(trip.data.expire_date);
       endBookingDate.setText(trip.data.end_booking);
       price.setText(trip.data.price);
    }

    private final View.OnClickListener onStartDateClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openDateTimePickerDialog(1);
        }
    };

    private final View.OnClickListener onEndDateClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openDateTimePickerDialog(2);
        }
    };

    private final View.OnClickListener onEndBookingDateClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openDateTimePickerDialog(3);
        }
    };

    private final View.OnClickListener onNextClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (checkInfo()){
                loadingDialog.show();
                TripViewModel.getINSTANCE().createTrip(getCreateTripObject());
                TripViewModel.getINSTANCE().createTripMutableLiveData.observe(requireActivity(),createTripObserver);
            }
        }
    };

    private final Observer<Pair<Trip,String>> createTripObserver = new Observer<Pair<Trip, String>>() {
        @Override
        public void onChanged(Pair<Trip, String> tripStringPair) {
            loadingDialog.dismiss();
            if (tripStringPair != null){
                if (tripStringPair.first != null){
                    FN.addFixedNameFadeFragment(MAIN_FRC, requireActivity(), new EditTripPlaceFragment(tripStringPair.first));
                }
                else {
                    new ErrorDialog(requireContext(), tripStringPair.second).show();
                }
            }
            else {
                new ErrorDialog(requireContext(),"Connection Error").show();
            }
        }
    };

    private void openDateTimePickerDialog(int type) {

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), R.style.MaterialCalendarTheme, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                TimePickerDialog.OnTimeSetListener timeSetListener = (view1, hourOfDay, minute) -> {
                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    c.set(Calendar.MINUTE, minute);

                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:00");

                    if (type == 1) {
                        startDate.setText(simpleDateFormat.format(c.getTime()));
                        startDateValue = simpleDateFormat.format(c.getTime());
                    } else if (type == 2){
                        endDate.setText(simpleDateFormat.format(c.getTime()));
                        endDateValue = simpleDateFormat.format(c.getTime());
                    }
                    else {
                        endBookingDate.setText(simpleDateFormat.format(c.getTime()));
                        endBookingValue = simpleDateFormat.format(c.getTime());
                    }

                };
                new TimePickerDialog(requireContext(),R.style.MaterialCalendarTheme, timeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();

            }
        }, mYear, mMonth, mDay);

        datePickerDialog.show();

        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setText("Ok");
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setText("Cancel");
    }

    private JsonObject getCreateTripObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("title", title.getText().toString());
        jsonObject.addProperty("description", description.getText().toString());
        jsonObject.addProperty("begin_date", startDateValue);
        jsonObject.addProperty("expire_date", endDateValue);
        jsonObject.addProperty("end_booking", endBookingValue);
        jsonObject.addProperty("price", price.getText().toString());
        return jsonObject;
    }

    private boolean checkInfo() {
        boolean ok = true;

        if (title.getText().toString().isEmpty()) {
            titleTextlayout.setError("the filed is required");
            titleTextlayout.setErrorEnabled(true);
            ok = false;
        } else titleTextlayout.setErrorEnabled(false);


        if (description.getText().toString().isEmpty()) {
            descriptionTextlayout.setError("the filed is required");
            descriptionTextlayout.setErrorEnabled(true);
            ok = false;
        } else descriptionTextlayout.setErrorEnabled(false);


        if (price.getText().toString().isEmpty()) {
            priceTextlayout.setError("the filed is required");
            priceTextlayout.setErrorEnabled(true);
            ok = false;
        } else if (price.getText().toString().equals("0")) {
            priceTextlayout.setError("the filed must be greater than zero");
            priceTextlayout.setErrorEnabled(true);
            ok = false;
        }

        if (startDateValue.isEmpty()) {
            ok = false;
            NoteMessage.showSnackBar(requireActivity(), "Start date must be selected");
        } else if (endDateValue.isEmpty()) {
            ok = false;
            NoteMessage.showSnackBar(requireActivity(), "End date must be selected");
        } else if (endBookingValue.isEmpty()) {
            ok = false;
            NoteMessage.showSnackBar(requireActivity(), "Booking end date must be selected");
        }
        else if(checkTripDateTimeValue()){
            ok = false;
            NoteMessage.showSnackBar(requireActivity(), "Trip Date Or Time not valid");
        }
        return ok;
    }

    private boolean checkTripDateTimeValue(){

        if (endDateValue.compareTo(startDateValue) <= 0)  return true;
        if (endBookingValue.compareTo(startDateValue) <= 0) return true;
        if (endDateValue.compareTo(endBookingValue) <= 0) return true;

        return false;
    }
}