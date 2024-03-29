/**
 * 
 */
package com.hypefoundry.engine.renderer2D.fx;

import com.hypefoundry.engine.math.Vector3;
import com.hypefoundry.engine.renderer2D.Color;
import com.hypefoundry.engine.renderer2D.RenderState;
import com.hypefoundry.engine.renderer2D.Spline;
import com.hypefoundry.engine.renderer2D.SpriteBatcher;

/**
 * An effect of a lightning.
 * 
 * @author Paksas
 *
 */
public class LightningFX 
{
	private Spline 				m_spline;
	private float				m_width;
	
	private Spline				m_particles;
	private Spline				m_bgWorldSpaceParticles;
	private Spline				m_fgWorldSpaceParticles;
	private Vector3				m_tmpPos = new Vector3();
	
	private Color				m_bgColor;
	private Color				m_fgColor;
	private RenderState			m_bgRenderState = new RenderState();
	private RenderState			m_fgRenderState = new RenderState();
	
	private float				m_slideSpeed = 0.06f;
	private float				m_timeToNextSlide = 0.0f;
	
	private float				m_pointRandomizationSpeed = 0.18f;
	private float				m_timeToNextPoint = 0.0f;
	private float				m_prevPointY = 0.0f;
	private float				m_nextPointY = 0.0f;
	private float				m_trendY = 0.0f;
	
	
	/**
	 * Constructor.
	 * 
	 * @param spline			spline the lightning should follow
	 * @param length
	 * @param width
	 * @param particlesCount
	 * @param color
	 */
	public LightningFX( Spline spline, float length, float width, int particlesCount, Color color )
	{
		m_spline = spline;
		
		// set the render states
		m_bgColor = new Color( color ).brighter( 0.5f );
		m_fgColor = color;
		m_bgRenderState.setLineWidth( 7.0f );
		m_fgRenderState.setLineWidth( 2.0f );
		
		// make sure the parameters are correct
		if ( particlesCount <= 0 )
		{
			particlesCount = 2;
		}
		if ( length <= 0 )
		{
			length = 0.1f;
		}
		
		m_width = width;
		if ( m_width <= 0 )
		{
			m_width = 0.1f;
		}
		
		// calculate the base placement of the particles
		m_particles = new Spline();
		m_bgWorldSpaceParticles = new Spline();
		m_fgWorldSpaceParticles = new Spline();
		float scatterDist = length / particlesCount;
		float x, y;
		x = 0;
		for ( int i = 0; i < particlesCount; ++i )
		{
			if ( i == 0 || i == particlesCount - 1 )
			{
				y = 0;
			}
			else
			{
				y = (float)( Math.random() - 0.5 ) * m_width;
			}

			m_particles.addPoint( new Vector3( x, y, 0 ), null );
			x += scatterDist;
			
			m_bgWorldSpaceParticles.addPoint( new Vector3(), m_bgColor );
			m_fgWorldSpaceParticles.addPoint( new Vector3(), m_fgColor );
		}
	}
	
	/**
	 * Draws the spline on the screen.
	 * 
	 * @param batcher
	 * @param offset
	 * @param deltaTime
	 */
	public void draw( SpriteBatcher batcher, float offset, float deltaTime )
	{
		// when the time comes, generate the next curve point
		{
			m_timeToNextPoint -= deltaTime;
			if ( m_timeToNextPoint <= 0.0f )
			{
				m_prevPointY = m_nextPointY;
				m_nextPointY = (float)( Math.random() - 0.5 ) * m_width;
				m_trendY = m_nextPointY - m_prevPointY;
				m_timeToNextPoint = m_pointRandomizationSpeed;
			}
		}
				
		// update the initial positions
		m_timeToNextSlide -= deltaTime;
		if ( m_timeToNextSlide <= 0.0f )
		{
			for ( int i = 2; i < m_particles.m_points.length - 1; ++i )
			{
				m_particles.m_points[i - 1].m_y = m_particles.m_points[i].m_y; 
			}
			
			// generate the next point
			m_particles.m_points[m_particles.m_points.length - 2].m_y = m_prevPointY + m_trendY * ( ( m_pointRandomizationSpeed - m_timeToNextPoint ) / m_pointRandomizationSpeed );
			
			// refresh the spline
			m_particles.refresh();
			
			m_timeToNextSlide = m_slideSpeed;
		}
	
		
		// create a world-space copy and draw them on the screen
		{
			for ( int i = 0; i < m_particles.m_points.length; ++i )
			{
				m_tmpPos.set( m_particles.m_points[i] );
				m_tmpPos.m_x += offset;
				
				m_spline.transform( m_tmpPos, m_bgWorldSpaceParticles.m_points[i] );
				m_spline.transform( m_tmpPos, m_fgWorldSpaceParticles.m_points[i] );
			}
			// refresh the spline
			m_bgWorldSpaceParticles.refresh();
			m_fgWorldSpaceParticles.refresh();
			
			// draw
			batcher.drawSpline( 0, 0, m_bgWorldSpaceParticles, m_bgRenderState );
			batcher.drawSpline( 0, 0, m_fgWorldSpaceParticles, m_fgRenderState );
		}
	}
}
