package com.thecubecast.reengine.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundManager {

    private float MasterVolume = 1.0f;
    private float MusicVolume = 0.4f;
    private float SoundVolume = 0.8f;

    //Define all sound objects
    Sound Click;

    //Define all your music objects
    private List<Music> HandledMusic = new ArrayList<>();
    private List<Sound> HandledSound = new ArrayList<>();

    Map<String, Long> SoundIds = new HashMap<String, Long>();

    public void init() { //Create the folders that hold everything neatly

        if (Gdx.app.getPreferences("properties").contains("MasterVolume")) {
            MasterVolume = Gdx.app.getPreferences("properties").getFloat("MasterVolume");
        } else {
            Gdx.app.getPreferences("properties").putFloat("MasterVolume", MasterVolume);
        }

        if (Gdx.app.getPreferences("properties").contains("MusicVolume")) {
            MusicVolume = Gdx.app.getPreferences("properties").getFloat("MusicVolume");
        } else {
            Gdx.app.getPreferences("properties").putFloat("MusicVolume", MusicVolume);
        }

        if (Gdx.app.getPreferences("properties").contains("SoundVolume")) {
            SoundVolume = Gdx.app.getPreferences("properties").getFloat("SoundVolume");
        } else {
            Gdx.app.getPreferences("properties").putFloat("SoundVolume", SoundVolume);
        }

        Gdx.app.getPreferences("properties").flush();

        Click = Gdx.audio.newSound(Gdx.files.internal("Music/Sound/menu-clik.wav"));

    }

    public void update() {
        for (int i = 0; i < HandledMusic.size(); i++) {
            if (HandledMusic.get(i).getVolume() != MusicVolume*MasterVolume) {
                Vector2 temp = new Vector2(HandledMusic.get(i).getVolume(), 0).interpolate(new Vector2(MusicVolume * MasterVolume, 0), 0.15f, Interpolation.exp5In);
                HandledMusic.get(i).setVolume(temp.x);
            }


        }

    }

    public void playS(String soundName) {
        Sound temp = Gdx.audio.newSound(Gdx.files.internal("Music/Sound/" + soundName));
        HandledSound.add(temp);
        SoundIds.put(soundName, temp.play(1));
    }

    public void play(String soundName) {
        if (soundName.equals("Click")) {
            long temp = Click.play(SoundVolume * MasterVolume);
            SoundIds.put(soundName, temp);
        }
    }

    public void pause(String soundName) {
        if (soundName.equals("Click")) {
            long temp = SoundIds.get(soundName);
            Click.pause(temp);
        }
    }

    public void stop(String soundName) {
        if (soundName.equals("Click")) {
            long temp = SoundIds.get(soundName);
            Click.stop(temp);
        }
    }

    public int playMusic(String Music, boolean looping) {
        Music temp = Gdx.audio.newMusic(Gdx.files.internal("Music/" + Music));
        HandledMusic.add(temp);
        temp.play();
        temp.setVolume(MusicVolume * MasterVolume);
        if (looping) {
            temp.setLooping(looping);
        }
        return HandledMusic.size() - 1;
    }

    public Music playMusic(String Music, boolean looping, boolean FadeIn) {
        Music temp = Gdx.audio.newMusic(Gdx.files.internal("Music/" + Music));
        HandledMusic.add(temp);
        temp.play();
        if (FadeIn)
            temp.setVolume(0);
        else
            temp.setVolume(MusicVolume * MasterVolume);
        if (looping) {
            temp.setLooping(looping);
        }
        return HandledMusic.get(HandledMusic.size()-1);
    }

    public boolean isPlaying(int MusicID) {
        if (HandledMusic.get(MusicID).isPlaying()) {
            return true;
        } else {
            return false;
        }
    }

    public void pauseMusic(int MusicID) {
        HandledMusic.get(MusicID).pause();
    }

    public void stopMusic(Music MusicID) {
        HandledMusic.remove(MusicID);
        HandledMusic.remove(MusicID);
    }

    public float getMasterVolume() {
        return MasterVolume;
    }

    public void setMasterVolume(float masterVolume) {
        if (Gdx.app.getPreferences("properties").contains("MasterVolume")) {
            Gdx.app.getPreferences("properties").putFloat("MasterVolume", masterVolume);
            Gdx.app.getPreferences("properties").flush();
        }
        MasterVolume = masterVolume;
    }

    public float getMusicVolume() {
        return MusicVolume;
    }

    public void setMusicVolume(float musicVolume) {
        if (Gdx.app.getPreferences("properties").contains("MusicVolume")) {
            Gdx.app.getPreferences("properties").putFloat("MusicVolume", musicVolume);
            Gdx.app.getPreferences("properties").flush();
        }
        MusicVolume = musicVolume;
    }

    public float getSoundVolume() {
        return SoundVolume;
    }

    public void setSoundVolume(float soundVolume) {
        if (Gdx.app.getPreferences("properties").contains("SoundVolume")) {
            Gdx.app.getPreferences("properties").putFloat("SoundVolume", soundVolume);
            Gdx.app.getPreferences("properties").flush();
        }
        SoundVolume = soundVolume;
    }
}