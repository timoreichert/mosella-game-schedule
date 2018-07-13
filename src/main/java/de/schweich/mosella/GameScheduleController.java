package de.schweich.mosella;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameScheduleController {

    //📅⌚
    
    @RequestMapping("/next-games")
    public String nextGames() {
        StringWriter out = new StringWriter();
        String spec = "http://www.fussball.de/ajax.club.next.games/-/id/00ES8GNB78000065VV0AG08LVUPGND5I";
        try (InputStream in = new URL(spec).openStream()) {
            Elements matchplan = Jsoup
                    .parse(in, "UTF-8", "")
                    .select(".club-matchplan-table")
                    .select("table>tbody>tr");
            matchplan.forEach(e -> {
                if (e.hasClass("row-headline")) {
                    //System.out.println(e.select("td").text());
                } else if (e.hasClass("row-competition")) {
                    if(e.select(".column-date").text().contains("|")){
                        out.write("<h2>📅 " + e.select(".column-date").text().replaceAll("\\s\\|{1}\\s", "</h2>\n<pre>⌚ "));
                    }else{
                        out.write("<pre>⌚ <time>" + e.select(".column-date").text() + "</time>");
                    }
                    out.write(" | " + e.select(".column-team>a").text() + '\n');
                } else {
                    e.select("td").forEach((c -> {
                        if (c.hasClass("column-club")) {
                            out.write(c.select(".club-name").text());
                        } else if (c.hasClass("column-colon")) {
                            out.write(" " + c.text() + " ");
                        } else if (c.hasClass("column-score")) {
                            out.write('\n');
                        }
                    }));
                    out.write("</pre>\n\n");
                    
                }
            });
        } catch (IOException e) {
            Logger.getLogger("MOSELLA").log(Level.SEVERE, null, e);
        }
        out.flush();
        return out.toString();
    }
}