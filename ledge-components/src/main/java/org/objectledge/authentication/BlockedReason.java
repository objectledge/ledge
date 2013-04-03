package org.objectledge.authentication;

import java.util.HashMap;
import java.util.Map;

public enum BlockedReason
{
    OK
    {
        @Override
        public Integer getCode()
        {
            return OK_CODE;
        }

        @Override
        public String getReason()
        {
            return "OK";
        }

        @Override
        public String getShortReason()
        {
            return "OK";
        }
    },
    NOT_CONFIRMED
    {
        @Override
        public Integer getCode()
        {
            return NOT_CONFIRMED_CODE;
        }

        @Override
        public String getReason()
        {
            return "Account blocked because email has been not confirmed yet";
        }

        @Override
        public String getShortReason()
        {
            return String.format(ACCOUNT_BLOCKED_SHORT, NOT_CONFIRMED_CODE);
        }
    },
    ACCOUNT_EXPIRED
    {
        @Override
        public Integer getCode()
        {
            return ACCOUNT_EXPIRED_CODE;
        }

        @Override
        public String getReason()
        {
            return "Account expired because of invactivity of the user";
        }

        @Override
        public String getShortReason()
        {
            return String.format(ACCOUNT_BLOCKED_SHORT, ACCOUNT_EXPIRED_CODE);
        }
    },
    ACCOUNT_DELETED
    {
        @Override
        public Integer getCode()
        {
            return ACCOUNT_DELETED_CODE;
        }

        @Override
        public String getReason()
        {
            return "Account deleted by user";
        }

        @Override
        public String getShortReason()
        {
            return String.format(ACCOUNT_BLOCKED_SHORT, ACCOUNT_DELETED_CODE);
        }
    },
    PASSWORD_EXPIRED
    {
        @Override
        public Integer getCode()
        {
            return PASSWORD_EXPIRED_CODE;
        }

        @Override
        public String getReason()
        {
            return "Password expired";
        }

        @Override
        public String getShortReason()
        {
            return String.format(ACCOUNT_BLOCKED_SHORT, PASSWORD_EXPIRED_CODE);
        }
    },
    BLOCKED_BY_ADMIN_DUE_TO_BREAKING_RULES
    {
        @Override
        public Integer getCode()
        {
            return BLOCKED_BY_ADMIN_DUE_TO_BREAKING_RULES_CODE;
        }

        @Override
        public String getReason()
        {
            return "Account blocked due to violating Terms Of Service";
        }

        @Override
        public String getShortReason()
        {
            return String
                .format(ACCOUNT_BLOCKED_SHORT, BLOCKED_BY_ADMIN_DUE_TO_BREAKING_RULES_CODE);
        }
    },
    BLOCKED_BY_ADMIN_DUE_TO_SPAM
    {
        @Override
        public Integer getCode()
        {
            return BLOCKED_BY_ADMIN_DUE_TO_SPAM_CODE;
        }

        @Override
        public String getReason()
        {
            return "Account blocked due to distributing spam";
        }

        @Override
        public String getShortReason()
        {
            return String.format(ACCOUNT_BLOCKED_SHORT, BLOCKED_BY_ADMIN_DUE_TO_SPAM_CODE);
        }

    },
    BLOCKED_BY_ADMIN
    {
        @Override
        public Integer getCode()
        {
            return BLOCKED_BY_ADMIN_CODE;
        }

        @Override
        public String getReason()
        {
            return "Account blocked by site administrator";
        }

        @Override
        public String getShortReason()
        {
            return String.format(ACCOUNT_BLOCKED_SHORT, BLOCKED_BY_ADMIN_CODE);
        }
    };

    public static BlockedReason getByCode(int code)
    {
        return codeToReason.get(code);
    }

    public static String translateReason(int reasonCode)
    {
        String result = "";
        switch(reasonCode)
        {
        case 0:
            result = "Aktywne";
            break;
        case 1:
            result = "Konto nie zostało potwierdzone, potwierdz wysłany emailem link";
            break;
        case 2:
            result = "Konto zostało zablokowane w wyniku jego nieaktywności";
            break;
        case 3:
            result = "Konto zostało zablokowane w wyniku wygaśnięcia hasła";
            break;
        case 4:
            result = "Konto zostało zablokowane przez admina z powodu naruszenia regulaminu";
            break;
        case 5:
            result = "Konto zostało zablokowane przez admina z powodu spamu";
            break;
        case 6:
            result = "Konto zostało zablokowane przez admina";
            break;
        case 7:
            result = "Konto zostało usunięte";
            break;
        default:
            result = "Konto jest zablokowane z nieznanej przyczyny skontaktuj się z administratorem systemu";
        }
        return result;
    }

    public abstract Integer getCode();

    public abstract String getReason();

    public abstract String getShortReason();

    // todo
    // maybe some method which takes locale and returns proper translation ??

    private final static String ACCOUNT_BLOCKED_SHORT = "account_blocked_%s";

    private final static Integer OK_CODE = 0;

    private final static Integer NOT_CONFIRMED_CODE = 1;

    private final static Integer ACCOUNT_EXPIRED_CODE = 2;

    private final static Integer PASSWORD_EXPIRED_CODE = 3;

    private final static Integer BLOCKED_BY_ADMIN_DUE_TO_BREAKING_RULES_CODE = 4;

    private final static Integer BLOCKED_BY_ADMIN_DUE_TO_SPAM_CODE = 5;

    private final static Integer BLOCKED_BY_ADMIN_CODE = 6;

    private final static Integer ACCOUNT_DELETED_CODE = 7;

    private static final Map<Integer, BlockedReason> codeToReason;

    static
    {
        codeToReason = new HashMap<Integer, BlockedReason>(6);

        for(BlockedReason reason : BlockedReason.values())
        {
            codeToReason.put(reason.getCode(), reason);
        }
    }
}
