package username65735.compactf3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Bridges the client GUI changes between Minecraft 26.1.1 and 26.2.
 */
final class MinecraftCompatibility {
	private static Method screenSetter;
	private static Object screenSetterOwner;
	private static boolean screenSetterResolved;
	private static Method hudHiddenGetter;
	private static Object hudHiddenGetterOwner;
	private static Field hudHiddenField;
	private static Object hudHiddenFieldOwner;
	private static boolean hudHiddenGetterResolved;

	private MinecraftCompatibility() {
	}

	static void setScreen(Minecraft minecraft, Screen screen) {
		resolveScreenSetter(minecraft);
		invoke(screenSetter, screenSetterOwner, screen);
	}

	static boolean isHudHidden(Minecraft minecraft) {
		resolveHudHiddenGetter(minecraft);

		if (hudHiddenGetter != null) {
			return (boolean) invoke(hudHiddenGetter, hudHiddenGetterOwner);
		}

		try {
			return hudHiddenField.getBoolean(hudHiddenFieldOwner);
		} catch (IllegalAccessException exception) {
			throw new IllegalStateException("Unable to access the Minecraft HUD visibility field.", exception);
		}
	}

	private static synchronized void resolveScreenSetter(Minecraft minecraft) {
		if (screenSetterResolved) {
			return;
		}

		try {
			screenSetter = Minecraft.class.getMethod("setScreen", Screen.class);
			screenSetterOwner = minecraft;
		} catch (NoSuchMethodException ignored) {
			Object gui = minecraft.gui;
			try {
				screenSetter = gui.getClass().getMethod("setScreen", Screen.class);
				screenSetterOwner = gui;
			} catch (ReflectiveOperationException exception) {
				throw new IllegalStateException("Unable to find the Minecraft screen setter.", exception);
			}
		}

		screenSetterResolved = true;
	}

	private static synchronized void resolveHudHiddenGetter(Minecraft minecraft) {
		if (hudHiddenGetterResolved) {
			return;
		}

		Object gui = minecraft.gui;
		try {
			hudHiddenGetter = gui.getClass().getMethod("isHidden");
			hudHiddenGetterOwner = gui;
		} catch (NoSuchMethodException ignored) {
			try {
				Field hudField = gui.getClass().getField("hud");
				hudHiddenGetterOwner = hudField.get(gui);
				hudHiddenGetter = hudHiddenGetterOwner.getClass().getMethod("isHidden");
			} catch (NoSuchFieldException noHudField) {
				Object options = minecraft.options;
				try {
					hudHiddenField = options.getClass().getField("hideGui");
					hudHiddenFieldOwner = options;
				} catch (ReflectiveOperationException exception) {
					throw new IllegalStateException("Unable to find the Minecraft HUD visibility setting.", exception);
				}
			} catch (ReflectiveOperationException exception) {
				throw new IllegalStateException("Unable to find the Minecraft HUD visibility getter.", exception);
			}
		}

		hudHiddenGetterResolved = true;
	}

	private static Object invoke(Method method, Object owner, Object... arguments) {
		try {
			return method.invoke(owner, arguments);
		} catch (IllegalAccessException exception) {
			throw new IllegalStateException("Unable to access a Minecraft compatibility method.", exception);
		} catch (InvocationTargetException exception) {
			throw new IllegalStateException("A Minecraft compatibility method failed.", exception.getCause());
		}
	}
}
