package com.intellij.lang.jsgraphql.types.language;

import com.intellij.lang.jsgraphql.types.PublicApi;

import java.io.Serializable;

@PublicApi
public class Comment implements Serializable {
    public final String content;
    public final SourceLocation sourceLocation;

    public Comment(String content, SourceLocation sourceLocation) {
        this.content = content;
        this.sourceLocation = sourceLocation;
    }

    public String getContent() {
        return content;
    }

    public SourceLocation getSourceLocation() {
        return sourceLocation;
    }
}
