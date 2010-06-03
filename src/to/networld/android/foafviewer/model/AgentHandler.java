package to.networld.android.foafviewer.model;

import java.net.URL;
import java.util.HashMap;

import android.content.Context;

public abstract class AgentHandler {
	public static final HashMap<String, Agent> agentsByName = new HashMap<String, Agent>();
	
	private AgentHandler() {}
	
	public static Agent initAgent(String _uri, Context _context) throws Exception {
		Agent agent = new Agent(new URL(_uri), _context);
		agentsByName.put(agent.getName(), agent);
		return agent;
	}
	
	public static Agent getAgent(String _name) { 
		return agentsByName.get(_name);
	}
}
