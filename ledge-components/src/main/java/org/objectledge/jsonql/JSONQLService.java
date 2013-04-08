package org.objectledge.jsonql;

import com.fasterxml.jackson.databind.JsonNode;

public interface JSONQLService
{
    void checkValue(String valueExpr)
        throws JSONQLParseException;

    void checkPredicate(String predicateExpr)
        throws JSONQLParseException;

    EvaluationContext contextOf(JsonNode node);

    JsonNode evaluate(String valueExpr, EvaluationContext context)
        throws JSONQLParseException;

    boolean satisfies(String predicateExpr, EvaluationContext context)
        throws JSONQLParseException;
}
