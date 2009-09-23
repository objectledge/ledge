package org.objectledge.mail;

import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.mail.MailmanMailingList;
import org.objectledge.mail.MailmanMailingListsManager;

public class IntegrationTest
{

    public IntegrationTest()
    {
        super();
    }

    /**
     * @param args
     */
    public static void main(String[] args)
        throws Exception
    {
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(IntegrationTest.class));
        MailmanMailingListsManager mml = 
            new MailmanMailingListsManager(logger, 
                "http://potenilla.caltha.pl/mailman/RPC2", "12345");

        MailmanMailingListsManager mml2 = 
            new MailmanMailingListsManager(logger, 
                "http://localhost:8999", "12345");

        /**
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
        
        */
        /**
        MailingList ml = mml.getList("kaszanka", "12345");
        ml.setSubscriptionPolicy(2);
        ml.setPostingModerated(true);
        System.out.println("Subscription policy?: "+ml.getSubscriptionPolicy());
        System.out.println("Moderowane?: "+ml.isPostingModerated());
        ml.setSubscriptionPolicy(3);
        ml.setPostingModerated(false);
        System.out.println("Subscription policy?: "+ml.getSubscriptionPolicy());
        System.out.println("Moderowane?: "+ml.isPostingModerated());
        */
        /**
        MailmanMailingList ml = (MailmanMailingList)mml.getList("testlist", "12345");
        System.out.println("Messages: "+ml.getPendingPosts());
        System.out.println("Type: "+ml.getPendingTaskType(4));
        System.out.println(ml.getPendingMessage(4));
        Properties sessionProperties = new Properties();
        Session session = Session.getInstance(sessionProperties);
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("testlist@potenilla.caltha.pl"));
        message.addRecipient(RecipientType.TO, new InternetAddress("blbble@potenilla.caltha.pl"));
        message.setSubject("test x 15");
        message.setText("bleble");
        message.setHeader("sender","fsdfsdf");
        ml.postMessage(message);
        */
        MailmanMailingList ml = (MailmanMailingList)mml2.getList("testlist", "12345");
        System.out.println("Messages: "+ml.getPendingPosts());
        //ml.acceptMessage(4);
    }

}
