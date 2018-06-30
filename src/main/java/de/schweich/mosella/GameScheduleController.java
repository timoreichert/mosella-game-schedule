package de.schweich.mosella;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameScheduleController {

    @RequestMapping("/next-games")
    public String nextGames() {
        return "next games...";
    }
}