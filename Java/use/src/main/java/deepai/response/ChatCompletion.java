package deepai.response;

import java.util.List;

public class ChatCompletion {
    private String id = "0e2c11df-5c98-4132-909a-ae393890b11b";
    private String object = "chat.completion";
    private long created = 1741848070;
    private String model = "deepseek-chat";
    private List<Choice> choices;
    private Usage usage;
    private String systemFingerprint = "fp_3a5770e1b4_prod0225";

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public String getSystemFingerprint() {
        return systemFingerprint;
    }

    public void setSystemFingerprint(String systemFingerprint) {
        this.systemFingerprint = systemFingerprint;
    }
}
