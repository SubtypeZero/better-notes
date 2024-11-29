package dev.subtypezero.betternotes.util;

import dev.subtypezero.betternotes.BetterNotesPlugin;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static BufferedImage loadImage(String path) {
        return ImageUtil.loadImageResource(BetterNotesPlugin.class, path);
    }

    public static BufferedImage luminanceOffset(Image rawImg, int offset) {
        return ImageUtil.luminanceOffset(rawImg, offset);
    }

    public static ImageIcon alphaOffset(Image rawImg, float percentage) {
        return new ImageIcon(ImageUtil.alphaOffset(rawImg, percentage));
    }

    public static ImageIcon alphaOffset(Image rawImg, int offset) {
        return new ImageIcon(ImageUtil.alphaOffset(rawImg, offset));
    }
}
