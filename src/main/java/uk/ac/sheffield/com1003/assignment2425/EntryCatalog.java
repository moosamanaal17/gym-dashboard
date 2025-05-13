package uk.ac.sheffield.com1003.assignment2425;

import uk.ac.sheffield.com1003.assignment2425.codeprovided.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This is the concrete implementation of AbstractEntryCatalog.
 * Responsible for parsing the dataset and providing methods to filter and
 * summarize gym entry data
 *
 * This class:
 * - Parse entries from a CSV line
 * - Filter entries
 * - Calculate min, max, and average for EntryProperty
 * - Gets first five entries
 *
 */
public class EntryCatalog extends AbstractEntryCatalog {

    /**
     * Constructor for EntryCatalog. Reads entries from CSV file.
     *
     * @param entryFile file path of the dataset
     * @throws IllegalArgumentException if file is empty/malformed
     * @throws IOException if an I/O error occurs
     */
    public EntryCatalog(String entryFile)
            throws IllegalArgumentException, IOException {
        super(entryFile);
    }

    /**
     * Parses a single CSV line into an EntryPropertyMap
     *
     * @param line the line of CSV text to parse
     * @return a populated EntryPropertyMap
     * @throws IllegalArgumentException if any value is missing/malformed/invalid
     */
    @Override
    public EntryPropertyMap parseEntryLine(String line) throws IllegalArgumentException {
        String[] parts = line.split(",");

        // Validates that the line contains exactly 15 field
        if (parts.length != 15) {
            throw new IllegalArgumentException("Malformed entry line: Expected 15 values, but got "
                    + parts.length + " -> " + line);
        }
        EntryPropertyMap entryPropertyMap = new EntryPropertyMap();

        // Add categorical values to the detail map
        entryPropertyMap.putDetail(EntryDetail.GENDER, requireNonEmpty(parts[1], 1, line));
        entryPropertyMap.putDetail(EntryDetail.WORKOUT_TYPE, requireNonEmpty(parts[9], 9, line));

        // Parse numerical EntryProperty values
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

        // Looping through EntryProperty fields to parse corresponding values
        for (int i = 0; i < properties.length; i++) {
            double value = parseValidatedDouble(parts[propertyIndexes[i]], propertyIndexes[i], line);
            entryPropertyMap.putProperty(properties[i], value);
        }
        return entryPropertyMap;
    }

    /**
     * Filters a list of entries based on EntryDetail value.
     *
     * @param filteredEntriesList list of entries to filter
     * @param entryDetail the detail to filter by
     * @param name case-insensitive value to match against
     * @return a new list containing only entries that match the detail
     * @throws IllegalArgumentException if name is null or blank
     */
    @Override
    public List<Entry> getEntriesListByEntryDetail(List<Entry> filteredEntriesList, EntryDetail entryDetail, String name) {

        // Validating the search string
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The detail name cannot be null or blank.");
        }

        // New list for matching entries
        List<Entry> filteredEntries = new ArrayList<>();

        for (Entry entry : filteredEntriesList) {
            // Get the value of specified EntryDetail
            String detailValue = entry.getEntryDetail(entryDetail);

            // Compare detail value to search name and add to the list
            if (detailValue != null && detailValue.equalsIgnoreCase(name.trim())) {
                filteredEntries.add(entry);
            }
        }

        filteredEntriesList = filteredEntries;
        return filteredEntriesList;
    }

    /**
     * Returns the minimum value of a given EntryProperty
     *
     * @param entryProperty the property to find the minimum for
     * @param entriesList list of entries to evaluate
     * @return the minimum value from that property
     * @throws NoSuchElementException if the list is null or empty
     */
    @Override
    public double getMinimumValue(EntryProperty entryProperty, List<Entry> entriesList) throws NoSuchElementException {

        // Check if entries are available
        if (entriesList == null || entriesList.isEmpty()) {
            throw new NoSuchElementException("Entry list is empty. Cannot compute minimum.");
        }

        double minimumValue = Double.MAX_VALUE;

        for (Entry entry : entriesList) {
            double value = entry.getEntryProperty(entryProperty);
            // Update if smaller value is found
            if (value < minimumValue) {
                minimumValue = value;
            }
        }
        return minimumValue;
    }

    /**
     * Returns the maximum value of a given EntryProperty
     *
     * @param entryProperty the property to find the maximum for
     * @param entriesList list of entries to evaluate
     * @return the maximum value from that property
     * @throws NoSuchElementException if the list is null or empty
     */
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

    /**
     * Returns the average value of a given EntryProperty
     *
     * @param entryProperty the property to find the average for
     * @param entriesList list of entries to evaluate
     * @return the average value from that property
     * @throws NoSuchElementException if the list is null or empty
     */
    @Override
    public double getAverageValue(EntryProperty entryProperty, List<Entry> entriesList) throws NoSuchElementException {
        // Throw an error if no entries are available
        if (entriesList == null || entriesList.isEmpty()) {
            throw new NoSuchElementException("Entry list is empty. Cannot compute average value.");
        }
        // Accumulate total sum
        double sum = 0.0;

        for (Entry entry : entriesList) {
            sum += entry.getEntryProperty(entryProperty);
        }
        // return avg
        return sum / entriesList.size();
    }

    /**
     * Returns the first five entries in the catalog
     *
     * @return a list of upto five entries from the data set
     */
    @Override
    public List<Entry> getFirstFiveEntries() {

        // Calculate the sublist size (max 5)
        int end = Math.min(entriesList.size(), 5);

        // Return first five entries
        return new ArrayList<>(entriesList.subList(0, end));
    }

    /**
     * Validates that a string is not null or blank
     *
     * @param value string to check
     * @param index CSV index for error context
     * @param line full CSV line
     * @return the trimmed string if the string is valid
     * @throws IllegalArgumentException if the value is missing or blank
     */
    private String requireNonEmpty(String value, int index, String line) {
        // Check if value is null or blank
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing or empty value at column index " + index + " -> " + line);
        }

        // Return trimmed version of the valid string
        return value.trim();
    }

    /**
     * Parses a string into a double after validation
     *
     * @param value the string to parse
     * @param index CSV index for error context
     * @param line full CSV line
     * @return the parsed double value
     * @throws IllegalArgumentException if parsing fails
     */
    private double parseValidatedDouble(String value, int index, String line) {

        value = requireNonEmpty(value, index, line);

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value at column index " + index + ": '" + value + "' -> " + line);
        }
    }
}
