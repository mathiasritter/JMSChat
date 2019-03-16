package tgm.geyerritter.dezsys06;

import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Broker {

    public static final Logger logger = LogManager.getLogger(Broker.class);

    private static BrokerService broker;

    public static void main(String[] args) {
        if (!start())
            throw new IllegalStateException("Broker not started");
    }

    public static boolean start() {

        logger.info("Starting broker on port 61616 ...");

        broker = new BrokerService();

        try {
            broker.addConnector("tcp://localhost:61616");
            broker.start();
            if (broker.waitUntilStarted()) {
                logger.info("Broker successfully started on port 61616");
                return true;
            }
            else {
                logger.error("Failed to start broker");
                return false;
            }

        } catch (Exception e) {
            logger.error("Failed to start broker");
            logger.error(e.getMessage());
            return false;
        }

    }

    public static void stop() {

        if (broker != null) {

            try {
                broker.stop();
                broker.waitUntilStopped();
            } catch (Exception e) {
                logger.error("Failed to stop broker");
                e.printStackTrace();
            }

            logger.info("Broker successfully stopped");
        } else {

            logger.error("Cannot stop broker - broker is not running");
        }

    }

}
