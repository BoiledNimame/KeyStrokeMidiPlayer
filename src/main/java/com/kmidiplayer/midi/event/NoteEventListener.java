package com.kmidiplayer.midi.event;

import java.util.function.Consumer;

public class NoteEventListener implements INoteEventListener {

    private final Consumer<NoteEvent> consumer;

    public NoteEventListener(Consumer<NoteEvent> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void fire(NoteEvent event) {
        consumer.accept(event);
    }
}
