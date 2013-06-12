package org.objectledge.authentication;

public enum LdapMapper
{

    LOGIN()
    {
        @Override
        public String getLdapName()
        {
            return "uid";
        }
    },
    PASSWORD()
    {
        @Override
        public String getLdapName()
        {
            return "userPassword";
        }
    },
    EMAIL()
    {
        @Override
        public String getLdapName()
        {
            return "mail";
        }
    },
    COMMON_NAME()
    {
        @Override
        public String getLdapName()
        {
            return "cn";
        }
    },
    GIVEN_NAME()
    {
        @Override
        public String getLdapName()
        {
            return "givenName";
        }
    },
    GIVEN_SURNAME()
    {
        @Override
        public String getLdapName()
        {
            return "sn";
        }
    },
    AVATAR_FILE()
    {
        @Override
        public String getLdapName()
        {
            return "jpegPhoto";
        }
    },
    LAST_LOGON_TIME()
    {
        @Override
        public String getLdapName()
        {
            return "lastLogonTimestamp";
        }
    },
    BLOCKED_REASON()
    {
        @Override
        public String getLdapName()
        {
            return "shadowFlag";
        }
    },
    LAST_PASSWORD_CHANGE()
    {

        @Override
        public String getLdapName()
        {
            return "shadowLastChange";
        }
    },
    PASSWORD_EXPIRATION_WARNING_DAYS()
    {
        @Override
        public String getLdapName()
        {
            return "shadowWarning";
        }
    },
    PASSWORD_EXPIRATION_DAYS_MAX()
    {
        @Override
        public String getLdapName()
        {
            return "shadowMax";
        }
    },
    PASSWORD_LAST_CHANGE()
    {
        @Override
        public String getLdapName()
        {
            return "shadowLastChange";
        }
    },
    ACCOUNT_EXPIRATION_DATE()
    {
        @Override
        public String getLdapName()
        {
            return "shadowExpire";
        }
    },
    LOGON_COUNT()
    {
        @Override
        public String getLdapName()
        {
            return "logonCount";
        }
    },
    ;
    public abstract String getLdapName();
}
