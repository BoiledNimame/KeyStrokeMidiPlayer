package com.kmidiplayer.midi.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
import com.kmidiplayer.keylogger.VkCodeMap;
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
    public static KeyCommand[] convert(int[] targetTrackIndex, Sequence sequence, List<Integer> definedNotes, int noteNumberOffset) {

        // 複数トラック統合のためにTrackからこちらのリストへ移す
        // Track内にあるArrayListに直接アクセスする手段はなく、かつListは継承していないためaddAllができない
        final ArrayList<MidiEvent> processingData = new ArrayList<>();

        // 調整用のログ出力のためのカウント
        int overRangedNotes = 0;
        int lessRangedNotes = 0;
        final int noteRangeMax = definedNotes.stream().mapToInt(x -> x).max().getAsInt();
        final int noteRangeMin = definedNotes.stream().mapToInt(x -> x).min().getAsInt();
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

                        if (definedNotes.contains(msg.getData1() + noteNumberOffset)) {

                            setOrAddIfContains(outRangedNotes, (msg).getData1());

                            if (noteRangeMax < (msg).getData1() + noteNumberOffset) {
                                overRangedNotes++;
                            } else if ((msg).getData1() + noteNumberOffset < noteRangeMin) {
                                lessRangedNotes++;
                            }
                        }
                    }
                }
            }
        }

        // 調整用に用いるためにconfigで定めた範囲から逸脱しているノートの数を示す.
        LOGGER.info("Less Notes: {}, Over Notes: {}", lessRangedNotes, overRangedNotes);
        if (!outRangedNotes.isEmpty()) {
            LOGGER.info(
                "Details: {}",
                outRangedNotes.entrySet().stream()
                    .sorted(Comparator.comparingInt(Entry::getKey))
                    .collect(Collectors.toList())
            );
        }
        // 逸脱したノートが多ければログ出力
        if (10 < lessRangedNotes + overRangedNotes) {
            LOGGER.info("If this number is too large, adjust the NoteNumberOffset.");
        }

        // MidiEventの仕様、ちょっとEvilすぎない？ (ShortMessageにキャストするとgetTickが使えない)
        return processingData.stream()
                             .filter(p -> p.getMessage() instanceof ShortMessage)
                             .filter(p -> ShortMessage.NOTE_ON == ((ShortMessage) p.getMessage()).getCommand() || ShortMessage.NOTE_OFF == ((ShortMessage) p.getMessage()).getCommand())
                             .map(m -> new KeyCommand(
                                           ShortMessage.NOTE_ON == ((ShortMessage) m.getMessage()).getCommand(),
                                           m.getTick(),
                                           convertNoteNumberToVkCode(((ShortMessage) m.getMessage()).getData1(), definedNotes, noteNumberOffset),
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
    private static int convertNoteNumberToVkCode(int noteNumber, List<Integer> definedNotes, int noteNumberOffset) {

        // configで設定したオフセット(調整用)を音階に加える
        final int buffedNoteNumber = noteNumber + noteNumberOffset;

        // configで指定した音階かどうか
        if (definedNotes.contains(buffedNoteNumber)){
            return noteNumberToVkCode(buffedNoteNumber);
        } else {
            return 0xE; // VkCode:0xE~F のUnassigned(未割り当て)にする
        }

    }

    private static int noteNumberToVkCode(int note) {
        if(!config.isUsingVkCode()){
            return VkCodeMap.GetVKcode(config.getKeyMap().get(Integer.toString(note)));
        } else {
            return Integer.parseInt(config.getKeyMap().get(Integer.toString(note)));
        }
    }
}
