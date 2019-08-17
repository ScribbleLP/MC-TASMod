package de.tr7zw.tas.duck;

import de.tr7zw.tas.PlaybackMethod;
import de.tr7zw.tas.Recorder;

public interface PlaybackInput {
    PlaybackMethod getPlayback();

    void setPlayback(PlaybackMethod newPlayback);

    void setRecorder(Recorder recorder);
}
