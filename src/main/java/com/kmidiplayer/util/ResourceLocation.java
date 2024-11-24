package com.kmidiplayer.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * ユーザーが編集可能であるべきリソースファイルの位置を記したもの.
 */
public enum ResourceLocation {

    // 仮想キーコードとそれを示す文字列表現の対応表
    YAML_VKCODE("./data", "vkcode.yaml"),

    // PROGRAM_CHANGEと(一般的には)それに対応する楽器の対応表
    YAML_INSTRUMENTS("./data", "instruments.yaml"),

    // 言語ごとのUIファイル(??????)
    YAML_LANGUAGE("./data", "lang.yaml"),

    // このアプリケーションに黒系テーマを適用するファイル.
    CSS_CUSTOM("./data", "view.css"),

    // このアプリケーションのテキストフィールドにおいて、無効な状態を示すスタイルが記述されるファイル.
    CSS_INVALID("./data", "invalid.css"),

    // mfxに含まれるデフォルトのスタイルが記述されているファイル.
    CSS_DEFAULT("./data", "default.css"),

    // キャッシュファイル
    CACHE("./data", "cache");

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
}
