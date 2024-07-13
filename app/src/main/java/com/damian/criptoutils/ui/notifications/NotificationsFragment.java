package com.damian.criptoutils.ui.notifications;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.damian.criptoutils.databinding.FragmentNotificationsBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();






        // Repetir codigo cada X segundos
        final Handler handler = new Handler();
        int delay = 500; // 1000 millisegundos (1 segundo)
        handler.postDelayed(new Runnable() {
            public void run() {
        try {
            Process process = Runtime.getRuntime().exec("logcat -d *:E ");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log=new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line + "\n");
            }
//            TextView tv = (TextView)findViewById(R.id.textView1);
//            tv.setText(log.toString());
            binding.textView2.setText(log.toString());
        } catch (IOException e) {
            // Handle Exception
        }


                if (binding.switch1.isChecked() == false) {
                    binding.scrollView.fullScroll(View.FOCUS_DOWN);
                }


                handler.postDelayed(this, delay * 1); //Repetir cada 1 seg
            }
        }, delay);









        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        binding = null;
    }
}