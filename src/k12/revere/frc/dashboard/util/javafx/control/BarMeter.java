package k12.revere.frc.dashboard.util.javafx.control;

import java.text.NumberFormat;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import k12.revere.frc.dashboard.util.MathUtil;

import static java.text.NumberFormat.getIntegerInstance;

/**
 *
 * @author Vince
 */
public class BarMeter extends BorderPane {

    private final double width;
    private final double height;
    private final double maxVal;
    private final double minVal;
    private final double spacing;
    private DoubleProperty value;

    private final Canvas canvas;
    private final Label titleLbl;
    private final Label valueLbl;
    private NumberFormat numFormat;
    private boolean isVertical;
    private boolean displayValue;

    public BarMeter(double w, double h, double sp, double min, double max, double initVal, boolean vert) {
        width = w;
        height = h;
        spacing = sp;
        maxVal = max;
        minVal = min;
        value = new SimpleDoubleProperty(initVal);
        numFormat = getIntegerInstance();
        titleLbl = new Label();
        valueLbl = new Label();
        isVertical = vert;
        displayValue = true;
        canvas = new Canvas(w, h);
        this.setCenter(canvas);
        this.setBottom(valueLbl);
        this.setTop(titleLbl);
        value.addListener((ov, t, t1) -> update());
        update();
        BorderPane.setAlignment(this.getTop(), Pos.CENTER);
        BorderPane.setAlignment(this.getCenter(), Pos.CENTER);
        BorderPane.setAlignment(this.getBottom(), Pos.CENTER);
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        if (isVertical) {
            drawVert(gc);
        } else {
            drawHoriz(gc);
        }
    }

    private void drawVert(GraphicsContext gc) {
        //  Draw border
        gc.setFill(Color.TRANSPARENT);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2D);
        gc.strokeRect(0, 0, width, height);
        //  Center
        double halfHeight = height / 2D;
        gc.strokeLine(0, halfHeight, width, halfHeight);
        //  Current value
        gc.setFill(Color.WHITE);
        gc.setLineWidth(0D);
        gc.setStroke(Color.TRANSPARENT);
        double reducedHalfHeight = halfHeight - spacing;
        double valueNorm = (value.doubleValue() - minVal) / (maxVal - minVal) * -2D + 1D;   //  -1.0 to 1.0
        double displacement = reducedHalfHeight * valueNorm;
        if (valueNorm < 0D) {
            double top = spacing + (reducedHalfHeight + displacement);
            gc.fillRect(spacing, top, width - 2D * spacing, halfHeight - top);
        } else {
            gc.fillRect(spacing, halfHeight, width - 2D*spacing, displacement);
        }
    }

    private void drawHoriz(GraphicsContext gc) {
        //  Draw border
        gc.setFill(Color.TRANSPARENT);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2D);
        gc.strokeRect(0, 0, width, height);
        //  Center
        double halfWidth = width / 2D;
        gc.strokeLine(halfWidth, 0, halfWidth, height);
        //  Current value
        gc.setFill(Color.WHITE);
        gc.setLineWidth(0D);
        gc.setStroke(Color.TRANSPARENT);
        double reducedHalfWidth = halfWidth - 2D;
        double valueNorm = (value.doubleValue() - minVal) / (maxVal - minVal) * 2D - 1D;   //  -1.0 to 1.0
        double displacement = reducedHalfWidth * valueNorm;
        if (valueNorm < 0D) {
            double right = spacing + (reducedHalfWidth + displacement);
            gc.fillRect(right, spacing, halfWidth - right, height - 2D * spacing);
        } else {
            gc.fillRect(halfWidth, height, displacement, height - 2D*spacing);
        }
        
    }

    private void update() {
        valueLbl.setText(numFormat.format(value.doubleValue()));
        draw();
    }

    public BarMeter value(double d) {
        value.set(MathUtil.clamp(minVal, maxVal, d));
        return this;
    }

    public BarMeter title(String s) {
        titleLbl.setText(s);
        return this;
    }

    public BarMeter numFormat(NumberFormat numFormat) {
        if (numFormat != null) {
            this.numFormat = numFormat;
        }
        return this;
    }

    public BarMeter displayValue(boolean b) {
        this.displayValue = b;
        valueLbl.setOpacity(b ? 1D : 0D);
        return this;
    }
    
    public Label getTitleLbl() {
        return titleLbl;
    }

    public Label getValueLbl() {
        return valueLbl;
    }

}
