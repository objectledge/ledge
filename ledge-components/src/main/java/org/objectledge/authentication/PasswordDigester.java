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

import java.security.MessageDigest;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import sun.misc.BASE64Encoder;

/**
 * Default implementation of password digester.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class PasswordDigester
{
    /** the digest algorithm - null for plaintext */
    private String algorithm;

    /** the local message digest pool */
    private ObjectPool messageDigestPool = new GenericObjectPool(new MessageDigestFactory());

    /**
     * component constructor.
     * 
     * @param algorithm the algorithm.
     */
    public PasswordDigester(String algorithm)
    {
        this.algorithm = algorithm;
    }

    /**
     * Digests a given password using a chosen algorithm.
     * @param password the password to be digested.
     */
    public String digestPassword(String password)
    {
        if (algorithm != null)
        {
            if (password == null)
            {
                // return unmatchable password for non-login accounts
                return "-";
            }
            MessageDigest digest = null;
            try
            {
                digest = (MessageDigest)messageDigestPool.borrowObject();
                byte[] hash = digest.digest(password.getBytes());
                messageDigestPool.returnObject(digest);
                BASE64Encoder enc = new BASE64Encoder();
                StringBuilder encoded = new StringBuilder();
                encoded.append('{');
                encoded.append(algorithm.toLowerCase());
                encoded.append('}');
                encoded.append(enc.encode(hash));
                return encoded.toString();
            }
            catch (Exception e)
            {
                RuntimeException ee = new IllegalArgumentException("Digest password exception: "+
                                                                    e.getMessage());
                ee.initCause(e);
                throw ee;
            }
        }
        else
        {
            return password;
        }
    }
    
    /**
     * A factory of MessageDigest objects.
     */
    private class MessageDigestFactory
        extends BasePoolableObjectFactory
    {
        /**
         * {@inheritDoc}
         */
        public Object makeObject() throws Exception
        {
            return MessageDigest.getInstance(algorithm);
        }
    }
    
}
