package org.objectledge.authentication.identity;

import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

public abstract class BaseIdentityStore implements IdentityStore
{
    private Random random = new SecureRandom();

    private int tokenLength;

    public BaseIdentityStore(int tokenLength)
    {
        this.tokenLength = tokenLength;
    }

    protected String newToken()
    {
        byte[] b = new byte[tokenLength];
        random.nextBytes(b);
        return Base64.encodeBase64String(b);
    }
}
