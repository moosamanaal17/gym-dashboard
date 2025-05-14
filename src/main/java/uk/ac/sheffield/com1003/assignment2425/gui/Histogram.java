package uk.ac.sheffield.com1003.assignment2425.gui;

import uk.ac.sheffield.com1003.assignment2425.codeprovided.AbstractEntryCatalog;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.Entry;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.EntryProperty;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.gui.AbstractHistogram;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.gui.HistogramBin;

import java.util.*;

/**
 * concrete implementation of AbstractHistogram which is responsible for:
 * - Creating histogram bins based on a selected EntryProperty
 * - Distributing entries across bins
 * - Providing histogram related stats
 */

public class Histogram extends AbstractHistogram {

    /**
     * Constructor for Histogram
     *
     * @param catalog the entry catalog containing all data
     * @param filteredEntries entries to be used in the histogram
     * @param property the EntryProperty to build the histogram for
     */
    public Histogram(AbstractEntryCatalog catalog, List<Entry> filteredEntries, EntryProperty property) {
        super(catalog, filteredEntries, property);
    }

    /**
     * updates histogram data by computing bins and assigning entry counts
     * based on the selected property.
     *
     * @param property the property to base the histogram on
     * @param filteredEntries the entries to include in the histogram
     */
    @Override
    public void updateHistogramContents(EntryProperty property, List<Entry> filteredEntries) {
        // Reset internal state for new property and entries
        resetHistogram(property, filteredEntries);

        // Exit early if no entries
        if (entryList.isEmpty()) return;

        // Create bins based on value range and count howmany entries fall in each
        List<HistogramBin> bins = createBins(minPropertyValue, maxPropertyValue, NUMBER_BINS);
        entryCountsPerBin = countEntriesInBins(bins);
    }

    /**
     * Resets histogram with a new property and entry list
     *
     * @param property the property being visualized
     * @param filteredEntries entries used for histogram calculations
     */
    private void resetHistogram(EntryProperty property, List<Entry> filteredEntries) {

        this.property = property;
        this.entryList = filteredEntries;
        this.entryCountsPerBin.clear();

        try {
            // Determine min and max values for the selected property
            this.maxPropertyValue = catalog.getMaximumValue(property, filteredEntries);
            this.minPropertyValue = catalog.getMinimumValue(property, filteredEntries);
        } catch (NoSuchElementException e) {
            // If list is empty or fails, use fallback values
            this.minPropertyValue = 0;
            this.maxPropertyValue = 1;
        }
    }

    /**
     * Creates bins for histogram using equal width based in min and max property values
     *
     * @param min minimum property value
     * @param max maximum property value
     * @param binCount number of bins to create
     * @return list of HistogramBin objects
     */
    private List<HistogramBin> createBins(double min, double max, int binCount) {

        List<HistogramBin> bins = new ArrayList<>();
        double binWidth = (max - min) / binCount;

        // Create bins with calculated bounds
        for (int i = 0; i < binCount; i++) {
            double lower = min + (i * binWidth);
            double upper = lower + binWidth;
            boolean isFinalBin = (i == binCount - 1); // upper bound for the last bin
            bins.add(new HistogramBin(lower, upper, isFinalBin));
        }

        return bins;
    }

    /**
     * Counts how many entries fall within each histogram bins
     *
     * @param bins list of bins to check entries against
     * @return a map of bin to entry count
     */
    private Map<HistogramBin, Integer> countEntriesInBins(List<HistogramBin> bins) {
        Map<HistogramBin, Integer> binCounts = new TreeMap<>();

        // Each bin with count 0 initially
        for (HistogramBin bin : bins) {
            binCounts.put(bin, 0);
        }

        // For each entry, find which bin its value belongs
        for (Entry entry : entryList) {
            double value = entry.getEntryProperty(property);
            for (HistogramBin bin : bins) {
                if (bin.valueInBin(value)) {
                    binCounts.put(bin, binCounts.get(bin) + 1);
                    break;
                }
            }
        }

        return binCounts;
    }

    /**
     * Computes the average value of the current histogram property
     *
     * @return the average value
     * @throws NoSuchElementException if the entry list is empty
     */
    @Override
    public double getAveragePropertyValue() throws NoSuchElementException {

        if (entryList.isEmpty()) throw new NoSuchElementException("No entries for histogram average.");

        double sum = 0.0;

        // Sum property values from all entries
        for (Entry entry : entryList) {
            sum += entry.getEntryProperty(property);
        }

        return sum / entryList.size();
    }

}
