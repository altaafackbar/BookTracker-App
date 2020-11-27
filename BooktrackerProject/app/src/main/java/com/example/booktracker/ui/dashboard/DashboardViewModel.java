/**
*DashboardViewModel
* ViewModel used in the DashboardFragment
* Helps manage the set up of the fragment
 */
package com.example.booktracker.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Username");
    }

    public LiveData<String> getText() {
        return mText;
    }
}