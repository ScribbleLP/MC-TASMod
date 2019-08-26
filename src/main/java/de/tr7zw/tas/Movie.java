package de.tr7zw.tas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Movie {
    public String location;
    public List<KeyFrame> frames;

    public void write(File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        writer.write(location);
        writer.write('\n');

        for (KeyFrame frame: frames) {
            writer.write(frame.pack());
            writer.write('\n');
        }

        writer.close();
    }

    public static Movie read(File file) throws IOException {
        Movie mov = new Movie();
        List<KeyFrame> frames = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        mov.location = reader.readLine();
        reader.lines().forEach((line) -> frames.add(KeyFrame.unpack(line)));
        mov.frames = frames;
        return mov;
    }
}
