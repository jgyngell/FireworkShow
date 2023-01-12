package org.jgcertified.fireworkshow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.Color;
import org.bukkit.entity.EntityType;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.util.Vector;
import org.redstonechips.RCPrefs;
import org.redstonechips.circuit.Circuit;
import org.redstonechips.chip.io.InterfaceBlock;
import org.redstonechips.util.BooleanArrays;
import org.redstonechips.util.BooleanSubset;
import org.redstonechips.util.Locations;
import org.redstonechips.wireless.Receiver;

public class firework extends Circuit {


	static boolean isEnabled;

	static int height, runTime, xSpan, zSpan;

	static Random rnd;

	@Override
	public void input(boolean state, int inIdx) {

		if(state)
		{
			isEnabled = true;
			triggerFirework();
			rc.getServer().getScheduler().scheduleSyncDelayedTask(rc, new EndShow(), runTime * 20);
		}
	}

	@Override
	public Circuit init(String[] args) {
		if (inputlen!=1) return error("Incorrect inputs. Expecting 1 trigger pin.");

		if (chip.interfaceBlocks.length != 1) return error("Expecting 1 interface block.");

		rnd = new Random();

		height = 5;
		xSpan = zSpan = 14;
		runTime = 30;

		int ret;

		for (int i = 0; i < args.length; i++)
		{
			if (args[i].startsWith("d{") && args[i].endsWith("}"))
			{
				ret = parseArg(args[i]);
				if (ret == -1) return error("Bad distance argument: " + args[i] + ". Expecting d{<distance>}.");
				xSpan = zSpan = ret;	
			} else if (args[i].startsWith("h{") && args[i].endsWith("}"))
			{
				ret = parseArg(args[i]);
				if (ret == -1) return error("Bad height argument: " + args[i] + ". Expecting h{<height>}.");
				height = ret;	
			
			} else if (args[i].startsWith("t{") && args[i].endsWith("}"))
			{			
				ret = parseArg(args[i]);
				if (ret == -1) return error("Bad time argument: " + args[i] + ". Expecting t{<time>}.");
				runTime = ret;	
			} else {
				info("Unknown argument, ignoring: " + args[i]);
			}
		}

		info("Creating firework show that lasts " + runTime + " seconds, " + height + " blocks above the chip on a plane of " + xSpan + "x" + zSpan + " blocks.");
		
		return this;
	}

	private int parseArg(String arg)
	{
		int ret; 

		try {
			ret = Integer.decode(arg.substring(arg.indexOf("{") + 1, arg.length() - 1));
		} catch (NumberFormatException ne) {
			ret = -1;
		}

		return ret;
	}

	public static void spawnFirework(Location location) {
		Location loc = location;

		if(!loc.getChunk().isLoaded()) return;

		int numEffect = rnd.nextInt(3) + 1;
		for (int x = 0; x < numEffect; x++)
		{
			Firework fw = (Firework) loc.getWorld().spawnEntity(loc.clone().add(rnd.nextInt(3), 0, rnd.nextInt(3)), EntityType.FIREWORK);
			FireworkMeta fwm = fw.getFireworkMeta();

			fwm.setPower(rnd.nextInt(3));

			fwm.addEffect(FireworkEffect.builder()
					.withColor(Color.fromRGB(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)))
					.flicker(rnd.nextBoolean())
					.trail(rnd.nextBoolean())
					.with(FireworkEffect.Type.values()[rnd.nextInt(FireworkEffect.Type.values().length)])
					.build());

			fw.setFireworkMeta(fwm);
		}
	}

	private Location getRandomLocation()
	{

		for (InterfaceBlock i : chip.interfaceBlocks) {
			if (!i.getLocation().getChunk().isLoaded()) return null;

			Location l = i.getLocation().clone().subtract(xSpan / 2, 0, zSpan / 2).add(rnd.nextInt(xSpan), height, rnd.nextInt(zSpan));

			return l;

		}

		return null;

	}

	private void triggerFirework() {
		for (InterfaceBlock i : chip.interfaceBlocks)
			if (!i.getLocation().getChunk().isLoaded()) return;

		Location l = getRandomLocation();
		if (l == null) return;

		rc.getServer().getScheduler().scheduleSyncDelayedTask(rc, new DoFirework(l), 5 + rnd.nextInt(25));
	}

	class EndShow implements Runnable {

		public EndShow() { }

		@Override
		public void run() {
			isEnabled = false;
		}
	}

	class DoFirework implements Runnable {
		Location loc;

		public DoFirework(Location loc) { this.loc = loc; }

		@Override
		public void run() {
			spawnFirework(loc);
			if(isEnabled)
				triggerFirework();
		}
	}
}
