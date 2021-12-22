package de.polarwolf.pendulumoscillator;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

	@Override
	public void onEnable() {
		saveDefaultConfig();
		new PendulumOscillatorListener(this);
		this.getLogger().info("Added pendulum as a new oscillator type.");
	}

}
