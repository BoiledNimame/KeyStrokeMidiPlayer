package com.kmidiplayer.midi.event;

import java.util.EventListener;

public interface INoteEventListener extends EventListener {
    public void fire(NoteEvent event);
}
