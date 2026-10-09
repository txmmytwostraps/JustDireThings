package com.direwolf20.justdirethings.common.items.interfaces;

public interface PoweredTool extends PoweredItem {
	default int getBlockBreakFECost() {
		return 50;
	}
}
