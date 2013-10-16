package org.objectledge.jsonql;

import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

public interface EvaluationContext
{
    JsonNode getNode();

    EvaluationContext getField(String field);

    EvaluationContext getMissing();

    EvaluationContext getElement(int index);

    String getValue();

    void addError(String error);

    Set<String> getErrors();
}
