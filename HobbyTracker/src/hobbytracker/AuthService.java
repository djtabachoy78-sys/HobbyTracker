package hobbytracker;

import java.sql.SQLException;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

public final class AuthService {
    private final QuestDao dao = new QuestDao();
    public Optional<User> login(String email,String password) throws SQLException { Optional<User> found=dao.findUser(email); if(found.isEmpty())return Optional.empty(); if(!BCrypt.checkpw(password,dao.passwordHashFor(found.get().id()).orElse("")))return Optional.empty(); dao.log(found.get().id(),"Login","Signed in to Questlog.");SessionManager.getInstance().login(found.get());return found; }
    public User register(String displayName, String email, String password) throws SQLException {
        if(displayName == null || displayName.trim().isBlank()) throw new IllegalArgumentException("Display name is required.");
        if(email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new IllegalArgumentException("Enter a valid email address.");
        if(password == null || password.length() < 8) throw new IllegalArgumentException("Password must be at least 8 characters.");
        if(dao.findUser(email.trim()).isPresent()) throw new IllegalArgumentException("An account with that email already exists.");
        User user=dao.createUser(displayName.trim(),email.trim(),BCrypt.hashpw(password,BCrypt.gensalt())); dao.log(user.id(),"Registration","Created a Questlog account.");SessionManager.getInstance().login(user);return user;
    }
    public User loginGoogle(String displayName,String email) throws SQLException { Optional<User> found=dao.findUser(email); User user=found.orElseGet(()->{try{return dao.createUser(displayName,email,BCrypt.hashpw(java.util.UUID.randomUUID().toString(),BCrypt.gensalt()));}catch(SQLException e){throw new java.io.UncheckedIOException(new java.io.IOException(e));}}); dao.log(user.id(),"Google Sign-In",found.isPresent()?"Signed in with Google.":"Created account and signed in with Google.");SessionManager.getInstance().login(user);return user; }
    public void logout(User u) throws SQLException { try { dao.log(u.id(),"Logout","Signed out of Questlog."); } finally { SessionManager.getInstance().logout(); } }
}
