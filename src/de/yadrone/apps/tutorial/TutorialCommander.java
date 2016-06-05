package de.yadrone.apps.tutorial;

import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.LEDAnimation;

public class TutorialCommander
{

	private IARDrone drone;

	public TutorialCommander(IARDrone drone)
	{
		this.drone = drone;
	}

	public void animateLEDs()
	{
		drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_ORANGE, 3, 10);
	}
	
	public void takeOffAndLand()
	{
		drone.getCommandManager().takeOff();
		drone.getCommandManager().hover().doFor(10000);
		
//		drone.getCommandManager().up(30).doFor(1000);
//		drone.getCommandManager().forward(30).doFor(1000);
//		drone.getCommandManager().hover().doFor(2000);
//		drone.getCommandManager().spinRight(100).doFor(2000);
		drone.getCommandManager().landing();
		
	}
	
//	public void leftRightForwardBackward()
//	{
//		final CommandManager cmd = drone.getCommandManager();
//		final int speed = 10; // percentage of max speed
//			
//		cmd.takeOff();
//		cmd.up(speed).doFor(1000);
//		
////		cmd.goLeft(speed).doFor(1000);
////		cmd.hover().doFor(2000);
////		
////		cmd.goRight(speed).doFor(1000);
////		cmd.hover().doFor(2000);
//		
//		cmd.forward(speed).doFor(1000);
////		cmd.hover().doFor(1000);
//		
//		
////		cmd.backward(speed).doFor(2000);
////		cmd.hover().doFor(2000);
//		
//		cmd.landing();
//		
//		// alternative: asynchronous call
////		cmd.takeOff();
////		cmd.schedule(5000, new Runnable() { // schedule to be executed in 5 secs
////			public void run()
////			{
////				cmd.goLeft(speed);
////				// [...]
////			}			
////		});
//	}
}
