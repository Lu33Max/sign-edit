package de.lumax.client;

import net.fabricmc.api.ClientModInitializer;

public class SignEditClient implements ClientModInitializer {
	public static final String MOD_ID = "signedit";

	@Override
	public void onInitializeClient() {
		System.out.println("SignEdit client initialized!");
	}
}