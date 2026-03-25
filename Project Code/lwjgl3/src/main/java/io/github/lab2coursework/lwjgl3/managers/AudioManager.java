package io.github.lab2coursework.lwjgl3.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private final Map<String, Sound> sounds;
    private final Map<String, Music> music;
    private float masterVolume = 1.0f;

    public AudioManager() {
        this.sounds = new HashMap<>();
        this.music = new HashMap<>();
    }

    public void loadSound(String key, String path) {
        sounds.put(key, Gdx.audio.newSound(Gdx.files.internal(path)));
    }

    public void loadMusic(String key, String path) {
        music.put(key, Gdx.audio.newMusic(Gdx.files.internal(path)));
    }

    public void playSound(String key) {
        if (sounds.containsKey(key)) {
            sounds.get(key).play(masterVolume);
        }
    }

    public void playMusic(String key, boolean loop) {
        if (music.containsKey(key)) {
            Music track = music.get(key);
            track.setLooping(loop);
            track.setVolume(masterVolume);
            track.play();
        }
    }

    public void stopMusic(String key) {
        if (music.containsKey(key)) {
            music.get(key).stop();
        }
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = volume;
    }

    public void dispose() {
        sounds.values().forEach(Sound::dispose);
        music.values().forEach(Music::dispose);
    }
}
