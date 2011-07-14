package com.hypefoundry.engine.renderer2D;

import com.hypefoundry.engine.core.Graphics;
import com.hypefoundry.engine.game.Entity;

/**
 * The visuals of an entity.
 * 
 * @author paksas
 *
 */
public abstract class EntityVisual 
{
	Entity			m_entity = null;
	
	/**
	 * Constructor.
	 * 
	 * @param entity			represented entity.
	 */
	public EntityVisual( Entity entity )
	{
		m_entity = entity;
	}
	
	/**
	 * Checks if the visual represents the specified entity.
	 * 
	 * @param entity
	 * @return
	 */
	public boolean isVisualOf( Entity entity )
	{
		return m_entity.equals( entity );
	}
	
	/**
	 * Draw self.
	 * 
	 * @param graphics
	 */
	public abstract void draw( Graphics graphics );
}