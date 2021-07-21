package com.lege.legecommonview.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.lege.legecommonview.R;


public class RoundRectLayout extends RelativeLayout {
    private Path mPath;
    private int mRadius;

    private int mWidth;
    private int mHeight;
    private int mLastRadius;

    public static final int MODE_NONE = 0;
    public static final int MODE_ALL = 1;
    public static final int MODE_LEFT = 2;
    public static final int MODE_TOP = 3;
    public static final int MODE_RIGHT = 4;
    public static final int MODE_BOTTOM = 5;

    private int mRoundMode = MODE_ALL;

    public RoundRectLayout(Context context) {
        super(context);

        init(context,null);
    }

    public RoundRectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){

//        setBackgroundDrawable(new ColorDrawable(0x1F2532));

        mPath = new Path();
        mPath.setFillType(Path.FillType.EVEN_ODD);
        if(attrs!=null){
            TypedArray ta = context.obtainStyledAttributes(attrs , R.styleable.RoundRectLayout);
            int corner = ta.getDimensionPixelSize(R.styleable.RoundRectLayout_layout_corners,30);
            mRoundMode= ta.getInt(R.styleable.RoundRectLayout_round_mode,MODE_ALL);
            ta.recycle();
            setCornerRadius(corner);
        }else{
            setCornerRadius(30);
        }


    }

    /**
     * 设置是否圆角裁边
     * @param roundMode
     */
    public void setRoundMode(int roundMode){
        mRoundMode = roundMode;
    }

    /**
     * 设置圆角半径
     * @param radius
     */
    public void setCornerRadius(int radius){
        mRadius = radius;
    }

    private void checkPathChanged(){
        if(getMeasuredWidth() == mWidth && getMeasuredHeight() == mHeight && mLastRadius == mRadius){
            return;
        }
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mLastRadius = mRadius;

        mPath.reset();

        switch (mRoundMode){
            case MODE_ALL:
                mPath.addRoundRect(new RectF(0, 0, mWidth, mHeight), mRadius, mRadius, Path.Direction.CW);
                break;
            case MODE_LEFT:
                mPath.addRoundRect(new RectF(0, 0, mWidth, mHeight),
                        new float[]{mRadius, mRadius, 0, 0, 0, 0, mRadius, mRadius},
                        Path.Direction.CW);
                break;
            case MODE_TOP:
                mPath.addRoundRect(new RectF(0, 0, mWidth, mHeight),
                        new float[]{mRadius, mRadius, mRadius, mRadius, 0, 0, 0, 0},
                        Path.Direction.CW);
                break;
            case MODE_RIGHT:
                mPath.addRoundRect(new RectF(0, 0, mWidth, mHeight),
                        new float[]{0, 0, mRadius, mRadius, mRadius, mRadius, 0, 0},
                        Path.Direction.CW);
                break;
            case MODE_BOTTOM:
                mPath.addRoundRect(new RectF(0, 0, mWidth, mHeight),
                        new float[]{0, 0, 0, 0, mRadius, mRadius, mRadius, mRadius},
                        Path.Direction.CW);
                break;
        }

    }

    @Override
    public void draw(Canvas canvas) {

        if(mRoundMode != MODE_NONE){
            int saveCount = canvas.save();
            checkPathChanged();

            canvas.clipPath(mPath);
            super.draw(canvas);

            canvas.restoreToCount(saveCount);
        }else {
            super.draw(canvas);
        }
    }

}
