/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package k12.revere.frc.dashboard;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import k12.revere.frc.dashboard.util.QWordPacker;
import k12.revere.frc.dashboard.util.javafx.control.BarMeter;
import k12.revere.frc.dashboard.util.javafx.control.DigitalLabel;
import k12.revere.frc.dashboard.util.javafx.control.TriangleLine;

import static javafx.geometry.Pos.*;

/**
 *
 * @author Vince
 */
public class RevereDashboard extends Application {

    public static final Logger logger = Logger.getLogger("dashboard");

    private NetworkTable table;
    private NetworkTableListener tableListener;
    private boolean hasConnectedAtLeastOnce;

    private BorderPane rootPane;
    private Stage stage;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) {
        hasConnectedAtLeastOnce = false;
        stage = primaryStage;
        rootPane = new BorderPane();
        scene = new Scene(rootPane, 500, 300);
        Font.loadFont(RevereDashboard.class.getResourceAsStream("k12.revere.frc.dashboard.assets.font.digital.ttf"), 12);
        Font.loadFont(RevereDashboard.class.getResourceAsStream("k12.revere.frc.dashboard.assets.font.seguisb.ttf"), 12);
        scene.getStylesheets().add("/k12/revere/frc/dashboard/assets/style.css");

//        Font.l
        stage.setScene(scene);
        stage.setTitle("Revere Dashboard");
        stage.show();

        displayAwaitConnection();

        Thread t = new Thread(() -> {
            NetworkTable.setClientMode();
            logger.info("Entered client mode.");
            String host = "localhost";  //"10.52.49.2";
            NetworkTable.setIPAddress(host);    //  IP Address OF THE ROBOT
            logger.info("Robot IP address is " + host);
            table = NetworkTable.getTable("SmartDashboard");
            logger.info("Got NetworkTable \"SmartDashboard\"");
            tableListener = new NetworkTableListener(this);
            //  When the connection listener is added it will immediately invoke connected() or disconnected() on the listener.
            table.addConnectionListener(tableListener, true);
        }, "NetThread");
        t.setDaemon(true);
        t.start();
    }

    public void displayAwaitConnection() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::displayAwaitConnection);
            return;
        }
        displaySpinny("Awaiting connection...");
    }

    private void displaySpinny(String s) {
        Label label = new Label(s);
        label.getStyleClass().add("heading");
        ProgressIndicator indicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
        VBox vbox = new VBox(8D, label, indicator);
        vbox.setAlignment(Pos.CENTER);
        rootPane.setCenter(vbox);
    }

    public void onLostConnection() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::onLostConnection);
            return;
        }
        //  Unregister value listener
        table.removeTableListener(tableListener);
        tableListener.clearNumberListeners();

        ProgressIndicator indicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
        indicator.setPrefSize(20, 20);
        Label label = new Label("Connection lost. Awaiting connection...", indicator);
        label.setAlignment(CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPadding(new Insets(2));
        rootPane.setTop(label);
    }

    public void onConnect() {
        hasConnectedAtLeastOnce = true;
        //  Perform layout
        rootPane.getChildren().clear();
        final GridPane layoutPane = new GridPane();
        layoutPane.setAlignment(TOP_LEFT);
        rootPane.setCenter(layoutPane);

        //  Robot General Info
        final Label versionLbl = new Label("Waiting for version...");
        final Label loggingLevelLbl = new Label("Waiting for logging level...");
        final Label debugIntervalLbl = new Label("Waiting for debug interval...");
        VBox robotInfoBox = new VBox(versionLbl, loggingLevelLbl, debugIntervalLbl);
        robotInfoBox.setAlignment(TOP_LEFT);
        robotInfoBox.setPadding(new Insets(4));
        layoutPane.add(robotInfoBox, 0, 0, 1, 2);

        //  LEFT MOTOR
        VBox leftMotorBox = new VBox(-16);
        leftMotorBox.setAlignment(CENTER);
        final BarMeter leftMotorMeter = new BarMeter(20, 100, 4D, -100D, 100D, 0D, true).
                title("Left Motor").
                displayValue(false);
        final DigitalLabel leftMotorValueLabel = new DigitalLabel();
        leftMotorValueLabel.setMinPlaces(3);
        leftMotorValueLabel.setValue(0);
        leftMotorValueLabel.addStyleClassToBoth("motor-val");
        leftMotorBox.getChildren().addAll(leftMotorMeter, leftMotorValueLabel);
        //  RIGHT MOTOR
        VBox rightMotorBox = new VBox(-16);
        rightMotorBox.setAlignment(CENTER);
        final BarMeter rightMotorMeter = new BarMeter(20, 100, 4D, -100D, 100D, 0D, true).
                title("Right Motor").
                displayValue(false);
        final DigitalLabel rightMotorValueLabel = new DigitalLabel();
        rightMotorValueLabel.setMinPlaces(3);
        rightMotorValueLabel.setValue(0);
        rightMotorValueLabel.addStyleClassToBoth("motor-val");
        rightMotorBox.getChildren().addAll(rightMotorMeter, rightMotorValueLabel);
        HBox motorBox = new HBox(8, leftMotorBox, rightMotorBox);
        layoutPane.add(motorBox, 0, 2, 1, 3);
        
        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setMaxHeight(Double.MAX_VALUE);
        separator.setPadding(new Insets(0, 20, 0, 20));
        layoutPane.add(separator, 1, 0, 1, 6);
        
        //  Winch Motor
        VBox winchMotorBox = new VBox(-16);
        winchMotorBox.setAlignment(CENTER);
        final BarMeter winchMotorMeter = new BarMeter(100, 20, 4D, -100D, 100D, 0D, false).
                title("Winch Motor").
                displayValue(false);
        final DigitalLabel winchMotorValueLabel = new DigitalLabel();
        winchMotorValueLabel.setMinPlaces(3);
        winchMotorValueLabel.setValue(0);
        winchMotorValueLabel.addStyleClassToBoth("motor-val");
        winchMotorValueLabel.setTranslateX(-70);
        final Rectangle highMotorSwitchRect = new Rectangle(14, 14, Color.GREEN);
        Label highMotorSwitchLabel = new Label("HIGH");
        VBox highMotorSwitchBox = new VBox(0, highMotorSwitchRect, highMotorSwitchLabel);
        highMotorSwitchBox.setAlignment(CENTER);
        final Rectangle lowMotorSwitchRect = new Rectangle(14, 14, Color.GREEN);
        Label lowMotorSwitchLabel = new Label("LOW");
        VBox lowMotorSwitchBox = new VBox(0, lowMotorSwitchRect, lowMotorSwitchLabel);
        lowMotorSwitchBox.setAlignment(CENTER);
        HBox switchBox = new HBox(110, lowMotorSwitchBox, highMotorSwitchBox);
        switchBox.setAlignment(CENTER);
        switchBox.setMaxWidth(Double.MAX_VALUE);
        switchBox.setTranslateY(-52);
        winchMotorBox.getChildren().addAll(winchMotorMeter, winchMotorValueLabel, switchBox);
        layoutPane.add(winchMotorBox, 2, 0);
        
        separator = new Separator(Orientation.HORIZONTAL);
        separator.setMaxWidth(Double.MAX_VALUE);
        separator.setPadding(new Insets(10, 0, 10, 0));
        layoutPane.add(separator, 2, 1);
        
        //  INPUT
        Label throttleLbl = new Label("Throttle");
        TriangleLine throttleLine = new TriangleLine(100, -100, 100, 0);
        VBox throttleBox = new VBox(2, throttleLbl, throttleLine);
        throttleBox.setAlignment(CENTER);
        Label turnLbl = new Label("Turn");
        TriangleLine turnLine = new TriangleLine(100, -100, 100, 0);
        VBox turnBox = new VBox(2, turnLbl, turnLine);
        turnBox.setAlignment(CENTER);
        HBox motionInputBox = new HBox(10, throttleBox, turnBox);
        motionInputBox.setAlignment(Pos.TOP_CENTER);
        layoutPane.add(motionInputBox, 2, 2);
        

        //  Register our listeners
        tableListener.registerStringListener("vers", (s) -> versionLbl.setText("Version " + s));
        tableListener.registerStringListener("log", (s) -> loggingLevelLbl.setText("Logging level: " + s));
        tableListener.registerNumberListener("debug", (d) -> debugIntervalLbl.setText(String.format("Debug interval: %sms", (int) d)));
        //  DriveSystem
        tableListener.registerNumberListener("tmv", (d) -> {
            long tmv = QWordPacker.d2l(d);
            double leftMotorVal = QWordPacker.unpackLOctoFloat255(tmv, 0) * 100D;
            double rightMotorVal = QWordPacker.unpackLOctoFloat255(tmv, 1) * 100D;
            leftMotorMeter.value(leftMotorVal);
            leftMotorValueLabel.setValue((int)leftMotorVal);
            rightMotorMeter.value(rightMotorVal);
            rightMotorValueLabel.setValue((int)rightMotorVal);
        });
         tableListener.registerNumberListener("wn", (d) -> {
            long wn = QWordPacker.d2l(d);
            boolean hiSwitch = QWordPacker.getBitFlag(wn, 63);
            boolean loSwitch = QWordPacker.getBitFlag(wn, 62);
            double winchMotorVal = QWordPacker.unpackLOctoFloat255(wn, 0) * 100D;
            lowMotorSwitchRect.setFill(loSwitch ? Color.RED : Color.GREEN);
            highMotorSwitchRect.setFill(hiSwitch ? Color.RED : Color.GREEN);
            winchMotorMeter.value(winchMotorVal);
            winchMotorValueLabel.setValue((int)winchMotorVal);
         });
        //  Register value listener and accept values
        table.addTableListener(tableListener, true);
    }

    public boolean hasConnectedAtLeastOnce() {
        return hasConnectedAtLeastOnce;
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application. main() serves only as fallback in case the application can not be launched through deployment artifacts, e.g., in IDEs with limited FX support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
