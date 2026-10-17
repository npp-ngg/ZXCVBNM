package jp.zxcvbnm;
//
import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;
//
// 変換候補表示用テキストビュークラス
public class CandidateTextView extends AppCompatTextView {
//
// コンストラクタ1
    public CandidateTextView(Context context) {
        super(context);
    }
//
// コンストラクタ2
    public CandidateTextView(Context context,AttributeSet attrs) {
        super(context,attrs);
    }
//
// コンストラクタ3
    public CandidateTextView(Context context,AttributeSet attrs,int defStyleAttr) {
        super(context,attrs,defStyleAttr);
    }
}
