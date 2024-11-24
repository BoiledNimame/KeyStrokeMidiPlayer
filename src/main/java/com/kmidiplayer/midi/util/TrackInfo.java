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
import com.kmidiplayer.util.ResourceLocation;

public class TrackInfo {

    final int NOTES_COUNT;
    public int getNotes() { return NOTES_COUNT; }
    final int PROGRAM_CHANGE;
    public int getProgramChange() { return PROGRAM_CHANGE; }

    public TrackInfo(Track track) {
        NOTES_COUNT = track.size();
        final HashMap<Integer, Integer> map = new HashMap<>(5);
        trackToList(track).stream()
            .map(MidiEvent::getMessage)
            .filter(p -> p instanceof ShortMessage)
            .map(m -> (ShortMessage) m)
            .filter(p -> p.getCommand() == ShortMessage.PROGRAM_CHANGE)
            .forEach(a -> setOrAddIfContains(map, a.getData1()));
        PROGRAM_CHANGE = map.entrySet().stream()
                            .filter(p -> p.getValue().equals(map.values()
                                .stream()
                                    .max(Integer::compareTo)
                                    .orElse(0)))
                            .mapToInt(Map.Entry::getKey)
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
        YamlLoader.loadAsMap(ResourceLocation.YAML_INSTRUMENTS.toFile()).entrySet().stream()
            .collect(Collectors.toMap((k) -> Integer.valueOf(k.getKey()), (v) -> v.getValue().toString(), (k1, k2) -> k1, HashMap::new));

    public static String getInstrumentFromProgramChange(int programChange) {
        return instruments.getOrDefault(programChange, "undefined");
    }
}
