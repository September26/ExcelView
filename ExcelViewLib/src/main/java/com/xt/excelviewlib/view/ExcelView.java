package com.xt.excelviewlib.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.xt.excelviewlib.R;
import com.xt.excelviewlib.util.StringUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Excel表格，
 * N*N的模式
 *
 * @author lxl
 */
public class ExcelView extends View {
    //常量值
    static final String TAG = "ExcelView";
    static final int TYPE_DEFAULT = 0;//默认状态，指的是可以拖动的状态
    static final int TYPE_RANGE = 4;//选中状态，指的是拖动范围选择的状态

    /**
     * 边界值，小于边界值才会触发自动滚动效果
     */
    private int edgeRange = 20;
    /**
     * 像素密度值，因为有的设备并不想按照实际的密度值来计算，所以支持外部定义
     */
    private int dipUnit = 3;

    /**
     * 绘制类
     */
    private final Paint mTextPaint = new Paint();
    private final Paint mOnlyReadPaint = new Paint();
    private final Paint mTextColorPaint = new Paint();
    private final Paint mDividerPaint = new Paint();
    private final Paint mSelectDividerPaint = new Paint();
    private final Paint mSelectBgPaint = new Paint();
    @SuppressLint("UseCompatLoadingForDrawables")
    private final Drawable mRangeIcon = getResources().getDrawable(R.drawable.common_execl_range_icon);
    private final DecimalFormat format = new DecimalFormat("#.####");

    /**
     * 属性值记录
     */
    private TableValueModel mValue = new TableValueModel();
    private AttrValue mAttr = new AttrValue();
    private final ShowValue mShowValue = new ShowValue();

    /**
     * 状态记录
     */
    private final int[] mSelectRange = {-1, -1, -1, -1};
    /**
     * 颜色绘制
     */
    private final Set<String> mColorSet = new HashSet<>();
    private RectF selectRectF = new RectF();
    private final PointF mStartPoint = new PointF();//选中记录
    private int mType = 0;//0默认，1选中，2滑动
    private boolean isInit;
    /**
     * 设置的数据和回调
     */
    public List<List<Double>> mDataList = new ArrayList<>();
    private SelectCallBack callBack;

    private final Handler mHandler = new Handler();

    public ExcelView(Context context) {
        super(context);
        init(null);
    }

    public ExcelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ExcelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ExcelView);
            mAttr.itemWidth = a.getDimension(R.styleable.ExcelView_excel_view_item_width, dipUnit * 70);
            mAttr.itemHeight = a.getDimension(R.styleable.ExcelView_excel_view_item_height, dipUnit * 28);
            mAttr.paddingTop = a.getDimension(R.styleable.ExcelView_excel_view_padding_top, dipUnit * 9);
            mAttr.paddingLeft = a.getDimension(R.styleable.ExcelView_excel_view_padding_left, dipUnit * 15);
            mAttr.dividerWidth = a.getDimension(R.styleable.ExcelView_excel_view_divider_width, 1);
            mAttr.iconWidth = a.getDimension(R.styleable.ExcelView_excel_view_range_icon_width, dipUnit * 12);
            mAttr.textSize = a.getDimension(R.styleable.ExcelView_excel_view_text_size, dipUnit * 11);

            mAttr.onlyReadColor = a.getColor(R.styleable.ExcelView_excel_view_text_color, Color.parseColor("#999999"));
            mAttr.textColor = a.getColor(R.styleable.ExcelView_excel_view_text_color, Color.parseColor("#333333"));
            mAttr.dividerColor = a.getColor(R.styleable.ExcelView_excel_view_divider_color, Color.parseColor("#e8e8e8"));
            mAttr.selectDividerColor = a.getColor(R.styleable.ExcelView_excel_view_select_divider_color, Color.parseColor("#287076"));
            mAttr.selectBgColor = a.getColor(R.styleable.ExcelView_excel_view_select_bg_color, Color.parseColor("#1A2870f6"));
            a.recycle();
        }
        setShowAttr(mAttr);

        Rect rect = new Rect();
        mTextPaint.getTextBounds("123", 0, 2, rect);
        mShowValue.textHeight = rect.height();//计算单个cell中的字体高度
    }

    public void setShowAttr(AttrValue showAttr) {
        mAttr = showAttr;
        Typeface normalTypeface = Typeface.create("sans-serif-medium", Typeface.NORMAL);

        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(showAttr.textColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mAttr.textSize);
        mTextPaint.setTypeface(normalTypeface);

        mOnlyReadPaint.setAntiAlias(true);
        mOnlyReadPaint.setColor(showAttr.onlyReadColor);
        mOnlyReadPaint.setStyle(Paint.Style.FILL);
        mOnlyReadPaint.setTextSize(dipUnit * 8);
        mOnlyReadPaint.setTypeface(normalTypeface);

        mTextColorPaint.setAntiAlias(true);
        mTextColorPaint.setColor(showAttr.textColor);
        mTextColorPaint.setStyle(Paint.Style.FILL);
        mTextColorPaint.setTypeface(normalTypeface);
        mTextColorPaint.setTextSize(mAttr.textSize);

        mSelectBgPaint.setAntiAlias(true);
        mSelectBgPaint.setColor(showAttr.selectBgColor);
        mSelectBgPaint.setStyle(Paint.Style.FILL);

        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setStrokeWidth(mAttr.dividerWidth == 0 ? 1 : mAttr.dividerWidth);
        mDividerPaint.setColor(mAttr.dividerColor == 0 ? Color.parseColor("#e8e8e8") : mAttr.dividerColor);
        mDividerPaint.setAntiAlias(true);

        mSelectDividerPaint.setAntiAlias(true);
        mSelectDividerPaint.setStrokeWidth(mAttr.dividerWidth == 0 ? 1 : mAttr.dividerWidth);
        mSelectDividerPaint.setColor(mAttr.selectDividerColor == 0 ? Color.parseColor("#2870f6") : mAttr.selectDividerColor);
        mSelectDividerPaint.setAntiAlias(true);
    }

    public void setCallBack(SelectCallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * 设置显示的数据
     *
     * @param value 数据内容
     */
    public void notifyDataChange(TableValueModel value) {
        //数据校验
        if (value.rows == 0) {
            value.rows = value.dataList.size();
        }
        if (value.columns == 0) {
            for (List<Double> valueList : value.dataList) {
                value.columns = Math.max(valueList.size(), value.columns);
            }
        }

        this.mValue = value;
        this.mDataList = value.dataList;
        //构建二维数组
        this.mShowValue.hasRowTitle = !StringUtil.emptyOrNull(mValue.rowTitle);
        this.mShowValue.hasColumnTitle = !StringUtil.emptyOrNull(mValue.columnTitle);
        this.mShowValue.rowNum = mValue.rows;
        this.mShowValue.columnNum = mValue.columns;
        mShowValue.initCoord(mAttr);
        isInit = true;
        //todo 这里调用requestLayout，但是draw没有收到，所以要调用invalidate一次
        invalidate();
        mHandler.post(this::requestLayout);

    }

    /**
     * 通知区域刷新
     *
     * @param rowNum     所在行
     * @param columnNum  所在列
     * @param valuesList 要填充的数据
     */
    @SuppressLint("")
    public void notifyDataChangeByRange(int rowNum, int columnNum, List<List<Double>> valuesList) {
        for (int i = 0; i < valuesList.size(); i++) {
            List<Double> doubles = this.mDataList.get(i + rowNum);
            List<Double> values = valuesList.get(i);
            for (int j = 0; j < valuesList.get(i).size(); j++) {
                doubles.set(j + columnNum, values.get(j));
            }
        }
        invalidate();
    }

    public void notifyDataColor(int[] colorRange, int color) {
        mTextColorPaint.setColor(color);
        for (int i = colorRange[1]; i <= colorRange[3]; i++) {
            for (int j = colorRange[0]; j <= colorRange[2]; j++) {
                mColorSet.add(i + "_" + j);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isInit) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            //计算高度
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //计算高度
//        setMeasuredDimension(mShowValue.viewWidth, mShowValue.viewHeight);
        mShowValue.measureWidth = getMeasuredWidth();
        mShowValue.measureHeight = getMeasuredHeight();
        mShowValue.initCoord(mAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mShowValue.viewHeight == 0 || mShowValue.viewWidth == 0) {
            super.onDraw(canvas);
            return;
        }
        drawData(canvas);
    }

    /**
     * 绘制方波图
     */
    protected void drawData(Canvas canvas) {
        //长度不对等或者没有初始化，则不绘制
        if (!isInit) {
            return;
        }
        //清理之前的
        float startX = mShowValue.offsetX;
        float startY = mShowValue.offsetY;
        float itemViewHeight = mShowValue.itemViewHeight;
        float itemViewWidth = mShowValue.itemViewWidth;
        float[] pts = new float[4];
        if (mShowValue.hasRowTitle) {
            pts[0] = startX;
            pts[1] = startY + itemViewHeight;
            pts[2] = mShowValue.viewWidth;
            pts[3] = startY + itemViewHeight;
            canvas.drawLines(pts, mDividerPaint);
            //绘制标题
            canvas.drawText(mValue.rowTitle, startX + mAttr.paddingLeft + itemViewHeight, startY + (itemViewHeight + mShowValue.textHeight) / 2, mTextPaint);
        }
        if (mShowValue.hasColumnTitle) {
            pts[0] = startX + itemViewHeight;
            pts[1] = startY;
            pts[2] = startX + itemViewHeight;
            pts[3] = mShowValue.viewHeight;
            canvas.drawLines(pts, mDividerPaint);
            //绘制标题，一个一个绘制
            String[] split = mValue.columnTitle.split("");
            for (int i = 0; i < split.length; i++) {
                canvas.drawText(split[i], startX + (itemViewHeight - mShowValue.textHeight) / 2, startY + mAttr.paddingTop + itemViewHeight + (mShowValue.textHeight + 15) * i, mTextPaint);
            }
            startX = startX + itemViewHeight;
        }
        if (mShowValue.hasRowTitle) {
            startY += itemViewHeight;
        }

        //绘制普通分割线
        for (int column = 0; column <= this.mShowValue.columnNum; column++) {
            pts[0] = startX;
            pts[1] = startY + column * itemViewHeight;
            pts[2] = mShowValue.viewWidth;
            pts[3] = startY + column * itemViewHeight;
            canvas.drawLines(pts, mDividerPaint);
        }
        for (int row = 0; row <= this.mShowValue.rowNum; row++) {
            pts[0] = startX + row * itemViewWidth;
            pts[1] = startY;
            pts[2] = startX + row * itemViewWidth;
            pts[3] = mShowValue.viewHeight;
            canvas.drawLines(pts, mDividerPaint);
        }

        float computeX;
        float computeY = startY;
        for (int column = 0; column < this.mShowValue.columnNum; column++) {
            computeX = startX;
            List<Double> line = mDataList.get(column);
            for (int row = 0; row < this.mShowValue.rowNum; row++) {
                String value = format.format(line.get(row));
                float textWidth = mTextPaint.measureText(value);
                //判断
                if (column < mValue.onlyReadYNum || row < mValue.onlyReadXNum) {
                    canvas.drawText(value, computeX + (itemViewWidth - textWidth) / 2, computeY + (itemViewHeight + mShowValue.textHeight) / 2, mOnlyReadPaint);
                } else {
                    boolean isColorChange = mColorSet.size() > 0 && mColorSet.contains(column + "_" + row);
                    canvas.drawText(value, computeX + (itemViewWidth - textWidth) / 2, computeY + (itemViewHeight + mShowValue.textHeight) / 2, isColorChange ? mTextColorPaint : mTextPaint);
                }
                computeX += mShowValue.itemViewWidth;
            }
            computeY += mShowValue.itemViewHeight;
        }

        //绘制选中范围
        if (mSelectRange[0] != -1) {
            int startColumn = mSelectRange[0];//第几列，X轴
            int startRow = mSelectRange[1];//第几行，Y轴
            int endColumn = mSelectRange[2];
            int endRow = mSelectRange[3];

            float x1, y1, x2, y2, x3, y3, x4, y4;
            x1 = x3 = startX + startColumn * itemViewWidth;
            y1 = y2 = startY + startRow * itemViewHeight;
            x2 = x4 = startX + (endColumn + 1) * itemViewWidth;
            y3 = y4 = startY + (endRow + 1) * itemViewHeight;

            RectF rect = new RectF(x1, y1, x4, y4);
            selectRectF = rect;
            //绘制区域
            canvas.drawRect(rect, mSelectBgPaint);
            float[] selectPts = new float[16];
            selectPts[0] = x1;
            selectPts[1] = y1;
            selectPts[2] = x2;
            selectPts[3] = y2;
            selectPts[4] = x2;
            selectPts[5] = y2;
            selectPts[6] = x4;
            selectPts[7] = y4;
            selectPts[8] = x4;
            selectPts[9] = y4;
            selectPts[10] = x3;
            selectPts[11] = y3;
            selectPts[12] = x3;
            selectPts[13] = y3;
            selectPts[14] = x1;
            selectPts[15] = y1;
            //绘制边线
            canvas.drawLines(selectPts, mSelectDividerPaint);

            //绘制选择框左上icon
            mRangeIcon.setBounds((int) (x1 - mShowValue.iconRadius), (int) (y1 - mShowValue.iconRadius), (int) (x1 + mShowValue.iconRadius), (int) (y1 + mShowValue.iconRadius));
            mRangeIcon.draw(canvas);

            //绘制选择框右下icon
            mRangeIcon.setBounds((int) (x4 - mShowValue.iconRadius), (int) (y4 - mShowValue.iconRadius), (int) (x4 + mShowValue.iconRadius), (int) (y4 + mShowValue.iconRadius));
            mRangeIcon.draw(canvas);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        //判断坐标变化，超过10则认为滑动
        float x = event.getX();
        float y = event.getY();
        if (action == MotionEvent.ACTION_DOWN) {
            if (selectRectF.top != 0) {
                //选中状态则判断是否范围内，范围是选中的区域+50%的控件
                float w = mShowValue.itemViewWidth / 2;
                float h = mShowValue.itemViewHeight / 2;
                if (y > (selectRectF.top - h) && y < (selectRectF.bottom + h) && x > (selectRectF.left - w) && x < (selectRectF.right + w)) {
                    mType = TYPE_RANGE;
                } else {
                    mType = TYPE_DEFAULT;
                }
            }
            mStartPoint.x = x;
            mStartPoint.y = y;
        } else if (action == MotionEvent.ACTION_UP) {
            if (mType == TYPE_DEFAULT) {
                actionSelect(mStartPoint);
            } else if (mType == TYPE_RANGE) {
                //区域选择的回调
                mHandler.post(() -> callBack.rangSelectCallBackByCoord(selectRectF, mSelectRange));
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            //判断距离移动是否超过10
            double sqrt = Math.sqrt(Math.pow((mStartPoint.x - x), 2.0) + Math.pow((mStartPoint.y - y), 2.0));
            if (sqrt < 10.0) {
                return super.dispatchTouchEvent(event);
            }
            if (mType == TYPE_RANGE) {
                int viewWidth = mShowValue.viewWidth;
                int viewHeight = mShowValue.viewHeight;
                //计算中心点，判断是左上，还是右下方向
                float centerX = (selectRectF.left + selectRectF.right) / 2;
                float centerY = (selectRectF.top + selectRectF.bottom) / 2;

                int startColumn = mSelectRange[0];
                int startRow = mSelectRange[1];
                int endColumn = mSelectRange[2];
                int endRow = mSelectRange[3];
                if (x > centerX && y > centerY) {
                    endColumn = (int) ((x - mShowValue.rowTitleHeight - mShowValue.offsetX) / mShowValue.itemViewWidth);
                    endRow = (int) ((y - mShowValue.columnTitleHeight - mShowValue.offsetY) / mShowValue.itemViewHeight);
                } else if (x < centerX && y < centerY) {
                    startColumn = (int) ((x - mShowValue.rowTitleHeight - mShowValue.offsetX) / mShowValue.itemViewWidth);//
                    startRow = (int) ((y - mShowValue.columnTitleHeight - mShowValue.offsetY) / mShowValue.itemViewHeight);
                }
                if (startColumn < mValue.onlyReadXNum) {
                    startColumn = mValue.onlyReadXNum;
                }
                if (startRow < mValue.onlyReadYNum) {
                    startRow = mValue.onlyReadYNum;
                }
                //todo
                //判断是否到了边缘位置，并且没有结束
                log("x:" + x + ",startColumn:" + startColumn + ",mShowValue.columnNum-1:" + (mShowValue.columnNum - 1));
                if (x > (viewWidth - edgeRange) && endColumn <= (mShowValue.columnNum - 1)) {
                    mShowValue.offsetX -= 10;
                }
                if (x < edgeRange && startColumn > mValue.onlyReadXNum) {
                    log("EdgeRange");
                    mShowValue.offsetX += 10;
                }
                if (y > (viewHeight - edgeRange) && endRow <= (mShowValue.rowNum - 1)) {
                    mShowValue.offsetY -= 10;
                }
                if (y < edgeRange && startRow > mValue.onlyReadYNum) {
                    mShowValue.offsetX += 10;
                }

//                log("after Y:" + startColumn + ",X:" + startRow);
                mSelectRange[0] = startColumn;//X轴
                mSelectRange[1] = startRow;
                mSelectRange[2] = endColumn;//X轴
                mSelectRange[3] = endRow;

                invalidate();
            } else if (mType == TYPE_DEFAULT) {
                mShowValue.offsetX += (x - mStartPoint.x);
                mShowValue.offsetY += (y - mStartPoint.y);
                //限定范围
                if (mShowValue.offsetX > 0) {
                    mShowValue.offsetX = 0;
                } else if (mShowValue.offsetX < (mShowValue.viewWidth - mShowValue.viewComputeWidth)) {
                    mShowValue.offsetX = mShowValue.viewWidth - mShowValue.viewComputeWidth;
                }

                if (mShowValue.offsetY > 0) {
                    mShowValue.offsetY = 0;
                } else if (mShowValue.offsetY < (mShowValue.viewHeight - mShowValue.viewComputeHeight)) {
                    mShowValue.offsetY = mShowValue.viewHeight - mShowValue.viewComputeHeight;
                }
                mStartPoint.x = x;
                mStartPoint.y = y;
                invalidate();
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public void notifyStartXY(int progressX, int progressY) {
        if (progressX < 0 && progressY < 0) {
            return;
        }
        if (progressX >= 0) {
            mShowValue.offsetX = (-mShowValue.viewComputeWidth + mShowValue.viewWidth) * progressX / 100;
        }
        if (progressY >= 0) {
            mShowValue.offsetY = (-mShowValue.viewComputeHeight + mShowValue.viewHeight) * progressY / 100;
        }
        invalidate();
    }

    private void actionSelect(PointF startPoint) {
        //column没有问题，row有问题
        //这里有title和没有title算法是不一样的
        int row = (int) ((startPoint.x - mShowValue.offsetX - ((mShowValue.hasRowTitle) ? mShowValue.itemViewHeight : 0)) / mShowValue.itemViewWidth);
        int column = (int) ((startPoint.y - mShowValue.offsetY) / mShowValue.itemViewHeight) - ((mShowValue.hasRowTitle) ? 1 : 0);
        //判断是否选中不可选中区域
        if ((mShowValue.hasRowTitle && row < mValue.onlyReadXNum || mShowValue.hasColumnTitle && column < mValue.onlyReadYNum)) {
            return;
        }
        //判断是否超过尾部
        if (row > mValue.rows || column > mValue.columns) {
            return;
        }
        //todo 这里判断有问题，要判断尾端
        //左上开始
        mSelectRange[0] = row;//第几列，X轴
        mSelectRange[1] = column;//第几行，Y轴

        //右下结束
        mSelectRange[2] = row;
        mSelectRange[3] = column;
        invalidate();
    }

    public boolean isInit() {
        return isInit;
    }

    public void setDipUnit(int dipUnit) {
        this.dipUnit = dipUnit;
    }

    public void setEdgeRange(int edgeRange) {
        this.edgeRange = edgeRange;
    }


    private void log(String str) {
        Log.i(TAG, str);
    }

    /**
     * 属性类
     */
    private static class AttrValue {
        public float itemWidth = 0;
        public float itemHeight = 0;
        public float iconWidth = 0;
        /**
         * 各种颜色配置
         */
        public int selectBgColor;
        public int textColor;
        public int onlyReadColor;
        public int dividerColor;
        public int selectDividerColor;
        /**
         * 绘制的边框的宽度
         */
        public float dividerWidth;

        public float paddingTop;
        public float paddingLeft;

        public float textSize = 0;
    }

    /**
     * 根据传入model计算得到的数据Bean
     */
    private static class ShowValue {
        /**
         * 多少行
         */
        public int columnNum = 4;
        /**
         * 多少列
         */
        public int rowNum = 4;
        private boolean hasColumnTitle = true;
        private boolean hasRowTitle = true;
        private float columnTitleHeight = 0;
        private float rowTitleHeight = 0;

        private int measureWidth = 0;
        private int measureHeight = 0;
        private int viewWidth = 150;//view的显示宽度
        private int viewHeight = 100;//view的显示高度

        private float viewComputeWidth = 150;//view的计算宽度
        private float viewComputeHeight = 100;//view的计算高度

        private float textHeight = 15;//字体的高度

        private float itemViewWidth;//每个子选项的宽度
        private float itemViewHeight;//每个子选项的高度

        private float offsetX = 0;
        private float offsetY = 0;

        private float iconRadius = 0;

        public void initCoord(AttrValue attrValue) {
            itemViewWidth = attrValue.itemWidth;
            itemViewHeight = attrValue.itemHeight;
            viewComputeWidth = ((hasColumnTitle) ? itemViewHeight : 0F) + itemViewWidth * columnNum;
            viewComputeHeight = ((hasRowTitle) ? itemViewHeight : 0F) + itemViewHeight * rowNum;
            viewWidth = viewComputeWidth > measureWidth ? measureWidth : (int) viewComputeWidth;//最大宽度测量有问题
            viewHeight = viewComputeHeight > measureHeight ? measureHeight : (int) viewComputeHeight;
            iconRadius = attrValue.iconWidth / 2;
            columnTitleHeight = hasColumnTitle ? itemViewHeight : 0;
            rowTitleHeight = hasRowTitle ? itemViewHeight : 0;
        }
    }

    /**
     * 传入数据Bean
     */
    public static class TableValueModel {
        public String rowTitle;//横向的标题
        public String columnTitle;//纵向的标题
        public int onlyReadXNum = 0;//不可编辑的列数
        public int onlyReadYNum = 0;//不可编辑的行数
        public int rows = 0;//要显示多少行
        public int columns = 0;//显示多少列
        public List<List<Double>> dataList = new ArrayList<>();//数据值
    }

    /**
     * 选中区域的回调
     */
    public interface SelectCallBack {
        /**
         * @param selectRectF 坐标范围
         * @param selectRange 选择的cell范围
         */
        void rangSelectCallBackByCoord(RectF selectRectF, int[] selectRange);
    }
}

