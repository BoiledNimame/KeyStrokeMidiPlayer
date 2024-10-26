package com.kmidiplayer.midi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import com.kmidiplayer.config.YamlLoader;
import com.kmidiplayer.util.Pair;
import com.kmidiplayer.util.Resource;

public class TrackInfo {

    final int NOTES_COUNT;
    public int getNotes() { return NOTES_COUNT; }
    final int PROGRAM_CHANGE;
    public int getProgramChange() { return PROGRAM_CHANGE; }

    public TrackInfo(Track track) {
        NOTES_COUNT = track.size();
        final HashMap<Integer, Integer> map = new HashMap<>(5);
        trackToList(track).stream()
            .map(m -> m.getMessage())
            .filter(p -> p instanceof ShortMessage)
            .map(m -> (ShortMessage) m)
            .filter(p -> p.getCommand() == ShortMessage.PROGRAM_CHANGE)
            .forEach(a -> setOrAddIfContains(map, a.getData1()));
        PROGRAM_CHANGE = map.entrySet().stream()
                            .filter(p -> p.getValue()==map.entrySet()
                                .stream()
                                    .map(m -> m.getValue())
                                    .max(Integer::compareTo)
                                    .orElse(0))
                            .mapToInt(p -> p.getKey())
                            .findFirst().orElse(0);

    }

    private static void setOrAddIfContains(Map<Integer, Integer> map, Integer key) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + (Integer) 1);
        } else {
            map.put(key, 1);
        }
    }

    private static List<MidiEvent> trackToList(Track track) {
        if (track.size()!=0) {
            final List<MidiEvent> events = new ArrayList<>(track.size());
            for (int i = 0; i < track.size(); i++) {
                events.add(track.get(i));
            }
            return events;
        } else {
            return new ArrayList<>(){};
        }
    }

    private final static Map<Integer, String> instruments =
        YamlLoader.loadAsMap(Resource.getFileAbsolutePathAsString(TrackInfo.class, "instruments.yaml"))
        .entrySet().stream()
            .map(m -> new Pair<>(Integer.parseInt(m.getKey().toString()), m.getValue().toString()))
            .collect(Collectors.toMap(Pair::getTag, Pair::getValue, (k1, k2) -> k1, HashMap::new));

    public static String getInstrumentFromProgramChange(int programChange) {
        return instruments.containsKey(programChange) ? instruments.get(programChange) : "undefined";
    }
}
