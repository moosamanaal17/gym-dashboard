package uk.ac.sheffield.com1003.assignment2425.gui;

import uk.ac.sheffield.com1003.assignment2425.codeprovided.*;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.gui.AbstractGymDashboardPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.io.IOException;

// This class provides some default code, but it needs to be completely replaced
// TODO replace default implementations by your own implementations
// TODO you WILL NEED to add new imports
// TODO you WILL NEED to add new methods and variables and constants
// TODO you WILL NEED to add new classes
// TODO remove the comments and tips provided with this template
// TODO add your own comments
// TODO document the class and methods with JavaDoc

public class GymDashboardPanel extends AbstractGymDashboardPanel {

    public GymDashboardPanel(AbstractEntryCatalog entryCatalog) {
        super(entryCatalog);
    }

    @Override
    public void executeQuery() {
        filteredEntriesList = entryCatalog.getEntriesList().stream()
                        .filter(this::passesSliderFilters)
                .collect(Collectors.toList());

        for (SubQuery subQuery : subQueryList) {
            filteredEntriesList = filteredEntriesList.stream()
                    .filter(e -> entryMatchesSubQuery(e, subQuery))
                    .collect(Collectors.toList());
        }
        updateHistogram();
        updateStatistics();
    }

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

    @Override
    public void addFilter() {
        String propertyName = (String) comboQueryProperties.getSelectedItem();
        String operator = (String) comboOperators.getSelectedItem();
        String valueText = (String) value.getText().trim();

        if (propertyName == null || operator == null || valueText.isEmpty()) return;

        try {
            EntryProperty property = EntryProperty.fromPropertyName(propertyName);
            double numericValue = Double.parseDouble(valueText);

            if (!SubQuery.isValidOperator(operator)) {
                throw new IllegalArgumentException("Invalid operator: " + operator);
            }

            SubQuery subQuery = new SubQuery(property, operator, numericValue);
            subQueryList.add(subQuery);

            subQueriesTextArea.append(subQuery.toString() + "\n");
            executeQuery();

        } catch (NoSuchElementException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error adding filter: " + e.getMessage(),
                    "Filter Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void populateComboBoxes() {
        comboOperators.removeAllItems();

        for (EntryProperty p : EntryProperty.values()) {
            if (p != EntryProperty.AGE && p != EntryProperty.HEIGHT && p != EntryProperty.WEIGHT) {
                comboQueryProperties.addItem(p.name());
            }
            comboBoxPropertyNames.addItem(p.name());
        }

        comboOperators.addItem(">");
        comboOperators.addItem("<");
        comboOperators.addItem(">=");
        comboOperators.addItem("<=");
        comboOperators.addItem("=");
        comboOperators.addItem("!=");
    }

    @Override
    public void addListeners() {
        buttonAddFilter.addActionListener(e -> addFilter());
        buttonClearFilters.addActionListener(e -> clearFilters());

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

        comboBoxPropertyNames.addActionListener(e -> {
            updateHistogram();
            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void updateStatistics() {

        if (filteredEntriesList == null || filteredEntriesList.isEmpty()) {
            statisticsTextArea.setText("No entries match the filters.");
            return;
        }

        StringBuilder stats = new StringBuilder();

        stats.append("Filtered Entries: ").append(filteredEntriesList.size()).append("\n\n");

        for (EntryProperty property : EntryProperty.values()) {
            try {
                double min = entryCatalog.getMinimumValue(property, filteredEntriesList);
                double max = entryCatalog.getMaximumValue(property, filteredEntriesList);
                double avg = entryCatalog.getAverageValue(property, filteredEntriesList);

                stats.append(property.name()).append("   ->   ")
                        .append("Min: ").append(String.format("%.2f", min)).append(",  ")
                        .append("Max: ").append(String.format("%.2f", max)).append(",  ")
                        .append("Avg: ").append(String.format("%.2f", avg)).append("\n\n");
        } catch (NoSuchElementException e) {
                stats.append(property.name()).append(": No data\n");
            }
        }

        statisticsTextArea.setText(stats.toString());
    }

    @Override
    public double getMinimumValue(EntryProperty entryProperty, List<Entry> entries) {
        try {
            return entryCatalog.getMinimumValue(entryProperty, entries);
        } catch (NoSuchElementException e) {
            return 0;
        }
    }

    @Override
    public double getMaximumValue(EntryProperty entryProperty, List<Entry> entries) {
        try {
            return entryCatalog.getMaximumValue(entryProperty, entries);
        } catch (NoSuchElementException e) {
            return 1;
        }
    }

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

