package com.kmidiplayer.midi.util;

import java.util.*;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.config.ConfigHolder;
import com.kmidiplayer.config.ConfigHolder.Configs;
import com.kmidiplayer.keylogger.VkCodeMap;
import com.kmidiplayer.midi.data.KeyCommand;

public class NoteConverter {

    private final static Logger LOGGER = LogManager.getLogger("[Converter]");
    private static final Configs config = ConfigHolder.configs;

    /**
     * 再生したいトラックを全てキー操作の情報へ変換
     * @param trackIndex 再生したいトラック番号の配列
     * @param sequence   再生対象のmidiファイルにおけるシーケンス
     * @return 変換されたキー操作情報の配列
     */
    public static KeyCommand[] convert(int[] trackIndex, Sequence sequence, int offset) {

        // 複数トラック統合のためにTrackからこちらのリストへ移す
        // Track内にあるArrayListに直接アクセスする手段はなく、かつListは継承していないためaddAllができない
        final ArrayList<MidiEvent> processingData = new ArrayList<>();

        // 調整用のログ出力のためのカウント
        int OverRangedNotes = 0;
        int LessRangedNotes = 0;
        Map<Integer, Integer> outRangedNotes = new HashMap<>();

        final int minNote = config.getKeyMap().keySet().stream().mapToInt(Integer::parseInt).min().orElse(0);
        final int maxNote = config.getKeyMap().keySet().stream().mapToInt(Integer::parseInt).max().orElse(0);

        for (int j : trackIndex) {
            final Track processingTrack = sequence.getTracks()[j];

            for (int index = 0; index < processingTrack.size(); index++) {

                processingData.add(processingTrack.get(index));

                if (processingTrack.get(index).getMessage() instanceof ShortMessage) {

                    final ShortMessage msg = (ShortMessage) processingTrack.get(index).getMessage();

                    final int MessageType = (msg).getCommand();

                    if (MessageType == ShortMessage.NOTE_ON || MessageType == ShortMessage.NOTE_OFF) {

                        if (maxNote < (msg).getData1() + offset) {
                            OverRangedNotes++;
                            setOrAddIfContains(outRangedNotes, (msg).getData1());
                        } else if ((msg).getData1() + offset < minNote) {
                            LessRangedNotes++;
                            setOrAddIfContains(outRangedNotes, (msg).getData1());
                        }
                    }
                }
            }
        }

        // 調整用に用いるためにconfigで定めた範囲から逸脱しているノートの数を示す.
        LOGGER.info("Less Notes:{}, Over Notes:{}", LessRangedNotes, OverRangedNotes);
        if (!outRangedNotes.isEmpty()) {
            LOGGER.info("Details:{}", outRangedNotes);
        }
        if (5 < LessRangedNotes || 5 < OverRangedNotes) {
            LOGGER.info("If this number is too large, adjust the config.yaml:NoteNumberOffset.");
        }

        final KeyCommand[] result = new KeyCommand[processingData.size()];

        for (int index = 0; index < processingData.size(); index++) {

            if (processingData.get(index).getMessage() instanceof ShortMessage) {

                final ShortMessage msg = ((ShortMessage) processingData.get(index).getMessage());

                if (ShortMessage.NOTE_ON == msg.getCommand() || ShortMessage.NOTE_OFF == msg.getCommand()) {
                    result[index] = new KeyCommand(
                        ShortMessage.NOTE_ON == msg.getCommand(),
                        processingData.get(index).getTick(),
                        convertNoteToVkCode(msg.getData1(), offset));
                }
            }
        }

        // 複数トラックの場合順序がめちゃくちゃになる可能性があるのでソートする
        return Arrays.stream(result)
                     .filter(Objects::nonNull)
                     .sorted(Comparator.comparing(KeyCommand::getTick))
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
    private static int convertNoteToVkCode(int noteNumber, int offset) {

        final int minNote = config.getKeyMap().keySet().stream().mapToInt(Integer::parseInt).min().orElse(0);
        final int maxNote = config.getKeyMap().keySet().stream().mapToInt(Integer::parseInt).max().orElse(0);

        // configで設定したオフセット(調整用)を音階に加える
        final int buffedNoteNumber = noteNumber + offset;

        // 下限上限に当たった場合範囲の最低値最高値に合わせるかどうか
        if (config.isCopyNearestNote()){

            // configで指定した音階の上限下限で制限
            if (buffedNoteNumber > maxNote){
                return maxNote;
            } else if (minNote > buffedNoteNumber){
                return minNote;
            } else {
                return noteToVkCode(buffedNoteNumber, minNote, maxNote);
            }
        } else {
            return noteToVkCode(buffedNoteNumber, minNote, maxNote);

        }
    }

    private static int noteToVkCode(int note, int min, int max) {
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
