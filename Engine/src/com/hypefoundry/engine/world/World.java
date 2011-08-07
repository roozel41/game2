package com.hypefoundry.engine.world;

import java.util.*;

import com.hypefoundry.engine.game.Updatable;

import android.util.Log;


/**
 * Represents the game world.
 * 
 * @author paksas
 *
 */
public class World implements Updatable
{
	private float		m_width;
	private float		m_height;
	List< Entity >		m_entities;
	List< Entity >		m_entitiesToAdd;
	List< Entity >		m_entitiesToRemove;
	List< WorldView >	m_views;
	
	/**
	 * Constructor.
	 * 
	 * @param width			world width
	 * @param height		world height
	 */
	public World( float	width, float height )
	{
		m_width = width;
		m_height = height;
		
		m_entities = new ArrayList< Entity >();
		m_entitiesToAdd = new ArrayList< Entity >();
		m_entitiesToRemove = new ArrayList< Entity >();
		m_views = new ArrayList< WorldView >();
	}
	
	
	// TODO: instead of these methods being polled all the time, 
	// send events to entities that are outside the world bounds
	
	/**
	 * Returns the width of the world.
	 * 
	 * @return
	 */
	public float getWidth()
	{
		return m_width;
	}
	
	/**
	 * Returns the height of the world.
	 * 
	 * @return
	 */
	public float getHeight()
	{
		return m_height;
	}
	
	/**
	 * Adds a new entity to the world.
	 * 
	 * @param entity
	 */
	public void addEntity( Entity entity )
	{
		m_entitiesToRemove.remove( entity );
		m_entitiesToAdd.add( entity );
	}
	
	/**
	 * Removes the entity from the world
	 * 
	 * @param entity
	 */
	public void removeEntity( Entity entity )
	{
		m_entitiesToRemove.add( entity );
		m_entitiesToAdd.remove( entity );
	}
	
	/**
	 * Actual attachment of an entity to the world.
	 * 
	 * @param entity
	 */
	private void attachEntity( Entity entity )
	{
		if ( m_entities.indexOf( entity ) < 0 )
		{
			m_entities.add( entity );
			
			// inform the entity itself
			entity.onAddedToWorld( this );
		}
		
		// notify the views
		for( WorldView view : m_views )
		{
			view.onEntityAdded( entity );
		}
	}
	
	/**
	 * Actual detachment of an entity to the world.
	 * 
	 * @param entity
	 */
	public void detachEntity( Entity entity )
	{		
		if ( m_entities.remove( entity ) )
		{
			// inform the entity itself
			entity.onRemovedFromWorld( this );
			
			// notify the views
			for( WorldView view : m_views )
			{
				view.onEntityRemoved( entity );
			}
		}
	}
	
	/**
	 * Attaches a new view to the world.
	 * 
	 * @param newView
	 */
	public void attachView( WorldView view )
	{
		for( WorldView v : m_views )
		{
			if ( v.equals( view ) )
			{
				return;
			}
		}
		m_views.add( view );

		// inform the view that it's been attached to a world
		view.onAttached( this );
		
		// inform the view about all present entities
		for ( Entity entity : m_entities )
		{
			view.onEntityAdded( entity );
		}
	}
	
	/**
	 * Attaches a new view to the world.
	 * 
	 * @param newView
	 */
	public void detachView( WorldView view )
	{
		// remove all entities from the view
		for ( Entity entity : m_entities )
		{
			view.onEntityRemoved( entity );
		}
		
		// inform the view that it's been detached from a world
		view.onDetached( this );
		
		// remove the view
		m_views.remove( view );
	}
	
	/**
	 * Executes an operation on all registered entities.
	 * 
	 * @param operation
	 */
	public void executeOperation( EntityOperation operation )
	{
		for ( Entity entity : m_entities )
		{
			operation.visit( entity );
		}
	}
	
	/**
	 * Updates the state of the world.
	 * 
	 * @param deltaTime
	 */
	@Override
	public void update( float deltaTime )
	{	
		// execute world attachment & detachment - detach first to save
		// memory
		for ( Entity entity : m_entitiesToRemove )
		{
			detachEntity( entity );
		}
		m_entitiesToRemove.clear();
		
		for ( Entity entity : m_entitiesToAdd )
		{
			attachEntity( entity );
		}
		m_entitiesToAdd.clear();
		
		// process the entity events
		for ( Entity entity : m_entities )
		{
			entity.processEvents();
		}
	}
	
	// ------------------------------------------------------------------------
	// Utilities
	// ------------------------------------------------------------------------

	/**
	 * Looks for the first entity of the specified type
	 * 
	 * @param entityType
	 * @return
	 */
	public Entity findEntity( Class entityType ) 
	{
		for ( Entity entity : m_entities )
		{
			if ( entityType.isInstance( entity ) )
			{
				return entity;
			}
		}
		
		return null;
	}
}
