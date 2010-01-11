package com.badlogic.gamedev.spaceinvaders;

public class Explosion 
{
	public static float EXPLOSION_LIVE_TIME = 1;
	public float aliveTime = 0;
	public final Vector position = new Vector( );
	
	public Explosion( Vector position )
	{
		this.position.set( position );
	}
	
	public void update( float delta )
	{
		aliveTime += delta;
	}
}