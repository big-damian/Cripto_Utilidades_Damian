package com.damian.criptoutils.ui.account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AccountViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AccountViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");

        //Quitar texto
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}