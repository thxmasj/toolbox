package it.thomasjohansen.toolbox.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastClientBuilder {

    private ClientConfig config = new ClientConfig();

    public HazelcastClientBuilder address(String address) {
        config.getNetworkConfig().addAddress(address);
        return this;
    }

    public HazelcastClientBuilder connectionAttemptLimit(int connectionAttemptLimit) {
        config.getNetworkConfig().setConnectionAttemptLimit(connectionAttemptLimit);
        return this;
    }

    public HazelcastInstance build() {
        if (config == null)
            throw new IllegalStateException("Already built");
        try {
            return HazelcastClient.newHazelcastClient(config);
        } finally {
            config = null;
        }
    }

}
