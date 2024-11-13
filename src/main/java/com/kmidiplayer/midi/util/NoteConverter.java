package com.kmidiplayer.midi.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.config.Options;
import com.kmidiplayer.config.Options.Configs;
import com.kmidiplayer.midi.data.KeyCommand;

public class NoteConverter {

    private final static Logger LOGGER = LogManager.getLogger("[Converter]");
    private static final Configs config = Options.configs;

    /**
     * 再生したいトラックを全てキー操作の情報へ変換
     * @param targetTrackIndex 再生したいトラック番号の配列
     * @param sequence         再生対象のmidiファイルにおけるシーケンス
     * @param minNoteNumber    変換を行うノートの最小値
     * @param maxNoteNumber    変換を行うノートの最大値
     * @param noteNumberOffset 変換を行うノートの最大値と最小値にこの値を加える
     * @return                 変換されたキー操作情報の配列
     */
    public static KeyCommand[] convert(int[] targetTrackIndex, Sequence sequence, int minNoteNumber, int maxNoteNumber, int noteNumberOffset) {

        // 複数トラック統合のためにTrackからこちらのリストへ移す
        // Track内にあるArrayListに直接アクセスする手段はなく、かつListは継承していないためaddAllができない
        final ArrayList<MidiEvent> processingData = new ArrayList<>();

        // 調整用のログ出力のためのカウント
        int OverRangedNotes = 0;
        int LessRangedNotes = 0;
        final Map<Integer, Integer> outRangedNotes = new HashMap<>();

        for (int j : targetTrackIndex) {
            final Track processingTrack = sequence.getTracks()[j];

            // TracksはIterableでないためfor loopで処理
            for (int index = 0; index < processingTrack.size(); index++) {

                processingData.add(processingTrack.get(index));

                if (processingTrack.get(index).getMessage() instanceof ShortMessage) {

                    final ShortMessage msg = (ShortMessage) processingTrack.get(index).getMessage();

                    final int MessageType = msg.getCommand();

                    if (MessageType == ShortMessage.NOTE_ON || MessageType == ShortMessage.NOTE_OFF) {

                        if (maxNoteNumber < (msg).getData1() + noteNumberOffset) {
                            OverRangedNotes++;
                            setOrAddIfContains(outRangedNotes, (msg).getData1());
                        } else if ((msg).getData1() + noteNumberOffset < minNoteNumber) {
                            LessRangedNotes++;
                            setOrAddIfContains(outRangedNotes, (msg).getData1());
                        }
                    }
                }
            }
        }

        // 調整用に用いるためにconfigで定めた範囲から逸脱しているノートの数を示す.
        LOGGER.info("Less Notes: {}, Over Notes: {}", LessRangedNotes, OverRangedNotes);
        if (!outRangedNotes.isEmpty()) {
            LOGGER.info(
                "Details: {}",
                outRangedNotes.entrySet().stream()
                    .sorted(Comparator.comparingInt(Entry::getKey))
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (k1, k2) -> k1, HashMap::new))
            );
        }
        // 逸脱したノートが多ければログ出力
        if (10 < LessRangedNotes + OverRangedNotes) {
            LOGGER.info("If this number is too large, adjust the NoteNumberOffset.");
        }

        // MidiEventの仕様、ちょっとEvilすぎない？ (ShortMessageにキャストするとgetTickが使えない)
        return processingData.stream()
                             .filter(p -> p.getMessage() instanceof ShortMessage)
                             .filter(p -> ShortMessage.NOTE_ON == ((ShortMessage) p.getMessage()).getCommand() || ShortMessage.NOTE_OFF == ((ShortMessage) p.getMessage()).getCommand())
                             .map(m -> new KeyCommand(
                                           ShortMessage.NOTE_ON == ((ShortMessage) m.getMessage()).getCommand(),
                                           m.getTick(),
                                           convertNoteNumberToVkCode(((ShortMessage) m.getMessage()).getData1(), minNoteNumber, maxNoteNumber, noteNumberOffset),
                                           ((ShortMessage) m.getMessage()).getData1()))
                             .sorted(Comparator.comparing(KeyCommand::getTick)) // 複数トラックの場合順序がめちゃくちゃになる可能性があるのでソートする
                             .toArray(KeyCommand[]::new);
    }

    private static void setOrAddIfContains(Map<Integer, Integer> map, Integer key) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + (Integer) 1);
        } else {
            map.put(key, 1);
        }
    }

    /**
     * @param noteNumber 仮想キーコードとして取得したいノート番号の整数値
     * @return configの情報を基にノート番号-仮想キーコードの対応を決定し、返す
     */
    private static int convertNoteNumberToVkCode(int noteNumber, int minNoteNumber, int maxNoteNumber, int noteNumberOffset) {

        // configで設定したオフセット(調整用)を音階に加える
        final int buffedNoteNumber = noteNumber + noteNumberOffset;

        // 下限上限に当たった場合範囲の最低値最高値に合わせるかどうか
        // configで指定した音階の上限下限で制限
        if (maxNoteNumber < buffedNoteNumber || buffedNoteNumber < minNoteNumber){
            return 0xE; // VkCode:0xE~F のUnassigned(未割り当て)にする
        } else {
            // ここまで来る間に範囲外ノートが何故か通過してしまった場合に備える
            return noteNumberToVkCode(buffedNoteNumber, minNoteNumber, maxNoteNumber);
        }

    }

    // なんとかして範囲外になるやつを抹消しようとしてた記憶がある
    private static int noteNumberToVkCode(int note, int min, int max) {
        // configに直接仮想キーコードを記述するかのオプション
        if (validNoteNumber(note, config.isDebug())) {
            if(!config.isUsingVkCode()){
                return VkCodeMap.GetVKcode(config.getKeyMap().get(Integer.toString(note)));
            } else {
                return Integer.parseInt(config.getKeyMap().get(Integer.toString(note)));
            }
        } else {
            int nearest = min;
            int[] gap = config.getKeyMap().keySet().stream().mapToInt(m -> Integer.parseInt(m) - note).toArray();

            final int negativeGap = Arrays.stream(gap).filter(p -> p < 0).map(Math::abs).min().orElse(min);
            final int positiveGap = Arrays.stream(gap).filter(p -> 0 < p).min().orElse(max);

            if (config.getKeyMap().containsKey(Integer.toString(note-negativeGap))) {
                nearest = note - negativeGap;
            } else if (config.getKeyMap().containsKey(Integer.toString(note+positiveGap))) {
                nearest = note + positiveGap;
            }
            return VkCodeMap.GetVKcode(config.getKeyMap().get(Integer.toString(nearest)));
        }
    }

    private static boolean validNoteNumber(int noteNumber, boolean logging) {
        if (!config.getKeyMap().containsKey(Integer.toString(noteNumber))) {
            if (logging) {
                LOGGER.info("required note's value (key:{}) is not exist on keymap.yaml !", noteNumber); }
            return false;
        }
        return true;
    }
}
