package username65735.compactf3;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CompactF3PositionScreen extends Screen {
	private final Screen parent;

	public CompactF3PositionScreen(Screen parent) {
		super(Text.literal("CompactF3 Position"));
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

		this.addDrawableChild(anchorButton(CompactF3Hud.Anchor.TOP_LEFT, startX, startY, buttonWidth, buttonHeight, current));
		this.addDrawableChild(anchorButton(CompactF3Hud.Anchor.TOP_RIGHT, startX + buttonWidth + spacing, startY, buttonWidth, buttonHeight, current));
		this.addDrawableChild(anchorButton(CompactF3Hud.Anchor.BOTTOM_LEFT, startX, startY + 24, buttonWidth, buttonHeight, current));
		this.addDrawableChild(anchorButton(CompactF3Hud.Anchor.BOTTOM_RIGHT, startX + buttonWidth + spacing, startY + 24, buttonWidth, buttonHeight, current));
		this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> this.close())
			.dimensions((this.width / 2) - 50, startY + 56, 100, buttonHeight)
			.build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context, mouseX, mouseY, delta);
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 58, 0xFFFFFF);
		context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Current: " + CompactF3Hud.getAnchor().label()), this.width / 2, this.height / 2 - 42, 0xCFCFCF);
	}

	@Override
	public void close() {
		if (this.client != null) {
			this.client.setScreen(this.parent);
		}
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	private ButtonWidget anchorButton(CompactF3Hud.Anchor anchor, int x, int y, int width, int height, CompactF3Hud.Anchor current) {
		String label = anchor == current ? anchor.label() + " *" : anchor.label();
		return ButtonWidget.builder(Text.literal(label), button -> {
			CompactF3Hud.setAnchor(anchor);
			if (this.client != null) {
				this.client.setScreen(new CompactF3PositionScreen(this.parent));
			}
		}).dimensions(x, y, width, height).build();
	}
}
