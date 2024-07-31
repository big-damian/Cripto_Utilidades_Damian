package com.damian.criptoutils.ui.account;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.damian.criptoutils.databinding.FragmentAccountBinding;

// MIS IMPORTS
import android.widget.CompoundButton;
import android.text.method.PasswordTransformationMethod;


public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    public static boolean loggeado = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel accountViewModel =
                new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        View rootView = inflater.inflate(R.layout.fra, container, false);
//        linearLayout = (LinearLayout) rootView.findViewById(R.id.linearlayout);
//        return rootView;


        ////////////////////////////////////////////////////////////////////////////////////////////
        // MI CODIGO


        // Ocultar layout loggeado
        if (loggeado) {
            binding.loggeadoAccountLayoutPrincipal.setVisibility(View.VISIBLE);
            binding.accountLayoutPrincipal.setVisibility(View.GONE);
        } else {
            binding.accountLayoutPrincipal.setVisibility(View.VISIBLE);
            binding.loggeadoAccountLayoutPrincipal.setVisibility(View.GONE);
        }


        // Escuchador para mostrar/ocultar contraseña
        binding.switchMostrarContra.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // on below line we are checking
                // if switch is checked or not.
                if (isChecked) {
                    binding.formularioContra.setTransformationMethod(null);
                    Log.e("Login", "Se muestra contraseña");
                } else {
                    binding.formularioContra.setTransformationMethod(new PasswordTransformationMethod());
                    Log.e("Login", "Se vuelve a ocultar la contraseña");
                }
            }
        });

        // Escuchador para mostrar/ocultar contraseña loggeado
        binding.loggeadoSwitchMostrarContra.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // on below line we are checking
                // if switch is checked or not.
                if (isChecked) {
                    binding.loggeadoFormularioPassword.setTransformationMethod(null);
                    Log.e("Login", "Se muestra contraseña");
                } else {
                    binding.loggeadoFormularioPassword.setTransformationMethod(new PasswordTransformationMethod());
                    Log.e("Login", "Se vuelve a ocultar la contraseña");
                }
            }
        });

        // FIN MI CODIGO
        ////////////////////////////////////////////////////////////////////////////////////////////


        final TextView textView = binding.textAccount;
        accountViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}