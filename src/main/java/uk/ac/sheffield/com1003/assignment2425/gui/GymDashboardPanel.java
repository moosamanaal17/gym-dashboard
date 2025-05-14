package uk.ac.sheffield.com1003.assignment2425.gui;

import uk.ac.sheffield.com1003.assignment2425.codeprovided.*;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.gui.AbstractGymDashboardPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Main GUI panel for the gym dashboard
 * It allows user to:
 * - Filter gym entries using sliders and query filters
 * - Display a histogram based on selected EntryProperty
 * - View statistics (min, max, avg) for selected properties
 */
public class GymDashboardPanel extends AbstractGymDashboardPanel {

    /**
     * Construct the GUI dashboard panel with an entry catalog as its data source
     *
     * @param entryCatalog the dataset to operate on
     */
    public GymDashboardPanel(AbstractEntryCatalog entryCatalog) {
        super(entryCatalog);
    }

    /**
     * Executes all current filters (sliders and subqueries) and updates GUI
     */
    @Override
    public void executeQuery() {
        // Apply slider-based filter (age, height, weight)
        filteredEntriesList = entryCatalog.getEntriesList().stream()
                        .filter(this::passesSliderFilters)
                .collect(Collectors.toList());

        // Apply subquery-based filters (property filters from dropdown)
        for (SubQuery subQuery : subQueryList) {
            filteredEntriesList = filteredEntriesList.stream()
                    .filter(e -> entryMatchesSubQuery(e, subQuery))
                    .collect(Collectors.toList());
        }

        // Update histogram and statistics panel
        updateHistogram();
        updateStatistics();
    }

    /**
     * Clears all subquery filters and reapplies only slider filters.
     */
    @Override
    public void clearFilters() {
        subQueryList.clear();
        subQueriesTextArea.setText("");
        filteredEntriesList = entryCatalog.getEntriesList().stream()
                .filter(this::passesSliderFilters)
                .collect(Collectors.toList());
        updateHistogram();
        updateStatistics();
    }

    /**
     * Adds a new subquery filter based on user-selected combo box input
     * Executes query immediately after adding
     */
    @Override
    public void addFilter() {
        String propertyName = (String) comboQueryProperties.getSelectedItem();
        String operator = (String) comboOperators.getSelectedItem();
        String valueText = (String) value.getText().trim();

        // Skip if any input is missing
        if (propertyName == null || operator == null || valueText.isEmpty()) return;

        try {
            EntryProperty property = EntryProperty.fromPropertyName(propertyName);
            double numericValue = Double.parseDouble(valueText);

            // Validate operator
            if (!SubQuery.isValidOperator(operator)) {
                throw new IllegalArgumentException("Invalid operator: " + operator);
            }

            // Add subquery and display it
            SubQuery subQuery = new SubQuery(property, operator, numericValue);
            subQueryList.add(subQuery);

            subQueriesTextArea.append(subQuery.toString() + "\n");
            executeQuery();

        } catch (NoSuchElementException | IllegalArgumentException e) {
            // Show error if user inputs invalid value or operator
            JOptionPane.showMessageDialog(this, "Error adding filter: " + e.getMessage(),
                    "Filter Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Populates combo boxes with EntryProperties and valid operators.
     * Filters out age, height, and weight from the subquery combo box.
     */
    @Override
    public void populateComboBoxes() {
        comboOperators.removeAllItems();

        for (EntryProperty p : EntryProperty.values()) {
            // Add to filter combo box (exclude sliders)
            if (p != EntryProperty.AGE && p != EntryProperty.HEIGHT && p != EntryProperty.WEIGHT) {
                comboQueryProperties.addItem(p.name());
            }
            // Add all to histogram selection combo box
            comboBoxPropertyNames.addItem(p.name());
        }

        // Populate valid operators
        comboOperators.addItem(">");
        comboOperators.addItem("<");
        comboOperators.addItem(">=");
        comboOperators.addItem("<=");
        comboOperators.addItem("=");
        comboOperators.addItem("!=");
    }

    /**
     * Adds listeners to sliders, buttons and combo boxes to handle user interaction
     */
    @Override
    public void addListeners() {
        // Button to add a new subquery
        buttonAddFilter.addActionListener(e -> addFilter());

        // Button to reset all filters
        buttonClearFilters.addActionListener(e -> clearFilters());

        // Slider listeners which update results when sliders are moved
        ageRangeSlider.addChangeListener(e -> {
            executeQuery();
            repaint();
        });
        heightRangeSlider.addChangeListener(e -> {
            executeQuery();
            repaint();
        });
        weightRangeSlider.addChangeListener(e -> {
            executeQuery();
            repaint();
        });

        // Histogram combo box which change histogram property and repaint
        comboBoxPropertyNames.addActionListener(e -> {
            updateHistogram();
            repaint();
        });
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    /**
     * Updates the statistic panel with min, max, and average for all EntryProperties
     */
    @Override
    public void updateStatistics() {

        // show message and exit if no matches for current filter
        if (filteredEntriesList == null || filteredEntriesList.isEmpty()) {
            statisticsTextArea.setText("No entries match the filters.");
            return;
        }

        // String builder for displaying statistics
        StringBuilder stats = new StringBuilder();

        stats.append("Filtered Entries: ").append(filteredEntriesList.size()).append("\n\n");

        // Compute and display stats for each property
        for (EntryProperty property : EntryProperty.values()) {
            try {
                // calculating min, max and avg for the current property
                double min = entryCatalog.getMinimumValue(property, filteredEntriesList);
                double max = entryCatalog.getMaximumValue(property, filteredEntriesList);
                double avg = entryCatalog.getAverageValue(property, filteredEntriesList);

                // Append formatted statistics to the output string
                stats.append(property.name()).append("   ->   ")
                        .append("Min: ").append(String.format("%.2f", min)).append(",  ")
                        .append("Max: ").append(String.format("%.2f", max)).append(",  ")
                        .append("Avg: ").append(String.format("%.2f", avg)).append("\n\n");
        } catch (NoSuchElementException e) {
                // If there is no data for the current property, display it
                stats.append(property.name()).append(": No data\n");
            }
        }

        statisticsTextArea.setText(stats.toString());
    }

    /**
     * Returns the minimum value for the given property in the provided entries list
     * Returns 0 if no data is available
     *
     * @param entryProperty the property to measure
     * @param entries the entries to evaluate
     * @return the minimum value or 0
     */
    @Override
    public double getMinimumValue(EntryProperty entryProperty, List<Entry> entries) {
        try {
            return entryCatalog.getMinimumValue(entryProperty, entries);
        } catch (NoSuchElementException e) {
            return 0;
        }
    }

    /**
     * Returns the maximum value for the given property in the provided entries list
     * Returns 1 if no data is available
     *
     * @param entryProperty the property to measure
     * @param entries the entries to evaluate
     * @return the maximum value or 1
     */
    @Override
    public double getMaximumValue(EntryProperty entryProperty, List<Entry> entries) {
        try {
            return entryCatalog.getMaximumValue(entryProperty, entries);
        } catch (NoSuchElementException e) {
            return 1;
        }
    }

    /**
     * Applies slider filters for age, height and weight
     *
     * @param entry the entry to check
     * @return true if the entry passes all the slider filters
     */
    private boolean passesSliderFilters(Entry entry) {
        double minAge = ageRangeSlider.getLowerValue();
        double maxAge = ageRangeSlider.getUpperValue();
        double minHeight = heightRangeSlider.getLowerValue();
        double maxHeight = heightRangeSlider.getUpperValue();
        double minWeight = weightRangeSlider.getLowerValue();
        double maxWeight = weightRangeSlider.getUpperValue();

        return entry.getEntryProperty(EntryProperty.AGE) >= minAge
                && entry.getEntryProperty(EntryProperty.AGE) <= maxAge
                && entry.getEntryProperty(EntryProperty.HEIGHT) >= minHeight
                && entry.getEntryProperty(EntryProperty.HEIGHT) <= maxHeight
                && entry.getEntryProperty(EntryProperty.WEIGHT) >= minWeight
                && entry.getEntryProperty(EntryProperty.WEIGHT) <= maxWeight;

    }

    /**
     * Evaluates whether a single entry matches a SubQuery condition
     *
     * @param entry the gym entry to check
     * @param subQuery the filter to apply
     * @return true if the entry satisfies the subquery condition
     */
    private boolean entryMatchesSubQuery(Entry entry, SubQuery subQuery) {
        double value = entry.getEntryProperty(subQuery.getEntryProperty());
        String operator = subQuery.getOperator();
        double target = subQuery.getValue();

        return switch (operator) {
            case ">" -> value > target;
            case "<" -> value < target;
            case ">=" -> value >= target;
            case "<=" -> value <= target;
            case "=" -> value == target;
            case "!=" -> value != target;
            default -> false;
        };
    }
}

