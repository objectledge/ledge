package org.objectledge.jsonql.impl;

import java.io.StringReader;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.jcontainer.dna.Configuration;
import org.objectledge.concurrent.Pool;
import org.objectledge.jsonql.EvaluationContext;
import org.objectledge.jsonql.JSONQLParseException;
import org.objectledge.jsonql.JSONQLService;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class JSONQLServiceImpl
    implements JSONQLService
{
    private static final ExpressionEvaluationVisitor VISITOR = new ExpressionEvaluationVisitor();

    private final Pool<JSONQL> parserPool;

    private final LoadingCache<String, ASTvalue> valueExprCache;

    private final LoadingCache<String, ASTpredicate> predicateExprCache;

    private final Cache<String, ParseException> parseErrorCache;

    public JSONQLServiceImpl()
    {
        this(new Config());
    }

    public JSONQLServiceImpl(Configuration dnaConfig)
    {
        this(new Config(dnaConfig));
    }

    public JSONQLServiceImpl(Config config)
    {
        parserPool = new Pool<JSONQL>(config.parserPoolSize, config.parserPoolTimeout,
                        TimeUnit.SECONDS)
            {
                @Override
                protected JSONQL make()
                {
                    return new JSONQL(new StringReader(""));
                }
            };
        valueExprCache = CacheBuilder.newBuilder().maximumSize(config.valueExprCacheSize)
            .build(new CacheLoader<String, ASTvalue>()
                {
                    @Override
                    public ASTvalue load(String expr)
                        throws Exception
                    {
                        ParseException e = parseErrorCache.getIfPresent(expr);
                        if(e != null)
                        {
                            throw e;
                        }
                        JSONQL parser = parserPool.take();
                        if(parser != null)
                        {
                            try
                            {
                                parser.ReInit(new StringReader(expr));
                                return parser.onlyValue();
                            }
                            catch(ParseException ee)
                            {
                                parseErrorCache.put(expr, ee);
                                throw ee;
                            }
                            finally
                            {
                                parserPool.release(parser);
                            }
                        }
                        else
                        {
                            throw new RuntimeException("parser pool exhausted");
                        }
                    }
                });
        predicateExprCache = CacheBuilder.newBuilder().maximumSize(config.valueExprCacheSize)
            .build(new CacheLoader<String, ASTpredicate>()
                {
                    @Override
                    public ASTpredicate load(String expr)
                        throws Exception
                    {
                        ParseException e = parseErrorCache.getIfPresent(expr);
                        if(e != null)
                        {
                            throw e;
                        }
                        JSONQL parser = parserPool.take();
                        if(parser != null)
                        {
                            try
                            {
                                parser.ReInit(new StringReader(expr));
                                return parser.onlyPredicate();
                            }
                            catch(ParseException ee)
                            {
                                parseErrorCache.put(expr, ee);
                                throw ee;
                            }
                            finally
                            {
                                parserPool.release(parser);
                            }
                        }
                        else
                        {
                            throw new RuntimeException("parser pool exhausted");
                        }
                    }
                });
        parseErrorCache = CacheBuilder.newBuilder().maximumSize(config.parseErrorCacheSize).build();
    }

    public EvaluationContext contextOf(JsonNode node)
    {
        return new EvaluationContextImpl(node);
    }

    private ASTvalue parseValue(String valueExpr)
        throws JSONQLParseException
    {
        try
        {
            return valueExprCache.get(valueExpr);
        }
        catch(ExecutionException e)
        {
            final Throwable cause = e.getCause();
            if(cause instanceof ParseException)
            {
                throw new JSONQLParseException(cause.toString());
            }
            if(cause instanceof RuntimeException)
            {
                throw (RuntimeException)cause;
            }
            else
            {
                throw new RuntimeException("unexpected", cause);
            }
        }
    }

    @Override
    public void checkValue(String valueExpr)
        throws JSONQLParseException
    {
        parseValue(valueExpr);
    }

    @Override
    public JsonNode evaluate(String valueExpr, EvaluationContext context)
        throws JSONQLParseException
    {
        ASTvalue valueAst = parseValue(valueExpr);
        EvaluationContext acc = (EvaluationContext)VISITOR.visit(valueAst, context);
        return acc.getNode();
    }

    private ASTpredicate parsePredicate(String predicateExpr)
        throws JSONQLParseException
    {
        try
        {
            return predicateExprCache.get(predicateExpr);
        }
        catch(ExecutionException e)
        {
            final Throwable cause = e.getCause();
            if(cause instanceof ParseException)
            {
                throw new JSONQLParseException(cause.toString());
            }
            if(cause instanceof RuntimeException)
            {
                throw (RuntimeException)cause;
            }
            else
            {
                throw new RuntimeException("unexpected", cause);
            }
        }
    }

    @Override
    public void checkPredicate(String predicateExpr)
        throws JSONQLParseException
    {
        parsePredicate(predicateExpr);
    }

    @Override
    public boolean satisfies(String predicateExpr, EvaluationContext context)
        throws JSONQLParseException
    {
        ASTpredicate valueAst = parsePredicate(predicateExpr);
        return ((Boolean)VISITOR.visit(valueAst, context)).booleanValue();
    }

    private static class ExpressionEvaluationVisitor
        implements JSONQLVisitor
    {

        @Override
        public Object visit(SimpleNode node, EvaluationContext context)
        {
            throw new RuntimeException("unexpected SimpleNode");
        }

        @Override
        public Object visit(ASTpredicate node, EvaluationContext context)
        {
            return node.children[0].jjtAccept(this, context);
        }

        @Override
        public Object visit(ASTdisjunction node, EvaluationContext context)
        {
            for(Node child : node.children)
            {
                if(((Boolean)child.jjtAccept(this, context)).booleanValue())
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Object visit(ASTconjunction node, EvaluationContext context)
        {
            for(Node child : node.children)
            {
                if(!((Boolean)child.jjtAccept(this, context)).booleanValue())
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public Object visit(ASTnegation node, EvaluationContext context)
        {
            return !((Boolean)node.children[0].jjtAccept(this, context)).booleanValue();
        }

        @Override
        public Object visit(ASTequalityPredicate node, EvaluationContext context)
        {
            EvaluationContext variable = (EvaluationContext)node.getLhs().jjtAccept(this,
                context);
            if(!variable.getNode().isMissingNode())
            {
                String value = variable.getValue();
                return value != null ? node.getValue().equals(value) : false;
            }
            else
            {
                return false;
            }
        }

        @Override
        public Object visit(ASTmatchPredicate node, EvaluationContext context)
        {
            EvaluationContext variable = (EvaluationContext)node.getLhs().jjtAccept(this,
                context);
            if(!variable.getNode().isMissingNode())
            {
                String value = variable.getValue();
                return value != null ? node.getPattern().matcher(value).matches() : false;
            }
            else
            {
                return false;
            }
        }

        @Override
        public Object visit(ASTcontainmentPredicate node, EvaluationContext context)
        {
            EvaluationContext variable = (EvaluationContext)node.getLhs().jjtAccept(this,
                context);
            if(!variable.getNode().isMissingNode())
            {
                String value = variable.getValue();
                return value != null ? node.getValues().contains(value) : false;
            }
            else
            {
                return false;
            }
        }

        @Override
        public Object visit(ASTexistencePredicate node, EvaluationContext context)
        {
            EvaluationContext variable = (EvaluationContext)node.getLhs().jjtAccept(this, context);
            return !(variable.getNode().isMissingNode() || variable.getNode().isNull());
        }

        @Override
        public Object visit(ASTcomparisonPredicate node, EvaluationContext context)
        {
            EvaluationContext variable = (EvaluationContext)node.getLhs().jjtAccept(this, context);
            if(!variable.getNode().isMissingNode())
            {
                String value = variable.getValue();
                if(value.matches("[0-9]+"))
                {
                    return node.getOperator().compare(Integer.parseInt(value), node.getValue());
                }
                else
                {
                    variable.addError(" is not a number");
                }
            }
            return false;
        }

        @Override
        public Object visit(ASTvalue node, EvaluationContext data)
        {
            EvaluationContext p = data;
            for(int i = 0; i < node.jjtGetNumChildren() && !p.getNode().isMissingNode(); i++)
            {
                p = (EvaluationContextImpl)node.jjtGetChild(i).jjtAccept(this, p);
            }
            return p;
        }

        @Override
        public Object visit(ASTfieldSelection node, EvaluationContext context)
        {
            return context.getField(node.getIdentifier());
        }

        @Override
        public Object visit(ASTelementSelection node, EvaluationContext context)
        {
            EvaluationContext value = null;
            switch(node.getKind())
            {
            case INDEX:
                value = context.getElement(node.getIndex());
                break;
            case FIELD:
                value = context.getField(node.getIdentifier());
                break;
            case PREDICATE:
                if(context.getNode().isObject())
                {
                    Iterator<String> fieldNames = context.getNode().fieldNames();
                    while(fieldNames.hasNext())
                    {
                        EvaluationContext field = context.getField(fieldNames.next());
                        if(((Boolean)node.children[0].jjtAccept(this, field)).booleanValue())
                        {
                            value = field;
                        }
                    }
                    if(value == null)
                    {
                        context.addError(" does not contain child node satisfying "
                            + node.children[0].toString());
                        value = context.getMissing();
                    }
                }
                else if(context.getNode().isArray())
                {
                    for(int i = 0; i < context.getNode().size(); i++)
                    {
                        EvaluationContext elment = context.getElement(node.getIndex());
                        if(((Boolean)node.children[0].jjtAccept(this, elment)).booleanValue())
                        {
                            value = elment;
                        }
                    }
                    if(value == null)
                    {
                        context.addError(" does not contain child node satisfying "
                            + node.children[0].toString());
                        value = context.getMissing();
                    }
                }
                else
                {
                    context.addError(" is not a container node");
                }
                break;
            default:
                throw new RuntimeException("unsupported selection " + node.getKind());
            }
            return value;
        }
    }

    /**
     * Configuration of the service.
     */
    public static class Config
    {
        int parserPoolSize = 8;

        int parserPoolTimeout = 5;

        int parseErrorCacheSize = 100;

        int valueExprCacheSize = 1000;

        int predicateExprCacheSize = 100;

        /**
         * Default configuration.
         */
        public Config()
        {
        }

        /**
         * Configuration based on DNA Configuration object.
         * 
         * @param config DNA Configuration.
         */
        public Config(Configuration config)
        {
            parserPoolSize = config.getChild("parserPool").getChild("size")
                .getValueAsInteger(parserPoolSize);
            parserPoolTimeout = config.getChild("parserPool").getChild("timeout")
                .getValueAsInteger(parserPoolTimeout);
            valueExprCacheSize = config.getChild("expressionCache").getChild("values")
                .getValueAsInteger(valueExprCacheSize);
            predicateExprCacheSize = config.getChild("expressionCache").getChild("predicates")
                .getValueAsInteger(predicateExprCacheSize);
            parseErrorCacheSize = config.getChild("errorCache").getChild("size")
                .getValueAsInteger(parseErrorCacheSize);
        }

        /**
         * Custom configuration.
         * 
         * @param parserPoolSize size of parser pool.
         * @param parserPoolTimeout timeout for acquiring a parser from the pool in seconds.
         * @param valueExprCacheSize size of LRU cache for value expressions.
         * @param predicateExprCacheSize size of LRU cache for predicate expressions.
         */
        public Config(int parserPoolSize, int parserPoolTimeout, int parseErrorCacheSize,
            int valueExprCacheSize, int predicateExprCacheSize)
        {
            this.parserPoolSize = parserPoolSize;
            this.parserPoolTimeout = parserPoolTimeout;
            this.parseErrorCacheSize = parseErrorCacheSize;
            this.valueExprCacheSize = valueExprCacheSize;
            this.predicateExprCacheSize = predicateExprCacheSize;
        }
    }
}
