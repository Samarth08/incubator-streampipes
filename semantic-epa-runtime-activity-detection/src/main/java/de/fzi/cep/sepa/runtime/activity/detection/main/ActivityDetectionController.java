package de.fzi.cep.sepa.runtime.activity.detection.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.StatusLine;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.gson.Gson;

import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.builder.EpRequirements;
import de.fzi.cep.sepa.model.builder.StreamBuilder;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.runtime.activity.detection.utils.AkerVariables;
import de.fzi.cep.sepa.runtime.activity.detection.utils.EnrichedUtils;
import de.fzi.cep.sepa.runtime.activity.detection.utils.ProaSenseSettings;
import de.fzi.cep.sepa.runtime.activity.detection.utils.Utils;

public class ActivityDetectionController implements SemanticEventProcessingAgentDeclarer {
	public static String STREAMSTORY_URL = "http://streamstory.de/";
	// private static String STREAMSTORY_URL = "http://localhost:18089/";

	@Override
	public SepaDescription declareModel() {
		SepaDescription desc = new SepaDescription("sepa/activitydetection", "ActivityDetection",
				"ActivityDetection description");

		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(AkerVariables.Enriched.topic()));
		grounding
				.setTransportFormats(de.fzi.cep.sepa.commons.Utils.createList(new TransportFormat(MessageFormat.Json)));

		EventStream stream = EnrichedUtils.getEnrichedStream();

		stream.setEventGrounding(grounding);
		desc.addEventStream(stream);

		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(Utils.getActivityDetection());
		desc.setOutputStrategies(strategies);

		return desc;
	}

	protected String getInputTopic(InvocableSEPAElement graph) {
		return graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();
	}

	protected String getOutputTopic(SepaInvocation graph) {
		return graph.getOutputStream().getEventGrounding().getTransportProtocol().getTopicName();
	}

	@Override
	public Response invokeRuntime(SepaInvocation invocationGraph) {
		// TODO make modelId dynamic
		int modelId = 1;

		String pipelineId = invocationGraph.getCorrespondingPipeline();
		String errorMessage = "";
		String inputTopic = getInputTopic(invocationGraph);
		String outputTopic = getOutputTopic(invocationGraph);

		ModelInvocationRequestParameters params = Utils.getModelInvocationRequestParameters(pipelineId, modelId,
				inputTopic, outputTopic);
		JsonObject payload = Utils.getModelInvocationMessage(params);

		try {
			org.apache.http.client.fluent.Response res = Request.Post(STREAMSTORY_URL + "/invoke").useExpectContinue()
					.version(HttpVersion.HTTP_1_1).bodyString(payload.toString(), ContentType.APPLICATION_JSON)
					.execute();

			return handleResponse(res, pipelineId);
		} catch (ClientProtocolException e) {
			errorMessage = e.toString();
			e.printStackTrace();
		} catch (IOException e) {
			errorMessage = e.toString();
			e.printStackTrace();
		}

		return new Response(pipelineId, false, errorMessage);
	}

	@Override
	public Response detachRuntime(String pipelineId) {
		// TODO make modelId dynamic
		int modelId = 1;
		String errorMessage = "";

		JsonObject params = Utils.getModelDetachMessage(pipelineId, modelId);

		try {
			org.apache.http.client.fluent.Response res = Request.Post(STREAMSTORY_URL + "/detach").useExpectContinue()
					.version(HttpVersion.HTTP_1_1).bodyString(params.toString(), ContentType.APPLICATION_JSON)
					.execute();
			return handleResponse(res, pipelineId);
		} catch (ClientProtocolException e) {
			errorMessage = e.toString();
			e.printStackTrace();
		} catch (IOException e) {
			errorMessage = e.toString();
			e.printStackTrace();
		}

		return new Response(pipelineId, false, errorMessage);
	}

	private Response handleResponse(org.apache.http.client.fluent.Response response, String elementId) {
		String errorMessage = "";
		try {
			HttpResponse resp = response.returnResponse();
			if (200 == resp.getStatusLine().getStatusCode()) {
				return new Response(elementId, true);
			} else {
				return new Response(elementId, false,
						"There is a problem with Service Stream Story!\n" + resp.getStatusLine());
			}
		} catch (ClientProtocolException e) {
			errorMessage = e.toString();
			e.printStackTrace();
		} catch (IOException e) {
			errorMessage = e.toString();
			e.printStackTrace();
		}

		return new Response(elementId, false, errorMessage);
	}

}
