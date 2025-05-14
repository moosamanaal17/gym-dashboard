package uk.ac.sheffield.com1003.assignment2425.gui;

import uk.ac.sheffield.com1003.assignment2425.codeprovided.gui.AbstractGymDashboardPanel;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.gui.AbstractHistogram;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.gui.AbstractHistogramPanel;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.gui.HistogramBin;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * HistogramPanel is responsible for drawing the visual representation
 * of the histogram which includes:
 * - Axes and Labels
 * - Bars based on frequency
 * - An average line
 * It renders a histogram based on the currently selected EntryProperty
 * and filtered data from the parent panel
 */

public class HistogramPanel extends AbstractHistogramPanel {
    private static final int PADDING = 100;
    private static final int AXIS_THICKNESS = 2;
    private static final int LABEL_GAP = 4;

    /**
     * Constructor for HistogramPanel
     *
     * @param parentPanel dashboard panel that owns the histogram panel
     * @param histogram histogram data model
     */
    public HistogramPanel(AbstractGymDashboardPanel parentPanel, AbstractHistogram histogram) {
        super(parentPanel, histogram);
    }

    /**
     * Overrides paintComponent to draw all histogram components
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawAxes(g2);
        drawBars(g2);
        drawYAxisLabels(g2);
        drawAverageLine(g2);
        drawLabels(g2);
        drawYAxisTitle(g2);
    }

    /**
     * Draws the x and y axes of the histogram
     */
    private void drawAxes(Graphics2D g2) {
        int width = getWidth();
        int height = getHeight();
        int x0 = PADDING;
        int y0 = height - PADDING;

        g2.setStroke(new BasicStroke(AXIS_THICKNESS));
        g2.setColor(Color.BLACK);

        g2.draw(new Line2D.Double(x0, y0, width - PADDING, y0));

        g2.draw(new Line2D.Double(x0, y0, x0, PADDING));
    }

    /**
     * Draws horizontal dashed grid lines and y-axis value labels
     */
    private void drawYAxisLabels(Graphics2D g2) {
        int xOrigin = PADDING;
        int yOrigin = getHeight() - PADDING;
        int height = getHeight();

        int maxCount = getHistogram().largestBinCount(); // Highest frequency
        int chartHeight = height - 2 * PADDING;
        int numTicks = 5; // Number of horizontal grid lines

        g2.setFont(new Font("Arial", Font.PLAIN, 10));

        for (int i = 0; i <= numTicks; i++) {
            int count = (int) Math.round((maxCount * i) / (double) numTicks);
            int y = yOrigin - (int) ((chartHeight * i) / (double) numTicks);
            String label = String.valueOf(count);

            // Draw y-axis tick label
            int labelWidth = g2.getFontMetrics().stringWidth(label);
            g2.setColor(Color.BLACK);
            g2.drawString(label, xOrigin - labelWidth - 10, y + 4);

            // Draw horizontal dashed grid line
            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                    0, new float[]{4}, 0);

            g2.setStroke(dashed);
            g2.setColor(Color.BLACK);
            g2.drawLine(xOrigin, y, getWidth() - PADDING, y);
        }
    }

    /**
     * Draws the y-axis title "frequency"
     */
    private void drawYAxisTitle(Graphics2D g2) {
        String title = "Frequency";
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int stringWidth = fm.stringWidth(title);

        // Rotate and draw the title
        Graphics2D g2Rotated = (Graphics2D) g2.create();
        g2Rotated.setColor(Color.BLACK);
        g2Rotated.rotate(-Math.PI / 2);
        g2Rotated.drawString(title, -getHeight() / 2 - stringWidth / 2, 40);
        g2Rotated.dispose();
    }

    /**
     * Draws histogram bars based on frequency
     */
    private void drawBars(Graphics2D g2) {
        List<HistogramBin> bins = getHistogram().getBinsInBoundaryOrder();
        if (bins.isEmpty()) return;

        int width = getWidth();
        int height = getHeight();
        int chartWidth = width - 2 * PADDING;
        int chartHeight = height - 2 * PADDING;

        int maxCount = getHistogram().largestBinCount();
        double barWidth = (double) chartWidth / bins.size();
        int xOrigin = PADDING;
        int yOrigin = height - PADDING;

        for (int i = 0; i < bins.size(); i++) {
            HistogramBin bin = bins.get(i);
            int count = getHistogram().getNumberOfEntriesInBin(bin);
            double normalizedHeight = (double) count / (double) maxCount;
            double barHeight = normalizedHeight * chartHeight;

            double x = xOrigin + i * barWidth;
            double y = yOrigin - barHeight;

            Rectangle2D.Double bar = new Rectangle2D.Double(x, y, barWidth, barHeight);

            g2.setColor(Color.gray);
            g2.fill(bar);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1));
            g2.draw(bar);
        }
    }

    /**
     * Draws a vertical red line at the average property value
     */
    private void drawAverageLine(Graphics2D g2) {
        double average = getHistogram().getAveragePropertyValue();
        double min = getHistogram().getMinPropertyValue();
        double max = getHistogram().getMaxPropertyValue();

        if (max == min) return;

        int width = getWidth();
        int height = getHeight();
        int chartWidth = width - 2 * PADDING;

        // Calculate normalized x-position for the average line
        double normalizedPosition = (average - min) / (max - min);
        int x = (int) (PADDING + normalizedPosition * chartWidth);
        int yTop = PADDING;
        int yBottom = height - PADDING;

        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(x, yTop, x, yBottom);
    }

    /**
     * Draws labels below each histogram bar indicating bin boundaries
     */
    private void drawLabels(Graphics2D g2) {
        List<HistogramBin> bins = getHistogram().getBinsInBoundaryOrder();
        if (bins.isEmpty()) return;

        int width = getWidth();
        int height = getHeight();
        int y0 = height - PADDING;
        int chartWidth = width - 2 * PADDING;
        double barWidth = (double) chartWidth / bins.size();

        g2.setColor(Color.BLACK);

        for (int i = 0; i < bins.size(); i++) {
            HistogramBin bin = bins.get(i);
            double x = PADDING + i * barWidth;

            // Label with lower boundary
            String label = String.format("%.1f", bin.getLowerBoundary());
            g2.drawString(label,  (int) x, y0 + 15 + LABEL_GAP);
        }

        // Label the upper boundary of the final bin
        HistogramBin last = bins.get(bins.size() - 1);
        String maxLabel = String.format("%.1f", last.getUpperBoundary());
        int xMax = width - PADDING - g2.getFontMetrics().stringWidth(maxLabel);
        g2.drawString(maxLabel, xMax, y0 + 15 + LABEL_GAP);
    }
}
