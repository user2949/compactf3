package username65735.compactf3;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CompactF3MenuScreen extends Screen {
	public CompactF3MenuScreen() {
		super(Component.literal("CompactF3 Menu"));
	}

	@Override
	protected void init() {
		int buttonWidth = 150;
		int buttonHeight = 20;
		int spacing = 6;
		int startX = (this.width - ((buttonWidth * 2) + spacing)) / 2;
		int y = this.height / 2 - 10;

		this.addRenderableWidget(Button.builder(Component.literal("Position Controller"), button ->
			MinecraftCompatibility.setScreen(this.minecraft, new CompactF3PositionScreen(this))
		).bounds(startX, y, buttonWidth, buttonHeight).build());

		this.addRenderableWidget(Button.builder(Component.literal("GUI Settings"), button ->
			MinecraftCompatibility.setScreen(this.minecraft, new CompactF3GuiSettingsScreen(this))
		).bounds(startX + buttonWidth + spacing, y, buttonWidth, buttonHeight).build());

		this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> this.onClose())
			.bounds((this.width / 2) - 50, y + 32, 100, buttonHeight)
			.build());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		this.extractBackground(graphics, mouseX, mouseY, delta);
		super.extractRenderState(graphics, mouseX, mouseY, delta);
		graphics.centeredText(this.font, this.title, this.width / 2, this.height / 2 - 36, 0xFFFFFF);
	}

	@Override
	public void onClose() {
		if (this.minecraft != null) {
			MinecraftCompatibility.setScreen(this.minecraft, null);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
