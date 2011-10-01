/**
 * 
 */
package com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.hideout;

import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.crap.Crappable;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.hunter.Shootable;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.pedestrian.Pedestrian;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.perkPedestrian.PerkPedestrian;
import com.hypefoundry.engine.math.BoundingBox;
import com.hypefoundry.engine.physics.DynamicObject;
import com.hypefoundry.engine.world.Entity;
import com.hypefoundry.engine.world.World;

/**
 * @author azagor
 *
 */
public class Hideout extends Entity implements NotWalkAble, Shootable, Crappable
{
	
	public int						m_pedestrians 		= 0;
	public int						m_perkPedestrians	= 0;
	public int						m_maxPerkPedestrianNumber	= 2;
	private World 					m_world;
	public boolean					m_isDemolished		= false;
	
	
	public enum State
	{
		Default,
		Bombed
	}
	
	State				m_state;
	/////////////////////////////////////////////////////////////
	/**
	 * Default constructor.
	 */
	public Hideout()
	{
	
		setBoundingBox( new BoundingBox( -0.7f, -0.7f, -15f, 0.7f, 0.7f, 15f ) );	// TODO: config
		setPosition( 0, 0, 70 );
		
		final float maxRotationSpeed = 0.0f;
		final float maxLinearSpeed = 0.0f;
		defineAspect( new DynamicObject( maxLinearSpeed, maxRotationSpeed ) );

	}
	
	/**
	 * Constructor.
	 * 
	 * @param x
	 * @param y
	 */
	public Hideout( float x, float y)
	{
		// call the default constructor first to perform the generic initialization
		this();
				
		setPosition( x, y, 70 );
		m_isDemolished		= false;
		
	}
	
	@Override
	public void onAddedToWorld( World hostWorld )
	{
		m_world = hostWorld;
	}
	
	public void goOut()
	{
		m_world.addEntity( new Pedestrian(getPosition() ) );
	}

	public void perkPedestrianGoOut()
	{
		//tu trzeba b�dzie przekazywa� instancj� hideouta
		m_world.addEntity( new PerkPedestrian(getPosition(), this ) );
	}
	
	public void bombed()
	{
		m_isDemolished		= true;
	}
}
