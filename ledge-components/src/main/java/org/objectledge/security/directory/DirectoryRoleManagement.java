package org.objectledge.security.directory;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.DefaultPrincipal;
import org.objectledge.naming.ContextFactory;
import org.objectledge.naming.ContextHelper;
import org.objectledge.security.RoleManagement;

/**
 * RoleChecking and RoleManagement implementation on a LDAP directory.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public class DirectoryRoleManagement
    implements RoleManagement
{
    private static final String OBJECT_CLASS_ATTR = "objectClass";

    private static final String CN_ATTR = "cn";

    private static final String ROLE_OCCUPANT_ATTR = "roleOccupant";

    private static final String ORGANIZATIONAL_ROLE_CLASS = "organizationalRole";

    private static final String CURRENT_CTX = "";

    private static final String HAS_ROLE_QUERY = "(&(objectClass=organizationalRole)(cn={0})(roleOccupant={1}))";

    private static final String ROLE_QUERY = "(&(objectClass=organizationalRole)(cn={0}))";

    private static final String USER_ROLES_QUERY = "(&(objectClass=organizationalRole)(roleOccupant={0}))";

    private static final String CONTEXT_ALIAS = "roles";

    private ContextHelper ctxHelper;

    public DirectoryRoleManagement(ContextFactory contextFactory, Logger logger)
        throws NamingException
    {
        ctxHelper = new ContextHelper(contextFactory, CONTEXT_ALIAS, logger);
    }

    private DirContext getContext()
    {
        try
        {
            return ctxHelper.getBaseDirContext();
        }
        catch(NamingException e)
        {
            throw new RuntimeException(CONTEXT_ALIAS + " context is not available", e);
        }
    }

    @Override
    public boolean hasRole(Principal user, String role)
    {
        DirContext ctx = getContext();
        try
        {
            final SearchControls ctl = new SearchControls();
            ctl.setReturningAttributes(new String[] { CN_ATTR });
            NamingEnumeration<SearchResult> results = ctx.search(CURRENT_CTX, HAS_ROLE_QUERY,
                new Object[] { role, user.getName() }, ctl);
            final boolean exists = results.hasMore();
            results.close();
            return exists;
        }
        catch(NamingException e)
        {
            throw new RuntimeException("Directory query failed", e);
        }
        finally
        {
            ctxHelper.close(ctx);
        }
    }

    private DirContext getRole(DirContext baseDirContext, String role)
        throws NamingException
    {
        final SearchControls ctl = new SearchControls();
        ctl.setReturningAttributes(new String[] { CN_ATTR });
        NamingEnumeration<SearchResult> results = baseDirContext.search(CURRENT_CTX, ROLE_QUERY,
            new Object[] { role }, ctl);
        DirContext result = null;
        if(results.hasMore())
        {
            final SearchResult element = results.next();
            result = (DirContext)baseDirContext.lookup(element.getName());
        }
        results.close();
        return result;
    }

    private DirContext createRole(DirContext ctx, String role)
        throws InvalidNameException, NamingException
    {
        Name name = new LdapName(Collections.singletonList(new Rdn(CN_ATTR, role)));
        Attributes attrs = new BasicAttributes();
        attrs.put(OBJECT_CLASS_ATTR, ORGANIZATIONAL_ROLE_CLASS);
        return ctx.createSubcontext(name, attrs);
    }

    @Override
    public void grant(Principal user, String role)
    {
        DirContext ctx = getContext();
        try
        {
            DirContext roleCtx = getRole(ctx, role);
            if(roleCtx == null)
            {
                roleCtx = createRole(ctx, role);
            }
            final BasicAttribute occupant = new BasicAttribute(ROLE_OCCUPANT_ATTR, user.getName());
            final ModificationItem addOccupant = new ModificationItem(DirContext.ADD_ATTRIBUTE,
                occupant);
            roleCtx.modifyAttributes(CURRENT_CTX, new ModificationItem[] { addOccupant });
        }
        catch(NamingException e)
        {
            throw new RuntimeException("Directory update failed", e);
        }
        finally
        {
            ctxHelper.close(ctx);
        }
    }

    @Override
    public void revoke(Principal user, String role)
    {
        DirContext ctx = getContext();
        try
        {
            DirContext roleCtx = getRole(ctx, role);
            if(roleCtx != null)
            {
                final BasicAttribute occupant = new BasicAttribute(ROLE_OCCUPANT_ATTR,
                    user.getName());
                final ModificationItem addOccupant = new ModificationItem(
                    DirContext.REMOVE_ATTRIBUTE, occupant);
                roleCtx.modifyAttributes(CURRENT_CTX, new ModificationItem[] { addOccupant });
            }
        }
        catch(NamingException e)
        {
            throw new RuntimeException("Directory update failed", e);
        }
        finally
        {
            ctxHelper.close(ctx);
        }
    }

    @Override
    public Set<String> getRoles(Principal user)
    {
        DirContext ctx = getContext();
        try
        {
            NamingEnumeration<SearchResult> results = ctx.search(CURRENT_CTX, USER_ROLES_QUERY,
                new Object[] { user.getName() }, new SearchControls());
            Set<String> roles = new HashSet<>();
            while(results.hasMore())
            {
                SearchResult result = results.next();
                roles.add((String)result.getAttributes().get(CN_ATTR).get());
            }
            results.close();
            return roles;
        }
        catch(NamingException e)
        {
            throw new RuntimeException("Directory query failed", e);
        }
        finally
        {
            ctxHelper.close(ctx);
        }
    }

    public Set<Principal> getRoleOccupants(String role)
    {
        DirContext ctx = getContext();
        try
        {
            final SearchControls ctl = new SearchControls();
            ctl.setReturningAttributes(new String[] { ROLE_OCCUPANT_ATTR });
            NamingEnumeration<SearchResult> results = ctx.search(CURRENT_CTX, ROLE_QUERY,
                new Object[] { role }, ctl);
            Set<Principal> principals = new HashSet<>();
            if(results.hasMore())
            {
                SearchResult result = results.next();
                NamingEnumeration<String> occupants = (NamingEnumeration<String>)result
                    .getAttributes().get(ROLE_OCCUPANT_ATTR).getAll();
                while(occupants.hasMore())
                {
                    principals.add(new DefaultPrincipal(occupants.next()));
                }
                occupants.close();
            }
            results.close();
            return principals;
        }
        catch(NamingException e)
        {
            throw new RuntimeException("Directory query failed", e);
        }
        finally
        {
            ctxHelper.close(ctx);
        }
    }
}
