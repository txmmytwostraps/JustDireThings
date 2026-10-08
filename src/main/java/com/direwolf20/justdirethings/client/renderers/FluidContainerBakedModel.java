package com.direwolf20.justdirethings.client.renderers;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.BakedModelWrapper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Restores the item transforms the forge:bucket loader leaves out, or dropped containers bob at full size.
 * Override models are wrapped too, since dropped items draw those.
 */
public class FluidContainerBakedModel extends BakedModelWrapper<BakedModel> {
	private final ItemTransforms transforms;
	private final Map<BakedModel, BakedModel> resolvedModels;
	private final ItemOverrides overrides = new ResolvedModelOverrides();

	public FluidContainerBakedModel(BakedModel originalModel, ItemTransforms transforms) {
		this(originalModel, transforms, new HashMap<>());
	}

	private FluidContainerBakedModel(BakedModel originalModel, ItemTransforms transforms, Map<BakedModel, BakedModel> resolvedModels) {
		super(originalModel);
		this.transforms = transforms;
		this.resolvedModels = resolvedModels;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public ItemTransforms getTransforms() {
		return transforms;
	}

	@Override
	public ItemOverrides getOverrides() {
		return overrides;
	}

	private final class ResolvedModelOverrides extends ItemOverrides {
		@Nullable
		@Override
		public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
			BakedModel resolved = originalModel.getOverrides().resolve(originalModel, stack, level, entity, seed);
			if (resolved == originalModel)
				return FluidContainerBakedModel.this;
			return resolved == null ? null : resolvedModels.computeIfAbsent(resolved, r -> new FluidContainerBakedModel(r, transforms, resolvedModels));
		}
	}
}
