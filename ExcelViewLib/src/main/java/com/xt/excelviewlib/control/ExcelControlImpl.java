package com.xt.excelviewlib.control;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.xt.excelviewlib.dialog.ExcelEditDialog;
import com.xt.excelviewlib.dialog.KeyBoardCenterDialog;
import com.xt.excelviewlib.dialog.KeyBoardDialog;
import com.xt.excelviewlib.util.DeviceUtil;
import com.xt.excelviewlib.view.ExcelView;

import java.util.ArrayList;
import java.util.List;

/**
 * 维护关系ExcelView和Model的关系
 *
 * @author lxl
 */
public class ExcelControlImpl implements ExcelControlInter {

    protected ExcelView excelView;
    protected SeekBar barX;
    protected SeekBar barY;
    protected View.OnClickListener listener;
    protected int dipUnit = 3;
    protected Context context;
    protected int[] mSelectRange;

    public ExcelControlImpl(ExcelView excelView, View.OnClickListener listener) {
        this(excelView, null, null, listener);
    }

    public ExcelControlImpl(ExcelView excelView, SeekBar barX, SeekBar barY, View.OnClickListener listener) {
        this.excelView = excelView;
        this.barX = barX;
        this.barY = barY;
        this.listener = listener;
        this.context = excelView.getContext();
        if (barY != null) {
            barY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    excelView.notifyStartXY(-1, progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
        if (barX != null) {
            barX.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    excelView.notifyStartXY(progress, -1);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }


    /**
     * 通过bind使页面和业务逻辑脱钩
     * 通过重写，实现不一样的业务逻辑
     */
    public void bindData(ExcelView.TableValueModel model) {
        excelView.setCallBack(new ExcelView.SelectCallBack() {

            @Override
            public void rangSelectCallBackByCoord(RectF selectRectF, int[] selectRange) {
                int popWidth = dipUnit * 222;
                int popHeight = dipUnit * 24;
                ExcelEditDialog popWin = new ExcelEditDialog(context, popWidth, popHeight);
                ExcelEditDialog.DialogModel dialogModel = new ExcelEditDialog.DialogModel();
                ArrayList<String> list = new ArrayList<>();
                list.add("复制");
                list.add("粘贴");
                list.add("加");
                list.add("减");
                list.add("乘");
                list.add("除");
                list.add("编辑");
                dialogModel.setDataList(list);
                popWin.bindData(dialogModel, new ExcelEditDialog.ClickCallBack() {
                    @Override
                    public void clickByIndex(int index) {
                        popWin.dismiss();
                        if (index == 0) {
                            mSelectRange = selectRange.clone();
                        } else if (index == 1) {
                            //粘贴
                            List arrayList = new ArrayList<List<Double>>();
                            int startColumn = mSelectRange[0]; //第几列，X轴
                            int startRow = mSelectRange[1]; //第几行，Y轴
                            int endColumn = mSelectRange[2];
                            int endRow = mSelectRange[3];
                            for (int row = startRow; row < endRow; row++) {
                                List<Double> line = model.dataList.get(row);
                                ArrayList<Double> doubles = new ArrayList<>();
                                doubles.addAll(line.subList(startColumn, endColumn + 1));
                            }
                            excelView.notifyDataChangeByRange(selectRange[1], selectRange[0], arrayList);
                            mSelectRange = null;
                        } else {
                            //唤起键盘
                            showKeyboard(model, selectRange, index - 1);
                        }
                    }
                });
                popWin.showAtLocation(excelView, Gravity.TOP | Gravity.LEFT, (int) ((selectRectF.left + selectRectF.right - popWidth) / 2), (int) (selectRectF.top + 10 - popHeight));
            }
        });
        excelView.notifyDataChange(model);
    }

    /**
     * 通过重写，实现不一样的业务逻辑
     */
    public void showKeyboard(ExcelView.TableValueModel tableModel, int[] selectRange, int selectType) {
        List<List<Double>> dataList = tableModel.dataList;
        KeyBoardDialog popWin = new KeyBoardDialog(context, DeviceUtil.getScreenSize(context)[0], dipUnit * 137);
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
                    for (int j = startColumn; j <= endColumn; j++) {
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
