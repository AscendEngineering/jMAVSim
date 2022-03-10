package me.drton.jmavsim;

import me.drton.jmavlib.mavlink.MAVLinkMessage;
import me.drton.jmavlib.mavlink.MAVLinkSchema;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * MAVLinkNode is generic object that can handle and send MAVLink messages, but may have no own ID, i.e. it can be e.g.
 * bridge between physical port and virtual MAVLinkConnection.
 * <p/>
 * User: ton Date: 13.02.14 Time: 21:51
 */
public abstract class MAVLinkNode {
    protected MAVLinkSchema schema;
    private List<MAVLinkConnection> connections = new ArrayList<MAVLinkConnection>();

    protected MAVLinkNode(MAVLinkSchema schema) {
        this.schema = schema;
    }

    public void addConnection(MAVLinkConnection connection) {
        connections.add(connection);
    }

    protected void sendMessage(MAVLinkMessage msg) {
        //System.out.println(connections);
        for (MAVLinkConnection connection : connections) {
            // Iterate through nodes to check if serial/UDP
            for(MAVLinkNode node : connection.nodes){
                if(node.toString().contains("Serial")){
                    if("HIL_SENSOR".equals(msg.getMsgName()) || "HIL_GPS".equals(msg.getMsgName())){
                        connection.sendMessage(this, msg);
			            break;
                    }
                } else if(node.toString().contains("UDP")) {
                    if(!"HIL_SENSOR".equals(msg.getMsgName()) && !"HIL_GPS".equals(msg.getMsgName())){
                        connection.sendMessage(this, msg);
                        break;
                    }
                }
            }
        }
    }

    public abstract void handleMessage(MAVLinkMessage msg);

    public abstract void update(long t, boolean paused);
}
