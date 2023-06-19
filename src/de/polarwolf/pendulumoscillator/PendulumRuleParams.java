package de.polarwolf.pendulumoscillator;

import static de.polarwolf.heliumballoon.tools.helium.HeliumParamType.STRING;

import de.polarwolf.heliumballoon.tools.helium.HeliumParam;
import de.polarwolf.heliumballoon.tools.helium.HeliumParamType;

public enum PendulumRuleParams implements HeliumParam {

	PENDULUM_AMPLITUDE(STRING, "pendulum_amplitude"),
	PENDULUM_LENGTH(STRING, "pendulum_length"),
	PENDULUM_DURATION(STRING, "pendulum_duration");

	private final HeliumParamType paramType;
	private final String attributeName;

	private PendulumRuleParams(HeliumParamType paramType, String attributeName) {
		this.paramType = paramType;
		this.attributeName = attributeName;
	}

	@Override
	public boolean isType(HeliumParamType testParamType) {
		return testParamType == paramType;
	}

	@Override
	public String getAttributeName() {
		return attributeName;
	}

}
