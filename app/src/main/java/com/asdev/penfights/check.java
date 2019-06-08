package com.asdev.penfights;

import com.google.firebase.auth.FirebaseAuth;

public class check {

    boolean isValidUser() // Checks is the user is logged in or not
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser()==null)
            return false;
        else
            return true;
    }
}
