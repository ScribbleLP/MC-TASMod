package de.tr7zw.tas.duck;

import de.tr7zw.tas.PlaybackMethod;

public interface PlaybackInput {
    PlaybackMethod getPlayback();

    void setPlayback(PlaybackMethod newPlayback);
}
