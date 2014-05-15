package k12.revere.frc.dashboard.util.javafx.control;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Vince
 */
public class DigitalLabel extends StackPane {

    public final Label backgroundLbl;
    public final Label foregroundLbl;
    private final Label negativeSign;
    private int minPlaces;
    
    public DigitalLabel() {
        backgroundLbl = new Label();
        backgroundLbl.getStyleClass().addAll("digital", "digital-num-bg");
        backgroundLbl.setTextAlignment(TextAlignment.RIGHT);
        foregroundLbl = new Label();
        foregroundLbl.getStyleClass().addAll("digital");
        foregroundLbl.setTextAlignment(TextAlignment.RIGHT);
        negativeSign = new Label();
        negativeSign.getStyleClass().addAll("digital");
        negativeSign.setTextAlignment(TextAlignment.LEFT);
        negativeSign.prefWidthProperty().bind(backgroundLbl.widthProperty());
        getChildren().addAll(backgroundLbl, foregroundLbl, negativeSign);
        setAlignment(Pos.CENTER_RIGHT);
        minPlaces = 1;
    }
    
    public void setValue(int i) {
        String s = Integer.toString(i);
        int numTerms = s.length();
        if(i < 0) {
            numTerms--;
        }
        if(numTerms < minPlaces) {
            numTerms = minPlaces;
        }
        StringBuilder builder = new StringBuilder().append('-');
        for(int c = 0; c < numTerms; c++) {
            builder.append('8');
        }
        backgroundLbl.setText(builder.toString());
        foregroundLbl.setText(Integer.toString(Math.abs(i)));
        if(i < 0) {
            negativeSign.setText("-");
        } else {
            negativeSign.setText("");
        }
    }
    
    public void addStyleClassToBoth(String... styleclasses) {
        backgroundLbl.getStyleClass().addAll(styleclasses);
        foregroundLbl.getStyleClass().addAll(styleclasses);
        negativeSign.getStyleClass().addAll(styleclasses);
    }
    
    public void setMinPlaces(int i) {
        minPlaces = i;
    }
}
