
import java.util.Locale;

import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.mail.MailmanMailingListsManager;

public class Tescik
{

    public Tescik()
    {
        super();
    }

    /**
     * @param args
     */
    public static void main(String[] args)
        throws Exception
    {
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(Tescik.class));
        MailmanMailingListsManager mml = 
            new MailmanMailingListsManager(logger, 
                "http://potenilla.caltha.pl/mailman/RPC2", "", "12345");
        mml.createList("dupsko", "potenilla.caltha.pl", false, 
            new String[]{"pablo@caltha.pl"},"", false, new Locale("pl","PL"));
    }

}
