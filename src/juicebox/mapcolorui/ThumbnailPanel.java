/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2015 Broad Institute, Aiden Lab
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package juicebox.mapcolorui;

import juicebox.HiC;
import juicebox.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.*;
import java.io.Serializable;

/**
 * @author jrobinso
 * @date Aug 2, 2010
 */
public class ThumbnailPanel extends JComponent implements Serializable {


    private static final long serialVersionUID = -3856114428388478494L;
    private static final AlphaComposite ALPHA_COMP = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f);
    double xscale, yscale, scaleFactor, originX, originY;
    boolean updateRendering = false;
    int wPixels, hPixels;
    private Image image;
    private Point lastPoint = null;
    private Rectangle innerRectangle;

    public ThumbnailPanel(final MainWindow mainWindow, final HiC hic) {

        updatePlotVariables(mainWindow, hic);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() >= 1) {
                    updatePlotVariables(mainWindow, hic);
                    try {
                        int xBP = (int) (mouseEvent.getX() * xscale);
                        int yBP = (int) (mouseEvent.getY() * yscale);

                        hic.center(xBP, yBP);
                    } catch (Exception e) {
                        System.out.println("Error when thumbnail clicked");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (innerRectangle != null && innerRectangle.contains(mouseEvent.getPoint())) {
                    lastPoint = mouseEvent.getPoint();
                    setCursor(MainWindow.fistCursor);
                }

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                updatePlotVariables(mainWindow, hic);
                lastPoint = null;
                setCursor(Cursor.getDefaultCursor());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                if (lastPoint != null) {
                    updatePlotVariables(mainWindow, hic);
                    int dxBP = ((int) ((mouseEvent.getX() - lastPoint.x) * xscale));
                    int dyBP = ((int) ((mouseEvent.getY() - lastPoint.y) * yscale));
                    hic.moveBy(dxBP, dyBP);
                    lastPoint = mouseEvent.getPoint();
                }
            }
        });
    }

    private void updatePlotVariables(MainWindow mainWindow, HiC hic) {

        try {
            updateRendering = hic != null && hic.getXContext() != null;
            wPixels = mainWindow.getHeatmapPanel().getWidth();
            hPixels = mainWindow.getHeatmapPanel().getHeight();
            originX = hic.getXContext().getBinOrigin();
            originY = hic.getYContext().getBinOrigin();
            xscale = (double) hic.getZd().getXGridAxis().getBinCount() / getWidth();
            yscale = (double) hic.getZd().getYGridAxis().getBinCount() / getHeight();
            scaleFactor = hic.getScaleFactor();
        } catch (Exception e) {
            System.out.println("X/Y scale were null");
        }
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getName() {
        return null;
    }

    public void setName(String nm) {

    }

    @Override
    protected void paintComponent(Graphics g) {

        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (image != null) {
            g.drawImage(image, 0, 0, null);
            renderVisibleWindow((Graphics2D) g);
        }
    }

    private void renderVisibleWindow(Graphics2D g) {


        if (updateRendering) {

            Rectangle outerRectangle = new Rectangle(0, 0, getBounds().width, getBounds().height);

            int x = (int) (originX / xscale);
            int y = (int) (originY / yscale);
            double wBins = wPixels / scaleFactor;
            int w = (int) (wBins / xscale);
            double yBins = hPixels / scaleFactor;
            int h = (int) (yBins / yscale);

            if (w < 4) {
                int delta = 4 - w;
                x -= delta / 2;
                w = 4;
            }
            if (h < 4) {
                int delta = 4 - h;
                y -= delta / 2;
                h = 4;
            }

            innerRectangle = new Rectangle(x, y, w, h);
            Shape shape = new SquareDonut(outerRectangle, innerRectangle);

            g.setColor(Color.gray);
            g.setComposite(ALPHA_COMP);
            g.fill(shape);

            g.draw(innerRectangle);
        }
    }

    private static class SquareDonut implements Shape {
        private final Area area;

        public SquareDonut(Rectangle outerRectangle, Rectangle innerRectangle) {
            this.area = new Area(outerRectangle);
            area.subtract(new Area(innerRectangle));
        }

        public Rectangle getBounds() {
            return area.getBounds();
        }

        public Rectangle2D getBounds2D() {
            return area.getBounds2D();
        }

        public boolean contains(double v, double v1) {
            return area.contains(v, v1);
        }

        public boolean contains(Point2D point2D) {
            return area.contains(point2D);
        }

        public boolean intersects(double v, double v1, double v2, double v3) {
            return area.intersects(v, v1, v2, v3);
        }

        public boolean intersects(Rectangle2D rectangle2D) {
            return area.intersects(rectangle2D);
        }

        public boolean contains(double v, double v1, double v2, double v3) {
            return area.contains(v, v1, v2, v3);
        }

        public boolean contains(Rectangle2D rectangle2D) {
            return area.contains(rectangle2D);
        }

        public PathIterator getPathIterator(AffineTransform affineTransform) {
            return area.getPathIterator(affineTransform);
        }

        public PathIterator getPathIterator(AffineTransform affineTransform, double v) {
            return area.getPathIterator(affineTransform, v);
        }
    }
}
