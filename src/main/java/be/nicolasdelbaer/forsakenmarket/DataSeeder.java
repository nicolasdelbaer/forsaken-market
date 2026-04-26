package be.nicolasdelbaer.forsakenmarket;

import be.nicolasdelbaer.forsakenmarket.entities.Player;
import be.nicolasdelbaer.forsakenmarket.models.player.CreateUserDto;
import be.nicolasdelbaer.forsakenmarket.services.PlayerService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class DataSeeder {

    @Inject
    PlayerService playerService;

    private static final Logger log = Logger.getLogger(DataSeeder.class.getName());

    @Transactional
    public void seed(@Observes @Initialized(ApplicationScoped.class) Object init) {
        try {
            Player player;
            player = playerService.register(new CreateUserDto("nidel@gmail.com", "test", "Nidel", 1000));
            player = playerService.register(new CreateUserDto("foo@gmail.com", "test", "Foo", 1000));
            player = playerService.register(new CreateUserDto("bar@gmail.com", "test", "Bar", 1000));

            log.info("DataSeeder : Done");
        } catch (Exception e) {
            log.log(Level.SEVERE, "DataSeeder : erreur lors du seed des données.", e);
        }
    }

}
