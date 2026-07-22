package username65735.compactf3;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.InputConstants;

import org.lwjgl.glfw.GLFW;

public class CompactF3Client implements ClientModInitializer {
    private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(CompactF3.MOD_ID, "compactf3")
    );

    private static final KeyMapping TOGGLE_OVERLAY_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.compactf3-fabric.toggle_overlay",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F8,
            KEY_CATEGORY
    ));

    private static final KeyMapping OPEN_MENU_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.compactf3-fabric.open_menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_BRACKET,
            KEY_CATEGORY
    ));

    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(CompactF3.MOD_ID, "compact_f3"),
                CompactF3Hud::render
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLE_OVERLAY_KEY.consumeClick()) {
                CompactF3Hud.toggle();
            }

            while (OPEN_MENU_KEY.consumeClick()) {
                openMenu(client);
            }

            CompactF3Hud.tick(client);
        });
    }

    private static void openMenu(Minecraft client) {
        MinecraftCompatibility.setScreen(client, new CompactF3MenuScreen());
    }
}
