package mygame;

import jgame.*;
import jgame.platform.*;

/** Minimal shooter for jgame skeletons. */
public class MyGame extends StdGame {
	public static void main(String[]args) {new MyGame(parseSizeArgs(args,0));}
	public MyGame() { initEngineApplet(); }
	public MyGame(JGPoint size) { initEngine(size.x,size.y); }
	public void initCanvas() { setCanvasSettings(48,36,16,16,JGColor.black,new JGColor(55,246,199),null); }
	public void initGame() {
		defineMedia("mygame.tbl");
		if (isMidlet()) {
			setFrameRate(20,1);
			setGameSpeed(2.0);
		} else {
			setFrameRate(45,1);
		}
		setHighscores(10,new Highscore(0,"nobody"),15);
		defineImage("bgimage","-",0,"space.gif","-");
		defineImage("ship","-",0,"spaceship.gif","-");
		defineImage("missle","-",0,"myMissile.gif","-");
		defineImage("crab","-",0,"crab.gif","-");
		defineImage("boss","-",0,"locust.gif","-");
		setBGImage("bgimage");
		startgame_ingame=true;
		leveldone_ingame=true;
		title_color = JGColor.white;
		title_bg_color = new JGColor(140,0,0);
		title_font = new JGFont("Arial",0,20);
		setHighscores(10,new Highscore(0,"nobody"),15);
		highscore_title_color = JGColor.red;
		highscore_title_font = new JGFont("Arial",0,20);
		highscore_color = JGColor.white;
		highscore_font = new JGFont("Arial",0,16);
		for (int i=0;i<20;i++){
			new Obstacle();
		}
	}
	public void initNewLife() {
		removeObjects(null,0);
		new Player(pfWidth()/2,pfHeight()-32,5);
	}
	public void doFrameTitle() {
		setFont(new JGFont("arial",0,5));
		drawString("Press N for the next level, or D to lose a life.",
				pfWidth()/2,180,0);
		if (getMouseButton(1)) {
			clearMouseButton(1);

			initGame();
		}
	}

	public void startGameOver() { removeObjects(null,0); }
	public void paintFrameGameOver() {
		setColor(title_bg_color);
		setStroke(1);
		drawRect(160,51,seqtimer*2,seqtimer/2,
			true,true,false);
		drawString("Game Over !",160,50,0,
			getZoomingFont(title_font,seqtimer,0.2,1/120.0),
				title_color);
	}
	public void paintFrameStartGame() {
		drawString("Get Ready!",160,50,0,
			getZoomingFont(title_font,seqtimer,0.2,1/80.0),
				title_color);
	}
	public void paintFrameStartLevel() {
		drawString("Stage "+(stage+1),160,50+seqtimer,0,
			getZoomingFont(title_font,seqtimer,0.2,1/80.0),
				title_color);
	}
	public void paintFrameLevelDone() {
		drawString("Stage "+(stage+1)+" Clear !",160,50,0,
			getZoomingFont(title_font,seqtimer+80,0.2,1/80.0),
				title_color);
	}
	public void paintFrameTitle() {
		drawString("Mirror",160,50,0,
			getZoomingFont(title_font,seqtimer+20,0.3,0.03),
				title_color);
		drawString("Press "+getKeyDesc(key_startgame)+" to start",160,120,0,
			getZoomingFont(title_font,seqtimer+5,0.3,0.03),
				title_color);
		if (!isMidlet())
			drawString("Press "+getKeyDesc(key_gamesettings)+" for settings",
				160,160,0,getZoomingFont(title_font,seqtimer,0.3,.03),
				title_color);
	}
	public void doFrameInGame() {
		moveObjects();
		if (getKey('N')) {
			levelDone();
		}
		if (getKey('D')) {
			lifeLost();
		}
		checkCollision(2,1); // enemies hit player
		checkCollision(4,2); // bullets hit enemies
		if (checkTime(0,(int)(800),(int)((12-level/2))))
			new Enemy();
		if (gametime>=800 && countObjects("enemy",0)==0) levelDone();
	}
	
	public void paintFrameInGame() {
		setFont(new JGFont("arial",0,10.0));
		drawString("Press N for the next level, or D to lose a life.",
			60,15,0);
	}
	
	public void incrementLevel() {
		score += 50;
		if (level<3) level++;
		stage++;
	}
	JGFont scoring_font = new JGFont("Garamond",0,8);
	
	public class Obstacle extends JGObject {

		/** Constructor. */
		public Obstacle () {
			super("obstacle",true,MyGame.this.random(32,pfWidth()-40),-8,3,"skull");
			xspeed = random(-2,2);
			yspeed = random(-2,2);
	}
}
	public class Enemy extends JGObject {
		double timer=0;
		public Enemy() {
			super("enemy",true,MyGame.this.random(32,pfWidth()-40),-8,
					2, stage%2==1 ? "crab" : "boss",
					MyGame.this.random(-1,1), ((level+1.0)*10.0), -2 );
		}
		public void move() {
			timer += gamespeed;
			x += Math.sin(0.1*timer);
			y += Math.cos(0.1*timer);
			if (y>pfHeight()) y = -8;

		}
		public void hit(JGObject o) {
			remove();
			o.remove();
			score += 5;
		}
	}
	
	public class Player extends JGObject {
		int prevmousex=0;
		public Player(double x,double y,double speed) {
			super("player",true,x,y,1,"ship", 0,0,20.0,20.0,-1);
		}
		public void move() {
			setDir(0,0);
			if (getKey(key_up)    && y > yspeed)               ydir=-1;
			if (getKey(key_down)  && y < pfHeight()-16-yspeed) ydir=1;
			if (getKey(key_left)  && x > xspeed)               xdir=-1;
			if (getKey(key_right) && x < pfWidth()-32-yspeed)  xdir=1;
			if (getKey(key_fire) && countObjects("bullet",0) < 15) {
				new JGObject("bullet",true,x,y,4,"missle", 0.0,-15.0, -2);
				clearKey(key_fire);
			}
			if (getMouseButton(1)) {
				new JGObject("bullet",true,x,y,4,"missle", 0.0,-15.0, -2);
				clearMouseButton(1);
			}
			if (getMouseX()!=prevmousex && getMouseInside()) {
				x = getMouseX();
				prevmousex = getMouseX();
			}
		}
		public void hit(JGObject obj) {
			if (and(obj.colid,2)) lifeLost();
			else {
				score += 5;
				obj.remove();
			}
		}
	}
}
