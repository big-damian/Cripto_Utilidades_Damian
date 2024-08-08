package com.damian.criptoutils.ui.account;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.damian.criptoutils.databinding.FragmentAccountBinding;

// MIS IMPORTS
import android.widget.CompoundButton;
import android.text.method.PasswordTransformationMethod;
import android.widget.Toast;


public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    // Variable para recordar cuenta del usuario
    public static boolean loggeado = false;

    // Variables para actualizar datos de cuenta
    boolean estadoEdicion = false;

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

        // Boton actualizar datos de cuenta
        // Encuentra el botón y configura el OnClickListener
        Button loggeadoBotonActualizarDatos = binding.loggeadoBotonActualizarDatos;
        // Animacion para el boton de actualizar (Que el usuario sepa que esta en dicho modo)
        ObjectAnimator animacionParpadeo = ObjectAnimator.ofFloat(binding.loggeadoBotonActualizarDatos, "alpha", 1f, 0.25f, 1f);
        animacionParpadeo.setDuration(2500); // Duración del parpadeo en milisegundos
        animacionParpadeo.setRepeatCount(ObjectAnimator.INFINITE); // Repetir infinitamente
        animacionParpadeo.setRepeatMode(ObjectAnimator.REVERSE); // Revertir la animación

        loggeadoBotonActualizarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (estadoEdicion == false) {

                    // Activamos modo edición y loggeamos
                    estadoEdicion = true;
                    Log.e("LoginActualizar", "Entrado en modo edición de datos de cuenta");

                    // Activamos los campos de texto para que sean editables
                    binding.loggeadoFormularioNombreCompleto.setEnabled(true);
                    binding.loggeadoFormularioNombreUsuario.setEnabled(true);
                    // binding.loggeadoFormularioEmail.setEnabled(true); // No permitimos editar el email porque es nuestra clave unica
                    binding.loggeadoFormularioPassword.setEnabled(true);
                    // Damos animacion al boton de actualizar
                    animacionParpadeo.start();

                } else if (estadoEdicion == true) {

                    // Desactivamos modo edición y loggeamos
                    estadoEdicion = false;
                    Log.e("LoginActualizar", "Enviando cambios de datos cuenta a BDD");

                    // TODO: Poner codigo de actualizar datos en BDD

                    // Cancelamos animación del boton Actualizar
                    animacionParpadeo.cancel();
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