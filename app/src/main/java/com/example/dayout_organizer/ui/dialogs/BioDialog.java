 package com.example.dayout_organizer.ui.dialogs;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.example.dayout_organizer.R;


import com.example.dayout_organizer.helpers.system.HttpRequestConverter;
import com.example.dayout_organizer.models.profile.ProfileModel;
import com.example.dayout_organizer.ui.activities.MainActivity;
import com.example.dayout_organizer.ui.dialogs.notify.ErrorDialog;
import com.example.dayout_organizer.ui.dialogs.notify.LoadingDialog;
import com.example.dayout_organizer.viewModels.UserViewModel;
import com.google.gson.JsonObject;


import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;

 @SuppressLint("NonConstantResourceId")
public class BioDialog extends Dialog {


    @BindView(R.id.bio_dialog_text_field)
    EditText bioDialogTextField;

    @BindView(R.id.bio_dialog_cancel_button)
    Button bioDialogCancelButton;

    @BindView(R.id.bio_dialog_save_button)
    Button bioDialogSaveButton;

    ProfileModel profileModel;

    LoadingDialog loadingDialog;

    Context context;

    public String bioString;


    public BioDialog(@NonNull Context context, ProfileModel profileModel) {
        super(context);
        this.context = context;
        this.profileModel = profileModel;
        setContentView(R.layout.bio_dialog);
        setCancelable(false);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        bioDialogSaveButton.setOnClickListener(onSaveButtonClicked);
        bioDialogCancelButton.setOnClickListener(onCancelButtonClicked);
        loadingDialog = new LoadingDialog(getContext());
    }


    public String getBioString(){
        return bioString;
    }

    private final View.OnClickListener onSaveButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            loadingDialog.show();
            UserViewModel.getINSTANCE().editProfile(getRequestBody("PUT"),
                    getRequestBody(profileModel.data.user.first_name),
                    getRequestBody(profileModel.data.user.last_name),
                    getRequestBody(bioDialogTextField.getText().toString()),
                    getRequestBody(profileModel.data.user.email),
                   null);
            UserViewModel.getINSTANCE().successfulPairMutableLiveData.observe((MainActivity) context, bioObserver);
        }
    };

     private RequestBody getRequestBody(String body) {
         return HttpRequestConverter.createStringAsRequestBody("multipart/form-data", body);
     }

    private final Observer<Pair<Boolean, String>> bioObserver = new Observer<Pair<Boolean, String>>() {
        @Override
        public void onChanged(Pair<Boolean, String> editProfileModelStringPair) {
            loadingDialog.dismiss();
            if (editProfileModelStringPair != null) {
                if (editProfileModelStringPair.first != null) {
                    cancel();
                } else
                    new ErrorDialog(getContext(), editProfileModelStringPair.second).show();
            } else
                new ErrorDialog(getContext(), getContext().getResources().getString(R.string.error_connection)).show();
        }
    };

    private final View.OnClickListener onCancelButtonClicked = view -> dismiss();

    @Override
    public void show() {
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        getWindow().setAttributes(wlp);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // match width dialog
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        super.show();
    }
}