package com.xt.excelview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.xt.excelview.custom.CustomControlImpl;
import com.xt.excelview.databinding.FragmentSecondBinding;
import com.xt.excelview.lib.control.ExcelControlInter;
import com.xt.excelview.lib.view.ExcelView;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Fragment2 extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ExcelControlInter control = new CustomControlImpl(binding.excelView, null);
        ExcelView.TableValueModel tableValueModel = getModel();
        //进行绑定
        control.bindData(tableValueModel);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public ExcelView.TableValueModel getModel() {
        ExcelView.TableValueModel model = new ExcelView.TableValueModel();
        model.columnTitle = "纵向标题";
        model.rowTitle = "横向标题";
        model.onlyReadXNum = 2;
        model.onlyReadYNum = 1;

        //横向，纵向都设置为30格
        int size = 30;
        for (int i = 1; i < size; i++) {
            ArrayList<Double> strings = new ArrayList<>();
            for (int j = 1; j < size; j++) {
                strings.add(new BigDecimal(i + (double) j / 100.0).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
            model.dataList.add(strings);
        }
        model.rows = model.dataList.size();
        model.columns = model.dataList.get(0).size();
        return model;
    }
}