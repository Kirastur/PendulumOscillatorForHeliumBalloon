package de.polarwolf.pendulumoscillator;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import de.polarwolf.heliumballoon.oscillators.SimpleOscillator;

public class PendulumOscillator extends SimpleOscillator {

	protected static final int MAX_MICROSTEPS = 10000;

	protected final Plugin plugin;
	protected final double pendulumLength;
	protected final double pendulumAmplitude;
	protected final double pendulumDuration;

	protected final List<Double> microSteps = new ArrayList<>();
	protected final List<Double> angles = new ArrayList<>();
	protected final List<Double> deltaXs = new ArrayList<>();
	protected final List<Double> deltaYs = new ArrayList<>();

	public PendulumOscillator(Plugin plugin, double pendulumLength, double pendulumAmplitude, double pendulumDuration) {
		this.plugin = plugin;
		this.pendulumLength = pendulumLength;
		this.pendulumAmplitude = pendulumAmplitude;
		this.pendulumDuration = pendulumDuration;
	}

	// Here I will do the mathematics.
	// We can't use the Small-angle approximation here,
	// because the given angle may be to large for a harmonic oscillator.
	// So we try a numeric approach by using microsteps.
	// Please see Wikipedia for more information:
	// https://de.wikipedia.org/wiki/Mathematisches_Pendel
	protected void calculateMicroSteps(double h) {
		double g = 10.0;
		double angle = Math.toRadians(pendulumAmplitude);
		double angularVelocity = 0;
		while ((angle >= 0) && (microSteps.size() < MAX_MICROSTEPS)) {
			microSteps.add(Double.valueOf(angle));
			angularVelocity = angularVelocity - (g * h / pendulumLength) * Math.sin(angle);
			angle = angle + h * angularVelocity;
		}
	}

	// Try do determine, how many microsteps we need.
	// 100 per Minecraft Tick should be enough.
	protected void iterateMicroSteps() {
		double h = 1;
		while ((microSteps.size() < (pendulumDuration / 4) * 20 * 100) && (microSteps.size() < MAX_MICROSTEPS)) {
			h = h / 2;
			microSteps.clear();
			calculateMicroSteps(h);
		}
	}

	// Sine now we have the microsteps, we can calculate our movement.
	// Remember: Index=0 is Maximal angle
	// Angle=0 is not covered by the microsteps, we must add them later.
	protected void calculateMovement() {
		int t = (int) ((pendulumDuration / 4) * 20);
		int step = microSteps.size() / t;
		for (int i = 0; i < t; i++) {
			double myAngle = microSteps.get(i * step);
			double myDeltaX = Math.sin(myAngle) * pendulumLength;
			double myDeltaY = (1 - Math.cos(myAngle)) * pendulumLength;
			angles.add(Double.valueOf(myAngle));
			deltaXs.add(Double.valueOf(myDeltaX));
			deltaYs.add(Double.valueOf(myDeltaY));
		}
	}

	// Write my calculated data to the Oscillator cache
	protected void fillOscillatorData() {

		// Add resting position
		addDeflection(new Vector());
		addMinecartSpin(new EulerAngle(0, 0, 0));

		// Move upwards on the right, without topmost position.
		for (int i = angles.size() - 1; i > 0; i--) {
			addDeflection(new Vector(deltaXs.get(i), deltaYs.get(i), 0));
			addMinecartSpin(new EulerAngle(0, 0, angles.get(i)));
		}

		// Move down, include topmost.
		for (int i = 0; i < angles.size(); i++) {
			addDeflection(new Vector(deltaXs.get(i), deltaYs.get(i), 0));
			addMinecartSpin(new EulerAngle(0, 0, angles.get(i)));
		}

		// Add resting position
		addDeflection(new Vector());
		addMinecartSpin(new EulerAngle(0, 0, 0));

		// Move upwards on the left, without topmost position.
		for (int i = angles.size() - 1; i > 0; i--) {
			addDeflection(new Vector(-deltaXs.get(i), deltaYs.get(i), 0));
			addMinecartSpin(new EulerAngle(0, 0, -angles.get(i)));
		}

		// Move down, include topmost.		
		for (int i = 0; i < angles.size(); i++) {
			addDeflection(new Vector(-deltaXs.get(i), deltaYs.get(i), 0));
			addMinecartSpin(new EulerAngle(0, 0, -angles.get(i)));
		}
	}

	protected void cleanup() {
		microSteps.clear();
		angles.clear();
		deltaXs.clear();
		deltaYs.clear();
	}

	@Override
	protected void prepare() {
		iterateMicroSteps();
		calculateMovement();
		fillOscillatorData();
		cleanup();
		setPrepared();
	}

}
