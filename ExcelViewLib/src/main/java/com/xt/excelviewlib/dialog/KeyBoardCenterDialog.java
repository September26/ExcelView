package com.xt.excelviewlib.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.xt.excelviewlib.R;


/**
 * 编辑框2
 *
 * @author lxl
 */
public class KeyBoardCenterDialog extends KeyBoardDialog {

    public KeyBoardCenterDialog(Context context, int width, int height) {
        super(context, width, height);
    }


    protected void initView(Context context) {
        mContainer = (ViewGroup) View.inflate(context, R.layout.common_dialog_keyboard_center_layout, null);
        setContentView(mContainer);
        ViewGroup viewGroup = mContainer.findViewById(R.id.keyboard_center_container);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            viewGroup.getChildAt(i).setOnClickListener(this);
        }
    }

    public void bindData(DialogModel dialogModel, SelectCallBack callBack) {
        mDialogModel = dialogModel;
        mCallBack = callBack;
        refreshViewSelect("");
    }

}
