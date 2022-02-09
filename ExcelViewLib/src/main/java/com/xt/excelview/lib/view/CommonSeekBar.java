package com.xt.excelview.lib.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.xt.excelview.lib.R;
import com.xt.excelview.lib.util.DeviceUtil;
import com.xt.excelview.lib.util.ImageUtil;

/**
 * 滑动条
 *
 * @author lxl
 */
@SuppressLint("AppCompatCustomView")
public class CommonSeekBar extends SeekBar {

    public CommonSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        //load styled attributes.
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CommonSeekBar,
                defStyleAttr, 0);

        float height = attributes.getDimension(R.styleable.CommonSeekBar_seekbar_thumb_height, 24);
        float width = attributes.getDimension(R.styleable.CommonSeekBar_seekbar_thumb_width, 24);
        Drawable thumb = getThumb();
        int displayDensity = DeviceUtil.getDisplayDensity((Activity) context);
        thumb = ImageUtil.zoomDrawable(thumb, (int) height, (int) width, displayDensity);
        attributes.recycle();
        setThumb(thumb);
    }

}
