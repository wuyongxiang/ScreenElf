package com.xiangzi.screenelf;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2018/2/5.
 */

public class LineView  extends View {

    PointF controlPoint,leftPoint,rightPoint;
    Paint paintArc = new Paint();
    Paint paintLine = new Paint();

    public LineView(Context context ) {
        super(context);
        controlPoint = new PointF(getMeasuredWidth()*0.5f,0);
        leftPoint =  new PointF(getMeasuredWidth()*0.1f, 0);
        rightPoint =  new PointF((getMeasuredWidth()*0.9f), 0);
        paintArc.setColor(getResources().getColor(R.color.colorAccent));
        paintArc.setStyle(Paint.Style.STROKE);
        paintArc.setStrokeWidth(20);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(20);
    }


    public void setControlPoint(float x ,float y ){
        this.controlPoint.x = x*2;
        this.controlPoint.y = y*2;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        Log.e("elf","onDraw"+getMeasuredWidth());
        leftPoint.x =  getMeasuredWidth()*0.3f;
        rightPoint.x =  getMeasuredWidth()*0.7f;
        if(controlPoint.x==0||controlPoint.y<30){
            controlPoint.y = 30;
            controlPoint.x = getMeasuredWidth()*0.5f;
        }
        canvas.drawCircle(leftPoint.x,leftPoint.y+30,20,paintArc);
        canvas.drawCircle(rightPoint.x,rightPoint.y+30,20,paintArc);
        Path linePath = new Path();
        linePath.moveTo(leftPoint.x,leftPoint.y+30);
        linePath.quadTo(controlPoint.x,controlPoint.y+30,rightPoint.x,rightPoint.y+30);
        canvas.drawPath(linePath,paintLine);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }
}
