/*
 * Copyright 2005 - 2026 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.site.servlet.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Parses additional OIDC authorization parameters. */
final class AdditionalAuthorizationParametersParser
{
    private static final Log LOGGER = LogFactory.getLog(AdditionalAuthorizationParametersParser.class);
    private static final char PARAM_DELIMITER = ',';
    private static final char PARAM_SEPARATOR = '=';
    private static final char QUOTE = '\'';

    private AdditionalAuthorizationParametersParser()
    {
    }

    /** Parses all configured entries into an insertion-ordered parameter map. */
    static Map<String, String> parse(String additionalParams)
    {
        Map<String, String> params = new LinkedHashMap<>();
        int entryStart = 0;
        int entryNumber = 1;
        int delimiter;
        do
        {
            delimiter = findEntryDelimiter(additionalParams, entryStart);
            int entryEnd = delimiter < 0 ? additionalParams.length() : delimiter;
            parseEntry(additionalParams.substring(entryStart, entryEnd), entryNumber++, params);
            entryStart = entryEnd + 1;
        }
        while (delimiter >= 0);
        return params;
    }

    /** Finds the next entry delimiter, ignoring delimiters inside a quoted value. */
    private static int findEntryDelimiter(String value, int entryStart)
    {
        int delimiter = value.indexOf(PARAM_DELIMITER, entryStart);
        int separator = value.indexOf(PARAM_SEPARATOR, entryStart);
        if (separator < 0 || delimiter >= 0 && delimiter < separator)
        {
            return delimiter;
        }

        int valueStart = separator + 1;
        while (valueStart < value.length() && Character.isWhitespace(value.charAt(valueStart)))
        {
            valueStart++;
        }
        if (valueStart < value.length() && value.charAt(valueStart) == QUOTE)
        {
            int closingQuote = findClosingQuote(value, valueStart + 1);
            return closingQuote < 0 ? -1 : value.indexOf(PARAM_DELIMITER, closingQuote + 1);
        }
        return delimiter;
    }

    /** Finds a closing quote while skipping escaped quote pairs. */
    private static int findClosingQuote(String value, int position)
    {
        int quote = value.indexOf(QUOTE, position);
        while (quote >= 0 && quote + 1 < value.length() && value.charAt(quote + 1) == QUOTE)
        {
            quote = value.indexOf(QUOTE, quote + 2);
        }
        return quote;
    }

    /** Validates one configured entry and adds it to the result map. */
    private static void parseEntry(String entry, int entryNumber, Map<String, String> params)
    {
        int separator = entry.indexOf(PARAM_SEPARATOR);
        if (separator < 0)
        {
            if (!entry.isBlank())
            {
                logMalformedEntry(entryNumber, "missing '" + PARAM_SEPARATOR + "' separator");
            }
            return;
        }

        String name = entry.substring(0, separator).strip();
        if (name.isEmpty())
        {
            logMalformedEntry(entryNumber, "empty parameter name");
            return;
        }
        parseValue(entry.substring(separator + 1), entryNumber).ifPresent(value -> params.put(name, value));
    }

    /** Parses an unquoted or single-quoted parameter value. */
    private static Optional<String> parseValue(String configuredValue, int entryNumber)
    {
        String value = configuredValue.strip();
        if (value.isEmpty() || value.charAt(0) != QUOTE)
        {
            return Optional.of(value);
        }

        StringBuilder parsedValue = new StringBuilder();
        for (int position = 1; position < value.length(); position++)
        {
            char current = value.charAt(position);
            if (current != QUOTE)
            {
                parsedValue.append(current);
            }
            else if (position + 1 < value.length() && value.charAt(position + 1) == QUOTE)
            {
                parsedValue.append(QUOTE);
                position++;
            }
            else if (value.substring(position + 1).isBlank())
            {
                return Optional.of(parsedValue.toString());
            }
            else
            {
                logMalformedEntry(entryNumber, "unexpected characters after quoted value");
                return Optional.empty();
            }
        }
        logMalformedEntry(entryNumber, "unterminated quoted value");
        return Optional.empty();
    }

    /** Logs why a configured entry was skipped. */
    private static void logMalformedEntry(int entryNumber, String reason)
    {
        LOGGER.warn("Ignoring malformed entry " + entryNumber
            + " in aims.authorization.additional-params: " + reason);
    }
}
