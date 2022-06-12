package com.example.dayout_organizer.adapter.recyclers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dayout_organizer.R;
import com.example.dayout_organizer.helpers.view.ImageViewer;
import com.example.dayout_organizer.helpers.view.NoteMessage;
import com.example.dayout_organizer.models.passenger.PassengerData;
import com.example.dayout_organizer.ui.activities.MainActivity;
import com.example.dayout_organizer.viewModels.TripViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;

import static com.example.dayout_organizer.api.ApiClient.BASE_URL;

public class PassengersListAdapter extends RecyclerView.Adapter<PassengersListAdapter.ViewHolder> {

    List<PassengerData> passengers;
    Context context;
    private boolean isUpcoming;
    private int tripId;

    public PassengersListAdapter(List<PassengerData> passengers, Context context, boolean isUpcoming, int tripId) {
        this.passengers = passengers;
        this.context = context;
        this.isUpcoming = isUpcoming;
        this.tripId = tripId;
    }

    public void refreshList(List<PassengerData> passengers) {
        this.passengers = passengers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PassengersListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_passenger_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassengersListAdapter.ViewHolder holder, int position) {

        if (isUpcoming) {
            String name = passengers.get(position).user.first_name + " " + passengers.get(position).user.last_name;
            holder.passengerName.setText(name);
            holder.bookingFor.setText(String.valueOf(passengers.get(position).passengers.size()));
            downloadUserImage(passengers.get(position).user.photo, holder.passengerPhoto);
            if (passengers.get(position).confirmed_at != null) {
                holder.confirmButton.setVisibility(View.GONE);
                holder.confirmed.setVisibility(View.VISIBLE);
            }
        } else{
            holder.passengerName.setText(passengers.get(position).passenger_name);
            holder.bookingFor.setVisibility(View.GONE);
            holder.confirmButton.setVisibility(View.GONE);
            holder.bookingForTV.setVisibility(View.GONE);
            holder.passengerPhoto.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return passengers.size();
    }

    private void downloadUserImage(String url, ImageView imageView) {
        String baseUrl = BASE_URL.substring(0, BASE_URL.length() - 1);
        ImageViewer.downloadCircleImage(context, imageView, R.drawable.profile_place_holder_orange, baseUrl + url);
    }

    @SuppressLint("NonConstantResourceId")
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.passenger_item_photo)
        ImageView passengerPhoto;

        @BindView(R.id.passenger_item_name)
        TextView passengerName;

        @BindView(R.id.passenger_item_booking_for)
        TextView bookingFor;

        @BindView(R.id.passneger_item_confirmed)
        TextView confirmed;

        @BindView(R.id.passenger_item_confirm_button)
        Button confirmButton;

        @BindView(R.id.passenger_item_booking_for_textView)
        TextView bookingForTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            confirmButton.setOnClickListener(onConfirmClicked);
        }

        @Override
        public void onClick(View v) {
            //TODO: Go to passenger's profile - Caesar.
        }

        private final View.OnClickListener onConfirmClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PassengerData data = passengers.get(getAdapterPosition());
                confirmBooking(data);
            }
        };

        private void confirmBooking(PassengerData data){
            TripViewModel.getINSTANCE().confirmPassengerBooking(data.customer_id, tripId);
            TripViewModel.getINSTANCE().confirmPassengerBooking.observe((MainActivity) context, confirmObserver);
        }

        private final Observer<Pair<ResponseBody, String>> confirmObserver = new Observer<Pair<ResponseBody, String>>() {
            @Override
            public void onChanged(Pair<ResponseBody, String> responseBodyStringPair) {
                if(responseBodyStringPair != null){
                    if(responseBodyStringPair.first != null){
                        confirmButton.setVisibility(View.GONE);
                        confirmed.setVisibility(View.VISIBLE);
                        NoteMessage.message(context, "Confirmed!");
                    }
                }
            }
        };
    }
}