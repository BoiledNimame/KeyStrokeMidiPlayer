package com.kmidiplayer.midi.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.ArrayList;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kmidiplayer.config.ConfigHolder;
import com.kmidiplayer.keylogger.VkCodeMap;
import com.kmidiplayer.midi.data.KeyCommand;

public class NoteConverter {

    private final static Logger LOGGER = LogManager.getLogger("[Converter]");
    private static final ConfigHolder config = ConfigHolder.instance();

    /**
     * 再生したいトラックを全てキー操作の情報へ変換
     * @param trackIndex 再生したいトラック番号の配列
     * @param sequence   再生対象のmidiファイルにおけるシーケンス
     * @return 変換されたキー操作情報の配列
     */
    public static KeyCommand[] convert(int[] trackIndex, Sequence sequence) {

        // 複数トラック統合のためにTrackからこちらのリストへ移す
        // Track内にあるArrayListに直接アクセスする手段はなく、かつListは継承していないためaddAllができない
        final ArrayList<MidiEvent> processingData = new ArrayList<>();

        // 調整用のログ出力のためのカウント
        int OverRangedNotes = 0;
        int LessRangedNotes = 0;

        for (int i = 0; i < trackIndex.length; i++) {
            final Track processingTrack = sequence.getTracks()[trackIndex[i]];

            for (int index = 0; index < processingTrack.size(); index++) {

                processingData.add(processingTrack.get(index));

                if (processingTrack.get(index).getMessage() instanceof ShortMessage) {

                    final ShortMessage msg = (ShortMessage) processingTrack.get(index).getMessage();

                    final int MessageType = (msg).getCommand();

                    if (MessageType==ShortMessage.NOTE_ON || MessageType==ShortMessage.NOTE_OFF) {

                        if (config.getMaxNote() < (msg).getData1()) {
                            OverRangedNotes++;
                        } else if ((msg).getData1() < config.getMinNote()) {
                            LessRangedNotes++;
                        }
                    }
                }
            }
        }

        // 調整用に用いるためにconfigで定めた範囲から逸脱しているノートの数を示す.
        LOGGER.info("Less Notes:" + LessRangedNotes + ", Over Notes:" + OverRangedNotes);
        if (LessRangedNotes < 1 && OverRangedNotes < 1) {
            LOGGER.info("If this number is too large, adjust the generalsettings.json::NoteNumberOffset.");
        }


        final KeyCommand[] result = new KeyCommand[processingData.size()];

        for (int index = 0; index < processingData.size(); index++) {

            if (processingData.get(index).getMessage() instanceof ShortMessage) {

                final ShortMessage msg = ((ShortMessage) processingData.get(index).getMessage());

                if (ShortMessage.NOTE_ON == msg.getCommand() || ShortMessage.NOTE_OFF == msg.getCommand()) {
                    result[index] = new KeyCommand(
                        ShortMessage.NOTE_ON == msg.getCommand(),
                        processingData.get(index).getTick(),
                        convertNoteToVkCode(msg.getData1()));
                }
            }
        }

        // 複数トラックの場合順序がめちゃくちゃになる可能性があるのでソートする
        return Arrays.stream(result)
                     .filter(item -> item!=null)
                     .sorted(Comparator.comparing(KeyCommand::getTick))
                     .toArray(KeyCommand[]::new);
    }

    /**
     * @param noteNumber 仮想キーコードとして取得したいノート番号の整数値
     * @return configの情報を基にノート番号-仮想キーコードの対応を決定し、返す
     */
    private static int convertNoteToVkCode(int noteNumber) {

        // configで設定したオフセット(調整用)を音階に加える
        final int buffedNoteNumber = noteNumber + config.getNoteOffset();

        // 下限上限に当たった場合範囲の最低値最高値に合わせるかどうか
        if (config.isCopyNearestNote()){

            // configで指定した音階の上限下限で制限
            if (buffedNoteNumber > config.getMaxNote()){
                return config.getMaxNote();
            } else if (config.getMinNote() > buffedNoteNumber){
                return config.getMinNote();
            } else {

                // configに直接仮想キーコードを記述するかのオプション
                if(!config.isUsingVkCode()){
                    return VkCodeMap.GetVKcode(config.getKeyMap().get(Integer.toString(buffedNoteNumber)));
                } else {
                    return Integer.parseInt(config.getKeyMap().get(Integer.toString(buffedNoteNumber)));
                }

            }

        } else {

            // configに直接仮想キーコードを記述するかのオプション
            if(!config.isUsingVkCode()){
                return VkCodeMap.GetVKcode(config.getKeyMap().get(Integer.toString(buffedNoteNumber)));
            } else {
                return Integer.parseInt(config.getKeyMap().get(Integer.toString(buffedNoteNumber)));
            }
        }
    }

}
