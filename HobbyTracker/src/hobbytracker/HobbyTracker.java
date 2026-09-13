package hobbytracker;

import atlantafx.base.theme.PrimerDark;
import java.util.concurrent.*;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/** Application entry point and asynchronous database bootstrap. */
public class HobbyTracker extends Application {
    private static final String STYLESHEET="/hobbytracker/questlog.css";
    private final ExecutorService startup=Executors.newSingleThreadExecutor(r->{Thread t=new Thread(r,"questlog-startup");t.setDaemon(true);return t;});
    @Override public void start(Stage stage){Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());showSplash(stage);}
    private void showSplash(Stage stage){Label status=new Label("Initializing H2 Database Engine...");status.getStyleClass().add("muted-text");VBox card=new VBox(16,createLogo(118),new Label("QUESTLOG"),status,new ProgressIndicator());card.setAlignment(Pos.CENTER);card.setPadding(new Insets(38));card.getStyleClass().addAll("questlog-card","login-card");StackPane root=new StackPane(card);root.setPadding(new Insets(28));root.getStyleClass().add("questlog-root");Scene scene=new Scene(root,900,650);applyStyle(scene);stage.setTitle("QUESTLOG — My Personal Hobby Tracker");stage.setScene(scene);stage.show();Task<Void> task=new Task<>(){@Override protected Void call()throws Exception{updateMessage("Initializing H2 Database Engine...");DatabaseManager.initialize();updateMessage("Verifying schema and tables...");Thread.sleep(80);updateMessage("Loading Questlog...");return null;}};status.textProperty().bind(task.messageProperty());task.setOnSucceeded(e->showLogin(stage));task.setOnFailed(e->{status.textProperty().unbind();status.setText("Database startup failed.");});startup.submit(task);}
    public void showLogin(Stage stage){new HobbyTrackerLogin(this,startup).show(stage);}
    static void applyStyle(Scene scene){var css=HobbyTracker.class.getResource(STYLESHEET);if(css!=null)scene.getStylesheets().add(css.toExternalForm());}
    static ImageView createLogo(double width){var resource=HobbyTracker.class.getResource("/hobbytracker/Assets/QuestLog_Logo.png");ImageView logo=new ImageView();if(resource!=null)logo.setImage(new Image(resource.toExternalForm(),true));logo.setFitWidth(width);logo.setPreserveRatio(true);logo.setSmooth(true);return logo;}
    public static void main(String[] args){launch(args);}
}
