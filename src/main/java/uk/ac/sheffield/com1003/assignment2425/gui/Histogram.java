package uk.ac.sheffield.com1003.assignment2425.gui;

import com.sun.source.tree.Tree;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.AbstractEntryCatalog;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.Entry;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.EntryProperty;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.gui.AbstractHistogram;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.gui.HistogramBin;

import java.util.*;

// This class provides some default code, but it needs to be completely replaced
// TODO replace default implementations by your own implementations
// TODO you WILL NEED to add new imports
// TODO you WILL NEED to add new methods and variables and constants
// TODO you WILL NEED to add new classes
// TODO remove the comments and tips provided with this template
// TODO add your own comments
// TODO document the class and methods with JavaDoc

public class Histogram extends AbstractHistogram
{

    public Histogram(AbstractEntryCatalog catalog, List<Entry> filteredEntries, EntryProperty property) {
        super(catalog, filteredEntries, property);
    }

    @Override
    public void updateHistogramContents(EntryProperty property, List<Entry> filteredEntries) {
        resetHistogram(property, filteredEntries);

        if (entryList.isEmpty()) return;

        List<HistogramBin> bins = createBins(minPropertyValue, maxPropertyValue, NUMBER_BINS);
        entryCountsPerBin = countEntriesInBins(bins);
    }

    private void resetHistogram(EntryProperty property, List<Entry> filteredEntries) {
        this.property = property;
        this.entryList = filteredEntries;
        this.entryCountsPerBin.clear();

        try {
            this.maxPropertyValue = catalog.getMaximumValue(property, filteredEntries);
            this.minPropertyValue = catalog.getMinimumValue(property, filteredEntries);
        } catch (NoSuchElementException e) {
            this.minPropertyValue = 0;
            this.maxPropertyValue = 1;
        }
    }

    private List<HistogramBin> createBins(double min, double max, int binCount) {
        List<HistogramBin> bins = new ArrayList<>();
        double binWidth = (max - min) / binCount;

        for (int i = 0; i < binCount; i++) {
            double lower = min + (i * binWidth);
            double upper = lower + binWidth;
            boolean isFinalBin = (i == binCount - 1);
            bins.add(new HistogramBin(lower, upper, isFinalBin));
        }

        return bins;
    }

    private Map<HistogramBin, Integer> countEntriesInBins(List<HistogramBin> bins) {
        Map<HistogramBin, Integer> binCounts = new TreeMap<>();

        for (HistogramBin bin : bins) {
            binCounts.put(bin, 0);
        }

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

    @Override
    public double getAveragePropertyValue() throws NoSuchElementException {
        if (entryList.isEmpty()) throw new NoSuchElementException("No entries for histogram average.");

        double sum = 0.0;
        for (Entry entry : entryList) {
            sum += entry.getEntryProperty(property);
        }

        return sum / entryList.size();
    }

}
