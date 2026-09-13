package hobbytracker;

/** Immutable authenticated-user session data. */
public record User(long id, String displayName, String email, int level) { }
