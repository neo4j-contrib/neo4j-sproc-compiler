package test_classes.bad_record_type;

public class BadRecord {

    private static final int DEFAULT_AGE = 42;
    private final String label;
    private final int age;

    public BadRecord(String label, int age) {
        this.label = label;
        this.age = age < 0 ? DEFAULT_AGE : age;
    }
}
