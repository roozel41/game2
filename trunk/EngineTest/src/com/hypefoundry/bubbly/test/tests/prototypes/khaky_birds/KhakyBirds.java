package com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds;


import com.hypefoundry.engine.game.Screen;
import com.hypefoundry.engine.impl.game.AndroidGame;


public class KhakyBirds extends AndroidGame 
{
	@Override
	public Screen getStartScreen()
	{
		return new MyScreen( this );
	}
}

