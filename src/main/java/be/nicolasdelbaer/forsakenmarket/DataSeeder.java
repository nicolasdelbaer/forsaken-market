package be.nicolasdelbaer.forsakenmarket;

import at.favre.lib.crypto.bcrypt.BCrypt;
import be.nicolasdelbaer.forsakenmarket.entities.*;
import be.nicolasdelbaer.forsakenmarket.enums.ItemRarity;
import be.nicolasdelbaer.forsakenmarket.exceptions.core.CannotFindGameState;
import be.nicolasdelbaer.forsakenmarket.repositories.*;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import be.nicolasdelbaer.forsakenmarket.utils.MarketPriceUtils;
import be.nicolasdelbaer.forsakenmarket.utils.PricesCalculator;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    @Inject private PlayerRepository playerRepository;
    @Inject private PlayerRoleRepository playerRoleRepository;
    @Inject private GameStateRepository gameStateRepository;
    @Inject private ItemCategoryRepository itemCategoryRepository;
    @Inject private ItemBlueprintRepository itemBlueprintRepository;
    @Inject private EntityManagerFactory entityManagerFactory;
    @Inject private MarketPriceRepository marketPriceRepository;


    public void onStart(@Observes @Priority(GameConfiguration.DATA_FEED_PRIORITY) @Initialized(ApplicationScoped.class) Object init) {
        if("dev".equals(System.getenv("APP_ENV")))
            seed();
    }

    private void seed() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            try {
                gameStateRepository.getData(entityManager);
                return;
            } catch (CannotFindGameState ignored) {}

            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            try {
                createGameState(entityManager);
                createPlayers(entityManager);
                createCategories(entityManager);
                warmupMarketPrices(entityManager);
                log.info("DataSeeder : Done");
                entityTransaction.commit();
            } catch (Exception e) {
                log.error("DataSeeder : erreur lors du seed des données.", e);
                entityTransaction.rollback();
            }
        }
    }

    private void warmupMarketPrices(EntityManager entityManager) {
        List<ItemBlueprint> blueprintList = itemBlueprintRepository.findAll(entityManager);
        List<MarketPrice> marketPricesList = new ArrayList<>();
        for (ItemBlueprint blueprint : blueprintList) {
            marketPricesList.add(MarketPriceUtils.getMarketPrice(
                        blueprint,
                PricesCalculator.warmupPrice(blueprint),
                        1
            ));

        }
        marketPriceRepository.saveAll(entityManager, marketPricesList);
    }

    private void createGameState(EntityManager entityManager) {
        gameStateRepository.save(entityManager, new GameState(1L));
    }

    private void createPlayers(EntityManager entityManager) {
        PlayerRole playerRoleAdmin = playerRoleRepository.save(entityManager, new PlayerRole("Admin"));
        PlayerRole playerRoleMerchant = playerRoleRepository.save(entityManager, new PlayerRole("Merchant"));
        PlayerRole playerRoleNoble = playerRoleRepository.save(entityManager, new PlayerRole("Noble"));

        Player player;
        String envCost = System.getenv("BCRYPT_COST");
        String defaultPassword = System.getenv("DEFAULT_USER_PASSWORD");
        int cost = !(envCost == null) ? Integer.parseInt(envCost) : 12;
        player = new Player(
                "Nidel",
                "nidel@gmail.com",
                BCrypt.withDefaults().hashToString(cost, defaultPassword.toCharArray()),
                GameConfiguration.STARTING_WALLET,
                List.of(playerRoleMerchant, playerRoleAdmin)
        );
        playerRepository.save(entityManager, player);
        player = new Player(
                "Foo",
                "foo@gmail.com",
                BCrypt.withDefaults().hashToString(cost, defaultPassword.toCharArray()),
                GameConfiguration.STARTING_WALLET,
                List.of(playerRoleMerchant)
        );
        playerRepository.save(entityManager, player);
        player = new Player(
                "Bar",
                "bar@gmail.com",
                BCrypt.withDefaults().hashToString(cost, defaultPassword.toCharArray()),
                GameConfiguration.STARTING_WALLET,
                List.of(playerRoleMerchant, playerRoleNoble)
        );
        playerRepository.save(entityManager, player);
    }

    private void createCategories(EntityManager entityManager) {
        // ─── ROOT CATEGORIES ──────────────────────────────────────────────────────────
        ItemCategory weaponsCategory        = itemCategoryRepository.save(entityManager, new ItemCategory( "Weapons",     null));
        ItemCategory protectionCategory     = itemCategoryRepository.save(entityManager, new ItemCategory( "Protection",  null));
        ItemCategory relicsCategory         = itemCategoryRepository.save(entityManager, new ItemCategory( "Relics",      null));
        ItemCategory knowledgeCategory      = itemCategoryRepository.save(entityManager, new ItemCategory( "Knowledge",   null));
        ItemCategory vesselsCategory        = itemCategoryRepository.save(entityManager, new ItemCategory( "Vessels",     null));
        ItemCategory jewelryCategory        = itemCategoryRepository.save(entityManager, new ItemCategory( "Jewelry",     null));
        ItemCategory clothingCategory       = itemCategoryRepository.save(entityManager, new ItemCategory( "Clothing",    null));
        ItemCategory toolsCategory          = itemCategoryRepository.save(entityManager, new ItemCategory( "Tools",       null));
        ItemCategory trophiesCategory       = itemCategoryRepository.save(entityManager, new ItemCategory( "Trophies",    null));
        ItemCategory curiositiesCategory    = itemCategoryRepository.save(entityManager, new ItemCategory( "Curiosities", null));

        // ─── WEAPONS ──────────────────────────────────────────────────────────────────
        ItemCategory bladesCategory         = itemCategoryRepository.save(entityManager, new ItemCategory( "Blades",         weaponsCategory));
        ItemCategory bluntWeaponsCategory   = itemCategoryRepository.save(entityManager, new ItemCategory( "Blunt Weapons",  weaponsCategory));
        ItemCategory polearmsCategory       = itemCategoryRepository.save(entityManager, new ItemCategory( "Polearms",       weaponsCategory));
        ItemCategory rangedWeaponsCategory  = itemCategoryRepository.save(entityManager, new ItemCategory( "Ranged Weapons", weaponsCategory));
        ItemCategory assassinToolsCategory  = itemCategoryRepository.save(entityManager, new ItemCategory( "Assassin Tools", weaponsCategory));

        // ─── PROTECTION ───────────────────────────────────────────────────────────────
        ItemCategory bodyArmorCategory      = itemCategoryRepository.save(entityManager, new ItemCategory( "Body Armor",  protectionCategory));
        ItemCategory headgearCategory       = itemCategoryRepository.save(entityManager, new ItemCategory( "Headgear",    protectionCategory));
        ItemCategory shieldsCategory        = itemCategoryRepository.save(entityManager, new ItemCategory( "Shields",     protectionCategory));
        ItemCategory extremitiesCategory    = itemCategoryRepository.save(entityManager, new ItemCategory( "Extremities", protectionCategory));

        // ─── RELICS ───────────────────────────────────────────────────────────────────
        ItemCategory bonesCategory          = itemCategoryRepository.save(entityManager, new ItemCategory( "Bones",               relicsCategory));
        ItemCategory divineSymbolsCategory  = itemCategoryRepository.save(entityManager, new ItemCategory( "Divine Symbols",      relicsCategory));
        ItemCategory ritualInstrCategory    = itemCategoryRepository.save(entityManager, new ItemCategory( "Ritual Instruments",  relicsCategory));
        ItemCategory divineFragmentsCategory = itemCategoryRepository.save(entityManager, new ItemCategory( "Divine Fragments",   relicsCategory));

        // ─── KNOWLEDGE ────────────────────────────────────────────────────────────────
        ItemCategory grimoiresCategory      = itemCategoryRepository.save(entityManager, new ItemCategory( "Grimoires",            knowledgeCategory));
        ItemCategory scrollsCategory        = itemCategoryRepository.save(entityManager, new ItemCategory( "Scrolls",              knowledgeCategory));
        ItemCategory tabletsCategory        = itemCategoryRepository.save(entityManager, new ItemCategory( "Tablets",              knowledgeCategory));
        ItemCategory notebooksCategory      = itemCategoryRepository.save(entityManager, new ItemCategory( "Notebooks & Journals", knowledgeCategory));

        // ─── VESSELS ──────────────────────────────────────────────────────────────────
        ItemCategory flasksVialsCategory    = itemCategoryRepository.save(entityManager, new ItemCategory( "Flasks & Vials",      vesselsCategory));
        ItemCategory urnsAmphorasCategory   = itemCategoryRepository.save(entityManager, new ItemCategory( "Urns & Amphoras",     vesselsCategory));
        ItemCategory mirrorsSurfacesCategory = itemCategoryRepository.save(entityManager, new ItemCategory( "Mirrors & Surfaces", vesselsCategory));
        ItemCategory chestsBoxesCategory    = itemCategoryRepository.save(entityManager, new ItemCategory( "Chests & Boxes",      vesselsCategory));

        // ─── JEWELRY ──────────────────────────────────────────────────────────────────
        ItemCategory ringsBandsCategory        = itemCategoryRepository.save(entityManager, new ItemCategory( "Rings & Bands",         jewelryCategory));
        ItemCategory necklacesPendantsCategory = itemCategoryRepository.save(entityManager, new ItemCategory( "Necklaces & Pendants",  jewelryCategory));
        ItemCategory braceletsCuffsCategory    = itemCategoryRepository.save(entityManager, new ItemCategory( "Bracelets & Cuffs",     jewelryCategory));
        ItemCategory crownsDiademsCategory     = itemCategoryRepository.save(entityManager, new ItemCategory( "Crowns & Diadems",      jewelryCategory));
        ItemCategory broochesPinsCategory      = itemCategoryRepository.save(entityManager, new ItemCategory( "Brooches & Pins",       jewelryCategory));

        // ─── CLOTHING ─────────────────────────────────────────────────────────────────
        ItemCategory cloaksRobesCategory    = itemCategoryRepository.save(entityManager, new ItemCategory( "Cloaks & Robes",   clothingCategory));
        ItemCategory hoodsVeilsCategory     = itemCategoryRepository.save(entityManager, new ItemCategory( "Hoods & Veils",    clothingCategory));
        ItemCategory glovesCategory         = itemCategoryRepository.save(entityManager, new ItemCategory( "Gloves",           clothingCategory));
        ItemCategory beltsBaldricCategory   = itemCategoryRepository.save(entityManager, new ItemCategory( "Belts & Baldrics", clothingCategory));

        // ─── TOOLS ────────────────────────────────────────────────────────────────────
        ItemCategory medicalTortureCategory = itemCategoryRepository.save(entityManager, new ItemCategory( "Medical & Torture",         toolsCategory));
        ItemCategory alchemistToolsCategory = itemCategoryRepository.save(entityManager, new ItemCategory( "Alchemist's Tools",         toolsCategory));
        ItemCategory navigationCategory     = itemCategoryRepository.save(entityManager, new ItemCategory( "Navigation",                toolsCategory));
        ItemCategory locksKeysCategory      = itemCategoryRepository.save(entityManager, new ItemCategory( "Keys & Locks",              toolsCategory));
        ItemCategory measurementCategory    = itemCategoryRepository.save(entityManager, new ItemCategory( "Measurement & Observation", toolsCategory));

        // ─── TROPHIES ─────────────────────────────────────────────────────────────────
        ItemCategory humanRemainsCategory    = itemCategoryRepository.save(entityManager, new ItemCategory( "Human Remains",     trophiesCategory));
        ItemCategory nonHumanRemainsCategory = itemCategoryRepository.save(entityManager, new ItemCategory( "Non-Human Remains", trophiesCategory));
        ItemCategory hidesFursCategory       = itemCategoryRepository.save(entityManager, new ItemCategory( "Hides & Furs",      trophiesCategory));
        ItemCategory victoryTokensCategory   = itemCategoryRepository.save(entityManager, new ItemCategory( "Victory Tokens",    trophiesCategory));

        // ─── CURIOSITIES ──────────────────────────────────────────────────────────────
        ItemCategory gamesEntertainCategory  = itemCategoryRepository.save(entityManager, new ItemCategory( "Games & Entertainment",   curiositiesCategory));
        ItemCategory automatonsMechCategory  = itemCategoryRepository.save(entityManager, new ItemCategory( "Automatons & Mechanisms", curiositiesCategory));
        ItemCategory fragmentsShardsCategory = itemCategoryRepository.save(entityManager, new ItemCategory( "Fragments & Shards",      curiositiesCategory));
        ItemCategory naturaMortaCategory     = itemCategoryRepository.save(entityManager, new ItemCategory( "Natura Morta",            curiositiesCategory));
        ItemCategory currenciesCategory      = itemCategoryRepository.save(entityManager, new ItemCategory( "Currencies & Values",     curiositiesCategory));



        // ITEMS ITEMS ITEMS ITEMS
        // ─── MUNDANE (36) ─────────────────────────────────────────────────────────────
        // Blades
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Notched Kitchen Knife",         "Used to cut bread, then rope, then something best left unnamed.",                              8,  "", ItemRarity.MUNDANE, bladesCategory));
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Bent Iron Sword",               "It has never been straight. Neither has its owner.",                                          15, "", ItemRarity.MUNDANE, bladesCategory));
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Snapped Sickle Blade",          "The handle is missing. The blade still holds an edge no one sharpened.",                      9,  "", ItemRarity.MUNDANE, bladesCategory));

        // Blunt Weapons
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Dented Candlestick",            "Heavy enough to serve as a weapon. Light enough to pretend it was not.",                      10, "", ItemRarity.MUNDANE, bluntWeaponsCategory));
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Chipped Stone Club",            "Older than the city. Older than the kingdom. Still functional.",                              12, "", ItemRarity.MUNDANE, bluntWeaponsCategory));

        // Body Armor
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Patched Leather Jerkin",        "Three different people tried to mend it. None of them finished.",                             20, "", ItemRarity.MUNDANE, bodyArmorCategory));
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Rusted Chain Shirt",            "Several rings are missing. The ones remaining argue about their responsibilities.",            25, "", ItemRarity.MUNDANE, bodyArmorCategory));

        // Headgear
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Iron Pot Helm",                 "Never designed for battle. It served in one anyway.",                                         18, "", ItemRarity.MUNDANE, headgearCategory));
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Worn Leather Hood",             "Smells of rain and decisions made too quickly.",                                              11, "", ItemRarity.MUNDANE, headgearCategory));

        // Shields
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Scratched Wooden Shield",       "It has stopped things. Not all of them were weapons.",                                        19, "", ItemRarity.MUNDANE, shieldsCategory));

        // Bones
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Unnamed Finger Bone",           "Someone kept it. Someone lost it. Now it is yours.",                                          5,  "", ItemRarity.MUNDANE, bonesCategory));
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Cracked Molar",                 "It aches on rainy days.",                                                                     6,  "", ItemRarity.MUNDANE, bonesCategory));

        // Scrolls
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Torn Map Fragment",             "It shows the eastern road. The road no longer exists.",                                       12, "", ItemRarity.MUNDANE, scrollsCategory));
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Debt Contract, Unsigned",       "The creditor's name is scratched out. The amount is not.",                                    9,  "", ItemRarity.MUNDANE, scrollsCategory));

        // Notebooks & Journals
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Water-Stained Diary",           "Written in a shaking hand. The entries stop abruptly.",                                       17, "", ItemRarity.MUNDANE, notebooksCategory));

        // Tablets
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Clay Tablet Fragment",          "Part of a longer message. The rest is either lost or deliberately destroyed.",                 9,  "", ItemRarity.MUNDANE, tabletsCategory));

        // Flasks & Vials
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Cloudy Phial",                  "The liquid inside has no odor. That is the concerning part.",                                  8,  "", ItemRarity.MUNDANE, flasksVialsCategory));
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Empty Oil Flask",               "Smells of lavender and something that ate lavender.",                                          6,  "", ItemRarity.MUNDANE, flasksVialsCategory));

        // Urns & Amphoras
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Cracked Clay Urn",              "Sealed with wax. The wax is very old. What it seals is older.",                               14, "", ItemRarity.MUNDANE, urnsAmphorasCategory));

        // Chests & Boxes
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Splintered Wooden Box",         "The lock is broken. Whatever was inside left on its own.",                                    15, "", ItemRarity.MUNDANE, chestsBoxesCategory));

        // Rings & Bands
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Bent Copper Ring",              "It left a green mark. The mark will not wash off.",                                            8,  "", ItemRarity.MUNDANE, ringsBandsCategory));

        // Necklaces & Pendants
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Knotted Rope Necklace",         "Seven knots. Each one tied differently. Each one tight.",                                     7,  "", ItemRarity.MUNDANE, necklacesPendantsCategory));

        // Brooches & Pins
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Tarnished Tin Brooch",          "The insignia is unfamiliar. The loyalty it represents, unclear.",                              7,  "", ItemRarity.MUNDANE, broochesPinsCategory));

        // Cloaks & Robes
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Mud-Caked Travel Cloak",        "The mud has dried. The journey has not.",                                                     22, "", ItemRarity.MUNDANE, cloaksRobesCategory));

        // Hoods & Veils
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Black Mourning Veil",           "Worn past the acceptable period of grief.",                                                   13, "", ItemRarity.MUNDANE, hoodsVeilsCategory));

        // Gloves
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Mismatched Leather Gloves",     "Different sizes. Different origins. Both stained.",                                            9,  "", ItemRarity.MUNDANE, glovesCategory));

        // Alchemist's Tools
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Chipped Ceramic Mortar",        "Whatever was ground in it left a permanent stain and a faint smell of sulfur.",               16, "", ItemRarity.MUNDANE, alchemistToolsCategory));

        // Keys & Locks
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Rusted Iron Key",               "The lock it belongs to is either lost or very well hidden.",                                  11, "", ItemRarity.MUNDANE, locksKeysCategory));

        // Measurement & Observation
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Cracked Hourglass",             "The sand runs, but not at the expected rate.",                                                18, "", ItemRarity.MUNDANE, measurementCategory));

        // Human Remains
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Glass Eye",                     "It watches. That is all it does.",                                                            12, "", ItemRarity.MUNDANE, humanRemainsCategory));

        // Hides & Furs
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Mangy Fox Pelt",                "Something fed on the original owner. Then something else fed on that.",                       11, "", ItemRarity.MUNDANE, hidesFursCategory));

        // Victory Tokens
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Bent Tin Medal",                "Awarded for valor. Pawned for food.",                                                          8,  "", ItemRarity.MUNDANE, victoryTokensCategory));

        // Games & Entertainment
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Loaded Ivory Die",              "It always rolls six. Always.",                                                                14, "", ItemRarity.MUNDANE, gamesEntertainCategory));

        // Natura Morta
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Pressed Black Flower",          "It was alive once. It still smells like it remembers.",                                        7,  "", ItemRarity.MUNDANE, naturaMortaCategory));
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Smooth River Stone",            "Carried too far from its river.",                                                              4,  "", ItemRarity.MUNDANE, naturaMortaCategory));

        // Currencies & Values
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Foreign Copper Coin",           "No kingdom claims it. It spends anyway.",                                                      5,  "", ItemRarity.MUNDANE, currenciesCategory));

        // ─── TAINTED (17) ─────────────────────────────────────────────────────────────
        // Blades
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Dagger That Warms",             "It is cold iron. It should not be warm.",                                                     85, "", ItemRarity.TAINTED, bladesCategory));

        // Bones
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Skull of Unknown Proportions",  "The eye sockets are too large. The jaw too small. It fits no species on record.",            110, "", ItemRarity.TAINTED, bonesCategory));

        // Divine Symbols
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Icon of a Nameless Saint",      "The prayer it inspires is in a language you do not speak. Your lips move anyway.",            90, "✝", ItemRarity.TAINTED, divineSymbolsCategory));

        // Ritual Instruments
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Tarnished Chalice, Stained Dark","The stain predates the current owner by three centuries.",                                  105, "", ItemRarity.TAINTED, ritualInstrCategory));
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Bell That Rings Alone",         "Only once, always at the same hour, always when no one expects it.",                         130, "", ItemRarity.TAINTED, ritualInstrCategory));

        // Grimoires
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Annotated Spellbook",           "Every margin is annotated. The annotations contradict the text. The text contradicts itself.",120, "", ItemRarity.TAINTED, grimoiresCategory));

        // Scrolls
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Contract Signed in Red",        "Both parties signed. One of them is no longer a party to anything.",                         130, "", ItemRarity.TAINTED, scrollsCategory));
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Self-Completing Prophecy",      "Three predictions have already come true. Four remain.",                                     150, "", ItemRarity.TAINTED, scrollsCategory));

        // Flasks & Vials
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Phial of Black Tears",          "The bottle was sealed. The tears still flow.",                                                95, "", ItemRarity.TAINTED, flasksVialsCategory));

        // Mirrors & Surfaces
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Pocket Mirror, Delayed",        "The reflection follows one second behind. Exactly one second.",                               140, "", ItemRarity.TAINTED, mirrorsSurfacesCategory));

        // Chests & Boxes
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Music Box That Plays Alone",    "The mechanism runs without winding. The song will become familiar.",                          160, "", ItemRarity.TAINTED, chestsBoxesCategory));

        // Rings & Bands
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Whispering Band",               "It whispers at night. The words stay just below the threshold of understanding.",            140, "", ItemRarity.TAINTED, ringsBandsCategory));

        // Headgear
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Mask of a Hanged Judge",        "Still bears the expression of a verdict being delivered.",                                    95, "", ItemRarity.TAINTED, headgearCategory));

        // Automatons & Mechanisms
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Porcelain Doll, Moving Fingers","Only when no one is looking.",                                                               175, "", ItemRarity.TAINTED, automatonsMechCategory));

        // Navigation
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Compass That Points Elsewhere", "Not north. Not any direction with a name.",                                                  130, "", ItemRarity.TAINTED, navigationCategory));

        // Games & Entertainment
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Tarot Deck, Twenty-Three Cards","There should be twenty-two. The extra card is always the same card.",                         95, "", ItemRarity.TAINTED, gamesEntertainCategory));

        // Polearms
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Ritual Scythe, Never Sharpened","It cuts regardless.",                                                                        185, "",  ItemRarity.TAINTED, polearmsCategory));

        // ─── CURSED (6) ───────────────────────────────────────────────────────────────
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Skull of a Nameless King",      "His kingdom is gone. His name is gone. The skull retains something that refuses to follow.",  340, "", ItemRarity.CURSED, bonesCategory));
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("The Judge's Mirror",            "Shows the room correctly. The people in it, incorrectly.",                                   390, "", ItemRarity.CURSED, mirrorsSurfacesCategory));
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Grimoire of the Third Covenant","Two covenants preceded it. Neither ended well. This one is still open.",                     420, "", ItemRarity.CURSED, grimoiresCategory));
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Key to a Door That Moved",      "The door was catalogued. It has since relocated. The key still turns.",                      280, "", ItemRarity.CURSED, locksKeysCategory));
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Icon of the Inverted Saint",    "The prayers work. The saint they reach is not the one depicted.",                            310, "", ItemRarity.CURSED, divineSymbolsCategory));
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("Garrote of the Last Executioner","The last man to use this died in his sleep. Peacefully. That is the strange part.",          450, "", ItemRarity.CURSED, assassinToolsCategory));

        // ─── FORSAKEN (1) ─────────────────────────────────────────────────────────────
        itemBlueprintRepository.save(entityManager, new ItemBlueprint("The Eye of the Last Prophet",   "He saw the end of three kingdoms before he was silenced. The eye was not silenced with him.", 850, "", ItemRarity.FORSAKEN, divineFragmentsCategory));
    }

}
