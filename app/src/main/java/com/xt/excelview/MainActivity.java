package com.xt.excelview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.xt.excelview.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Fragment1 firstFragment;
    private Fragment2 secondFragment;
    private Fragment3 thirdFragment;
    private int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.buttonFirst.setOnClickListener(v -> {
            if (firstFragment == null) {
                firstFragment = new Fragment1();
            }
            showFragment(firstFragment);
        });

        binding.buttonSecond.setOnClickListener(v -> {
            if (secondFragment == null) {
                secondFragment = new Fragment2();
            }
            showFragment(secondFragment);
        });

        binding.buttonThird.setOnClickListener(v -> {
            if (thirdFragment == null) {
                thirdFragment = new Fragment3();
            }
            Bundle bundle = new Bundle();
            bundle.putString("type", "xls");
            thirdFragment.setArguments(bundle);
            showFragment(thirdFragment);
        });

        binding.buttonFourth.setOnClickListener(v -> {
            if (thirdFragment == null) {
                thirdFragment = new Fragment3();
            }
            Bundle bundle = new Bundle();
            bundle.putString("type", "xlsx");
            thirdFragment.setArguments(bundle);
            showFragment(thirdFragment);
        });
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!fragment.isAdded()) {
            fragmentTransaction.add(android.R.id.content, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.show(fragment);
        }
    }

//    private void requestPermission(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            // 先判断有没有权限
//            if (Environment.isExternalStorageManager()) {
//                readFile();
//            } else {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                intent.setData(Uri.parse("package:" + context.getPackageName()));
//                startActivityForResult(intent, REQUEST_CODE);
//            }
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // 先判断有没有权限
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                readFile();
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
//            }
//        } else {
//            readFile();
//        }
//    }
//
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CODE) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                readFile();
//            } else {
//                Toast.makeText(this, "存储权限获取失败", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (Environment.isExternalStorageManager()) {
//                readFile();
//            } else {
//                Toast.makeText(this, "存储权限获取失败", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
}