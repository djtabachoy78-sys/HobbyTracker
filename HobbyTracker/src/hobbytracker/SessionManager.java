package hobbytracker;

import java.time.Instant;
import java.util.Optional;

/** Process-local session; it is intentionally cleared on sign out. */
public final class SessionManager {
    private static final SessionManager INSTANCE = new SessionManager();
    private volatile User currentUser;
    private volatile Instant loginTimestamp;
    private SessionManager() { }
    public static SessionManager getInstance() { return INSTANCE; }
    public synchronized void login(User user) { currentUser = user; loginTimestamp = Instant.now(); }
    public Optional<User> getCurrentUser() { return Optional.ofNullable(currentUser); }
    public Instant getLoginTimestamp() { return loginTimestamp; }
    public boolean isLoggedIn() { return currentUser != null; }
    public synchronized void logout() { currentUser = null; loginTimestamp = null; }
    // Compatibility helpers for the existing programmatic UI.
    public static void start(User user) { INSTANCE.login(user); }
    public static Optional<User> currentUser() { return INSTANCE.getCurrentUser(); }
    public static void clear() { INSTANCE.logout(); }
}
