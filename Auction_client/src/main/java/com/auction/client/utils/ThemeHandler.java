package com.auction.client.utils;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class ThemeHandler {
  private static ThemeHandler instance;
  private static final Set<Scene> activeScenes = new HashSet<>();
  private static String activeTheme = "Default"; // default to basic javafx theme
  private final String THEME_CSS_PATH = getClass().getResource("/files/theme.css").toExternalForm();

  // singleton stuff
  private ThemeHandler() {
  };

  public static synchronized ThemeHandler getInstance() {
    if (instance == null) {
      instance = new ThemeHandler();
    }
    return instance;
  }

  // getter for active theme
  public String getActiveTheme() {
    return activeTheme;
  }

  /**
   * Usage: switch root theme based on given theme strings. if nothing found,
   * defaults to standard
   * light theme
   *
   * @param theme
   * @param root
   */
  private void applyTheme(String theme, Parent root) {
    if (root == null)
      return; // skip if no root
    root.getStyleClass().removeAll("theme-active", "theme-dark", "theme-modernblue", "theme-mint");
    if (theme.equals("Default"))
      return; // skip if theme default is white
    root.getStyleClass().add("theme-active");
    switch (theme) {
      case "Dark" -> root.getStyleClass().add("theme-dark");
      case "Modern Blue" -> root.getStyleClass().add("theme-modernblue");
      case "Mint" -> root.getStyleClass().add("theme-mint");
      default -> {
      }
    }
  }

  /**
   * usage: set theme to active scenes stored in activeScenes
   *
   * @param theme
   */
  public void setTheme(String theme) {
    ConfigFileHandler.setProperty("theme", theme);
    activeTheme = theme;
    // cleanup inactive scenes
    activeScenes.removeIf(scene -> scene.getWindow() == null);
    for (Scene scn : activeScenes) {
      applyTheme(theme, scn.getRoot());
    }
  }

  /**
   * Usage: add a scene when created, to the scene hashset
   *
   * @param scene
   */
  public void addActiveScene(Scene scene) {
    activeScenes.add(scene);
    // inject style css to scene if not present
    if (!scene.getStylesheets().contains(THEME_CSS_PATH)) {
      scene.getStylesheets().add(THEME_CSS_PATH);
    }
    applyTheme(activeTheme, scene.getRoot());
  }
}
