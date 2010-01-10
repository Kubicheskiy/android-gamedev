package com.badlogic.gamedev.spaceinvaders;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.util.Log;

import com.badlogic.gamedev.tools.GameActivity;
import com.badlogic.gamedev.tools.Mesh;
import com.badlogic.gamedev.tools.MeshLoader;
import com.badlogic.gamedev.tools.Texture;
import com.badlogic.gamedev.tools.Mesh.PrimitiveType;
import com.badlogic.gamedev.tools.Texture.TextureFilter;
import com.badlogic.gamedev.tools.Texture.TextureWrap;

public class Renderer 
{
	Mesh shipMesh;
	Texture shipTexture;
	Mesh invaderMesh;
	Texture invaderTexture;
	Mesh blockMesh;
	Mesh shotMesh;
	
	public Renderer( GL10 gl, GameActivity activity )
	{
		try
		{
			shipMesh = MeshLoader.loadObj(gl, activity.getAssets().open( "ship.obj" ) );
			invaderMesh = MeshLoader.loadObj( gl, activity.getAssets().open( "invader.obj" ) );
			blockMesh = MeshLoader.loadObj( gl, activity.getAssets().open( "block.obj" ) );
			shotMesh = MeshLoader.loadObj( gl, activity.getAssets().open( "block.obj" ) );
		}
		catch( Exception ex )
		{
			Log.d( "Space Invaders", "couldn't load meshes" );
			throw new RuntimeException( ex );
		}
		
		try
		{
			Bitmap bitmap = BitmapFactory.decodeStream( activity.getAssets().open( "ship.png" ) );
			shipTexture = new Texture( gl, bitmap, TextureFilter.MipMap, TextureFilter.Linear, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge );
			bitmap.recycle();
			
			bitmap = BitmapFactory.decodeStream( activity.getAssets().open( "invader.png" ) );
			invaderTexture = new Texture( gl, bitmap, TextureFilter.MipMap, TextureFilter.Linear, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge );
			bitmap.recycle();
		}
		catch( Exception ex )
		{
			Log.d( "Space Invaders", "couldn't load textures" );
			throw new RuntimeException( ex );
		}
		
		float[] lightColor = { 1, 1, 1, 1 };
		float[] ambientLightColor = {0.0f, 0.0f, 0.0f, 1 };		
		gl.glLightfv( GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientLightColor, 0 );
		gl.glLightfv( GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightColor, 0 );
		gl.glLightfv( GL10.GL_LIGHT0, GL10.GL_SPECULAR, lightColor, 0 );
	}
	
	public void render( GL10 gl, GameActivity activity, Simulation simulation )
	{		
		gl.glClear( GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT );
		gl.glViewport( 0, 0, activity.getViewportWidth(), activity.getViewportHeight() );		
			
		gl.glEnable( GL10.GL_DEPTH_TEST );
		gl.glEnable( GL10.GL_CULL_FACE );
		
		setProjectionAndCamera( gl, simulation.ship, activity );
		setLighting( gl );
		
		gl.glEnable( GL10.GL_TEXTURE_2D );		
		renderShip( gl, simulation.ship, activity );
		renderInvaders( gl, simulation.invaders );
		
		gl.glDisable( GL10.GL_TEXTURE_2D );
//		renderBlocks( gl, simulation.blocks );
		renderShots( gl, simulation.shots );
	}
	
	private void setProjectionAndCamera( GL10 gl, Ship ship, GameActivity activity )
	{
		gl.glMatrixMode( GL10.GL_PROJECTION );
		gl.glLoadIdentity();
		float aspectRatio = (float)activity.getViewportWidth() / activity.getViewportHeight();
		GLU.gluPerspective( gl, 67, aspectRatio, 1, 1000 );					
		
		gl.glMatrixMode( GL10.GL_MODELVIEW );
		gl.glLoadIdentity();
		GLU.gluLookAt( gl, ship.position.x, 6, 2, ship.position.x, 0, -4, 0, 1, 0 );
	}
	
	float[] direction = { 1, 0.5f, 0, 0 };	
	private void setLighting( GL10 gl )
	{
		gl.glEnable( GL10.GL_LIGHTING );
		gl.glEnable( GL10.GL_LIGHT0 );				
		gl.glLightfv( GL10.GL_LIGHT0, GL10.GL_POSITION, direction, 0 );
		gl.glEnable( GL10.GL_COLOR_MATERIAL );		
	}
	
	private void renderShip( GL10 gl, Ship ship, GameActivity activity )
	{
		shipTexture.bind();
		gl.glPushMatrix();
		gl.glTranslatef( ship.position.x, ship.position.y, ship.position.z );
		gl.glRotatef( 45 * (-activity.getAccelerationOnYAxis() / 5), 0, 0, 1 );
		gl.glRotatef( 180, 0, 1, 0 );
		shipMesh.render(PrimitiveType.Triangles);
		gl.glPopMatrix();
	}
	
	private void renderInvaders( GL10 gl, ArrayList<Invader> invaders )
	{
		invaderTexture.bind();
		for( int i = 0; i < invaders.size(); i++ )
		{
			Invader invader = invaders.get(i);
			gl.glPushMatrix();
			gl.glTranslatef( invader.position.x, invader.position.y, invader.position.z );
			invaderMesh.render(PrimitiveType.Triangles);
			gl.glPopMatrix();
		}
	}
	
	private void renderBlocks( GL10 gl, ArrayList<Block> blocks )
	{		
		for( int i = 0; i < blocks.size(); i++ )
		{
			Block block = blocks.get(i);
			gl.glPushMatrix();
			gl.glTranslatef( block.position.x, block.position.y, block.position.z );
			blockMesh.render(PrimitiveType.Triangles);
			gl.glPopMatrix();
		}		
	}
	
	private void renderShots( GL10 gl, ArrayList<Shot> shots )
	{
		for( int i = 0; i < shots.size(); i++ )
		{
			Shot shot = shots.get(i);
			gl.glPushMatrix();
			gl.glTranslatef( shot.position.x, shot.position.y, shot.position.z );
			shotMesh.render(PrimitiveType.Triangles);
			gl.glPopMatrix();
		}			
	}
	
	public void dispose( )
	{
		shipTexture.dispose();
		invaderTexture.dispose();
	}
}