/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.gamesstates;

import java.awt.FontFormatException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.Player;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import controller.StateMap;
import view.data.StartData;
import view.menus.MainMenu;
import view.menus.MapChooser;
import view.menus.MenuTempl;

/**
 *
 * @author Bobthepeanut
 */
public class Menu extends BasicGameState {
    private StartData sd;
    private MainMenu mm;
	private MapChooser mc;
	private MenuTempl currentMenu;
    private static boolean switchMode, exit;
	private static int state = 0;
    
    public Menu(StartData sd) {
        this.sd = sd;
    }

    @Override
    public int getID() {
        return 1;
    }
    
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        mm = new MainMenu();
		mc = new MapChooser();
        try {
            mm.init(gc.getInput(), gc, sd.getFont());
			mc.init(sd.getFont(), gc);
        } catch (FontFormatException | IOException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        }
		currentMenu = mm;
    }    

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        if (state == 0) {
            mm.draw(g);   
        } else if (state == 1) {
			mc.draw(g);
		}
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int i) throws SlickException {
		Input input = gc.getInput();
		boolean mousePressed = input.isMousePressed(0);
    	if (state == 0) {
			mm.update(mousePressed);			
		} else if (state == 1) {
			mc.update(sd, mousePressed);
		}
        if (input.isKeyDown(Input.KEY_ESCAPE) || currentMenu.shouldExit()) {
            gc.exit();
        }
        
        if (currentMenu.getModeSwitch()) {
        	//TODO create StateMap in teamselection
    		sd.setSm(new StateMap(new Player[]{new Player("BH16", Color.red, 0), new Player("Enemy", Color.green, 1)}));
            sbg.enterState(getID() + 1);
        }
		if (currentMenu.switchMenu()) {
			String name = currentMenu.getSwitchMenu();
			switch (name) {
				case "Settings":
					state = 2;
					break;
				case "MapChooser":
					state = 1;
					currentMenu = mc;
					break;
			}
		}
    }    
}
