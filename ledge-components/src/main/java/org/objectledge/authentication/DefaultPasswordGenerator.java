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

import java.util.Random;

/**
 * Default password generator.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class DefaultPasswordGenerator extends PasswordGenerator
{
    /** The pseudorandom numbers generator */
    private Random randomGenerator;
    
    /**
     * Alphabet consisting of letters A-Z and digits 0-9.
     */
    private static final char[] ALPHABET = {
        'A','B','C','D','E','F','G','H',
        'I','J','K','L','M','N','O','P',
        'Q','R','S','T','U','V','W','X',
        'Y','Z','1','2','3','4','5','6',
        '7','8','9','0','a','b','c','d',
        'e','f','g','h','i','j','k','l',
        'm','n','o','p','q','r','s','t',
        'u','v','w','x','y','z',
    };

    /**
     * Component constructor.
     */
    public DefaultPasswordGenerator()
    {
        randomGenerator = new Random();
    }

    /**
     * {@inheritDoc}
     */
    public String createRandomPassword(int min, int max)
    {
        int length = 0;
        if (min < max)
        {
            int offset = randomGenerator.nextInt(max - min + 1);
            length = min + offset;
        }
        else
        {
            length = min;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++)
        {
            sb.append(ALPHABET[randomGenerator.nextInt(ALPHABET.length)]);
        }
        return sb.toString();
    }
}
