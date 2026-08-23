package de.lumax.signedit;

import de.lumax.signedit.network.SignFormattingPayload;
import de.lumax.signedit.server.SignFormattingServer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.resources.Identifier;

public class SignEdit implements ModInitializer {
	public static final String MOD_ID = "signedit";

	@Override
	public void onInitialize() {
		System.out.println("SignEdit COMMON initialized");

		PayloadTypeRegistry.serverboundPlay().register(
				SignFormattingPayload.TYPE,
				SignFormattingPayload.CODEC
		);

		SignFormattingServer.register();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
