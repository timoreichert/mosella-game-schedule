package de.schweich.mosella;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class GameScheduleController {

    @RequestMapping("/next-games")
    public String nextGames() {

        DateFormat df = new SimpleDateFormat("YYYY-MM-dd", Locale.GERMANY);

        StringWriter out = new StringWriter();
        out.write("<h1>Spielvorschau Abteilung Fußball KW "
                + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
                + "/"
                + Calendar.getInstance().get(Calendar.YEAR)
                + "\n<br><small>Alle wichtigen Termine der kommenden Woche auf eine Blick</small>"
                + "</h1>");

        RestTemplate restTemplate = new RestTemplate();
        String spec = "http://www.fussball.de/ajax.club.matchplan/-/id/00ES8GNB78000065VV0AG08LVUPGND5I/mode/PAGE/show-filter/false"
                + "/max/{max}"
                + "/datum-von/{datum-von}"
                + "/datum-bis/{datum-bis}"
                + "/offset/{offset}"
                + "/show-venues/checked";

        Calendar cal = Calendar.getInstance(Locale.GERMANY);
        String from = df.format(cal.getTime());

        cal.add(Calendar.DAY_OF_MONTH, 7);
        String until = df.format(cal.getTime());

        String response = restTemplate.getForObject(spec, String.class, "60", from, until, "0");

        Elements matchplan = Jsoup
                .parse(response)
                .select("#id-club-matchplan-table")
                .select("div>table>tbody>tr");

        final String[] date = {""};
        matchplan.forEach((Element tr) -> {
            if (tr.hasClass("hidden-small")) {
                if (tr.hasClass("row-venue")) {
                    out.write("<br>🏟️ <small>" + tr.text() + "</small>");
                }
            } else {
                if (tr.hasClass("row-headline")) {
                    String[] headline = tr.select("td").text().split((" - "));
                    if (headline.length == 2) {
                        if (!date[0].contentEquals(headline[0])) {
                            out.write("\n<h2>📅 " + (date[0] = headline[0]) + "</h2>");
                        }
                        if (headline[1].matches("[0-9]{1,2}:[0-9]{1,2} Uhr.*")) {
                            out.write("\n<h3>⚽ " + headline[1] + "</h3>");
                        }
                    }
                } else {
                    if (tr.toString().toLowerCase().contains("spielfrei")) {
                        //skip
                    } else {
                        tr.select("td").forEach(td -> {
                            if (td.hasClass("column-club")) {
                                if (td.text().toLowerCase().contains("tus mosella schweich")) {
                                    out.write("<mark>" + td.text() + "</mark>");
                                } else {
                                    out.write(td.text());
                                }
                            } else if (td.hasClass("column-colon")) {
                                out.write(" 🆚 ");
                            }
                        });
                    }
                }
            }
        });
        out.flush();
        return out.toString();
    }
}
