package view.gamesstates;

import java.util.Arrays;

import model.Layer;
import model.map.Coordinate;
import model.map.Map;
import model.map.MapList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import controller.Options;
import controller.StateMap;

import view.data.StartData;
import view.eventhandler.MouseEvents;
import view.renderer.FoWRenderer;
import view.renderer.MapRenderer;
import view.renderer.UnitRenderer;

public class StartPositionChooser extends BasicGameState {
	private StartData sd;
	private StateMap sm;
	private MapRenderer mr;
	private Map map;
	private MouseEvents me;
	private UnitRenderer ur;
	private FoWRenderer fowr;
	
	private int basex=-1, basey=-1;
	
	private int screenWidth;
	private int screenHeight;
	private boolean scaleUp;
	private boolean scaleDown;
	private boolean moveLeft;
	private boolean moveRight;
	private boolean moveUp;
	private boolean moveDown;
	private boolean endTurn;
	private int mouseX;
	private int mouseY;
	private float scale = 1;
	private float camX = 0;
	private float camY = 0;
	
	public StartPositionChooser(StartData sd) {
		this.sd = sd;
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		if (MapList.getInstance().getCurrentMap() != null) {
			sm = sd.getSm();
			mr = sd.getMr();
			map = sd.getMap();
			me = new MouseEvents();
			me.init();
			ur = sd.getUr();
			fowr = sd.getFowr();
			
			screenWidth = gc.getScreenWidth();
			screenHeight = gc.getScreenHeight();
		}
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.setAntiAlias(Options.antiAliasing);
		g.resetTransform();
		g.translate(-camX * scale, -camY * scale);
		g.scale(scale, scale);
		mr.draw(g);
		fowr.draw(g, sm, sm.getSight());
		ur.draw(g, sm, null, null, 0, sm.getCurrentPlayer().getColor());
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		//zooming/scaling
		int mw = Mouse.getDWheel();
		if ((scaleUp || mw > 0) && scale < 2) {
			float mouseMapX = mouseX / scale + camX;
			float mouseMapY = mouseY / scale + camY;
			scale += 0.05;
			camX = mouseMapX - mouseX / scale;
			camY = mouseMapY - mouseY / scale;
		}
		if ((scaleDown || mw < 0) && scale >= 0.3) {
			camX += gc.getScreenWidth() / 2 / scale - gc.getScreenWidth() / 2;
			camY += gc.getScreenHeight() / 2 / scale - gc.getScreenHeight() / 2;
			scale -= 0.05;
			camX -= gc.getScreenWidth() / 2 / scale - gc.getScreenWidth() / 2;
			camY -= gc.getScreenHeight() / 2 / scale - gc.getScreenHeight() / 2;
		}

		//camera
		float moveSpeed = delta / scale;
		float minX = -500 / scale * ((float) gc.getScreenWidth() / gc.getScreenHeight());
		float minY = -500 / scale * ((float) gc.getScreenHeight() / gc.getScreenWidth());
		float maxX = map.getWidth() * 32 - minX - gc.getScreenWidth() / scale;
		float maxY = map.getHeight() * 32 - minY - gc.getScreenHeight() / scale;

		if (moveLeft && camX > minX) {
			camX -= moveSpeed;
		}
		if (moveUp && camY > minY) {
			camY -= moveSpeed;
		}
		if (moveRight && camX < maxX) {
			camX += moveSpeed;
		}
		if (moveDown && camY < maxY) {
			camY += moveSpeed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			gc.exit();
		}
		
		if (me.isMousePressed(0)) {
			Point p = me.getMousePos();
			int mapx = (int) (camX + p.getX() / scale);
			int mapy = (int) (camY + p.getY() / scale);
			if (basex != -1) {
				sm.removeModel(new Coordinate(basex, basey, Layer.Ground));
			}
			basex = mapx/32;
			basey = mapy/32;
			if (!Arrays.asList(map.getStartAreaOfTeam(sm.getCurrentPlayer().getTeam())).contains(new Coordinate(basex, basey, Layer.Ground))) {
				basex = -1;
				basey = -1;
			}
			sm.addModel(basex, basey, 512);
		}
		
		if (endTurn) {
			endTurn = false;
			if (basex != -1 && basey != -1) {
				sm.endTurn();
				basex = -1;
				basey = -1;
				if (sm.getRound() == 1) {
					sbg.getState(4).init(gc, sbg);
			        sbg.enterState(getID() + 1);
				}
			}
		}
	}

	@Override
	public int getID() {
		return 3;
	}
	
	@Override
	public void keyPressed(int key, char c) {
//    	System.out.println("Pressed: " + key + " " + c);
		if (c == '+') {
			scaleUp = true;
		}
		if (c == '-') {
			scaleDown = true;
		}
		if (key == Input.KEY_LEFT) {
			moveLeft = true;
		}
		if (key == Input.KEY_RIGHT) {
			moveRight = true;
		}
		if (key == Input.KEY_UP) {
			moveUp = true;
		}
		if (key == Input.KEY_DOWN) {
			moveDown = true;
		}
	}

	@Override
	public void keyReleased(int key, char c) {
//    	System.out.println("Released: " + key + " " + c);
		if (c == '+') {
			scaleUp = false;
		}
		if (c == '-') {
			scaleDown = false;
		}
		if (key == Input.KEY_LEFT) {
			moveLeft = false;
		}
		if (key == Input.KEY_RIGHT) {
			moveRight = false;
		}
		if (key == Input.KEY_UP) {
			moveUp = false;
		}
		if (key == Input.KEY_DOWN) {
			moveDown = false;
		}
		if (key == Input.KEY_RETURN) {
			endTurn = true;
		}
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		mouseX = newx;
		mouseY = newy;
		if (newx < 3) {
			moveLeft = true;
		} else if (newx > screenWidth - 4) {
			moveRight = true;
		} else if (moveLeft && oldx < 3) {
			moveLeft = false;
		} else if (moveRight && oldx > screenWidth - 4) {
			moveRight = false;
		}

		if (newy < 3) {
			moveUp = true;
		} else if (newy > screenHeight - 4) {
			moveDown = true;
		} else if (moveUp && oldy < 3) {
			moveUp = false;
		} else if (moveDown && oldy > screenHeight - 4) {
			moveDown = false;
		}
	}
}
