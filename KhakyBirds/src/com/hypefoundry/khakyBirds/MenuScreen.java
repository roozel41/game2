/**
 * 
 */
package com.hypefoundry.khakyBirds;

import com.hypefoundry.engine.core.Input;
import com.hypefoundry.engine.game.Game;
import com.hypefoundry.engine.game.Screen;
import com.hypefoundry.engine.hud.Hud;
import com.hypefoundry.engine.hud.HudLayout;
import com.hypefoundry.engine.hud.HudRenderer;

/**
 * @author Paksas
 *
 */
public class MenuScreen extends Screen 
{
	Input								m_input;
	HudRenderer							m_hudRenderer;
	HudLayout 							m_hudLayout = null;
	
	/**
	 * Constructor.
	 * 
	 * @param game
	 */
	public MenuScreen( Game game ) 
	{
		super( game );
		
		m_input = game.getInput();
		
		// load the HUD
		Hud hud = m_resourceManager.getResource( Hud.class, "hud/hudDefinition.xml" );
		if ( hud == null )
		{
			throw new RuntimeException( "No HUD definition" );
		}
		
		m_hudRenderer = new HudRenderer( game, hud );
		
		// load the menu layout
		if ( m_hudLayout == null )
		{
			m_hudLayout = m_resourceManager.getResource( HudLayout.class, "hud/mainMenu.xml" );
			m_hudLayout.attachRenderer( m_hudRenderer ); 
		}
	}


	@Override
	public void present( float deltaTime ) 
	{			
		// draw the world contents
		m_hudRenderer.draw( deltaTime );
	}

	@Override
	public void pause() 
	{
		m_resourceManager.releaseResources();
	}

	@Override
	public void resume() 
	{		
		m_resourceManager.loadResources();
	}

	@Override
	public void dispose() 
	{
	}

}