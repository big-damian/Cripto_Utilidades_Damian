package com.damian.criptoutils.ui.account_loggeado;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AccountLoggeadoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AccountLoggeadoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");

        //Quitar texto
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}