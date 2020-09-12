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

public class Game extends PApplet {

 SoundFile loop; SoundFile ded; SoundFile win; PImage loader; PImage fuel;

float canisterX;                   float ballX;             
float canisterY;                   float ballY;             
float canisterDiameter = 100;      float ballDiameter = 250;
//                                 
float canisterSpeedUpgrade = 0.3f;  float movementspeed = 30;
float canisterSpeedY = 1;          float ballSpeedX;    
float rainbow;                  
//parametere                  
int goal = 2;
int health = 1;                
int points = 0;
int score = 0;
float volume = 0.05f;             // game volume
// Game mechanics 
boolean dead = false;            boolean won = false; 
boolean start = true;            boolean restart = false;

// Strings for essential text
String Start = "Press any key to start";                String Start1 = "Use the Arrow keys or W & D";
String Won = "YOU WON!";                                String Won1= "Press any key for menu";
String Gameover = "GAME OVER";                          String Gameover1 = "You ran out of fuel!"+'\n'+"Press a key to return to menu";
// Strings with info
String Creator = ""; // Created by Slider Slayer :D
String Achievement = "Collect 500 fuel to Win!";
String Info = "|Working on obstacles|";

public void setup() {
  loop = new SoundFile(this, "loop.wav"); 
  ded = new SoundFile(this, "ded.wav");
  win = new SoundFile(this, "win.wav");
  loader = loadImage("loader.png");
  fuel = loadImage("fuel.png");
  
  loop.loop(1,volume);
  //size(1000,1000); // only here for debug reasons
  colorMode(HSB);
  canisterX=width/2;    
  ballX=width/2;
  canisterY=height/2;   
  ballY=height/1.1f;
  textAlign(CENTER,BOTTOM);
}

public void draw() {
  if (rainbow >= 255)  rainbow = 0;  else  rainbow++;
  if (start == true) {
    game();
    fill(rainbow, 250, 255);
    textSize(50);
    text("Highest score: "+score, width/2, height/12);
    return; 
  }
    if(score == 0 || points >= score){
    score = points;
  }
  
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
  
// Galaxy effect
    background(0);
    for (int i = 0; i < (width + (canisterY*2)); i += 100) {
      stroke(255);
      strokeWeight(random(6));
      point(random(width), random(height));
    }

  noStroke();
  fill(rainbow, 255, 255);
  image(fuel, canisterX - 30, canisterY - 50, canisterDiameter - 45, canisterDiameter);
  //ellipse(canisterX, canisterY, canisterDiameter, canisterDiameter);         // kun til at teste!
  circle(ballX, ballY + 20, 75); 
  image(loader, ballX - 100, ballY - 145, ballDiameter - 50, ballDiameter);
  //circle(ballX, ballY-60, 43); // vindue
  //ellipse(ballX, ballY, ballDiameter, ballDiameter);             // kun til at teste!
  ballX = ballX + ballSpeedX;

float distance = dist(canisterX, canisterY, ballX, ballY);
  if (distance < (ballDiameter + canisterDiameter)/2) {
    canisterSpeedY = canisterSpeedY + canisterSpeedUpgrade;
    health = health + 4;
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
  text(Achievement, width/10, 30);
  
  textSize(25);
  //text(Info, width/7, height);                                                                              //turned off for github
  
  canisterY += canisterSpeedY;
  if (canisterY > height) {
    canisterY = 0;
    health = health-1;
  } else if (ballX > width) {
    ballX = 0;
  } else if (ballX < 0) {
    ballX = width;
  }
}

public void game() {
if (start == true) {
   background(0,0,0);
    textSize(30);
    fill(50, 255, 255);
    text(Start, width/2, height/2);
    text(Start1, width/2, height/2+100);
    fill(rainbow++, 255, 255);
    text(Creator, width/8, 0+50);
    return; 
  }
}

public void gamerestart(){
  if(restart){
    restart = false;
    start = true;
    health = 1;                
    points = 0;
    canisterSpeedY = 1;
    loop.loop(1,volume);
    draw();
    System.out.print("Nyt game --> ");
  }
}

public void sound(){
  if (won) {
    win.play(1,volume);
    won = false;
    System.out.print("Vandt + lyd --> ");
  } else if (dead) {
    ded.play(1,volume);
    dead = false;
    System.out.print("Død + Lyd --> ");
  } 
}

public void keyPressed() {
  if (start) {
    start = false;
    System.out.print("Start --> ");
  }
  if (key == 'A' | key == 'a'| keyCode==LEFT) {
    ballSpeedX = -movementspeed;
  } else if (key == 'D' | key == 'd'| keyCode==RIGHT) {
    ballSpeedX =  movementspeed;
  }
   if (restart){
     gamerestart();
     loop();
     System.out.print("Restart --> ");
  }
}

public void keyReleased() { 
  if (key == 'A' | key == 'a' | keyCode==LEFT && ballSpeedX<0) {
    ballSpeedX = 0;
  } else if (key == 'D' | key == 'd' | keyCode==RIGHT && ballSpeedX>0) {
    ballSpeedX = 0;
  }
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Game" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
