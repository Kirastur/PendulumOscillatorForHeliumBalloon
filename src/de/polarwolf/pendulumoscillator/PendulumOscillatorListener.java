package de.polarwolf.pendulumoscillator;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import de.polarwolf.heliumballoon.config.ConfigRule;
import de.polarwolf.heliumballoon.config.ConfigSection;
import de.polarwolf.heliumballoon.events.BalloonOscillatorCreateEvent;
import de.polarwolf.heliumballoon.events.BalloonRebuildConfigEvent;
import de.polarwolf.heliumballoon.exception.BalloonException;

public class PendulumOscillatorListener implements Listener {

	public static final String HINT_PENDULUM = "pendulum";

	public static final double PENDULUM_LENGTH = 5.0; // in Blocks
	public static final double PENDULUM_AMPLITUDE = 60.0; // in Degree
	public static final double PENDULUM_DURATION = 3.0; // in Seconds

	protected final Plugin plugin;

	public PendulumOscillatorListener(Plugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	// This event is called every time when a new Oscillator should be created
	// as part of a balloon.
	@EventHandler(priority = EventPriority.NORMAL)
	public void onOscillatorCreateEvent(BalloonOscillatorCreateEvent event) {

		// Get the rule from the event.
		ConfigRule rule = event.getConfigRule();

		// Get the oscillatorHint from the rule.
		String oscillatorHint = rule.getOscillatorHint();

		// Check if the hint matches to us, return if not.
		if ((oscillatorHint == null) || !oscillatorHint.equalsIgnoreCase(HINT_PENDULUM)) {
			return;
		}

		// Override the existing oscillator with our own PendulumOscillator.
		// We use static values for the pendulum parameters here, this simplify things.
		event.setOscillator(new PendulumOscillator(plugin, PENDULUM_LENGTH, PENDULUM_AMPLITUDE, PENDULUM_DURATION));
	}

	// This event reloads our custom config from our own config.yml.
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBalloonRebuildConfigEvent(BalloonRebuildConfigEvent event) {
		try {

			// Don't load our config if the Rebuild is already cancelled
			if (event.isCancelled()) {
				return;
			}

			// reload our config.yml
			plugin.reloadConfig();

			// Build our Balloon ConfigSection using the standard syntax
			ConfigSection newSection = event.buildConfigSectionFromConfigFile(plugin);

			// Add my new ConfigSection to the list of valid sections
			event.addSection(newSection);

		} catch (BalloonException be) {
			be.printStackTrace();
			event.cancelWithReason(be);
		} catch (Exception e) {
			e.printStackTrace();
			event.cancelWithReason(new BalloonException(null, BalloonException.JAVA_EXCEPTION, null, e));
		}
	}

}
