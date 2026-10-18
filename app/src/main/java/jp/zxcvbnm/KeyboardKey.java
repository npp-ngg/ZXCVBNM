package jp.zxcvbnm;
//
import jp.zxcvbnm.C;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatButton;
//
// キーボードキーのカスタムビュークラス
public class KeyboardKey extends AppCompatButton {
//
// ラベル保持用のフィールド定義
    private static final String NULL_STRING=new String("");
    private String btnLabelA;
    private String btnLabelAS;
    private String btnLabelJ;
    private String btnLabelJS;
    private String btnLabelJP109;
    private String btnLabelJP109S;
//
// キーコード保持用のフィールド定義
    private int btnCodeA=C.Key.KEY_NULL;
    private int btnCodeAS=C.Key.KEY_NULL;
    private int btnCodeJ=C.Key.KEY_NULL;
    private int btnCodeJS=C.Key.KEY_NULL;
    private int btnCodeJP109=C.Key.KEY_NULL;
    private int btnCodeJP109S=C.Key.KEY_NULL;
    private int btnCodeLongTap=C.Key.KEY_NULL;
//
// コンストラクタ1
    public KeyboardKey(Context context) {
        super(context);
        init(context,null,0);
    }
//
// コンストラクタ2
    public KeyboardKey(Context context,AttributeSet attrs) {
        super(context,attrs);
        init(context,attrs,0);
    }
//
// コンストラクタ3
    public KeyboardKey(Context context,AttributeSet attrs,int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        init(context,attrs,defStyleAttr);
    }
//
// 属性の初期化処理
    private void init(Context context,AttributeSet attrs,int defStyleAttr) {
        if(attrs==null){
            return;
        }
// 属性読み込み処理
        TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.KeyboardKey,defStyleAttr,0);
        try{
            if((btnLabelA=a.getString(R.styleable.KeyboardKey_btnLabelA))==null)btnLabelA=NULL_STRING;
            if((btnLabelAS=a.getString(R.styleable.KeyboardKey_btnLabelAS))==null)btnLabelAS=NULL_STRING;
            if((btnLabelJ=a.getString(R.styleable.KeyboardKey_btnLabelJ))==null)btnLabelJ=NULL_STRING;
            if((btnLabelJS=a.getString(R.styleable.KeyboardKey_btnLabelJS))==null)btnLabelJS=NULL_STRING;
            if((btnLabelJP109=a.getString(R.styleable.KeyboardKey_btnLabelJP109))==null)btnLabelJP109=NULL_STRING;
            if((btnLabelJP109S=a.getString(R.styleable.KeyboardKey_btnLabelJP109S))==null)btnLabelJP109S=NULL_STRING;
//
            if((btnCodeA=a.getInt(R.styleable.KeyboardKey_btnCodeA,C.Key.KEY_NULL))==C.Key.KEY_NULL)
                if(!btnLabelA.isEmpty())btnCodeA=btnLabelA.charAt(0);
            if((btnCodeAS=a.getInt(R.styleable.KeyboardKey_btnCodeAS,C.Key.KEY_NULL))==C.Key.KEY_NULL)
                if(!btnLabelAS.isEmpty())btnCodeAS=btnLabelAS.charAt(0);
            if((btnCodeJ=a.getInt(R.styleable.KeyboardKey_btnCodeJ,C.Key.KEY_NULL))==C.Key.KEY_NULL)
                if(!btnLabelJ.isEmpty())btnCodeJ=btnLabelJ.charAt(0);
            if((btnCodeJS=a.getInt(R.styleable.KeyboardKey_btnCodeJS,C.Key.KEY_NULL))==C.Key.KEY_NULL)
                if(!btnLabelJS.isEmpty())btnCodeJS=btnLabelJS.charAt(0);
            if((btnCodeJP109=a.getInt(R.styleable.KeyboardKey_btnCodeJP109,C.Key.KEY_NULL))==C.Key.KEY_NULL)
                if(!btnLabelJP109.isEmpty())btnCodeJP109=btnLabelJP109.charAt(0);
            if((btnCodeJP109S=a.getInt(R.styleable.KeyboardKey_btnCodeJP109S,C.Key.KEY_NULL))==C.Key.KEY_NULL)
                if(!btnLabelJP109S.isEmpty())btnCodeJP109S=btnLabelJP109S.charAt(0);
            if(btnCodeJ==C.Key.KEY_NULL)btnCodeJ=btnCodeA;
            if(btnCodeJS==C.Key.KEY_NULL)btnCodeJS=btnCodeAS;
            btnCodeLongTap=a.getInt(R.styleable.KeyboardKey_btnCodeLongTap,C.Key.KEY_NULL);
        }finally{
            a.recycle();
        }
    }
//
// ゲッターメソッド群
    public String getBtnLabelA() {
        return btnLabelA;
    }
//
    public String getBtnLabelAS() {
        return btnLabelAS;
    }
//
    public String getBtnLabelJ() {
        if(btnLabelJ==null||btnLabelJ.isEmpty()){
            return btnLabelA;
        }
        return btnLabelJ;
    }
//
    public String getBtnLabelJS() {
        if(btnLabelJS==null||btnLabelJS.isEmpty()){
            return btnLabelAS;
        }
        return btnLabelJS;
    }
//
    public String getBtnLabelJP109() {
        return btnLabelJP109;
    }
//
    public String getBtnLabelJP109S() {
        return btnLabelJP109S;
    }
//
    public int getBtnCodeA() {
        return btnCodeA;
    }
//
    public int getBtnCodeAS() {
        return btnCodeAS;
    }
//
    public int getBtnCodeJ() {
        if(btnCodeJ==C.Key.KEY_NULL){
            return btnCodeA;
        }
        return btnCodeJ;
    }
//
    public int getBtnCodeJS() {
        if(btnCodeJS==C.Key.KEY_NULL){
            return btnCodeAS;
        }
        return btnCodeJS;
    }
//
    public int getBtnCodeJP109() {
        return btnCodeJP109;
    }
//
    public int getBtnCodeJP109S() {
        return btnCodeJP109S;
    }
//
    public int getBtnCodeLongTap() {
        return btnCodeLongTap;
    }
//
// セッターメソッド群
    public void setBtnLabelA(String btnLabelA) {
        this.btnLabelA=btnLabelA;
    }
//
    public void setBtnLabelAS(String btnLabelAS) {
        this.btnLabelAS=btnLabelAS;
    }
//
    public void setBtnLabelJ(String btnLabelJ) {
        this.btnLabelJ=btnLabelJ;
    }
//
    public void setBtnLabelJS(String btnLabelJS) {
        this.btnLabelJS=btnLabelJS;
    }
//
    public void setBtnLabelJP109(String btnLabelJP109) {
        this.btnLabelJP109=btnLabelJP109;
    }
//
    public void setBtnLabelJP109S(String btnLabelJP109S) {
        this.btnLabelJP109S=btnLabelJP109S;
    }
//
    public void setBtnCodeA(int btnCodeA) {
        this.btnCodeA=btnCodeA;
    }
//
    public void setBtnCodeAS(int btnCodeAS) {
        this.btnCodeAS=btnCodeAS;
    }
//
    public void setBtnCodeJ(int btnCodeJ) {
        this.btnCodeJ=btnCodeJ;
    }
//
    public void setBtnCodeJS(int btnCodeJS) {
        this.btnCodeJS=btnCodeJS;
    }
//
    public void setBtnCodeJP109(int btnCodeJP109) {
        this.btnCodeJP109=btnCodeJP109;
    }
//
    public void setBtnCodeJP109S(int btnCodeJP109S) {
        this.btnCodeJP109S=btnCodeJP109S;
    }
//
    public void setBtnCodeLongTap(int btnCodeLongTap) {
        this.btnCodeLongTap=btnCodeLongTap;
    }
}
