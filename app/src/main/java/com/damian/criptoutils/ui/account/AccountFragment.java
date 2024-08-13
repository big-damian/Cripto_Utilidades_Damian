package com.damian.criptoutils.ui.account;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.google.android.material.snackbar.Snackbar;

// MIS IMPORTS
import com.damian.criptoutils.utilities.MySQLManager;
import android.widget.CompoundButton;
import android.text.method.PasswordTransformationMethod;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// TODO: Pensar si se pone funcionalidad de recordar la cuenta y de 'olvidé mi contraseña'
public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    // Variable para recordar cuenta del usuario
    public static boolean loggeado = false;

    // Variables para actualizar datos de cuenta
    public static boolean estadoEdicion = false;

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
                    Log.e("MySQLCuentaActualizar", "Entrado en modo edición de datos de cuenta");

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
                    Log.e("MySQLCuentaActualizar", "Enviando cambios de datos cuenta a BDD y cambiando a modoEdicion false");

                    int resultadoActualizacionDatos = actualizarUsuarioMySQL();

                    if (resultadoActualizacionDatos == 1) {

                        // Desactivamos los campos de texto para que ya no sean editables
                        binding.loggeadoFormularioNombreCompleto.setEnabled(false);
                        binding.loggeadoFormularioNombreUsuario.setEnabled(false);
                        // binding.loggeadoFormularioEmail.setEnabled(true); // No permitimos editar el email porque es nuestra clave unica
                        binding.loggeadoFormularioPassword.setEnabled(false);

                        // Cancelamos animación del boton Actualizar
                        binding.loggeadoFormularioNombreCompleto.setAlpha(1f); // Asegura que el botón quede visible
                        animacionParpadeo.cancel();

                    } else if (resultadoActualizacionDatos == 0) {

                        // Dejamos estadoEdicion en true para que se vuelva a intentar
                        estadoEdicion = true;

                        // TODO: (ARREGLAR ESTA GUARRADA) Arreglar esta parte del codigo de actualizar datos en BDD
                        // Desactivamos los campos de texto para que ya no sean editables
                        binding.loggeadoFormularioNombreCompleto.setEnabled(false);
                        binding.loggeadoFormularioNombreUsuario.setEnabled(false);
                        // binding.loggeadoFormularioEmail.setEnabled(true); // No permitimos editar el email porque es nuestra clave unica
                        binding.loggeadoFormularioPassword.setEnabled(false);

                        // Cancelamos animación del boton Actualizar
                        ObjectAnimator animacionParpadeo = ObjectAnimator.ofFloat(binding.loggeadoBotonActualizarDatos, "alpha", 1f);
                        binding.loggeadoFormularioNombreCompleto.setAlpha(1f); // Asegura que el botón quede visible
                        animacionParpadeo.end();

                        estadoEdicion = false;
                        // ----------------------------------------------------------------------------------------

                    } else if (resultadoActualizacionDatos > 1) {

                        // Desactivamos los campos de texto para que ya no sean editables
                        binding.loggeadoFormularioNombreCompleto.setEnabled(false);
                        binding.loggeadoFormularioNombreUsuario.setEnabled(false);
                        // binding.loggeadoFormularioEmail.setEnabled(true); // No permitimos editar el email porque es nuestra clave unica
                        binding.loggeadoFormularioPassword.setEnabled(false);

                        // Cancelamos animación del boton Actualizar
                        binding.loggeadoFormularioNombreCompleto.setAlpha(1f); // Asegura que el botón quede visible
                        animacionParpadeo.cancel();

                        // Dejamos estadoEdicion en true para que se vuelva a intentar
                        estadoEdicion = false;

                    } else { }
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

    int filasActualizadas; // Variable de clase para el metodo actualizarUsuarioMySQL()
    public int actualizarUsuarioMySQL() {
        filasActualizadas = 0;

        ObjectAnimator animacionParpadeo = ObjectAnimator.ofFloat(binding.loggeadoBotonActualizarDatos, "alpha", 1f, 0.25f, 1f);
        animacionParpadeo.setDuration(1500); // Duración del parpadeo en milisegundos
        animacionParpadeo.setRepeatCount(ObjectAnimator.INFINITE); // Repetir infinitamente
        animacionParpadeo.setRepeatMode(ObjectAnimator.REVERSE); // Revertir la animación
        animacionParpadeo.start();

        String nuevoNombreCompleto = binding.loggeadoFormularioNombreCompleto.getText().toString();
        String nuevoNombreUsuario = binding.loggeadoFormularioNombreUsuario.getText().toString();
        String nuevoPassword = binding.loggeadoFormularioPassword.getText().toString();

        // Comprobamos que el nombre de usuario y la contraseña cumplen con los minimos
        if (binding.loggeadoFormularioNombreUsuario.getText().toString().equals("")) {
            Snackbar snackbar = Snackbar.make(getView(), "El nombre de usuario no puede estar en blanco", Snackbar.LENGTH_SHORT);
            /* Aplicar margen inferior de 50dp */ snackbar.getView().setTranslationY(-50 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
            snackbar.show();

            //filasActualizadas = 0; return filasActualizadas; // Devolvemos filasActualizadas = 0 indicando que la actualizacion no se hizo
        }
        if (binding.loggeadoFormularioPassword.getText().toString().length() < 4) {
            Snackbar snackbar = Snackbar.make(getView(), "La contraseña no puede ser inferior a 4 caracteres", Snackbar.LENGTH_SHORT);
            /* Aplicar margen inferior de 50dp */ snackbar.getView().setTranslationY(-50 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
            snackbar.show();

            //filasActualizadas = 0; return filasActualizadas; // Devolvemos filasafectadas = 0 indicando que la actualizacion no se hizo
        }
        if (!binding.loggeadoFormularioNombreUsuario.getText().toString().equals("") && binding.loggeadoFormularioPassword.getText().toString().length() >= 4) {

            binding.loggeadoBotonActualizarDatos.setEnabled(false);

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {

                    Connection cone = MySQLManager.ConexionMySQL();

                    if (cone == null) {
                        Log.e("MySQLCuentaActualizar", "Error al conectar a MySQL");
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "No se puede conectar con la Base de datos, comprueba tu internet", Toast.LENGTH_SHORT).show());
                    } else {
                        Log.e("MySQLCuentaActualizar", "Conectado a MySQL");
                    }

                    String query = "UPDATE usuarios SET username = ?, contraseña = ?, nombre = ? WHERE email = ?";
                    PreparedStatement statement = cone.prepareStatement(query);
                    statement.setString(1, nuevoNombreUsuario);
                    statement.setString(2, nuevoPassword);
                    statement.setString(3, nuevoNombreCompleto);
                    statement.setString(4, binding.loggeadoFormularioEmail.getText().toString());

                    // Ejecutamos query update
                    int filasActualizadasLambda = statement.executeUpdate();
                    // Asignamos el filas actualizadas para devolver
                    filasActualizadas = filasActualizadasLambda;

                    if (filasActualizadasLambda == 1) {
                        Log.e("MySQLCuentaActualizar", "Registro actualizado exitosamente");
                        getActivity().runOnUiThread(() -> {
                            Snackbar snackbar = Snackbar.make(getView(), "Datos actualizados exitosamente", Snackbar.LENGTH_SHORT);
                            /* Aplicar margen inferior de 50dp */
                            snackbar.getView().setTranslationY(-50 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                            snackbar.show();
                        });
                    } else if (filasActualizadasLambda > 1) {
                        Log.e("MySQLCuentaActualizar", "MULTIPLES registros actualizados!!");
                        getActivity().runOnUiThread(() -> {
                            Snackbar snackbar = Snackbar.make(getView(), "Datos actualizados exitosamente", Snackbar.LENGTH_SHORT);
                            /* Aplicar margen inferior de 250dp */
                            snackbar.getView().setTranslationY(-250 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                            snackbar.show();
                        });
                    } else {
                        Log.e("MySQLCuentaActualizar", "No se encontró el registro para actualizar");
                        getActivity().runOnUiThread(() -> {
                            Snackbar snackbar = Snackbar.make(getView(), "No se pudo actualizar los datos, comprueba los datos introducidos", Snackbar.LENGTH_SHORT);
                            /* Aplicar margen inferior de 250dp */
                            snackbar.getView().setTranslationY(-250 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                            snackbar.show();
                        });
                    }

                    statement.close();
                    cone.close();

                } catch (SQLException e) {
                    Log.e("MySQLCuentaActualizar", Log.getStackTraceString(e));
                }
            });

            animacionParpadeo.end();
            binding.loggeadoBotonActualizarDatos.setEnabled(true);

        }

        return filasActualizadas;
        
    }
}