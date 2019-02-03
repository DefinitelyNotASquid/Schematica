package com.github.lunatrius.schematica.client.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
This is a scrolling class for implementing scrollable GUI to minecraft.
"Based on code originally in WDL (by pokechu22), used with permission." - Pokechu22
For use of this code, ask permission from Pokechu22.
 */
public class ScrollGUI extends GuiScreen {
    private final GuiScreen parent;
    private final ClientGUI clientGUI;
    private String title;
    private static final int SET_TEXT_FIELD = 0xE0E0E0, DEFAULT_TEXT_FIELD = 0x808080;
    @Nullable
    private static String hoveredToolTip;

    public static void initGUI(GuiIngameMenu guiIngameMenu) {
        Minecraft.getMinecraft().displayGuiScreen(new ScrollGUI(guiIngameMenu));
    }

    public ScrollGUI(GuiScreen parent) {
        this.parent = parent;
        this.clientGUI = new ClientGUI(this);
    }

    public void initGui() {
        if (ClientGUI.list == null) {
            ClientGUI.list = new GuiGameRuleList();
        }
        this.title = "Schematica options";

        this.buttonList.add(new GuiButton(100, this.width / 2 - 100,
                this.height - 29, "Back"));
        clientGUI.getList().setDimensions(this.width, this.height, 39, this.height - 32);
        ClientGUI.display();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 100) {
            if (clientGUI.isRootElseDropTo()) {
                this.mc.displayGuiScreen(this.parent);
            }
        }
    }

    public void ruleButtonClicked(int id) {
        clientGUI.optionChanged(id, null, 0, 0);
    }

    public void textButtonClicked(int id, String text) {
        clientGUI.optionChanged(id, text, 0, 0);
    }

    public void resetButtonClicked(int id) {
        clientGUI.resetButton(id);
    }

    public void sliderButtonClicked(int id, int i, float f) {
        System.out.println("test " + id + " " + i + " " + f);
        clientGUI.optionChanged(id, null, i, f);
    }

    public void infoButtonClicked(int id) {
    }

    public void buttonClicked(int id) {
        clientGUI.optionChanged(id, null, 0, 0);
    }

    public class GuiGameRuleList extends GuiListExtended {
        @Nullable
        private String lastClickedRule = null;
        private final List<IGuiListEntry> entries = new ArrayList<>();

        public GuiGameRuleList() {
            super(ScrollGUI.this.mc, ScrollGUI.this.width, ScrollGUI.this.height, 39, ScrollGUI.this.height - 32, 24);
        }

        public void clear() {
            entries.clear();
        }

        public void addNewButton(String btnText, int id) {
            this.entries.add(new ButtonEntry(btnText, id));
        }

        public void addNewRuleButton(String str, String btnText, boolean reset, String info, int id) {
            this.entries.add(new ButtonRuleEntry(str, btnText, reset, info, id));
        }

        public void addNewRuleSlider(String str, boolean reset, String info, float min, float max, float def, int id) {
            this.entries.add(new SliderRuleEntryFloat(str, reset, info, min, max, def, id));
        }

        public void addNewRuleSlider(String str, boolean reset, String info, int min, int max, int def, int id) {
            this.entries.add(new SliderRuleEntryInteger(str, reset, info, min, max, def, id));
        }

        public void addNewText(String str, String txtText, boolean reset, String info, int id) {
            this.entries.add(new TextRuleEntry(str, txtText, reset, info, id));
        }

        public void addLabel(String str) {
            this.entries.add(new Label(str));
        }

        @Override
        public int getListWidth() {
            return 180 * 2;
        }

        @Override
        protected int getScrollBarX() {
            return this.width / 2 + getListWidth() / 2 + 4;
        }

        public GuiGameRuleList(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
            super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        }

        @Override
        public IGuiListEntry getListEntry(int index) {
            return entries.get(index);
        }

        @Override
        protected int getSize() {
            return entries.size();
        }

        public void update() {
            // Use a manual for loop to avoid concurrent modification exceptions
            for (int i = 0; i < getSize(); i++) {
                IGuiListEntry entry = getListEntry(i);
                if (entry instanceof KeyboardEntry) {
                    ((KeyboardEntry) entry).onUpdate();
                }
            }
        }

        public void keyDown(char typedChar, int keyCode) {
            // Use a manual for loop to avoid concurrent modification exceptions
            for (int i = 0; i < getSize(); i++) {
                IGuiListEntry entry = getListEntry(i);
                if (entry instanceof KeyboardEntry) {
                    ((KeyboardEntry) entry).keyDown(typedChar, keyCode);
                }
            }
        }

        private class Label extends RuleEntry {
            String text;
            int width;

            public Label(String text) {
                this.text = text;
                width = fontRenderer.getStringWidth(text);
            }

            @Override
            protected void draw(int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, float partialTicks) {
                x = x + listWidth / 2 - width / 2;
                drawString(fontRenderer, text, x, y + 6, 0xFFFFFFFF);
            }

            @Override
            protected boolean mouseDown(int x, int y, int button) {
                return false;
            }

            @Override
            protected void mouseUp(int x, int y, int button) {

            }

            @Override
            public void updatePosition(int slotIndex, int x, int y, float partialTicks) {

            }
        }

        private class ButtonEntry extends RuleEntry {
            private GuiButton button;
            private int buttonID;

            public ButtonEntry(String btnText, int buttonID) {
                super();
                this.buttonID = buttonID;
                button = new GuiButton(0, 0, 0, 150, 20, btnText);
            }

            @Override
            protected void draw(int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, float partialTicks) {
                this.button.x = x + listWidth / 2 - button.getButtonWidth() / 2;
                this.button.y = y;
//                this.button.displayString = getRule(ruleName);
                button.drawButton(mc, mouseX, mouseY, partialTicks);
            }

            @Override
            protected boolean mouseDown(int x, int y, int button) {
                if (this.button.mousePressed(mc, x, y)) {
                    this.button.playPressSound(mc.getSoundHandler());
                    performRuleAction();
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            protected void mouseUp(int x, int y, int button) {
                this.button.mouseReleased(x, y);
            }

            @Override
            public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
            }

            protected void performRuleAction() {
                buttonClicked(buttonID);
            }
        }

        private class ButtonRuleEntry extends RuleEntry {
            private GuiButton button;
            private int buttonID;

            public ButtonRuleEntry(String str, String btnText, boolean reset, String info, int id) {
                super(str, reset, info, id);
                button = new GuiButton(0, 0, 0, 100, 20, btnText);
                this.buttonID = id;
            }

            @Override
            protected void draw(int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, float partialTicks) {
                this.button.x = x + listWidth / 2;
                this.button.y = y;
//                this.button.displayString = getRule(ruleName);
                button.drawButton(mc, mouseX, mouseY, partialTicks);
            }

            @Override
            protected boolean mouseDown(int x, int y, int button) {
                if (this.button.mousePressed(mc, x, y)) {
                    this.button.playPressSound(mc.getSoundHandler());
                    performRuleAction();
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            protected void mouseUp(int x, int y, int button) {
                this.button.mouseReleased(x, y);
            }

//            @Override
//            protected boolean isMouseOverControl(int mouseX, int mouseY) {
//                return button.isMouseOver();
//            }

            @Override
            public void updatePosition(int slotIndex, int x, int y, float partialTicks) {

            }

            protected void performRuleAction() {
                ruleButtonClicked(buttonID);
            }
        }

        private class TextRuleEntry extends RuleEntry implements KeyboardEntry {
            private GuiTextField field;
            private int textfieldID;

            public TextRuleEntry(String ruleName, String text, boolean reset, String info, int id) {
                super(ruleName, reset, info, id);
                field = new GuiTextField(0, fontRenderer, 0, 0, 100, 20);
                field.setText(text);
                textfieldID = id;
            }

            @Override
            public void draw(int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, float partialTicks) {
                if (!this.isFocused()) {
                    field.setFocused(false);
                }
                field.x = x + listWidth / 2;
                field.y = y;
                field.drawTextBox();
            }

            @Override
            protected boolean mouseDown(int x, int y, int button) {
                field.mouseClicked(x, y, button);
                return false;
            }

            @Override
            protected void mouseUp(int x, int y, int button) {
            }

            @Override
            public void onUpdate() {
                this.field.updateCursorCounter();
            }

            @Override
            public void keyDown(char typedChar, int keyCode) {
                if (this.field.textboxKeyTyped(typedChar, keyCode)) {
                } else if (keyCode == Keyboard.KEY_RETURN) {
                    if (field.isFocused()) performTextAction();
                    field.setFocused(false);
                }
            }

            protected void performTextAction() {
                textButtonClicked(textfieldID, field.getText());
            }

            @Override
            public void updatePosition(int slotIndex, int x, int y, float partialTicks) {

            }
        }

        private class SliderRuleEntryInteger extends SliderRuleEntry{
            GuiSlider.FormatHelper format = new Format();

            public SliderRuleEntryInteger(String str, boolean reset, String info, float min, float max, int def, int id) {
                super(str, reset, info, id);
                slider = new GuiSlider(this, id, 0, 0, str, min, max, 0, format);
                slider.setWidth(160);
                slider.setSliderValue(def, false);
                showResetButton(false);
            }

            private class Format implements GuiSlider.FormatHelper{
                @Override
                public String getText(int id, String name, float value) {
                    return Integer.toString((int)value);
                }
            }

            @Override
            protected void mouseUp(int x, int y, int button) {
                if(slider.isMouseDown) sliderButtonClicked(sliderID, (int)barValueFloat, 0);
                slider.mouseReleased(x, y);
            }
        }

        private class SliderRuleEntryFloat extends SliderRuleEntry{
            GuiSlider.FormatHelper format = new Format();

            public SliderRuleEntryFloat(String str, boolean reset, String info, float min, float max, float def, int id) {
                super(str, reset, info, id);
                slider = new GuiSlider(this, id, 0, 0, str, min, max, 0, format);
                slider.setSliderValue(def, false);
                slider.setWidth(160);
                showResetButton(false);
            }

            private class Format implements GuiSlider.FormatHelper{
                @Override
                public String getText(int id, String name, float value) {
                    return Float.toString(value);
                }
            }

            @Override
            protected void mouseUp(int x, int y, int button) {
                if(slider.isMouseDown) sliderButtonClicked(sliderID, 0, barValueFloat);
                slider.mouseReleased(x, y);
            }
        }

        private class SliderRuleEntry extends RuleEntry implements GuiPageButtonList.GuiResponder {
            protected GuiSlider slider;
            protected float barValueFloat;
            protected int sliderID;

            public SliderRuleEntry(String ruleName, boolean reset, String info, int id) {
                super(ruleName, reset, info, id);
                sliderID = id;
            }

            @Override
            protected void draw(int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, float partialTicks) {
                this.slider.x = x + listWidth / 2;
                this.slider.y = y;
//                this.button.displayString = getRule(ruleName);
                slider.drawButton(mc, mouseX, mouseY, partialTicks);
            }

            @Override
            protected boolean mouseDown(int x, int y, int button) {
                return slider.mousePressed(mc, x, y);
            }

            @Override
            protected void mouseUp(int x, int y, int button) {
                slider.mouseReleased(x, y);
            }

//            @Override
//            protected boolean isMouseOverControl(int mouseX, int mouseY) {
//                return button.isMouseOver();
//            }

            @Override
            public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
            }

            protected void performRuleAction() {
            }

            @Override
            public void setEntryValue(int id, boolean value) {
            }

            @Override
            public void setEntryValue(int id, float value) {
                barValueFloat = value;
            }

            @Override
            public void setEntryValue(int id, String value) {
            }
        }

        private abstract class RuleEntry implements IGuiListEntry {
            @Nonnull
            protected String ruleName;
            private boolean justButton;
            private GuiButton resetButton;
            private GuiButton infoButton;
            private String ruleInfo;
            private boolean resetButtonDraw;
            private int ruleID;

            public RuleEntry() {
                justButton = true;
            }

            public void showResetButton(boolean show) {
                resetButtonDraw = show;
            }

            public RuleEntry(@Nonnull String ruleName, boolean reset, String info, int id) {
                this.ruleName = ruleName;
                this.resetButton = new GuiButton(0, 0, 0, 50, 20, "reset");
                this.infoButton = new GuiButton(0, 0, 0, 14, 15, "i");
                resetButton.enabled = reset;
                ruleInfo = info;
                resetButtonDraw = true;
                ruleID = id;
            }

            @Override
            public final void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
                drawString(fontRenderer, this.ruleName, x, y + 6, 0xFFFFFFFF);
                if (!justButton) {
                    if (resetButtonDraw) {
                        this.resetButton.x = x + listWidth / 2 + 110;
                        this.resetButton.y = y;
//                this.resetButton.enabled = isRuleSet(this.ruleName);
                        resetButton.drawButton(mc, mouseX, mouseY, partialTicks);
                    }
                    this.infoButton.x = x + listWidth / 2 - 17;
                    this.infoButton.y = y + 2;
                    this.infoButton.enabled = (ruleInfo.length() == 0);
                    infoButton.drawButton(mc, mouseX, mouseY, partialTicks);
                }
                this.draw(x, y, listWidth, slotHeight, mouseX, mouseY, partialTicks);

//                if (this.isMouseOverControl(mouseX, mouseY)) {
//                    String key = ruleName;
//                    hoveredToolTip = ruleName;
////                    if (I18n.hasKey(key)) { // may return false for mods
////                        hoveredToolTip = I18n.format(key);
////                    }
//                }

                if (this.isMouseOverInfo(mouseX, mouseY)) {
                    if (ruleInfo.length() > 0) hoveredToolTip = ruleInfo;
                }
            }

            @Override
            public final boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
                lastClickedRule = this.ruleName;
                if (!justButton) {
                    if (resetButton.mousePressed(mc, mouseX, mouseY)) {
                        resetButton.playPressSound(mc.getSoundHandler());
                        this.performResetAction();
                        return true;
                    }
                    if (infoButton.mousePressed(mc, mouseX, mouseY)) {
                        infoButton.playPressSound(mc.getSoundHandler());
                        this.performInfoAction();
                        return true;
                    }
                }
                return mouseDown(mouseX, mouseY, mouseEvent);
            }

            @Override
            public final void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
                if (!justButton) resetButton.mouseReleased(mouseX, mouseY);
                mouseUp(mouseX, mouseY, mouseEvent);
            }

            protected abstract void draw(int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, float partialTicks);

            protected abstract boolean mouseDown(int x, int y, int button);

            protected abstract void mouseUp(int x, int y, int button);

//            protected abstract boolean isMouseOverControl(int mouseX, int mouseY);

            protected boolean isMouseOverInfo(int mouseX, int mouseY) {
                if (justButton) return false;
                return infoButton.isMouseOver();
            }

            protected boolean isFocused() {
                return lastClickedRule == this.ruleName;  // Ref equals
            }

            /**
             * Called when the reset button is clicked.
             */
            protected void performResetAction() {
                resetButtonClicked(ruleID);
            }

            protected void performInfoAction() {
                infoButtonClicked(ruleID);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        hoveredToolTip = null;
        clientGUI.getList().drawScreen(mouseX, mouseY, partialTicks);

        super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawCenteredString(fontRenderer, title, width / 2, 4, 0xFFFFFF);

        if (hoveredToolTip != null) {
            drawGuiInfoBox(hoveredToolTip, 360, 168, width, height, 48);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
            throws IOException {
        clientGUI.getList().mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        clientGUI.getList().mouseReleased(mouseX, mouseY, state);
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        clientGUI.getList().keyDown(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        clientGUI.getList().handleMouseInput();
    }

    private static interface KeyboardEntry extends IGuiListEntry {
        public abstract void keyDown(char typedChar, int keyCode);

        public abstract void onUpdate();
    }

    public static void drawGuiInfoBox(String text, int infoBoxWidth,
                                      int infoBoxHeight, int guiWidth, int guiHeight, int bottomPadding) {
        if (text == null) {
            return;
        }

        int infoX = guiWidth / 2 - infoBoxWidth / 2;
        int infoY = 40;
        int y = infoY + 5;

        Gui.drawRect(infoX, infoY, infoX + infoBoxWidth, infoY
                + infoBoxHeight, 0xCF000000);

        List<String> lines = wordWrap(text, infoBoxWidth - 10);

        for (String s : lines) {
            Minecraft.getMinecraft().fontRenderer.drawString(s, infoX + 5, y, 0xFFFFFF);
            y += Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
        }
    }

    public static List<String> wordWrap(String s, int width) {
        s = s.replace("\r", ""); // If we got a \r\n in the text somehow, remove it.

        List<String> lines = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(s, width);

        return lines;
    }

    public static boolean isMouseOverTextBox(int mouseX, int mouseY,
                                             GuiTextField textBox) {
        int scaledX = mouseX - textBox.x;
        int scaledY = mouseY - textBox.y;

        // Standard text box height -- there is no actual getter for the real
        // one.
        final int height = 20;

        return scaledX >= 0 && scaledX < textBox.getWidth() && scaledY >= 0
                && scaledY < height;
    }

    class GuiNumericTextField extends GuiTextField {
        /**
         * Last text that was successfully entered.
         */
        private String lastSafeText = "0";
        private boolean useInteger = true;

        public GuiNumericTextField(int id, FontRenderer fontRenderer,
                                   int x, int y, int width, int height, boolean useInt) {
            super(id, fontRenderer, x, y, width,
                    height);
            setText("0");
            useInteger = useInt;
        }

        @Override
        public void drawTextBox() {
            // Save last safe text.

            try {
                if (useInteger) {
                    Integer.parseInt(getText());
                } else {
                    Float.parseFloat(getText());
                }
                lastSafeText = getText();
            } catch (NumberFormatException e) {
                setText(lastSafeText);
            }

            super.drawTextBox();
        }

        /**
         * Sets the value.
         *
         * @param value
         * @return
         */
        public void setValue(int value) {
            String text = String.valueOf(value);
            lastSafeText = text;
            setText(text);
        }

        @Override
        public String getText() {
            String text = super.getText();

            try {
                if (text.contains("d") || text.contains("f")) return lastSafeText;
                if (useInteger) {
                    return String.valueOf(Integer.parseInt(text));
                } else {
                    return String.valueOf(Float.parseFloat(text));
                }
            } catch (NumberFormatException e) {
                setText(lastSafeText);
                return lastSafeText;
            }
        }

        @Override
        public void setText(String text) {
            String value;

            try {
                if (useInteger) {
                    value = String.valueOf(Integer.parseInt(text));
                } else {
                    value = String.valueOf(Float.parseFloat(text));
                }
            } catch (NumberFormatException e) {
                value = lastSafeText;
            }

            super.setText(value);
            lastSafeText = value;
        }
    }
}
