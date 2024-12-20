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

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * This enum encapsulates all the sound effects of a game.
 */
public enum SoundEffect {
   EAT_FOOD("audio/correct.wav"),
   BACKGORUND("audio/background.wav"),
   DIE("audio/win.wav"),
   LOSE_AI("audio/lose.wav");

   /** Nested enumeration for specifying volume */
   public static enum Volume {
      MUTE, LOW, MEDIUM, HIGH
   }

   public static Volume volume = Volume.LOW;

   /** Each sound effect has its own clip, loaded with its own sound file. */
   private Clip clip;

   /**
    * Private Constructor to construct each element of the enum with its own sound file.
    */
   private SoundEffect(String soundFileName) {
      try {
         // Use URL (instead of File) to read from disk and JAR.
         URL url = this.getClass().getClassLoader().getResource(soundFileName);
         if (url == null) {
            throw new IOException("Sound file not found: " + soundFileName);
         }
         // Set up an audio input stream piped from the sound file.
         AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
         // Get a clip resource.
         clip = AudioSystem.getClip();
         // Open audio clip and load samples from the audio input stream.
         clip.open(audioInputStream);
      } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
         System.err.println("Error loading sound file: " + soundFileName);
         e.printStackTrace();
      }
   }

   /** Play or Re-play the sound effect from the beginning, by rewinding. */
   public void play() {
      if (volume != Volume.MUTE) {
         if (clip.isRunning())
            clip.stop(); // Stop the player if it is still running
         clip.setFramePosition(0); // rewind to the beginning
         clip.start(); // Start playing
      }
   }

   public void playLoop() {
      if (volume != Volume.MUTE) {
         if (clip.isRunning()) {
            clip.stop(); // Stop if already running
         }
         clip.setFramePosition(0); // Rewind to the beginning
         clip.loop(Clip.LOOP_CONTINUOUSLY); // Play in a loop
      }
   }

   public void stop() {
      if (clip.isRunning()) {
         clip.stop(); // Stop if running
      }
   }

   static void initGame() {
      values(); // calls the constructor for all the elements
   }
}