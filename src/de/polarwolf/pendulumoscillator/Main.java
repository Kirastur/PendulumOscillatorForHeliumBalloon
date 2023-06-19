package de.polarwolf.pendulumoscillator;

import org.bukkit.plugin.java.JavaPlugin;

import de.polarwolf.heliumballoon.api.HeliumBalloonAPI;
import de.polarwolf.heliumballoon.api.HeliumBalloonProvider;

public final class Main extends JavaPlugin {

	protected PendulumBehavior pendulumBehavior;

	@Override
	public void onEnable() {
		saveDefaultConfig();

		HeliumBalloonAPI api = HeliumBalloonProvider.getAPI();
		api.addReloadRegistration(this, null, null);

		pendulumBehavior = new PendulumBehavior(api.getBehaviorHelper());
		api.addBehaviorDefinition(pendulumBehavior);
		api.addCompatibility(api.findElementDefinition("minecart"), pendulumBehavior, api.findBalloonDefinition("rotators"));

		this.getLogger().info("Added pendulum as a new oscillator type.");
	}

}
