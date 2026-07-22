package username65735.compactf3;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CompactF3Client implements ClientModInitializer {
	private static final String KEY_CATEGORY = "key.categories.compactf3-fabric";
	private static final KeyBinding TOGGLE_OVERLAY_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
		"key.compactf3-fabric.toggle_overlay",
		InputUtil.Type.KEYSYM,
		GLFW.GLFW_KEY_F8,
		KEY_CATEGORY
	));
	private static final KeyBinding OPEN_MENU_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
		"key.compactf3-fabric.open_menu",
		InputUtil.Type.KEYSYM,
		GLFW.GLFW_KEY_RIGHT_BRACKET,
		KEY_CATEGORY
	));

	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register(CompactF3Hud::render);
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (TOGGLE_OVERLAY_KEY.wasPressed()) {
				CompactF3Hud.toggle();
			}

			while (OPEN_MENU_KEY.wasPressed()) {
				openMenu(client);
			}

			CompactF3Hud.tick(client);
		});
	}

	private static void openMenu(MinecraftClient client) {
		client.setScreen(new CompactF3MenuScreen());
	}
}
