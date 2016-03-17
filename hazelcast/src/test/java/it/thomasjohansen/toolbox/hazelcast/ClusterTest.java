package it.thomasjohansen.toolbox.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import it.thomasjohansen.toolbox.socket.AvailablePort;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author thomas@thomasjohansen.it
 */
public class ClusterTest {

    @Test
    public void whenBuildingThreeInstancesThenTheyWillJoinACluster() {
        int port1 = AvailablePort.find();
        int port2 = AvailablePort.find();
        int port3 = AvailablePort.find();
        HazelcastInstance node1 = new HazelcastBuilder()
                .port(port1)
                .member("localhost:" + port2)
                .member("localhost:" + port3)
                .build();
        HazelcastInstance node2 = new HazelcastBuilder()
                .port(port2)
                .member("localhost:" + port1)
                .member("localhost:" + port3)
                .build();
        HazelcastInstance node3 = new HazelcastBuilder()
                .port(port3)
                .member("localhost:" + port1)
                .member("localhost:" + port2)
                .build();
        assertEquals(3, node1.getCluster().getMembers().size());
        assertEquals(3, node2.getCluster().getMembers().size());
        assertEquals(3, node3.getCluster().getMembers().size());
    }



}
