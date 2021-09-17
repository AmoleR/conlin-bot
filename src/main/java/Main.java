import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Main {
    public static void main(String[] args) throws Exception {
        JDA api = JDABuilder.createDefault("ODg4MjIyNDAzNTcyOTQ5MDIz.YUPjkw.1X5fom8an8TSnB_s_nPgAJriwNk").build();

        Listener listener = new Listener();

        listener.update();

        api.addEventListener(listener);
    }
}