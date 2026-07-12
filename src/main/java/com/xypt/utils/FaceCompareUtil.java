package com.xypt.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 人脸照片比对工具类
 * 使用直方图相似度 + 结构相似度进行照片比对
 */
public class FaceCompareUtil {
    private static final int COMPARE_SIZE = 128;
    private static final int HIST_BINS = 64;
    /**
     * 比对两张人脸照片的相似度
     * @param registered 注册时的基准照片
     * @param verify 验证时拍摄的照片
     * @return 相似度 0.0~1.0
     */
    public static double compareFaces(File registered, File verify) throws IOException {
        BufferedImage img1 = normalizeImage(ImageIO.read(registered));
        BufferedImage img2 = normalizeImage(ImageIO.read(verify));

        double histSim = compareHistogram(img1, img2);
        double pixelSim = comparePixels(img1, img2);

        return histSim * 0.6 + pixelSim * 0.4;
    }
    private static BufferedImage normalizeImage(BufferedImage src) {
        BufferedImage resized = new BufferedImage(COMPARE_SIZE, COMPARE_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, 0, 0, COMPARE_SIZE, COMPARE_SIZE, null);
        g.dispose();
        return resized;
    }

    private static double compareHistogram(BufferedImage img1, BufferedImage img2) {
        int[] hist1 = buildHistogram(img1);
        int[] hist2 = buildHistogram(img2);

        double dotProduct = 0, norm1 = 0, norm2 = 0;
        for (int i = 0; i < hist1.length; i++) {
            dotProduct += (double) hist1[i] * hist2[i];
            norm1 += (double) hist1[i] * hist1[i];
            norm2 += (double) hist2[i] * hist2[i];
        }

        if (norm1 == 0 || norm2 == 0) return 0;
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    private static int[] buildHistogram(BufferedImage img) {
        int[] hist = new int[HIST_BINS * 3];
        int binSize = 256 / HIST_BINS;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                hist[r / binSize]++;
                hist[HIST_BINS + g / binSize]++;
                hist[HIST_BINS * 2 + b / binSize]++;
            }
        }
        return hist;
    }

    private static double comparePixels(BufferedImage img1, BufferedImage img2) {
        long totalDiff = 0;
        int totalPixels = COMPARE_SIZE * COMPARE_SIZE;

        for (int y = 0; y < COMPARE_SIZE; y++) {
            for (int x = 0; x < COMPARE_SIZE; x++) {
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);
                int gray1 = toGray(rgb1);
                int gray2 = toGray(rgb2);
                totalDiff += Math.abs(gray1 - gray2);
            }
        }
        double avgDiff = (double) totalDiff / totalPixels;
        return Math.max(0, 1.0 - avgDiff / 128.0);
    }

    private static int toGray(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return (int) (0.299 * r + 0.587 * g + 0.114 * b);
    }
}
