package com.asdev.penfights.helper;

import android.os.Build;

import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.FirebaseAuth;

public class check {

    public int GOOGLE_ACCOUNT_CHANGE_CALL_FLAG = -1;

    // Checks if the SDK is compatible i.e. Lollipop or higher
    public boolean compatibleSDK()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return true;
        } else {
            return false;
        }

    }


    // Checks if the user is Firebase authenticated or not
    public boolean isValidUser()
    {
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
            return false;
        else
            return true;
    }


    //Checks if the country code is valid or not
    public boolean countryCodeExisting( TextInputEditText editText) {
        String currentValue = editText.getText().toString();
        if(currentValue.equals(""))
            return false;

        else if (currentValue.charAt(0) == '+')
        {

            if (currentValue.length() < 3)
                return false;

            else
            {
                int endindex=-1;
                for(int x = 0; x<currentValue.length();x++)
                {
                    if(currentValue.charAt(x)==' ')
                    {endindex = x; break;}
                }
                if(endindex==-1)
                    return false;
                else
                {
                    String dialCode = currentValue.substring(1, endindex);
                    if (new countryCode().isValidDialCode(dialCode))

                        return true;
                    else

                        return false;

                }
            }
        }
        else
            return false;

    }

    //Checks if the text is perfect Phone number
    public boolean isValidPhone(String phone) {
        boolean validity = false;
        if(phone.length()<=15 && phone.length()>=5)
            validity=true;
        return validity;
    }
}
