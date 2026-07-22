package deepai.request;

import java.util.ArrayList;
import java.util.List;

public class ChatRequest {
	private String model = "deepseek-chat";
	private List<Message> messages = new ArrayList<>();
	private double frequencyPenalty = 0;
	private int maxTokens = 1;
	private double presencePenalty = 0;
	private ResponseFormat responseFormat = new ResponseFormat();
	private Object stop = null;
	private boolean stream = false;
	private Object streamOptions = null;
	private double temperature = 1;
	private double topP = 1;
	private Object tools = null;
	private String toolChoice = "none";
	private boolean logprobs = false;
	private Object topLogprobs = null;

	// Getters and Setters
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public double getFrequencyPenalty() {
		return frequencyPenalty;
	}

	public void setFrequencyPenalty(double frequencyPenalty) {
		this.frequencyPenalty = frequencyPenalty;
	}

	public int getMaxTokens() {
		return maxTokens;
	}

	public void setMaxTokens(int maxTokens) {
		this.maxTokens = maxTokens;
	}

	public double getPresencePenalty() {
		return presencePenalty;
	}

	public void setPresencePenalty(double presencePenalty) {
		this.presencePenalty = presencePenalty;
	}

	public ResponseFormat getResponseFormat() {
		return responseFormat;
	}

	public void setResponseFormat(ResponseFormat responseFormat) {
		this.responseFormat = responseFormat;
	}

	public Object getStop() {
		return stop;
	}

	public void setStop(Object stop) {
		this.stop = stop;
	}

	public boolean isStream() {
		return stream;
	}

	public void setStream(boolean stream) {
		this.stream = stream;
	}

	public Object getStreamOptions() {
		return streamOptions;
	}

	public void setStreamOptions(Object streamOptions) {
		this.streamOptions = streamOptions;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getTopP() {
		return topP;
	}

	public void setTopP(double topP) {
		this.topP = topP;
	}

	public Object getTools() {
		return tools;
	}

	public void setTools(Object tools) {
		this.tools = tools;
	}

	public String getToolChoice() {
		return toolChoice;
	}

	public void setToolChoice(String toolChoice) {
		this.toolChoice = toolChoice;
	}

	public boolean isLogprobs() {
		return logprobs;
	}

	public void setLogprobs(boolean logprobs) {
		this.logprobs = logprobs;
	}

	public Object getTopLogprobs() {
		return topLogprobs;
	}

	public void setTopLogprobs(Object topLogprobs) {
		this.topLogprobs = topLogprobs;
	}
}
