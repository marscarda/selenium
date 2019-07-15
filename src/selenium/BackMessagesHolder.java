package selenium;
//******************************************************************
import java.util.ArrayList;
import java.util.List;
//******************************************************************
public class BackMessagesHolder 
{
    //==============================================================
    private static final Object sync = new Object();
    private static BackMessage[] messages = null;
    //==============================================================
    public static void addMessage (long userid, String msg, boolean red) {
        BackMessage message = new BackMessage();
        message.userid = userid;
        message.message = msg;
        message.red = red;
        synchronized (sync) {
            //-------------------------------------------------------
            if (messages == null) {
                messages = new BackMessage[1];
                messages[0] = message;
                return;
            }
            //-------------------------------------------------------
            int count = messages.length;
            BackMessage[] newmessages = new BackMessage[count + 1];
            System.arraycopy(messages, 0, newmessages, 0, count);
            newmessages[count] = message;
            messages = newmessages;
            //-------------------------------------------------------
        }
    }
    //==============================================================
    public static BackMessage[] getMessages(long userid) {
        if (messages == null) return new BackMessage[0];
        List<BackMessage> deliver = new ArrayList<>();
        List<BackMessage> keeping = new ArrayList<>();
        synchronized (sync) {
            for (BackMessage message : messages) {
                if (message.userid == userid) deliver.add(message);
                else keeping.add(message);
            }
            messages = keeping.toArray(new BackMessage[0]);
        }
        return deliver.toArray(new BackMessage[0]);
    }
    //==============================================================
}
//******************************************************************