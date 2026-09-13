package hobbytracker;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.scene.Parent;
/** In-memory theme state; persistence belongs to a later backend phase. */
public final class ThemeManager {
    /* One UI-only theme setting shared by the desktop shell and mobile preview. */
    private static boolean dark = true;
    public boolean isDark() { return dark; }
    public void setDark(boolean value, Parent root) {
        dark = value;
        apply(root);
    }
    /** Applies the current shared state to a newly-created UI root. */
    public void apply(Parent root) {
        Application.setUserAgentStylesheet(dark ? new PrimerDark().getUserAgentStylesheet() : new PrimerLight().getUserAgentStylesheet());
        root.getStyleClass().remove("theme-light");
        if (!dark) root.getStyleClass().add("theme-light");
    }
}
