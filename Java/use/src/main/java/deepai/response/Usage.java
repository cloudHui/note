package deepai.response;

public class Usage {
    private int promptTokens = 9;
    private int completionTokens = 11;
    private int totalTokens = 20;
    private PromptTokensDetails promptTokensDetails = new PromptTokensDetails();
    private int promptCacheHitTokens = 0;
    private int promptCacheMissTokens = 9;

    // Getters and Setters
    public int getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(int promptTokens) {
        this.promptTokens = promptTokens;
    }

    public int getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(int completionTokens) {
        this.completionTokens = completionTokens;
    }

    public int getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(int totalTokens) {
        this.totalTokens = totalTokens;
    }

    public PromptTokensDetails getPromptTokensDetails() {
        return promptTokensDetails;
    }

    public void setPromptTokensDetails(PromptTokensDetails promptTokensDetails) {
        this.promptTokensDetails = promptTokensDetails;
    }

    public int getPromptCacheHitTokens() {
        return promptCacheHitTokens;
    }

    public void setPromptCacheHitTokens(int promptCacheHitTokens) {
        this.promptCacheHitTokens = promptCacheHitTokens;
    }

    public int getPromptCacheMissTokens() {
        return promptCacheMissTokens;
    }

    public void setPromptCacheMissTokens(int promptCacheMissTokens) {
        this.promptCacheMissTokens = promptCacheMissTokens;
    }
}
