package com.inspirationlogical.receipt.corelib.utility;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import java.util.Formatter;
import java.util.List;
import java.util.stream.Collectors;

import com.inspirationlogical.receipt.corelib.model.view.AbstractView;

public class Search {
    private static final String IGNORE_CASE = "(?i)";
    private static final String ANY_GREEDY = ".*";
    private static final String HEAD = "^";
    public static final String MATCH_HEAD_IGNORE_CASE = "(" + IGNORE_CASE + HEAD + "%s" + ANY_GREEDY + ")";
    public static final String SPACE = " ";

    private static <T extends AbstractView> List<T> search(List<T> source, String delimiter,
                                                       String pattern, String format) {
        if (pattern.contains(delimiter)) {
            return source.stream()
                    .filter(productView -> containsIgnoreCase(productView.getName(), pattern))
                    .collect(Collectors.toList());
        } else {
            Formatter formatter = new Formatter().format(format, pattern);
            return source.stream().filter(productView -> {
                List<String> tokens = asList(productView.getName().split(delimiter));
                return !tokens.stream()
                        .filter(token -> token.matches(formatter.toString()))
                        .collect(Collectors.toList())
                        .isEmpty();
            }).collect(Collectors.toList());
        }
    }

    public static class SearchBuilder <T extends AbstractView> {
        private List<T> source;
        private String delimiter = SPACE;
        private String pattern = EMPTY;
        private String format = "%s";

        public SearchBuilder(List<T> source) {
            this.source = source;
        }

        public SearchBuilder withDelimiter(String delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        public SearchBuilder withPattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public SearchBuilder withFormat(String format) {
            this.format = format;
            return this;
        }

        public List<T> search() {
            return Search.search(source, delimiter, pattern, format);
        }
    }
}
