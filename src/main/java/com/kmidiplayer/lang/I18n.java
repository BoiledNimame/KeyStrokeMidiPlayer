package com.kmidiplayer.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.kmidiplayer.config.Options;
import com.kmidiplayer.config.YamlLoader;
import com.kmidiplayer.util.ResourceLocation;

public enum I18n {

    // メインウィンドウのタイトル
    TITLE("title"),

    //
    ;

    // この辺からコンストラクタなど

    public final String key;

    I18n(String translationKey) {
        key = translationKey;
    }

    // 言語ファイルのmap
    private static final Map<String, String> i18n =
        YamlLoader.loadAsMap(ResourceLocation.YAML_LANGUAGE.toFile()).entrySet().stream()
            .collect(Collectors.toMap(Entry::getKey, (x) -> x.getValue().toString(), (k1, k2) -> k1, HashMap::new));

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