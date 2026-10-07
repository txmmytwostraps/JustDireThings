package com.direwolf20.justdirethings.util;

import com.direwolf20.justdirethings.setup.Config;
import net.minecraftforge.common.ForgeConfigSpec;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.List;

public class ConfigValueComponentProcessor implements IComponentProcessor {
	private String rawText;
	private List<String> options;

	@Override
	public void setup(IVariableProvider variables) {
		this.rawText = variables.get("text").asString();
		this.options = variables.get("config_options").asList().stream().map(IVariable::asString).toList();
	}

	@Override
	public IVariable process(String key) {
		if (!key.equals("text")) {
			return null;
		}
		String result = this.rawText;
		for (String option : this.options) {
			Object value = ((ForgeConfigSpec.ConfigValue<?>) Config.SERVER_CONFIG.getValues().get(option)).get();
			result = result.replace("#" + option + "#", String.valueOf(value));
		}
		return IVariable.wrap(result);
	}
}
