package de.schweich.mosella;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameScheduleController {

    @RequestMapping("/next-games")
    public String nextGames() {      
        StringWriter out = new StringWriter();
        
        out.write("<h2>Spielvorschau Abteilung Fußball KW " 
                + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
                +  "/"
                + Calendar.getInstance().get(Calendar.YEAR)
                + "</h2>");
        out.write("\n<p>Alle wichtigen Termine der kommenden Woche auf eine Blick</p>"
                + "\n\t<ul>"); 
        
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
                        
                        out.write("\n</ul>"
                                + "\n<h3>📅 " + e.select(".column-date").text().replaceAll("\\s\\|{1}\\s", "</h3>"
                                + "\n<ul style=\"margin-top:1rem;\">"
                                + "\n\t<li style=\"font-family: Lucida Console,monospace; margin-bottom:1rem;\">"));
                    }else{
                        out.write("\t<li style=\"font-family: Lucida Console,monospace; margin-bottom:1rem;\">" 
                                + e.select(".column-date").text());
                    }
                    out.write(" | " + e.select(".column-team>a").text());
                } else {
                    e.select("td").forEach((c -> {
                        if (c.hasClass("column-club")) {
                            if (!c.hasClass("no-border")) {
                                out.write("\t\t<br>");
                            }
                            out.write(c.select(".club-name").text());
                        } else if (c.hasClass("column-colon")) {
                            out.write(" " + c.text() + " ");
                        } else if (c.hasClass("column-score")) {
                            if (c.select("span").hasClass("info-text")) {
                                out.write("\n\t\t<br><b>❗❗❗"+c.select("span").text()+"❗❗❗</b>");
                            }
                            out.write('\n');
                        }
                    }));
                    out.write("\t</li>\n");
                }
                
            });
        } catch (IOException e) {
            Logger.getLogger("MOSELLA").log(Level.SEVERE, null, e);
        }
        out.flush();
        return out.toString();
    }
}
