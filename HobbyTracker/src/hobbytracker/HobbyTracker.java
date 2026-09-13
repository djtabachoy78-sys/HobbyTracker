package hobbytracker;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/** UI-only entry point. No database, auth, or persistent services are started. */
public class HobbyTracker extends Application {
    private static final String STYLESHEET = "/hobbytracker/questlog.css";
    @Override public void start(Stage stage) {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        new HobbyTrackerPcUi(this, "User").show(stage);
    }
    /** Compatibility hook for legacy classes; opens the UI-only main experience. */
    public void showLogin(Stage stage) { new HobbyTrackerMobileUi(this, "User").show(stage); }
    static void applyStyle(Scene scene) {
        var css = HobbyTracker.class.getResource(STYLESHEET);
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
    }
    static ImageView createLogo(double width) {
        var resource = HobbyTracker.class.getResource("/hobbytracker/Assets/QuestLog_Logo.png");
        ImageView logo = new ImageView();
        if (resource != null) logo.setImage(new Image(resource.toExternalForm(), true));
        logo.setFitWidth(width); logo.setPreserveRatio(true); return logo;
    }
    public static void main(String[] args) { launch(args); }
}
