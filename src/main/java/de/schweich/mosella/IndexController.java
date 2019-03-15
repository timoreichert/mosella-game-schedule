package de.schweich.mosella;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @RequestMapping("/")
    public String nextGames() {
        final StringWriter out = new StringWriter();
        out.write("<a href=\"/next-games\">Spielplan</a>");
        out.write("<a href=\"/table?team=S1\">Tabelle Senioren I</a>");
        out.write("<a href=\"/table?team=S2\">Tabelle Senioren II</a>");
        out.write("<a href=\"/table?team=S3\">Tabelle Senioren III</a>");
        out.flush();
        return out.toString();
    }
}
        