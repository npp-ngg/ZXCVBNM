package jp.zxcvbnm;
//
import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import com.google.android.apps.inputmethod.libs.mozc.session.MozcJNI;
import org.mozc.android.inputmethod.japanese.MozcLog;
//
// メイン入力メソッドサービスモジュールクラス
public class Zxcvbnm extends InputMethodService implements View.OnClickListener,View.OnLongClickListener {
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
    private String getKeyLabel(KeyboardKey key) {
        String label=null;
        switch(currentMode){
            case MODE_JAPANESE:
                label=isShifted?key.getBtnLabelJS():key.getBtnLabelJ();
                if(label==null){
                    label=key.getBtnLabelJ();
                }
                break;
            case MODE_ALPHABET:
                label=isShifted?key.getBtnLabelAS():key.getBtnLabelA();
                if(label==null){
                    label=key.getBtnLabelA();
                }
                break;
            case MODE_JP109:
                label=isShifted?key.getBtnLabelJP109S():key.getBtnLabelJP109();
                if(label==null){
                    label=key.getBtnLabelJP109();
                }
                break;
        }
        if(label==null){
            label=key.getBtnLabelA();
        }
        return label;
    }
//
// モードおよびシフト状態に応じたキーコード取得処理
    private int getKeyCode(KeyboardKey key) {
        int code=C.Key.KEY_NULL;
        switch(currentMode){
            case MODE_JAPANESE:
                code=isShifted?key.getBtnCodeJS():key.getBtnCodeJ();
                break;
            case MODE_ALPHABET:
                code=isShifted?key.getBtnCodeAS():key.getBtnCodeA();
                break;
            case MODE_JP109:
                code=isShifted?key.getBtnCodeJP109S():key.getBtnCodeJP109();
                break;
        }
        return code;
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
// キーのクリックイベント処理
    @Override public void onClick(View v) {
        if(!(v instanceof KeyboardKey)){
            return;
        }
        KeyboardKey key=(KeyboardKey)v;
        int code=getKeyCode(key);
        switch(code){
            case C.Key.KEY_SHIFT:
// シフトキー処理
                isShifted=!isShifted;
                updateKeyboardKeys();
                break;
            case -4:
// エンターキー処理
                sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER);
                break;
            case -5:
// バックスペースキー処理
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL);
                break;
            case -103:
// 右カーソルキー処理
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_RIGHT);
                break;
            case -104:
// 左カーソルキー処理
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
                break;
            case -115:
// 英数モード切替処理
                currentMode=MODE_ALPHABET;
                updateKeyboardKeys();
                break;
            case -116:
// 日本語モード切替処理
                currentMode=MODE_JAPANESE;
                updateKeyboardKeys();
                break;
            default:
// 通常文字キー処理
                if(code>0){
                    String label=getKeyLabel(key);
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
// キーのロングタップイベント処理
    @Override public boolean onLongClick(View v) {
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
