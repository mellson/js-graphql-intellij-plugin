package com.intellij.lang.jsgraphql.types.language;

import com.google.common.collect.ImmutableList;
import com.intellij.lang.jsgraphql.types.PublicApi;

import java.io.Serializable;
import java.util.List;

import static com.intellij.lang.jsgraphql.types.collect.ImmutableKit.emptyList;

@PublicApi
public class IgnoredChars implements Serializable {

    private final ImmutableList<IgnoredChar> left;
    private final ImmutableList<IgnoredChar> right;

    public static final IgnoredChars EMPTY = new IgnoredChars(emptyList(), emptyList());

    public IgnoredChars(List<IgnoredChar> left, List<IgnoredChar> right) {
        this.left = ImmutableList.copyOf(left);
        this.right = ImmutableList.copyOf(right);
    }


    public List<IgnoredChar> getLeft() {
        return left;
    }

    public List<IgnoredChar> getRight() {
        return right;
    }
}
