package username65735.compactf3;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CompactF3GuiSettingsScreen extends Screen {
	private final Screen parent;

	public CompactF3GuiSettingsScreen(Screen parent) {
		super(Component.literal("CompactF3 GUI Settings"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int sliderWidth = 220;
		int sliderHeight = 20;
		int x = (this.width - sliderWidth) / 2;
		int startY = this.height / 2 - 36;

		this.addRenderableWidget(new CompactF3SliderWidget(
			x,
			startY,
			sliderWidth,
			sliderHeight,
			"Rate of Change (in Hz)",
			CompactF3Hud.getRefreshRateHz(),
			1,
			360
		) {
			@Override
			protected void applySetting(int settingValue) {
				CompactF3Hud.setRefreshRateHz(settingValue);
			}

			@Override
			protected String valueText(int settingValue) {
				return settingValue + " Hz";
			}
		});

		this.addRenderableWidget(new CompactF3SliderWidget(
			x,
			startY + 24,
			sliderWidth,
			sliderHeight,
			"GUI Opacity",
			CompactF3Hud.getOpacityPercent(),
			0,
			100
		) {
			@Override
			protected void applySetting(int settingValue) {
				CompactF3Hud.setOpacityPercent(settingValue);
			}

			@Override
			protected String valueText(int settingValue) {
				return settingValue + "%";
			}
		});

		this.addRenderableWidget(new CompactF3SliderWidget(
			x,
			startY + 48,
			sliderWidth,
			sliderHeight,
			"GUI Padding",
			CompactF3Hud.getEdgePadding(),
			0,
			40
		) {
			@Override
			protected void applySetting(int settingValue) {
				CompactF3Hud.setEdgePadding(settingValue);
			}

			@Override
			protected String valueText(int settingValue) {
				return settingValue + " px";
			}
		});

		this.addRenderableWidget(Button.builder(Component.literal("Back"), button -> this.onClose())
			.bounds((this.width / 2) - 50, startY + 78, 100, 20)
			.build());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		this.extractBackground(graphics, mouseX, mouseY, delta);
		super.extractRenderState(graphics, mouseX, mouseY, delta);
		graphics.centeredText(this.font, this.title, this.width / 2, this.height / 2 - 58, 0xFFFFFF);
	}

	@Override
	public void onClose() {
		if (this.minecraft != null) {
			MinecraftCompatibility.setScreen(this.minecraft, this.parent);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private abstract static class CompactF3SliderWidget extends AbstractSliderButton {
		private final String label;
		private final int min;
		private final int max;

		protected CompactF3SliderWidget(int x, int y, int width, int height, String label, int currentValue, int min, int max) {
			super(x, y, width, height, Component.literal(""), normalize(currentValue, min, max));
			this.label = label;
			this.min = min;
			this.max = max;
			this.updateMessage();
		}

		@Override
		protected void updateMessage() {
			int settingValue = this.settingValue();
			this.setMessage(Component.literal(this.label + ": " + valueText(settingValue)));
		}

		@Override
		protected void applyValue() {
			int settingValue = this.settingValue();
			applySetting(settingValue);
			CompactF3Hud.requestRefresh();
		}

		private int settingValue() {
			return this.min + (int) Math.round(this.value * (this.max - this.min));
		}

		private static double normalize(int value, int min, int max) {
			if (max <= min) {
				return 0.0D;
			}

			return (value - min) / (double) (max - min);
		}

		protected abstract void applySetting(int settingValue);

		protected abstract String valueText(int settingValue);
	}
}
