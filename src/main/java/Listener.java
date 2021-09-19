import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import java.util.*;
import java.util.concurrent.TimeUnit;

// :hw => current day's homework

public class Listener extends ListenerAdapter {

    public static final String PREFIX = "?";

    private void conlin(MessageReceivedEvent event) {
        sendMessage(event, 
            "> The limit of the function never depends on the value of the function at that point.\n\n- John Conlin"
        );
    }
    private void german(MessageReceivedEvent event){
        sendMessage(event, "Guten Morgen");
    }

    private void sendMessage(MessageReceivedEvent event, MessageEmbed payload) {
        event.getChannel().sendMessage(payload)
                .queue();
    }

    private void sendMessage(MessageReceivedEvent event, String payload) {
        event.getChannel().sendMessage(payload).queueAfter(3, TimeUnit.SECONDS);
                // .queue();
    }

    public void update() throws Exception {
        Conlin.getHomework(Conlin.getMonth() + Conlin.getYear());
    }

    private String getClosestHWDate() {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("PST"));
        int day = now.get(Calendar.DAY_OF_MONTH);
        int month = now.get(Calendar.MONTH) + 1;
        
        return getClosestHWDate(month, day);
    }

    private String getClosestHWDate(int month, int day) {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("PST"));
        int year = now.get(Calendar.YEAR) + 1;
        String hw = month + "/" + day;

        while(!Conlin.homeworks.containsKey(hw)) {
            day--;
            if(day == 0) {
                month--;

                if(month == 0)
                    return null;

                // ugly checks for stupid month days
                if(month == 2)
                    day = year % 4 == 0 ? 28 : 27;
                else if(month < 8) {
                    if(month % 2 == 0)
                        day = 30;
                    else
                        day = 31;
                } else {
                    if(month % 2 == 0)
                        day = 31;
                    else
                        day = 30;
                }
                
            }
            hw = month + "/" + day;
        }

        return hw;
    }

    private void hw(MessageReceivedEvent event, List<String> args) {
        try {
            String hw = "";

            if (args.size() > 0)
                hw = args.get(0);
            
            if (!hw.equals("")) {
                try {
                    hw = hw.replaceAll("\\.", "/");
                    hw = hw.replaceAll("-", "/");

                    int month = Integer.parseInt(hw.substring(0, hw.indexOf("/")));
                    int day = Integer.parseInt(hw.substring(hw.indexOf("/") + 1));

                    hw = getClosestHWDate(month, day);
                } catch (Exception e) {
                    System.out.println(e);
                    sendMessage(event, "Father Conlin dislikes your date format");
                    return;
                }
            } else 
                hw = getClosestHWDate();

            Homework homework = Conlin.homeworks.get(hw);

            if (homework == null) {
                sendMessage(event, "Homework not found");
                return;
            }

            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle("Homework for " + hw, Conlin.getUrl(Conlin.getMonth() + Conlin.getYear()));

            eb.setColor(Color.PINK);

            eb.setDescription(homework.assignment);

            eb.addField("Homework Number", Integer.toString(homework.number), true);
            eb.addField("Section", homework.section, true);

            eb.setAuthor("John Conlin", "http://mvhs-fuhsd.org/john_conlin/");

            eb.setFooter("🍞 Bread 👍 ");

            sendMessage(event, eb.build());
        } catch (Exception e) {
            sendMessage(event, "Homework not found");
        }
    }

    private void answers(MessageReceivedEvent event) {
        sendMessage(event, "https://drive.google.com/drive/u/2/folders/1iw7QwFqWgbEOwm6SIt_fJO-Tp0Qvvroo");
    }

    private void lecky(MessageReceivedEvent event) {
        sendMessage(event, "http://www.chaoticgolf.com/tutorials_calc_aahs.html");
    }

    private void help(MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Help", Conlin.getUrl(Conlin.getMonth() + Conlin.getYear()));

        eb.setColor(Color.GREEN);

        eb.addField("Help", "Get the help command; you can run `?help` to get this message.", false);
        eb.addField("Homework", "Get the current homework with `?hw` or a past/future one with `?hw month/day`.",
                false);
        eb.addField("Conlin", "Use `?conlin` to get Mr. Conlin's most iconic quote.", false);
        eb.addField("Lecky", "Use `?lecky` to get a link to Mr. Lecky's website.", false);
        eb.addField("Answers", "Use `?answers` to get a link to the Google Drive.", false);

        eb.setAuthor("Conlin BC", "http://mvhs-fuhsd.org/john_conlin/");

        eb.setFooter("🍞 Bread 👍");

        sendMessage(event, eb.build());
    }

    private void mathHelp(MessageReceivedEvent event) {
        sendMessage(event, "Help Daddy <@446065841172250638>");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;

        String content = event.getMessage().getContentRaw();

        if(content.equalsIgnoreCase("fiesta")) {
            sendMessage(event, content);
            return;
        }

        if (!content.startsWith(PREFIX))
            return;

        ArrayList<String> args = new ArrayList<String>(Arrays.asList(content.split("\\s+")));
        String command = args.get(0).substring(1);
        args.remove(0);

        switch (command) {
            case "conlin":
                conlin(event);
                break;
            case "hw":
                hw(event, args);
                break;
            case "update":
                try {
                    update();
                    sendMessage(event, "Updated problems!");
                } catch (Exception e) {
                    sendMessage(event, "Error: " + e);
                }
                break;
            case "answers":
                answers(event);
                break;
            case "lecky":
                lecky(event);
                break;
            case "help":
                help(event);
                break;
            case "math-help":
                mathHelp(event);
                break;
            case "dont-say":
                sendMessage(event, "fuck");
                break;
            case "german":
                german(event);
        }
    }
}
