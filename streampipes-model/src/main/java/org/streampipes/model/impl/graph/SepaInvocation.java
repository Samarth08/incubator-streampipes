package org.streampipes.model.impl.graph;

import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;
import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.Cloner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@RdfsClass("sepa:SEPAInvocationGraph")
@Entity
public class SepaInvocation extends InvocableSEPAElement implements Serializable {

	private static final long serialVersionUID = 865870355944824186L;


	@OneToOne (fetch = FetchType.EAGER,
			   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty("sepa:produces")
  EventStream outputStream;
	
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasOutputStrategy")
	List<OutputStrategy> outputStrategies;
	
	String pathName;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:epaType")
	protected List<String> category;
	
	public SepaInvocation(SepaDescription sepa)
	{
		super();
		this.setName(sepa.getName());
		this.setDescription(sepa.getDescription());
		this.setIconUrl(sepa.getIconUrl());
		this.setInputStreams(sepa.getEventStreams());
		this.setSupportedGrounding(sepa.getSupportedGrounding());
		this.setStaticProperties(sepa.getStaticProperties());
		this.setOutputStrategies(sepa.getOutputStrategies());
		this.setBelongsTo(sepa.getElementId().toString());
		this.category = sepa.getCategory();
		this.setStreamRequirements(sepa.getEventStreams());
		//this.setUri(belongsTo +"/" +getElementId());		
	}
	
	public SepaInvocation(SepaInvocation other)
	{
		super(other);
		this.outputStrategies = new Cloner().strategies(other.getOutputStrategies());
		if (other.getOutputStream() != null) this.outputStream =  new Cloner().stream(other.getOutputStream());
		this.pathName = other.getPathName();
		this.category = new Cloner().epaTypes(other.getCategory());
	}

	public SepaInvocation(SepaDescription sepa, String domId)
	{
		this(sepa);
		this.DOM = domId;
	}
	
	public SepaInvocation()
	{
		super();
		inputStreams = new ArrayList<EventStream>();
	}
	
	public SepaInvocation(String uri, String name, String description, String iconUrl, String pathName, List<EventStream> eventStreams, List<StaticProperty> staticProperties)
	{
		super(uri, name, description, iconUrl);
		this.pathName = pathName;
		this.inputStreams = eventStreams;
		this.staticProperties = staticProperties;
	}
	
	public SepaInvocation(String uri, String name, String description, String iconUrl, String pathName)
	{
		super(uri, name, description, iconUrl);
		this.pathName = pathName;
		inputStreams = new ArrayList<EventStream>();
		staticProperties = new ArrayList<StaticProperty>();
	}
	
	public boolean addInputStream(EventStream eventStream)
	{
		return inputStreams.add(eventStream);
	}
	
	

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public EventStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(EventStream outputStream) {
		this.outputStream = outputStream;
	}

	public List<OutputStrategy> getOutputStrategies() {
		return outputStrategies;
	}

	public void setOutputStrategies(List<OutputStrategy> outputStrategies) {
		this.outputStrategies = outputStrategies;
	}

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}
	
}
