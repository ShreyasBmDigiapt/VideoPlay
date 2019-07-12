package com.example.videoplay;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

public class AutoFitTextureView extends TextureView {

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    public AutoFitTextureView(Context context) {
        super(context);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void setUpaspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw  new IllegalArgumentException("size cannot be negative");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (mRatioHeight == 0 || mRatioWidth == 0) {
            setMeasuredDimension(width, height);
        }else if (width > height * mRatioWidth / mRatioHeight) {
            setMeasuredDimension(width, mRatioHeight * width / mRatioWidth);
        }else {
            setMeasuredDimension( mRatioWidth * height / mRatioHeight, height );
        }
    }
}
