package com.xt.excelviewlib.control;

import com.xt.excelviewlib.view.ExcelView;

/**
 * 接口定义
 *
 * @author lxl
 */
public interface ExcelControlInter {

    /**
     * 绑定数据Model
     */
    public void bindData(ExcelView.TableValueModel model);

    /**
     * 现实编辑键盘
     */
    public void showKeyboard(ExcelView.TableValueModel tableModel, int[] selectRange, int selectType);

}

