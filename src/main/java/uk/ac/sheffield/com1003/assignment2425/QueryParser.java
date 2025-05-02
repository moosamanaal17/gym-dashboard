package uk.ac.sheffield.com1003.assignment2425;

import uk.ac.sheffield.com1003.assignment2425.codeprovided.AbstractQueryParser;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.Query;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.EntryProperty;
import uk.ac.sheffield.com1003.assignment2425.codeprovided.SubQuery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

// This class provides some default code, but it needs to be completely replaced
// TODO replace default implementations by your own implementations
// TODO you WILL NEED to add new imports
// TODO you WILL NEED to add new methods and variables and constants
// TODO you WILL NEED to add new classes
// TODO remove the comments and tips provided with this template
// TODO add your own comments
// TODO document the class and methods with JavaDoc

public class QueryParser extends AbstractQueryParser {
    @Override
    public List<Query> buildQueries(List<String> queryTokens) throws IllegalArgumentException {
        if (queryTokens == null || queryTokens.isEmpty()) {
            throw new IllegalArgumentException("Query token list is empty.");
        }
        return parseQueryTokens(queryTokens);
    }
    private List<Query> parseQueryTokens(List<String> tokens) {
        List<Query> queries = new ArrayList<>();
        ListIterator<String> iterator = tokens.listIterator();

        while (iterator.hasNext()) {
            queries.add(parseSingleQuery(iterator));
        }

        return queries;
    }
    private Query parseSingleQuery(ListIterator<String> iterator) {
        requireNextToken(iterator, "select");
        requireNextToken(iterator, "entries");
        requireNextToken(iterator, "where");

        List<SubQuery> subQueries = new ArrayList<>();

        while (iterator.hasNext()) {
            SubQuery subQuery = parseSubQuery(iterator);
            subQueries.add(subQuery);

            if (!iterator.hasNext()) {
                break;
            }

            String next = iterator.next();
            if (next.equalsIgnoreCase("and")) {
                continue;
            } else if (next.equalsIgnoreCase("select")) {
                iterator.previous();
                break;
            } else {
                throw new IllegalArgumentException("Unexpected token: '" + next + "'");
            }
        }

        if (subQueries.isEmpty()) {
            throw new IllegalArgumentException("No subqueries found after 'where.");
        }

        return new Query(subQueries);
    }

    private SubQuery parseSubQuery(ListIterator<String> iterator) {
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("Missing property in subquery.");
        }
        String propertyToken = iterator.next();

        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("Missing operator after property '" + propertyToken + "'.");
        }
        String operatorToken = iterator.next();

        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("Missing value after operator '" + operatorToken + "'.");
        }
        String valueToken = iterator.next();

        EntryProperty property = parseEntryProperty(propertyToken);

        if (!SubQuery.isValidOperator(operatorToken)) {
            throw new IllegalArgumentException("Invalid operator: " + operatorToken);
        }

        double value;
        try {
            value = Double.parseDouble(valueToken);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number: " + valueToken);
        }

        return  new SubQuery(property, operatorToken, value);
    }

    private EntryProperty parseEntryProperty(String name) {
        try {
            return  EntryProperty.fromName(name);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("No such property: " + name);
        }
    }

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
