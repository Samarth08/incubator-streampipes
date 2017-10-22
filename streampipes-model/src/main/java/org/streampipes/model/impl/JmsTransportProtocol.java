package org.streampipes.model.impl;

import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import javax.persistence.Entity;

@RdfsClass("sepa:JmsTransportProtocol")
@Entity
public class JmsTransportProtocol extends TransportProtocol{

	private static final long serialVersionUID = -5650426611208789835L;
	
	@RdfProperty("sepa:jmsPort")
	private int port;
	
	public JmsTransportProtocol(String uri, int port, String topicName)
	{
		super(uri, topicName);
		this.port = port;
	}
	
	public JmsTransportProtocol(JmsTransportProtocol other)
	{
		super(other);
		this.port = other.getPort();
	}
	
	public JmsTransportProtocol() 
	{
		super();
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return getBrokerHostname() + ":" + getPort();
	}
}
