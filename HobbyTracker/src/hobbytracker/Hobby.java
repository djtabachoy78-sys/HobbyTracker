package hobbytracker;

/** A persisted quest/hobby. Progress is stored as an integer from 0 to 100. */
public record Hobby(long id, long userId, String title, String description,
                    String status, int progressPercentage, int streakDays) { }
