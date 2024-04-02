package com.kmidiplayer.midi;

import java.util.List;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import com.kmidiplayer.gui.Gui;
import com.kmidiplayer.gui.PrimaryController;

public class midiData {
    private final List<long[]> playableKeyArr;
    private final Sequence sequence;

    public midiData(String midiDirectory) {
        sequence = midiLoader.getSequencefromDirectory(midiDirectory);
        final List<MidiEvent> rawEvent = midiLoader.convertSequenceToMidiEvent(sequence);
        playableKeyArr = midiLoader.convertRawKeys(rawEvent);
        
        // これ自体はFx Thread以外から呼ばれることしか意図していない
        PrimaryController.IsFileLoadSucsessSetter(true);
    }

    public List<long[]> getplayableKeyArr() {
        return playableKeyArr;
    }

    public double getTickInMilliSeconds() {
        double tempoBPM = 120D; // デフォルト値
        
        GET_TEMPO : {
            for (Track track : sequence.getTracks()) {
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    MidiMessage message = event.getMessage();
                    if (message instanceof MetaMessage) {
                        MetaMessage meta = (MetaMessage) message;
    
                        final int TEMPO_MESSAGE = 0x51;
                        if (meta.getType() == TEMPO_MESSAGE) {
                            // テンポを算出
                            byte[] data = meta.getData();
                            int tempo = (data[0] & 0xFF) << 16 | (data[1] & 0xFF) << 8 | (data[2] & 0xFF);
                            tempoBPM = 60000000D / tempo;
                            break GET_TEMPO;
                        }
                    }
                }
            }
        }

        // 1tickの秒数計算
        final double tickInMilliSeconds =  60D / (sequence.getResolution() * tempoBPM);
        Gui.logger().info("1 tick = " + tickInMilliSeconds + " millisecond");
        
        return tickInMilliSeconds;
    }
}
