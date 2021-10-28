package com.xt.excelview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xt.excelview.databinding.FragmentFirstBinding;
import com.xt.excelviewlib.control.ExcelControlInter;
import com.xt.excelviewlib.control.ExcelControlImpl;
import com.xt.excelviewlib.view.ExcelView;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * demo1样例
 * 直接使用ExcelControlImpl以及其中的逻辑
 *
 * @author lxl
 */
public class Fragment1 extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ExcelControlInter control = new ExcelControlImpl(binding.excelView, binding.scrollX, binding.scrollY, null);
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