package tgm.geyerritter.dezsys06;

import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Broker {

    public static final Logger logger = LogManager.getLogger(Broker.class);

    private static BrokerService broker;

    public static void main(String[] args) throws Exception {

        logger.info("Starting broker on port 61616 ...");

        broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        broker.start();

        if (broker.waitUntilStarted())
            logger.info("Broker successfully started on port 61616");
        else
            logger.error("Failed to start broker");
    }

    public static void stopBroker() throws Exception {
        broker.stop();
        broker.waitUntilStopped();

        logger.info("Broker successfully stopped");
    }

}
