/**
 * 
 */
package com.hypefoundry.engine.math;

/**
 * A bounding shape interface.
 * 
 * @author paksas
 *
 */
public interface BoundingShape 
{
	/**
	 * Returns the width of the shape.
	 * 
	 * @return
	 */
	float getWidth();
	
	/**
	 * Returns the height of the shape.
	 * 
	 * @return
	 */
	float getHeight();
	
	/**
	 * Returns the geometrical center of mass.
	 * @return
	 */
	Vector3 getMassCenter();
	
	/**
	 * Checks if the shape overlaps a sphere ( in 2D ).
	 * 
	 * @param sphere
	 * @return
	 */
	boolean doesOverlap2D( BoundingSphere sphere );

	/**
	 * Checks if the shape overlaps a box ( in 2D ).
	 * 
	 * @param box
	 * @return
	 */
	boolean doesOverlap2D( BoundingBox box );
	
	/**
	 * Checks if the shape overlaps a point ( in 2D ).
	 * 
	 * @param point
	 * @return
	 */
	boolean doesOverlap2D( Vector3 point );
	
	/**
	 * Checks if the shape overlaps a sphere.
	 * 
	 * @param sphere
	 * @return
	 */
	boolean doesOverlap( BoundingSphere sphere );

	/**
	 * Checks if the shape overlaps a box.
	 * 
	 * @param box
	 * @return
	 */
	boolean doesOverlap( BoundingBox box );
	
	/**
	 * Checks if the shape overlaps a point.
	 * 
	 * @param point
	 * @return
	 */
	boolean doesOverlap( Vector3 point );
}
