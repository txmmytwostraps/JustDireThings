package com.direwolf20.justdirethings.common.events;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.capabilities.EnergyStorageItemStackNoReceive;
import com.direwolf20.justdirethings.common.capabilities.EnergyStorageItemstack;
import com.direwolf20.justdirethings.common.capabilities.ItemStackItemHandler;
import com.direwolf20.justdirethings.common.containers.handlers.PotionCanisterHandler;
import com.direwolf20.justdirethings.common.items.FluidCanister;
import com.direwolf20.justdirethings.common.items.PocketGenerator;
import com.direwolf20.justdirethings.common.items.PolymorphicWand;
import com.direwolf20.justdirethings.common.items.PolymorphicWandV2;
import com.direwolf20.justdirethings.common.items.PortalGunV2;
import com.direwolf20.justdirethings.common.items.PotionCanister;
import com.direwolf20.justdirethings.common.items.TimeWand;
import com.direwolf20.justdirethings.common.items.interfaces.PoweredItem;
import com.direwolf20.justdirethings.common.items.tools.BlazegoldBow;
import com.direwolf20.justdirethings.common.items.tools.CelestigemBow;
import com.direwolf20.justdirethings.common.items.tools.EclipseAlloyBow;
import com.direwolf20.justdirethings.common.items.tools.FerricoreBow;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class CapabilityEvents {

	private static final ResourceLocation ITEM_HANDLER_CAP_KEY = ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "item_handler");
	private static final ResourceLocation ENERGY_CAP_KEY = ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "energy");
	private static final ResourceLocation FLUID_CAP_KEY = ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "fluid");

	@SubscribeEvent
	public static void onAttachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
		ItemStack stack = event.getObject();
		Item item = stack.getItem();

		IItemHandler itemHandler = getItemHandler(stack);
		if (itemHandler != null)
			attach(event, ITEM_HANDLER_CAP_KEY, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandler);

		if (item instanceof PoweredItem poweredItem) {
			IEnergyStorage storage = item instanceof PocketGenerator
					? new EnergyStorageItemStackNoReceive(stack, poweredItem)
					: new EnergyStorageItemstack(stack, poweredItem);
			attach(event, ENERGY_CAP_KEY, CapabilityEnergy.ENERGY, storage);
		}

		IFluidHandlerItem fluidHandler = getFluidHandler(stack);
		if (fluidHandler != null)
			attach(event, FLUID_CAP_KEY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, fluidHandler);
	}

	@Nullable
	private static IItemHandler getItemHandler(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof PocketGenerator)
			return new ItemStackItemHandler(stack, PocketGenerator.FUEL_INVENTORY, 1);
		if (item instanceof FerricoreBow)
			return new ItemStackItemHandler(stack, PotionCanister.TOOL_CONTENTS, 1);
		if (item instanceof BlazegoldBow)
			return new ItemStackItemHandler(stack, PotionCanister.TOOL_CONTENTS, 2);
		if (item instanceof CelestigemBow)
			return new ItemStackItemHandler(stack, PotionCanister.TOOL_CONTENTS, 3);
		if (item instanceof EclipseAlloyBow)
			return new ItemStackItemHandler(stack, PotionCanister.TOOL_CONTENTS, 4);
		if (item instanceof PotionCanister)
			return new PotionCanisterHandler(stack, PotionCanister.TOOL_CONTENTS, 1);
		return null;
	}

	@Nullable
	private static IFluidHandlerItem getFluidHandler(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof PortalGunV2)
			return singleFluidHandler(stack, () -> PortalGunV2.maxMB, Registration.PORTAL_FLUID_SOURCE);
		if (item instanceof TimeWand timeWand)
			return singleFluidHandler(stack, timeWand::getMaxMB, Registration.TIME_FLUID_SOURCE);
		if (item instanceof PolymorphicWand polymorphicWand)
			return singleFluidHandler(stack, polymorphicWand::getMaxMB, Registration.POLYMORPHIC_FLUID_SOURCE);
		if (item instanceof PolymorphicWandV2 polymorphicWandv2)
			return singleFluidHandler(stack, polymorphicWandv2::getMaxMB, Registration.POLYMORPHIC_FLUID_SOURCE);
		if (item instanceof FluidCanister fluidCanister)
			return new FluidHandlerItemStack(stack, fluidCanister.getMaxMB());
		return null;
	}

	private static FluidHandlerItemStack singleFluidHandler(ItemStack stack, IntSupplier maxMB, Supplier<? extends Fluid> fluid) {
		return new FluidHandlerItemStack(stack, maxMB.getAsInt()) {
			// Capacity is a config option that can change at any time
			@Override
			public int getTankCapacity(int tank) {
				capacity = maxMB.getAsInt();
				return super.getTankCapacity(tank);
			}

			@Override
			public int fill(FluidStack resource, FluidAction doFill) {
				capacity = maxMB.getAsInt();
				return super.fill(resource, doFill);
			}

			@Override
			public boolean isFluidValid(int tank, @Nonnull FluidStack fluidStack) {
				return fluidStack.getFluid().isSame(fluid.get());
			}

			@Override
			public boolean canFillFluidType(FluidStack fluidStack) {
				return fluidStack.getFluid().isSame(fluid.get());
			}
		};
	}

	private static <T> void attach(AttachCapabilitiesEvent<ItemStack> event, ResourceLocation key, Capability<T> capability, T handler) {
		LazyOptional<T> holder = LazyOptional.of(() -> handler);
		event.addCapability(key, new ICapabilityProvider() {
			@Nonnull
			@Override
			public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> cap, @Nullable Direction side) {
				return cap == capability ? holder.cast() : LazyOptional.empty();
			}
		});
		event.addListener(holder::invalidate);
	}
}
