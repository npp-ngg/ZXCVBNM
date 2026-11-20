package jp.zxcvbnm;
//
import org.mozc.android.inputmethod.japanese.protobuf.ProtoConfig;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
//
final class C{
    static final String TAG="ZXCVBNM";
    static final String memoFile="memo.dat";
    static final int KEY_PREVIEW_TIMEOUT_MS=300;
    static final int KEY_REPEAT_INTERVAL_MS=80;
    static final class Candidate{
        static final String SEPARATOR=" ｜ ";
    }
    static final class Mozc{
        static final String mozcDataFile="mozc.data";
        static final String mozcChildDir=".mozc";
        static final int suggestionsSize=10;
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
        static final Map<Character,String> US_KEY_TO_KANA_MAP=createUsKeyToKanaMap();
//
// 英語キーボード配列かな変換テーブル生成処理
        private static Map<Character,String> createUsKeyToKanaMap() {
            Map<Character,String> map=new HashMap<>();
            map.put('1',"ぬ");map.put('!',"ぬ");
            map.put('2',"ふ");map.put('@',"ふ");
            map.put('3',"あ");map.put('#',"ぁ");
            map.put('4',"う");map.put('$',"ぅ");
            map.put('5',"え");map.put('%',"ぇ");
            map.put('6',"お");map.put('^',"ぉ");
            map.put('7',"や");map.put('&',"ゃ");
            map.put('8',"ゆ");map.put('*',"ゅ");
            map.put('9',"よ");map.put('(',"ょ");
            map.put('0',"わ");map.put(')',"を");
            map.put('-',"ほ");map.put('_',"ほ");
            map.put('=',"へ");map.put('+',"へ");
            map.put('q',"た");map.put('Q',"た");
            map.put('w',"て");map.put('W',"て");
            map.put('e',"い");map.put('E',"ぃ");
            map.put('r',"す");map.put('R',"す");
            map.put('t',"か");map.put('T',"か");
            map.put('y',"ん");map.put('Y',"ん");
            map.put('u',"な");map.put('U',"な");
            map.put('i',"に");map.put('I',"に");
            map.put('o',"ら");map.put('O',"ら");
            map.put('p',"せ");map.put('P',"せ");
            map.put('[',"゛");map.put('{',"゛");
            map.put(']',"゜");map.put('}',"「");
            map.put('a',"ち");map.put('A',"ち");
            map.put('s',"と");map.put('S',"と");
            map.put('d',"し");map.put('D',"し");
            map.put('f',"は");map.put('F',"は");
            map.put('g',"き");map.put('G',"き");
            map.put('h',"く");map.put('H',"く");
            map.put('j',"ま");map.put('J',"ま");
            map.put('k',"の");map.put('K',"の");
            map.put('l',"り");map.put('L',"り");
            map.put(';',"れ");map.put(':',"れ");
            map.put('\'',"け");map.put('"',"け");
            map.put('`',"む");map.put('~',"」");
            map.put('z',"つ");map.put('Z',"っ");
            map.put('x',"さ");map.put('X',"さ");
            map.put('c',"そ");map.put('C',"そ");
            map.put('v',"ひ");map.put('V',"ひ");
            map.put('b',"こ");map.put('B',"こ");
            map.put('n',"み");map.put('N',"み");
            map.put('m',"も");map.put('M',"も");
            map.put(',',"ね");map.put('<',"、");
            map.put('.',"る");map.put('>',"。");
            map.put('/',"め");map.put('?',"・");
            map.put('\\',"ろ");map.put('|',"ー");
            return Collections.unmodifiableMap(map);
        }
    }
    static class Preferences{
        static int maxPhoneWidth=720;
        static int maxKeyboardWidth=900;
    }
}