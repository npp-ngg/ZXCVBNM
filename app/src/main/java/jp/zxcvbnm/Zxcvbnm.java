package jp.zxcvbnm;
//
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.PopupWindow;
import android.widget.TextView;
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
    private boolean isFlickUp=false;
    private boolean isFlickLeft=false;
    private boolean isFlickRight=false;
//
// ビュー保持用フィールド
    private View keyboardView;
    private CandidateTextView candidateTextView;
//
// ポップアッププレビュー保持用フィールド
    private PopupWindow keyPreviewPopup;
    private TextView keyPreviewText;
    private final Handler popupHandler=new Handler(Looper.getMainLooper());
    private final Runnable dismissPopupRunnable=new Runnable() {
        @Override public void run() {
            dismissKeyPreview();
        }
    };
//
// キーリピート保持用フィールド
    private int repeatCode=C.Key.KEY_NULL;
    private String repeatLabel=null;
    private boolean isRepeating=false;
    private final Runnable keyRepeatRunnable=new Runnable() {
        @Override public void run() {
            if(isRepeating){
                processKeyAction(repeatCode,repeatLabel);
                popupHandler.postDelayed(this,C.KEY_REPEAT_INTERVAL_MS);
            }
        }
    };
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
        Log.i(C.TAG,"Version="+getResources().getString(R.string.version_name)+
                "(BUILD."+getResources().getInteger(R.integer.build_number)+")");
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
// 入力終了時の処理
    @Override public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);
        stopKeyRepeat();
        dismissKeyPreview();
    }
//
// キーイベントリスナーの全キーへの設定処理
    private void setupKeyListeners(View view) {
        if(view instanceof KeyboardKey){
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            view.setOnTouchListener(this);
            view.setLongClickable(true);
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
        if(shifted){
            switch(currentMode){
                case MODE_JAPANESE:
                    label=key.getBtnLabelJS();
                    break;
                case MODE_ALPHABET:
                    label=key.getBtnLabelAS();
                    break;
                case MODE_JP109:
                    label=key.getBtnLabelJP109S();
                    break;
            }
        }else{
            switch(currentMode){
                case MODE_JAPANESE:
                    label=key.getBtnLabelJ();
                    if(label==null||label.isEmpty()){
                        label=key.getBtnLabelA();
                    }
                    break;
                case MODE_ALPHABET:
                    label=key.getBtnLabelA();
                    break;
                case MODE_JP109:
                    label=key.getBtnLabelJP109();
                    if(label==null||label.isEmpty()){
                        label=key.getBtnLabelA();
                    }
                    break;
            }
            if(label==null||label.isEmpty()){
                label=key.getBtnLabelA();
            }
        }
        return label!=null?label:"";
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
// モード切替キー（あキー）判定処理
    private boolean isModeSwitchKey(KeyboardKey key) {
        if(key==null){
            return false;
        }
        if(key.getId()==R.id.btn49){
            return true;
        }
        int code=getKeyCode(key);
        return code==C.Key.KEY_TO_KANA||code==C.Key.KEY_TO_ALPHA||code==C.Key.KEY_ZENKAKU_HANKAKU;
    }
//
// JP109モード切り替え処理
    private void toggleJp109Mode() {
        if(currentMode==MODE_JP109){
            currentMode=MODE_JAPANESE;
        }else{
            currentMode=MODE_JP109;
        }
        updateKeyboardKeys();
    }
//
// キーの下フリックイベント処理
    private void onFlickDown(KeyboardKey key) {
        int code=getKeyCode(key,true);
        String label=getKeyLabel(key,true);
        processKeyAction(code,label);
    }
//
// キーの上フリックイベント処理
    private void onFlickUp(KeyboardKey key) {
        int code=getKeyCodeUp(key);
        String label=getKeyLabelUp(key);
        processKeyAction(code,label);
    }
//
// キーの左フリックイベント処理
    private void onFlickLeft(KeyboardKey key) {
        if(isModeSwitchKey(key)){
            toggleJp109Mode();
            return;
        }
        int code=getKeyCodeLeft(key);
        String label=getKeyLabelLeft(key);
        if(code!=C.Key.KEY_NULL){
            processKeyAction(code,label);
        }
    }
//
// キーの右フリックイベント処理
    private void onFlickRight(KeyboardKey key) {
        if(isModeSwitchKey(key)){
            toggleJp109Mode();
            return;
        }
        int code=getKeyCodeRight(key);
        String label=getKeyLabelRight(key);
        if(code!=C.Key.KEY_NULL){
            processKeyAction(code,label);
        }
    }
//
// モードに応じた上フリックキーラベル取得処理
    private String getKeyLabelUp(KeyboardKey key) {
        String label=null;
        if(currentMode==MODE_JAPANESE){
            label=key.getBtnLabelJU();
        }
        return label!=null?label:"";
    }
//
// モードに応じた上フリックキーコード取得処理
    private int getKeyCodeUp(KeyboardKey key) {
        int code=C.Key.KEY_NULL;
        if(currentMode==MODE_JAPANESE){
            code=key.getBtnCodeJU();
        }
        return code;
    }
//
// モードに応じた左フリックキーラベル取得処理
    private String getKeyLabelLeft(KeyboardKey key) {
        if(isModeSwitchKey(key)){
            return currentMode==MODE_JP109?"かな":"JP109";
        }
        String label=null;
        if(currentMode==MODE_JAPANESE){
            label=key.getBtnLabelJL();
        }
        return label!=null?label:"";
    }
//
// モードに応じた左フリックキーコード取得処理
    private int getKeyCodeLeft(KeyboardKey key) {
        if(isModeSwitchKey(key)){
            return 1;
        }
        int code=C.Key.KEY_NULL;
        if(currentMode==MODE_JAPANESE){
            code=key.getBtnCodeJL();
        }
        return code;
    }
//
// モードに応じた右フリックキーラベル取得処理
    private String getKeyLabelRight(KeyboardKey key) {
        if(isModeSwitchKey(key)){
            return currentMode==MODE_JP109?"かな":"JP109";
        }
        return "";
    }
//
// モードに応じた右フリックキーコード取得処理
    private int getKeyCodeRight(KeyboardKey key) {
        if(isModeSwitchKey(key)){
            return 1;
        }
        return C.Key.KEY_NULL;
    }
//
// フリックフラグリセット処理
    private void resetFlickFlags() {
        isFlickDown=false;
        isFlickUp=false;
        isFlickLeft=false;
        isFlickRight=false;
    }
//
// キーのタッチイベント処理 (フリック検出・キープレビュー表示)
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
// タッチ開始座標保持およびリピート停止・プレビュー表示
                touchDownX=event.getX();
                touchDownY=event.getY();
                resetFlickFlags();
                stopKeyRepeat();
                showKeyPreview(key,false,false,false,false);
                return false;
            case MotionEvent.ACTION_MOVE:
// フリック状態判定およびプレビュー更新
                float deltaY=event.getY()-touchDownY;
                float deltaX=event.getX()-touchDownX;
                boolean newFlickDown=(deltaY>flickThreshold&&deltaY>Math.abs(deltaX));
                boolean newFlickUp=(deltaY<-flickThreshold&&-deltaY>Math.abs(deltaX));
                boolean newFlickRight=(deltaX>flickThreshold&&deltaX>Math.abs(deltaY));
                boolean newFlickLeft=(deltaX<-flickThreshold&&-deltaX>Math.abs(deltaY));
                if(newFlickDown!=isFlickDown||newFlickUp!=isFlickUp||
                    newFlickLeft!=isFlickLeft||newFlickRight!=isFlickRight){
                    isFlickDown=newFlickDown;
                    isFlickUp=newFlickUp;
                    isFlickLeft=newFlickLeft;
                    isFlickRight=newFlickRight;
                    if(isFlickDown||isFlickUp||isFlickLeft||isFlickRight){
                        stopKeyRepeat();
                    }
                    showKeyPreview(key,isFlickDown,isFlickUp,isFlickLeft,isFlickRight);
                }
                return isFlickDown||isFlickUp||isFlickLeft||isFlickRight;
            case MotionEvent.ACTION_UP:
// キーリピート停止
                boolean wasRepeating=isRepeating;
                stopKeyRepeat();
// フリック確定時の入力実行
                float upDeltaY=event.getY()-touchDownY;
                float upDeltaX=event.getX()-touchDownX;
                if(isFlickDown||(upDeltaY>flickThreshold&&upDeltaY>Math.abs(upDeltaX))){
                    resetFlickFlags();
                    key.setPressed(false);
                    onFlickDown(key);
                    return true;
                }
                if(isFlickUp||(upDeltaY<-flickThreshold&&-upDeltaY>Math.abs(upDeltaX))){
                    resetFlickFlags();
                    key.setPressed(false);
                    onFlickUp(key);
                    return true;
                }
                if(isFlickLeft||(upDeltaX<-flickThreshold&&-upDeltaX>Math.abs(upDeltaY))){
                    resetFlickFlags();
                    key.setPressed(false);
                    onFlickLeft(key);
                    return true;
                }
                if(isFlickRight||(upDeltaX>flickThreshold&&upDeltaX>Math.abs(upDeltaY))){
                    resetFlickFlags();
                    key.setPressed(false);
                    onFlickRight(key);
                    return true;
                }
                resetFlickFlags();
                if(wasRepeating){
                    return true;
                }
                return false;
            case MotionEvent.ACTION_CANCEL:
// タッチキャンセル処理およびリピート停止・プレビュー非表示
                stopKeyRepeat();
                dismissKeyPreview();
                resetFlickFlags();
                return false;
        }
        return false;
    }
//
// キーのロングタップイベント処理
    @Override public boolean onLongClick(View v) {
        if(isFlickDown||isFlickUp||isFlickLeft||isFlickRight){
            return true;
        }
        if(!(v instanceof KeyboardKey)){
            return false;
        }
        KeyboardKey key=(KeyboardKey)v;
        int codeLong=key.getBtnCodeLongTap();
        int code;
        String label;
        if(codeLong!=C.Key.KEY_NULL&&codeLong!=-1){
            code=codeLong;
            label=null;
        }else{
            code=getKeyCode(key);
            label=getKeyLabel(key);
        }
        if(code!=C.Key.KEY_NULL){
            dismissKeyPreview();
            startKeyRepeat(code,label);
            return true;
        }
        return false;
    }
//
// キーリピート開始処理
    private void startKeyRepeat(int code,String label) {
        stopKeyRepeat();
        repeatCode=code;
        repeatLabel=label;
        processKeyAction(repeatCode,repeatLabel);
        if(code!=C.Key.KEY_SHIFT&&code!=C.Key.KEY_TO_ALPHA&&code!=C.Key.KEY_TO_KANA){
            isRepeating=true;
            popupHandler.postDelayed(keyRepeatRunnable,C.KEY_REPEAT_INTERVAL_MS);
        }
    }
//
// キーリピート停止処理
    private void stopKeyRepeat() {
        isRepeating=false;
        popupHandler.removeCallbacks(keyRepeatRunnable);
    }
//
// キープレビューポップアップ表示処理
    private void showKeyPreview(KeyboardKey key,boolean isFlickDown,boolean isFlickUp,
        boolean isFlickLeft,boolean isFlickRight) {
        if(key==null){
            return;
        }
        String label;
        int code;
        if(isFlickLeft){
            label=getKeyLabelLeft(key);
            code=getKeyCodeLeft(key);
        }else if(isFlickRight){
            label=getKeyLabelRight(key);
            code=getKeyCodeRight(key);
        }else if(isFlickUp){
            label=getKeyLabelUp(key);
            code=getKeyCodeUp(key);
        }else{
            boolean shifted=isFlickDown||isShifted;
            label=getKeyLabel(key,shifted);
            code=getKeyCode(key,shifted);
        }
        if(label==null||label.trim().isEmpty()||code<=0){
            dismissKeyPreview();
            return;
        }
        popupHandler.removeCallbacks(dismissPopupRunnable);
        if(keyPreviewPopup==null){
            View popupView=getLayoutInflater().inflate(R.layout.key_popup_view,null);
            keyPreviewText=(TextView)popupView;
            keyPreviewPopup=new PopupWindow(popupView,ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,false);
            keyPreviewPopup.setTouchable(false);
            keyPreviewPopup.setClippingEnabled(false);
        }
        if(keyPreviewText!=null)keyPreviewText.setText(label);
        int[] location=new int[2];
        key.getLocationInWindow(location);
        int keyX=location[0];
        int keyY=location[1];
        int keyWidth=key.getWidth();
        int previewWidth=getResources().getDimensionPixelSize(R.dimen.keypreview_width);
        int previewHeight=getResources().getDimensionPixelSize(R.dimen.keypreview_height);
        int popupWidth=Math.max(keyWidth,previewWidth);
        int popupHeight=previewHeight;
        int x=keyX+(keyWidth-popupWidth)/2;
        int y=keyY-popupHeight;
        if(keyPreviewPopup.isShowing()){
            keyPreviewPopup.update(x,y,popupWidth,popupHeight);
        }else if(keyboardView!=null&&keyboardView.getWindowToken()!=null){
            keyPreviewPopup.setWidth(popupWidth);
            keyPreviewPopup.setHeight(popupHeight);
            keyPreviewPopup.showAtLocation(keyboardView,Gravity.NO_GRAVITY,x,y);
        }
        popupHandler.postDelayed(dismissPopupRunnable,C.KEY_PREVIEW_TIMEOUT_MS);
    }
//
// キープレビューポップアップ非表示処理
    private void dismissKeyPreview() {
        popupHandler.removeCallbacks(dismissPopupRunnable);
        if(keyPreviewPopup!=null&&keyPreviewPopup.isShowing()){
            keyPreviewPopup.dismiss();
        }
    }
}
