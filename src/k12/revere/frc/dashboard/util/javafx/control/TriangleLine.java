package k12.revere.frc.dashboard.util.javafx.control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

/**
 *
 * @author Vince
 */
public class TriangleLine extends VBox {
    
    private Line line;
    private Polygon triangle;
    private final double width, minVal, maxVal;
    private DoubleProperty value;
    
    
    public TriangleLine(double width, double minVal, double maxVal, double initVal) {
        super.setSpacing(-4);
        this.width = width;
        this.minVal = minVal;
        this.maxVal = maxVal;
        value = new SimpleDoubleProperty(0);
        
        line = new Line(-width/2D, 0, width/2D, 0);
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(2);
        triangle = new Polygon(0, -2, 5, 8, -5, 8);
        
        Line vertLine = new Line(0, -4, 0, 4);
        vertLine.setStroke(Color.WHITE);
        vertLine.setStrokeWidth(2);
        
        triangle.setFill(Color.WHITE);
        
        this.setAlignment(Pos.CENTER);
        this.setPrefWidth(width);
        this.getChildren().addAll(new StackPane(line, vertLine), triangle);
        setValue(initVal);
    }

    public void setValue(double value) {
        value = Math.max(minVal, Math.min(maxVal, value));
        this.value.set(value);
        double normalized = (value - minVal)/(maxVal - minVal) * 2D - 1D;
        triangle.setTranslateX(normalized * (width/2D));
    }
    
    
    
    

}
