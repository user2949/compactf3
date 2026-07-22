package username65735.compactf3;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CompactF3PositionScreen extends Screen {
	private final Screen parent;

	public CompactF3PositionScreen(Screen parent) {
		super(Component.literal("CompactF3 Position"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int buttonWidth = 140;
		int buttonHeight = 20;
		int spacing = 6;
		int startX = (this.width - ((buttonWidth * 2) + spacing)) / 2;
		int startY = this.height / 2 - 32;
		CompactF3Hud.Anchor current = CompactF3Hud.getAnchor();

		this.addRenderableWidget(anchorButton(CompactF3Hud.Anchor.TOP_LEFT, startX, startY, buttonWidth, buttonHeight, current));
		this.addRenderableWidget(anchorButton(CompactF3Hud.Anchor.TOP_RIGHT, startX + buttonWidth + spacing, startY, buttonWidth, buttonHeight, current));
		this.addRenderableWidget(anchorButton(CompactF3Hud.Anchor.BOTTOM_LEFT, startX, startY + 24, buttonWidth, buttonHeight, current));
		this.addRenderableWidget(anchorButton(CompactF3Hud.Anchor.BOTTOM_RIGHT, startX + buttonWidth + spacing, startY + 24, buttonWidth, buttonHeight, current));
		this.addRenderableWidget(Button.builder(Component.literal("Back"), button -> this.onClose())
			.bounds((this.width / 2) - 50, startY + 56, 100, buttonHeight)
			.build());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		this.extractBackground(graphics, mouseX, mouseY, delta);
		super.extractRenderState(graphics, mouseX, mouseY, delta);
		graphics.centeredText(this.font, this.title, this.width / 2, this.height / 2 - 58, 0xFFFFFF);
		graphics.centeredText(this.font, Component.literal("Current: " + CompactF3Hud.getAnchor().label()), this.width / 2, this.height / 2 - 42, 0xCFCFCF);
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

	private Button anchorButton(CompactF3Hud.Anchor anchor, int x, int y, int width, int height, CompactF3Hud.Anchor current) {
		String label = anchor == current ? anchor.label() + " *" : anchor.label();
		return Button.builder(Component.literal(label), button -> {
			CompactF3Hud.setAnchor(anchor);
			if (this.minecraft != null) {
				MinecraftCompatibility.setScreen(this.minecraft, new CompactF3PositionScreen(this.parent));
			}
		}).bounds(x, y, width, height).build();
	}
}
