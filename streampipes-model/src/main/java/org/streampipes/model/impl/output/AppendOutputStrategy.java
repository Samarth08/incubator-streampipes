package org.streampipes.model.impl.output;

import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.util.Cloner;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass("sepa:AppendOutputStrategy")
@Entity
public class AppendOutputStrategy extends OutputStrategy {

	private static final long serialVersionUID = 7202888911899551012L;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:appendsProperty")
	List<EventProperty> eventProperties;
	
	public AppendOutputStrategy()
	{
		super();
		eventProperties = new ArrayList<>();
	}
	
	public AppendOutputStrategy(AppendOutputStrategy other)
	{
		super(other);
		this.setEventProperties(new Cloner().properties(other.getEventProperties()));
	}
	
	public AppendOutputStrategy(List<EventProperty> eventProperties) {
		super();
		this.eventProperties = eventProperties;
	}

	public List<EventProperty> getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(List<EventProperty> eventProperties) {
		this.eventProperties = eventProperties;
	}
	
	
}
