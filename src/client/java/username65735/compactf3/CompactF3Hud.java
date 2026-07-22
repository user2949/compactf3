package username65735.compactf3;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class CompactF3Hud {
	private static final int PANEL_PADDING_LEFT = 3;
	private static final int PANEL_PADDING_RIGHT = 28;
	private static final int PANEL_PADDING_TOP = 2;
	private static final int PANEL_PADDING_BOTTOM = 3;
	private static final int LINE_SPACING = 1;
	private static final String SUB_LINE_PREFIX = "  - ";
	private static final String NOT_IN_WORLD_TEXT = "Waiting for world...";

	private static boolean enabled = true;
	private static double previousX;
	private static double previousY;
	private static double previousZ;
	private static boolean hasPreviousPosition;
	private static double horizontalSpeedMetersPerSecond;
	private static double verticalSpeedMetersPerSecond;
	private static double totalSpeedMetersPerSecond;
	private static Anchor anchor = Anchor.TOP_LEFT;
	private static int edgePadding = 3;
	private static int opacityPercent = 30;
	private static int refreshRateHz = 60;
	private static List<String> cachedLines = List.of("FPS: 0 (0.0 ms)", NOT_IN_WORLD_TEXT);
	private static long lastRefreshNanos;

	private CompactF3Hud() {
	}

	public static void toggle() {
		enabled = !enabled;
	}

	public static Anchor getAnchor() {
		return anchor;
	}

	public static void setAnchor(Anchor anchor) {
		CompactF3Hud.anchor = anchor;
		requestRefresh();
	}

	public static int getEdgePadding() {
		return edgePadding;
	}

	public static void setEdgePadding(int edgePadding) {
		CompactF3Hud.edgePadding = Math.max(0, edgePadding);
		requestRefresh();
	}

	public static int getOpacityPercent() {
		return opacityPercent;
	}

	public static void setOpacityPercent(int opacityPercent) {
		CompactF3Hud.opacityPercent = Math.max(0, Math.min(100, opacityPercent));
		requestRefresh();
	}

	public static int getRefreshRateHz() {
		return refreshRateHz;
	}

	public static void setRefreshRateHz(int refreshRateHz) {
		CompactF3Hud.refreshRateHz = Math.max(1, Math.min(360, refreshRateHz));
		requestRefresh();
	}

	public static void tick(Minecraft client) {
		LocalPlayer player = client.player;

		if (player == null || client.level == null) {
			horizontalSpeedMetersPerSecond = 0.0D;
			verticalSpeedMetersPerSecond = 0.0D;
			totalSpeedMetersPerSecond = 0.0D;
			hasPreviousPosition = false;
			return;
		}

		double currentX = player.getX();
		double currentY = player.getY();
		double currentZ = player.getZ();

		if (hasPreviousPosition) {
			double deltaX = currentX - previousX;
			double deltaY = currentY - previousY;
			double deltaZ = currentZ - previousZ;
			horizontalSpeedMetersPerSecond = Math.hypot(deltaX, deltaZ) * 20.0D;
			verticalSpeedMetersPerSecond = deltaY * 20.0D;
			totalSpeedMetersPerSecond = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ)) * 20.0D;
		}

		previousX = currentX;
		previousY = currentY;
		previousZ = currentZ;
		hasPreviousPosition = true;
	}

	public static void render(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
		Minecraft client = Minecraft.getInstance();

		if (!enabled || MinecraftCompatibility.isHudHidden(client)) {
			return;
		}

		refreshIfNeeded(client);

		if (cachedLines.isEmpty()) {
			return;
		}

		int lineHeight = client.font.lineHeight + LINE_SPACING;
		int maxWidth = maxLineWidth(client, cachedLines);
		int boxWidth = maxWidth + PANEL_PADDING_LEFT + PANEL_PADDING_RIGHT;
		int boxHeight = (cachedLines.size() * lineHeight) + PANEL_PADDING_TOP + PANEL_PADDING_BOTTOM;
		int panelX = anchor.resolveX(graphics.guiWidth(), boxWidth, edgePadding);
		int panelY = anchor.resolveY(graphics.guiHeight(), boxHeight, edgePadding);
		drawPanel(graphics, cachedLines, panelX, panelY, lineHeight);
	}

	public static void requestRefresh() {
		lastRefreshNanos = 0L;
	}

	private static void refreshIfNeeded(Minecraft client) {
		long now = System.nanoTime();
		long interval = Math.max(1L, 1_000_000_000L / Math.max(1, refreshRateHz));

		if ((now - lastRefreshNanos) < interval) {
			return;
		}

		cachedLines = buildLines(client);
		lastRefreshNanos = now;
	}

	private static int backgroundColor() {
		int alpha = (int) Math.round((opacityPercent / 100.0D) * 255.0D);
		return alpha << 24;
	}

	private static void drawPanel(GuiGraphicsExtractor graphics, List<String> lines, int panelX, int panelY, int lineHeight) {
		Minecraft client = Minecraft.getInstance();
		int boxWidth = maxLineWidth(client, lines) + PANEL_PADDING_LEFT + PANEL_PADDING_RIGHT;
		int boxHeight = (lines.size() * lineHeight) + PANEL_PADDING_TOP + PANEL_PADDING_BOTTOM;
		graphics.fill(panelX, panelY, panelX + boxWidth, panelY + boxHeight, backgroundColor());

		int textX = panelX + PANEL_PADDING_LEFT;
		int textY = panelY + PANEL_PADDING_TOP;

		for (String line : lines) {
			graphics.text(client.font, line, textX, textY, 0xFFFFFFFF, false);
			textY += lineHeight;
		}
	}

	private static int maxLineWidth(Minecraft client, List<String> lines) {
		int maxWidth = 0;

		for (String line : lines) {
			maxWidth = Math.max(maxWidth, client.font.width(line));
		}

		return maxWidth;
	}

	private static List<String> buildLines(Minecraft client) {
		List<String> lines = new ArrayList<>();
		lines.add(String.format(Locale.ROOT, "FPS: %d (%.1f ms)", client.getFps(), frameTimeMillis(client)));

		LocalPlayer player = client.player;
		ClientLevel world = client.level;

		if (player == null || world == null) {
			lines.add(NOT_IN_WORLD_TEXT);
			return lines;
		}

		lines.add(String.format(Locale.ROOT, "XYZ: %.1f, %.1f, %.1f", player.getX(), player.getY(), player.getZ()));
		lines.add("Speed:");
		lines.add(speedLine("Horizontal", horizontalSpeedMetersPerSecond));
		lines.add(speedLine("Vertical", verticalSpeedMetersPerSecond));
		lines.add(speedLine("Total Speed", totalSpeedMetersPerSecond));
		lines.add(String.format(Locale.ROOT, "Facing: %s (%.1f\u00B0)", formatDirection(player.getDirection()), normalizeYaw(player.getYRot())));
		lines.add("Time: " + formatTimeOfDay(world.getOverworldClockTime()));
		lines.add("Biome: " + formatBiome(world, player));
		return lines;
	}

	private static double frameTimeMillis(Minecraft client) {
		int fps = client.getFps();
		return fps > 0 ? 1000.0D / fps : 0.0D;
	}

	private static String speedLine(String label, double speedMetersPerSecond) {
		return String.format(
			Locale.ROOT,
			"%s%s: %.1f km/h (%.1f m/s)",
			SUB_LINE_PREFIX,
			label,
			speedMetersPerSecond * 3.6D,
			speedMetersPerSecond
		);
	}

	private static String formatTimeOfDay(long timeOfDay) {
		long ticks = Math.floorMod(timeOfDay, 24000L);
		int totalMinutes = (int) (((ticks + 6000L) % 24000L) * 60L / 1000L);
		int hours = totalMinutes / 60;
		int minutes = totalMinutes % 60;
		return String.format(Locale.ROOT, "%d:%02d", hours, minutes);
	}

	private static double normalizeYaw(float yaw) {
		double degrees = yaw % 360.0D;

		if (degrees < 0.0D) {
			degrees += 360.0D;
		}

		return degrees;
	}

	private static String formatDirection(Direction direction) {
		String name = direction.getName();
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	private static String formatBiome(ClientLevel world, LocalPlayer player) {
		Holder<Biome> biomeEntry = world.getBiome(player.blockPosition());
		return biomeEntry.unwrapKey()
			.map(ResourceKey::identifier)
			.map(Identifier::toString)
			.orElseGet(biomeEntry::getRegisteredName);
	}

	public enum Anchor {
		TOP_LEFT("Top Left") {
			@Override
			int resolveX(int screenWidth, int boxWidth, int padding) {
				return padding;
			}

			@Override
			int resolveY(int screenHeight, int boxHeight, int padding) {
				return padding;
			}
		},
		TOP_RIGHT("Top Right") {
			@Override
			int resolveX(int screenWidth, int boxWidth, int padding) {
				return screenWidth - boxWidth - padding;
			}

			@Override
			int resolveY(int screenHeight, int boxHeight, int padding) {
				return padding;
			}
		},
		BOTTOM_LEFT("Bottom Left") {
			@Override
			int resolveX(int screenWidth, int boxWidth, int padding) {
				return padding;
			}

			@Override
			int resolveY(int screenHeight, int boxHeight, int padding) {
				return screenHeight - boxHeight - padding;
			}
		},
		BOTTOM_RIGHT("Bottom Right") {
			@Override
			int resolveX(int screenWidth, int boxWidth, int padding) {
				return screenWidth - boxWidth - padding;
			}

			@Override
			int resolveY(int screenHeight, int boxHeight, int padding) {
				return screenHeight - boxHeight - padding;
			}
		};

		private final String label;

		Anchor(String label) {
			this.label = label;
		}

		public String label() {
			return label;
		}

		abstract int resolveX(int screenWidth, int boxWidth, int padding);

		abstract int resolveY(int screenHeight, int boxHeight, int padding);
	}
}
