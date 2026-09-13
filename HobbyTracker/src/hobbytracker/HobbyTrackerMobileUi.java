package hobbytracker;

import animatefx.animation.FadeIn;
import java.util.List;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/** Single-window mobile UI. Every number, quest and interaction is intentionally mock data. */
public final class HobbyTrackerMobileUi {
    private final HobbyTracker app; private final String user; private final ThemeManager theme = new ThemeManager();
    private final BorderPane root = new BorderPane(); private final StackPane host = new StackPane(); private final HBox nav = new HBox(2);
    private String tab = "Home";
    private final List<Quest> data = List.of(new Quest("Jogging","Run the riverside route",.72,"ACTIVE","🏃"),new Quest("Gym","Build upper body strength",.45,"ACTIVE","🏋"),new Quest("Reading","Finish Atomic Habits",.86,"ACTIVE","📖"),new Quest("Meditation","Seven calm mornings",1,"DONE","✦"));
    public HobbyTrackerMobileUi(HobbyTracker app) { this(app,"User"); }
    public HobbyTrackerMobileUi(HobbyTracker app,String user) { this.app=app; this.user=user; }
    public void show(Stage stage) {
        root.getStyleClass().add("questlog-root"); theme.apply(root); root.setTop(header(null)); root.setCenter(host); root.setBottom(bottom());
        Scene scene=new Scene(root,375,812); HobbyTracker.applyStyle(scene); stage.setTitle("QUESTLOG"); stage.setMinWidth(340); stage.setMinHeight(620); stage.setScene(scene); stage.show(); open("Home");
    }
    private Node header(String detail) {
        Label name=new Label(detail==null?"QUESTLOG":detail); name.getStyleClass().add("brand-title");
        Label sub=muted(detail==null?"Welcome back, "+user:"Activity quest"); Region gap=space(); Button avatar=new Button("◉"); avatar.getStyleClass().add("avatar-button");
        HBox h=new HBox(10); if(detail!=null){Button back=new Button("‹");back.getStyleClass().add("back-button");back.setOnAction(e->open(tab));h.getChildren().add(back);} h.getChildren().addAll(new VBox(1,name,sub),gap,avatar); h.setAlignment(Pos.CENTER_LEFT);h.setPadding(new Insets(12,16,12,16));h.getStyleClass().add("top-header");return h;
    }
    private Node bottom() { nav.setPadding(new Insets(8,10,12,10));nav.getStyleClass().add("mobile-navigation"); for(String[] x:new String[][]{{"⌂","Home"},{"✓","Quests"},{"▥","Stats"},{"◉","Profile"}}){Button b=new Button(x[0]+"\n"+x[1]);b.setMaxWidth(Double.MAX_VALUE);HBox.setHgrow(b,Priority.ALWAYS);b.getStyleClass().add("mobile-nav-item");b.setOnAction(e->open(x[1]));nav.getChildren().add(b);}return nav; }
    private void open(String next) { tab=next; root.setTop(header(null)); Node v=switch(next){case "Quests"->quests();case "Stats"->stats();case "Profile"->profile();default->home();}; set(v); for(Node n:nav.getChildren()){Button b=(Button)n;b.getStyleClass().remove("mobile-nav-active");if(b.getText().endsWith(next))b.getStyleClass().add("mobile-nav-active");} }
    private void detail(String title,Node view) { root.setTop(header(title));set(view); }
    private void set(Node n) { host.getChildren().setAll(n);new FadeIn(n).setSpeed(1.8).play(); }
    private Node home() {
        GridPane grid=new GridPane();grid.setHgap(10);grid.setVgap(10);grid.add(metric("68%","Overall progress","orange"),0,0);grid.add(metric("8","Day streak","cyan"),1,0);grid.add(metric("3","Active quests","green"),0,1);grid.add(metric("12","Completed","purple"),1,1);grid.getColumnConstraints().addAll(col(),col());
        VBox list=new VBox(10);data.subList(0,3).forEach(q->list.getChildren().add(quest(q)));
        Button add=primary("＋ ADD QUEST");add.setOnAction(e->add.setText("QUEST ADDED (DEMO)"));Button ranks=secondary("VIEW MAXXING RANKS");ranks.setOnAction(e->detail("MAXXING RANKS",ranks()));
        return scroll(new VBox(18,title("Home","Your next win is waiting."),section("Progress summary","Level 14 · 2,480 XP"),grid,section("Active quests","Keep the momentum going."),list,add,ranks));
    }
    private Node quests() {
        HBox filters=new HBox(7);ToggleGroup g=new ToggleGroup();for(String x:List.of("All","Active","Done")){ToggleButton b=chip(x,g);if(x.equals("All"))b.setSelected(true);filters.getChildren().add(b);}VBox list=new VBox(10);data.forEach(q->list.getChildren().add(quest(q)));return scroll(new VBox(18,title("Quests","Your active and completed missions."),filters,list));
    }
    private VBox quest(Quest q) {
        Label icon=new Label(q.icon);icon.getStyleClass().add("quest-icon");Label name=new Label(q.name);name.getStyleClass().add("quest-name");Label status=new Label(q.status);status.getStyleClass().addAll("status-badge",q.status.equals("DONE")?"status-complete":"status-active");
        VBox info=new VBox(6,new HBox(8,name,space(),status),muted(q.detail),bar(q.progress),muted((int)(q.progress*100)+"% complete"));HBox.setHgrow(info,Priority.ALWAYS);VBox card=card(new HBox(12,icon,info));card.setOnMouseClicked(e->{switch(q.name){case "Jogging"->detail("JOGGING",jogging());case "Gym"->detail("GYM WORKOUT",gym());case "Reading"->detail("READING",reading());}});return card;
    }
    private Node stats() {
        VBox week=card(new VBox(12,section("Weekly activity","5h 40m this week"),week("Mon",.35),week("Tue",.75),week("Wed",.45),week("Thu",.9),week("Fri",.6),week("Sat",.3),week("Sun",.5)));
        VBox breakdown=card(new VBox(10,section("Activity breakdown","Where your XP comes from"),breakdown("🏃 Jogging",.42),breakdown("🏋 Gym",.31),breakdown("📖 Reading",.27)));
        return scroll(new VBox(18,title("Stats","Your consistency, visualized."),metricRow("2,480","Total XP","42","Quests done"),metricRow("8 days","Current streak","74%","Weekly goal"),week,breakdown));
    }
    private Node profile() {
        Label avatar=new Label("U");avatar.getStyleClass().add("profile-avatar");Label name=new Label(user);name.getStyleClass().add("profile-name");VBox identity=card(new VBox(8,avatar,name,muted("user@questlog.demo"),new Label("LEVEL 14  •  2,480 XP  •  GOLD")));identity.setAlignment(Pos.CENTER);
        ToggleButton dark=new ToggleButton("Dark Mode");dark.setSelected(theme.isDark());dark.getStyleClass().add("theme-toggle");dark.selectedProperty().addListener((o,old,v)->theme.setDark(v,root));Button ranks=secondary("MAXXING RANKS");ranks.setOnAction(e->detail("MAXXING RANKS",ranks()));
        return scroll(new VBox(18,title("Profile","Your adventurer profile."),identity,section("Appearance","Choose your questing mode."),card(new HBox(12,new VBox(3,new Label("Theme"),muted("Switch instantly")),space(),dark)),ranks,secondary("SETTINGS  ›")));
    }
    private Node jogging() {
        ToggleGroup g=new ToggleGroup();ToggleButton metres=chip("Meters",g),miles=chip("Miles",g);metres.setSelected(true);VBox tracker=card(new VBox(12,section("Run tracker","Demo session ready"),track("Distance","00.00 km"),track("Time","00:00:00"),track("Pace","--:--")));VBox map=new VBox(new Label("MAP PLACEHOLDER\n\n    ● ─── ● ─── ●"));map.setAlignment(Pos.CENTER);map.setMinHeight(180);map.getStyleClass().add("map-placeholder");return scroll(new VBox(18,section("Target distance","Set a goal for your next run."),new HBox(8,metres,miles),card(new VBox(8,new Label("Distance"),new Spinner<Double>(.5,100,5,.5))),tracker,section("Route","Map integration arrives later."),map,primary("START DEMO RUN")));
    }
    private Node gym() {
        HBox muscles=new HBox(6);ToggleGroup g=new ToggleGroup();for(String x:List.of("Legs","Back","Biceps","Triceps","Chest")){ToggleButton b=chip(x,g);if(x.equals("Legs"))b.setSelected(true);muscles.getChildren().add(b);}return scroll(new VBox(18,section("Target muscle","Pick today’s focus."),muscles,section("Workout recommendations","Legs · mock recommendations"),workout("Barbell Squat",4,8),workout("Romanian Deadlift",3,10),workout("Walking Lunges",3,12)));
    }
    private Node reading() {
        Label timer=new Label("25:00");timer.getStyleClass().add("reading-timer");Button start=primary("START");start.setOnAction(e->start.setText("RUNNING (DEMO)"));HBox controls=new HBox(8,start,secondary("PAUSE"),secondary("RESET"));HBox.setHgrow(start,Priority.ALWAYS);return scroll(new VBox(18,section("Focus session","A calm reading sprint."),card(new VBox(8,timer,muted("Pomodoro focus timer"))),controls,section("Reading targets","Choose your session goals."),card(new VBox(12,new Label("Target duration · 25 minutes"),new Slider(5,90,25),new Label("Target pages"),new Spinner<Integer>(1,250,20)))));
    }
    private Node ranks() { return scroll(new VBox(18,title("Maxxing Ranks","Earn XP. Climb every tier."),rank("🏃","JOGGING","Gold",720,"Platinum",.72),rank("🏋","GYM","Silver",430,"Gold",.43),rank("📖","READING","Bronze",210,"Silver",.21),card(new VBox(7,new Label("RANK TIERS"),muted("Wood › Bronze › Silver › Gold › Platinum"),muted("Diamond › Champion › Titan › Olympian"))))); }
    private VBox rank(String icon,String activity,String current,int xp,String next,double p){Label badge=new Label(current.toUpperCase());badge.getStyleClass().add("rank-badge");return card(new VBox(9,new HBox(8,new Label(icon),new Label(activity),space(),badge),new Label(current),bar(p),muted(xp+" / 1000 XP"),muted("Next rank: "+next)));}
    private VBox workout(String name,int sets,int reps){return card(new VBox(9,new HBox(new Label(name),space(),secondary("SELECT")),muted("Recommended set"),new HBox(8,new VBox(4,muted("Sets"),new Spinner<Integer>(1,10,sets)),new VBox(4,muted("Reps"),new Spinner<Integer>(1,30,reps)))));}
    private HBox track(String a,String b){Label value=new Label(b);value.getStyleClass().add("tracker-value");return new HBox(new VBox(2,muted(a),value),space());}
    private HBox week(String day,double p){Label d=new Label(day);d.setMinWidth(32);ProgressBar b=bar(p);HBox.setHgrow(b,Priority.ALWAYS);return new HBox(10,d,b,muted((int)(p*60)+"m"));}
    private HBox breakdown(String text,double p){ProgressBar b=bar(p);HBox.setHgrow(b,Priority.ALWAYS);return new HBox(8,new Label(text),b,muted((int)(p*100)+"%"));}
    private HBox metricRow(String v1,String l1,String v2,String l2){VBox a=metric(v1,l1,"cyan"),b=metric(v2,l2,"orange");HBox.setHgrow(a,Priority.ALWAYS);HBox.setHgrow(b,Priority.ALWAYS);return new HBox(10,a,b);}
    private VBox metric(String value,String label,String color){Label n=new Label(value);n.getStyleClass().addAll("stat-number",color);VBox v=card(new VBox(4,n,muted(label)));v.setMaxWidth(Double.MAX_VALUE);return v;}
    private VBox title(String h,String d){Label l=new Label(h);l.getStyleClass().add("page-greeting");return new VBox(3,l,muted(d));} private VBox section(String h,String d){Label l=new Label(h);l.getStyleClass().add("section-title");return new VBox(2,l,muted(d));}
    private VBox card(Node n){VBox b=new VBox(n);b.getStyleClass().addAll("questlog-card","mobile-quest-card");return b;} private ScrollPane scroll(Node n){VBox w=new VBox(n);w.setPadding(new Insets(16,16,22,16));ScrollPane s=new ScrollPane(w);s.setFitToWidth(true);s.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);s.getStyleClass().add("content-scroll");return s;}
    private Button primary(String t){Button b=new Button(t);b.setMaxWidth(Double.MAX_VALUE);b.getStyleClass().add("questlog-button-primary");return b;}private Button secondary(String t){Button b=new Button(t);b.getStyleClass().add("questlog-button-secondary");return b;}private Label muted(String t){Label l=new Label(t);l.getStyleClass().add("muted-text");return l;}private ProgressBar bar(double p){ProgressBar b=new ProgressBar(p);b.setMaxWidth(Double.MAX_VALUE);b.getStyleClass().add("questlog-progress");return b;}private Region space(){Region r=new Region();HBox.setHgrow(r,Priority.ALWAYS);return r;}private ColumnConstraints col(){ColumnConstraints c=new ColumnConstraints();c.setPercentWidth(50);return c;}private ToggleButton chip(String t,ToggleGroup g){ToggleButton b=new ToggleButton(t);b.setToggleGroup(g);b.getStyleClass().add("filter-chip");return b;}
    private record Quest(String name,String detail,double progress,String status,String icon){}
}
