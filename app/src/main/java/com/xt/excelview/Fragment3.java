package com.xt.excelview;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xt.excelview.custom.CustomControlImpl;
import com.xt.excelview.databinding.FragmentThirdBinding;
import com.xt.excelview.util.IOHelper;
import com.xt.excelviewlib.control.ExcelControlInter;
import com.xt.excelviewlib.util.PoiUtil;
import com.xt.excelviewlib.view.ExcelView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Fragment3 extends Fragment {

    private FragmentThirdBinding binding;
    private final String FILE_NAME = "demo.xls";
    private String filePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filePath = getActivity().getFilesDir().getAbsolutePath() + File.separator + FILE_NAME;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentThirdBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //拷贝文件到data/file目录下，然后跳转

        File file = new File(filePath);
        if (!file.exists()) {
            try {
                InputStream open = getActivity().getAssets().open(FILE_NAME);
                IOHelper.copyFileFromInputStream(open, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        showExcel(file);
    }

    private void showExcel(File file) {
        ExcelControlInter control = new CustomControlImpl(binding.excelView, null);
        ExcelView.TableValueModel model = PoiUtil.readExcel(file);
        Log.i("lxltest", "model:" + model.dataList.size());
        //进行绑定
        control.bindData(model);

        binding.buttonSave.setOnClickListener(v -> {
            //很对model生成
            PoiUtil.write2Excel(filePath, model);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}