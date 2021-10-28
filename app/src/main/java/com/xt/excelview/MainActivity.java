package com.xt.excelview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.xt.excelview.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Fragment1 firstFragment;
    private Fragment2 secondFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.buttonFirst.setOnClickListener(v -> {
            if (firstFragment == null) {
                firstFragment = new Fragment1();
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (!firstFragment.isAdded()) {
                fragmentTransaction.add(android.R.id.content, firstFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else {
                fragmentTransaction.show(firstFragment);
            }

        });

        binding.buttonSecond.setOnClickListener(v -> {
            if (secondFragment == null) {
                secondFragment = new Fragment2();
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (!secondFragment.isAdded()) {
                fragmentTransaction.add(android.R.id.content, secondFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else {
                fragmentTransaction.show(secondFragment);
            }
        });
    }


}