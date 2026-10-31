package jp.zxcvbnm;
//
import org.mozc.android.inputmethod.japanese.protobuf.ProtoConfig;
//
final class C{
    static final String TAG="ZXCVBNM";
    static final String memoFile="memo.dat";
    static final int KEY_PREVIEW_TIMEOUT_MS=300;
    static final int KEY_REPEAT_INTERVAL_MS=80;
    static final class Mozc{
        static final String mozcDataFile="mozc.data";
        static final String mozcChildDir=".mozc";
        static final int suggestionsSize=15;
        static final int candidatesSizeLimit=30;
        static final int sessionKeymap=ProtoConfig.Config.SessionKeymap.MSIME_VALUE;
        static final String userDictionaryName="_USER_DICTIONARY_";
    }
    static final class Key{
        static final char CHAR_SPACE=' ';
        static final int KEY_NULL=0;
        static final int KEY_DELETE=-5;
        static final int KEY_DONE=-4;
        static final int KEY_SHIFT=-1;
        static final int KEY_HENKAN=-100;
        static final int KEY_DEFINITE=-101;
        static final int KEY_UP_ARROW=-105;
        static final int KEY_DOWN_ARROW=-102;
        static final int KEY_RIGHT_ARROW=-103;
        static final int KEY_LEFT_ARROW=-104;
        static final int KEY_NOUSE=-999;
        static final int KEY_DELETE_FORWARD=-106;
        static final int KEY_CTL_A=-110;
        static final int KEY_CTL_X=-111;
        static final int KEY_CTL_C=-112;
        static final int KEY_CTL_V=-113;
        static final int KEY_CTL_I=-114;
        static final int KEY_TO_ALPHA=-115;
        static final int KEY_TO_KANA=-116;
        static final int KEY_HOME=-150;
        static final int KEY_END=-151;
        static final int KEY_MEMO=-152;
        static final int KEYBOARD_TOGLE_SOFTKEYBOARD=-153;
        static final int KEYBOARD_DIRECT=-154;
        static final int KEY_MUHENKAN=-155;
        static final int KEY_ZENKAKU_HANKAKU=-156;
        static final int KEY_EISUU=-157;
        static final int KEY_KANA=-158;
        static final int KEY_LEFT_RIGHT_SHIFT=-159;
        static final int KEY_RELOAD_PREF=-160;
    }
    static final class Keyboard{
        static final int KEYBOARD_PHONE=0;
        static final int KEYBOARD_TABLET=1;
    }
    static class Preferences{
        static int maxPhoneWidth=720;
    }
}