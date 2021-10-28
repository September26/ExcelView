package com.xt.excelviewlib.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xt.excelviewlib.R;
import com.xt.excelviewlib.util.StringUtil;


/**
 * 编辑框1
 *
 * @author lxl
 */
public class KeyBoardDialog extends PopupWindow implements View.OnClickListener {

    public final static int TYPE_ADD = 1;
    public final static int TYPE_SUB = 2;
    public final static int TYPE_MULTI = 3;
    public final static int TYPE_DIVISION = 4;
    public final static int TYPE_ASSIGN = 5;

    protected ViewGroup mContainer;
    protected SelectCallBack mCallBack;
    protected DialogModel mDialogModel;

    public KeyBoardDialog(Context context, int width, int height) {
        super(context);
        setWidth(width);
        setHeight(height);
        initView(context);
        setFocusable(true);//是否允许事件穿透
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0));//new ColorDrawable(0)即为透明背景
    }


    protected void initView(Context context) {
        mContainer = (ViewGroup) View.inflate(context, R.layout.common_dialog_keyboard_layout, null);
        for (int i = 0; i < mContainer.getChildCount(); i++) {
            mContainer.getChildAt(i).setOnClickListener(this);
        }
        setContentView(mContainer);
    }

    public void bindData(DialogModel dialogModel, SelectCallBack callBack) {
        mDialogModel = dialogModel;
        mCallBack = callBack;
        refreshViewSelect("");
    }

    public void refreshViewSelect(String typeValue) {
        int type = mDialogModel.type;
        mContainer.findViewById(R.id.keyboard_add).setSelected(type == TYPE_ADD);
        mContainer.findViewById(R.id.keyboard_sub).setSelected(type == TYPE_SUB);
        mContainer.findViewById(R.id.keyboard_multi).setSelected(type == TYPE_MULTI);
        mContainer.findViewById(R.id.keyboard_division).setSelected(type == TYPE_DIVISION);
        mContainer.findViewById(R.id.keyboard_assign).setSelected(type == TYPE_ASSIGN);
        if (StringUtil.emptyOrNull(typeValue)) {
            if (type == TYPE_ADD) {
                typeValue = "加";
            } else if (type == TYPE_SUB) {
                typeValue = "减";
            } else if (type == TYPE_MULTI) {
                typeValue = "乘";
            } else if (type == TYPE_DIVISION) {
                typeValue = "除";
            } else if (type == TYPE_ASSIGN) {
                typeValue = "编辑";
            }
        }
        TextView typeValueText = (TextView) (mContainer.findViewById(R.id.keyboard_type_value));
        if (typeValueText != null) {
            typeValueText.setText(typeValue);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.keyboard_close) {
            dismiss();
            return;
        }
        TextView valueText = (TextView) (mContainer.findViewById(R.id.keyboard_value_value));
        String typeValue = null;
        if (id == R.id.keyboard_add) {
            mDialogModel.type = TYPE_ADD;
            typeValue = "加";
        }
        if (id == R.id.keyboard_sub) {
            mDialogModel.type = TYPE_SUB;
            typeValue = "减";
        }
        if (id == R.id.keyboard_multi) {
            mDialogModel.type = TYPE_MULTI;
            typeValue = "乘";
        }
        if (id == R.id.keyboard_division) {
            mDialogModel.type = TYPE_DIVISION;
            typeValue = "除";
        }
        if (id == R.id.keyboard_assign) {
            mDialogModel.type = TYPE_ASSIGN;
            typeValue = "编辑";
        }
        if (!StringUtil.emptyOrNull(typeValue)) {
            refreshViewSelect(typeValue);
            return;
        }
        if (id == R.id.keyboard_reversal && mCallBack != null) {
            mCallBack.selectByType(TYPE_MULTI, -1.0);
            dismiss();
            return;
        }
        if (id == R.id.keyboard_action_confirm && mCallBack != null) {
            double v1 = Double.parseDouble(valueText.getText().toString());
            mCallBack.selectByType(mDialogModel.type, v1);
            dismiss();
            return;
        }

        String currentValue = valueText.getText().toString();
        if (id == R.id.keyboard_num_1) {
            currentValue += "1";
        } else if (id == R.id.keyboard_num_2) {
            currentValue += "2";
        } else if (id == R.id.keyboard_num_3) {
            currentValue += "3";
        } else if (id == R.id.keyboard_num_4) {
            currentValue += "4";
        } else if (id == R.id.keyboard_num_5) {
            currentValue += "5";
        } else if (id == R.id.keyboard_num_6) {
            currentValue += "6";
        } else if (id == R.id.keyboard_num_7) {
            currentValue += "7";
        } else if (id == R.id.keyboard_num_8) {
            currentValue += "8";
        } else if (id == R.id.keyboard_num_9) {
            currentValue += "9";
        } else if (id == R.id.keyboard_num_0) {
            currentValue += "0";
        } else if (id == R.id.keyboard_del) {
            if (currentValue.length() >= 1) {
                currentValue = currentValue.substring(0, currentValue.length() - 1);
            } else {
                currentValue = "";
            }
        } else if (id == R.id.keyboard_negative) {
            if (StringUtil.emptyOrNull(currentValue)) {
                currentValue = "-";
            } else {
                currentValue = String.valueOf(Double.parseDouble(currentValue) * -1);
            }
        } else {
            return;
        }
        valueText.setText(currentValue);
    }

    public interface SelectCallBack {
        void selectByType(int type, double value);
    }

    static public class DialogModel {
        private int type = 0;

        public int getType() {
            return this.type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
