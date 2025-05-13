package uk.ac.sheffield.com1003.assignment2425;

import uk.ac.sheffield.com1003.assignment2425.codeprovided.AbstractQueryParser;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.Query;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.EntryProperty;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.SubQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * This is the implementation of AbstractQueryParser.
 * Responsible for parsing a list of query tokens into valid Query objects.
 * Only EntryProperty fields and valid operators are allowed
 *
 */

public class QueryParser extends AbstractQueryParser {

    /**
     * Parses a list of query tokens into a list of Query objects.
     *
     * @param queryTokens list of tokens from the query file
     * @return list of parsed Query objects
     * @throws IllegalArgumentException if tokens are null, empty, or malformed
     */
    @Override
    public List<Query> buildQueries(List<String> queryTokens) throws IllegalArgumentException {
        if (queryTokens == null || queryTokens.isEmpty()) {
            throw new IllegalArgumentException("Query token list is empty.");
        }
        return parseQueryTokens(queryTokens);
    }

    /**
     * Converts the raw token list into multiple Query objects
     *
     * @param tokens list of all tokens
     * @return list of Query objects
     */
    private List<Query> parseQueryTokens(List<String> tokens) {

        List<Query> queries = new ArrayList<>();
        ListIterator<String> iterator = tokens.listIterator();

        // Iterate over token list and extract each query independently
        while (iterator.hasNext()) {
            queries.add(parseSingleQuery(iterator));
        }

        return queries;
    }

    /**
     * Parses a single query from the token stream. Requires the prefix
     * "select entries where" followed by one or more subqueries
     *
     * @param iterator a ListIterator over the tokens
     * @return a valid Query object
     * @throws IllegalArgumentException if any syntax errors are encountered
     */
    private Query parseSingleQuery(ListIterator<String> iterator) {

        // Confirm query starts with the correct fixed header
        requireNextToken(iterator, "select");
        requireNextToken(iterator, "entries");
        requireNextToken(iterator, "where");

        List<SubQuery> subQueries = new ArrayList<>();

        // Parse each subquery and expect 'and' between them
        while (iterator.hasNext()) {
            SubQuery subQuery = parseSubQuery(iterator);
            subQueries.add(subQuery);

            // Check if more subqueries follow
            if (!iterator.hasNext()) {
                break;
            }

            String next = iterator.next();
            if (next.equalsIgnoreCase("and")) {
                continue;
            } else if (next.equalsIgnoreCase("select")) {
                // Rewind to allow outer loop to reparse it
                iterator.previous();
                break;
            } else {
                throw new IllegalArgumentException("Unexpected token: '" + next + "'");
            }
        }

        if (subQueries.isEmpty()) {
            throw new IllegalArgumentException("No subqueries found after 'where'.");
        }

        return new Query(subQueries);
    }

    /**
     * Parses a single subquery in a specific format
     *
     * @param iterator the token stream
     * @return a valid SubQuery object
     * @throws IllegalArgumentException if syntax or content is invalid
     */
    private SubQuery parseSubQuery(ListIterator<String> iterator) {
        // Expects property
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("Missing property in subquery.");
        }
        String propertyToken = iterator.next();

        // Expects operator
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("Missing operator after property '" + propertyToken + "'.");
        }
        String operatorToken = iterator.next();

        // Expects value
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("Missing value after operator '" + operatorToken + "'.");
        }
        String valueToken = iterator.next();

        // Parse the property name into a valid EntryProperty enum
        EntryProperty property = parseEntryProperty(propertyToken);

        // Check if the property is one of the allowed ones
        if (!SubQuery.isValidOperator(operatorToken)) {
            throw new IllegalArgumentException("Invalid operator: " + operatorToken);
        }

        // Try converting the string value to a number
        double value;
        try {
            value = Double.parseDouble(valueToken);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number: " + valueToken);
        }

        // Create and return the parsed subquery
        return  new SubQuery(property, operatorToken, value);
    }

    /**
     * Converts string into a valid EntryProperty
     *
     * @param name the property name as a string
     * @return EntryProperty enum value
     * @throws IllegalArgumentException if the property name is invalid
     */
    private EntryProperty parseEntryProperty(String name) {

        try {
            return  EntryProperty.fromName(name);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("No such property: " + name);
        }
    }

    /**
     * Validates and consumes the next expected token from the list
     *
     * @param iterator the token iterator
     * @param expectedToken case-insensitive expected string
     * @throws IllegalArgumentException if the expected token is incorrect or missing
     */
    private void requireNextToken(ListIterator<String> iterator, String expectedToken) {

        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("Expected '" + expectedToken + "', but reached end of query.");
        }

        String token = iterator.next();

        if (!token.equalsIgnoreCase(expectedToken)) {
            throw new IllegalArgumentException("Expected '" + expectedToken + "', but got '" + token + "'.");
        }
    }
}
