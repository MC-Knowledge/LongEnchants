package com.mck.longenchants;

/** Locale-independent grouping to always use commas, including for Long.MAX_VALUE. */
public final class LevelFormat {
    private static final String[] ROMAN = {
        "", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"
    };

    private LevelFormat() {}

    public static String numeric(long value) {
        final String raw = Long.toString(value);
        final int start = raw.startsWith("-") ? 1 : 0;
        final StringBuilder result = new StringBuilder(raw.length() + raw.length() / 3);
        if (start == 1) result.append('-');
        for (int i = start; i < raw.length(); i++) {
            if (i > start && (raw.length() - i) % 3 == 0) result.append(',');
            result.append(raw.charAt(i));
        }
        return result.toString();
    }

    public static String level(long level) {
        if (level >= 1 && level <= 10) return ROMAN[(int) level];
        return numeric(level);
    }

    /** Accept either ungrouped digits or correctly grouped commas; never silently truncate. */
    public static long parseLevel(String input) {
        String s = input.trim();
        if (!s.matches("(?:[0-9]+|[0-9]{1,3}(?:,[0-9]{3})+)")) {
            throw new IllegalArgumentException("Use digits or groups of three separated by commas");
        }
        try {
            return Long.parseLong(s.replace(",", ""));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Level must fit in a signed 64-bit integer", ex);
        }
    }
}
