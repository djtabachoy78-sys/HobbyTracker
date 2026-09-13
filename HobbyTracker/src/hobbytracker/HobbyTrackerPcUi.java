package hobbytracker;

import animatefx.animation.FadeIn;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Responsive desktop UI. Hobby entries are small in-memory demo data, ready for a real source later. */
public class HobbyTrackerPcUi {
    private final HobbyTracker application;
    private final String username;
    private final StackPane contentHost = new StackPane();
    private final VBox navigation = new VBox(6);
    private final List<Hobby> hobbies = List.of(new Hobby("Gaming", "Finish the co-op campaign", .80, "Active", 6), new Hobby("Coding", "Build Questlog", .60, "Active", 4), new Hobby("Drawing", "Portrait study", .50, "Paused", 2), new Hobby("Reading", "Science-fiction shelf", 1, "Completed", 8));

    public HobbyTrackerPcUi(HobbyTracker application) { this(application, "Adventurer"); }
    public HobbyTrackerPcUi(HobbyTracker application, String username) { this.application = application; this.username = username; }

    public void show(Stage stage) {
        BorderPane root = new BorderPane(); root.getStyleClass().add("questlog-root");
        root.setTop(header()); root.setLeft(sidebar(stage)); root.setCenter(contentHost); switchView("Dashboard", createDashboardView());
        Scene scene = new Scene(root, 1180, 760); HobbyTracker.applyStyle(scene);
        stage.setScene(scene); stage.setMinWidth(850); stage.setMinHeight(600); stage.show();
    }
    private Node header() {
        Label brand = new Label("QUESTLOG"); brand.getStyleClass().add("brand-title");
        Label caption = new Label("CONTROL CENTER"); caption.getStyleClass().add("header-caption");
        Region gap = new Region(); HBox.setHgrow(gap, Priority.ALWAYS);
        Label state = badge("● ACTIVE", "status-active");
        Button mobile = new Button("OPEN MOBILE VIEW"); mobile.getStyleClass().add("quiet-button"); mobile.setOnAction(e -> new HobbyTrackerMobileUi(application, username).show());
        HBox box = new HBox(16, new VBox(1, brand, caption), gap, new Label("68% OVERALL PROGRESS"), state, mobile, new Button(username));
        box.setAlignment(Pos.CENTER_LEFT); box.setPadding(new Insets(16, 24, 16, 24)); box.getStyleClass().add("top-header"); return box;
    }
    private Node sidebar(Stage stage) {
        Label label = new Label("YOUR QUESTS"); label.getStyleClass().add("sidebar-label");
        for (String name : List.of("Dashboard", "Quests", "Stats", "Settings", "Profile")) navigation.getChildren().add(nav(name, stage));
        Region gap = new Region(); VBox.setVgrow(gap, Priority.ALWAYS);
        Button logout = new Button("Sign out"); logout.getStyleClass().add("sidebar-signout"); logout.setOnAction(e -> application.showLogin(stage));
        VBox box = new VBox(14, label, navigation, gap, logout); box.setPadding(new Insets(24,14,20,14)); box.setPrefWidth(205); box.getStyleClass().add("questlog-sidebar"); return box;
    }
    private Button nav(String name, Stage stage) {
        Button button = new Button(name); button.setMaxWidth(Double.MAX_VALUE); button.getStyleClass().add("questlog-nav-item");
        button.setOnAction(e -> switchView(name, switch (name) { case "Quests" -> createQuestsView(); case "Stats" -> createStatsView(); case "Settings" -> createSettingsView(); case "Profile" -> createProfileView(stage); default -> createDashboardView(); })); return button;
    }
    private void switchView(String name, Node view) {
        navigation.getChildren().forEach(n -> { Button b=(Button)n; b.getStyleClass().remove("questlog-nav-item-active"); if(b.getText().equals(name)) b.getStyleClass().add("questlog-nav-item-active"); });
        contentHost.getChildren().setAll(view); new FadeIn(view).setSpeed(1.7).play();
    }
    private Node createDashboardView() {
        VBox hello = title("Welcome back, " + username, "Here is how your personal quests are progressing.");
        FlowPane metrics = new FlowPane(16,16, metric("68%", "Overall progress", "hero-number"), metric("05", "Active hobbies", "stat-number"), metric("12", "Completed quests", "stat-number"), metric("08", "Current streak", "stat-number"));
        FlowPane questCards = new FlowPane(14,14); hobbies.stream().filter(h -> !h.status.equals("Completed")).forEach(h -> questCards.getChildren().add(hobbyCard(h)));
        VBox feed = new VBox(12, activity("TODAY", "Progress updated", "Coding quest is now 60% complete."), activity("YESTERDAY", "New hobby added", "Drawing has joined your quest log."), activity("FRIDAY", "Streak increased", "Gaming streak reached 6 days.")); feed.getStyleClass().add("questlog-card");
        VBox actions = actions(); HBox lower = new HBox(16,feed,actions); HBox.setHgrow(feed,Priority.ALWAYS); feed.setMaxWidth(Double.MAX_VALUE);
        return scroll(new VBox(24,hello,section("Overall progress", "Small wins add up."),metrics,section("Active hobby quests", "Your current side quests"),questCards,section("Recent activity", "Your latest progress"),lower));
    }
    private Node createQuestsView() {
        VBox cards = new VBox(12); cards.getChildren().addAll(hobbies.stream().map(this::hobbyCard).toList());
        return scroll(new VBox(20,title("Your hobby quests", "Every hobby deserves a place in the log."),section("All quests", "Active, paused and completed hobbies"),cards));
    }
    private Node createStatsView() {
        FlowPane metrics = new FlowPane(16,16, metric("42h", "Total hours logged", "stat-number"),metric("08", "Current streak", "stat-number"),metric("12", "Quests completed", "stat-number"),metric("74%", "Monthly consistency", "hero-number"));
        VBox weekly = new VBox(12,section("Weekly momentum", "A simple snapshot until you connect real activity data."),barRow("Mon",.65),barRow("Tue",.90),barRow("Wed",.45),barRow("Thu",.75),barRow("Fri",.60)); weekly.getStyleClass().add("questlog-card");
        return scroll(new VBox(20,title("Statistics", "Your time and consistency at a glance."),metrics,weekly));
    }
    private Node createSettingsView() {
        ToggleButton dark = new ToggleButton("Dark mode"); dark.setSelected(true); CheckBox notifications = new CheckBox("Quest notifications"); notifications.setSelected(true); CheckBox sync = new CheckBox("Enable mobile syncing");
        Label note = muted("Preferences are saved for this session only.");
        Button backup = new Button("DATA BACKUP"); backup.getStyleClass().add("questlog-button-secondary"); backup.setOnAction(e -> note.setText("Backup placeholder: connect your storage provider here."));
        Button clear = new Button("CLEAR CACHE"); clear.getStyleClass().add("questlog-button-secondary"); clear.setOnAction(e -> note.setText("Cache placeholder: no local cache is currently stored."));
        VBox card = new VBox(15,dark,notifications,sync,new Separator(),backup,clear,note); card.getStyleClass().add("questlog-card");
        return scroll(new VBox(20,title("Settings", "Keep Questlog feeling like yours."),card));
    }
    private Node createProfileView(Stage stage) {
        TextField display = new TextField(username); TextField email = new TextField(username.toLowerCase().replace(' ', '.') + "@questlog.local"); Label saved = muted("Level 5 Hobbyist · Joined September 2026");
        Button save = new Button("SAVE PROFILE"); save.getStyleClass().add("questlog-button-primary"); save.setOnAction(e -> saved.setText("Profile details saved for this session."));
        Button logout = new Button("SIGN OUT"); logout.getStyleClass().add("questlog-button-secondary"); logout.setOnAction(e -> application.showLogin(stage));
        VBox card = new VBox(13,new Label("LEVEL 5 HOBBYIST"),new Label("Display name"),display,new Label("Email"),email,save,new Separator(),logout,saved); card.getStyleClass().add("questlog-card");
        return scroll(new VBox(20,title("Your profile", "The adventurer behind the quests."),card));
    }
    private VBox actions() { Button add=button("+ ADD HOBBY",true); Button update=button("UPDATE PROGRESS",false); Button complete=button("COMPLETE QUEST",false); Label note=muted("Demo actions are ready for your data layer."); return card(new VBox(12,new Label("Today’s actions"),add,update,complete,note)); }
    private VBox hobbyCard(Hobby h) { Label name=new Label(h.name);name.getStyleClass().add("quest-name"); Region topGap=new Region();HBox.setHgrow(topGap,Priority.ALWAYS); Region actionGap=new Region();HBox.setHgrow(actionGap,Priority.ALWAYS); Button update=button("UPDATE",false); update.setOnAction(e -> update.setText("UPDATED")); VBox b=card(new VBox(10,new HBox(name,topGap,badge(h.status,h.status.equals("Completed")?"status-active":"status-pending")),muted(h.detail+" · "+h.streak+" day streak"),progress(h.progress),new HBox(muted((int)(h.progress*100)+"% complete"),actionGap,update))); b.getStyleClass().add("hobby-card");return b; }
    private VBox metric(String value,String label,String style) { Label n=new Label(value);n.getStyleClass().add(style);Label l=muted(label);return card(new VBox(7,n,l)); }
    private HBox barRow(String day,double value) { Label l=new Label(day);l.setPrefWidth(38);ProgressBar p=progress(value);HBox.setHgrow(p,Priority.ALWAYS);return new HBox(10,l,p,new Label((int)(value*100)+"%")); }
    private VBox title(String heading,String detail) { Label h=new Label(heading);h.getStyleClass().add("page-greeting");return new VBox(4,h,muted(detail)); }
    private VBox section(String heading,String detail) { Label h=new Label(heading);h.getStyleClass().add("section-title");return new VBox(2,h,muted(detail)); }
    private VBox activity(String when,String heading,String detail) { Label w=new Label(when);w.getStyleClass().add("timeline-time");Label h=new Label(heading);h.getStyleClass().add("timeline-title");return new VBox(3,w,h,muted(detail)); }
    private VBox card(Node node) { VBox b=new VBox();b.getChildren().add(node);b.getStyleClass().add("questlog-card");return b; }
    private ScrollPane scroll(Node view) { VBox wrap=new VBox(view);wrap.setPadding(new Insets(30));ScrollPane s=new ScrollPane(wrap);s.setFitToWidth(true);s.getStyleClass().add("content-scroll");return s; }
    private Button button(String text,boolean primary) { Button b=new Button(text);b.getStyleClass().add(primary?"questlog-button-primary":"questlog-button-secondary");return b; }
    private Label muted(String text) { Label l=new Label(text);l.getStyleClass().add("muted-text");return l; }
    private Label badge(String text,String style) { Label l=new Label(text.toUpperCase());l.getStyleClass().addAll("status-badge",style);return l; }
    private ProgressBar progress(double value) { ProgressBar p=new ProgressBar(value);p.setMaxWidth(Double.MAX_VALUE);p.getStyleClass().add("questlog-progress");return p; }
    private record Hobby(String name,String detail,double progress,String status,int streak) { }
}
