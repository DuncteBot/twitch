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
import com.dunctebot.twitch.db.Database;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import gnu.trove.impl.sync.TSynchronizedIntList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class HangmanCommand extends AbstractCommand {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final TwitchClient client;

    private final Set<String> onCooldown = new HashSet<>();
    private final TObjectIntMap<String> selectedWord = new TObjectIntHashMap<>();
    private final Map<String, TIntList> guessedLetters = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> blockedPlayers = new ConcurrentHashMap<>();
    private final Map<String, String> eastereggs = new HashMap<>();
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
        "Nidoran",
        "Nidorina",
        "Nidoqueen",
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
        "Flabebe",
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
        "Wyrdeer",
        "Kleavor",
        "Ursaluna",
        "Basculegion",
        "Sneasler",
        "Overquil",
        "Enamorous",
        "Sprigatito",
        "Floragato",
        "Moewscarade",
        "Fuecoco",
        "Crocolar",
        "Skeledirge",
        "Quaxly",
        "Quaxwell",
        "Quaquavel",
        "Lechonk",
        "Oinklogne",
        "Tarountula",
        "Spidops",
        "Nymble",
        "Lokix",
        "Pawmi",
        "Pawmo",
        "Pawmot",
        "Tandemaus",
        "Maushold",
        "Fidough",
        "Dachsbun",
        "Smoliv",
        "Dolliv",
        "Arboliva",
        "Squarkability",
        "Nacle",
        "Naclstack",
        "Garganacl",
        "Charcadet",
        "Armarouge",
        "Ceruledge",
        "Tadbulb",
        "Bellibolt",
        "Wattrel",
        "Kilowattrel",
        "Maschiff",
        "Mabosstiff",
        "Shroodle",
        "Grafaiai",
        "Bramblin",
        "Brambleghast",
        "Toadscool",
        "Toadscruel",
        "Klawf",
        "Capsakid",
        "Scovillain",
        "Rellor",
        "Rabsca",
        "Flittle",
        "Espathra",
        "Tinkatink",
        "Tinkatuff",
        "Tinkaton",
        "Wiglett",
        "Wugtrio",
        "Bombirdier",
        "Finizen",
        "Palafin",
        "Varoom",
        "Revavroom",
        "Cyclizar",
        "Orthworm",
        "Glimmet",
        "Glimmora",
        "Greavard",
        "Houndstone",
        "Flamigo",
        "Cetoddle",
        "Cetitan",
        "Veluza",
        "Dondozo",
        "Tatsugiri",
        "Annihilape",
        "Clodsire",
        "Farigiraf",
        "Dudunsparce",
        "Kingambit",
        "Great Tusk",
        "Scream Tail",
        "Brute Bonnet",
        "Flutter Mane",
        "Slither Wing",
        "Sandy Shocks",
        "Iron Treads",
        "Iron Bundle",
        "Iron Hands",
        "Iron Jugulis",
        "Iron Moth",
        "Iron Thorns",
        "Frigibax",
        "Artibax",
        "Baxcalibur",
        "Gimmighoul",
        "Gholdengo",
        "Wo-Chien",
        "Chein-Pao",
        "Ting-Lu",
        "Chi-Yu",
        "Roaring Moon",
        "Iron Valiant",
        "Koraidon",
        "Miraidon",
        "Walking Wake",
        "Iron Leaves",
    };
    /// </editor-fold>

    public HangmanCommand(TwitchClient client) {
        super("hangman");

        this.client = client;

        this.eastereggs.put("Shuckle", "(but actually a ditto)");
        this.eastereggs.put("Clefairy", "with a gun");
        this.eastereggs.put("Lanturn", "AKA service fish");
        this.eastereggs.put("Stufful", "AKA best boy!");
    }

    @Override
    public void execute(ChannelMessageEvent event, List<String> args) {
        final String channelName = event.getChannel().getName();
        final TwitchChat chat = event.getTwitchChat();

        if (!args.isEmpty()) {
            if ("reset".equals(args.get(0)) && event.getPermissions().contains(CommandPermission.BROADCASTER)) {
                event.reply(chat, "Game has been reset for channel");
                this.resetGame(channelName, false);
                return;
            }

            // Broke
            /*if ("cheat".equals(args.get(0))) {
                // :P
                this.client.getChat().sendPrivateMessage(event.getUser().getName(), "the word");
                event.reply(chat, "I've DM'd you the word");
                return;
            }*/
        }

        if (this.onCooldown.contains(channelName)) {
            event.reply(chat, "Hangman is currently on cooldown!");
            return;
        }

        if (this.selectedWord.containsKey(channelName)) {
            event.reply(chat, "Already running in this chat: " + this.generateDisplay(channelName));
            return;
        }

        this.blockedPlayers.put(channelName, new HashSet<>());
        final var guessList = new TSynchronizedIntList(new TIntArrayList(), new Object());
        this.guessedLetters.put(channelName, guessList);
        this.selectedWord.put(channelName, ThreadLocalRandom.current().nextInt(this.words.length));

        event.reply(chat, "Pokemon name hangman started, use !guess to guess a letter or the entire word");
        event.reply(chat, this.generateDisplay(channelName));
    }

    private void startCooldown(String channel) {
        this.onCooldown.add(channel);

        this.scheduler.schedule(() -> {
            this.onCooldown.remove(channel);
            this.client.getChat().sendMessage(channel, "Hangman is available again crroolHappy");
        }, 10L, TimeUnit.MINUTES);
    }

    private String getCurrentWord(String channel) {
        return this.words[this.selectedWord.get(channel)];
    }

    private String pkmn(String word) {
        if (this.eastereggs.containsKey(word)) {
            return word + ' ' + this.eastereggs.get(word);
        }

        return word;
    }

    private void resetGame(String channel, boolean doCooldown) {
        this.blockedPlayers.remove(channel);
        this.guessedLetters.remove(channel);
        this.selectedWord.remove(channel);

        if (doCooldown) {
            this.startCooldown(channel);
        } else {
            this.onCooldown.remove(channel);
        }
    }

    private String generateDisplay(String channel) {
        final String word = this.getCurrentWord(channel);
        final TIntList guesses = this.guessedLetters.get(channel);

        return word.chars()
            .mapToObj(
                (i) -> guesses.contains(Character.toLowerCase(i)) ? String.valueOf((char) i) : Character.isWhitespace(i)  ? "/" : "_"
            )
            .collect(Collectors.joining(" "));
    }

    public static class GuessCommand extends AbstractCommand {
        private final Database database = new Database();
        private final HangmanCommand hangman;

        public GuessCommand(HangmanCommand hangman) {
            super("guess");

            this.hangman = hangman;
        }

        private String pkmn(String word) {
            return this.hangman.pkmn(word);
        }

        private long countUnderscores(String withSpotsLeft) {
            return withSpotsLeft.chars().filter(c -> ((char) c) == '_').count();
        }

        // Remember, upper bound is exclusive
        private int getScore(String wordWithSpotsLeft) {
            final long undrescores = countUnderscores(wordWithSpotsLeft);
            final ThreadLocalRandom rng = ThreadLocalRandom.current();

            // lucky guess lol
            if (undrescores == wordWithSpotsLeft.length()) {
                return rng.nextInt(400, 601);
            }

            if (undrescores >= 8) {
                return rng.nextInt(150, 301);
            }

            if (undrescores >= 4) {
                return rng.nextInt(75, 151);
            }

            if (undrescores >= 2) {
                return rng.nextInt(50, 76);
            }

            return rng.nextInt(1, 15);
        }

        @Override
        public void execute(ChannelMessageEvent event, List<String> args) {
            final String channelName = event.getChannel().getName();

            if (args.isEmpty() || !this.hangman.selectedWord.containsKey(channelName)) {
                return;
            }


            final Set<String> blocks = this.hangman.blockedPlayers.get(channelName);
            final String userName = event.getUser().getName();

            if (blocks.contains(userName)) {
                return;
            }

            final TwitchChat chat = event.getTwitchChat();
            final String guess = String.join(" ", args).toLowerCase();
            final String nonLowerWord = this.hangman.getCurrentWord(channelName);
            final String currentWord = nonLowerWord.toLowerCase();

            // a word is guessed
            if (guess.length() > 1) {
                if (guess.equals(currentWord)) {
                    // can't be outside, will cause a bug
                    final String display = this.hangman.generateDisplay(channelName);
                    final int points = getScore(display);

                    this.database.addPoints(event.getUser().getId(), userName, points);
                    event.reply(chat, "Correct, the pokemon was %s! crroolHug you earned %d points! (check !leaderboard for your current points)".formatted(pkmn(nonLowerWord), points));
                    this.hangman.resetGame(channelName, true);
                } else {
                    blocks.add(userName);
                    event.reply(chat, "Nope that's not it crroolOof (blocked from playing until the next round)");
                }
                return;
            }

            final TIntList guesses = this.hangman.guessedLetters.get(channelName);
            final char guessedLetter = guess.charAt(0);

            if (guesses.contains(guessedLetter)) {
                event.reply(chat, "That letter was already guessed crroolDerp");
                return;
            }

            if (currentWord.contains(guess)) {
                guesses.add(guessedLetter);
            }

            final String display = this.hangman.generateDisplay(channelName);

            if (!display.contains("_")) {
                final int points = ThreadLocalRandom.current().nextInt(0, 10);

                this.database.addPoints(event.getUser().getId(), userName, points);
                event.reply(chat, "You won! crroolWee");
                event.reply(chat, "You guessed that the pokemon was " + pkmn(nonLowerWord) + " and earned " + points + " points! (check !leaderboard for your current points)");

                this.hangman.resetGame(channelName, true);
                return;
            }

            event.reply(chat, display);
        }
    }
}
