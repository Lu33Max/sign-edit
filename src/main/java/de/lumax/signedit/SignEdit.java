package de.lumax.signedit;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public class SignEdit implements ModInitializer {
	public static final String MOD_ID = "signedit";

	@Override
	public void onInitialize() {
		System.out.println("SignEdit COMMON initialized");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
