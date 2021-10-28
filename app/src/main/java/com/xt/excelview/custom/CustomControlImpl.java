package com.xt.excelview.custom;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.xt.excelviewlib.control.ExcelControlImpl;
import com.xt.excelviewlib.dialog.KeyBoardCenterDialog;
import com.xt.excelviewlib.dialog.KeyBoardDialog;
import com.xt.excelviewlib.util.DeviceUtil;
import com.xt.excelviewlib.view.ExcelView;

import java.util.List;

/**
 * 开发者自定义
 */
public class CustomControlImpl extends ExcelControlImpl {

    public CustomControlImpl(ExcelView excelView, View.OnClickListener listener) {
        super(excelView, listener);
    }

    @Override
    public void showKeyboard(ExcelView.TableValueModel tableModel, int[] selectRange, int selectType) {
        //这里使用KeyBoardCenterDialog替换原来的KeyBoardDialog，从而实现不一样的显示效果
        List<List<Double>> dataList = tableModel.dataList;
        KeyBoardCenterDialog popWin = new KeyBoardCenterDialog(context, DeviceUtil.getScreenSize(context)[0], dipUnit * 137);
        KeyBoardDialog.DialogModel dialogModel = new KeyBoardDialog.DialogModel();
        dialogModel.setType(selectType);
        popWin.bindData(dialogModel, new KeyBoardDialog.SelectCallBack() {
            @Override
            public void selectByType(int type, double value) {
                if (type == KeyBoardCenterDialog.TYPE_DIVISION && value == 0.0) {
                    Toast.makeText(context, "不能除以0，请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                int startColumn = selectRange[0];
                int startRow = selectRange[1];
                int endColumn = selectRange[2];
                int endRow = selectRange[3];
                //针对数据进行批量处理
                for (int i = 0; i < dataList.size(); i++) {
                    if (i < startRow || i > endRow) {
                        continue;
                    }
                    for (int j = startColumn; j < endColumn; j++) {
                        Double old = dataList.get(i).get(j);
                        Double newValue = 0.0;
                        switch (type) {
                            case KeyBoardCenterDialog.TYPE_ADD:
                                newValue = old + value;
                                break;
                            case KeyBoardCenterDialog.TYPE_SUB:
                                newValue = old - value;
                                break;
                            case KeyBoardCenterDialog.TYPE_MULTI:
                                newValue = old * value;
                                break;
                            case KeyBoardCenterDialog.TYPE_DIVISION:
                                newValue = old / value;
                                break;
                            case KeyBoardCenterDialog.TYPE_ASSIGN:
                                newValue = value;
                        }
                        dataList.get(i).set(j, newValue);
                    }
                }
                excelView.notifyDataColor(selectRange, Color.RED);
                excelView.notifyDataChange(tableModel);
            }
        });
        //显示的坐标位置不一样
        popWin.showAtLocation(excelView, Gravity.CENTER, 0, 500);
    }
}
