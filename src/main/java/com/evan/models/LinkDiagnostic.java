package com.evan.models;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class LinkDiagnostic {
    final String url;
    Optional<Exception> error = Optional.empty();
    Set<String> sourceUrls = new HashSet<>();

    public LinkDiagnostic(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setError(Exception e) {
        error = Optional.of(e);
    }

    public Optional<Exception> getError() {
        return error;
    }

    public Set<String> getSourceUrls() {
        return sourceUrls;
    }

    public void addSourceUrl(String url) {
        sourceUrls.add(url);
    }
}