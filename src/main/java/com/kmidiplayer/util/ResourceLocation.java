package com.kmidiplayer.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.kmidiplayer.config.YamlLoader;

/**
 * ユーザーが編集可能であるべきリソースファイルの位置を記したもの.
 */
public enum ResourceLocation {

    // コンフィグファイル
    YAML_CONFIG("./", "config.yaml"),

    // ユーザーが定義するノート番号とキーの対応表
    YAML_KEYMAP("./", "keymap.yaml"),

    // 仮想キーコードとそれを示す文字列表現の対応表
    YAML_VKCODE("./data", "vkcode.yaml"),

    // PROGRAM_CHANGEと(一般的には)それに対応する楽器の対応表
    YAML_INSTRUMENTS("./data", "instruments.yaml"),

    // 言語ごとのUIファイル(??????)
    YAML_LANGUAGE("./data", "lang.yaml"),

    // このアプリケーションに黒系テーマを適用するファイル.
    CSS_CUSTOM("./data", "view.css"),

    // mfxに含まれるデフォルトのスタイルが記述されているファイル.
    CSS_DEFAULT("./data/generated", "default.css"),

    // キャッシュファイル
    CACHE("./data/generated", "cache");

    private final String location;
    private final String fileName;

    ResourceLocation(String location, String fileName) {
        this.location = location;
        this.fileName = fileName;
    }

    public File toFile() {
        return new File(location, fileName);
    }

    public URL toURL() {
        try {
            return (new File(location, fileName)).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> getYamlAsMap() {
        if (isYaml(this)) {
            return YamlLoader.loadAsMap(toFile());
        } else {
            throw new IllegalArgumentException("this file is not Yaml file !");
        }
    }

    public <V> Map<String, V> getYamlAsMap(Function<Entry<String, Object>, V> valueMapper) {
        return getYamlAsMap().entrySet().stream().collect(Collectors.toMap(Entry::getKey, valueMapper));
    }

    public <K, V> Map<K, V> getYamlAsMap(Function<Entry<String, Object>, K> keyMapper, Function<Entry<String, Object>, V> valueMapper) {
        return getYamlAsMap().entrySet().stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    private static final String EXTENSION_STRING = "\\.";

    private static boolean isYaml(ResourceLocation loc) {
        final String[] splitedFileName = loc.fileName.split(EXTENSION_STRING);
        return "yaml".equals(splitedFileName[splitedFileName.length - 1]) || "yml".equals(splitedFileName[splitedFileName.length - 1]);
    }
}
