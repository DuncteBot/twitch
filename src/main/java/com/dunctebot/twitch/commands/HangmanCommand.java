/*
 *     A twitch bot for personal use
 *     Copyright (C) 2021  Duncan "duncte123" Sterken
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.dunctebot.twitch.commands;

import com.dunctebot.twitch.AbstractCommand;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class HangmanCommand extends AbstractCommand {
    protected TIntList guessedLetters = new TIntArrayList();
    protected Set<String> blockedPlayers = new HashSet<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    /// <editor-fold desc="pokemon names" defaultstate="collapsed">
    private final String[] words = {
        "Bulbasaur",
        "Ivysaur",
        "Venusaur",
        "Charmander",
        "Charmeleon",
        "Charizard",
        "Squirtle",
        "Wartortle",
        "Blastoise",
        "Caterpie",
        "Metapod",
        "Butterfree",
        "Weedle",
        "Kakuna",
        "Beedrill",
        "Pidgey",
        "Pidgeotto",
        "Pidgeot",
        "Rattata",
        "Raticate",
        "Spearow",
        "Fearow",
        "Ekans",
        "Arbok",
        "Pikachu",
        "Raichu",
        "Sandshrew",
        "Sandslash",
        "Nidoran♀",
        "Nidorina",
        "Nidoqueen",
        "Nidoran♂",
        "Nidorino",
        "Nidoking",
        "Clefairy",
        "Clefable",
        "Vulpix",
        "Ninetales",
        "Jigglypuff",
        "Wigglytuff",
        "Zubat",
        "Golbat",
        "Oddish",
        "Gloom",
        "Vileplume",
        "Paras",
        "Parasect",
        "Venonat",
        "Venomoth",
        "Diglett",
        "Dugtrio",
        "Meowth",
        "Persian",
        "Psyduck",
        "Golduck",
        "Mankey",
        "Primeape",
        "Growlithe",
        "Arcanine",
        "Poliwag",
        "Poliwhirl",
        "Poliwrath",
        "Abra",
        "Kadabra",
        "Alakazam",
        "Machop",
        "Machoke",
        "Machamp",
        "Bellsprout",
        "Weepinbell",
        "Victreebel",
        "Tentacool",
        "Tentacruel",
        "Geodude",
        "Graveler",
        "Golem",
        "Ponyta",
        "Rapidash",
        "Slowpoke",
        "Slowbro",
        "Magnemite",
        "Magneton",
        "Farfetch'd",
        "Doduo",
        "Dodrio",
        "Seel",
        "Dewgong",
        "Grimer",
        "Muk",
        "Shellder",
        "Cloyster",
        "Gastly",
        "Haunter",
        "Gengar",
        "Onix",
        "Drowzee",
        "Hypno",
        "Krabby",
        "Kingler",
        "Voltorb",
        "Electrode",
        "Exeggcute",
        "Exeggutor",
        "Cubone",
        "Marowak",
        "Hitmonlee",
        "Hitmonchan",
        "Lickitung",
        "Koffing",
        "Weezing",
        "Rhyhorn",
        "Rhydon",
        "Chansey",
        "Tangela",
        "Kangaskhan",
        "Horsea",
        "Seadra",
        "Goldeen",
        "Seaking",
        "Staryu",
        "Starmie",
        "Mr. Mime",
        "Scyther",
        "Jynx",
        "Electabuzz",
        "Magmar",
        "Pinsir",
        "Tauros",
        "Magikarp",
        "Gyarados",
        "Lapras",
        "Ditto",
        "Eevee",
        "Vaporeon",
        "Jolteon",
        "Flareon",
        "Porygon",
        "Omanyte",
        "Omastar",
        "Kabuto",
        "Kabutops",
        "Aerodactyl",
        "Snorlax",
        "Articuno",
        "Zapdos",
        "Moltres",
        "Dratini",
        "Dragonair",
        "Dragonite",
        "Mewtwo",
        "Mew",
        "Chikorita",
        "Bayleef",
        "Meganium",
        "Cyndaquil",
        "Quilava",
        "Typhlosion",
        "Totodile",
        "Croconaw",
        "Feraligatr",
        "Sentret",
        "Furret",
        "Hoothoot",
        "Noctowl",
        "Ledyba",
        "Ledian",
        "Spinarak",
        "Ariados",
        "Crobat",
        "Chinchou",
        "Lanturn",
        "Pichu",
        "Cleffa",
        "Igglybuff",
        "Togepi",
        "Togetic",
        "Natu",
        "Xatu",
        "Mareep",
        "Flaaffy",
        "Ampharos",
        "Bellossom",
        "Marill",
        "Azumarill",
        "Sudowoodo",
        "Politoed",
        "Hoppip",
        "Skiploom",
        "Jumpluff",
        "Aipom",
        "Sunkern",
        "Sunflora",
        "Yanma",
        "Wooper",
        "Quagsire",
        "Espeon",
        "Umbreon",
        "Murkrow",
        "Slowking",
        "Misdreavus",
        "Unown",
        "Wobbuffet",
        "Girafarig",
        "Pineco",
        "Forretress",
        "Dunsparce",
        "Gligar",
        "Steelix",
        "Snubbull",
        "Granbull",
        "Qwilfish",
        "Scizor",
        "Shuckle",
        "Heracross",
        "Sneasel",
        "Teddiursa",
        "Ursaring",
        "Slugma",
        "Magcargo",
        "Swinub",
        "Piloswine",
        "Corsola",
        "Remoraid",
        "Octillery",
        "Delibird",
        "Mantine",
        "Skarmory",
        "Houndour",
        "Houndoom",
        "Kingdra",
        "Phanpy",
        "Donphan",
        "Porygon2",
        "Stantler",
        "Smeargle",
        "Tyrogue",
        "Hitmontop",
        "Smoochum",
        "Elekid",
        "Magby",
        "Miltank",
        "Blissey",
        "Raikou",
        "Entei",
        "Suicune",
        "Larvitar",
        "Pupitar",
        "Tyranitar",
        "Lugia",
        "Ho-Oh",
        "Celebi",
        "Treecko",
        "Grovyle",
        "Sceptile",
        "Torchic",
        "Combusken",
        "Blaziken",
        "Mudkip",
        "Marshtomp",
        "Swampert",
        "Poochyena",
        "Mightyena",
        "Zigzagoon",
        "Linoone",
        "Wurmple",
        "Silcoon",
        "Beautifly",
        "Cascoon",
        "Dustox",
        "Lotad",
        "Lombre",
        "Ludicolo",
        "Seedot",
        "Nuzleaf",
        "Shiftry",
        "Taillow",
        "Swellow",
        "Wingull",
        "Pelipper",
        "Ralts",
        "Kirlia",
        "Gardevoir",
        "Surskit",
        "Masquerain",
        "Shroomish",
        "Breloom",
        "Slakoth",
        "Vigoroth",
        "Slaking",
        "Nincada",
        "Ninjask",
        "Shedinja",
        "Whismur",
        "Loudred",
        "Exploud",
        "Makuhita",
        "Hariyama",
        "Azurill",
        "Nosepass",
        "Skitty",
        "Delcatty",
        "Sableye",
        "Mawile",
        "Aron",
        "Lairon",
        "Aggron",
        "Meditite",
        "Medicham",
        "Electrike",
        "Manectric",
        "Plusle",
        "Minun",
        "Volbeat",
        "Illumise",
        "Roselia",
        "Gulpin",
        "Swalot",
        "Carvanha",
        "Sharpedo",
        "Wailmer",
        "Wailord",
        "Numel",
        "Camerupt",
        "Torkoal",
        "Spoink",
        "Grumpig",
        "Spinda",
        "Trapinch",
        "Vibrava",
        "Flygon",
        "Cacnea",
        "Cacturne",
        "Swablu",
        "Altaria",
        "Zangoose",
        "Seviper",
        "Lunatone",
        "Solrock",
        "Barboach",
        "Whiscash",
        "Corphish",
        "Crawdaunt",
        "Baltoy",
        "Claydol",
        "Lileep",
        "Cradily",
        "Anorith",
        "Armaldo",
        "Feebas",
        "Milotic",
        "Castform",
        "Kecleon",
        "Shuppet",
        "Banette",
        "Duskull",
        "Dusclops",
        "Tropius",
        "Chimecho",
        "Absol",
        "Wynaut",
        "Snorunt",
        "Glalie",
        "Spheal",
        "Sealeo",
        "Walrein",
        "Clamperl",
        "Huntail",
        "Gorebyss",
        "Relicanth",
        "Luvdisc",
        "Bagon",
        "Shelgon",
        "Salamence",
        "Beldum",
        "Metang",
        "Metagross",
        "Regirock",
        "Regice",
        "Registeel",
        "Latias",
        "Latios",
        "Kyogre",
        "Groudon",
        "Rayquaza",
        "Jirachi",
        "Deoxys",
        "Turtwig",
        "Grotle",
        "Torterra",
        "Chimchar",
        "Monferno",
        "Infernape",
        "Piplup",
        "Prinplup",
        "Empoleon",
        "Starly",
        "Staravia",
        "Staraptor",
        "Bidoof",
        "Bibarel",
        "Kricketot",
        "Kricketune",
        "Shinx",
        "Luxio",
        "Luxray",
        "Budew",
        "Roserade",
        "Cranidos",
        "Rampardos",
        "Shieldon",
        "Bastiodon",
        "Burmy",
        "Wormadam",
        "Mothim",
        "Combee",
        "Vespiquen",
        "Pachirisu",
        "Buizel",
        "Floatzel",
        "Cherubi",
        "Cherrim",
        "Shellos",
        "Gastrodon",
        "Ambipom",
        "Drifloon",
        "Drifblim",
        "Buneary",
        "Lopunny",
        "Mismagius",
        "Honchkrow",
        "Glameow",
        "Purugly",
        "Chingling",
        "Stunky",
        "Skuntank",
        "Bronzor",
        "Bronzong",
        "Bonsly",
        "Mime Jr.",
        "Happiny",
        "Chatot",
        "Spiritomb",
        "Gible",
        "Gabite",
        "Garchomp",
        "Munchlax",
        "Riolu",
        "Lucario",
        "Hippopotas",
        "Hippowdon",
        "Skorupi",
        "Drapion",
        "Croagunk",
        "Toxicroak",
        "Carnivine",
        "Finneon",
        "Lumineon",
        "Mantyke",
        "Snover",
        "Abomasnow",
        "Weavile",
        "Magnezone",
        "Lickilicky",
        "Rhyperior",
        "Tangrowth",
        "Electivire",
        "Magmortar",
        "Togekiss",
        "Yanmega",
        "Leafeon",
        "Glaceon",
        "Gliscor",
        "Mamoswine",
        "Porygon-Z",
        "Gallade",
        "Probopass",
        "Dusknoir",
        "Froslass",
        "Rotom",
        "Uxie",
        "Mesprit",
        "Azelf",
        "Dialga",
        "Palkia",
        "Heatran",
        "Regigigas",
        "Giratina",
        "Cresselia",
        "Phione",
        "Manaphy",
        "Darkrai",
        "Shaymin",
        "Arceus",
        "Victini",
        "Snivy",
        "Servine",
        "Serperior",
        "Tepig",
        "Pignite",
        "Emboar",
        "Oshawott",
        "Dewott",
        "Samurott",
        "Patrat",
        "Watchog",
        "Lillipup",
        "Herdier",
        "Stoutland",
        "Purrloin",
        "Liepard",
        "Pansage",
        "Simisage",
        "Pansear",
        "Simisear",
        "Panpour",
        "Simipour",
        "Munna",
        "Musharna",
        "Pidove",
        "Tranquill",
        "Unfezant",
        "Blitzle",
        "Zebstrika",
        "Roggenrola",
        "Boldore",
        "Gigalith",
        "Woobat",
        "Swoobat",
        "Drilbur",
        "Excadrill",
        "Audino",
        "Timburr",
        "Gurdurr",
        "Conkeldurr",
        "Tympole",
        "Palpitoad",
        "Seismitoad",
        "Throh",
        "Sawk",
        "Sewaddle",
        "Swadloon",
        "Leavanny",
        "Venipede",
        "Whirlipede",
        "Scolipede",
        "Cottonee",
        "Whimsicott",
        "Petilil",
        "Lilligant",
        "Basculin",
        "Sandile",
        "Krokorok",
        "Krookodile",
        "Darumaka",
        "Darmanitan",
        "Maractus",
        "Dwebble",
        "Crustle",
        "Scraggy",
        "Scrafty",
        "Sigilyph",
        "Yamask",
        "Cofagrigus",
        "Tirtouga",
        "Carracosta",
        "Archen",
        "Archeops",
        "Trubbish",
        "Garbodor",
        "Zorua",
        "Zoroark",
        "Minccino",
        "Cinccino",
        "Gothita",
        "Gothorita",
        "Gothitelle",
        "Solosis",
        "Duosion",
        "Reuniclus",
        "Ducklett",
        "Swanna",
        "Vanillite",
        "Vanillish",
        "Vanilluxe",
        "Deerling",
        "Sawsbuck",
        "Emolga",
        "Karrablast",
        "Escavalier",
        "Foongus",
        "Amoonguss",
        "Frillish",
        "Jellicent",
        "Alomomola",
        "Joltik",
        "Galvantula",
        "Ferroseed",
        "Ferrothorn",
        "Klink",
        "Klang",
        "Klinklang",
        "Tynamo",
        "Eelektrik",
        "Eelektross",
        "Elgyem",
        "Beheeyem",
        "Litwick",
        "Lampent",
        "Chandelure",
        "Axew",
        "Fraxure",
        "Haxorus",
        "Cubchoo",
        "Beartic",
        "Cryogonal",
        "Shelmet",
        "Accelgor",
        "Stunfisk",
        "Mienfoo",
        "Mienshao",
        "Druddigon",
        "Golett",
        "Golurk",
        "Pawniard",
        "Bisharp",
        "Bouffalant",
        "Rufflet",
        "Braviary",
        "Vullaby",
        "Mandibuzz",
        "Heatmor",
        "Durant",
        "Deino",
        "Zweilous",
        "Hydreigon",
        "Larvesta",
        "Volcarona",
        "Cobalion",
        "Terrakion",
        "Virizion",
        "Tornadus",
        "Thundurus",
        "Reshiram",
        "Zekrom",
        "Landorus",
        "Kyurem",
        "Keldeo",
        "Meloetta",
        "Genesect",
        "Chespin",
        "Quilladin",
        "Chesnaught",
        "Fennekin",
        "Braixen",
        "Delphox",
        "Froakie",
        "Frogadier",
        "Greninja",
        "Bunnelby",
        "Diggersby",
        "Fletchling",
        "Fletchinder",
        "Talonflame",
        "Scatterbug",
        "Spewpa",
        "Vivillon",
        "Litleo",
        "Pyroar",
        "Flabébé",
        "Floette",
        "Florges",
        "Skiddo",
        "Gogoat",
        "Pancham",
        "Pangoro",
        "Furfrou",
        "Espurr",
        "Meowstic",
        "Honedge",
        "Doublade",
        "Aegislash",
        "Spritzee",
        "Aromatisse",
        "Swirlix",
        "Slurpuff",
        "Inkay",
        "Malamar",
        "Binacle",
        "Barbaracle",
        "Skrelp",
        "Dragalge",
        "Clauncher",
        "Clawitzer",
        "Helioptile",
        "Heliolisk",
        "Tyrunt",
        "Tyrantrum",
        "Amaura",
        "Aurorus",
        "Sylveon",
        "Hawlucha",
        "Dedenne",
        "Carbink",
        "Goomy",
        "Sliggoo",
        "Goodra",
        "Klefki",
        "Phantump",
        "Trevenant",
        "Pumpkaboo",
        "Gourgeist",
        "Bergmite",
        "Avalugg",
        "Noibat",
        "Noivern",
        "Xerneas",
        "Yveltal",
        "Zygarde",
        "Diancie",
        "Hoopa",
        "Volcanion",
        "Rowlet",
        "Dartrix",
        "Decidueye",
        "Litten",
        "Torracat",
        "Incineroar",
        "Popplio",
        "Brionne",
        "Primarina",
        "Pikipek",
        "Trumbeak",
        "Toucannon",
        "Yungoos",
        "Gumshoos",
        "Grubbin",
        "Charjabug",
        "Vikavolt",
        "Crabrawler",
        "Crabominable",
        "Oricorio",
        "Cutiefly",
        "Ribombee",
        "Rockruff",
        "Lycanroc",
        "Wishiwashi",
        "Mareanie",
        "Toxapex",
        "Mudbray",
        "Mudsdale",
        "Dewpider",
        "Araquanid",
        "Fomantis",
        "Lurantis",
        "Morelull",
        "Shiinotic",
        "Salandit",
        "Salazzle",
        "Stufful",
        "Bewear",
        "Bounsweet",
        "Steenee",
        "Tsareena",
        "Comfey",
        "Oranguru",
        "Passimian",
        "Wimpod",
        "Golisopod",
        "Sandygast",
        "Palossand",
        "Pyukumuku",
        "Type: Null",
        "Silvally",
        "Minior",
        "Komala",
        "Turtonator",
        "Togedemaru",
        "Mimikyu",
        "Bruxish",
        "Drampa",
        "Dhelmise",
        "Jangmo-o",
        "Hakamo-o",
        "Kommo-o",
        "Tapu Koko",
        "Tapu Lele",
        "Tapu Bulu",
        "Tapu Fini",
        "Cosmog",
        "Cosmoem",
        "Solgaleo",
        "Lunala",
        "Nihilego",
        "Buzzwole",
        "Pheromosa",
        "Xurkitree",
        "Celesteela",
        "Kartana",
        "Guzzlord",
        "Necrozma",
        "Magearna",
        "Marshadow",
        "Poipole",
        "Naganadel",
        "Stakataka",
        "Blacephalon",
        "Zeraora",
        "Meltan",
        "Melmetal",
        "Grookey",
        "Thwackey",
        "Rillaboom",
        "Scorbunny",
        "Raboot",
        "Cinderace",
        "Sobble",
        "Drizzile",
        "Inteleon",
        "Skwovet",
        "Greedent",
        "Rookidee",
        "Corvisquire",
        "Corviknight",
        "Blipbug",
        "Dottler",
        "Orbeetle",
        "Nickit",
        "Thievul",
        "Gossifleur",
        "Eldegoss",
        "Wooloo",
        "Dubwool",
        "Chewtle",
        "Drednaw",
        "Yamper",
        "Boltund",
        "Rolycoly",
        "Carkol",
        "Coalossal",
        "Applin",
        "Flapple",
        "Appletun",
        "Silicobra",
        "Sandaconda",
        "Cramorant",
        "Arrokuda",
        "Barraskewda",
        "Toxel",
        "Toxtricity",
        "Sizzlipede",
        "Centiskorch",
        "Clobbopus",
        "Grapploct",
        "Sinistea",
        "Polteageist",
        "Hatenna",
        "Hattrem",
        "Hatterene",
        "Impidimp",
        "Morgrem",
        "Grimmsnarl",
        "Obstagoon",
        "Perrserker",
        "Cursola",
        "Sirfetch'd",
        "Mr. Rime",
        "Runerigus",
        "Milcery",
        "Alcremie",
        "Falinks",
        "Pincurchin",
        "Snom",
        "Frosmoth",
        "Stonjourner",
        "Eiscue",
        "Indeedee",
        "Morpeko",
        "Cufant",
        "Copperajah",
        "Dracozolt",
        "Arctozolt",
        "Dracovish",
        "Arctovish",
        "Duraludon",
        "Dreepy",
        "Drakloak",
        "Dragapult",
        "Zacian",
        "Zamazenta",
        "Eternatus",
        "Kubfu",
        "Urshifu",
        "Zarude",
        "Regieleki",
        "Regidrago",
        "Glastrier",
        "Spectrier",
        "Calyrex",
    };
    private int currentWord = 0;
    /// </editor-fold>

    public HangmanCommand() {
        super("hangman");
    }

    @Override
    public void execute(ChannelMessageEvent event, List<String> args) {
        final boolean currentState = this.running.get();
        final TwitchChat chat = event.getTwitchChat();

        if (currentState) {
            event.reply(chat, "A hangman has already started, current letters guessed: " + this.generateDisplay());
            return;
        }

        this.running.set(true);
        this.guessedLetters = new TIntArrayList();
        this.currentWord = ThreadLocalRandom.current().nextInt(this.words.length);

        event.reply(chat, "Hangman started, use !guess to guess a letter or the entire word");
        event.reply(chat, this.generateDisplay());
        event.reply(chat, this.getCurrentWord());
    }

    protected String getCurrentWord() {
        return this.words[this.currentWord];
    }

    protected void resetGame() {
        this.running.set(false);
        this.blockedPlayers = new HashSet<>();
        this.guessedLetters = new TIntArrayList();
    }

    protected String generateDisplay() {
        final String word = this.getCurrentWord();

        return word.chars()
            .mapToObj(
                (i) -> this.guessedLetters.contains(Character.toLowerCase(i)) ? String.valueOf((char) i) : "_"
            )
            .collect(Collectors.joining(" "));
    }

    public static class GuessCommand extends AbstractCommand {
        private final HangmanCommand hangman;

        public GuessCommand(HangmanCommand hangman) {
            super("guess");

            this.hangman = hangman;
        }

        @Override
        public void execute(ChannelMessageEvent event, List<String> args) {
            if (args.isEmpty()) {
                return;
            }

            final String userName = event.getUser().getName();

            if (this.hangman.blockedPlayers.contains(userName)) {
                return;
            }

            final TwitchChat chat = event.getTwitchChat();
            final String guess = args.get(0).toLowerCase();
            final String currentWord = this.hangman.getCurrentWord().toLowerCase();

            // a word is guessed
            if (guess.length() > 1) {
                if (guess.equals(currentWord)) {
                    final String nonLowerWord = this.hangman.getCurrentWord();
                    event.reply(chat, "Correct, the word was " + nonLowerWord + "! crroolWee");
                    this.hangman.resetGame();
                } else {
                    this.hangman.blockedPlayers.add(userName);
                    event.reply(chat, "Nope that's not it crroolOof (blocked from playing until the next round)");
                }
                return;
            }

            final char guessedLetter = guess.charAt(0);

            if (this.hangman.guessedLetters.contains(guessedLetter)) {
                event.reply(chat, "That letter was already guessed crroolDerp");
                return;
            }

            if (currentWord.contains(guess)) {
                this.hangman.guessedLetters.add(guessedLetter);
            }

            final String display = this.hangman.generateDisplay();

            if (!display.contains("_")) {
                final String nonLowerWord = this.hangman.getCurrentWord();
                final int points = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, 100);

                event.reply(chat, "You won! crroolWee");
                event.reply(chat, "You guessed that the word was " + nonLowerWord + " and earned " + points + " points!");

                this.hangman.resetGame();
                return;
            }

            event.reply(chat, display);
        }
    }
}
