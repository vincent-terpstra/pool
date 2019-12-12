package com.vdt.poolgame.library;

import com.badlogic.gdx.utils.TimeUtils;

/**
 * @author Vincent Terpstra
 * @date Jan 27, 2019
 * @file TimeStep.java
 * @class TimeStep
 *   implements a discrete update step for a game clock
 *
 */
public class TimeStep {
	public TimeStep() {
		this(1/60f); //100 updates per second
	}
	public TimeStep(float delta){
		this.delta = delta;
		this.frame = (long)(delta * 1000000000l);
		this.pastTime = TimeUtils.nanoTime();
	}
	private 	  long pastTime;
	private		  long bank;
	private final long frame;	//millisecond time for update
	public  final float delta;  //second time for update
	private boolean synced = false;
	
	public boolean update(){
		if(!synced) {
			long time = TimeUtils.nanoTime();
			bank += time - pastTime;
			pastTime = time;
			//if there is more then 100 frames banked throw out bank
			if(Math.abs(bank) > 10 * frame ) {
				bank = 0;
			}
			synced = true;
		}
		if(bank > frame / 2 ){ 
			bank -= frame;
			return true;
		} 
		return (synced = false);
	}
}
