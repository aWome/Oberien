/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.menus;

/**
 *
 * @author Bobthepeanut
 */
public abstract class MenuTempl {
	
	/**
	 * Used by Menu to get if the GameMode must be switched.
	 * 
	 * @return is the GameMode to be switched?
	 */
	public abstract boolean getModeSwitch();
	
	/**
	 * Useb by Menu to get if another Menu must be set active.
	 * 
	 * @return should the Menu be switched?
	 */
	public abstract boolean switchMenu();
	
	/**
	 * Used by Menu to get the menu name the game should switch to.
	 * 
	 * @return the name of the menu
	 */
	public abstract String getSwitchMenu();
	
	/**
	 * Used by Menu to see if the game should exit.
	 * 
	 * @return should the game exit?
	 */
	public abstract boolean shouldExit();
}
