package de.schweich.mosella;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeamTableController {

    Map<String, String> specMap = new HashMap<String, String>();

    public TeamTableController() {
        specMap.put("S1", "http://www.fussball.de/ajax.actual.table/-/staffel/023ULJ2P7000000DVS54898DVVG1IBJM-G");
        specMap.put("S2", "http://www.fussball.de/ajax.actual.table/-/staffel/023PRA3JT000000BVS54898EVV9PO6D7-G");
        specMap.put("S3", "http://www.fussball.de/ajax.actual.table/-/staffel/023PRCISIC000008VS54898EVV9PO6D7-G");
        
        specMap.put("A1", "http://www.fussball.de/ajax.actual.table/-/staffel/0241I065BG000004VS54898DVSE4SAI6-G");
        specMap.put("A2", "http://www.fussball.de/ajax.actual.table/-/staffel/023UV4OI98000009VS54898DVVG1IBJM-G");
        
        specMap.put("B1", "http://www.fussball.de/ajax.actual.table/-/staffel/023N1R87PG000008VS54898EVV49UB8O-G");
        specMap.put("B2", "http://www.fussball.de/ajax.actual.table/-/staffel/023UVA3HS000000LVS54898DVVG1IBJM-G");
        //specMap.put("B3", "http://www.fussball.de/ajax.actual.table/-/staffel/");
        
        specMap.put("C1", "http://www.fussball.de/ajax.actual.table/-/staffel/0240MVDNNS000004VS54898EVUVF00VA-G");
        
        //specMap.put("D1", "http://www.fussball.de/ajax.actual.table/-/staffel/0241I065BG000004VS54898DVSE4SAI6-G");
    }

    @RequestMapping("/table")
    public String table(@RequestParam String team) {
        StringWriter out = new StringWriter();
        String spec = specMap.get(team);
        try (InputStream in = new URL(spec).openStream()) {
            Elements table = Jsoup
                    .parse(in, "UTF-8", "")
                    .select("#fixture-league-tables");

            out.write("<table border=\"1\">");

            Elements header = table.select("table>thead>tr");
            out.write("<thead><tr>");
            header.forEach(row -> {
                row.select("th").forEach(col -> {
                    if(col.hasClass("column-large")){
                        out.write("<th>" + col.select(".column-large").text() + "</th>");
                    }else{
                        out.write("<th>" + col.select(".hidden-small").text() + "</th>");
                    }
                });
            });
            out.write("</tr></thead>");

            Elements body = table.select("table>tbody>tr");
            out.write("<tbody>");
            body.forEach(row -> {
                out.write("<tr>");
                row.select("td").forEach(col -> {
                    if (col.hasClass("column-icon")) {
                        
                    }else if (col.hasClass("column-club")) {
                        out.write("<td>" + col.select(".club-name").text() + "</td>");
                    } else if (col.hasClass("no-wrap")) {
                        out.write("<td class=\"no-wrap\">" + col.text() + "</td>");
                    } else {
                        out.write("<td>" + col.text() + "</td>");
                    }
                });
                out.write("</tr>");
            });
            out.write("</tbody>");
            out.write("</table>");
        } catch (IOException e) {
            Logger.getLogger("MOSELLA").log(Level.SEVERE, null, e);
        }
        out.flush();
        return out.toString();
    }

}
