package de.tr7zw.tas.duck;

import java.io.IOException;

import de.tr7zw.tas.Recorder;

public interface TASGuiContainer {
    void setRecorder(Recorder newRecorder);
    void callMouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException;
    void callKeyPressed(char typedChar, int keyCode) throws IOException;
    void callMouseClickMoved(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick);
    void callMouseReleased(int mouseX, int mouseY, int state);
}
