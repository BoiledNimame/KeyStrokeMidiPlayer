package com.kmidiplayer.midi.integrated;

import java.io.File;
import java.util.List;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import com.kmidiplayer.application.UI;
import com.kmidiplayer.gui.PrimaryModel;

public class MidiData {
    private final List<long[]> playableKeyArr;
    private final Sequence sequence;

    public MidiData(PrimaryModel model, File midiFile) {
        sequence = MidiLoader.getSequencefromDirectory(model, midiFile);
        final List<MidiEvent> rawEvent = MidiLoader.convertSequenceToMidiEvent(sequence);
        playableKeyArr = MidiLoader.convertRawKeys(rawEvent);
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
        UI.logger().info("1 tick = " + tickInMilliSeconds + " millisecond");
        
        return tickInMilliSeconds;
    }
}
