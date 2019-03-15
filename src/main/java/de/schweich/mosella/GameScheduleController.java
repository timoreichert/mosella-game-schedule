package de.schweich.mosella;

import java.io.StringWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance(Locale.GERMANY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        StringWriter out = new StringWriter();
        out.write("<h2>Spielvorschau Abteilung Fußball KW " 
                + cal.get(Calendar.WEEK_OF_YEAR) + "/" + cal.get(Calendar.YEAR)
                + "</h2>");
        out.write("\n<p>Alle wichtigen Termine der kommenden Woche auf eine Blick</p>"
                + "\n\t<ul>"); 
        
        int dayFromMonday = (cal.get(Calendar.DAY_OF_WEEK) + 7 - Calendar.MONDAY) % 7;
        cal.add(Calendar.DATE, -dayFromMonday);
        String from = df.format(cal.getTime());
        cal.add(Calendar.DATE, 7);
        cal.add(Calendar.MILLISECOND, -1);
        String to = df.format(cal.getTime());

        String spec = "http://www.fussball.de/ajax.club.matchplan/-/"
                + "id/00ES8GNB78000065VV0AG08LVUPGND5I/"
                + "mime-type/HTML/"
                + "mode/PAGE/"
                + "show-filter/false/"
                + "max/50/"
                + "datum-von/"+from+"/"
                + "datum-bis/"+to+"/"
                + "offset/0";
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
                                + "\n\t<li><pre>"));
                    }else{
                        out.write("\t<li><pre>" 
                                + e.select(".column-date").text());
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
            });
        } catch (IOException e) {
            Logger.getLogger("MOSELLA").log(Level.SEVERE, null, e);
        }
        out.flush();
        return out.toString();
    }
}
