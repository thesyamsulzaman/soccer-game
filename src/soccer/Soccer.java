/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soccer;

import com.golden.gamedev.Game;
import com.golden.gamedev.GameLoader;
import com.golden.gamedev.object.AnimatedSprite;
import com.golden.gamedev.object.Background;
import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.SpriteGroup;
import com.golden.gamedev.object.Timer;
import com.golden.gamedev.object.background.ImageBackground;
import com.golden.gamedev.object.collision.BasicCollisionGroup;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

/**
 *
 * @author Syamsul
 */
public class Soccer extends Game {

  int score = 0;
  Timer timer;
  GameFont font;
  String playerDirection = "left";
  String ballColor = "RED";
  long startTimer = System.nanoTime();

  Background background;
  Sprite leftGoalPost, rightGoalPost;
  AnimatedSprite player;
  String[] colors = {"RED", "BLUE", "GREEN"};

  SpriteGroup ballGroup;
  SpriteGroup playerGroup;
  SpriteGroup goalPostGroup;

  BallCollision playerBallCollision;
  GoalPostCollision ballGoalPostCollision;

  class BallCollision extends BasicCollisionGroup {

    BallCollision() {

    }

    @Override
    public void collided(Sprite player, Sprite ball) {
      if ("left".equals(playerDirection)) {
        ball.setSpeed(-0.2, 0);
      }

      if ("right".equals(playerDirection)) {
        ball.setSpeed(0.2, 0);
      }

    }

  }

  class GoalPostCollision extends BasicCollisionGroup {

    GoalPostCollision() {
    }

    @Override
    public void collided(Sprite ball, Sprite goalPost) {
      ball.setActive(false);

      if ("RED".equals(ballColor)) {
        score += 10;
      }

      if ("GREEN".equals(ballColor)) {
        score += 15;
      }

      if ("BLUE".equals(ballColor)) {
        score += 20;
      }

    }

  }

  @Override
  public void initResources() {
    font = fontManager.getFont(getImages("Resources/font.png", 20, 3), " !            ..0123456789:   -? ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    background = new ImageBackground(getImage("Resources/background.jpg"), 800, 600);
    leftGoalPost = new Sprite(getImage("Resources/gawang_kiri.png"), 0, 230);
    rightGoalPost = new Sprite(getImage("Resources/gawang_kanan.png"), 755, 230);
    ballGroup = new SpriteGroup("BALL");
    playerGroup = new SpriteGroup("PLAYER");
    goalPostGroup = new SpriteGroup("GOALPOST");

    player = new AnimatedSprite(getImages(getPlayerImagePath(playerDirection), 1, 1), 300, 400);

    timer = new Timer(3000);

    goalPostGroup.add(leftGoalPost);
    goalPostGroup.add(rightGoalPost);

    playerBallCollision = new BallCollision();
    playerBallCollision.setCollisionGroup(playerGroup, ballGroup);

    ballGoalPostCollision = new GoalPostCollision();
    ballGoalPostCollision.setCollisionGroup(ballGroup, goalPostGroup);

    playerGroup.add(player);
    randomizeBall();
  }

  public String getPlayerImagePath(String direction) {
    return "Resources/player-face-" + direction + ".png";
  }

  public void randomizeBall() {
    ballColor = colors[getRandom(0, 2)];
    Sprite ball = new Sprite(getImage("Resources/" + ballColor + ".png"), getRandom(200, 600), 10);
    ball.setVerticalSpeed(0.2);
    ballGroup.add(ball);
  }

  @Override
  public void update(long timeLapsed) {
    playerBallCollision.checkCollision();
    ballGoalPostCollision.checkCollision();

    if (timer.action(timeLapsed)) {
      randomizeBall();
    }

    ballGroup.update(timeLapsed);

    if (score >= 200) {

      long finishTimer = System.nanoTime();
      long timeSpent = TimeUnit.SECONDS.convert(finishTimer - startTimer, TimeUnit.NANOSECONDS);

      String timeSpentMessage;

      if (timeSpent > 60) {
        long minute = timeSpent / 60;
        long seconds = timeSpent - (minute * 60);
        timeSpentMessage = minute + " menit " + seconds + " detik";
      } else {
        timeSpentMessage = timeSpent + " detik";
      }

      JOptionPane.showMessageDialog(null, "Selamat anda menyelesaikan game dalam waktu " + timeSpentMessage, "Pesan", JOptionPane.PLAIN_MESSAGE);

      score = 0;
      startTimer = System.nanoTime();
    }

    if (keyDown(KeyEvent.VK_LEFT)) {
      playerDirection = "left";
      player.setImages(getImages(getPlayerImagePath(playerDirection), 1, 1));
      player.setAnimationFrame(0, 0);
      player.setSpeed(-0.2, 0);
      player.update(timeLapsed);
    }

    if (keyDown(KeyEvent.VK_RIGHT)) {
      playerDirection = "right";
      player.setImages(getImages(getPlayerImagePath(playerDirection), 1, 1));
      player.setAnimationFrame(0, 0);
      player.setSpeed(0.2, 0);
      player.update(timeLapsed);
    }

    if (keyDown(KeyEvent.VK_UP)) {
      player.setAnimationFrame(0, 0);
      player.setSpeed(0, -0.2);
      player.update(timeLapsed);
    }

    if (keyDown(KeyEvent.VK_DOWN)) {
      player.setAnimationFrame(0, 0);
      player.setSpeed(0, 0.2);
      player.update(timeLapsed);
    }

  }

  @Override
  public void render(Graphics2D gd) {
    background.render(gd);
    leftGoalPost.render(gd);
    rightGoalPost.render(gd);
    player.render(gd);
    font.drawString(gd, "SCORE : " + score, 15, 15);
    ballGroup.render(gd);

  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    GameLoader gameLoader = new GameLoader();
    gameLoader.setup(new Soccer(), new Dimension(800, 600), false);
    gameLoader.start();

  }

}
