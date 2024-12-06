/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #6
 * 1 - 5026231084 - Azhar Aditya Pratama
 * 2 - 5026231109 - Abdul Ghoni
 * 3 - 5026231200 - Cristo Pison Ben Jarred
 */

package org.example;

import javax.sound.sampled.*;

public class AudioManager {
    private Clip backgroundMusic;
    private Clip correctSound;
    private Clip wrongSound;
    private Clip winSound;
    private boolean isMusicPlaying = false;

    public AudioManager() {
        loadAudio();
    }

    private void loadAudio() {
        try {
            String basePath = "/audio/";

            AudioInputStream musicStream = AudioSystem.getAudioInputStream(
                    getClass().getResourceAsStream(basePath + "background.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(musicStream);

            AudioInputStream correctStream = AudioSystem.getAudioInputStream(
                    getClass().getResourceAsStream(basePath + "correct.wav"));
            correctSound = AudioSystem.getClip();
            correctSound.open(correctStream);

            AudioInputStream wrongStream = AudioSystem.getAudioInputStream(
                    getClass().getResourceAsStream(basePath + "wrong.wav"));
            wrongSound = AudioSystem.getClip();
            wrongSound.open(wrongStream);

            AudioInputStream winStream = AudioSystem.getAudioInputStream(
                    getClass().getResourceAsStream(basePath + "win.wav"));
            winSound = AudioSystem.getClip();
            winSound.open(winStream);

        } catch (Exception e) {
            System.out.println("Error loading audio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void playBackgroundMusic() {
        if (backgroundMusic != null && !isMusicPlaying) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            isMusicPlaying = true;
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null && isMusicPlaying) {
            backgroundMusic.stop();
            isMusicPlaying = false;
        }
    }

    public void playCorrectSound() {
        if (correctSound != null) {
            correctSound.setFramePosition(0);
            correctSound.start();
        }
    }

    public void playWrongSound() {
        if (wrongSound != null) {
            wrongSound.setFramePosition(0);
            wrongSound.start();
        }
    }

    public void playWinSound() {
        if (winSound != null) {
            winSound.setFramePosition(0);
            winSound.start();
        }
    }

    public void cleanup() {
        if (backgroundMusic != null) backgroundMusic.close();
        if (correctSound != null) correctSound.close();
        if (wrongSound != null) wrongSound.close();
        if (winSound != null) winSound.close();
    }
}
