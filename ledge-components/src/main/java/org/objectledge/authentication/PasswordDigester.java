// 
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//All rights reserved. 
//   
//Redistribution and use in source and binary forms, with or without modification,  
//are permitted provided that the following conditions are met: 
//   
//* Redistributions of source code must retain the above copyright notice,  
//this list of conditions and the following disclaimer. 
//* Redistributions in binary form must reproduce the above copyright notice,  
//this list of conditions and the following disclaimer in the documentation  
//and/or other materials provided with the distribution. 
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//nor the names of its contributors may be used to endorse or promote products  
//derived from this software without specific prior written permission. 
// 
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
//POSSIBILITY OF SUCH DAMAGE. 
//

package org.objectledge.authentication;

import static java.lang.System.arraycopy;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.impl.StackKeyedObjectPool;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.ComponentInitializationError;

/**
 * Default implementation of password digester.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class PasswordDigester
{
    /**
     * Password schemes supported with JDK 1.6.
     */
    public static final Set<String> SUPPORTED_SCHEMES = unmodifiableSet(new HashSet<String>(asList(
        "SHA", "SSHA", "SHA1", "SSHA1", "MD5", "SMD5", "SHA256", "SSHA256", "SHA384", "SSHA384",
        "SHA512", "SSHA512")));

    /** Password scheme */
    private final String defaultScheme;

    /** the local message digest pool */
    private final KeyedObjectPool digestPool = new StackKeyedObjectPool(new PasswordDigestFactory());

    /** random generator for salt. */
    private final Random random;

    private static final Pattern ENCODED_PASSORD_PATTERN = Pattern
        .compile("\\{([A-Za-z0-9]+)\\}([A-Za-z0-9+/\\-_=]+)");

    /**
     * Create PasswordDigester instance.
     * 
     * @param scheme default password encryption scheme.
     */
    public PasswordDigester(String scheme)
    {
        this.defaultScheme = scheme;
        random = new SecureRandom();
        random.setSeed(System.nanoTime());
        try
        {
            PasswordDigest digest = (PasswordDigest)digestPool.borrowObject(scheme);
            digestPool.returnObject(scheme, digest);
        }
        catch(Exception e)
        {
            throw new ComponentInitializationError("Unable to initialize password digester", e);
        }
    }

    public PasswordDigester(Configuration configuration)
        throws ConfigurationException
    {
        this(configuration.getChild("scheme").getValue());
    }

    /**
     * Generate password digest using default scheme.
     * 
     * @param password password to process.
     * @return digested and encoded password.
     */
    public String generateDigest(String password)
    {
        try
        {
            PasswordDigest digest = (PasswordDigest)digestPool.borrowObject(defaultScheme);
            try
            {
                StringBuilder buff = new StringBuilder();
                buff.append("{").append(defaultScheme).append("}");
                buff.append(digest.generate(password));
                return buff.toString();
            }
            finally
            {
                digestPool.returnObject(defaultScheme, digest);
            }
        }
        catch(Exception e)
        {
            // algorithm was verified in component constructor, so we don't expecte exceptions at
            // this point.
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate password digest using custom scheme.
     * 
     * @param password the password.
     * @param scheme password scheme to use.
     * @return digested and encoded password.
     * @throws NoSuchAlgorithmException if the specified password scheme is not supported
     */
    public String generateDigest(String password, String scheme)
        throws NoSuchAlgorithmException
    {
        try
        {
            PasswordDigest digest = (PasswordDigest)digestPool.borrowObject(scheme);
            try
            {
                StringBuilder buff = new StringBuilder();
                buff.append("{").append(scheme).append("}");
                buff.append(digest.generate(password));
                return buff.toString();
            }
            finally
            {
                digestPool.returnObject(scheme, digest);
            }
        }
        catch(Exception e)
        {
            if(e instanceof NoSuchAlgorithmException)
            {
                throw (NoSuchAlgorithmException)e;
            }
            else
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Validate password against password digest.
     * 
     * @param password the password.
     * @param encoded digested and encoded password.
     * @return true if the password matches
     * @throws NoSuchAlgorithmException if the specified password scheme is not supported
     */
    public boolean validateDigest(String password, String encoded)
        throws NoSuchAlgorithmException
    {
        Matcher matcher = ENCODED_PASSORD_PATTERN.matcher(encoded);
        if(matcher.matches())
        {
            String scheme = matcher.group(1);
            String hash = matcher.group(2);
            try
            {
                PasswordDigest digest = (PasswordDigest)digestPool.borrowObject(scheme);
                try
                {
                    return digest.validate(password, hash);
                }
                finally
                {
                    digestPool.returnObject(scheme, digest);
                }
            }
            catch(RuntimeException e)
            {
                throw e;
            }
            catch(Exception e)
            {
                if(e instanceof NoSuchAlgorithmException)
                {
                    throw (NoSuchAlgorithmException)e;
                }
                else
                {
                    throw new RuntimeException(e);
                }
            }
        }
        else
        {
            throw new IllegalArgumentException("invalid hashed password format " + encoded
                + " {SCHEME}BASE64 expected");
        }
    }

    private class PasswordDigest
    {
        private final Pattern SHA2_PATTERN = Pattern.compile("SHA(\\d+)");

        public static final int SALT_BITS = 64;

        private final MessageDigest messageDigest;

        private final int digestLength;

        private final boolean useSalt;

        private final Base64 base64 = new Base64();

        public PasswordDigest(String scheme)
            throws NoSuchAlgorithmException
        {
            String algorithm = scheme.toUpperCase();
            if(algorithm.matches("^S[^H].*"))
            {
                algorithm = algorithm.substring(1);
                useSalt = true;
            }
            else
            {
                useSalt = false;
            }
            if(algorithm.equals("SHA1"))
            {
                algorithm = "SHA";
            }
            Matcher m = SHA2_PATTERN.matcher(algorithm);
            if(m.matches())
            {
                algorithm = "SHA-" + m.group(1);
            }
            this.messageDigest = MessageDigest.getInstance(algorithm);
            this.digestLength = messageDigest.getDigestLength();
        }

        public String generate(String password)
        {
            messageDigest.reset();
            messageDigest.update(password.getBytes());
            byte[] digest;
            if(useSalt)
            {
                byte[] salt = new byte[SALT_BITS / 8];
                random.nextBytes(salt);
                messageDigest.update(salt);
                digest = messageDigest.digest();
                byte[] temp = new byte[digest.length + salt.length];
                System.arraycopy(digest, 0, temp, 0, digest.length);
                System.arraycopy(salt, 0, temp, digest.length, salt.length);
                digest = temp;
            }
            else
            {
                digest = messageDigest.digest();
            }
            return base64.encodeAsString(digest);
        }

        public boolean validate(String password, String hash)
        {
            messageDigest.reset();
            messageDigest.update(password.getBytes());
            byte[] digest = base64.decode(hash);
            if(useSalt)
            {
                int saltBytes = digest.length - digestLength;
                if(saltBytes < 1)
                {
                    throw new IllegalArgumentException(
                        "not enough salt bytes - wrong password scheme used or truncated data: expected "
                            + (useSalt ? "at least " : "") + (digestLength + 1) + " bytes, got "
                            + digest.length);
                }
                byte[] salt = new byte[saltBytes];
                arraycopy(digest, digestLength, salt, 0, saltBytes);
                messageDigest.update(salt);
                byte[] temp = new byte[digestLength];
                arraycopy(digest, 0, temp, 0, digestLength);
                digest = temp;
            }
            if(digest.length < digestLength)
            {
                throw new IllegalArgumentException(
                    "wrong password scheme used or truncated data: expected "
                        + (useSalt ? ("at least " + (digestLength + 1)) : digestLength)
                        + " bytes, got " + digest.length);
            }
            return MessageDigest.isEqual(digest, messageDigest.digest());
        }
    }

    /**
     * A factory of PasswordDigest objects.
     */
    private class PasswordDigestFactory
        extends BaseKeyedPoolableObjectFactory
    {

        @Override
        public Object makeObject(Object key)
            throws Exception
        {
            return new PasswordDigest((String)key);
        }
    }
}
