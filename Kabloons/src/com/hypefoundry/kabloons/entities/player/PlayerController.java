/**
 * 
 */
package com.hypefoundry.kabloons.entities.player;


import com.hypefoundry.engine.controllers.fsm.FSMState;
import com.hypefoundry.engine.controllers.fsm.FiniteStateMachine;
import com.hypefoundry.engine.gestures.Gesture;
import com.hypefoundry.engine.gestures.GesturesListener;
import com.hypefoundry.engine.gestures.GesturesRecognition;
import com.hypefoundry.engine.hud.ButtonListener;
import com.hypefoundry.engine.hud.HudLayout;
import com.hypefoundry.engine.hud.widgets.button.ButtonWidget;
import com.hypefoundry.engine.hud.widgets.image.ImageWidget;
import com.hypefoundry.engine.math.Vector3;
import com.hypefoundry.engine.renderer2D.Camera2D;
import com.hypefoundry.engine.world.Entity;
import com.hypefoundry.engine.world.World;
import com.hypefoundry.engine.world.WorldView;
import com.hypefoundry.kabloons.GameScreen;
import com.hypefoundry.kabloons.entities.background.AnimatedBackground;
import com.hypefoundry.kabloons.entities.baloon.Baloon;
import com.hypefoundry.kabloons.entities.fan.Fan;
import com.hypefoundry.kabloons.entities.help.Help;
import com.hypefoundry.kabloons.utils.AssetsFactory;


/**
 * @author Paksas
 *
 */
public class PlayerController extends FiniteStateMachine
{
	private Player						m_player;
	private GameScreen 					m_screen;
	private World						m_world;
	private Camera2D					m_camera;
	private AssetsFactory 				m_assetsFactory;
	private GesturesRecognition			m_gesturesRecognition;
	

	// ------------------------------------------------------------------------
	// States
	// ------------------------------------------------------------------------
	
	/**
	 * Main gameplay mode.
	 * 
	 * @author Paksas
	 */
	class Gameplay extends FSMState implements GesturesListener, PlayerListener, WorldView, ButtonListener
	{
		// baloon related data
		private Vector3				m_baloonReleasePos = new Vector3( 2.4f, -0.2f, 0.0f );
		private Baloon				m_baloon;
		
		private Help				m_help = null;
		
		private Vector3				m_touchPos = new Vector3();
		private HudLayout			m_hudLayout;
		
		
		@Override
		public void activate()
		{
			// create a hud
			if ( m_hudLayout == null )
			{
				m_hudLayout = m_screen.getResourceManager().getResource( HudLayout.class, "hud/gameplay/gameHud.xml" );
				m_hudLayout.attachRenderer( m_screen.m_hudRenderer ); 
				m_hudLayout.attachButtonListener( this );
						
				updateFanCounters();
			}
			
			m_screen.registerInputHandler( m_gesturesRecognition );
			m_gesturesRecognition.attachListener( this );
			
			m_player.attachListener( this );
			
			m_world.attachView( this );
		}
		
		@Override
		public void deactivate()
		{		
			m_hudLayout.detachButtonListener( this );
			m_hudLayout.detachRenderer( m_screen.m_hudRenderer ); 
			m_hudLayout = null;
			
			m_gesturesRecognition.detachListener( this );
			m_screen.unregisterInputHandler( m_gesturesRecognition  );
			
			m_player.detachListener( this );
			
			m_world.detachView( this );
		}
		
		@Override
		public void execute( float deltaTime )
		{
			if ( m_baloon == null )
			{
				// baloon hasn't been released yet
				return;
			}
			
			// monitor baloon's state
			if ( m_baloon.isAlive() == false )
			{
				// baloon was destroyed
				transitionTo( Failure.class );
			}
			else if ( m_baloon.isSafe() == true )
			{
				// baloon reached safety
				transitionTo( Success.class );
			}
		}
		
		@Override
		public void onGestureRecognized( Gesture gesture ) 
		{
			if ( gesture.m_id.equalsIgnoreCase( "ADD_LEFT_FAN" ) )
			{
				addFan( gesture, Fan.Direction.Left );
			}
			else if ( gesture.m_id.equalsIgnoreCase( "ADD_RIGHT_FAN" ) )
			{
				addFan( gesture, Fan.Direction.Right );
			}
			else if ( gesture.m_id.equalsIgnoreCase( "REMOVE_FAN" ) )
			{
				removeFan( gesture );
			}
			else if ( gesture.m_id.equalsIgnoreCase( "RELEASE_BALOON" ) )
			{
				if ( m_baloon == null && m_player.m_ghostReleaseEnabled )
				{
					// release a single baloon
					m_baloon = m_assetsFactory.createRandomBaloon( m_baloonReleasePos );
					m_screen.m_world.addEntity( m_baloon );
				}
			}
		}
		
		@Override
		public void onButtonPressed( String id ) 
		{
			if ( id.equalsIgnoreCase( "HelpButton" ) && m_help != null )
			{
				m_help.toggleVisible();
			}
		}
		
		/**
		 * Places a new fan in the world in response to the user's input.
		 * 
		 * @param gesture
		 * @param direction
		 */
		private void addFan( Gesture gesture, Fan.Direction direction )
		{	
			if ( m_player.m_fansLeft[direction.m_idx] <= 0 )
			{
				return;
			}
				
			// change the gesture direction from screen to model space
			gesture.getStart( m_touchPos );
			m_camera.screenPosToWorld( m_touchPos, m_touchPos );
			
			// decrease the number of fans we've left to place
			m_player.m_fansLeft[direction.m_idx]--;
			
			// place the fan
			Fan fan = new Fan( m_touchPos );
			m_assetsFactory.initializeFan( fan, direction );
			m_world.addEntity( fan );
			
			// play proper effects
			playFanEditionEffect( fan );
			
			// update the hud
			updateFanCounters();
		}
		
		/**
		 * Removes a previously placed fan from the world in response to the user's input.
		 * 
		 * @param gesture
		 * @param direction
		 */
		private void removeFan( Gesture gesture )
		{	
			gesture.getCenter( m_touchPos );
			m_camera.screenPosToWorld( m_touchPos, m_touchPos );
					
			// get the entity that's located on the clicked location
			Fan clickedFan = m_world.findNearestEntity( Fan.class, 0.2f, m_touchPos );
			if ( clickedFan != null && clickedFan.m_wasCreatedByUser )
			{
				m_player.m_fansLeft[clickedFan.getBlowDirection().m_idx]++;
				m_world.removeEntity( clickedFan );
				
				// play proper effects
				playFanEditionEffect( clickedFan );
			}
			
			// update the hud
			updateFanCounters();
		}
		
		/**
		 * A helper method that updates the fan counters on the hud
		 */
		private void updateFanCounters()
		{
			ImageWidget rightFansCounter = m_hudLayout.getWidget( ImageWidget.class, "RightFansCounter" );
			rightFansCounter.m_caption = Integer.toString( m_player.m_fansLeft[ Fan.Direction.Right.m_idx ] );
					
			ImageWidget leftFansCounter = m_hudLayout.getWidget( ImageWidget.class, "LeftFansCounter" );
			leftFansCounter.m_caption = Integer.toString( m_player.m_fansLeft[ Fan.Direction.Left.m_idx ] );
		}
		
		/**
		 * Plays the effects associated with a fan appearing or disappearing from the screen
		 */
		private void playFanEditionEffect( Fan fan )
		{
			// spawn the puff effect
			AnimatedBackground puffEffect = new AnimatedBackground();
			m_assetsFactory.initializePuff( puffEffect, fan.getPosition() );
			m_world.addEntity( puffEffect );
		}

		@Override
		public void onFansCountChanged() 
		{
			updateFanCounters();
		}

		@Override
		public void onAttached(World world) {}

		@Override
		public void onDetached(World world) {}

		@Override
		public void onEntityAdded(Entity entity) 
		{
			if ( entity instanceof Help )
			{
				m_help = (Help)entity;
				
				// show the help icon
				ButtonWidget helpButton = m_hudLayout.getWidget( ButtonWidget.class, "HelpButton" );
				helpButton.m_isVisible = true;
			}
		}

		@Override
		public void onEntityRemoved(Entity entity) 
		{
			if ( entity instanceof Help )
			{
				m_help = null;
				
				ButtonWidget helpButton = m_hudLayout.getWidget( ButtonWidget.class, "HelpButton" );
				helpButton.m_isVisible = false;
			}
		}
	}
	
	/**
	 * Player completed the level successfully.
	 * 
	 * @author Paksas
	 */
	class Success extends FSMState implements ButtonListener
	{
		// baloon related data
		private HudLayout			m_hudLayout;
		
		@Override
		public void activate()
		{
			// create a hud
			if ( m_hudLayout == null )
			{
				m_hudLayout = m_screen.getResourceManager().getResource( HudLayout.class, "hud/gameplay/winnerHud.xml" );
				m_hudLayout.attachRenderer( m_screen.m_hudRenderer ); 
				m_hudLayout.attachButtonListener( this );
			}
			
			// unlock the next level
			m_screen.unlockNextLevel();
		}

		@Override
		public void onButtonPressed( String buttonId ) 
		{
			if ( buttonId.equals( "ExitToMenu" ) )
			{	
				m_screen.exitToMenu();
			}
			else if ( buttonId.equals( "NextLevel" ) )
			{
				m_screen.loadNextLevel();
			}
		}	
	}
	
	/**
	 * Player failed to complete the level.
	 * 
	 * @author Paksas
	 */
	class Failure extends FSMState implements ButtonListener
	{
		// baloon related data
		private HudLayout			m_hudLayout;
		
		@Override
		public void activate()
		{
			// create a hud
			if ( m_hudLayout == null )
			{
				m_hudLayout = m_screen.getResourceManager().getResource( HudLayout.class, "hud/gameplay/looserHud.xml" );
				m_hudLayout.attachRenderer( m_screen.m_hudRenderer ); 
				m_hudLayout.attachButtonListener( this );
			}
		}

		@Override
		public void onButtonPressed( String buttonId ) 
		{
			if ( buttonId.equals( "ExitToMenu" ) )
			{	
				m_screen.exitToMenu();
			}
			else if ( buttonId.equals( "RetryLevel" ) )
			{
				m_screen.reloadLevel();
			}
		}	
	}
	
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------
	
	/**
	 * Constructor.
	 * 
	 * @param screen
	 */
	public PlayerController( GameScreen screen, Entity playerEntity ) 
	{
		super( playerEntity );
		
		m_screen = screen;
		m_player = (Player)playerEntity;
		m_world = screen.m_world;
		m_camera = screen.m_worldRenderer.getCamera();
		m_assetsFactory = screen.m_assetsFactory;	
		m_gesturesRecognition = m_assetsFactory.getGesturesRecognition();
		
		// register states
		register( new Gameplay() );
		register( new Success() );
		register( new Failure() );
		begin( Gameplay.class );
	}
}
