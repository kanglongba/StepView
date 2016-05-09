package com.bupt.edison.mystepview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edison on 16/2/1.
 */
public class ExpendStepView extends View {

    private int stepMaxFlag; //最大的步骤
    private int stepCurrFlag; //当前的步骤
    private Drawable stepSelectedBackground; //当前步骤(选中)的背景图片
    private Drawable stepNormalBackground; //正常状态的背景图片
    private Drawable stepCompleteBackground; //完成状态的背景图片
    private int stepTextNormalColor; //说明文字(包含标题和注释)的正常颜色
    private int stepTextTransferColor; //标题文字的变化颜色
    private String stepTitleArray; //标题的文字
    private String[] titles;
    private List<SingleStepView> stepList;
    private Context context;


    public ExpendStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpendStepView);
        initData(typedArray);
    }

    public ExpendStepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpendStepView);
        initData(typedArray);
    }

    void initData(TypedArray typedArray) {
        stepMaxFlag = typedArray.getInt(R.styleable.ExpendStepView_StepMaxFlag, 1);
        stepCurrFlag = typedArray.getInt(R.styleable.ExpendStepView_StepCurrFlag, 0);
        stepSelectedBackground = typedArray.getDrawable(R.styleable.ExpendStepView_StepSelectedBackground);
        stepNormalBackground = typedArray.getDrawable(R.styleable.ExpendStepView_StepNormalBackground);
        stepCompleteBackground = typedArray.getDrawable(R.styleable.ExpendStepView_StepCompletedBackground);
        stepTextNormalColor = typedArray.getColor(R.styleable.ExpendStepView_StepTextNormalColor, context.getResources().getColor(R.color.colorPrimary));
        stepTextTransferColor = typedArray.getColor(R.styleable.ExpendStepView_StepTextTransferColor, context.getResources().getColor(R.color.colorPrimaryDark));
        stepTitleArray = typedArray.getString(R.styleable.ExpendStepView_StepTitleArray);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width, height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = getMeasuredWidth();
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = dip2px(context, 80); //80dp
        }
        setMeasuredDimension(width, height);

        stepList = new ArrayList<SingleStepView>();
        titles = stepTitleArray.split("\\|");
        for (int i = 0; i < stepMaxFlag; i++) {
            SingleStepView stepView = new SingleStepView(i, stepMaxFlag, width, height, titles[i], context);
            stepList.add(stepView);
        }
        initStatus();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (SingleStepView stepView : stepList) {
            stepView.draw(canvas);
        }
    }

    /**
     * 获取指定位置的SingleStepView
     *
     * @param index
     * @return
     */
    public SingleStepView getStepViewAtPosition(int index) {
        return stepList.get(index);
    }

    /**
     * 获取当前位置的SingleStepView
     *
     * @return
     */
    public SingleStepView getCurrentStepView() {
        return getStepViewAtPosition(getCurrentStep());
    }

    /**
     * 获得一共有多少步
     *
     * @return
     */
    public int getTotalStep() {
        return this.stepMaxFlag;
    }

    private void initStatus(){
        if(getCurrentStep()<getTotalStep() && getCurrentStep() >= 0){
            for (int i = 0; i < stepMaxFlag; i++) {
                if (i < stepCurrFlag) {
                    stepList.get(i).setStepStatus(-1);
                } else if (i == stepCurrFlag) {
                    stepList.get(i).setStepStatus(0);
                } else {
                    stepList.get(i).setStepStatus(1);
                }
            }
        }
    }

    /**
     * 设置当前进度
     *
     * @param currentStep
     */
    public void setCurrentStep(int currentStep) {
        if (currentStep < stepMaxFlag && currentStep >= 0) {
            this.stepCurrFlag = currentStep;
            for (int i = 0; i < stepMaxFlag; i++) {
                if (i < stepCurrFlag) {
                    stepList.get(i).setStepStatus(-1);
                } else if (i == stepCurrFlag) {
                    stepList.get(i).setStepStatus(0);
                } else {
                    stepList.get(i).setStepStatus(1);
                }
            }
            invalidate();
        }

    }

    /**
     * 返回当前的当前是第几步
     *
     * @return
     */
    public int getCurrentStep() {
        return this.stepCurrFlag;
    }

    /**
     * 启动进程
     *
     * @param annotation, 注释文字,通常为启动时间
     */
    public void startProgress(String annotation) {
        if (!TextUtils.isEmpty(annotation)) {
            getStepViewAtPosition(0).setAnnotation(annotation);
        }
        setCurrentStep(0);
    }

    /**
     * 启动进程
     */
    public void startProgress() {
        startProgress(null);
    }

    /**
     * 开始下一步
     */
    public void nextStep() {
        nextStep(null);
    }

    /**
     * 开始下一步
     *
     * @param annotation,注释文字,通常为时间
     */
    public void nextStep(String annotation) {
        int step = getCurrentStep();
        if (step == stepMaxFlag - 1) {
            return;
        }
        if (!TextUtils.isEmpty(annotation)) {
            stepList.get(step + 1).setAnnotation(annotation);
        }
        setCurrentStep(step + 1);
    }

    /**
     * 把这个流程标记为成功状态
     */
    public void setProgressCompeleted() {
        setCurrentStep(stepMaxFlag - 1);
    }

    /**
     * 把这个流程标记为失败状态
     */
    public void setProgressFailture() {
        stepCurrFlag = 0;
        for (int i = 0; i < stepMaxFlag; i++) {
            stepList.get(i).setStepStatus(1);
            stepList.get(i).clearAnnotation();
        }
        invalidate();
    }

    /**
     * 重置流程.重置以后,ExpendStepView会回复初始状态,此时如要开始进程,必须首先
     * 调用startProgress(String annotation),或startProgress()
     */
    public void restore() {
        setProgressFailture();
    }

    /**
     * 为步骤添加注释
     *
     * @param step 第几步
     * @param time 注释,通常为经过格式化的可读时间.
     */
    public void setStepTime(int step, String time) {
        if (step >= 0 && step < stepMaxFlag) {
            stepList.get(step).setAnnotation(time);
            invalidate();
        }
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param fontScale （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @param scale    （DisplayMetrics类中属性density）
     * @return
     */
    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 内部类,表示一个步骤
     */
    public class SingleStepView {

        int index; //索引
        int totalSteps; //一共有几步
        float centerX; //指示点的圆心
        float centerY; //指示点的圆心
        float parentWidth; //父View的宽度
        float parentHeight; //父View的高度
        float positionTitleX; //title的位置
        float positionTitleY; //title的位置
        float positionTimeX; //注释的位置
        float positionTimeY; //注释的位置
        String title;
        String time;
        Paint circlePaint;
        Paint linePaint;
        Paint titlePaint;
        Paint timePaint;
        Context context;
        private float normalRadius; //圆的半径
        private float selectedRadius; //选中时,圆的半径
        Rect rect; //绘图时的矩阵
        int stepStatus = 1; //-1,代表完成;0,代表选中;1,代表正常;


        public SingleStepView(int index, int totalSteps, int parentWidth, int parentHeight, String title, Context context) {
            this.index = index;
            this.totalSteps = totalSteps;
            this.parentWidth = parentWidth;
            this.parentHeight = parentHeight;
            this.context = context;
            this.title = title;

            initPaint();
            caculatePostion();
        }

        private void initPaint() {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float strokewWidth = displayMetrics.density;

            circlePaint = new Paint();
            circlePaint.setAntiAlias(true);
            circlePaint.setStrokeWidth(1);
            circlePaint.setStyle(Paint.Style.FILL);

            linePaint = new Paint();
            linePaint.setAntiAlias(true);
            linePaint.setStrokeWidth(1 * strokewWidth);
            linePaint.setStyle(Paint.Style.FILL);

            titlePaint = new Paint();
            titlePaint.setAntiAlias(true);
            titlePaint.setStrokeWidth(1 * strokewWidth / 3);
            titlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            titlePaint.setTextSize(sp2px(context, 12)); //字体大小要做转换 sp——>px


            timePaint = new Paint();
            timePaint.setAntiAlias(true);
            timePaint.setStrokeWidth(1 * strokewWidth / 3);
            timePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            timePaint.setColor(stepTextNormalColor);
            timePaint.setTextSize(sp2px(context, 12)); //字体大小要做转换 sp——>px
        }

        private void caculatePostion() {
            centerX = (index * 2 + 1) * parentWidth / (totalSteps * 2);
            centerY = parentHeight / 3 - dip2px(context, 2);

            positionTitleX = index * parentWidth / totalSteps + (parentWidth / totalSteps - getStringWidth(titlePaint, title)) / 2;
            positionTitleY = parentHeight / 2 + computeStringHeight(titlePaint, title);

            this.time = "02-03 14:32"; //样例时间格式,用于计算位置
            positionTimeX = index * parentWidth / totalSteps + (parentWidth / totalSteps - getStringWidth(timePaint, time)) / 2;
            positionTimeY = parentHeight * 2 / 3 + computeStringHeight(timePaint, time) + dip2px(context, 3);
            this.time = ""; //清空样例时间,初始化时不能绘制时间

            normalRadius = Math.min(parentHeight / 8, dip2px(context, 7));
            selectedRadius = Math.min(parentHeight / 6, dip2px(context, 13));

            rect = new Rect();
        }

        public void draw(Canvas canvas) {
            drawLines(canvas);
            drawStepPoint(canvas);
            drawTitle(canvas);
            drawTime(canvas);
        }

        private void drawTitle(Canvas canvas) {
            if (isSelected() || isCompleted()) {
                titlePaint.setColor(stepTextTransferColor);
            } else {
                titlePaint.setColor(stepTextNormalColor);
            }
            canvas.drawText(title, positionTitleX, positionTitleY, titlePaint);

        }

        private void drawTime(Canvas canvas) {
            if (!TextUtils.isEmpty(time)) {
                canvas.drawText(time, positionTimeX, positionTimeY, timePaint);
            }
        }

        private void drawStepPoint(Canvas canvas) {
            if (isSelected()) {
                rect.set((int) (centerX - selectedRadius), (int) (centerY - selectedRadius), (int) (centerX + selectedRadius), (int) (centerY + selectedRadius));
                stepSelectedBackground.setBounds(rect);
                stepSelectedBackground.draw(canvas);
            } else {
                rect.set((int) (centerX - normalRadius), (int) (centerY - normalRadius), (int) (centerX + normalRadius), (int) (centerY + normalRadius));
                if (isCompleted()) {
                    stepCompleteBackground.setBounds(rect);
                    stepCompleteBackground.draw(canvas);
                } else {
                    stepNormalBackground.setBounds(rect);
                    stepNormalBackground.draw(canvas);
                }
            }
        }

        private void drawLines(Canvas canvas) {
            if (isSelected() || isCompleted()) {
                linePaint.setColor(stepTextTransferColor);
            } else {
                linePaint.setColor(stepTextNormalColor);
            }
            switch (checkPosition()) {
                case -1: //头
                    canvas.drawLine(centerX, centerY, centerX + parentWidth / (totalSteps * 2), centerY, linePaint);
                    break;
                case 0: //中间
                    canvas.drawLine(centerX - parentWidth / (totalSteps * 2), centerY, centerX + parentWidth / (totalSteps * 2), centerY, linePaint);
                    break;
                case 1: //结尾
                    canvas.drawLine(centerX, centerY, centerX - parentWidth / (totalSteps * 2), centerY, linePaint);
                    break;
            }
        }

        /**
         * 设置当前步骤的状态
         * -1,代表完成;0,代表选中;1,代表正常;
         *
         * @param status
         */
        public void setStepStatus(int status) {
            this.stepStatus = status;
        }

        /**
         * 判断SingleStepView是否被选中,即是否为当前SingleStepView
         *
         * @return
         */
        private boolean isSelected() {
            return 0 == stepStatus;
        }

        /**
         * 判断当前步骤是否已完成
         *
         * @return
         */
        private boolean isCompleted() {
            return -1 == stepStatus;
        }


        /**
         * 检查当前view的位置
         *
         * @return -1,头
         * 0,中间
         * 1,尾
         */
        public int checkPosition() {
            if (0 == index) {
                return -1;
            } else if ((totalSteps - 1) == index) {
                return 1;
            } else {
                return 0;
            }
        }

        /**
         * 获取圆心
         *
         * @return
         */
        public float getCenterX() {
            return centerX;
        }

        /**
         * 获取圆心
         *
         * @return
         */
        public float getCenterY() {
            return centerY;
        }

        /**
         * 为此步添加注释
         *
         * @param time,注释,通常为时间
         */
        public void setAnnotation(String time) {
            this.time = time;
        }

        /**
         * 清除此步的注释
         */
        public void clearAnnotation() {
            this.time = null;
        }

        /**
         * 计算需要绘制的字符串的中的单个字符所需的最大宽度.
         *
         * @param currentMax ,可以为0
         * @param strings
         * @param p
         * @return 返回占用空间最大的一个字符的宽度
         * Google代码中的用法
         */
        private int computeMaxStringWidth(int currentMax, String[] strings, Paint p) {
            float maxWidthF = 0.0f;
            int len = strings.length;
            for (int i = 0; i < len; i++) {
                float width = p.measureText(strings[i]);
                maxWidthF = Math.max(width, maxWidthF);
            }
            int maxWidth = (int) (maxWidthF + 0.5);
            if (maxWidth < currentMax) {
                maxWidth = currentMax;
            }
            return maxWidth;
        }

        /**
         * 据说这种方法更精确, 因为每个字符的宽度是不一样的(比如 1和8), 所以单个计算更准确
         *
         * @param paint
         * @param str
         * @return
         */
        public int getStringWidth(Paint paint, String str) {
            int iRet = 0;
            if (!TextUtils.isEmpty(str)) {
                int len = str.length();
                float[] widths = new float[len];
                paint.getTextWidths(str, widths);
                for (int j = 0; j < len; j++) {
                    iRet += (int) Math.ceil(widths[j]);
                }
            }
            return iRet;
        }

        /**
         * 计算需要绘制的字符串的宽度
         * 经过测试,不准.
         *
         * @param paint
         * @param string
         * @return
         */
        public int computeStringWidth(Paint paint, String string) {
            Rect rect = new Rect();

            //返回包围整个字符串的最小的一个Rect区域
            paint.getTextBounds(string, 0, 1, rect);
            return rect.width();

        }

        /**
         * 计算字符串的绘制高度
         *
         * @param paint
         * @param string
         * @return 只有这种方法可以计算字符串的高度
         */
        public int computeStringHeight(Paint paint, String string) {
            Rect rect = new Rect();

            //返回包围整个字符串的最小的一个Rect区域
            paint.getTextBounds(string, 0, 1, rect);
            return rect.height();
        }

    }
}
