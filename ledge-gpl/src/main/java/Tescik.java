
import java.util.Locale;

import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.mail.MailingList;
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
        mml.createList("test1", "potenilla.caltha.pl", false, 
          new String[]{"pablo@caltha.pl"},"haslo", false, new Locale("pl","PL"));
        System.out.println(mml.getLists());
        MailingList ml = mml.getList("test1", "haslo");
        System.out.println("STATUS: "+ml.addMember("test1@caltha.pl", "jan dzban", "qaz", true, true));
        System.out.println("STATUS: "+ml.addMember("test2@caltha.pl", "john don", "zaq", false, true));
        System.out.println("STATUS: "+ml.addMember("test3@caltha.pl", "john don", "zaq", false, false));
        System.out.println("MEMBERS: "+ml.getMembers());
        System.out.println("STATUS: "+ml.deleteMember("test1@caltha.pl", true)); 
        //System.out.println("STATUS: "+ml.deleteMember("007j24@wp.pl", true));
        System.out.println("MEMBERS: "+ml.getMembers());
        mml.deleteList("test1", true);
    }

}
