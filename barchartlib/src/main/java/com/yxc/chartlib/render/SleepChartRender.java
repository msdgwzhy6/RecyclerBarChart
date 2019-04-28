package com.yxc.chartlib.render;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yxc.chartlib.attrs.SleepChartAttrs;
import com.yxc.chartlib.entrys.SleepEntry;
import com.yxc.commonlib.util.DisplayUtil;
import com.yxc.commonlib.util.TimeUtil;

/**
 * @author yxc
 * @since 2019/4/14
 */
final public class SleepChartRender {

    private SleepChartAttrs mChartAttrs;

    private Paint mDeepSleepPaint;
    private Paint mSlumberPaint;
    private Paint mWakePaint;

    private Paint mTextPaint;

    private float contentTextPadding = 0;

    public SleepChartRender(SleepChartAttrs sleepChartAttrs) {
        this.mChartAttrs = sleepChartAttrs;
        contentTextPadding = DisplayUtil.dip2px(2);
        initDeepSleepPaint();
        initSlumberPaint();
        initWakePaint();
        initTextPaint();
    }

    private void initTextPaint() {
        mTextPaint = new Paint();
        mTextPaint.reset();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(DisplayUtil.dip2px(14));
        mTextPaint.setColor(mChartAttrs.txtColor);
    }

    private void initDeepSleepPaint() {
        mDeepSleepPaint = new Paint();
        mDeepSleepPaint.reset();
        mDeepSleepPaint.setAntiAlias(true);
        mDeepSleepPaint.setStyle(Paint.Style.FILL);
        mDeepSleepPaint.setColor(mChartAttrs.deepSleepColor);
    }

    private void initSlumberPaint() {
        mSlumberPaint = new Paint();
        mSlumberPaint.reset();
        mSlumberPaint.setAntiAlias(true);
        mSlumberPaint.setStyle(Paint.Style.FILL);
        mSlumberPaint.setColor(mChartAttrs.slumberColor);
    }

    private void initWakePaint() {
        mWakePaint = new Paint();
        mWakePaint.reset();
        mWakePaint.setAntiAlias(true);
        mWakePaint.setStyle(Paint.Style.FILL);
        mWakePaint.setColor(mChartAttrs.weakColor);
    }

    //绘制柱状图, mYAxis这个坐标会实时变动，所以通过 BarChartItemDecoration 传过来的精确值。
    final public void drawSleepChart(final Canvas canvas, @NonNull final RecyclerView parent) {

        float parentRight = parent.getWidth() - parent.getPaddingRight();
        float parentHeight = parent.getMeasuredHeight() - mChartAttrs.contentPaddingBottom;
        float parentBottom = parent.getBottom() - mChartAttrs.contentPaddingBottom;
        float distanceHeight = parentHeight / 3;

        final int childCount = parent.getChildCount();

        int sumWidth = 0;

        long sumWake = 0;
        long sumDeepSleep = 0;
        long sumSlumber = 0;

        SleepEntry latestSleepEntry = null;
        SleepEntry longestSleepEntry = null;

        float wakeRatio = 0.f;
        float slumberRatio = 0.f;
        float deepSleepRatio = 0.f;

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            SleepEntry sleepEntry = (SleepEntry) child.getTag();

            if (i == 0) {
                latestSleepEntry = sleepEntry;
            } else if (i == childCount - 1) {
                longestSleepEntry = sleepEntry;
            }

            long timeDistance = sleepEntry.endTimestamp - sleepEntry.startTimestamp;
            float end = parentRight - sumWidth;
            float start = end - child.getWidth();
            sumWidth += child.getWidth();

            if (sleepEntry.type == SleepEntry.TYPE_DEEP_SLEEP) {
                float rectFTop = parentBottom - distanceHeight;
                drawRectF(canvas, mDeepSleepPaint, start, rectFTop, end, parentBottom);
                sumDeepSleep += timeDistance;
            } else if (sleepEntry.type == SleepEntry.TYPE_SLUMBER) {
                float rectFTop = parentBottom - 2 * distanceHeight;
                drawRectF(canvas, mSlumberPaint, start, rectFTop, end, parentBottom);
                sumSlumber += timeDistance;
            } else {
                float rectFTop = parentBottom - 3 * distanceHeight;
                drawRectF(canvas, mWakePaint, start, rectFTop, end, parentBottom);
                sumWake += timeDistance;
            }
        }

        float parentLeft = parentRight - sumWidth;

        if (null != latestSleepEntry && null != longestSleepEntry) {
            long timeDistance = latestSleepEntry.endTimestamp - longestSleepEntry.startTimestamp;

            drawRatio(canvas, sumWake, sumSlumber, sumDeepSleep, timeDistance, parent, parentLeft);
        }
    }


    private void drawRatio(Canvas canvas, long sumWake, long sumSlumber, long sumDeepSleep,
                           long timeDistance, RecyclerView parent, float parentLeft) {

        float wakeRatio = (float) (sumWake * 100.0 / timeDistance);
        float slumberRatio = (float) (sumSlumber * 100.0 / timeDistance);
        float deepSleepRatio = (float) (sumDeepSleep * 100.0 / timeDistance);

        float parentRight = parent.getWidth() - parent.getPaddingRight();
        float rectRatioTop = parent.getBottom() - mChartAttrs.contentPaddingBottom + DisplayUtil.dip2px(5);
        float widthDistance = (parentRight - parentLeft) / 3;

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom

        float txtHeight = bottom - top;
        float rectFBottom = rectRatioTop + txtHeight;

        float rectFDescTop = rectFBottom + DisplayUtil.dip2px(2);
        float rectFDescBottom = rectFDescTop + txtHeight;

        float rectFTimeTop = rectFDescBottom + DisplayUtil.dip2px(2);
        float rectFTimeBottom = rectFTimeTop + txtHeight;
        float txtCenter = (top + bottom) / 2;

        float rectFWake = parentLeft + contentTextPadding;
        drawRatioText(canvas, wakeRatio, rectFWake,
                rectRatioTop, rectFBottom, txtCenter, mWakePaint);
        drawDescTxt(canvas, "醒着", rectFWake, rectFDescTop, rectFDescBottom, txtCenter, mChartAttrs.weakColor);
        drawTimeStr(canvas, sumWake, rectFWake, rectFTimeTop, rectFTimeBottom, txtCenter);

        float rectFSlumber = parentLeft + widthDistance + contentTextPadding;
        drawRatioText(canvas, slumberRatio, rectFSlumber,
                rectRatioTop, rectFBottom, txtCenter, mSlumberPaint);
        drawDescTxt(canvas, "浅睡眠", rectFSlumber,
                rectFDescTop, rectFDescBottom, txtCenter, mChartAttrs.slumberColor);
        drawTimeStr(canvas, sumSlumber, rectFSlumber, rectFTimeTop, rectFTimeBottom, txtCenter);

        float rectFDeepSleepLeft = parentLeft + 2 * widthDistance + contentTextPadding;
        drawRatioText(canvas, deepSleepRatio, rectFDeepSleepLeft,
                rectRatioTop, rectFBottom, txtCenter, mDeepSleepPaint);
        drawDescTxt(canvas, "深睡眠", rectFDeepSleepLeft,
                rectFDescTop, rectFDescBottom, txtCenter, mChartAttrs.deepSleepColor);
        drawTimeStr(canvas, sumDeepSleep, rectFDeepSleepLeft, rectFTimeTop, rectFTimeBottom, txtCenter);
    }

    private void drawTimeStr(Canvas canvas, long sumWake, float parentLeft, float rectFTop, float rectTimeBottom, float txtCenter) {
        String timeStr = getTimeStr(sumWake);
        float rectFLeft = parentLeft + contentTextPadding;
        float txtTimeWidth = mTextPaint.measureText(timeStr);
        float rectFRight = rectFLeft + txtTimeWidth + 2 * contentTextPadding;

        RectF timeRectF = new RectF(rectFLeft, rectFTop, rectFRight, rectTimeBottom);
        int timeBaseLineY = (int) (timeRectF.centerY() - txtCenter);//基线中间点的y轴计算公式

        int color = mTextPaint.getColor();
        mTextPaint.setColor(Color.WHITE);
        canvas.drawText(timeStr, parentLeft + contentTextPadding, timeBaseLineY, mTextPaint);
        mTextPaint.setColor(color);
    }

    private String getTimeStr(long sumTime) {
        int hour = (int) (sumTime / TimeUtil.TIME_HOUR);
        int min = (int) (sumTime % TimeUtil.TIME_HOUR / 60);
        String hourStr = hour > 0 ? hour+ "h" + ":" : "";
        String minStr = min + "min";
        return hourStr + minStr;
    }

    private void drawDescTxt(Canvas canvas, String descStr, float parentLeft, float rectFTop, float rectDescBottom, float txtCenter, int txtColor) {
        float rectFLeft = parentLeft + contentTextPadding;
        float txtDescWidth = mTextPaint.measureText(descStr);
        float rectFRight = rectFLeft + txtDescWidth + 2 * contentTextPadding;

        RectF descRectF = new RectF(rectFLeft, rectFTop, rectFRight, rectDescBottom);
        int descBaseLineY = (int) (descRectF.centerY() - txtCenter);//基线中间点的y轴计算公式

        int color = mTextPaint.getColor();
        mTextPaint.setColor(txtColor);
        canvas.drawText(descStr, parentLeft + contentTextPadding, descBaseLineY, mTextPaint);
        mTextPaint.setColor(color);
    }


    private void drawRatioText(Canvas canvas, float ratio,
                               float rectFLeft, float rectFTop,
                               float rectFBottom, float txtCenter,
                               Paint mSlumberPaint) {
        String ratioStr = ratio + "%";
        float ratioTxtWidth = mTextPaint.measureText(ratioStr);
        //绘文字
        RectF ratioRectF = new RectF(rectFLeft, rectFTop, rectFLeft + ratioTxtWidth + 2 * contentTextPadding, rectFBottom);
        int ratioBaseLineY = (int) (ratioRectF.centerY() - txtCenter);//基线中间点的y轴计算公式
        canvas.drawRoundRect(ratioRectF, DisplayUtil.dip2px(2), DisplayUtil.dip2px(2), mSlumberPaint);
        canvas.drawText(ratioStr, ratioRectF.left + contentTextPadding, ratioBaseLineY, mTextPaint);
    }

    private void drawRectF(Canvas canvas, Paint paint, float start, float top, float right, float bottom) {
        RectF rectF = new RectF();
        rectF.set(start, top, right, bottom);
        canvas.drawRect(rectF, paint);
    }


}
