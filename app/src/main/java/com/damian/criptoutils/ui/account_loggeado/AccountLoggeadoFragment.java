package com.damian.criptoutils.ui.account_loggeado;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.damian.criptoutils.R;
import com.damian.criptoutils.databinding.FragmentAccountLoggeadoBinding;
import com.damian.criptoutils.ui.account_loggeado.AccountLoggeadoViewModel;

// MIS IMPORTS
import android.widget.CompoundButton;
import android.text.method.PasswordTransformationMethod;


public class AccountLoggeadoFragment extends Fragment {

    private FragmentAccountLoggeadoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AccountLoggeadoViewModel accountLoggeadoViewModel =
                new ViewModelProvider(this).get(AccountLoggeadoViewModel.class);

        binding = FragmentAccountLoggeadoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        ////////////////////////////////////////////////////////////////////////////////////////////
        // MI CODIGO



        // FIN MI CODIGO
        ////////////////////////////////////////////////////////////////////////////////////////////


        final TextView textView = binding.textHome;
//        accountLoggeadoViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

//    public void metodo() {
//        // Create new fragment and transaction
//        Fragment newFragment = new AccountFragmentLoggeado;
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//// Replace whatever is in the fragment_container view with this fragment,
//// and add the transaction to the back stack
//        transaction.replace(R.id.fragment_container, newFragment);
//        transaction.addToBackStack(null);
//
//// Commit the transaction
//        transaction.commit();
//    }

}