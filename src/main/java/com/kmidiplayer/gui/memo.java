package com.kmidiplayer.gui;

public interface memo {
    /**
     * 渡されたファイルをMidiファイルかチェックし、保持する。
     * 同時に、Midiファイルから必要データをコピーし保持することが望ましい
     *
     * 正常に保持した場合、もし既にデータがあれば破棄する
     * @param filePath このインスタンスへ渡すファイルのパス
     * @return         Midiファイルかどうか
     */
    boolean setAsMidiFile(String filePath);

    /**
     * @return ファイルから読み出した有効なデータが存在しているかどうか。
     */
    boolean hasFileData();

    /**
     * 保持しているデータを再生可能なキーストロークへ変換し、再生する。
     * トラックの分割-再結合もここで行う
     * @param tracks 再結合を行うトラック番号
     * @return       成功したかどうか
     */
    boolean convertAndPlay(String windowName, int initialDelay, int[] tracks);

    /**
     * 既に再生が行われていればその再生を停止する
     * @return
     */
    boolean stopPlayer();
}