package hobbytracker;

import animatefx.animation.FadeIn;
import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Entry point and simple local sign-in screen for Questlog. */
public class HobbyTracker extends Application {
    private static final String STYLESHEET = "/hobbytracker/questlog.css";

    @Override public void start(Stage stage) {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        showLogin(stage);
    }

    public void showLogin(Stage stage) {
        ImageView logo = createLogo(118);
        Label title = new Label("Welcome back"); title.getStyleClass().add("login-title");
        Label subtitle = new Label("Pick up your next side quest."); subtitle.getStyleClass().add("muted-text");
        TextField user = new TextField(); user.setPromptText("Username or email");
        PasswordField password = new PasswordField(); password.setPromptText("Password");
        Label hint = new Label("Demo mode — any username and password will work."); hint.getStyleClass().add("form-hint");
        Button login = new Button("LOGIN"); login.setMaxWidth(Double.MAX_VALUE); login.getStyleClass().add("questlog-button-primary");
        login.setOnAction(e -> {
            String displayName = user.getText().trim();
            new HobbyTrackerPcUi(this, displayName.isBlank() ? "Adventurer" : displayName).show(stage);
        });
        password.setOnAction(e -> login.fire());
        VBox card = new VBox(14, logo, title, subtitle, user, password, login, hint);
        card.setAlignment(Pos.CENTER); card.setPadding(new Insets(38)); card.setMaxWidth(390);
        card.getStyleClass().addAll("questlog-card", "login-card");
        StackPane root = new StackPane(card); root.setPadding(new Insets(28)); root.getStyleClass().add("questlog-root");
        Scene scene = new Scene(root, 900, 650); applyStyle(scene);
        stage.setTitle("QUESTLOG — My Personal Hobby Tracker"); stage.setMinWidth(520); stage.setMinHeight(500); stage.setScene(scene); stage.show();
        new FadeIn(card).setSpeed(1.4).play(); user.requestFocus();
    }

    static void applyStyle(Scene scene) {
        var css = HobbyTracker.class.getResource(STYLESHEET);
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
    }
    static ImageView createLogo(double width) {
        var resource = HobbyTracker.class.getResource("/hobbytracker/Assets/QuestLog_Logo.png");
        ImageView logo = new ImageView();
        if (resource != null) logo.setImage(new Image(resource.toExternalForm(), true));
        logo.setFitWidth(width); logo.setPreserveRatio(true); logo.setSmooth(true);
        return logo;
    }
    public static void main(String[] args) { launch(args); }
}
