package home.entity;

import java.io.Serializable;

public class Word implements Serializable {
    private final String text;
    private final String uri;
    private final String errorMessage;

    public Word(String text, String uri, String errorMessage) {
        this.text = text;
        this.uri = uri;
        this.errorMessage = errorMessage;
    }

    public Word(String text, String uri) {
        this(text, uri, null);
    }

    public String getText() {
        return text;
    }

    public String getUri() {
        return uri;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
