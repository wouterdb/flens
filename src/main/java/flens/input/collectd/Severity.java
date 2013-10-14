package flens.input.collectd;


public enum Severity {
    FAILURE(1),
    WARNING(2),
    UNKNOWN(3),
    OKAY(4);

    private static final Severity[] lookup = {UNKNOWN, FAILURE, WARNING, UNKNOWN, OKAY};
    private static final String[] names = {FAILURE.name(), WARNING.name(), UNKNOWN.name(), OKAY.name()};

    public static String[] names() {
        return names;
    }

    public static Severity find(int severity) {
        if (severity > 0 && severity < lookup.length) {
            return lookup[severity];
        }
        return UNKNOWN;
    }

    public final int id;

    Severity(int severity) {
        this.id = severity;
    }

}