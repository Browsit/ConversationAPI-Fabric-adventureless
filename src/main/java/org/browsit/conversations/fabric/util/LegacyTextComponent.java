package org.browsit.conversations.fabric.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

/**
 * Converts legacy ampersand color codes into a Minecraft {@link Component}.
 */
public final class LegacyTextComponent {

    private LegacyTextComponent() {
    }

    public static Component from(String input) {
        MutableComponent root = Component.empty();
        StringBuilder segment = new StringBuilder();
        Style style = Style.EMPTY;

        int length = input.length();
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            if (c == '&' && i + 1 < length) {
                char code = Character.toLowerCase(input.charAt(i + 1));
                if (code == 'x') {
                    int rgb = 0;
                    int j = i + 2;
                    boolean valid = j + 11 <= length;
                    for (int n = 0; n < 6 && valid; n++) {
                        if (input.charAt(j) != '&') {
                            valid = false;
                            break;
                        }
                        int digit = Character.digit(Character.toLowerCase(input.charAt(j + 1)), 16);
                        if (digit < 0) {
                            valid = false;
                            break;
                        }
                        rgb = (rgb << 4) | digit;
                        j += 2;
                    }
                    if (valid) {
                        if (segment.length() > 0) {
                            root.append(Component.literal(segment.toString()).setStyle(style));
                            segment.setLength(0);
                        }
                        style = style.withColor(rgb);
                        i = j - 1;
                        continue;
                    }
                } else {
                    ChatFormatting formatting = ChatFormatting.getByCode(code);
                    if (formatting != null) {
                        if (segment.length() > 0) {
                            root.append(Component.literal(segment.toString()).setStyle(style));
                            segment.setLength(0);
                        }
                        style = apply(style, formatting);
                        i += 1;
                        continue;
                    }
                }
            }
            segment.append(c);
        }
        if (segment.length() > 0) {
            root.append(Component.literal(segment.toString()).setStyle(style));
        }
        return root;
    }

    private static Style apply(Style style, ChatFormatting formatting) {
        if (formatting == ChatFormatting.RESET) {
            return Style.EMPTY;
        }
        if (formatting == ChatFormatting.BOLD) {
            return style.withBold(true);
        }
        if (formatting == ChatFormatting.ITALIC) {
            return style.withItalic(true);
        }
        if (formatting == ChatFormatting.UNDERLINE) {
            return style.withUnderlined(true);
        }
        if (formatting == ChatFormatting.STRIKETHROUGH) {
            return style.withStrikethrough(true);
        }
        if (formatting == ChatFormatting.OBFUSCATED) {
            return style.withObfuscated(true);
        }
        return style.withColor(formatting);
    }
}