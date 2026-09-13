package hobbytracker;

import animatefx.animation.FadeIn;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Narrow-screen companion UI with independently switchable dashboard, quests, stats and profile views. */
public class HobbyTrackerMobileUi {
    private final HobbyTracker application;
    private final String username;
    private final StackPane contentHost = new StackPane();
    private final HBox bottomNav = new HBox(4);
    private final List<Hobby> hobbies = List.of(new Hobby("Gaming", "Finish the co-op campaign", .80, "Active", 6), new Hobby("Coding", "Build Questlog", .60, "Active", 4), new Hobby("Drawing", "Portrait study", .50, "Paused", 2), new Hobby("Reading", "Science-fiction shelf", 1, "Completed", 8));
    public HobbyTrackerMobileUi(HobbyTracker application) { this(application, SessionManager.currentUser().map(User::displayName).orElse("Adventurer")); }
    public HobbyTrackerMobileUi(HobbyTracker application,String username) { this.application=application;this.username=username; }
    public void show() {
        Stage stage=new Stage(); BorderPane root=new BorderPane();root.getStyleClass().add("questlog-root");root.setTop(header());root.setCenter(contentHost);root.setBottom(bottom(stage));switchView("Home",dashboard());
        Scene scene=new Scene(root,390,720);HobbyTracker.applyStyle(scene);stage.setTitle("QUESTLOG Mobile");stage.setMinWidth(340);stage.setMinHeight(560);stage.setScene(scene);stage.show();
    }
    private Node header() {
        Label brand=new Label("QUESTLOG");brand.getStyleClass().add("brand-title");Label hello=muted("Welcome back, "+username);Label status=badge("ACTIVE","status-active");Region gap=new Region();HBox.setHgrow(gap,Priority.ALWAYS);Button notice=new Button("NOTICES");notice.getStyleClass().add("quiet-button");Button me=new Button("ME");me.getStyleClass().add("profile-button");
        HBox box=new HBox(8,new VBox(1,brand,hello),gap,status,notice,me);box.setAlignment(Pos.CENTER_LEFT);box.setPadding(new Insets(12,16,12,16));box.getStyleClass().add("top-header");return box;
    }
    private Node bottom(Stage stage) { for(String tab:List.of("Home","Quests","Stats","Profile")) bottomNav.getChildren().add(nav(tab,stage));bottomNav.setPadding(new Insets(9,12,12,12));bottomNav.getStyleClass().add("mobile-navigation");return bottomNav; }
    private Button nav(String tab,Stage stage) { Button b=new Button(tab);b.setMaxWidth(Double.MAX_VALUE);HBox.setHgrow(b,Priority.ALWAYS);b.getStyleClass().add("mobile-nav-item");b.setOnAction(e->switchView(tab,switch(tab){case "Quests"->quests();case "Stats"->stats();case "Profile"->profile(stage);default->dashboard();}));return b; }
    private void switchView(String tab,Node view) { for(Node n:bottomNav.getChildren()){Button b=(Button)n;b.getStyleClass().remove("mobile-nav-active");if(b.getText().equals(tab))b.getStyleClass().add("mobile-nav-active");}contentHost.getChildren().setAll(view);new FadeIn(view).setSpeed(1.8).play(); }
    private Node dashboard() {
        Label percent=new Label("68%");percent.getStyleClass().add("hero-number");VBox overall=card(new VBox(8,percent,new Label("Overall progress"),bar(.68),muted("5 active hobbies · 12 completed · 8 day streak")));
        VBox active=new VBox(10);hobbies.stream().filter(h->h.status.equals("Active")).forEach(h->active.getChildren().add(hobbyCard(h,false)));
        VBox feed=card(new VBox(10,activity("TODAY","Progress updated","Coding is now 60% complete."),activity("YESTERDAY","Hobby added","Drawing joined your quest log."),activity("FRIDAY","Streak increased","Gaming reached a 6 day streak.")));
        Button add=button("+ ADD HOBBY",true);Label result=muted("Quick actions are local UI placeholders.");Button update=button("UPDATE PROGRESS",false);update.setOnAction(e->result.setText("Select a hobby to connect progress updates."));Button complete=button("COMPLETE QUEST",false);complete.setOnAction(e->result.setText("Select a hobby to connect completion."));
        return scroll(new VBox(18,heading("Dashboard","Your hobbies, treated like quests."),overall,section("Active hobby quests","Keep your current side quests moving."),active,section("Recent activity","Small wins, logged."),feed,section("Quick actions",""),add,update,complete,result));
    }
    private Node quests() {
        VBox list=new VBox(12);renderFilter(list,"All");HBox filters=new HBox(6);for(String filter:List.of("All","Active","Completed","Paused")){Button b=new Button(filter);b.getStyleClass().add("filter-button");b.setOnAction(e->renderFilter(list,filter));filters.getChildren().add(b);}
        return scroll(new VBox(18,heading("Your quests","Filter and update your hobbies."),filters,list));
    }
    private void renderFilter(VBox list,String filter) { list.getChildren().setAll(hobbies.stream().filter(h->filter.equals("All")||h.status.equals(filter)).map(h->hobbyCard(h,true)).toList()); }
    private Node stats() {
        HBox row1=new HBox(12,metric("42h","Hours logged"),metric("08","Day streak"));HBox row2=new HBox(12,metric("12","Completed"),metric("74%","Consistency"));
        CategoryAxis x=new CategoryAxis();NumberAxis y=new NumberAxis();y.setLabel("Hours");BarChart<String,Number> chart=new BarChart<>(x,y);chart.setLegendVisible(false);chart.setAnimated(false);chart.setPrefHeight(235);XYChart.Series<String,Number> series=new XYChart.Series<>();series.getData().add(new XYChart.Data<>("Mon",2));series.getData().add(new XYChart.Data<>("Tue",4));series.getData().add(new XYChart.Data<>("Wed",1));series.getData().add(new XYChart.Data<>("Thu",3));series.getData().add(new XYChart.Data<>("Fri",2));chart.getData().add(series);chart.getStyleClass().add("questlog-chart");VBox chartCard=card(new VBox(8,section("Weekly activity","Hours logged this week"),chart));
        return scroll(new VBox(18,heading("Statistics","Your recent momentum."),row1,row2,chartCard));
    }
    private Node profile(Stage stage) {
        TextField display=new TextField(username);TextField email=new TextField(username.toLowerCase().replace(' ','.')+"@questlog.local");Label status=muted("Level 5 Hobbyist · Joined September 2026");Button save=button("SAVE PROFILE",true);save.setOnAction(e->status.setText("Profile saved for this session."));Button signOut=button("SIGN OUT",false);signOut.setOnAction(e->{stage.close();application.showLogin(stage);});
        return scroll(new VBox(18,heading("Your profile","The adventurer behind the quests."),card(new VBox(12,new Label("LEVEL 5 HOBBYIST"),new Label("Display name"),display,new Label("Email"),email,save,status,signOut))));
    }
    private VBox hobbyCard(Hobby h,boolean action) { Label name=new Label(h.name);name.getStyleClass().add("quest-name");Region g1=new Region();HBox.setHgrow(g1,Priority.ALWAYS);HBox top=new HBox(name,g1,badge(h.status,h.status.equals("Completed")?"status-active":"status-pending"));VBox content=new VBox(9,top,muted(h.detail+" · "+h.streak+" day streak"),bar(h.progress),muted((int)(h.progress*100)+"% complete"));if(action){Button update=button("UPDATE",false);update.setOnAction(e->update.setText("UPDATED"));content.getChildren().add(update);}return card(content); }
    private VBox metric(String value,String label) { Label n=new Label(value);n.getStyleClass().add("stat-number");VBox box=card(new VBox(5,n,muted(label)));HBox.setHgrow(box,Priority.ALWAYS);box.setMaxWidth(Double.MAX_VALUE);return box; }
    private VBox heading(String h,String d) { Label title=new Label(h);title.getStyleClass().add("page-greeting");return new VBox(3,title,muted(d)); }
    private VBox section(String h,String d) { Label title=new Label(h);title.getStyleClass().add("section-title");return new VBox(2,title,muted(d)); }
    private VBox activity(String time,String title,String detail) { Label t=new Label(time);t.getStyleClass().add("timeline-time");Label h=new Label(title);h.getStyleClass().add("timeline-title");return new VBox(2,t,h,muted(detail)); }
    private VBox card(Node content) { VBox b=new VBox(content);b.getStyleClass().addAll("questlog-card","mobile-quest-card");return b; }
    private ScrollPane scroll(Node content) { VBox wrap=new VBox(content);wrap.setPadding(new Insets(16));ScrollPane s=new ScrollPane(wrap);s.setFitToWidth(true);s.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);s.getStyleClass().add("content-scroll");return s; }
    private Button button(String text,boolean primary) { Button b=new Button(text);b.setMaxWidth(Double.MAX_VALUE);b.getStyleClass().add(primary?"questlog-button-primary":"questlog-button-secondary");return b; }
    private Label muted(String t) { Label l=new Label(t);l.getStyleClass().add("muted-text");return l; }
    private Label badge(String t,String style) { Label l=new Label(t.toUpperCase());l.getStyleClass().addAll("status-badge",style);return l; }
    private ProgressBar bar(double value) { ProgressBar p=new ProgressBar(value);p.setMaxWidth(Double.MAX_VALUE);p.getStyleClass().add("questlog-progress");return p; }
    private record Hobby(String name,String detail,double progress,String status,int streak) { }
}
