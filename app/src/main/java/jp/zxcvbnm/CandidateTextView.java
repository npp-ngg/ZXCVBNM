package jp.zxcvbnm;
//
import android.content.Context;
import android.text.Layout;
import android.text.Spanned;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import androidx.appcompat.widget.AppCompatTextView;
import java.util.List;
//
// 変換候補表示用テキストビュークラス
public class CandidateTextView extends AppCompatTextView {
//
// 候補タップイベント用インターフェース
    public interface OnCandidateClickListener {
        void onCandidateClick(int index,String candidate);
    }
//
// フィールド定義
    private OnCandidateClickListener listener;
    private float lastTouchX;
    private float downX;
    private float downY;
    private boolean isScrolling;
    private int touchSlop;
//
// コンストラクタ1
    public CandidateTextView(Context context) {
        super(context);
        init();
    }
//
// コンストラクタ2
    public CandidateTextView(Context context,AttributeSet attrs) {
        super(context,attrs);
        init();
    }
//
// コンストラクタ3
    public CandidateTextView(Context context,AttributeSet attrs,int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        init();
    }
//
// 初期化処理
    private void init() {
        setSingleLine(true);
        setHorizontallyScrolling(true);
        touchSlop=ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }
//
// 候補リスト設定処理
    public void setCandidates(List<String> candidates,OnCandidateClickListener listener) {
        this.listener=listener;
        scrollTo(0,0);
        if(candidates==null||candidates.isEmpty()){
            setText("");
            return;
        }
        SpannableStringBuilder ssb=new SpannableStringBuilder();
        for(int i=0;i<candidates.size();i++){
            if(i>0){
                ssb.append(C.Candidate.SEPARATOR);
            }
            final int index=i;
            final String candidate=candidates.get(i);
            int start=ssb.length();
            ssb.append(candidate);
            int end=ssb.length();
            ssb.setSpan(new ClickableSpan() {
                @Override public void onClick(View widget) {
                    if(CandidateTextView.this.listener!=null){
                        CandidateTextView.this.listener.onCandidateClick(index,candidate);
                    }
                }
                @Override public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                    ds.setColor(getCurrentTextColor());
                }
            },start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        setText(ssb,BufferType.SPANNABLE);
    }
//
// 最大スクロール量取得処理
    private int getMaxScrollX() {
        Layout layout=getLayout();
        if(layout==null){
            return 0;
        }
        int textWidth=(int)layout.getLineWidth(0);
        int viewWidth=getWidth()-getPaddingLeft()-getPaddingRight();
        return Math.max(0,textWidth-viewWidth);
    }
//
// タッチイベント処理 (左右スクロールおよび候補選択の制御)
    @Override public boolean onTouchEvent(MotionEvent event) {
        int action=event.getAction();
        float x=event.getX();
        float y=event.getY();
        switch(action){
            case MotionEvent.ACTION_DOWN:
                downX=x;
                downY=y;
                lastTouchX=x;
                isScrolling=false;
                super.onTouchEvent(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                float dx=x-lastTouchX;
                float totalDx=Math.abs(x-downX);
                float totalDy=Math.abs(y-downY);
                if(!isScrolling&&(totalDx>touchSlop&&totalDx>totalDy)){
                    isScrolling=true;
                }
                if(isScrolling){
                    int newScrollX=getScrollX()-(int)dx;
                    int maxScroll=getMaxScrollX();
                    if(newScrollX<0){
                        newScrollX=0;
                    }else if(newScrollX>maxScroll){
                        newScrollX=maxScroll;
                    }
                    scrollTo(newScrollX,0);
                    lastTouchX=x;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(!isScrolling&&Math.abs(x-downX)<touchSlop){
                    performCandidateClick(x,y);
                }
                isScrolling=false;
                return true;
            case MotionEvent.ACTION_CANCEL:
                isScrolling=false;
                break;
        }
        return super.onTouchEvent(event);
    }
//
// タッチ座標からの候補タップ実行処理
    private void performCandidateClick(float x,float y) {
        CharSequence text=getText();
        if(!(text instanceof Spanned)){
            Log.d(C.TAG,"text is not Spanned: "+(text!=null?text.getClass():"null"));
            return;
        }
        Spanned spanned=(Spanned)text;
        Layout layout=getLayout();
        if(layout==null){
            Log.d(C.TAG,"layout is null");
            return;
        }
        float horiz=x-getTotalPaddingLeft()+getScrollX();
        ClickableSpan[] spans=spanned.getSpans(0,spanned.length(),ClickableSpan.class);
        if(spans==null||spans.length==0){
            Log.d(C.TAG,"no spans found");
            return;
        }
        for(ClickableSpan span:spans){
            int start=spanned.getSpanStart(span);
            int end=spanned.getSpanEnd(span);
            float left=layout.getPrimaryHorizontal(start);
            float right=layout.getPrimaryHorizontal(end);
            if(horiz>=left&&horiz<=right){
                Log.d(C.TAG,"span clicked via horiz bounds: "+left+"-"+right);
                span.onClick(this);
                return;
            }
        }
        float vert=y-getTotalPaddingTop()+getScrollY();
        int line=layout.getLineForVertical((int)vert);
        int offset=layout.getOffsetForHorizontal(line,horiz);
        ClickableSpan[] offsetSpans=spanned.getSpans(Math.max(0,offset-1),
            Math.min(spanned.length(),offset+1),ClickableSpan.class);
        if(offsetSpans.length>0){
            Log.d(C.TAG,"span clicked via offset: "+offset);
            offsetSpans[0].onClick(this);
        }
    }
}
