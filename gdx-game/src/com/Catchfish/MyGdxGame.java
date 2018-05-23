package com.Catchfish;

import com.badlogic.gdx.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import android.content.pm.*;
import android.os.*;
import java.util.*;
import com.badlogic.gdx.math.*;
import android.graphics.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;

public class MyGdxGame implements ApplicationListener
{
	Array<Sprite> start,worker,star,openbox,help,ending,counter,bonusgame,bg;
	TextureAtlas atlas;
	Animation starfish,optimus,fish,cookie,box,boom,explore;
	BitmapFont font;
	OrthographicCamera camera;
	SpriteBatch batch;
	int height,width,startgame,positioncat,status,t,d,maxitem=1000,timeclock,type=6;
	int[] count;
	float time,endtime;
	boolean started,ended;
	Vector3[] item;
	
	@Override
	public Array<Sprite> Open(TextureAtlas atlas,String name)
	{
		Array<Sprite> tmp=atlas.createSprites(name);
		return tmp;
	}
	
	@Override
	public void Clock(int x,int y)
	{
		int i=0;
		do 
		{
			font.draw(batch,(x%10)+"",width/8-i,y);
			x/=10;
			i+=7;
		} while(x>0);
	}
	
	@Override
	public void animationcat()
	{
		positioncat+=Gdx.input.getDeltaX();
		if(positioncat<0) positioncat=0;
		if(positioncat>width*9/10) positioncat=width*9/10;
		if(Gdx.input.getDeltaX()!=0) status=1-status;
		else status=0;
	}
	
	@Override
	public void createitem()
	{
		int h=height;
		for(int i=0;i<maxitem;i++)
		{
			int x;
			if(new Random().nextInt(1000)>=499+i/2) x=new Random().nextInt(4)+1;
			else x=5;
			h+=height/8+new Random().nextInt(height/8);
			item[i]=new Vector3().add(x,new Random().nextInt(width*11/12),h);
		}
		item[100].x=0;
		d=0;
		t=0;
	}
	
	@Override
	public void endgame()
	{
		int i;
		ended=true;
		status=5;
		for(i=d;item[i].z-time*height/2<=height;i++);
		for(;i<maxitem;i++)
			item[i]=new Vector3().add(0,0,0);
	}
	
	@Override
	public void Checkdrop(int i)
	{
		Rectangle v1=new Rectangle(item[i].y,item[i].z-time*height/2,75,80);
		Rectangle v2=new Rectangle(positioncat,height*9/20-1,width/10,1);
		if(v1.overlaps(v2))
		{
			switch((int) item[i].x)
			{
				case 0:
					count[0]=new Random().nextInt(50)+50;
					count[type-1]+=count[0];
					break;
				case 1:
					count[type-1]+=5;
					count[3]++;
					break;
				case 2:
					count[type-1]+=2;
					count[2]++;
					break;
				case 3:
					count[type-1]++;
					count[1]++;
					break;
				case 4:
					if(count[type-1]>0) count[type-1]--;
					count[4]++;
					break;
				case 5:
					endgame();
					break;
			}
			item[i]=new Vector3().add(0,0,0);
		}
	}

	@Override
	public void create()
	{
		atlas=new TextureAtlas(Gdx.files.internal("minigame.txt"));
		start=Open(atlas,"start");
		worker=Open(atlas,"worker");
		star=Open(atlas,"star");
		openbox=Open(atlas,"openbox");
		starfish=new Animation(0.2f,atlas.findRegions("itemstarfish"));
		boom=new Animation(0.2f,atlas.findRegions("itemboom"));
		fish=new Animation(0.2f,atlas.findRegions("itemfish"));
		box=new Animation(0.2f,atlas.findRegions("itembox"));
		optimus=new Animation(0.2f,atlas.findRegions("itemoptimus"));
		cookie=new Animation(0.2f,atlas.findRegions("itemcookie"));
		explore=new Animation(0.2f,atlas.findRegions("explore"));
		help=Open(atlas,"help");
		ending=Open(atlas,"ending");
		counter=Open(atlas,"counter");
		bonusgame=Open(atlas,"bonusgame");
		bg=Open(atlas,"bg");
		font=new BitmapFont();
		width=Gdx.graphics.getWidth();
		height=Gdx.graphics.getHeight();
		batch = new SpriteBatch();
		camera=new OrthographicCamera();
		resetGame();
	}
	
	@Override
	public void DrawItem(Animation tmp,int i)
	{
		batch.draw(tmp.getKeyFrame(2-item[i].z*2/height+time,true),item[i].y,item[i].z-time*height/2,width/12,height/8);
	}
	
	@Override
	public void DrawExplore(Animation tmp,int i)
	{
		batch.draw(tmp.getKeyFrame(0.4f+time-item[i].z*2/height,true),item[i].y,height/5,width/12,height/8);
	}
	
	@Override
	public void DrawSprite(Sprite a,int x,int y,int w,int h) {
		a.setSize(w,h);
		a.setPosition(x,y);
		a.draw(batch);
	}
	
	@Override
	public void ShowItem()
	{
		while(item[d].z-time*height/2<-height/10 && d<maxitem-1) d++;
		while(item[t].z-time*height/2<height/5 && t<maxitem-1) {
			item[t].x=6;
			t++;
		}
		for(int i=d;i<maxitem-1 && item[i].z-time*height/2<=height;i++)
		{
			if(!ended) Checkdrop(i);
			switch((int) item[i].x)
			{
				case 0:
					DrawItem(box,i);
					break;
				case 1:
					DrawItem(cookie,i);
					break;
				case 2:
					DrawItem(optimus,i);
					break;
				case 3:
					DrawItem(fish,i);
					break;
				case 4:
					DrawItem(starfish,i);
					break;
				case 5:
					DrawItem(boom,i);
					break;
				case 6:
					DrawExplore(explore,i);
					break;
			}
		}
	}

	@Override
	public void render()
	{
	    Gdx.gl.glClearColor(1, 1, 1, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		DrawSprite(bg.get(0),0,0,width,height);
		DrawSprite(worker.get(status),positioncat,height/5,width/10,height/4);
		if(!started)
		{
			DrawSprite(bonusgame.get(0),width/4,height*3/8,width/2,height/4);
			font.draw(batch,"<touch>",width/2-25,height*5/16);
			DrawSprite(help.get(0),width/8,0,width*3/4,height/5);
			if(Gdx.input.isTouched()) started=true;
		}
		else
		{
			time+=Gdx.graphics.getDeltaTime();
			if(!ended) timeclock=(int) time;
			if(startgame>0)
			{
				if(startgame==1) DrawSprite(start.get(3),width*3/8,height*7/16,width/4,height/8);
				else DrawSprite(start.get(startgame-2),width*7/16,height*7/16,width/8,height/8);
				DrawSprite(help.get(0),width/8,0,width*3/4,height/5);
				if(time>1)
				{
					startgame--;
					time-=1;
				}
			}
			else
			{
				if(!ended) animationcat();
				DrawSprite(counter.get(0),0,height*7/8,width/6,height/8);
				Clock(timeclock,height*63/64);
				Clock(count[type-1],height*59/64);
				ShowItem();
			}
		}
		if(ended)
		{
			endtime+=Gdx.graphics.getDeltaTime();
			if(status>3 && endtime>0.5)
			{
				status--;
				endtime-=0.5;
			}
			if(d==maxitem-1)
			{
				DrawSprite(ending.get(0),width/8,height/8,width*3/4,height*3/4);
				font.draw(batch,""+(int) (time-endtime),width*3/4,height*3/4);
				for(int i=1;i<type-1;i++)
					font.draw(batch,""+ count[i],width/16+width*15/128*i,height*37/64);
				font.draw(batch,""+ (count[type-1]-count[0]),width*3/4,height*37/64);
				font.draw(batch,""+ count[0],width*3/4,height*26/64);
				font.draw(batch,""+(int) ((time-endtime)+count[type-1]),width*3/4,height*15/64);
				font.draw(batch,"<touch to play again>",width/2-77,height/16);
				if(Gdx.input.isTouched()) resetGame();
			}
		}
		batch.end();
	}

	@Override
	public void dispose()
	{
		batch.dispose();
		font.dispose();
		atlas.dispose();
	}

	@Override
	public void ConfigurationCamera()
	{
		camera.setToOrtho(false,width,height);
	}

	@Override
	public void resetGame()
	{
		startgame=4;
		positioncat=width*9/20;
		status=0;
		count=new int[type];
		endtime=0;
		time=0;
		timeclock=0;
		item=new Vector3[maxitem];
		started=false;
		ended=false;
		createitem();
		ConfigurationCamera();
	}

	@Override
	public void resize(int width, int height)
	{
		ConfigurationCamera();
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}
}
