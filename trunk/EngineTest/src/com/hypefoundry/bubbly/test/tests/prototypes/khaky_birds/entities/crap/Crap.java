package com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.crap;

import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.bird.Bird;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.pedestrian.Pedestrian;
import com.hypefoundry.engine.world.Entity;
import com.hypefoundry.engine.world.EntityEvent;
import com.hypefoundry.engine.world.EntityEventListener;
import com.hypefoundry.engine.world.World;
import com.hypefoundry.engine.math.BoundingBox;
import com.hypefoundry.engine.math.Vector3;
import com.hypefoundry.engine.physics.DynamicObject;
import com.hypefoundry.engine.physics.events.CollisionEvent;

/**
 * Crap a bird makes.
 * 
 * @author azagor
 *
 */
public class Crap extends Entity implements EntityEventListener
{
	private World 	m_world    			 = null;
	public boolean pedestrianHit         = false;
	private Bird   m_bird 				 = null;
	
	/**
	 * Constructor.
	 */
	public Crap()
	{
		setPosition( 0, 0, 0 );
		setBoundingBox( new BoundingBox( -0.2f, -0.2f, -0.1f, 0.2f, 0.2f, 0.1f ) );	// TODO: config
		
		// register events listeners
		attachEventListener( this );
		
		// add movement capabilities
		final float maxLinearSpeed = 1.0f;
		final float maxRotationSpeed = 180.0f;
		defineAspect( new DynamicObject( maxLinearSpeed, maxRotationSpeed ) );
	}

	
	/**
	 * Setting starting position of crap
	 *
	 */
	@Override
	public void onAddedToWorld( World hostWorld )
	{
		m_world = hostWorld;
		m_bird = (Bird) hostWorld.findEntity( Bird.class );
		
		if ( m_bird != null )
		{
			Vector3 pos = m_bird.getPosition();
			float x = pos.m_x;
			float y = pos.m_y;
			
			setPosition( x, y + 26, 1 );
		}
	}

	@Override
	public void onEvent( EntityEvent event ) 
	{
		if ( event instanceof CollisionEvent )
		{
			if ( ((CollisionEvent)event).m_collider instanceof Pedestrian )
			{
				((CollisionEvent)event).m_collider.sendEvent( Crapped.class );
				pedestrianHit = true;
			}
		}
	}
}