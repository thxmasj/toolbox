package it.thomasjohansen.toolbox.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastBuilder {

    private Config config;

    public HazelcastBuilder() {
        config = new Config();
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
    }

    public HazelcastBuilder member(String member) {
        config.getNetworkConfig().getJoin().getTcpIpConfig().addMember(member);
        return this;
    }

    public HazelcastBuilder port(int port) {
        config.getNetworkConfig().setPort(port);
        return this;
    }

    public HazelcastInstance build() {
        if (config == null)
            throw new IllegalStateException("Already built");
        try {
            return Hazelcast.newHazelcastInstance(config);
        } finally {
            config = null;
        }
    }

}
