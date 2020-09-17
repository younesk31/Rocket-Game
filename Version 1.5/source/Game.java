import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.sound.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Game_new extends PApplet {

// Her kan du styre den generelle game volume
float volume = 0.06f;             
public void setup() {
  
  //size(1600,900); // only here for debug reasons, can be used if a smaller screen is desired.
  loop = new SoundFile(this, "loop.wav"); 
  ded = new SoundFile(this, "ded.wav");
  win = new SoundFile(this, "win.wav");
  C = new CoreSystem();
  S = new StarSystem();
  C.ready();
}

public void draw() {
  if (start == true) {
    C.displayMenu();
    return;
  }
  C.play();
}

public void keyPressed() {
  if (start) {
    start = false;
    System.out.print("Start --> ");
  }
  if (key == 'A' | key == 'a'| keyCode==LEFT) {
    rocketSpeedX = -movementspeed;
  } else if (key == 'D' | key == 'd'| keyCode==RIGHT) {
    rocketSpeedX =  movementspeed;
  }
  if (restart) {
    C.gamerestart();
    loop();
    System.out.print("Restart --> ");
  }
}

public void keyReleased() { 
  if (key == 'A' | key == 'a' | keyCode==LEFT && rocketSpeedX<0) {
    rocketSpeedX = 0;
  } else if (key == 'D' | key == 'd' | keyCode==RIGHT && rocketSpeedX>0) {
    rocketSpeedX = 0;
  }
}
// bruger processings egen lyd og billed import system for at det skal virke på din masking skal du download deres libary.
 SoundFile loop; SoundFile ded; SoundFile win; PImage loader; PImage fuel;
// variabler der fastsætter positioner og størrelser
float canisterX;                   float rocketX;             
float canisterY;                   float rocketY;             
float canisterDiameter = 100;      float rocketDiameter = 250;
// hastigheder på fjende såvel som spiller/raket                     
float canisterSpeedUpgrade = 0.3f;  float movementspeed = 31;
float canisterSpeedY = 1;          float rocketSpeedX;    
// rainbow effect som jeg bruger rundt omkring til text osv.
float rainbow;                  
// parametere der sørger for goal/score/health også kaldt fuel og points.
float rnd = random(150,200);
int score = 0;                     int goal = round(rnd);
int health = 1;                    int points = 0;          
// game boolean expressions/mechanics 
boolean dead = false;            boolean won = false; 
boolean start = true;            boolean restart = false;
// Strings med essential text
String Start = "Press any key to start";                String Start1 = "Use the Arrow keys or W & D";
String Won = "YOU WON!";                                String Won1= "Press any key for menu";
String Gameover = "GAME OVER";                          String Gameover1 = "You ran out of fuel!"+'\n'+"Press a key to return to menu";
// Strings med info
String Creator = "Creator; Slider"; // Created by Slider Slayer aka younes :D
String Achievement = "Collect "+goal+" fuel to Win!";        String Info = "|Working on obstacles|";
// Class calls
CoreSystem C; StarSystem S;

class CoreSystem {  
  public void ready() {
    loader = loadImage("loader.png");
    fuel = loadImage("fuel.png");
    loop.loop(1, volume);
    colorMode(HSB);
    canisterX=width/2;    
    rocketX=width/2;
    canisterY=height/2;   
    rocketY=height/1.1f;
    textAlign(CENTER, BOTTOM);
  }


  public void play() {
    if (rainbow >= 255)  rainbow = 0;  else  rainbow++;
    // highscore = max score i samme game.
    if (score == 0 || points >= score) {
      score = points;
    }
    // points == Collected 
    if (points == goal) {
      loop.stop();
      background(0);
      fill(rainbow, 250, 255);
      noStroke();
      textSize(50);
      text(Won, width/2, height/2);
      text(Won1, width/2, height/2+100);
      start = false;
      restart = true;
      dead = false;
      won = true;
      sound();
      System.out.print("Venter på input --> ");
      noLoop();
      return;
    }
    // health == Fuel capacity
    if (health == 0) {
      loop.stop();
      background(0);
      fill(rainbow, 250, 255);
      textSize(50);
      text("Highest score: "+score, width/2, height/12);
      noStroke();
      textSize(100);
      text(Gameover, width/2, height/2);
      fill(255, 150, 150);
      textSize(50);
      text(Gameover1, width/2, height/2+125);
      start = false;
      restart = true;
      dead = true;
      won = false;
      sound();
      System.out.print("Venter på input --> ");
      noLoop();
      return;
    }
    background(0);
    //Tegn en illusion af stjerner
    S.stars();
    // Billed import som er erstatning for mine cirkler.
    noStroke();
    fill(rainbow, 255, 255);
    image(fuel, canisterX - 30, canisterY - 50, canisterDiameter - 45, canisterDiameter);
    //ellipse(canisterX, canisterY, canisterDiameter, canisterDiameter);         // kun til at teste!
    circle(rocketX, rocketY + 20, 75); 
    image(loader, rocketX - 100, rocketY - 145, rocketDiameter - 50, rocketDiameter);
    //ellipse(rocketX, rocketY, rocketDiameter, rocketDiameter);                         // kun til at teste!
    rocketX = rocketX + rocketSpeedX;

    float distance = dist(canisterX, canisterY, rocketX, rocketY);
    if (distance < (rocketDiameter + canisterDiameter)/2) {
      canisterSpeedY = canisterSpeedY + canisterSpeedUpgrade;
      health = health + 2;
      points = points + 1;
      canisterY = height;
      canisterX = random(width);

    }

    fill(64, 75, 255);
    textSize(25);
    text("Fuel capacity: "+health, width/1.1f, height/12-25);
    text("Collected: "+points, width/1.1f, height/12);

    fill(64, 75, 255);
    textSize(25);
    text(Achievement, width/6, 30);
    

    //textSize(25);
    //text(Info, width/7, height);                                                                             //turned off for github

    canisterY += canisterSpeedY;
    if (canisterY > height) {
      canisterY = 0;
      health = health-1;
    } else if (rocketX > width) {
      rocketX = 0;
    } else if (rocketX < 0) {
      rocketX = width;
    }
  }

  public void displayMenu() {
    background(0, 0, 0);
    textSize(30);
    fill(50, 255, 255);
    text(Start, width/2, height/2);
    text(Start1, width/2, height/2+100);
    fill(rainbow++, 255, 255);
    //    text(Creator, width/8, 0+50);
    return;
  }

  public void gamerestart() {
    if (restart) {
      restart = false;
      start = true;
      health = 1;                
      points = 0;
      canisterSpeedY = 1;
      loop.loop(1, volume);
      draw();
      System.out.print("Nyt game --> ");
    }
  }

  public void sound() {
    if (won) {
      win.play(1, volume);
      won = false;
      System.out.print("Vandt + lyd --> ");
    } else if (dead) {
      ded.play(1, volume);
      dead = false;
      System.out.print("Død + Lyd --> ");
    }
  }
}

class StarSystem {
  int x[], y[], speed[], count; 
  {
    count = 1000;
    x = new int[count];
    y = new int[count];
    speed = new int[count];

    for (int i = 0; i < count; i++)
    {
      x[i] = round(random(width));
      y[i] = round(random(height));
      speed[i] = 1;
    }
  }

  public void stars() {
    stroke(255) ;
    for (int i = 0; i < count; i++)
    {
      if (y[i] > height)
      {
        y[i] = 0;
        x[i] = round(random(width));
        //speed[i] = round(random(1, 2));
      } else {
        point(x[i], y[i]);
        point(x[i], y[i]);
        y[i] += (speed[i]*2+canisterSpeedUpgrade+5);
      }
    }
  }
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "rocket_game" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
