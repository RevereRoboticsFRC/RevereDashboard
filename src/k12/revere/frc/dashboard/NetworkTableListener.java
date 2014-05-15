package k12.revere.frc.dashboard;

import edu.wpi.first.wpilibj.tables.IRemote;
import edu.wpi.first.wpilibj.tables.IRemoteConnectionListener;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import javafx.application.Platform;

import static k12.revere.frc.dashboard.RevereDashboard.logger;

/**
 *
 * @author Vince
 */
public class NetworkTableListener implements IRemoteConnectionListener, ITableListener {

    private final RevereDashboard dashboard;
    private final Map<String, DoubleConsumer> numberListeners;
    private final Map<String, Consumer<String>> stringListeners;

    public NetworkTableListener(RevereDashboard d) {
        dashboard = d;
        numberListeners = new HashMap<>();
        stringListeners = new HashMap<>();
    }

    @Override
    public void connected(IRemote remote) {
        logger.info("Client connected.");
        Platform.runLater(dashboard::onConnect);
    }

    @Override
    public void disconnected(IRemote remote) {
        if (dashboard.hasConnectedAtLeastOnce()) {
            logger.info("Connection lost.");
            Platform.runLater(dashboard::onLostConnection);
        } else {
            logger.info("Awaiting connection...");
            Platform.runLater(dashboard::displayAwaitConnection);
        }
    }

    @Override
    public void valueChanged(ITable source, String key, Object value, boolean isNew) {
        if(value instanceof Double) {
            DoubleConsumer consumer = numberListeners.get(key);
            if(consumer != null) {
                Platform.runLater(()->consumer.accept((Double)value));
            }
        } else if(value instanceof String) {
            Consumer<String> consumer = stringListeners.get(key);
            if(consumer != null) {
                Platform.runLater(()->consumer.accept((String)value));
            }
        } else {
            //  TODO
        }
    }

    public void registerNumberListener(String key, DoubleConsumer consumer) {
        numberListeners.put(key, consumer);
    }
    
    public void unregisterNumberListener(String key) {
        numberListeners.remove(key);
    }
    
    public void clearNumberListeners() {
        numberListeners.clear();
    }
    
    public void registerStringListener(String key, Consumer<String> consumer) {
        stringListeners.put(key, consumer);
    }
    
    public void unregisterStringListener(String key) {
        stringListeners.remove(key);
    }
    
    public void clearStringListeners() {
        stringListeners.clear();
    }
    
}
