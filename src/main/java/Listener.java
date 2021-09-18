import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import java.util.*;

// :hw => current day's homework

public class Listener extends ListenerAdapter {

    public static final String PREFIX = "?";

    private void conlin(MessageReceivedEvent event) {
        event.getChannel().sendMessage(
                "> The limit of the function never depends on the value of the function at that point.\n\n- John Conlin")
                .queue();
    }

    public void update() throws Exception {
        Conlin.getHomework(Conlin.getMonth() + Conlin.getYear());
    }

    private String getClosestHWDate() {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("PST"));
        int day = now.get(Calendar.DAY_OF_MONTH);
        int month = now.get(Calendar.MONTH) + 1;
        String hw = month + "/" + day;

        while(!Conlin.homeworks.containsKey(hw)) {
            day--;
            if(day == 0) 
                return null;
            hw = month + "/" + day;
        }

        return hw;
    }

    private void hw(MessageReceivedEvent event, List<String> args) {
        try {
            String hw = "";

            if (args.size() > 0)
                hw = args.get(0);
            
            hw = getClosestHWDate();

            Homework homework = Conlin.homeworks.get(hw);

            if (homework == null) {
                event.getChannel().sendMessage("Homework not found").queue();
                return;
            }

            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle("Homework for " + hw, Conlin.getUrl(Conlin.getMonth() + Conlin.getYear()));

            eb.setColor(Color.PINK);

            eb.setDescription(homework.assignment);

            eb.addField("Homework Number", Integer.toString(homework.number), true);
            eb.addField("Section", homework.section, true);

            eb.setAuthor("John Conlin", "http://mvhs-fuhsd.org/john_conlin/");

            eb.setFooter("🍞 Bread 👍");

            event.getChannel().sendMessage(eb.build()).queue();
        } catch (Exception e) {
            event.getChannel().sendMessage("Homework not found").queue();
        }
    }

    private void answers(MessageReceivedEvent event) {
        event.getChannel().sendMessage("https://drive.google.com/drive/u/2/folders/1iw7QwFqWgbEOwm6SIt_fJO-Tp0Qvvroo")
                .queue();
    }

    private void lecky(MessageReceivedEvent event) {
        event.getChannel().sendMessage("http://www.chaoticgolf.com/tutorials_calc_aahs.html").queue();
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

        event.getChannel().sendMessage(eb.build()).queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;

        String content = event.getMessage().getContentRaw();

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
                    event.getChannel().sendMessage("Updated problems!");
                } catch (Exception e) {
                    event.getChannel().sendMessage("Error: " + e);
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
        }
    }
}
