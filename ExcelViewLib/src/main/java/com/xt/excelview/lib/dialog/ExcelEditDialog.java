package com.xt.excelview.lib.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.xt.excelview.lib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用dialog
 */
public class ExcelEditDialog extends PopupWindow implements View.OnClickListener {

    private LinearLayout mContainer;
    private ClickCallBack mCallBack;
    private int dipUnit = 3;


    public ExcelEditDialog(Context context, int w, int h) {
        super(context);
        setWidth(w);
        setHeight(h);
        initView(context);
//        setFocusable(true);//是否允许事件穿透
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0));//new ColorDrawable(0)即为透明背景
    }


    private void initView(Context context) {
        mContainer = new LinearLayout(context);
        mContainer.setOrientation(LinearLayout.HORIZONTAL);
        mContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        mContainer.setBackgroundColor(Color.BLACK);
        mContainer.setGravity(Gravity.CENTER_VERTICAL);
        int padding = dipUnit * 6;
        mContainer.setPadding(padding, padding, padding, padding);
        mContainer.setDividerDrawable(context.getResources().getDrawable(R.drawable.common_divider_ffffff));
        setContentView(mContainer);
    }

    public void bindData(DialogModel dialogModel, ClickCallBack callBack) {
        mCallBack = callBack;
        mContainer.removeAllViews();
        for (int i = 0; i < dialogModel.dataList.size(); i++) {
            TextView textView = new TextView(mContainer.getContext());
            textView.setText(dialogModel.dataList.get(i));
            textView.setOnClickListener(this);
            textView.setTag(i);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setIncludeFontPadding(false);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, dipUnit * 11);
            mContainer.addView(textView, createLP());
        }
    }

    private LinearLayout.LayoutParams createLP() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.weight = 1;
        lp.gravity = Gravity.CENTER_VERTICAL;
        //去掉text的
//        lp.topMargin = (int) mContainer.getContext().getResources().getDimension(R.dimen.dip_1);
        return lp;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (mCallBack == null) {
            return;
        }
        mCallBack.clickByIndex((int) v.getTag());
    }

    public interface ClickCallBack {
        void clickByIndex(int index);
    }

    static public class DialogModel {
        List<String> dataList = new ArrayList<>();

        public void setDataList(List<String> list) {
            dataList = list;
        }
    }

    public void setDipUnit(int dipUnit) {
        this.dipUnit = dipUnit;
    }
}
