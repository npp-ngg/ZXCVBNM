package jp.zxcvbnm;
//
import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import com.google.android.apps.inputmethod.libs.mozc.session.MozcJNI;
import org.mozc.android.inputmethod.japanese.MozcLog;
//
// メイン入力メソッドサービスモジュールクラス
public class Zxcvbnm extends InputMethodService implements View.OnClickListener,View.OnLongClickListener,View.OnTouchListener {
//
// 入力モード定数定義
    public static final int MODE_JAPANESE=0;
    public static final int MODE_ALPHABET=1;
    public static final int MODE_JP109=2;
//
// 現在の入力モード保持用フィールド
    private int currentMode=MODE_JAPANESE;
    private boolean isShifted=false;
//
// タッチ・フリック操作保持用フィールド
    private float touchDownX=0;
    private float touchDownY=0;
    private boolean isFlickDown=false;
//
// ビュー保持用フィールド
    private View keyboardView;
    private CandidateTextView candidateTextView;
//
// サービス作成時の初期化処理
    @Override public void onCreate() {
        super.onCreate();
// Mozc JNIライブラリの初期化処理
        try{
            String userProfileDir=getFilesDir().getAbsolutePath();
            MozcJNI.load(userProfileDir,null);
        }catch(Throwable e){
            MozcLog.e("Failed to load MozcJNI",e);
        }
    }
//
// キーボードビューの生成処理
    @Override public View onCreateInputView() {
        keyboardView=getLayoutInflater().inflate(R.layout.keyboard,null);
        setupKeyListeners(keyboardView);
        updateKeyboardKeys();
        return keyboardView;
    }
//
// 変換候補ビューの生成処理
    @Override public View onCreateCandidatesView() {
        View candidatesView=getLayoutInflater().inflate(R.layout.candidate_popup_view,null);
        if(candidatesView!=null){
            candidateTextView=candidatesView.findViewById(R.id.candidate_text);
        }
        return candidatesView;
    }
//
// 入力開始時の処理
    @Override public void onStartInputView(EditorInfo info,boolean restarting) {
        super.onStartInputView(info,restarting);
        updateKeyboardKeys();
    }
//
// キーイベントリスナーの全キーへの設定処理
    private void setupKeyListeners(View view) {
        if(view instanceof KeyboardKey){
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            view.setOnTouchListener(this);
        }else if(view instanceof ViewGroup){
            ViewGroup group=(ViewGroup)view;
            int count=group.getChildCount();
            for(int i=0;i<count;i++){
                setupKeyListeners(group.getChildAt(i));
            }
        }
    }
//
// キーボード各キーのラベル更新処理
    private void updateKeyboardKeys() {
        if(keyboardView==null){
            return;
        }
        updateKeysRecursively(keyboardView);
    }
//
// 再帰的なキーラベル設定処理
    private void updateKeysRecursively(View view) {
        if(view instanceof KeyboardKey){
            KeyboardKey key=(KeyboardKey)view;
            String label=getKeyLabel(key);
            if(label!=null){
                key.setText(label);
            }
        }else if(view instanceof ViewGroup){
            ViewGroup group=(ViewGroup)view;
            int count=group.getChildCount();
            for(int i=0;i<count;i++){
                updateKeysRecursively(group.getChildAt(i));
            }
        }
    }
//
// モードおよびシフト状態に応じたキーラベル取得処理
    private String getKeyLabel(KeyboardKey key,boolean shifted) {
        String label=null;
        switch(currentMode){
            case MODE_JAPANESE:
                label=shifted?key.getBtnLabelJS():key.getBtnLabelJ();
                if(label==null||label.isEmpty()){
                    label=shifted?key.getBtnLabelAS():key.getBtnLabelA();
                }
                break;
            case MODE_ALPHABET:
                label=shifted?key.getBtnLabelAS():key.getBtnLabelA();
                if(label==null||label.isEmpty()){
                    label=key.getBtnLabelA();
                }
                break;
            case MODE_JP109:
                label=shifted?key.getBtnLabelJP109S():key.getBtnLabelJP109();
                if(label==null||label.isEmpty()){
                    label=key.getBtnLabelJP109();
                }
                break;
        }
        if(label==null||label.isEmpty()){
            label=key.getBtnLabelA();
        }
        return label;
    }
//
    private String getKeyLabel(KeyboardKey key) {
        return getKeyLabel(key,isShifted);
    }
//
// モードおよびシフト状態に応じたキーコード取得処理
    private int getKeyCode(KeyboardKey key,boolean shifted) {
        int code=C.Key.KEY_NULL;
        switch(currentMode){
            case MODE_JAPANESE:
                code=shifted?key.getBtnCodeJS():key.getBtnCodeJ();
                break;
            case MODE_ALPHABET:
                code=shifted?key.getBtnCodeAS():key.getBtnCodeA();
                break;
            case MODE_JP109:
                code=shifted?key.getBtnCodeJP109S():key.getBtnCodeJP109();
                break;
        }
        return code;
    }
//
    private int getKeyCode(KeyboardKey key) {
        return getKeyCode(key,isShifted);
    }
//
// 文字列送信処理
    private void sendText(String text) {
        InputConnection ic=getCurrentInputConnection();
        if(ic!=null){
            ic.commitText(text,1);
        }
    }
//
// キーアクション共通処理
    private void processKeyAction(int code,String label) {
        switch(code){
            case C.Key.KEY_SHIFT:
// シフトキー処理
                isShifted=!isShifted;
                updateKeyboardKeys();
                break;
            case C.Key.KEY_DONE:
// エンターキー処理
                sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER);
                break;
            case C.Key.KEY_DELETE:
// バックスペースキー処理
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL);
                break;
            case C.Key.KEY_DELETE_FORWARD:
// 順方向削除処理
                sendDownUpKeyEvents(KeyEvent.KEYCODE_FORWARD_DEL);
                break;
            case C.Key.KEY_RIGHT_ARROW:
// 右カーソルキー処理
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_RIGHT);
                break;
            case C.Key.KEY_LEFT_ARROW:
// 左カーソルキー処理
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
                break;
            case C.Key.KEY_TO_ALPHA:
// 英数モード切替処理
                currentMode=MODE_ALPHABET;
                updateKeyboardKeys();
                break;
            case C.Key.KEY_TO_KANA:
// 日本語モード切替処理
                currentMode=MODE_JAPANESE;
                updateKeyboardKeys();
                break;
            default:
// 通常文字キー処理
                if(code>0){
                    if(label!=null&&!label.isEmpty()){
                        sendText(label);
                    }else{
                        char c=(char)code;
                        sendKeyChar(c);
                    }
                    if(isShifted){
                        isShifted=false;
                        updateKeyboardKeys();
                    }
                }
                break;
        }
    }
//
// キーのクリックイベント処理
    @Override public void onClick(View v) {
        if(!(v instanceof KeyboardKey)){
            return;
        }
        KeyboardKey key=(KeyboardKey)v;
        int code=getKeyCode(key);
        String label=getKeyLabel(key);
        processKeyAction(code,label);
    }
//
// キーの下フリックイベント処理
    private void onFlickDown(KeyboardKey key) {
        int code=getKeyCode(key,true);
        String label=getKeyLabel(key,true);
        processKeyAction(code,label);
    }
//
// キーのタッチイベント処理 (下フリック検出)
    @Override public boolean onTouch(View v,MotionEvent event) {
        if(!(v instanceof KeyboardKey)){
            return false;
        }
        KeyboardKey key=(KeyboardKey)v;
        int action=event.getAction();
        float density=getResources().getDisplayMetrics().density;
        float flickThreshold=20.0f*density;
        switch(action){
            case MotionEvent.ACTION_DOWN:
// タッチ開始座標保持
                touchDownX=event.getX();
                touchDownY=event.getY();
                isFlickDown=false;
                return false;
            case MotionEvent.ACTION_MOVE:
// 下フリック状態判定
                float deltaY=event.getY()-touchDownY;
                float deltaX=event.getX()-touchDownX;
                if(deltaY>flickThreshold&&deltaY>Math.abs(deltaX)){
                    isFlickDown=true;
                }
                return false;
            case MotionEvent.ACTION_UP:
// 下フリック確定時の入力実行
                float upDeltaY=event.getY()-touchDownY;
                float upDeltaX=event.getX()-touchDownX;
                if(isFlickDown||(upDeltaY>flickThreshold&&upDeltaY>Math.abs(upDeltaX))){
                    isFlickDown=false;
                    key.setPressed(false);
                    onFlickDown(key);
                    return true;
                }
                isFlickDown=false;
                return false;
            case MotionEvent.ACTION_CANCEL:
// タッチキャンセル処理
                isFlickDown=false;
                return false;
        }
        return false;
    }
//
// キーのロングタップイベント処理
    @Override public boolean onLongClick(View v) {
        if(isFlickDown){
            return true;
        }
        if(!(v instanceof KeyboardKey)){
            return false;
        }
        KeyboardKey key=(KeyboardKey)v;
        int codeLong=key.getBtnCodeLongTap();
        if(codeLong!=-1){
            if(codeLong>0){
                sendKeyChar((char)codeLong);
                return true;
            }
        }
        return false;
    }
}
