package com.securevault.ui.components;

import com.securevault.ui.AppColors;
import com.securevault.ui.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Procedural vector icon renderer for SecureVault.
 * Generates crisp, pixel-perfect icons at arbitrary scales without image assets.
 */
public final class Icons {
    private Icons() {}

    public static Icon getIcon(String name, int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        ThemeManager.applyQualityRendering(g2);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(Math.max(1.3f, size * 0.08f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        float s = size;
        switch (name.toLowerCase()) {
            case "dashboard": {
                int pad = Math.round(s * 0.18f);
                int gap = Math.round(s * 0.12f);
                int boxW = Math.round((s - 2 * pad - gap) / 2.0f);
                g2.drawRect(pad, pad, boxW, boxW);
                g2.drawRect(pad + boxW + gap, pad, boxW, boxW);
                g2.drawRect(pad, pad + boxW + gap, boxW, boxW);
                g2.drawRect(pad + boxW + gap, pad + boxW + gap, boxW, boxW);
                break;
            }
            case "vault":
            case "key": {
                // Key icon
                float cx = s * 0.38f;
                float cy = s * 0.38f;
                float r = s * 0.22f;
                g2.draw(new Ellipse2D.Float(cx - r, cy - r, 2 * r, 2 * r));
                g2.drawLine(Math.round(cx + r * 0.7f), Math.round(cy + r * 0.7f), Math.round(s * 0.82f), Math.round(s * 0.82f));
                g2.drawLine(Math.round(s * 0.72f), Math.round(s * 0.72f), Math.round(s * 0.82f), Math.round(s * 0.62f));
                g2.drawLine(Math.round(s * 0.80f), Math.round(s * 0.80f), Math.round(s * 0.88f), Math.round(s * 0.72f));
                break;
            }
            case "shield":
            case "logo": {
                Path2D.Float path = new Path2D.Float();
                path.moveTo(s * 0.5f, s * 0.15f);
                path.lineTo(s * 0.82f, s * 0.28f);
                path.curveTo(s * 0.82f, s * 0.62f, s * 0.68f, s * 0.82f, s * 0.5f, s * 0.88f);
                path.curveTo(s * 0.32f, s * 0.82f, s * 0.18f, s * 0.62f, s * 0.18f, s * 0.28f);
                path.closePath();
                g2.draw(path);
                // Center keyhole or lock pip
                g2.fill(new Ellipse2D.Float(s * 0.46f, s * 0.42f, s * 0.08f, s * 0.08f));
                g2.drawLine(Math.round(s * 0.5f), Math.round(s * 0.46f), Math.round(s * 0.5f), Math.round(s * 0.58f));
                break;
            }
            case "generator":
            case "refresh": {
                // Two circular arrows
                int pad = Math.round(s * 0.2f);
                g2.drawArc(pad, pad, Math.round(s - 2 * pad), Math.round(s - 2 * pad), 45, 230);
                // Arrowhead
                g2.drawLine(Math.round(s * 0.75f), Math.round(s * 0.25f), Math.round(s * 0.82f), Math.round(s * 0.24f));
                g2.drawLine(Math.round(s * 0.82f), Math.round(s * 0.24f), Math.round(s * 0.80f), Math.round(s * 0.35f));
                break;
            }
            case "audit": {
                // Shield with checkmark
                Path2D.Float p = new Path2D.Float();
                p.moveTo(s * 0.5f, s * 0.16f);
                p.lineTo(s * 0.82f, s * 0.28f);
                p.curveTo(s * 0.82f, s * 0.62f, s * 0.68f, s * 0.80f, s * 0.5f, s * 0.86f);
                p.curveTo(s * 0.32f, s * 0.80f, s * 0.18f, s * 0.62f, s * 0.18f, s * 0.28f);
                p.closePath();
                g2.draw(p);
                // Checkmark
                Path2D.Float chk = new Path2D.Float();
                chk.moveTo(s * 0.36f, s * 0.50f);
                chk.lineTo(s * 0.46f, s * 0.60f);
                chk.lineTo(s * 0.64f, s * 0.38f);
                g2.draw(chk);
                break;
            }
            case "settings": {
                // Minimalist gear / sliders
                float cy1 = s * 0.32f;
                float cy2 = s * 0.68f;
                g2.drawLine(Math.round(s * 0.18f), Math.round(cy1), Math.round(s * 0.82f), Math.round(cy1));
                g2.drawLine(Math.round(s * 0.18f), Math.round(cy2), Math.round(s * 0.82f), Math.round(cy2));
                // Knobs
                g2.fill(new Ellipse2D.Float(s * 0.38f, cy1 - s * 0.10f, s * 0.20f, s * 0.20f));
                g2.fill(new Ellipse2D.Float(s * 0.58f, cy2 - s * 0.10f, s * 0.20f, s * 0.20f));
                break;
            }
            case "logout": {
                // Exit arrow from bracket
                g2.drawPolyline(
                    new int[]{Math.round(s * 0.55f), Math.round(s * 0.22f), Math.round(s * 0.22f), Math.round(s * 0.55f)},
                    new int[]{Math.round(s * 0.20f), Math.round(s * 0.20f), Math.round(s * 0.80f), Math.round(s * 0.80f)},
                    4
                );
                g2.drawLine(Math.round(s * 0.40f), Math.round(s * 0.50f), Math.round(s * 0.82f), Math.round(s * 0.50f));
                g2.drawLine(Math.round(s * 0.68f), Math.round(s * 0.36f), Math.round(s * 0.82f), Math.round(s * 0.50f));
                g2.drawLine(Math.round(s * 0.68f), Math.round(s * 0.64f), Math.round(s * 0.82f), Math.round(s * 0.50f));
                break;
            }
            case "search": {
                float r = s * 0.25f;
                float cx = s * 0.42f;
                float cy = s * 0.42f;
                g2.draw(new Ellipse2D.Float(cx - r, cy - r, 2 * r, 2 * r));
                g2.drawLine(Math.round(cx + r * 0.707f), Math.round(cy + r * 0.707f), Math.round(s * 0.82f), Math.round(s * 0.82f));
                break;
            }
            case "copy": {
                // Overlapping sheets
                int w = Math.round(s * 0.44f);
                int h = Math.round(s * 0.48f);
                g2.drawRect(Math.round(s * 0.36f), Math.round(s * 0.32f), w, h);
                g2.drawPolyline(
                    new int[]{Math.round(s * 0.22f), Math.round(s * 0.22f), Math.round(s * 0.60f)},
                    new int[]{Math.round(s * 0.64f), Math.round(s * 0.18f), Math.round(s * 0.18f)},
                    3
                );
                break;
            }
            case "eye": {
                // Open eye
                Path2D.Float p = new Path2D.Float();
                p.moveTo(s * 0.15f, s * 0.50f);
                p.curveTo(s * 0.32f, s * 0.24f, s * 0.68f, s * 0.24f, s * 0.85f, s * 0.50f);
                p.curveTo(s * 0.68f, s * 0.76f, s * 0.32f, s * 0.76f, s * 0.15f, s * 0.50f);
                p.closePath();
                g2.draw(p);
                g2.fill(new Ellipse2D.Float(s * 0.42f, s * 0.42f, s * 0.16f, s * 0.16f));
                break;
            }
            case "eye-off": {
                // Slashed eye
                Path2D.Float p = new Path2D.Float();
                p.moveTo(s * 0.15f, s * 0.50f);
                p.curveTo(s * 0.32f, s * 0.24f, s * 0.68f, s * 0.24f, s * 0.85f, s * 0.50f);
                p.curveTo(s * 0.68f, s * 0.76f, s * 0.32f, s * 0.76f, s * 0.15f, s * 0.50f);
                p.closePath();
                g2.draw(p);
                g2.fill(new Ellipse2D.Float(s * 0.42f, s * 0.42f, s * 0.16f, s * 0.16f));
                g2.drawLine(Math.round(s * 0.20f), Math.round(s * 0.80f), Math.round(s * 0.80f), Math.round(s * 0.20f));
                break;
            }
            case "plus": {
                g2.drawLine(Math.round(s * 0.5f), Math.round(s * 0.22f), Math.round(s * 0.5f), Math.round(s * 0.78f));
                g2.drawLine(Math.round(s * 0.22f), Math.round(s * 0.5f), Math.round(s * 0.78f), Math.round(s * 0.5f));
                break;
            }
            case "trash": {
                // Bin body
                g2.drawLine(Math.round(s * 0.22f), Math.round(s * 0.30f), Math.round(s * 0.78f), Math.round(s * 0.30f));
                g2.drawPolyline(
                    new int[]{Math.round(s * 0.30f), Math.round(s * 0.32f), Math.round(s * 0.68f), Math.round(s * 0.70f)},
                    new int[]{Math.round(s * 0.30f), Math.round(s * 0.82f), Math.round(s * 0.82f), Math.round(s * 0.30f)},
                    4
                );
                g2.drawLine(Math.round(s * 0.42f), Math.round(s * 0.40f), Math.round(s * 0.42f), Math.round(s * 0.72f));
                g2.drawLine(Math.round(s * 0.58f), Math.round(s * 0.40f), Math.round(s * 0.58f), Math.round(s * 0.72f));
                break;
            }
            case "pencil":
            case "edit": {
                Path2D.Float p = new Path2D.Float();
                p.moveTo(s * 0.65f, s * 0.22f);
                p.lineTo(s * 0.78f, s * 0.35f);
                p.lineTo(s * 0.38f, s * 0.75f);
                p.lineTo(s * 0.22f, s * 0.78f);
                p.lineTo(s * 0.25f, s * 0.62f);
                p.closePath();
                g2.draw(p);
                break;
            }
            case "check": {
                Path2D.Float p = new Path2D.Float();
                p.moveTo(s * 0.22f, s * 0.52f);
                p.lineTo(s * 0.42f, s * 0.72f);
                p.lineTo(s * 0.78f, s * 0.28f);
                g2.draw(p);
                break;
            }
            case "warning": {
                Path2D.Float p = new Path2D.Float();
                p.moveTo(s * 0.50f, s * 0.18f);
                p.lineTo(s * 0.84f, s * 0.80f);
                p.lineTo(s * 0.16f, s * 0.80f);
                p.closePath();
                g2.draw(p);
                g2.drawLine(Math.round(s * 0.50f), Math.round(s * 0.38f), Math.round(s * 0.50f), Math.round(s * 0.58f));
                g2.fill(new Ellipse2D.Float(s * 0.47f, s * 0.68f, s * 0.06f, s * 0.06f));
                break;
            }
            default:
                g2.drawRect(Math.round(s * 0.2f), Math.round(s * 0.2f), Math.round(s * 0.6f), Math.round(s * 0.6f));
                break;
        }

        g2.dispose();
        return new ImageIcon(img);
    }
}

