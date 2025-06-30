package com.yarn_util;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.nbt.NbtCompound;

public class SimpleProfile {
	// Since yarn mappings doesn't have ResolvableProfile, this class is an alternate where it can be compatible with bedrock + yarn

    private final UUID uuid;
    private final String name;
    
    // CONSTRUCTOR
    public SimpleProfile(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
    
    
    public UUID getUuid() { 
    	return uuid; 
    }
    
    
    public String getName() { 
    	return name; 
    }
    
    public GameProfile toGameProfile() {
    	return new GameProfile(uuid, name);
    }
    

    public NbtCompound toNbt() {
        NbtCompound tag = new NbtCompound();
        tag.putUuid("Id", uuid);
        tag.putString("Name", name);
        return tag;
    }

    public static SimpleProfile fromNbt(NbtCompound tag) {
        UUID uuid = tag.contains("Id") ? tag.getUuid("Id") : UUID.randomUUID();
        String name = tag.contains("Name") ? tag.getString("Name") : "Unknown";
        return new SimpleProfile(uuid, name);
    }
}
