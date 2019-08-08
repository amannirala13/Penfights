package com.asdev.penfights;

import androidx.annotation.NonNull;
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

import com.asdev.penfights.helper.CustomToast;
import com.asdev.penfights.helper.check;
import com.asdev.penfights.helper.countryCode;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Register extends AppCompatActivity {


    //UI elements defined
    private TextInputEditText userIDText, nameText, phoneText, aboutText;
    private Button continueBtn, anotherGoogleButton;
    private ImageView profileView;
    private CardView profileViewContainer;
    private TextInputLayout userIDContainer, nameContainer, phoneContainer, aboutContainer;
    private GoogleSignInAccount GOOGLE_ACCOUNT;

    // Values defined
    private String USER_ID, NAME, PHONE, ABOUT, EMAIL, REGISTRATION_DATE;
    private Uri  PROFILE_PIC_URI;
    private int PROFILE_PIC_REQUEST_CODE = 1;
    private Uri profilePicUri;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference userDB = db.getReference().child("app").child("user").getRef();

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

        //Variable Initiation
        GOOGLE_ACCOUNT = getIntent().getParcelableExtra("GOOGLE_ACCOUNT");
        profilePicUri = Uri.parse("android.resource://com.asdev.penfights/drawable/ic_default_profile_pic");
        mAuth = FirebaseAuth.getInstance();


        //OnClick Listener for continue button
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              if(isValidInput())
              {
                  getValue();
                  firebaseAuthWithGoogle(GOOGLE_ACCOUNT);

              }
            }
        });

        //OnClick Listener for Use another Google Account button
        anotherGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent googleAccountChangeIntent = new Intent(Register.this, Login.class);
                googleAccountChangeIntent.putExtra("CALL_FLAG", new check().GOOGLE_ACCOUNT_CHANGE_CALL_FLAG);
                startActivity(googleAccountChangeIntent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();

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

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            setRegistrationDate();
                            setProfileDetails(user);
                        }
                        else
                        {
                            showToast("Oops! Unable to connect to server");
                        }

                        // ...
                    }
                });

    }

    private void setRegistrationDate() {
        Date date = Calendar.getInstance() .getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(date);
        REGISTRATION_DATE = formattedDate;
    }

    private void setProfileDetails(FirebaseUser user) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(NAME)
                .setPhotoUri(PROFILE_PIC_URI)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            registerToDatabase();
                        }
                    }
                });
    }

    private void registerToDatabase() {
        final DatabaseReference currentUserDB = userDB.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        currentUserDB.child("userID").setValue(USER_ID);
        currentUserDB.child("Name").setValue(NAME);
        currentUserDB.child("Email").setValue(EMAIL);
        currentUserDB.child("Phone").setValue(PHONE);
        currentUserDB.child("About").setValue(ABOUT);
        currentUserDB.child("Reg_Date").setValue(REGISTRATION_DATE);
        currentUserDB.child("Premium").setValue(false);

        openGenreActivity();

    }

    private void openGenreActivity() {

        startActivity(new Intent(Register.this, Genre.class));
        finish();
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

                  showToast("Error in loading image!");  // Error if image at the URI is not available

                }
            }

        }
    }

    //Open the image to crop
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
          showToast("Image very large in size!" );// Memory leak found
            openImageChooser();     //Recursion of image chooser
        }
    }

    // Gets all the values from text fields
    private void getValue() {

        USER_ID = Objects.requireNonNull(userIDText.getText()).toString();
        NAME = Objects.requireNonNull(nameText.getText()).toString();
        PHONE = Objects.requireNonNull(phoneText.getText()).toString();
        ABOUT = Objects.requireNonNull(aboutText.getText()).toString();
        EMAIL = GOOGLE_ACCOUNT.getEmail();
        PROFILE_PIC_URI = profilePicUri;
    }

    // Makes sure we get no null values
    private boolean isValidInput() {

        boolean validity = true;

        if(Objects.requireNonNull(userIDText.getText()).toString().compareTo("")==0) {
            YoYo.with(Techniques.Shake).duration(700).repeat(0).playOn(userIDContainer);
            validity = false;
        }
        if(Objects.requireNonNull(nameText.getText()).toString().compareTo("")==0) {
            YoYo.with(Techniques.Shake).duration(700).repeat(0).playOn(nameContainer);
            validity = false;
        }
        if (Objects.requireNonNull(phoneText.getText()).toString().compareTo("")==0) {
            YoYo.with(Techniques.Shake).duration(700).repeat(0).playOn(phoneContainer);
            validity=false;
        }
        if (Objects.requireNonNull(aboutText.getText()).toString().compareTo("")==0) {
            YoYo.with(Techniques.Shake).duration(700).repeat(0).playOn(aboutContainer);
            validity = false;
        }
        return validity;
    }

    //Creates Toast
    private void showToast(String MESSAGE) {

        CustomToast toast = new CustomToast(this);
        toast.setMessage(MESSAGE);
        toast.setLongDuration();
        toast.show();
    }
}
