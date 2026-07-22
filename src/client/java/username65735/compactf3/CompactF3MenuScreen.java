package username65735.compactf3;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CompactF3MenuScreen extends Screen {
	public CompactF3MenuScreen() {
		super(Text.literal("CompactF3 Menu"));
	}

	@Override
	protected void init() {
		int buttonWidth = 150;
		int buttonHeight = 20;
		int spacing = 6;
		int startX = (this.width - ((buttonWidth * 2) + spacing)) / 2;
		int y = this.height / 2 - 10;

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Position Controller"), button ->
			this.client.setScreen(new CompactF3PositionScreen(this))
		).dimensions(startX, y, buttonWidth, buttonHeight).build());

		this.addDrawableChild(ButtonWidget.builder(Text.literal("GUI Settings"), button ->
			this.client.setScreen(new CompactF3GuiSettingsScreen(this))
		).dimensions(startX + buttonWidth + spacing, y, buttonWidth, buttonHeight).build());

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> this.close())
			.dimensions((this.width / 2) - 50, y + 32, 100, buttonHeight)
			.build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context, mouseX, mouseY, delta);
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 36, 0xFFFFFF);
	}

	@Override
	public void close() {
		if (this.client != null) {
			this.client.setScreen(null);
		}
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
