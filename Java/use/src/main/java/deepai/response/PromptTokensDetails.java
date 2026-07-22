package deepai.response;

public class PromptTokensDetails {
    private int cachedTokens = 0;

    // Getters and Setters
    public int getCachedTokens() {
        return cachedTokens;
    }

    public void setCachedTokens(int cachedTokens) {
        this.cachedTokens = cachedTokens;
    }
}
