package com.kmidiplayer.lang;

import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import com.kmidiplayer.config.Options;
import com.kmidiplayer.util.ResourceLocation;

public enum I18n {

    // メインウィンドウのタイトル
    TITLE("title"),

    // ドラッグ&ドロップするエリアに表示するテキストの1行目と2行目
    DRAGDROP_LABEL_1("Text_Drop_1"),
    DRAGDROP_LABEL_2("Text_Drop_2"),

    // 入力ファイルの絶対パスが出たり書いたり選択できたりするやつ
    COMBOBOX_PATH("ComboBox_Path"),

    // 入力ファイルのパスを入力するテキストフィールドの内容をリセットするボタン
    BUTTON_RESET("Button_Reset"),

    // 再生ボタン
    BUTTON_PLAY("Button_Play"),

    // 停止ボタン
    BUTTON_STOP("Button_Stop"),

    // 入力プレビューを表示するウィンドウを出すボタン
    BUTTON_PREV("Button_Prev"),

    // 再生遅延の入力フィールド
    TEXTFIELD_INITIAL_DELAY("Text_Field_Initial_Delay"),

    // 入力先ウィンドウタイトルの入力フィールド
    TEXTFIELD_WINDOW_NAME("Text_Field_Window_Title"),

    // 音階オフセットの入力フィールド
    TEXTFIELD_NOTE_OFFSET("Text_Field_NOTE_OFFSET"),

    // トラック情報を含むトラックボタンのホルダーのテキスト
    LABEL_TRACKS("Text_Tracks");

    // この辺からコンストラクタなど

    public final String key;

    I18n(String translationKey) {
        key = translationKey;
    }

    // 言語ファイルのmap
    private static final Map<String, String> i18n =
        ResourceLocation.YAML_LANGUAGE.getYamlAsMap((x) -> x.getValue().toString());

    /**
     * このアプリケーションが稼働しているロケールの言語(default)から値を呼び出すメソッド
     * @return その列挙型が持つキー(key)と現在のロケールの言語の組み合わせが持つ値を返す
     */
    public String getDefault() {
        return safeGet(
            i18n,
            key.concat(".").concat(Options.configs.getLanguage())
        );
    }

    private <K, V> V safeGet(Map<K, V> map, K key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            throw new NoSuchElementException(map.toString().concat(", key:").concat(key.toString()));
        }
    }

    // util
    public static String getDefaultLocaleLanguage() {
        return Locale.getDefault().getLanguage();
    }
}