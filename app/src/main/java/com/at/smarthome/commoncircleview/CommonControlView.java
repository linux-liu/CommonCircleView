package com.at.smarthome.commoncircleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import java.util.ArrayList;



/**
 * 圆形控制的View 电视，功放，机顶盒等
 * 刘信
 */
public class CommonControlView extends View {
    private Paint mPaint;
    private int miconNum;
    private static final int DEFAULT_NUM = 2;
    private Bitmap[] mDrawables;
    private Bitmap mCenterDrawables;
    private String centerText;
    private ArrayList<Region> regions;
    private Path centPath;
    private Region center;
    private float raidus;
    private float innerRaidus;
    private int startAngel = 225;
    private int sweepAngel = 90;
    private ArrayList<Path> mPaths;
    private BlurMaskFilter blurMaskFilter;
    private float padding;
    private int currentSelect = -1;//0上 1右 2下 3左 4中
    private boolean isCenterCanClick = true;

    public CommonControlView(Context context) {
        super(context);
        init(null, 0);
    }

    public CommonControlView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CommonControlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CommonControlView);

        miconNum = typedArray.getInt(R.styleable.CommonControlView_icon_num, DEFAULT_NUM);
        mDrawables = new Bitmap[miconNum];
        //最多支持四个
        for (int i = 0; i < miconNum; i++) {
            switch (i) {
                case 0:
                    mDrawables[i] = Utils.drawableToBitmap(typedArray.getDrawable(R.styleable.CommonControlView_first_icon), 0);
                    break;
                case 1:
                    if (miconNum == 2) {
                        mDrawables[i] = Utils.drawableToBitmap(typedArray.getDrawable(R.styleable.CommonControlView_second_icon), 180);
                    } else if (miconNum == 3) {
                        mDrawables[i] = Utils.drawableToBitmap(typedArray.getDrawable(R.styleable.CommonControlView_second_icon), -120);
                    } else if (miconNum == 4) {
                        mDrawables[i] = Utils.drawableToBitmap(typedArray.getDrawable(R.styleable.CommonControlView_second_icon), -90);
                    }

                    break;
                case 2:
                    if (miconNum == 3) {
                        mDrawables[i] = Utils.drawableToBitmap(typedArray.getDrawable(R.styleable.CommonControlView_third_icon), -240);
                    } else if (miconNum == 4) {
                        mDrawables[i] = Utils.drawableToBitmap(typedArray.getDrawable(R.styleable.CommonControlView_third_icon), 180);
                    }

                    break;
                case 3:
                    mDrawables[i] = Utils.drawableToBitmap(typedArray.getDrawable(R.styleable.CommonControlView_four_icon), 90);
                    break;

            }
        }
        isCenterCanClick = typedArray.getBoolean(R.styleable.CommonControlView_center_is_can_click, true);
        centerText = typedArray.getString(R.styleable.CommonControlView_center_text);
        mCenterDrawables = Utils.drawableToBitmap(typedArray.getDrawable(R.styleable.CommonControlView_center_icon), 0);
        raidus = typedArray.getDimensionPixelSize(R.styleable.CommonControlView_control_radius, Utils.dip2px(getContext(), 80));
        typedArray.recycle();

        innerRaidus = raidus * 229.5f / 510;
        padding = raidus * 30 / 510;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.line_grey));
        regions = new ArrayList<>(miconNum);
        mPaths = new ArrayList<>(miconNum);
        centPath = new Path();
        center = new Region();
        initRegin();
        setLayerType(View.LAYER_TYPE_SOFTWARE, mPaint);
        blurMaskFilter = new BlurMaskFilter(25, BlurMaskFilter.Blur.SOLID);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) raidus * 2, (int) raidus * 2);

    }

    private void initRegin() {
        centPath.addCircle(raidus, raidus, innerRaidus, Path.Direction.CW);
        center.setPath(centPath, new Region((int) (raidus - innerRaidus), (int) (raidus - innerRaidus), (int) (raidus + innerRaidus), (int) (raidus + innerRaidus)));
        if (miconNum == 3) {
            startAngel = 210;
            sweepAngel = 120;
        }
        for (int i = 0; i < miconNum; i++) {
            RectF rect = new RectF(padding, padding, raidus + raidus - padding, raidus + raidus - padding);

            RectF rect1 = new RectF(raidus - innerRaidus, raidus - innerRaidus, raidus + innerRaidus, raidus + innerRaidus);
            Path path = new Path();
            path.moveTo(raidus, raidus);
            path.arcTo(rect, startAngel, sweepAngel);

            Path path1 = new Path();
            path1.moveTo(raidus, raidus);
            path1.arcTo(rect1, startAngel, sweepAngel);

            if (miconNum == 2) {
                startAngel += 2 * sweepAngel;
            } else {
                startAngel += sweepAngel;
            }
            RectF rectF = new RectF();

            RectF rectF1 = new RectF();

            path.computeBounds(rectF, true);
            path.close();

            path1.computeBounds(rectF1, true);
            path1.close();

            Path pathReal = new Path();

            pathReal.op(path, path1, Path.Op.DIFFERENCE);

            mPaths.add(pathReal);

            Region region = new Region();

            region.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));

            Region region1 = new Region();
            region1.setPath(path1, new Region((int) rectF1.left, (int) rectF1.top, (int) rectF1.right, (int) rectF1.bottom));

            Region region2 = new Region();
            region2.op(region, region1, Region.Op.DIFFERENCE);
            regions.add(region2);
        }

    }

    private boolean isDown = false;
    private float lastX, lastY;


    /**
     * 改变中间的bitmap
     *
     * @param resId
     */
    public void changeCenterBitmapResource(@DrawableRes int resId) {
        if (mCenterDrawables != null && mCenterDrawables.isRecycled()) {
            mCenterDrawables.recycle();
            mCenterDrawables = null;
        }
        mCenterDrawables = Utils.drawableToBitmap(getContext().getResources().getDrawable(resId), 0);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //按下
                    lastX = event.getX();
                    lastY = event.getY();

                    if (isCenterTop((int) event.getX(), (int) event.getY()) && isCenterCanClick) {


                        isDown = true;
                        currentSelect = 4;
                        invalidate();
                    } else if (isDownTop((int) event.getX(), (int) event.getY())) {

                        isDown = true;
                        currentSelect = 0;
                        invalidate();
                    } else if (isDownLeft((int) event.getX(), (int) event.getY())) {
                        isDown = true;

                        currentSelect = 3;

                        invalidate();
                    } else if (isDownRight((int) event.getX(), (int) event.getY())) {

                        isDown = true;
                        currentSelect = 1;
                        invalidate();

                    } else if (isDownBottom((int) event.getX(), (int) event.getY())) {
                        isDown = true;
                        currentSelect = 2;
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    //弹起


                    if (isCenterCanClick && isCenterTop((int) event.getX(), (int) event.getY()) && currentSelect == 4 && isClick(lastX, lastY, event.getX(), event.getY())) {
                        if (callBack != null) {
                            callBack.centerClick();
                        }
                    } else if (isDownTop((int) event.getX(), (int) event.getY()) && currentSelect == 0 && isClick(lastX, lastY, event.getX(), event.getY())) {

                        if (callBack != null) {
                            callBack.oneClick();
                        }
                    } else if (isDownLeft((int) event.getX(), (int) event.getY()) && currentSelect == 3 && isClick(lastX, lastY, event.getX(), event.getY())) {

                        if (callBack != null) {
                            callBack.fourClick();
                        }
                    } else if (isDownRight((int) event.getX(), (int) event.getY()) && currentSelect == 1 && isClick(lastX, lastY, event.getX(), event.getY())) {

                        if (callBack != null) {
                            callBack.twoClick();
                        }
                    } else if (isDownBottom((int) event.getX(), (int) event.getY()) && currentSelect == 2 && isClick(lastX, lastY, event.getX(), event.getY())) {

                        if (callBack != null) {
                            callBack.threeClick();
                        }
                    }
                    isDown = false;
                    currentSelect = -1;
                    invalidate();
                    break;
                default:
                    break;
            }

            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    private boolean isClick(float lastX, float lastY, float thisX,
                            float thisY) {
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        if (offsetX <= touchSlop && offsetY <= touchSlop) {
            return true;
        }
        return false;
    }

    private boolean isCenterTop(int x, int y) {
        if (center != null && center.contains(x, y)) {
            return true;
        } else {
            return false;
        }

    }

    private boolean isDownTop(int x, int y) {
        if (regions.size() > 0 && regions.get(0).contains(x, y)) {
            return true;
        } else {
            return false;
        }

    }

    private boolean isDownBottom(int x, int y) {
        if (regions.size() > 2 && regions.get(2).contains(x, y)) {
            return true;
        } else {
            return false;
        }
    }


    private boolean isDownLeft(int x, int y) {
        if (regions.size() > 3 && regions.get(3).contains(x, y)) {
            return true;
        } else {
            return false;
        }

    }

    private boolean isDownRight(int x, int y) {

        if (regions.size() > 1 && regions.get(1).contains(x, y)) {
            return true;
        } else {
            return false;
        }
    }


    private FourStateClickCallBack callBack;


    public void setFourStateClickCallBack(FourStateClickCallBack callBack) {
        this.callBack = callBack;
    }

    public interface FourStateClickCallBack {


        void centerClick();

        void oneClick();

        void twoClick();

        void threeClick();

        void fourClick();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制圆有阴影效果
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setMaskFilter(blurMaskFilter);
        mPaint.setColor(getResources().getColor(R.color.color_black10));
        canvas.drawCircle(raidus, raidus, raidus - padding, mPaint);
        mPaint.setMaskFilter(null);
        mPaint.setColor(getResources().getColor(R.color.white));
        canvas.drawCircle(raidus, raidus, raidus - padding, mPaint);


        mPaint.setColor(getResources().getColor(R.color.line_grey));
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(raidus, raidus, innerRaidus, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(0);
        mPaint.setColor(getResources().getColor(R.color.line_grey));

        if (isDown) {
            if (currentSelect != -1 && currentSelect != 4 && currentSelect < mPaths.size()) {
                drawRegion(canvas, mPaths.get(currentSelect), mPaint);
            } else if (currentSelect == 4) {
                drawRegion(canvas, centPath, mPaint);
            }

        }

        if (mCenterDrawables != null) {
            canvas.drawBitmap(mCenterDrawables, raidus - mCenterDrawables.getWidth() / 2.0f, raidus - mCenterDrawables.getHeight() / 2.0f, null);
        } else {
            if (!TextUtils.isEmpty(centerText)) {
                mPaint.setColor(getResources().getColor(R.color.color666666));
                mPaint.setTextSize(Utils.dip2px(getContext(), 14));
                float textWidth = mPaint.measureText(centerText);
                Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
                float y = raidus + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2;
                canvas.drawText(centerText, raidus - textWidth / 2, y, mPaint);
            }
        }

        canvas.save();

        for (Bitmap mDrawable : mDrawables) {
            canvas.drawBitmap(mDrawable, raidus - (mDrawable.getWidth() / 2.0f), (raidus - innerRaidus - mDrawable.getHeight() + padding) / 2, null);
            if (miconNum == 2) {
                canvas.rotate(180, raidus, raidus);
            } else if (miconNum == 3) {
                canvas.rotate(120, raidus, raidus);
            } else if (miconNum == 4) {
                canvas.rotate(90, raidus, raidus);
            }

        }
        canvas.restore();

    }

    private void drawRegion(Canvas canvas, Path rgn, Paint paint) {
        //用Region方法来绘制图会有很明显的锯齿问题
        canvas.drawPath(rgn, paint);

    }
}
