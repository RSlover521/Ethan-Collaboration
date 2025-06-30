package com.rslover521.custom_grave_data;

import com.b1n_ry.yigd.components.InventoryComponent;
import com.b1n_ry.yigd.components.ExpComponent;
import com.b1n_ry.yigd.data.GraveStatus;
import com.b1n_ry.yigd.data.TimePoint;
import com.mojang.authlib.GameProfile;
import com.b1n_ry.yigd.components.GraveComponent;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.text.Text; // Yarn replacement for Component
import net.minecraft.component.type.ProfileComponent;


import com.yarn_util.SimpleProfile;

import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.Optional;
import java.util.UUID;

/**
 * A custom implementation that uses UUID and name to create a ResolvableProfile.
 * This wraps existing functionality while staying Yarn-compatible.
 */
public class CustomGraveComponent extends GraveComponent {
    private final SimpleProfile ownerProfile;

	public CustomGraveComponent(
            SimpleProfile profile,
            InventoryComponent inv,
            ExpComponent exp,
            ServerWorld world,
            BlockPos pos,
            Text deathMessage,
            UUID killerId
    ) {
        // Yarn-compatible: create a ProfileComponent (used in ResolvableProfile)
        super(
                null,
                inv,
                exp,
                world,
                pos,
                deathMessage,
                UUID.randomUUID(),
                GraveStatus.UNCLAIMED,
                true,
                new TimePoint(world),
                killerId
        );
        this.ownerProfile = profile;
    }
	public SimpleProfile getSimpleProfile() {
		return this.ownerProfile;
	}

	public UUID getOwnerId() {
	    return this.ownerProfile.getUuid();
	}

	public String getOwnerName() {
	    return this.ownerProfile.getName();
	}
}