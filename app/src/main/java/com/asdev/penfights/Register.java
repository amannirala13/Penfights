package com.asdev.penfights;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yalantis.ucrop.UCrop;

import java.io.IOException;
import java.util.Objects;

public class Register extends AppCompatActivity {


    //UI elements defined
    private TextInputEditText userIDText, nameText, phoneText, aboutText;
    private Button continueBtn, anotherGoogleButton;
    private ImageView profileView;
    private CardView profileViewContainer;
    private TextInputLayout userIDContainer, nameContainer, phoneContainer, aboutContainer;

    // Values defined
    private String USER_ID, NAME, PHONE, ABOUT;
    private int PROFILE_PIC_REQUEST_CODE = 1;
    private Uri profilePicUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // UI components initiation
        userIDText = findViewById(R.id.register_userid_text);
        userIDContainer = findViewById(R.id.register_userid_container);
        nameText = findViewById(R.id.register_name_text);
        nameContainer = findViewById(R.id.register_name_container);
        phoneText = findViewById(R.id.register_phone_text);
        phoneContainer = findViewById(R.id.register_phone_container);
        aboutText = findViewById(R.id.register_about_text);
        aboutContainer = findViewById(R.id.register_about_container);
        profileView = findViewById(R.id.register_profile_pic_view);
        profileViewContainer = findViewById(R.id.register_profile_pic_container);

        //UI buttons Initiation
        continueBtn = findViewById(R.id.register_continue_btn);
        anotherGoogleButton = findViewById(R.id.register_another_google_btn);



        //OnClick Listener for continue button
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              if(isValidInput())
              {
                  getValue();
              }
            }
        });

        //OnClick Listener for Use another Google Account button
        anotherGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //OnClick Listener for Upload profile pic
        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(500).repeat(0).playOn(profileViewContainer);  //Animates the image viewer

                openImageChooser();  // Calling image chooser
            }
        });

        phoneText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                set_country_code(phoneText);
            }
        });

    }

    //Sets country code to the phone text
    private void set_country_code(TextInputEditText phoneText) {
        if (!new check().countryCodeExisting(phoneText)) {


            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String countryCodeValue = tm.getNetworkCountryIso();

            if (phoneText.getText().length() <= 0)
                phoneText.setText("+" +(new countryCode().getDialCode(countryCodeValue)) + " ");
            else if (phoneText.getText().charAt(0) == '+')
                phoneText.setText("+" +(new countryCode().getDialCode(countryCodeValue)) + " " + phoneText.getText().toString().substring(1));
            else
                phoneText.setText("+" +(new countryCode().getDialCode(countryCodeValue)) + " " + phoneText.getText());

            int existingValueLength = phoneText.getText().length();
            phoneText.setSelection(existingValueLength);

        }
    }


    // Opens image chooser
    private void openImageChooser() {

        Intent imageChooserIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(imageChooserIntent,PROFILE_PIC_REQUEST_CODE);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

    }

    //Handles image chooser results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PROFILE_PIC_REQUEST_CODE && resultCode== RESULT_OK && data !=null && data.getData() != null)
        {
            profilePicUri = data.getData();   //Stores image URI
            openCropActivity(profilePicUri);


        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE )
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK)
            {
                profilePicUri = result.getUri();
                try {

                    checkImageSize(profilePicUri);  //Checking Bitmap and loading

                } catch (IOException e) {

                    Toast.makeText(this, "Error in loading image!", Toast.LENGTH_SHORT).show();  // Error if image at the URI is not available

                }
            }

        }
    }

    private void openCropActivity(Uri sourceUri) {

        CropImage.activity(sourceUri)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(1,1)
                .setActivityTitle("")
                .setAllowRotation(true)
                .setAllowFlipping(true)
                .setAutoZoomEnabled(true)
                .setActivityMenuIconColor(R.color.colorAccent)
                .start(this);
    }

    //Checks Bitmap size and compares it with runtime memory to prevent memory leaks
    private void checkImageSize(Uri profilePicUri) throws IOException {

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory()); //Max Runtime memory in bytes

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profilePicUri); //Converting URI to Bitmap

        if(bitmap.getByteCount()<maxMemory)
            Picasso.get().load(profilePicUri).fit().into(profileView); // Rendering Bitmap
        else
        {
            Toast.makeText(this,  "Image very large in size!", Toast.LENGTH_SHORT).show(); // Memory leak found
            openImageChooser();     //Recursion of image chooser
        }
    }

    // Gets all the values from text fields
    private void getValue() {
        USER_ID = Objects.requireNonNull(userIDText.getText()).toString();
        NAME = Objects.requireNonNull(nameText.getText()).toString();
        PHONE = Objects.requireNonNull(phoneText.getText()).toString();
        ABOUT = Objects.requireNonNull(aboutText.getText()).toString();
    }

    // Makes sure we get no null values
    private boolean isValidInput() {

        boolean validity = true;

        if(Objects.requireNonNull(userIDText.getText()).toString().compareTo("")==0)
        {
            YoYo.with(Techniques.Shake).duration(700).repeat(0).playOn(userIDContainer);
            validity = false;
        }

        if(Objects.requireNonNull(nameText.getText()).toString().compareTo("")==0)
        {
            YoYo.with(Techniques.Shake).duration(700).repeat(0).playOn(nameContainer);
            validity = false;
        }

        if (Objects.requireNonNull(phoneText.getText()).toString().compareTo("")==0)
        {
            YoYo.with(Techniques.Shake).duration(700).repeat(0).playOn(phoneContainer);
            validity=false;
        }

        if (Objects.requireNonNull(aboutText.getText()).toString().compareTo("")==0)
        {
            YoYo.with(Techniques.Shake).duration(700).repeat(0).playOn(aboutContainer);
            validity = false;
        }

        return validity;
    }
}
