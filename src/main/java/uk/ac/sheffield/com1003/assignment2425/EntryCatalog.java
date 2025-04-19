package uk.ac.sheffield.com1003.assignment2425;

import uk.ac.sheffield.com1003.assignment2425.codeprovided.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.stream.Collectors;

// This class provides some default code, but it needs to be completely replaced
// TODO replace default implementations by your own implementations
// TODO you WILL NEED to add new imports
// TODO you WILL NEED to add new methods and variables and constants
// TODO you WILL NEED to add new classes
// TODO remove the comments and tips provided with this template
// TODO add your own comments
// TODO document the class and methods with JavaDoc

public class EntryCatalog extends AbstractEntryCatalog {

    public EntryCatalog(String entryFile)
            throws IllegalArgumentException, IOException {
        super(entryFile);
    }

    @Override
    public EntryPropertyMap parseEntryLine(String line) throws IllegalArgumentException {
        String[] parts = line.split(",");

        if (parts.length != 15) {
            throw new IllegalArgumentException("Malformed entry line: Expected 15 values, but got "
                    + parts.length + " -> " + line);
        }
        EntryPropertyMap entryPropertyMap = new EntryPropertyMap();


        entryPropertyMap.putDetail(EntryDetail.GENDER, requireNonEmpty(parts[1], 1, line));
        entryPropertyMap.putDetail(EntryDetail.WORKOUT_TYPE, requireNonEmpty(parts[9], 9, line));

        EntryProperty[] properties = {
                EntryProperty.AGE,
                EntryProperty.WEIGHT,
                EntryProperty.HEIGHT,
                EntryProperty.MAX_BPM,
                EntryProperty.AVG_BPM,
                EntryProperty.RESTING_BPM,
                EntryProperty.SESSION_DURATION,
                EntryProperty.CALORIES_BURNED,
                EntryProperty.BODY_FAT_PERCENTAGE,
                EntryProperty.WATER_INTAKE,
                EntryProperty.WORKOUT_FREQUENCY,
                EntryProperty.EXPERIENCE_LEVEL,
                EntryProperty.BMI
        };

        int[] propertyIndexes = {
                0, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14
        };

        for (int i = 0; i < properties.length; i++) {
            double value = parseValidatedDouble(parts[propertyIndexes[i]], propertyIndexes[i], line);
            entryPropertyMap.putProperty(properties[i], value);
        }
        return entryPropertyMap;
    }

    @Override
    public List<Entry> getEntriesListByEntryDetail(List<Entry> filteredEntriesList, EntryDetail entryDetail, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The detail name cannot be null or blank.");
        }
        List<Entry> filteredEntries = new ArrayList<>();

        for (Entry entry : filteredEntriesList) {
            String detailValue = entry.getEntryDetail(entryDetail);

            if (detailValue != null && detailValue.equalsIgnoreCase(name.trim())) {
                filteredEntries.add(entry);
            }
        }
        filteredEntriesList = filteredEntries;
        return filteredEntriesList;
    }

    @Override
    public double getMinimumValue(EntryProperty entryProperty, List<Entry> entriesList) throws NoSuchElementException {
        if (entriesList == null || entriesList.isEmpty()) {
            throw new NoSuchElementException("Entry list is empty. Cannot compute minimum.");
        }

        double minimumValue = Double.MAX_VALUE;

        for (Entry entry : entriesList) {
            double value = entry.getEntryProperty(entryProperty);
            if (value < minimumValue) {
                minimumValue = value;
            }
        }
        return minimumValue;
    }

    @Override
    public double getMaximumValue(EntryProperty entryProperty, List<Entry> entriesList) throws NoSuchElementException {
        if (entriesList == null || entriesList.isEmpty()) {
            throw new NoSuchElementException("Entry list is empty. Cannot compute maximum.");
        }

        double maximumValue = -Double.MAX_VALUE;

        for (Entry entry : entriesList) {
            double value = entry.getEntryProperty(entryProperty);
            if (value > maximumValue) {
                maximumValue = value;
            }
        }
        return maximumValue;
    }

    @Override
    public double getAverageValue(EntryProperty entryProperty, List<Entry> entriesList) throws NoSuchElementException {
        if (entriesList == null || entriesList.isEmpty()) {
            throw new NoSuchElementException("Entry list is empty. Cannot compute average value.");
        }
        double sum = 0.0;

        for (Entry entry : entriesList) {
            sum += entry.getEntryProperty(entryProperty);
        }
        return sum / entriesList.size();
    }

    @Override
    public List<Entry> getFirstFiveEntries() {
        int end = Math.min(entriesList.size(), 5);
        return new ArrayList<>(entriesList.subList(0, end));
    }

    private String requireNonEmpty(String value, int index, String line) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing or empty value at column index " + index + " -> " + line);
        }
        return value.trim();
    }

    private double parseValidatedDouble(String value, int index, String line) {
        value = requireNonEmpty(value, index, line);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value at column index " + index + ": '" + value + "' -> " + line);
        }
    }
}
