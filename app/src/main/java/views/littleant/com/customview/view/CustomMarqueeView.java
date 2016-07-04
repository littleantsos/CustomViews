package views.littleant.com.customview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import views.littleant.com.customview.utils.DisplayUtils;

public class CustomMarqueeView extends View {

    private List<FirerNewsModel> listNews;
    /**
     * 正在滚动的数据
     */
    private List<MarqueeItem> marqueeItemList;
    private Paint paint;
    private boolean isScroll = false;
    public static final int SPACING = 120;

    /**
     * 每次刷新移动的距离
     */
    public static final int STEP = 4;
    /**
     * 刷新频率
     */
    public static final int REFRESH_FREQUENCY = 20;

    private int textPixelSize;

    private ScrollCompleteListener scrollCompleteListener;

    public CustomMarqueeView(Context context) {
        this(context, null);
    }

    public CustomMarqueeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomMarqueeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        listNews = new ArrayList<>();
        marqueeItemList = new ArrayList<>();
        paint = new Paint();
        textPixelSize  = DisplayUtils.dip2px(context,12);
        paint.setTextSize(textPixelSize);
        paint.setColor(Color.parseColor("#333333"));
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int waitScrollPoolSize = listNews.size();
        int inScrollPoolSize = marqueeItemList.size();
        if(waitScrollPoolSize == 0 && inScrollPoolSize == 0){
            //没有正在跑的数据和未跑的数据时，1s一次等待数据
            postInvalidateDelayed(REFRESH_FREQUENCY);
        } else if(waitScrollPoolSize != 0 && inScrollPoolSize == 0){
            //屏幕上没有正在跑的数据时，添加的数据要跑
            addOneToScrollQueue(canvas);
            postInvalidateDelayed(REFRESH_FREQUENCY);
        }else if(inScrollPoolSize != 0){
            //判断最前面的一个是否出了屏幕，出了屏幕，移除
            //判断最后一个时候超过了屏幕，超过了后，添加待滚动的数据
            boolean needRemoveFirst = false;
            boolean needAddOne = false;
            for(int i = 0; i < inScrollPoolSize; i++){
                MarqueeItem marqueeItem = marqueeItemList.get(i);
                marqueeItem.scrollPosition -= STEP;
                Paint.FontMetrics fontMetrics = paint.getFontMetrics();
                float fontHeight = fontMetrics.bottom - fontMetrics.top;
                canvas.drawText(marqueeItem.body, marqueeItem.scrollPosition,
                        (getMeasuredHeight() + fontHeight) / 2 - fontMetrics.bottom, paint);
                if(i == 0) {
                    float rightPosition = marqueeItem.scrollPosition +
                            paint.measureText(marqueeItem.body);
                    if(rightPosition < 0)
                        needRemoveFirst = true;
                }

                if(i == inScrollPoolSize - 1){
                    float rightPosition = marqueeItem.scrollPosition +
                            paint.measureText(marqueeItem.body) + SPACING;
                    if(rightPosition < canvas.getWidth()){
                        needAddOne = true;
                    }
                }
            }

            if(needRemoveFirst)
                marqueeItemList.remove(0);

            if(needAddOne &&  waitScrollPoolSize != 0)
                addOneToScrollQueue(canvas);

            postInvalidateDelayed(REFRESH_FREQUENCY);

            if(isScroll && marqueeItemList.size() == 0){
                scrollCompleteListener.scrollComplete();
            }
        }
    }

    private void addOneToScrollQueue(Canvas canvas) {
        FirerNewsModel firewireNewsModel = listNews.get(0);
        String title = firewireNewsModel.getTitle();
        MarqueeItem marqueeItem = new MarqueeItem();
        marqueeItem.scrollPosition = canvas.getWidth();
        marqueeItem.body = title;
        listNews.remove(0);
        marqueeItemList.add(marqueeItem);
    }

    public void addNews(List<FirerNewsModel> list){
        if(null == list) return;
        if(list.size() == 0) return;
        listNews.addAll(list);
        if(!isScroll) {
            postInvalidate();
            isScroll = true;
        }
    }

    public void setScrollCompleteListener(ScrollCompleteListener listener){
        this.scrollCompleteListener = listener;
    }

    public class MarqueeItem{
        String body;
        float scrollPosition;
    }

    interface ScrollCompleteListener{
        void scrollComplete();
    }


    class FirerNewsModel {
        private int ID;
        private String NewsId;
        private String UpdateTime;
        private String FirstPubTime;
        private boolean IsEcoIndicator;
        private String Title;
        private boolean IsTop;
        private String GoodType;
        private double Prev;
        private double Pred;
        private double Curr;
        private String KeyWords;
        private int StarLevel;
        private String Nation;
        private String Details;
        private boolean IsPush;
        private String title;


        public String getTitle() {
            return title;
        }
    }
}