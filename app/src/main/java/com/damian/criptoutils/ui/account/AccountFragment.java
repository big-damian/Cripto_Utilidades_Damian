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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.damian.criptoutils.MainActivity;
import com.damian.criptoutils.databinding.FragmentAccountBinding;
import com.damian.criptoutils.utilities.SQLiteManager;
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

        // Comprobar si usuario recordado y cargar desde BDD
        // Abrir base de datos y comprobar si guardado usuario
        SQLiteManager dbManager = new SQLiteManager(getContext());
        dbManager.open();
        if (dbManager.comprobarUsarioRecordado().equals("true")) {
            Log.e("Login", "Usario recordado, cargando en campos");
            binding.formularioEmail.setText(dbManager.recuperarEmailRecordado());
            binding.formularioContra.setText(dbManager.recuperarContraseñaRecordado());
            binding.switchRecordarContra.setChecked(true);

            // Si el usuario tiene los datos guardados inciamos sesion directamente
            ((MainActivity)getActivity()).hacerLoginMySQL();

        } else {
            Log.e("Login", "No hay usuario recordado");
            binding.formularioEmail.setText("");
            binding.formularioContra.setText("");
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

        // Escuchador para recordar contraseña
        binding.switchRecordarContra.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // on below line we are checking
                // if switch is checked or not.
                if (isChecked) {
                    // Llamar a metodo guardar usuario
                    dbManager.recordarUsuarioBDD(binding.formularioEmail.getText().toString(), binding.formularioContra.getText().toString());
                    Log.e("Login", "Se guarda email y contraseña");
                } else {
                    // Llamar a metodo eliminar usuarios guardados
                    dbManager.olvidarUsuarioBDD();
                    Log.e("Login", "Se borra email y contraseña");
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
                    binding.loggeadoFormularioPassword.setEnabled(true);

                    // Damos animación al botón de actualizar
                    animacionParpadeo.start();

                } else {
                    // Desactivamos modo edición y loggeamos
                    estadoEdicion = false;
                    Log.e("MySQLCuentaActualizar", "Enviando cambios de datos cuenta a BDD y cambiando a modoEdicion false");

                    // Realizamos las comprobaciones necesarias y la actualización en la base de datos
                    String nuevoNombreCompleto = binding.loggeadoFormularioNombreCompleto.getText().toString();
                    String nuevoNombreUsuario = binding.loggeadoFormularioNombreUsuario.getText().toString();
                    String nuevoPassword = binding.loggeadoFormularioPassword.getText().toString();

                    if (nuevoNombreUsuario.isEmpty()) {
                        Snackbar snackbar = Snackbar.make(getView(), "El nombre de usuario no puede estar en blanco", Snackbar.LENGTH_SHORT);
                        snackbar.getView().setTranslationY(-50 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                        snackbar.show();
                        estadoEdicion = true;
                        return;
                    }

                    if (nuevoPassword.length() < 4) {
                        Snackbar snackbar = Snackbar.make(getView(), "La contraseña no puede ser inferior a 4 caracteres", Snackbar.LENGTH_SHORT);
                        snackbar.getView().setTranslationY(-50 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                        snackbar.show();
                        estadoEdicion = true;
                        return;
                    }

                    // Deshabilitamos el botón para evitar múltiples clics
                    binding.loggeadoBotonActualizarDatos.setEnabled(false);

                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(() -> {
                        try {
                            Connection cone = MySQLManager.ConexionMySQL();

                            if (cone == null) {
                                Log.e("MySQLCuentaActualizar", "Error al conectar a MySQL");
                                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "No se puede conectar con la Base de datos, comprueba tu internet", Toast.LENGTH_SHORT).show());
                            } else {
                                Log.e("MySQLCuentaActualizar", "Conectado a MySQL para actualizar datos de usuario");

                                String query = "UPDATE usuarios SET username = ?, contraseña = ?, nombre = ? WHERE email = ?";
                                PreparedStatement statement = cone.prepareStatement(query);
                                statement.setString(1, nuevoNombreUsuario);
                                statement.setString(2, nuevoPassword);
                                statement.setString(3, nuevoNombreCompleto);
                                statement.setString(4, binding.loggeadoFormularioEmail.getText().toString());

                                int filasActualizadas = statement.executeUpdate();

                                getActivity().runOnUiThread(() -> {
                                    if (filasActualizadas == 1) {
                                        Log.e("MySQLCuentaActualizar", "Registro actualizado exitosamente");
                                        Snackbar snackbar = Snackbar.make(getView(), "Datos actualizados exitosamente", Snackbar.LENGTH_SHORT);
                                        snackbar.getView().setTranslationY(-50 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                                        snackbar.show();

                                        // Desactivamos los campos de texto para que ya no sean editables
                                        binding.loggeadoFormularioNombreCompleto.setEnabled(false);
                                        binding.loggeadoFormularioNombreUsuario.setEnabled(false);
                                        binding.loggeadoFormularioPassword.setEnabled(false);

                                        // Cancelamos animación del botón Actualizar
                                        binding.loggeadoFormularioNombreCompleto.setAlpha(1f); // Asegura que el botón quede visible
                                        animacionParpadeo.end();
                                    } else if (filasActualizadas > 1) {
                                        Log.e("MySQLCuentaActualizar", "MULTIPLES registros actualizados!!");
                                        Snackbar snackbar = Snackbar.make(getView(), "Datos actualizados exitosamente (?)", Snackbar.LENGTH_SHORT);
                                        snackbar.getView().setTranslationY(-250 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                                        snackbar.show();
                                    } else {
                                        Log.e("MySQLCuentaActualizar", "No se encontró el registro para actualizar");
                                        Snackbar snackbar = Snackbar.make(getView(), "No se pudo actualizar los datos, comprueba los datos introducidos", Snackbar.LENGTH_SHORT);
                                        snackbar.getView().setTranslationY(-250 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                                        snackbar.show();
                                    }
                                });

                                statement.close();
                                cone.close();
                            }
                        } catch (SQLException e) {
                            Log.e("MySQLCuentaActualizar", Log.getStackTraceString(e));
                        } finally {
                            // Restauramos el estado del botón al finalizar la tarea
                            getActivity().runOnUiThread(() -> binding.loggeadoBotonActualizarDatos.setEnabled(true));
                            getActivity().runOnUiThread(() -> animacionParpadeo.end());
                        }
                    });
                }
            }
        });

        // Boton cerrar sesion
        binding.loggeadoBotonCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Establecemos loggeado en false para el recordar contraseña
                loggeado = false;

                // Establecemos estado de edicion en false y reestablecemos siempre que se cierre sesión
                estadoEdicion = false;
                // Desactivamos los campos de texto para que ya no sean editables
                binding.loggeadoFormularioNombreCompleto.setEnabled(false);
                binding.loggeadoFormularioNombreUsuario.setEnabled(false);
                binding.loggeadoFormularioPassword.setEnabled(false);
                // Quitamos animación al botón actualizar
                animacionParpadeo.end();

                // Cambiar la visibilidad de los layouts
                binding.loggeadoAccountLayoutPrincipal.setVisibility(View.GONE);
                binding.accountLayoutPrincipal.setVisibility(View.VISIBLE);

                // Mostrar snackbar de éxito en el logout
                Snackbar snackbar = Snackbar.make(getView(), "Cerrada sesión exitosamente", Snackbar.LENGTH_LONG);
                /* Aplicar margen inferior de 50dp */ snackbar.getView().setTranslationY(-50 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                snackbar.show();
            }
        });


        // FIN MI CODIGO
        ////////////////////////////////////////////////////////////////////////////////////////////


        final TextView textView = binding.textAccount;
        accountViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Desactivar el botón de retroceso mientras este fragmento esté activo
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // No hacer nada para desactivar la función de retroceso
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}