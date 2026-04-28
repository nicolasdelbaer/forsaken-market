package be.nicolasdelbaer.forsakenmarket;

import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.entities.ItemCategory;
import be.nicolasdelbaer.forsakenmarket.entities.Player;
import be.nicolasdelbaer.forsakenmarket.enums.ItemRarity;
import be.nicolasdelbaer.forsakenmarket.models.player.CreateUserDto;
import be.nicolasdelbaer.forsakenmarket.repositories.ItemBlueprintRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.ItemCategoryRepository;
import be.nicolasdelbaer.forsakenmarket.services.PlayerService;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class DataSeeder {

    @Inject private PlayerService playerService;

    @Inject private ItemCategoryRepository itemCategoryRepository;
    @Inject private ItemBlueprintRepository itemBlueprintRepository;

    private static final Logger log = Logger.getLogger(DataSeeder.class.getName());

    @Transactional
    public void seed(@Observes @Priority(GameConfiguration.DataFeedPriority) @Initialized(ApplicationScoped.class) Object init) {
        try {
            Player player;
            player = playerService.register(new CreateUserDto("nidel@gmail.com", "test", "Nidel"));
            player = playerService.register(new CreateUserDto("foo@gmail.com", "test", "Foo"));
            player = playerService.register(new CreateUserDto("bar@gmail.com", "test", "Bar"));

            createCategories();

            log.info("DataSeeder : Done");
        } catch (Exception e) {
            log.log(Level.SEVERE, "DataSeeder : erreur lors du seed des données.", e);
        }
    }

    private void createCategories() {
        // ─── ROOT CATEGORIES ──────────────────────────────────────────────────────────
        ItemCategory weaponsCategory        = itemCategoryRepository.save(new ItemCategory( "Weapons",     null));
        ItemCategory protectionCategory     = itemCategoryRepository.save(new ItemCategory( "Protection",  null));
        ItemCategory relicsCategory         = itemCategoryRepository.save(new ItemCategory( "Relics",      null));
        ItemCategory knowledgeCategory      = itemCategoryRepository.save(new ItemCategory( "Knowledge",   null));
        ItemCategory vesselsCategory        = itemCategoryRepository.save(new ItemCategory( "Vessels",     null));
        ItemCategory jewelryCategory        = itemCategoryRepository.save(new ItemCategory( "Jewelry",     null));
        ItemCategory clothingCategory       = itemCategoryRepository.save(new ItemCategory( "Clothing",    null));
        ItemCategory toolsCategory          = itemCategoryRepository.save(new ItemCategory( "Tools",       null));
        ItemCategory trophiesCategory       = itemCategoryRepository.save(new ItemCategory( "Trophies",    null));
        ItemCategory curiositiesCategory    = itemCategoryRepository.save(new ItemCategory( "Curiosities", null));

        // ─── WEAPONS ──────────────────────────────────────────────────────────────────
        ItemCategory bladesCategory         = itemCategoryRepository.save(new ItemCategory( "Blades",         weaponsCategory));
        ItemCategory bluntWeaponsCategory   = itemCategoryRepository.save(new ItemCategory( "Blunt Weapons",  weaponsCategory));
        ItemCategory polearmsCategory       = itemCategoryRepository.save(new ItemCategory( "Polearms",       weaponsCategory));
        ItemCategory rangedWeaponsCategory  = itemCategoryRepository.save(new ItemCategory( "Ranged Weapons", weaponsCategory));
        ItemCategory assassinToolsCategory  = itemCategoryRepository.save(new ItemCategory( "Assassin Tools", weaponsCategory));

        // ─── PROTECTION ───────────────────────────────────────────────────────────────
        ItemCategory bodyArmorCategory      = itemCategoryRepository.save(new ItemCategory( "Body Armor",  protectionCategory));
        ItemCategory headgearCategory       = itemCategoryRepository.save(new ItemCategory( "Headgear",    protectionCategory));
        ItemCategory shieldsCategory        = itemCategoryRepository.save(new ItemCategory( "Shields",     protectionCategory));
        ItemCategory extremitiesCategory    = itemCategoryRepository.save(new ItemCategory( "Extremities", protectionCategory));

        // ─── RELICS ───────────────────────────────────────────────────────────────────
        ItemCategory bonesCategory          = itemCategoryRepository.save(new ItemCategory( "Bones",               relicsCategory));
        ItemCategory divineSymbolsCategory  = itemCategoryRepository.save(new ItemCategory( "Divine Symbols",      relicsCategory));
        ItemCategory ritualInstrCategory    = itemCategoryRepository.save(new ItemCategory( "Ritual Instruments",  relicsCategory));
        ItemCategory divineFragmentsCategory = itemCategoryRepository.save(new ItemCategory( "Divine Fragments",   relicsCategory));

        // ─── KNOWLEDGE ────────────────────────────────────────────────────────────────
        ItemCategory grimoiresCategory      = itemCategoryRepository.save(new ItemCategory( "Grimoires",            knowledgeCategory));
        ItemCategory scrollsCategory        = itemCategoryRepository.save(new ItemCategory( "Scrolls",              knowledgeCategory));
        ItemCategory tabletsCategory        = itemCategoryRepository.save(new ItemCategory( "Tablets",              knowledgeCategory));
        ItemCategory notebooksCategory      = itemCategoryRepository.save(new ItemCategory( "Notebooks & Journals", knowledgeCategory));

        // ─── VESSELS ──────────────────────────────────────────────────────────────────
        ItemCategory flasksVialsCategory    = itemCategoryRepository.save(new ItemCategory( "Flasks & Vials",      vesselsCategory));
        ItemCategory urnsAmphorasCategory   = itemCategoryRepository.save(new ItemCategory( "Urns & Amphoras",     vesselsCategory));
        ItemCategory mirrorsSurfacesCategory = itemCategoryRepository.save(new ItemCategory( "Mirrors & Surfaces", vesselsCategory));
        ItemCategory chestsBoxesCategory    = itemCategoryRepository.save(new ItemCategory( "Chests & Boxes",      vesselsCategory));

        // ─── JEWELRY ──────────────────────────────────────────────────────────────────
        ItemCategory ringsBandsCategory        = itemCategoryRepository.save(new ItemCategory( "Rings & Bands",         jewelryCategory));
        ItemCategory necklacesPendantsCategory = itemCategoryRepository.save(new ItemCategory( "Necklaces & Pendants",  jewelryCategory));
        ItemCategory braceletsCuffsCategory    = itemCategoryRepository.save(new ItemCategory( "Bracelets & Cuffs",     jewelryCategory));
        ItemCategory crownsDiademsCategory     = itemCategoryRepository.save(new ItemCategory( "Crowns & Diadems",      jewelryCategory));
        ItemCategory broochesPinsCategory      = itemCategoryRepository.save(new ItemCategory( "Brooches & Pins",       jewelryCategory));

        // ─── CLOTHING ─────────────────────────────────────────────────────────────────
        ItemCategory cloaksRobesCategory    = itemCategoryRepository.save(new ItemCategory( "Cloaks & Robes",   clothingCategory));
        ItemCategory hoodsVeilsCategory     = itemCategoryRepository.save(new ItemCategory( "Hoods & Veils",    clothingCategory));
        ItemCategory glovesCategory         = itemCategoryRepository.save(new ItemCategory( "Gloves",           clothingCategory));
        ItemCategory beltsBaldricCategory   = itemCategoryRepository.save(new ItemCategory( "Belts & Baldrics", clothingCategory));

        // ─── TOOLS ────────────────────────────────────────────────────────────────────
        ItemCategory medicalTortureCategory = itemCategoryRepository.save(new ItemCategory( "Medical & Torture",         toolsCategory));
        ItemCategory alchemistToolsCategory = itemCategoryRepository.save(new ItemCategory( "Alchemist's Tools",         toolsCategory));
        ItemCategory navigationCategory     = itemCategoryRepository.save(new ItemCategory( "Navigation",                toolsCategory));
        ItemCategory locksKeysCategory      = itemCategoryRepository.save(new ItemCategory( "Keys & Locks",              toolsCategory));
        ItemCategory measurementCategory    = itemCategoryRepository.save(new ItemCategory( "Measurement & Observation", toolsCategory));

        // ─── TROPHIES ─────────────────────────────────────────────────────────────────
        ItemCategory humanRemainsCategory    = itemCategoryRepository.save(new ItemCategory( "Human Remains",     trophiesCategory));
        ItemCategory nonHumanRemainsCategory = itemCategoryRepository.save(new ItemCategory( "Non-Human Remains", trophiesCategory));
        ItemCategory hidesFursCategory       = itemCategoryRepository.save(new ItemCategory( "Hides & Furs",      trophiesCategory));
        ItemCategory victoryTokensCategory   = itemCategoryRepository.save(new ItemCategory( "Victory Tokens",    trophiesCategory));

        // ─── CURIOSITIES ──────────────────────────────────────────────────────────────
        ItemCategory gamesEntertainCategory  = itemCategoryRepository.save(new ItemCategory( "Games & Entertainment",   curiositiesCategory));
        ItemCategory automatonsMechCategory  = itemCategoryRepository.save(new ItemCategory( "Automatons & Mechanisms", curiositiesCategory));
        ItemCategory fragmentsShardsCategory = itemCategoryRepository.save(new ItemCategory( "Fragments & Shards",      curiositiesCategory));
        ItemCategory naturaMortaCategory     = itemCategoryRepository.save(new ItemCategory( "Natura Morta",            curiositiesCategory));
        ItemCategory currenciesCategory      = itemCategoryRepository.save(new ItemCategory( "Currencies & Values",     curiositiesCategory));



        // ITEMS ITEMS ITEMS ITEMS
        // ─── MUNDANE (36) ─────────────────────────────────────────────────────────────
        // Blades
        itemBlueprintRepository.save(new ItemBlueprint("Notched Kitchen Knife",         "Used to cut bread, then rope, then something best left unnamed.",                              8,  "", ItemRarity.MUNDANE, bladesCategory));
        itemBlueprintRepository.save(new ItemBlueprint("Bent Iron Sword",               "It has never been straight. Neither has its owner.",                                          15, "", ItemRarity.MUNDANE, bladesCategory));
        itemBlueprintRepository.save(new ItemBlueprint("Snapped Sickle Blade",          "The handle is missing. The blade still holds an edge no one sharpened.",                      9,  "", ItemRarity.MUNDANE, bladesCategory));

        // Blunt Weapons
        itemBlueprintRepository.save(new ItemBlueprint("Dented Candlestick",            "Heavy enough to serve as a weapon. Light enough to pretend it was not.",                      10, "", ItemRarity.MUNDANE, bluntWeaponsCategory));
        itemBlueprintRepository.save(new ItemBlueprint("Chipped Stone Club",            "Older than the city. Older than the kingdom. Still functional.",                              12, "", ItemRarity.MUNDANE, bluntWeaponsCategory));

        // Body Armor
        itemBlueprintRepository.save(new ItemBlueprint("Patched Leather Jerkin",        "Three different people tried to mend it. None of them finished.",                             20, "", ItemRarity.MUNDANE, bodyArmorCategory));
        itemBlueprintRepository.save(new ItemBlueprint("Rusted Chain Shirt",            "Several rings are missing. The ones remaining argue about their responsibilities.",            25, "", ItemRarity.MUNDANE, bodyArmorCategory));

        // Headgear
        itemBlueprintRepository.save(new ItemBlueprint("Iron Pot Helm",                 "Never designed for battle. It served in one anyway.",                                         18, "", ItemRarity.MUNDANE, headgearCategory));
        itemBlueprintRepository.save(new ItemBlueprint("Worn Leather Hood",             "Smells of rain and decisions made too quickly.",                                              11, "", ItemRarity.MUNDANE, headgearCategory));

        // Shields
        itemBlueprintRepository.save(new ItemBlueprint("Scratched Wooden Shield",       "It has stopped things. Not all of them were weapons.",                                        19, "", ItemRarity.MUNDANE, shieldsCategory));

        // Bones
        itemBlueprintRepository.save(new ItemBlueprint("Unnamed Finger Bone",           "Someone kept it. Someone lost it. Now it is yours.",                                          5,  "", ItemRarity.MUNDANE, bonesCategory));
        itemBlueprintRepository.save(new ItemBlueprint("Cracked Molar",                 "It aches on rainy days.",                                                                     6,  "", ItemRarity.MUNDANE, bonesCategory));

        // Scrolls
        itemBlueprintRepository.save(new ItemBlueprint("Torn Map Fragment",             "It shows the eastern road. The road no longer exists.",                                       12, "", ItemRarity.MUNDANE, scrollsCategory));
        itemBlueprintRepository.save(new ItemBlueprint("Debt Contract, Unsigned",       "The creditor's name is scratched out. The amount is not.",                                    9,  "", ItemRarity.MUNDANE, scrollsCategory));

        // Notebooks & Journals
        itemBlueprintRepository.save(new ItemBlueprint("Water-Stained Diary",           "Written in a shaking hand. The entries stop abruptly.",                                       17, "", ItemRarity.MUNDANE, notebooksCategory));

        // Tablets
        itemBlueprintRepository.save(new ItemBlueprint("Clay Tablet Fragment",          "Part of a longer message. The rest is either lost or deliberately destroyed.",                 9,  "", ItemRarity.MUNDANE, tabletsCategory));

        // Flasks & Vials
        itemBlueprintRepository.save(new ItemBlueprint("Cloudy Phial",                  "The liquid inside has no odor. That is the concerning part.",                                  8,  "", ItemRarity.MUNDANE, flasksVialsCategory));
        itemBlueprintRepository.save(new ItemBlueprint("Empty Oil Flask",               "Smells of lavender and something that ate lavender.",                                          6,  "", ItemRarity.MUNDANE, flasksVialsCategory));

        // Urns & Amphoras
        itemBlueprintRepository.save(new ItemBlueprint("Cracked Clay Urn",              "Sealed with wax. The wax is very old. What it seals is older.",                               14, "", ItemRarity.MUNDANE, urnsAmphorasCategory));

        // Chests & Boxes
        itemBlueprintRepository.save(new ItemBlueprint("Splintered Wooden Box",         "The lock is broken. Whatever was inside left on its own.",                                    15, "", ItemRarity.MUNDANE, chestsBoxesCategory));

        // Rings & Bands
        itemBlueprintRepository.save(new ItemBlueprint("Bent Copper Ring",              "It left a green mark. The mark will not wash off.",                                            8,  "", ItemRarity.MUNDANE, ringsBandsCategory));

        // Necklaces & Pendants
        itemBlueprintRepository.save(new ItemBlueprint("Knotted Rope Necklace",         "Seven knots. Each one tied differently. Each one tight.",                                     7,  "", ItemRarity.MUNDANE, necklacesPendantsCategory));

        // Brooches & Pins
        itemBlueprintRepository.save(new ItemBlueprint("Tarnished Tin Brooch",          "The insignia is unfamiliar. The loyalty it represents, unclear.",                              7,  "", ItemRarity.MUNDANE, broochesPinsCategory));

        // Cloaks & Robes
        itemBlueprintRepository.save(new ItemBlueprint("Mud-Caked Travel Cloak",        "The mud has dried. The journey has not.",                                                     22, "", ItemRarity.MUNDANE, cloaksRobesCategory));

        // Hoods & Veils
        itemBlueprintRepository.save(new ItemBlueprint("Black Mourning Veil",           "Worn past the acceptable period of grief.",                                                   13, "", ItemRarity.MUNDANE, hoodsVeilsCategory));

        // Gloves
        itemBlueprintRepository.save(new ItemBlueprint("Mismatched Leather Gloves",     "Different sizes. Different origins. Both stained.",                                            9,  "", ItemRarity.MUNDANE, glovesCategory));

        // Alchemist's Tools
        itemBlueprintRepository.save(new ItemBlueprint("Chipped Ceramic Mortar",        "Whatever was ground in it left a permanent stain and a faint smell of sulfur.",               16, "", ItemRarity.MUNDANE, alchemistToolsCategory));

        // Keys & Locks
        itemBlueprintRepository.save(new ItemBlueprint("Rusted Iron Key",               "The lock it belongs to is either lost or very well hidden.",                                  11, "", ItemRarity.MUNDANE, locksKeysCategory));

        // Measurement & Observation
        itemBlueprintRepository.save(new ItemBlueprint("Cracked Hourglass",             "The sand runs, but not at the expected rate.",                                                18, "", ItemRarity.MUNDANE, measurementCategory));

        // Human Remains
        itemBlueprintRepository.save(new ItemBlueprint("Glass Eye",                     "It watches. That is all it does.",                                                            12, "", ItemRarity.MUNDANE, humanRemainsCategory));

        // Hides & Furs
        itemBlueprintRepository.save(new ItemBlueprint("Mangy Fox Pelt",                "Something fed on the original owner. Then something else fed on that.",                       11, "", ItemRarity.MUNDANE, hidesFursCategory));

        // Victory Tokens
        itemBlueprintRepository.save(new ItemBlueprint("Bent Tin Medal",                "Awarded for valor. Pawned for food.",                                                          8,  "", ItemRarity.MUNDANE, victoryTokensCategory));

        // Games & Entertainment
        itemBlueprintRepository.save(new ItemBlueprint("Loaded Ivory Die",              "It always rolls six. Always.",                                                                14, "", ItemRarity.MUNDANE, gamesEntertainCategory));

        // Natura Morta
        itemBlueprintRepository.save(new ItemBlueprint("Pressed Black Flower",          "It was alive once. It still smells like it remembers.",                                        7,  "", ItemRarity.MUNDANE, naturaMortaCategory));
        itemBlueprintRepository.save(new ItemBlueprint("Smooth River Stone",            "Carried too far from its river.",                                                              4,  "", ItemRarity.MUNDANE, naturaMortaCategory));

        // Currencies & Values
        itemBlueprintRepository.save(new ItemBlueprint("Foreign Copper Coin",           "No kingdom claims it. It spends anyway.",                                                      5,  "", ItemRarity.MUNDANE, currenciesCategory));

        // ─── TAINTED (17) ─────────────────────────────────────────────────────────────
        // Blades
        itemBlueprintRepository.save(new ItemBlueprint("Dagger That Warms",             "It is cold iron. It should not be warm.",                                                     85, "", ItemRarity.TAINTED, bladesCategory));

        // Bones
        itemBlueprintRepository.save(new ItemBlueprint("Skull of Unknown Proportions",  "The eye sockets are too large. The jaw too small. It fits no species on record.",            110, "", ItemRarity.TAINTED, bonesCategory));

        // Divine Symbols
        itemBlueprintRepository.save(new ItemBlueprint("Icon of a Nameless Saint",      "The prayer it inspires is in a language you do not speak. Your lips move anyway.",            90, "✝", ItemRarity.TAINTED, divineSymbolsCategory));

        // Ritual Instruments
        itemBlueprintRepository.save(new ItemBlueprint("Tarnished Chalice, Stained Dark","The stain predates the current owner by three centuries.",                                  105, "", ItemRarity.TAINTED, ritualInstrCategory));
        itemBlueprintRepository.save(new ItemBlueprint("Bell That Rings Alone",         "Only once, always at the same hour, always when no one expects it.",                         130, "", ItemRarity.TAINTED, ritualInstrCategory));

        // Grimoires
        itemBlueprintRepository.save(new ItemBlueprint("Annotated Spellbook",           "Every margin is annotated. The annotations contradict the text. The text contradicts itself.",120, "", ItemRarity.TAINTED, grimoiresCategory));

        // Scrolls
        itemBlueprintRepository.save(new ItemBlueprint("Contract Signed in Red",        "Both parties signed. One of them is no longer a party to anything.",                         130, "", ItemRarity.TAINTED, scrollsCategory));
        itemBlueprintRepository.save(new ItemBlueprint("Self-Completing Prophecy",      "Three predictions have already come true. Four remain.",                                     150, "", ItemRarity.TAINTED, scrollsCategory));

        // Flasks & Vials
        itemBlueprintRepository.save(new ItemBlueprint("Phial of Black Tears",          "The bottle was sealed. The tears still flow.",                                                95, "", ItemRarity.TAINTED, flasksVialsCategory));

        // Mirrors & Surfaces
        itemBlueprintRepository.save(new ItemBlueprint("Pocket Mirror, Delayed",        "The reflection follows one second behind. Exactly one second.",                               140, "", ItemRarity.TAINTED, mirrorsSurfacesCategory));

        // Chests & Boxes
        itemBlueprintRepository.save(new ItemBlueprint("Music Box That Plays Alone",    "The mechanism runs without winding. The song will become familiar.",                          160, "", ItemRarity.TAINTED, chestsBoxesCategory));

        // Rings & Bands
        itemBlueprintRepository.save(new ItemBlueprint("Whispering Band",               "It whispers at night. The words stay just below the threshold of understanding.",            140, "", ItemRarity.TAINTED, ringsBandsCategory));

        // Headgear
        itemBlueprintRepository.save(new ItemBlueprint("Mask of a Hanged Judge",        "Still bears the expression of a verdict being delivered.",                                    95, "", ItemRarity.TAINTED, headgearCategory));

        // Automatons & Mechanisms
        itemBlueprintRepository.save(new ItemBlueprint("Porcelain Doll, Moving Fingers","Only when no one is looking.",                                                               175, "", ItemRarity.TAINTED, automatonsMechCategory));

        // Navigation
        itemBlueprintRepository.save(new ItemBlueprint("Compass That Points Elsewhere", "Not north. Not any direction with a name.",                                                  130, "", ItemRarity.TAINTED, navigationCategory));

        // Games & Entertainment
        itemBlueprintRepository.save(new ItemBlueprint("Tarot Deck, Twenty-Three Cards","There should be twenty-two. The extra card is always the same card.",                         95, "", ItemRarity.TAINTED, gamesEntertainCategory));

        // Polearms
        itemBlueprintRepository.save(new ItemBlueprint("Ritual Scythe, Never Sharpened","It cuts regardless.",                                                                        185, "",  ItemRarity.TAINTED, polearmsCategory));

        // ─── CURSED (6) ───────────────────────────────────────────────────────────────
        itemBlueprintRepository.save(new ItemBlueprint("Skull of a Nameless King",      "His kingdom is gone. His name is gone. The skull retains something that refuses to follow.",  340, "", ItemRarity.CURSED, bonesCategory));
        itemBlueprintRepository.save(new ItemBlueprint("The Judge's Mirror",            "Shows the room correctly. The people in it, incorrectly.",                                   390, "", ItemRarity.CURSED, mirrorsSurfacesCategory));
        itemBlueprintRepository.save(new ItemBlueprint("Grimoire of the Third Covenant","Two covenants preceded it. Neither ended well. This one is still open.",                     420, "", ItemRarity.CURSED, grimoiresCategory));
        itemBlueprintRepository.save(new ItemBlueprint("Key to a Door That Moved",      "The door was catalogued. It has since relocated. The key still turns.",                      280, "", ItemRarity.CURSED, locksKeysCategory));
        itemBlueprintRepository.save(new ItemBlueprint("Icon of the Inverted Saint",    "The prayers work. The saint they reach is not the one depicted.",                            310, "", ItemRarity.CURSED, divineSymbolsCategory));
        itemBlueprintRepository.save(new ItemBlueprint("Garrote of the Last Executioner","The last man to use this died in his sleep. Peacefully. That is the strange part.",          450, "", ItemRarity.CURSED, assassinToolsCategory));

        // ─── FORSAKEN (1) ─────────────────────────────────────────────────────────────
        itemBlueprintRepository.save(new ItemBlueprint("The Eye of the Last Prophet",   "He saw the end of three kingdoms before he was silenced. The eye was not silenced with him.", 850, "", ItemRarity.FORSAKEN, divineFragmentsCategory));
    }

}
