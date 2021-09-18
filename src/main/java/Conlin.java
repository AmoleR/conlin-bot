import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import io.github.furstenheim.CopyDown;

public class Conlin {
    public static HashMap<String, Homework> homeworks = new HashMap<String, Homework>();

    public static String getMonth() {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));

        switch (now.get(Calendar.MONTH)) {
            case Calendar.AUGUST:
                return "AugSept";
            case Calendar.SEPTEMBER:
                return "AugSept";
            case Calendar.OCTOBER:
                return "Oct";
            case Calendar.NOVEMBER:
                return "Nov";
            case Calendar.DECEMBER:
                return "Dec";
            case Calendar.JANUARY:
                return "Jan";
            case Calendar.FEBRUARY:
                return "Feb";
            case Calendar.MARCH:
                return "March";
            case Calendar.APRIL:
                return "April";
            case Calendar.MAY:
                return "May";
            case Calendar.JUNE:
                return "June";
        }

        return "Conlin website bad";
    }

    public static String getYear() {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));

        int year = now.get(Calendar.YEAR);

        return Integer.toString(year).substring(2);
    }

    public static String getCurrentYears() {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));

        int year = now.get(Calendar.YEAR);

        if (now.get(Calendar.MONTH) > Calendar.JULY)
            return Integer.toString(year).substring(2) + "-" + Integer.toString(year + 1).substring(2);

        return Integer.toString(year - 1).substring(2) + "-" + Integer.toString(year).substring(2);
    }

    private static StringBuffer extractContent(InputStream istream) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(istream));
        String inputLine;
        StringBuffer content = new StringBuffer();

        while ((inputLine = in.readLine()) != null)
            content.append(inputLine);

        in.close();

        return content;
    }

    private static String parseHTML(String html) {
        html = html.replaceAll("\\*", "\\\\*");
        html = html.replaceAll("\\.\\.", getBaseUrl());
        return html;
    }

    public static String getBaseUrl() {
        return "http://mvhs-fuhsd.org/john_conlin/CalcBC/HW_folder_BC";
    }

    public static String getUrl(String month) {
        return getBaseUrl() + "/HW_" + getCurrentYears() + "/BC_" + month + ".html";
    }

    public static void getHomework(String month) throws Exception {
        URL url = new URL(getUrl(month));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // If this is < 300, that means things worked out well. If it's anything
        // greater, it means something went wrong.
        int status = conn.getResponseCode();

        if (status >= 300) {
            StringBuffer error = extractContent(conn.getErrorStream());
            conn.disconnect();

            throw new Exception("Website error: " + error);
        }

        StringBuffer content = extractContent(conn.getInputStream());

        conn.disconnect();

        Document doc = Jsoup.parse(content.toString());
        Elements rows = doc.select("tr");

        for (Element row : rows) {
            String firstTD = row.child(0).toString();
            if (firstTD.contains("<b>"))
                row.remove();
            else if (firstTD.contains("MVHS<br> Main Page"))
                row.remove();
            else if (firstTD.contains("&nbsp;"))
                row.remove();
            else {
                Elements rowComponents = row.children();

                List<String> dates = Arrays.asList(rowComponents.get(1).select("div").first().text().split(" "));

                int hwNumber = 0;

                try {
                    Matcher matcher = Pattern.compile("\\d+").matcher(rowComponents.get(2).text());
                    matcher.find();
                    hwNumber = Integer.valueOf(matcher.group());
                } catch (Exception _e) {
                    continue;
                }

                String sectionNumber = rowComponents.get(3).select("div").first().text();
                String assignment = (new CopyDown()).convert(parseHTML(rowComponents.get(4).html()));

                for (String date : dates) {
                    homeworks.put(date.trim(), new Homework(hwNumber, sectionNumber, assignment));
                }
            }
        }
    }
}
