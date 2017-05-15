package com.inspirationlogical.receipt.corelib.utility;

import static java.time.LocalDateTime.now;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.corelib.model.adapter.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.model.entity.Address;
import com.inspirationlogical.receipt.corelib.model.entity.Client;
import com.inspirationlogical.receipt.corelib.model.entity.DailyClosure;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.Recipe;
import com.inspirationlogical.receipt.corelib.model.entity.Reservation;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.entity.VAT;
import com.inspirationlogical.receipt.corelib.model.entity.VATSerie;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.QuantityUnit;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.enums.VATName;
import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.security.Role;
import com.inspirationlogical.receipt.corelib.security.model.entity.AuthUser;

import lombok.Getter;
import org.hibernate.mapping.Bag;

public class BuildSchema  {

    private @Getter EntityManager entityManager;

    private @Getter Product productAdHoc;
    private @Getter Product productGameFee;

    /*----- ETLAP -----*/
    // Rágcsák
    private @Getter Product SosPerec;
    private @Getter Product Mogyoro;
    private @Getter Product RagcsaMix1Perec2Mogyi;
    private @Getter Product RagcsaMix2Perec1Mogyi;
    private @Getter Product RagcsaMix3Perec;
    private @Getter Product NachosSosSajt;
    private @Getter Product NachosSosChili;
    private @Getter Product NachosBBQSajt;
    private @Getter Product NachosBBQChili;
    private @Getter Product Chips;
    private @Getter Product Popcorn;
    private @Getter Product Gumicukor;
    private @Getter Product Balatonszelet;
    private @Getter Product Csoki;

    // Ételek
    private @Getter Product MelegszendivcsSonkas;
    private @Getter Product MelegszendivcsSzalamis;
    private @Getter Product MelegszendivcsVega;
    private @Getter Product SajtosCsikok;
    private @Getter Product ZsirosDeszka;
    private @Getter Product Wrap;
    private @Getter Product SpecialisFeltetekPiritossalTonhal;
    private @Getter Product SpecialisFeltetekPiritossalPadlizsan;
    private @Getter Product Sajttal;
    private @Getter Product GameUpTal;
    private @Getter Product GameUpFalankTal;

    // Menuk
    private @Getter Product AgentShotCover;
    private @Getter Product LimonCept;
    private @Getter Product SplendBor;
    private @Getter Product TatraTime;
    private @Getter Product SorrelAzEmberisegEllen;

    // Alapanyagok
    private @Getter Product Citrom;
    private @Getter Product Narancs;

    private @Getter Product MalnaSzirup;
    private @Getter Product EperNarancsSzirup;
    private @Getter Product CitrusSzirup;
    private @Getter Product MeggySzirup;

    private @Getter Product Tabasco;
    private @Getter Product BlueCuracao;

    private @Getter Product FagyiGyumi;

    private @Getter Product BarnaCukor;
    private @Getter Product FeherCukor;
    private @Getter Product Mez;

    private @Getter Product Tej;

    private @Getter Product TortillaSos;
    private @Getter Product TortillaBBQ;
    private @Getter Product NachosSajtszosz;
    private @Getter Product NachosChiliszosz;

    private @Getter Product Bagett;
    private @Getter Product Zsir;

    private @Getter Product Lilahagyma;
    private @Getter Product Pirospaprika;
    private @Getter Product Salata;
    private @Getter Product Paradicsom;
    private @Getter Product Uborka;
    private @Getter Product PiritottHagyma;

    private @Getter Product Trappista;
    private @Getter Product Cheddar;
    private @Getter Product Kecskesajt;
    private @Getter Product Camambert;

    private @Getter Product Sonka;
    private @Getter Product Szalami;
    private @Getter Product TonhalKrem;

    private @Getter Product Margarin;

    private @Getter Product Mustar;
    private @Getter Product Majonez;
    private @Getter Product Ajvar;

    private @Getter Product TortillaLap;
    
    /* ----- ITALLAP -----*/
    // Csapolt sorok
    private @Getter Product Soproni03;
    private @Getter Product Soproni05;
    private @Getter Product Edelweiss03;
    private @Getter Product Edelweiss05;

    // Uveges sorok
    private @Getter Product KrusoviceSvetle;
    private @Getter Product SoproniDemon;
    private @Getter Product SoproniMaxx;
    private @Getter Product Heineken;
    private @Getter Product GosserNaturRadler;
    private @Getter Product GosserNaturRadler00;
    private @Getter Product BekesSzentandrasiMeggyes;
    private @Getter Product StrongbowDarkfruit;
    private @Getter Product StrongbowGoldAppleCider;
    private @Getter Product Edelweiss;

    // Borok
    private @Getter Product HazBoraNagyEsNagy;
    private @Getter Product HilltopIrsaiOliver;
    private @Getter Product HilltopIrsaiOliverDecire;
    private @Getter Product GereAttilaOlaszrizling;

    private @Getter Product HazBoraLisiczaRoseCuvee;
    private @Getter Product MeszarosPinotNoirRose;
    private @Getter Product MeszarosPinotNoirRoseDecire;

    private @Getter Product HazBoraPolgarSerumVeritas;
    private @Getter Product VinczeMerlot;
    private @Getter Product VylyanCabernetSauvignon;
    private @Getter Product MeszarosHidasptereCabernetFrancReserve;

    // Fröccsök
    private @Getter Product HazBoraNagyEsNagyKisfroccs;
    private @Getter Product HazBoraNagyEsNagyNagyfroccs;
    private @Getter Product HazBoraNagyEsNagyVicehazmester;
    private @Getter Product HazBoraNagyEsNagyHazmester;
    private @Getter Product HazBoraNagyEsNagyHosszulepes;
    private @Getter Product HazBoraNagyEsNagySportFroccs;
    
    private @Getter Product HilltopIrsaiOliverKisfroccs;
    private @Getter Product HilltopIrsaiOliverNagyfroccs;
    private @Getter Product HilltopIrsaiOliverVicehazmester;
    private @Getter Product HilltopIrsaiOliverHazmester;
    private @Getter Product HilltopIrsaiOliverHosszulepes;
    private @Getter Product HilltopIrsaiOliverSportFroccs;
    
    private @Getter Product HazBoraLisiczaRoseCuveeKisfroccs;
    private @Getter Product HazBoraLisiczaRoseCuveeNagyfroccs;
    private @Getter Product HazBoraLisiczaRoseCuveeVicehazmester;
    private @Getter Product HazBoraLisiczaRoseCuveeHazmester;
    private @Getter Product HazBoraLisiczaRoseCuveeHosszulepes;
    private @Getter Product HazBoraLisiczaRoseCuveeSportFroccs;
    
    private @Getter Product MeszarosPinotNoirRoseKisfroccs;
    private @Getter Product MeszarosPinotNoirRoseNagyfroccs;
    private @Getter Product MeszarosPinotNoirRoseVicehazmester;
    private @Getter Product MeszarosPinotNoirRoseHazmester;
    private @Getter Product MeszarosPinotNoirRoseHosszulepes;
    private @Getter Product MeszarosPinotNoirRoseSportFroccs;
    
    
    // Pezsgok
    private @Getter Product TorleyGalaSzaraz;
    private @Getter Product TorleyCharmantEdes;

    // Roviditalok
    private @Getter Product JimBeam04;
    private @Getter Product JohnnieWalkerRedLabel04;
    private @Getter Product JackDaniels04;
    private @Getter Product TullamoreDew04;

    private @Getter Product Royal04;
    private @Getter Product Finlandia04;

    private @Getter Product BacardiSuperior04;
    private @Getter Product CaptainMorganSpicedRum04;

    private @Getter Product Beefeater04;

    private @Getter Product TequilaSierraReposadoGold04;
    private @Getter Product TequilaSierraSilver04;

    private @Getter Product Unicum04;
    private @Getter Product Jagermeister04;
    private @Getter Product Baileys08;

    private @Getter Product JimBeam02;
    private @Getter Product JohnnieWalkerRedLabel02;
    private @Getter Product JackDaniels02;
    private @Getter Product TullamoreDew02;

    private @Getter Product Royal02;
    private @Getter Product Finlandia02;

    private @Getter Product BacardiSuperior02;
    private @Getter Product CaptainMorganSpicedRum02;

    private @Getter Product Beefeater02;

    private @Getter Product TequilaSierraReposadoGold02;
    private @Getter Product TequilaSierraSilver02;

    private @Getter Product Unicum02;
    private @Getter Product Jagermeister02;
    private @Getter Product Baileys04;

    // Palinkak/
    private @Getter Product _22KokuszTatratea04;
    private @Getter Product _32CitrusTatratea04;
    private @Getter Product _42BarackTatratea04;
    private @Getter Product _52EredetiTatratea04;
    private @Getter Product _62ErdeiGyumolcsTatratea04;
    private @Getter Product _72OutlawBetyarTatratea04;

    private @Getter Product CseresznyePalinka04;
    private @Getter Product KajszibarackPalinka04;
    private @Getter Product Szilvapalinka04;

    private @Getter Product _22KokuszTatratea02;
    private @Getter Product _32CitrusTatratea02;
    private @Getter Product _42BarackTatratea02;
    private @Getter Product _52EredetiTatratea02;
    private @Getter Product _62ErdeiGyumolcsTatratea02;
    private @Getter Product _72OutlawBetyarTatratea02;

    private @Getter Product CseresznyePalinka02;
    private @Getter Product KajszibarackPalinka02;
    private @Getter Product Szilvapalinka02;

    // Shotok
    private @Getter Product Finca1;
    private @Getter Product Bang1;
    private @Getter Product Imagine1;

    private @Getter Product Finca6;
    private @Getter Product Bang6;
    private @Getter Product Imagine6;
    private @Getter Product RiffRaff6;

    private @Getter Product Finca12;
    private @Getter Product Bang12;
    private @Getter Product Imagine12;
    private @Getter Product RiffRaff12;

    // Uveges uditok
    private @Getter Product CocaCola;
    private @Getter Product CocaColaZero;
    private @Getter Product FantaNarancs;
    private @Getter Product Sprite;
    private @Getter Product KinleyGyomber;
    private @Getter Product KinleyTonic;
    private @Getter Product NesteaCitrom;
    private @Getter Product NesteaBarack;

    // Kimert uditok
    private @Getter Product CappyAlma;
    private @Getter Product CappyNarancs;
    private @Getter Product CappyBarack;
    private @Getter Product CappyAnanasz;

    // Limonadek
    private @Getter Product LimonadeMalna05;
    private @Getter Product LimonadeMeggy05;
    private @Getter Product LimonadeEperNarancs05;
    private @Getter Product LimonadeCitrus05;

    private @Getter Product LimonadeMalna10;
    private @Getter Product LimonadeMeggy10;
    private @Getter Product LimonadeEperNarancs10;
    private @Getter Product LimonadeCitrus10;

    // Asvanyviz
    private @Getter Product NaturaquaSzensavas;
    private @Getter Product NaturaquaSzensavmentes;
    private @Getter Product Szoda;

    // Energiaitalok
    private @Getter Product BurnOriginal;
    private @Getter Product BurnZero;
    private @Getter Product MonsterEnergy;
    private @Getter Product MonsterAssault;
    private @Getter Product MonsterRehab;

    // Filteres Teaak
    private @Getter Product DallmayrFekete;
    private @Getter Product DallmayrGyumolcs;
    private @Getter Product DallmayrZold;

    private @Getter Product PiramisDarjeling;
    private @Getter Product PiramisMangoMaracuja;
    private @Getter Product PiramisAnanaszPapaja;
    private @Getter Product PiramisCitrusVerbena;

    // Kavek
    private @Getter Product Espresso;
    private @Getter Product Americano;
    private @Getter Product Cappuccino;
    private @Getter Product CaffeLatte;
    private @Getter Product LatteMacchiato;
    private @Getter Product CaffeMelange;
    private @Getter Product ForroCsokiBarna;
    private @Getter Product ForroCsokiFeher;

    // Napi akciok
    private @Getter Product CaptainAndGyomber;
    private @Getter Product GinTonic;
    private @Getter Product JackAndCoke;
    private @Getter Product VodkaSzoda;
    
    private @Getter ProductCategory root;

    private @Getter ProductCategory etlap;
    private @Getter ProductCategory ragcsak;
    private @Getter ProductCategory etelek;
    private @Getter ProductCategory sos;
    private @Getter ProductCategory edes;

    private @Getter ProductCategory itallap;
    private @Getter ProductCategory shotok;
    private @Getter ProductCategory sorok;
    private @Getter ProductCategory csapolt;
    private @Getter ProductCategory uvegesSor;
    private @Getter ProductCategory borok;
    private @Getter ProductCategory uvegBor;
    private @Getter ProductCategory borDecire;
    private @Getter ProductCategory froccsok;
    private @Getter ProductCategory rovidek;
    private @Getter ProductCategory palinkak;
    private @Getter ProductCategory uditok;
    private @Getter ProductCategory uvegesUdito;
    private @Getter ProductCategory limo;
    private @Getter ProductCategory uditoDecire;
    private @Getter ProductCategory forroItalok;
    private @Getter ProductCategory tea;
    private @Getter ProductCategory filteres;
    private @Getter ProductCategory piramis;
    private @Getter ProductCategory forraltBor;
    private @Getter ProductCategory kave;
    private @Getter ProductCategory napiAkciok;

    private @Getter ProductCategory menuk;
    private @Getter ProductCategory menukAggregate;

    private @Getter ProductCategory alapanyagok;
    private @Getter ProductCategory alapanyagokAggregate;

    private @Getter ProductCategory pseudoAdHoc;
    private @Getter ProductCategory pseudoGameFee;

    // Rágcsák
    private @Getter ProductCategory PseudoSosPerec;
    private @Getter ProductCategory PseudoMogyoro;
    private @Getter ProductCategory PseudoRagcsaMix1Perec2Mogyi;
    private @Getter ProductCategory PseudoRagcsaMix2Perec1Mogyi;
    private @Getter ProductCategory PseudoRagcsaMix3Perec;
    private @Getter ProductCategory PseudoNachosSosSajt;
    private @Getter ProductCategory PseudoNachosSosChili;
    private @Getter ProductCategory PseudoNachosBBQSajt;
    private @Getter ProductCategory PseudoNachosBBQChili;
    private @Getter ProductCategory PseudoChips;
    private @Getter ProductCategory PseudoPopcorn;
    private @Getter ProductCategory PseudoGumicukor;
    private @Getter ProductCategory PseudoBalatonszelet;
    private @Getter ProductCategory PseudoCsoki;

    // Ételek
    private @Getter ProductCategory PseudoMelegszendivcsSonkas;
    private @Getter ProductCategory PseudoMelegszendivcsSzalamis;
    private @Getter ProductCategory PseudoMelegszendivcsVega;
    private @Getter ProductCategory PseudoSajtosCsikok;
    private @Getter ProductCategory PseudoZsirosDeszka;
    private @Getter ProductCategory PseudoWrap;
    private @Getter ProductCategory PseudoSpecialisFeltetekPiritossalTonhal;
    private @Getter ProductCategory PseudoSpecialisFeltetekPiritossalPadlizsan;
    private @Getter ProductCategory PseudoSajttal;
    private @Getter ProductCategory PseudoGameUpTal;
    private @Getter ProductCategory PseudoGameUpFalankTal;

    // Menuk
    private @Getter ProductCategory PseudoAgentShotCover;
    private @Getter ProductCategory PseudoLimonCept;
    private @Getter ProductCategory PseudoSplendBor;
    private @Getter ProductCategory PseudoTatraTime;
    private @Getter ProductCategory PseudoSorrelAzEmberisegEllen;

    // Alapanyagok
    private @Getter ProductCategory PseudoCitrom;
    private @Getter ProductCategory PseudoNarancs;

    private @Getter ProductCategory PseudoMalnaSzirup;
    private @Getter ProductCategory PseudoEperNarancsSzirup;
    private @Getter ProductCategory PseudoCitrusSzirup;
    private @Getter ProductCategory PseudoMeggySzirup;

    private @Getter ProductCategory PseudoTabasco;
    private @Getter ProductCategory PseudoBlueCuracao;

    private @Getter ProductCategory PseudoFagyiGyumi;

    private @Getter ProductCategory PseudoBarnaCukor;
    private @Getter ProductCategory PseudoFeherCukor;
    private @Getter ProductCategory PseudoMez;

    private @Getter ProductCategory PseudoTej;

    private @Getter ProductCategory PseudoTortillaSos;
    private @Getter ProductCategory PseudoTortillaBBQ;
    private @Getter ProductCategory PseudoNachosSajtszosz;
    private @Getter ProductCategory PseudoNachosChiliszosz;

    private @Getter ProductCategory PseudoBagett;
    private @Getter ProductCategory PseudoZsir;

    private @Getter ProductCategory PseudoLilahagyma;
    private @Getter ProductCategory PseudoPirospaprika;
    private @Getter ProductCategory PseudoSalata;
    private @Getter ProductCategory PseudoParadicsom;
    private @Getter ProductCategory PseudoUborka;
    private @Getter ProductCategory PseudoPiritottHagyma;

    private @Getter ProductCategory PseudoTrappista;
    private @Getter ProductCategory PseudoCheddar;
    private @Getter ProductCategory PseudoKecskesajt;
    private @Getter ProductCategory PseudoCamambert;

    private @Getter ProductCategory PseudoSonka;
    private @Getter ProductCategory PseudoSzalami;
    private @Getter ProductCategory PseudoTonhalKrem;

    private @Getter ProductCategory PseudoMargarin;

    private @Getter ProductCategory PseudoMustar;
    private @Getter ProductCategory PseudoMajonez;
    private @Getter ProductCategory PseudoAjvar;

    private @Getter ProductCategory PseudoTortillaLap;

    // Csapolt sorok
    private @Getter ProductCategory PseudoSoproni03;
    private @Getter ProductCategory PseudoSoproni05;
    private @Getter ProductCategory PseudoEdelweiss03;
    private @Getter ProductCategory PseudoEdelweiss05;

    // Uveges sorok
    private @Getter ProductCategory PseudoKrusoviceSvetle;
    private @Getter ProductCategory PseudoSoproniDemon;
    private @Getter ProductCategory PseudoSoproniMaxx;
    private @Getter ProductCategory PseudoHeineken;
    private @Getter ProductCategory PseudoGosserNaturRadler;
    private @Getter ProductCategory PseudoGosserNaturRadler00;
    private @Getter ProductCategory PseudoBekesSzentadrasiMeggyes;
    private @Getter ProductCategory PseudoStrongbowDarkfruit;
    private @Getter ProductCategory PseudoStrongbowGoldAppleCider;
    private @Getter ProductCategory PseudoEdelweiss;

    // Borok
    private @Getter ProductCategory PseudoHazBoraNagyEsNagy;
    private @Getter ProductCategory PseudoHilltopIrsaiOliver;
    private @Getter ProductCategory PseudoHilltopIrsaiOliverDecire;
    private @Getter ProductCategory PseudoGereAttilaOlaszrizling;

    private @Getter ProductCategory PseudoHazBoraLisiczaRoseCuvee;
    private @Getter ProductCategory PseudoMeszarosPinotNoireRose;
    private @Getter ProductCategory PseudoMeszarosPinotNoireRoseDecire;

    private @Getter ProductCategory PseudoHazBoraPolgarSerumVeritas;
    private @Getter ProductCategory PseudoVinczeMerlot;
    private @Getter ProductCategory PseudoVylyanCabernetSauvignon;
    private @Getter ProductCategory PseudoMeszarosHidasptereCabernetFrancReserve;

    // Fröccsök
    private @Getter ProductCategory PseudoHazBoraNagyEsNagyKisfroccs;
    private @Getter ProductCategory PseudoHazBoraNagyEsNagyNagyfroccs;
    private @Getter ProductCategory PseudoHazBoraNagyEsNagyVicehazmester;
    private @Getter ProductCategory PseudoHazBoraNagyEsNagyHazmester;
    private @Getter ProductCategory PseudoHazBoraNagyEsNagyHosszulepes;
    private @Getter ProductCategory PseudoHazBoraNagyEsNagySportFroccs;

    private @Getter ProductCategory PseudoHilltopIrsaiOliverKisfroccs;
    private @Getter ProductCategory PseudoHilltopIrsaiOliverNagyfroccs;
    private @Getter ProductCategory PseudoHilltopIrsaiOliverVicehazmester;
    private @Getter ProductCategory PseudoHilltopIrsaiOliverHazmester;
    private @Getter ProductCategory PseudoHilltopIrsaiOliverHosszulepes;
    private @Getter ProductCategory PseudoHilltopIrsaiOliverSportFroccs;

    private @Getter ProductCategory PseudoHazBoraLisiczaRoseCuveeKisfroccs;
    private @Getter ProductCategory PseudoHazBoraLisiczaRoseCuveeNagyfroccs;
    private @Getter ProductCategory PseudoHazBoraLisiczaRoseCuveeVicehazmester;
    private @Getter ProductCategory PseudoHazBoraLisiczaRoseCuveeHazmester;
    private @Getter ProductCategory PseudoHazBoraLisiczaRoseCuveeHosszulepes;
    private @Getter ProductCategory PseudoHazBoraLisiczaRoseCuveeSportFroccs;

    private @Getter ProductCategory PseudoMeszarosPinotNoirRoseKisfroccs;
    private @Getter ProductCategory PseudoMeszarosPinotNoirRoseNagyfroccs;
    private @Getter ProductCategory PseudoMeszarosPinotNoirRoseVicehazmester;
    private @Getter ProductCategory PseudoMeszarosPinotNoirRoseHazmester;
    private @Getter ProductCategory PseudoMeszarosPinotNoirRoseHosszulepes;
    private @Getter ProductCategory PseudoMeszarosPinotNoirRoseSportFroccs;
    
    // Pezsgok
    private @Getter ProductCategory PseudoTorleyGalaSzaraz;
    private @Getter ProductCategory PseudoTorleyCharmantEdes;

    // Roviditalok
    private @Getter ProductCategory PseudoJimBeam04;
    private @Getter ProductCategory PseudoJohnnieWalkerRedLabel04;
    private @Getter ProductCategory PseudoJackDaniels04;
    private @Getter ProductCategory PseudoTullamoreDew04;

    private @Getter ProductCategory PseudoRoyal04;
    private @Getter ProductCategory PseudoFinlandia04;

    private @Getter ProductCategory PseudoBacardiSuperior04;
    private @Getter ProductCategory PseudoCaptainMorganSpicedRum04;

    private @Getter ProductCategory PseudoBeefeater04;

    private @Getter ProductCategory PseudoTequilaSierraReposadoGold04;
    private @Getter ProductCategory PseudoTequilaSierraSilver04;

    private @Getter ProductCategory PseudoUnicum04;
    private @Getter ProductCategory PseudoJagermeister04;
    private @Getter ProductCategory PseudoBaileys08;

    private @Getter ProductCategory PseudoJimBeam02;
    private @Getter ProductCategory PseudoJohnnieWalkerRedLabel02;
    private @Getter ProductCategory PseudoJackDaniels02;
    private @Getter ProductCategory PseudoTullamoreDew02;

    private @Getter ProductCategory PseudoRoyal02;
    private @Getter ProductCategory PseudoFinlandia02;

    private @Getter ProductCategory PseudoBacardiSuperior02;
    private @Getter ProductCategory PseudoCaptainMorganSpicedRum02;

    private @Getter ProductCategory PseudoBeefeater02;

    private @Getter ProductCategory PseudoTequilaSierraReposadoGold02;
    private @Getter ProductCategory PseudoTequilaSierraSilver02;

    private @Getter ProductCategory PseudoUnicum02;
    private @Getter ProductCategory PseudoJagermeister02;
    private @Getter ProductCategory PseudoBaileys04;

    // Palinkak/
    private @Getter ProductCategory Pseudo_22KokuszTatratea04;
    private @Getter ProductCategory Pseudo_32CitrusTatratea04;
    private @Getter ProductCategory Pseudo_42BarackTatratea04;
    private @Getter ProductCategory Pseudo_52EredetiTatratea04;
    private @Getter ProductCategory Pseudo_62ErdeiGyumolcsTatratea04;
    private @Getter ProductCategory Pseudo_72OutlawBetyarTatratea04;

    private @Getter ProductCategory PseudoCseresznyePalinka04;
    private @Getter ProductCategory PseudoKajszibarackPalinka04;
    private @Getter ProductCategory PseudoSzilvapalinka04;

    private @Getter ProductCategory Pseudo_22KokuszTatratea02;
    private @Getter ProductCategory Pseudo_32CitrusTatratea02;
    private @Getter ProductCategory Pseudo_42BarackTatratea02;
    private @Getter ProductCategory Pseudo_52EredetiTatratea02;
    private @Getter ProductCategory Pseudo_62ErdeiGyumolcsTatratea02;
    private @Getter ProductCategory Pseudo_72OutlawBetyarTatratea02;

    private @Getter ProductCategory PseudoCseresznyePalinka02;
    private @Getter ProductCategory PseudoKajszibarackPalinka02;
    private @Getter ProductCategory PseudoSzilvapalinka02;

    // Shotok
    private @Getter ProductCategory PseudoFinca1;
    private @Getter ProductCategory PseudoBang1;
    private @Getter ProductCategory PseudoImagine1;

    private @Getter ProductCategory PseudoFinca6;
    private @Getter ProductCategory PseudoBang6;
    private @Getter ProductCategory PseudoImagine6;
    private @Getter ProductCategory PseudoRiffRaff6;

    private @Getter ProductCategory PseudoFinca12;
    private @Getter ProductCategory PseudoBang12;
    private @Getter ProductCategory PseudoImagine12;
    private @Getter ProductCategory PseudoRiffRaff12;

    // Uveges uditok
    private @Getter ProductCategory PseudoCocaCola;
    private @Getter ProductCategory PseudoCocaColaZero;
    private @Getter ProductCategory PseudoFantaNarancs;
    private @Getter ProductCategory PseudoSprite;
    private @Getter ProductCategory PseudoKinleyGyomber;
    private @Getter ProductCategory PseudoKinleyTonic;
    private @Getter ProductCategory PseudoNesteaCitrom;
    private @Getter ProductCategory PseudoNesteaBarack;

    // Kimert uditok
    private @Getter ProductCategory PseudoCappyAlma;
    private @Getter ProductCategory PseudoCappyNarancs;
    private @Getter ProductCategory PseudoCappyBarack;
    private @Getter ProductCategory PseudoCappyAnanasz;

    // Limonadek
    private @Getter ProductCategory PseudoLimonadeMalna05;
    private @Getter ProductCategory PseudoLimonadeMeggy05;
    private @Getter ProductCategory PseudoLimonadeEperNarancs05;
    private @Getter ProductCategory PseudoLimonadeCitrus05;

    private @Getter ProductCategory PseudoLimonadeMalna10;
    private @Getter ProductCategory PseudoLimonadeMeggy10;
    private @Getter ProductCategory PseudoLimonadeEperNarancs10;
    private @Getter ProductCategory PseudoLimonadeCitrus10;

    // Asvanyviz
    private @Getter ProductCategory PseudoNaturaquaSzensavas;
    private @Getter ProductCategory PseudoNaturaquaSzensavmentes;
    private @Getter ProductCategory PseudoSzoda;

    // Energiaitalok
    private @Getter ProductCategory PseudoBurnOriginal;
    private @Getter ProductCategory PseudoBurnZero;
    private @Getter ProductCategory PseudoMonsterEnergy;
    private @Getter ProductCategory PseudoMonsterAssault;
    private @Getter ProductCategory PseudoMonsterRehab;

    // Filteres Teaak
    private @Getter ProductCategory PseudoDallmyrFekete;
    private @Getter ProductCategory PseudoDallmyrGyumolcs;
    private @Getter ProductCategory PseudoDallmyrZold;

    private @Getter ProductCategory PseudoPiramis1;
    private @Getter ProductCategory PseudoPiramis2;
    private @Getter ProductCategory PseudoPiramis3;
    private @Getter ProductCategory PseudoPiramis4;

    // Kavek
    private @Getter ProductCategory PseudoEspresso;
    private @Getter ProductCategory PseudoAmericano;
    private @Getter ProductCategory PseudoCappuccino;
    private @Getter ProductCategory PseudoCaffeLatte;
    private @Getter ProductCategory PseudoLatteMacchiato;
    private @Getter ProductCategory PseudoCaffeMelange;
    private @Getter ProductCategory PseudoForroCsokiBarna;
    private @Getter ProductCategory PseudoForroCsokiFeher;

    // Napi akciok
    private @Getter ProductCategory PseudoCaptainAndGyomber;
    private @Getter ProductCategory PseudoGinTonic;
    private @Getter ProductCategory PseudoJackAndCoke;
    private @Getter ProductCategory PseudoVodkaSzoda;

    /* ----- ETLAP -----*/
    // Rágcsák
    private @Getter Recipe SosPerecRecipe1;
    private @Getter Recipe MogyoroRecipe1;

    private @Getter Recipe RagcsaMix1Perec2MogyiRecipe1;
    private @Getter Recipe RagcsaMix1Perec2MogyiRecipe2;

    private @Getter Recipe RagcsaMix2Perec1MogyiRecipe1;
    private @Getter Recipe RagcsaMix2Perec1MogyiRecipe2;

    private @Getter Recipe RagcsaMix3PerecRecipe1;

    private @Getter Recipe NachosSosSajtRecipe1;
    private @Getter Recipe NachosSosSajtRecipe2;

    private @Getter Recipe NachosSosChiliRecipe1;
    private @Getter Recipe NachosSosChiliRecipe2;

    private @Getter Recipe NachosBBQSajtRecipe1;
    private @Getter Recipe NachosBBQSajtRecipe2;

    private @Getter Recipe NachosBBQChiliRecipe1;
    private @Getter Recipe NachosBBQChiliRecipe2;

    private @Getter Recipe ChipsRecipe1;
    private @Getter Recipe PopcornRecipe1;
    private @Getter Recipe GumicukorRecipe1;
    private @Getter Recipe BalatonszeletRecipe1;
    private @Getter Recipe CsokiRecipe1;

    // Ételek
    private @Getter Recipe MelegszendivcsSonkasRecipe1;
    private @Getter Recipe MelegszendivcsSonkasRecipe2;
    private @Getter Recipe MelegszendivcsSonkasRecipe3;
    private @Getter Recipe MelegszendivcsSonkasRecipe4;
    private @Getter Recipe MelegszendivcsSonkasRecipe5;
    private @Getter Recipe MelegszendivcsSonkasRecipe6;

    private @Getter Recipe MelegszendivcsSzalamisRecipe1;
    private @Getter Recipe MelegszendivcsSzalamisRecipe2;
    private @Getter Recipe MelegszendivcsSzalamisRecipe3;
    private @Getter Recipe MelegszendivcsSzalamisRecipe4;
    private @Getter Recipe MelegszendivcsSzalamisRecipe5;
    private @Getter Recipe MelegszendivcsSzalamisRecipe6;

    private @Getter Recipe MelegszendivcsVegaRecipe1;
    private @Getter Recipe MelegszendivcsVegaRecipe2;
    private @Getter Recipe MelegszendivcsVegaRecipe3;
    private @Getter Recipe MelegszendivcsVegaRecipe4;
    private @Getter Recipe MelegszendivcsVegaRecipe5;
    private @Getter Recipe MelegszendivcsVegaRecipe6;

    private @Getter Recipe SajtosCsikokRecipe1;
    private @Getter Recipe SajtosCsikokRecipe2;
    private @Getter Recipe SajtosCsikokRecipe3;
    private @Getter Recipe SajtosCsikokRecipe4;
    private @Getter Recipe SajtosCsikokRecipe5;

    private @Getter Recipe ZsirosDeszkaRecipe1;
    private @Getter Recipe ZsirosDeszkaRecipe2;
    private @Getter Recipe ZsirosDeszkaRecipe3;
    private @Getter Recipe ZsirosDeszkaRecipe4;

    private @Getter Recipe WrapRecipe1;
    private @Getter Recipe WrapRecipe2;
    private @Getter Recipe WrapRecipe3;
    private @Getter Recipe WrapRecipe4;
    private @Getter Recipe WrapRecipe5;
    private @Getter Recipe WrapRecipe6;
    private @Getter Recipe WrapRecipe7;
    private @Getter Recipe WrapRecipe8;

    private @Getter Recipe SpecialisFeltetekPiritossalTonhalRecipe1;
    private @Getter Recipe SpecialisFeltetekPiritossalTonhalRecipe2;
    private @Getter Recipe SpecialisFeltetekPiritossalTonhalRecipe3;
    private @Getter Recipe SpecialisFeltetekPiritossalTonhalRecipe4;

    private @Getter Recipe SpecialisFeltetekPiritossalPadlizsanRecipe1;
    private @Getter Recipe SpecialisFeltetekPiritossalPadlizsanRecipe2;
    private @Getter Recipe SpecialisFeltetekPiritossalPadlizsanRecipe3;

    private @Getter Recipe SajttalRecipe1;
    private @Getter Recipe SajttalRecipe2;
    private @Getter Recipe SajttalRecipe3;
    private @Getter Recipe SajttalRecipe4;

    private @Getter Recipe GameUpTalRecipe1;
    private @Getter Recipe GameUpTalRecipe2;
    private @Getter Recipe GameUpTalRecipe3;
    private @Getter Recipe GameUpTalRecipe4;
    private @Getter Recipe GameUpTalRecipe5;
    private @Getter Recipe GameUpTalRecipe6;

    private @Getter Recipe GameUpFalankTalRecipe1;
    private @Getter Recipe GameUpFalankTalRecipe2;
    private @Getter Recipe GameUpFalankTalRecipe3;
    private @Getter Recipe GameUpFalankTalRecipe4;
    private @Getter Recipe GameUpFalankTalRecipe5;
    private @Getter Recipe GameUpFalankTalRecipe6;
    private @Getter Recipe GameUpFalankTalRecipe7;
    private @Getter Recipe GameUpFalankTalRecipe8;
    private @Getter Recipe GameUpFalankTalRecipe9;

    // Menuk
    private @Getter Recipe AgentShotCoverRecipe1;
    private @Getter Recipe LimonCeptRecipe1;
    private @Getter Recipe SplendBorRecipe1;
    private @Getter Recipe TatraTimeRecipe1;
    private @Getter Recipe SorrelAzEmberisegEllenRecipe1;

    /* ----- ITALLAP -----*/
    // Csapolt sorok
    private @Getter Recipe Soproni03Recipe1;
    private @Getter Recipe Soproni05Recipe1;
    private @Getter Recipe Edelweiss03Recipe1;
    private @Getter Recipe Edelweiss05Recipe1;

    // Uveges sorok
    private @Getter Recipe KrusoviceSvetleRecipe1;
    private @Getter Recipe SoproniDemonRecipe1;
    private @Getter Recipe SoproniMaxxRecipe1;
    private @Getter Recipe HeinekenRecipe1;
    private @Getter Recipe GosserNaturRadlerRecipe1;
    private @Getter Recipe GosserNaturRadler00Recipe1;
    private @Getter Recipe BekesSzentandrasiMeggyesRecipe1;
    private @Getter Recipe StrongbowDarkfruitRecipe1;
    private @Getter Recipe StrongbowGoldAppleCiderRecipe1;
    private @Getter Recipe EdelweissRecipe1;

    // Borok
    private @Getter Recipe HazBoraNagyEsNagyRecipe1;
    private @Getter Recipe HilltopIrsaiOliverRecipe1;
    private @Getter Recipe HilltopIrsaiOliverDecireRecipe1;
    private @Getter Recipe GereAttilaOlaszrizlingRecipe1;

    private @Getter Recipe HazBoraLisiczaRoseCuveeRecipe1;
    private @Getter Recipe MeszarosPinotNoirRoseRecipe1;
    private @Getter Recipe MeszarosPinotNoirRoseDecireRecipe1;

    private @Getter Recipe HazBoraPolgarSerumVeritasRecipe1;
    private @Getter Recipe VinczeMerlotRecipe1;
    private @Getter Recipe VylyanCabernetSauvignonRecipe1;
    private @Getter Recipe MeszarosHidasptereCabernetFrancReserveRecipe1;

    // Pezsgok
    private @Getter Recipe TorleyGalaSzarazRecipe1;
    private @Getter Recipe TorleyCharmantEdesRecipe1;

    // Fröccsok
    private @Getter Recipe HazBoraNagyEsNagyKisfroccsRecipe1;
    private @Getter Recipe HazBoraNagyEsNagyNagyfroccsRecipe1;
    private @Getter Recipe HazBoraNagyEsNagyVicehazmesterRecipe1;
    private @Getter Recipe HazBoraNagyEsNagyHazmesterRecipe1;
    private @Getter Recipe HazBoraNagyEsNagyHosszulepesRecipe1;
    private @Getter Recipe HazBoraNagyEsNagySportFroccsRecipe1;

    private @Getter Recipe HilltopIrsaiOliverKisfroccsRecipe1;
    private @Getter Recipe HilltopIrsaiOliverNagyfroccsRecipe1;
    private @Getter Recipe HilltopIrsaiOliverVicehazmesterRecipe1;
    private @Getter Recipe HilltopIrsaiOliverHazmesterRecipe1;
    private @Getter Recipe HilltopIrsaiOliverHosszulepesRecipe1;
    private @Getter Recipe HilltopIrsaiOliverSportFroccsRecipe1;

    private @Getter Recipe HazBoraLisiczaRoseCuveeKisfroccsRecipe1;
    private @Getter Recipe HazBoraLisiczaRoseCuveeNagyfroccsRecipe1;
    private @Getter Recipe HazBoraLisiczaRoseCuveeVicehazmesterRecipe1;
    private @Getter Recipe HazBoraLisiczaRoseCuveeHazmesterRecipe1;
    private @Getter Recipe HazBoraLisiczaRoseCuveeHosszulepesRecipe1;
    private @Getter Recipe HazBoraLisiczaRoseCuveeSportFroccsRecipe1;

    private @Getter Recipe MeszarosPinotNoirRoseKisfroccsRecipe1;
    private @Getter Recipe MeszarosPinotNoirRoseNagyfroccsRecipe1;
    private @Getter Recipe MeszarosPinotNoirRoseVicehazmesterRecipe1;
    private @Getter Recipe MeszarosPinotNoirRoseHazmesterRecipe1;
    private @Getter Recipe MeszarosPinotNoirRoseHosszulepesRecipe1;
    private @Getter Recipe MeszarosPinotNoirRoseSportFroccsRecipe1;

    // Roviditalok
    private @Getter Recipe JimBeam04Recipe1;
    private @Getter Recipe JohnnieWalkerRedLabel04Recipe1;
    private @Getter Recipe JackDaniels04Recipe1;
    private @Getter Recipe TullamoreDew04Recipe1;

    private @Getter Recipe Royal04Recipe1;
    private @Getter Recipe Finlandia04Recipe1;

    private @Getter Recipe BacardiSuperior04Recipe1;
    private @Getter Recipe CaptainMorganSpicedRum04Recipe1;

    private @Getter Recipe Beefeater04Recipe1;

    private @Getter Recipe TequilaSierraReposadoGold04Recipe1;
    private @Getter Recipe TequilaSierraSilver04Recipe1;

    private @Getter Recipe Unicum04Recipe1;
    private @Getter Recipe Jagermeister04Recipe1;
    private @Getter Recipe Baileys08Recipe1;

    private @Getter Recipe JimBeam02Recipe1;
    private @Getter Recipe JohnnieWalkerRedLabel02Recipe1;
    private @Getter Recipe JackDaniels02Recipe1;
    private @Getter Recipe TullamoreDew02Recipe1;

    private @Getter Recipe Royal02Recipe1;
    private @Getter Recipe Finlandia02Recipe1;

    private @Getter Recipe BacardiSuperior02Recipe1;
    private @Getter Recipe CaptainMorganSpicedRum02Recipe1;

    private @Getter Recipe Beefeater02Recipe1;

    private @Getter Recipe TequilaSierraReposadoGold02Recipe1;
    private @Getter Recipe TequilaSierraSilver02Recipe1;

    private @Getter Recipe Unicum02Recipe1;
    private @Getter Recipe Jagermeister02Recipe1;
    private @Getter Recipe Baileys04Recipe1;

    // Palinkak/
    private @Getter Recipe _22KokuszTatratea04Recipe1;
    private @Getter Recipe _32CitrusTatratea04Recipe1;
    private @Getter Recipe _42BarackTatratea04Recipe1;
    private @Getter Recipe _52EredetiTatratea04Recipe1;
    private @Getter Recipe _62ErdeiGyumolcsTatratea04Recipe1;
    private @Getter Recipe _72OutlawBetyarTatratea04Recipe1;

    private @Getter Recipe CseresznyePalinka04Recipe1;
    private @Getter Recipe KajszibarackPalinka04Recipe1;
    private @Getter Recipe Szilvapalinka04Recipe1;

    private @Getter Recipe _22KokuszTatratea02Recipe1;
    private @Getter Recipe _32CitrusTatratea02Recipe1;
    private @Getter Recipe _42BarackTatratea02Recipe1;
    private @Getter Recipe _52EredetiTatratea02Recipe1;
    private @Getter Recipe _62ErdeiGyumolcsTatratea02Recipe1;
    private @Getter Recipe _72OutlawBetyarTatratea02Recipe1;

    private @Getter Recipe CseresznyePalinka02Recipe1;
    private @Getter Recipe KajszibarackPalinka02Recipe1;
    private @Getter Recipe Szilvapalinka02Recipe1;

    // Shotok
    private @Getter Recipe Finca1Recipe1;
    private @Getter Recipe Finca1Recipe2;
    private @Getter Recipe Finca1Recipe3;

    private @Getter Recipe Bang1Recipe1;
    private @Getter Recipe Bang1Recipe2;

    private @Getter Recipe Imagine1Recipe1;
    private @Getter Recipe Imagine1Recipe2;

    private @Getter Recipe Finca6Recipe1;
    private @Getter Recipe Finca6Recipe2;
    private @Getter Recipe Finca6Recipe3;

    private @Getter Recipe Bang6Recipe1;
    private @Getter Recipe Bang6Recipe2;

    private @Getter Recipe Imagine6Recipe1;
    private @Getter Recipe Imagine6Recipe2;

    private @Getter Recipe RiffRaff6Recipe1;
    private @Getter Recipe RiffRaff6Recipe2;
    private @Getter Recipe RiffRaff6Recipe3;

    private @Getter Recipe Finca12Recipe1;
    private @Getter Recipe Finca12Recipe2;
    private @Getter Recipe Finca12Recipe3;

    private @Getter Recipe Bang12Recipe1;
    private @Getter Recipe Bang12Recipe2;

    private @Getter Recipe Imagine12Recipe1;
    private @Getter Recipe Imagine12Recipe2;

    private @Getter Recipe RiffRaff12Recipe1;
    private @Getter Recipe RiffRaff12Recipe2;
    private @Getter Recipe RiffRaff12Recipe3;

    // Uveges uditok
    private @Getter Recipe CocaColaRecipe1;
    private @Getter Recipe CocaColaZeroRecipe1;
    private @Getter Recipe FantaNarancsRecipe1;
    private @Getter Recipe SpriteRecipe1;
    private @Getter Recipe KinleyGyomberRecipe1;
    private @Getter Recipe KinleyTonicRecipe1;
    private @Getter Recipe NesteaCitromRecipe1;
    private @Getter Recipe NesteaBarackRecipe1;

    // Kimert uditok
    private @Getter Recipe CappyAlmaRecipe1;
    private @Getter Recipe CappyNarancsRecipe1;
    private @Getter Recipe CappyBarackRecipe1;
    private @Getter Recipe CappyAnanaszRecipe1;

    // Limonadek
    private @Getter Recipe LimonadeMalna05Recipe1;
    private @Getter Recipe LimonadeMalna05Recipe2;
    private @Getter Recipe LimonadeMalna05Recipe3;
    private @Getter Recipe LimonadeMalna05Recipe4;

    private @Getter Recipe LimonadeMeggy05Recipe1;
    private @Getter Recipe LimonadeMeggy05Recipe2;
    private @Getter Recipe LimonadeMeggy05Recipe3;
    private @Getter Recipe LimonadeMeggy05Recipe4;

    private @Getter Recipe LimonadeEperNarancs05Recipe1;
    private @Getter Recipe LimonadeEperNarancs05Recipe2;
    private @Getter Recipe LimonadeEperNarancs05Recipe3;
    private @Getter Recipe LimonadeEperNarancs05Recipe4;

    private @Getter Recipe LimonadeCitrus05Recipe1;
    private @Getter Recipe LimonadeCitrus05Recipe2;
    private @Getter Recipe LimonadeCitrus05Recipe3;
    private @Getter Recipe LimonadeCitrus05Recipe4;

    private @Getter Recipe LimonadeMalna10Recipe1;
    private @Getter Recipe LimonadeMalna10Recipe2;
    private @Getter Recipe LimonadeMalna10Recipe3;
    private @Getter Recipe LimonadeMalna10Recipe4;

    private @Getter Recipe LimonadeMeggy10Recipe1;
    private @Getter Recipe LimonadeMeggy10Recipe2;
    private @Getter Recipe LimonadeMeggy10Recipe3;
    private @Getter Recipe LimonadeMeggy10Recipe4;

    private @Getter Recipe LimonadeEperNarancs10Recipe1;
    private @Getter Recipe LimonadeEperNarancs10Recipe2;
    private @Getter Recipe LimonadeEperNarancs10Recipe3;
    private @Getter Recipe LimonadeEperNarancs10Recipe4;

    private @Getter Recipe LimonadeCitrus10Recipe1;
    private @Getter Recipe LimonadeCitrus10Recipe2;
    private @Getter Recipe LimonadeCitrus10Recipe3;
    private @Getter Recipe LimonadeCitrus10Recipe4;

    // Asvanyviz
    private @Getter Recipe NaturaquaSzensavasRecipe1;
    private @Getter Recipe NaturaquaSzensavmentesRecipe1;
    private @Getter Recipe SzodaRecipe1;

    // Energiaitalok
    private @Getter Recipe BurnOriginalRecipe1;
    private @Getter Recipe BurnZeroRecipe1;
    private @Getter Recipe MonsterEnergyRecipe1;
    private @Getter Recipe MonsterAssaultRecipe1;
    private @Getter Recipe MonsterRehabRecipe1;

    // Filteres Teaak
    private @Getter Recipe DallmayrFeketeRecipe1;
    private @Getter Recipe DallmayrGyumolcsRecipe1;
    private @Getter Recipe DallmayrZoldRecipe1;

    private @Getter Recipe PiramisDarjelingRecipe1;
    private @Getter Recipe PiramisMangoMaracujaRecipe1;
    private @Getter Recipe PiramisAnanaszPapajaRecipe1;
    private @Getter Recipe PiramisCitrusVerbenaRecipe1;

    // Kavek
    private @Getter Recipe EspressoRecipe1;
    private @Getter Recipe AmericanoRecipe1;
    private @Getter Recipe CappuccinoRecipe1;
    private @Getter Recipe CaffeLatteRecipe1;
    private @Getter Recipe LatteMacchiatoRecipe1;
    private @Getter Recipe CaffeMelangeRecipe1;
    private @Getter Recipe ForroCsokiBarnaRecipe1;
    private @Getter Recipe ForroCsokiFeherRecipe1;

    // Napi akciok
    private @Getter Recipe CaptainAndGyomberRecipe1;
    private @Getter Recipe GinTonicRecipe1;
    private @Getter Recipe JackAndCokeRecipe1;
    private @Getter Recipe VodkaSzodaRecipe1;

    private @Getter PriceModifier priceModifierEdelweiss;
    private @Getter PriceModifier priceModifierCaptainAndGyomber;
    private @Getter PriceModifier priceModifierGinTonic;
    private @Getter PriceModifier priceModifierJackAndCoke;
    private @Getter PriceModifier priceModifierVodkaSzoda;
    private @Getter PriceModifier priceModifierBekesSzentdrasiMeggyes;

    private @Getter Receipt receiptPurchase;
    private @Getter Receipt receiptInventory;
    private @Getter Receipt receiptDisposal;
    private @Getter Receipt receiptOther;

    private @Getter Table table1;
    private @Getter Table table2;
    private @Getter Table table3;
    private @Getter Table table4;
    private @Getter Table table5;
    private @Getter Table table6;
    private @Getter Table table7;
    private @Getter Table table8;
    private @Getter Table table9;
    private @Getter Table table10;
    private @Getter Table table11;
    private @Getter Table table12;
    private @Getter Table table13;

    private @Getter Table tablePurchase;
    private @Getter Table tableInventory;
    private @Getter Table tableDisposal;
    private @Getter Table tableOther;
    private @Getter Table tableOrphanage;

    private @Getter Reservation reservationOne;
    private @Getter Reservation reservationTwo;

    private @Getter Restaurant restaurant;

    private @Getter VATSerie vatSerie;

    private @Getter VAT vatOne;
    private @Getter VAT vatTwo;
    private @Getter VAT vatThree;
    private @Getter VAT vatFour;
    private @Getter VAT vatFive;

    private @Getter DailyClosure dailyClosureOne;
    private @Getter DailyClosure dailyClosureTwo;

    private @Getter AuthUser admin;
    private @Getter AuthUser user;

    public BuildSchema(){
        entityManager = EntityManagerProvider.getEntityManager("ProductionPersistance");
    }

    private void dropAll(){

        GuardedTransaction.run(() -> {
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.PriceModifier").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Recipe").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Stock").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Product").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Receipt").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Reservation").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Table").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.DailyClosure").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.Restaurant").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.VAT").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.VATSerie").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.model.entity.ProductCategory").executeUpdate();
            entityManager.createQuery("DELETE FROM com.inspirationlogical.receipt.corelib.security.model.entity.AuthUser").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM PRODUCT_CATEGORY_RELATIONS").executeUpdate();
            entityManager.createNativeQuery("UPDATE hibernate_sequence SET next_val=1").executeUpdate();
        });
        entityManager.clear();
    }

    public void buildTestSchema() {
        dropAll();
        buildObjects();
        setUpObjectRelationShips();
        persistObjects();
    }

    private void buildObjects() {
        buildProducts();
        buildProductCategories();
        buildPriceModifiers();
        buildRecipes();
//        buildStocks();
        buildReceipts();
//        buildReceiptRecords();
        buildVatSeries();
        BuildVATs();
        buildTables();
        buildReservations();
        buildRestaurant();
        buildDailyClosures();
        buildAuthUsers();
    }

    private void buildAuthUsers() {
        buildAdmin();
        buildUser();
    }

    private void buildDailyClosures() {
        buildDailyClosureOne();
        buildDailyClosureTwo();
    }

    private void setUpObjectRelationShips() {
        productCategories();
        categoriesAndPriceModifiers();
        productsAndCategories();
        productsAndRecipes();
        vatSerieAndVatValues();
        restaurantAndTables();
        tablesAndReservations();
        restaurantAndDailyClosures();
    }

    private void persistObjects() {
        GuardedTransaction.run(() -> entityManager.persist(restaurant));
        GuardedTransaction.run(() -> entityManager.persist(root));
        GuardedTransaction.run(() -> entityManager.persist(vatSerie));
        GuardedTransaction.run(() -> entityManager.persist(admin));
        GuardedTransaction.run(() -> entityManager.persist(user));
    }

    private void buildProducts() {
        buildProductAdHoc();
        buildProductGameFee();

        /*----- ETLAP -----*/
        // Rágcsák
        buildSosPerec();
        buildMogyoro();
        buildRagcsaMix1Perec2Mogyi();
        buildRagcsaMix2Perec1Mogyi();
        buildRagcsaMix3Perec();
        buildNachosSosSajt();
        buildNachosSosChili();
        buildNachosBBQSajt();
        buildNachosBBQChili();
        buildChips();
        buildPopcorn();
        buildGumicukor();
        buildBalatonszelet();
        buildCsoki();

        // Ételek
        buildMelegszendivcsSonkas();
        buildMelegszendivcsSzalamis();
        buildMelegszendivcsVega();
        buildSajtosCsikok();
        buildZsirosDeszka();
        buildWrap();
        buildSpecialisFeltetekPiritossalTonhal();
        buildSpecialisFeltetekPiritossalPadlizsan();
        buildSajttal();
        buildGameUpTal();
        buildGameUpFalankTal();

        // Menuk
        buildAgentShotCover();
        buildLimonCept();
        buildSplendBor();
        buildTatraTime();
        buildSorrelAzEmberisegEllen();

        // Alapanyagok
        buildCitrom();
        buildNarancs();

        buildMalnaSzirup();
        buildEperNarancsSzirup();
        buildCitrusSzirup();
        buildMeggySzirup();

        buildTabasco();
        buildBlueCuracao();

        buildFagyiGyumi();

        buildBarnaCukor();
        buildFeherCukor();
        buildMez();

        buildTej();

        buildTortillaSos();
        buildTortillaBBQ();
        buildNachosSajtszosz();
        buildNachosChiliszosz();

        buildBagett();
        buildZsir();

        buildLilahagyma();
        buildPirospaprika();
        buildSalata();
        buildParadicsom();
        buildUborka();
        buildPiritottHagyma();

        buildTrappista();
        buildCheddar();
        buildKecskesajt();
        buildCamambert();

        buildSonka();
        buildSzalami();
        buildTonhalKrem();

        buildMargarin();

        buildMustar();
        buildMajonez();
        buildAjvar();

        buildTortillaLap();

        /*----- ITALLAP -----*/
        //Csapolt Sorok
        buildSoproni03();
        buildSoproni05();
        buildEdelweiss03();
        buildEdelweiss05();

        // Uveges sorok
        buildKrusoviceSvetle();
        buildSoproniDemon();
        buildSoproniMaxx();
        buildHeineken();
        buildGosserNaturRadler();
        buildGosserNaturRadler00();
        buildBekesSzentandrasiMeggyes();
        buildStrongbowDarkfruit();
        buildStrongbowGoldAppleCider();
        buildEdelweiss();

        // Borok
        buildHazBoraNagyEsNagy();
        buildHilltopIrsaiOliver();
        buildHilltopIrsaiOliverDecire();
        buildGereAttilaOlaszrizling();

        buildHazBoraLisiczaRoseCuvee();
        buildMeszarosPinot();
        buildMeszarosPinotDecire();


        buildHazBoraPolgarSerumVeritas();
        buildVinczeMerlot();
        buildVylyanCabernetSauvignon();
        buildMeszarosHidasptereCabernetFrancReserve();

        // Fröccsök
        buildHazBoraNagyEsNagyKisfroccs();
        buildHazBoraNagyEsNagyNagyfroccs();
        buildHazBoraNagyEsNagyVicehazmester();
        buildHazBoraNagyEsNagyHazmester();
        buildHazBoraNagyEsNagyHosszulepes();
        buildHazBoraNagyEsNagySportfroccs();

        buildHilltopIrsaiOliverKisfroccs();
        buildHilltopIrsaiOliverNagyfroccs();
        buildHilltopIrsaiOliverVicehazmester();
        buildHilltopIrsaiOliverHazmester();
        buildHilltopIrsaiOliverHosszulepes();
        buildHilltopIrsaiOliverSportfroccs();

        buildHazBoraLisiczaRoseCuveeKisfroccs();
        buildHazBoraLisiczaRoseCuveeNagyfroccs();
        buildHazBoraLisiczaRoseCuveeVicehazmester();
        buildHazBoraLisiczaRoseCuveeHazmester();
        buildHazBoraLisiczaRoseCuveeHosszulepes();
        buildHazBoraLisiczaRoseCuveeSportfroccs();

        buildMeszarosPinotKisfroccs();
        buildMeszarosPinotNagyfroccs();
        buildMeszarosPinotVicehazmester();
        buildMeszarosPinotHazmester();
        buildMeszarosPinotHosszulepes();
        buildMeszarosPinotSportfroccs();

        // Pezsgok
        buildTorleyGalaSzaraz();
        buildTorleyCharmantEdes();
        
        // Roviditalok
        buildJimBeam04();
        buildJohnnieWalkerRedLabel04();
        buildJackDaniels04();
        buildTullamoreDew04();
        
        buildRoyal04();
        buildFinlandia04();
        
        buildBacardiSuperior04();
        buildCaptainMorganSpicedRum04();
        
        buildBeefeater04();
        
        buildTequilaSierraReposadoGold04();
        buildTequilaSierraSilver04();
        
        buildUnicum04();
        buildJagermeister04();
        buildBaileys08();

        buildJimBeam02();
        buildJohnnieWalkerRedLabel02();
        buildJackDaniels02();
        buildTullamoreDew02();

        buildRoyal02();
        buildFinlandia02();

        buildBacardiSuperior02();
        buildCaptainMorganSpicedRum02();

        buildBeefeater02();

        buildTequilaSierraReposadoGold02();
        buildTequilaSierraSilver02();

        buildUnicum02();
        buildJagermeister02();
        buildBaileys04();

        // Palinkak
        build22KokuszTatratea04();
        build32CitrusTatratea04();
        build42BarackTatratea04();
        build52EredetiTatratea04();
        build62ErdeiGyumolcsTatratea04();
        build72OutlawBetyarTatratea04();

        buildCseresznyePalinka04();
        buildKajszibarackPalinka04();
        buildSzilvapalinka04();

        build22KokuszTatratea02();
        build32CitrusTatratea02();
        build42BarackTatratea02();
        build52EredetiTatratea02();
        build62ErdeiGyumolcsTatratea02();
        build72OutlawBetyarTatratea02();

        buildCseresznyePalinka02();
        buildKajszibarackPalinka02();
        buildSzilvapalinka02();

        // Shotok
        buildFinca1();
        buildBang1();
        buildImagine1();

        buildFinca6();
        buildBang6();
        buildImagine6();
        buildRiffRaff6();

        buildFinca12();
        buildBang12();
        buildImagine12();
        buildRiffRaff12();

        // Uveges uditok
        buildCocaCola();
        buildCocaColaZero();
        buildFantaNarancs();
        buildSprite();
        buildKinleyGyomber();
        buildKinleyTonic();
        buildNesteaCitrom();
        buildNesteaBarack();

        // Kimert uditok
        buildCappyAlma();
        buildCappyNarancs();
        buildCappyBarack();
        buildCappyAnanasz();

        // Limonadek
        buildLimonadeMalna05();
        buildLimonadeMeggy05();
        buildLimonadeEperNarancs05();
        buildLimonadeCitrus05();

        buildLimonadeMalna10();
        buildLimonadeMeggy10();
        buildLimonadeEperNarancs10();
        buildLimonadeCitrus10();

        // Asvanyviz
        buildNaturaquaSzensavas();
        buildNaturaquaSzensavmentes();
        buildSzoda();

        // Energiaitalok
        buildBurnOriginal();
        buildBurnZero();
        buildMonsterEnergy();
        buildMonsterAssault();
        buildMonsterRehab();

        // Filteres Teaak
        buildDallmyrFekete();
        buildDallmyrGyumolcs();
        buildDallmyrZold();

        buildPiramisDarjeling();
        buildPiramisMangoMaracuja();
        buildPiramisAnanaszPapaja();
        buildPiramisCitrusVerbena();

        // Kavek
        buildEspresso();
        buildAmericano();
        buildCappuccino();
        buildCaffeLatte();
        buildLatteMacchiato();
        buildCaffeMelange();
        buildForroCsokiBarna();
        buildForroCsokiFeher();

        // Napi akciok
        buildCaptainAndGyomber();
        buildGinTonic();
        buildJackAndCoke();
        buildVodkaSzoda();
    }

    private void buildProductAdHoc() {
        productAdHoc = Product.builder()
                .longName("productAdHoc")
                .shortName("productAdHoc")
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.AD_HOC_PRODUCT)
                .build();
    }

    private void buildProductGameFee() {
        productGameFee = Product.builder()
                .longName("Játékdíj")
                .shortName("Játékdíj")
                .salePrice(300)
                .purchasePrice(0)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.LITER)
                .type(ProductType.GAME_FEE_PRODUCT)
                .build();
    }

    private void buildSosPerec() {
        SosPerec = Product.builder()
                .longName("Sós Perec")
                .shortName("Perec")
                .orderNumber(1)
                .rapidCode(91)
                .salePrice(290)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildMogyoro() {
        Mogyoro = Product.builder()
                .longName("Mogyoró")
                .shortName("Mogyi")
                .orderNumber(2)
                .rapidCode(92)
                .salePrice(390)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildRagcsaMix1Perec2Mogyi() {
        RagcsaMix1Perec2Mogyi = Product.builder()
                .longName("Rágcsa Mix 1")
                .shortName("Mix 1Perec 2Mogyi")
                .orderNumber(8)
                .rapidCode(98)
                .salePrice(890)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildRagcsaMix2Perec1Mogyi() {
        RagcsaMix2Perec1Mogyi = Product.builder()
                .longName("Rágcsa Mix 2")
                .shortName("Mix 2Perec 1Mogyi")
                .orderNumber(9)
                .rapidCode(98)
                .salePrice(890)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildRagcsaMix3Perec() {
        RagcsaMix3Perec = Product.builder()
                .longName("Rágcsa Mix 3")
                .shortName("Mix 3Perec")
                .orderNumber(10)
                .rapidCode(98)
                .salePrice(890)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildNachosSosSajt() {
        NachosSosSajt = Product.builder()
                .longName("Nachos Sós Sajt")
                .shortName("Nachos SS")
                .orderNumber(3)
                .rapidCode(93)
                .salePrice(720)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildNachosSosChili() {
        NachosSosChili = Product.builder()
                .longName("Nachos Sos Chili")
                .shortName("Nachos SC")
                .orderNumber(4)
                .rapidCode(94)
                .salePrice(720)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildNachosBBQSajt() {
        NachosBBQSajt = Product.builder()
                .longName("Nachos BBQ Sajt")
                .shortName("Nachos BS")
                .orderNumber(5)
                .rapidCode(95)
                .salePrice(720)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildNachosBBQChili() {
        NachosBBQChili = Product.builder()
                .longName("Nachos BBQ Chili")
                .shortName("Nachos BC")
                .orderNumber(6)
                .rapidCode(96)
                .salePrice(720)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildChips() {
        Chips = Product.builder()
                .longName("Chips")
                .shortName("Chips")
                .orderNumber(11)
                .rapidCode(99)
                .salePrice(490)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildPopcorn() {
        Popcorn = Product.builder()
                .longName("Popcorn")
                .shortName("Popcorn")
                .orderNumber(7)
                .rapidCode(97)
                .salePrice(390)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildGumicukor() {
        Gumicukor = Product.builder()
                .longName("Gumicukor")
                .shortName("Gumicukor")
                .orderNumber(14)
                .rapidCode(102)
                .salePrice(490)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildBalatonszelet() {
        Balatonszelet = Product.builder()
                .longName("Balatonszelet")
                .shortName("Balatonszelet")
                .orderNumber(13)
                .rapidCode(101)
                .salePrice(190)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildCsoki() {
        Csoki = Product.builder()
                .longName("Csoki")
                .shortName("Csoki")
                .orderNumber(12)
                .rapidCode(100)
                .salePrice(290)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildMelegszendivcsSonkas() {
        MelegszendivcsSonkas = Product.builder()
                .longName("Melegszendivcs Sonkás")
                .shortName("Sonkás")
                .orderNumber(1)
                .rapidCode(81)
                .salePrice(790)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildMelegszendivcsSzalamis() {
        MelegszendivcsSzalamis = Product.builder()
                .longName("Melegszendivcs Szalámis")
                .shortName("Szalámis")
                .orderNumber(2)
                .rapidCode(82)
                .salePrice(790)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildMelegszendivcsVega() {
        MelegszendivcsVega = Product.builder()
                .longName("Melegszendivcs Vega")
                .shortName("Vega")
                .orderNumber(3)
                .rapidCode(83)
                .salePrice(790)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildSajtosCsikok() {
        SajtosCsikok = Product.builder()
                .longName("Sajtos Csíkok")
                .shortName("Sajtos Csíkok")
                .orderNumber(5)
                .rapidCode(85)
                .salePrice(590)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildZsirosDeszka() {
        ZsirosDeszka = Product.builder()
                .longName("Zsíros Deszka")
                .shortName("Zsíros Deszka")
                .orderNumber(4)
                .rapidCode(84)
                .salePrice(590)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildWrap() {
        Wrap = Product.builder()
                .longName("Wrap")
                .shortName("Wrap")
                .orderNumber(6)
                .rapidCode(86)
                .salePrice(990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildSpecialisFeltetekPiritossalTonhal() {
        SpecialisFeltetekPiritossalTonhal = Product.builder()
                .longName("Speciális Feltétek Piritóssal Tonhal")
                .shortName("Spec. Felt. Tonhal")
                .orderNumber(8)
                .rapidCode(88)
                .salePrice(890)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }


    private void buildSpecialisFeltetekPiritossalPadlizsan() {
        SpecialisFeltetekPiritossalPadlizsan = Product.builder()
                .longName("Speciális Feltétek Piritóssal Padlizsán")
                .shortName("Spec. Felt. Padl")
                .orderNumber(9)
                .rapidCode(88)
                .salePrice(890)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildSajttal() {
        Sajttal = Product.builder()
                .longName("Sajttál")
                .shortName("Sajttál")
                .orderNumber(7)
                .rapidCode(87)
                .salePrice(990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildGameUpTal() {
        GameUpTal = Product.builder()
                .longName("Game Up Tál")
                .shortName("Game Up Tál")
                .orderNumber(10)
                .rapidCode(89)
                .salePrice(2490)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildGameUpFalankTal() {
        GameUpFalankTal = Product.builder()
                .longName("Game Up Falánk Tál")
                .shortName("Falánk Tál")
                .orderNumber(11)
                .rapidCode(90)
                .salePrice(4990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildAgentShotCover() {
        AgentShotCover = Product.builder()
                .longName("Agent Shot Cover")
                .shortName("Agent Shot Cover")
                .orderNumber(1)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildLimonCept() {
        LimonCept = Product.builder()
                .longName("Limoncept")
                .shortName("Limoncept")
                .orderNumber(2)
                .salePrice(3900)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildSplendBor() {
        SplendBor = Product.builder()
                .longName("SplendBor")
                .shortName("SplendBor")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildTatraTime() {
        TatraTime = Product.builder()
                .longName("Tátra Time")
                .shortName("Tátra Time")
                .orderNumber(4)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }


    private void buildSorrelAzEmberisegEllen() {
        SorrelAzEmberisegEllen = Product.builder()
                .longName("Sörrel Az Emberiség Ellen")
                .shortName("Sörrel Az Emberiség")
                .orderNumber(5)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildCitrom() {
        Citrom = Product.builder()
                .longName("Citrom")
                .shortName("Citrom")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(1000)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildNarancs() {
        Narancs = Product.builder()
                .longName("Narancs")
                .shortName("Narancs")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(1000)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildMalnaSzirup() {
        MalnaSzirup = Product.builder()
                .longName("Málna Szirup")
                .shortName("Málna Szirup")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildEperNarancsSzirup() {
        EperNarancsSzirup = Product.builder()
                .longName("Eper-Narancs Szirup")
                .shortName("Eper-Narancs Szirup")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildCitrusSzirup() {
        CitrusSzirup = Product.builder()
                .longName("Citrus Szirup")
                .shortName("Citrus Szirup")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildMeggySzirup() {
        MeggySzirup = Product.builder()
                .longName("Meggy Szirup")
                .shortName("Meggy Szirup")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildTabasco() {
        Tabasco = Product.builder()
                .longName("Tabasco")
                .shortName("Tabasco")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildBlueCuracao() {
        BlueCuracao = Product.builder()
                .longName("Blue Curacao")
                .shortName("Blue Curacao")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildFagyiGyumi() {
        FagyiGyumi = Product.builder()
                .longName("Fagyi Gyumi")
                .shortName("Fagyi Gyumi")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildBarnaCukor() {
        BarnaCukor = Product.builder()
                .longName("Barna Cukor")
                .shortName("Barna Cukor")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildFeherCukor() {
        FeherCukor = Product.builder()
                .longName("Feher Cukor")
                .shortName("Feher Cukor")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildMez() {
        Mez = Product.builder()
                .longName("Méz")
                .shortName("Méz")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildTej() {
        Tej = Product.builder()
                .longName("Tej")
                .shortName("Tej")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildTortillaSos() {
        TortillaSos = Product.builder()
                .longName("Tortilla Sós")
                .shortName("Tortilla Sós")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildTortillaBBQ() {
        TortillaBBQ = Product.builder()
                .longName("Tortilla BBQ")
                .shortName("Tortilla BBQ")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildNachosSajtszosz() {
        NachosSajtszosz = Product.builder()
                .longName("Nachos Sajtszósz")
                .shortName("Nachos Sajtszósz")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildNachosChiliszosz() {
        NachosChiliszosz = Product.builder()
                .longName("Nachos Chiliszósz")
                .shortName("Nachos Chiliszósz")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildBagett() {
        Bagett = Product.builder()
                .longName("Bagett")
                .shortName("Bagett")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(250)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildZsir() {
        Zsir = Product.builder()
                .longName("Zsir")
                .shortName("Zsir")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildLilahagyma() {
        Lilahagyma = Product.builder()
                .longName("Lilahagyma")
                .shortName("Lilahagyma")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildPirospaprika() {
        Pirospaprika = Product.builder()
                .longName("Pirospaprika")
                .shortName("Pirospaprika")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildSalata() {
        Salata = Product.builder()
                .longName("Saláta")
                .shortName("Saláta")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(250)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildParadicsom() {
        Paradicsom = Product.builder()
                .longName("Paradicsom")
                .shortName("Paradicsom")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(1000)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildUborka() {
        Uborka = Product.builder()
                .longName("Uborka")
                .shortName("Uborka")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildPiritottHagyma() {
        PiritottHagyma = Product.builder()
                .longName("Piritott Hagyma")
                .shortName("Piritott Hagyma")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildTrappista() {
        Trappista = Product.builder()
                .longName("Trappista")
                .shortName("Trappista")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(1000)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildCheddar() {
        Cheddar = Product.builder()
                .longName("Cheddar")
                .shortName("Cheddar")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(500)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildKecskesajt() {
        Kecskesajt = Product.builder()
                .longName("Kecskesajt")
                .shortName("Kecskesajt")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(150)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildCamambert() {
        Camambert = Product.builder()
                .longName("Camambert")
                .shortName("Camambert")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(120)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildSonka() {
        Sonka = Product.builder()
                .longName("Sonka")
                .shortName("Sonka")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(2500)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildSzalami() {
        Szalami = Product.builder()
                .longName("Szalámi")
                .shortName("Szalámi")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(1000)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildTonhalKrem() {
        TonhalKrem = Product.builder()
                .longName("Tonhal Krém")
                .shortName("Tonhal Krém")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(100)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildMargarin() {
        Margarin = Product.builder()
                .longName("Margarin")
                .shortName("Margarin")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(500)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildMustar() {
        Mustar = Product.builder()
                .longName("Mustár")
                .shortName("Mustár")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildMajonez() {
        Majonez = Product.builder()
                .longName("Majonéz")
                .shortName("Majonéz")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildAjvar() {
        Ajvar = Product.builder()
                .longName("Ajvár")
                .shortName("Ajvár")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildTortillaLap() {
        TortillaLap = Product.builder()
                .longName("Tortilla Lap")
                .shortName("Tortilla Lap")
                .orderNumber(3)
                .salePrice(3990)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.GRAM)
                .storageMultiplier(1620)
                .type(ProductType.STORABLE)
                .build();
    }

    private void buildSoproni03() {
        Soproni03 = Product.builder()
                .longName("Soproni 0,3L")
                .shortName("Sop 0,3L")
                .orderNumber(13)
                .rapidCode(110)
                .salePrice(320)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.LITER)
                .storageMultiplier(30)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildSoproni05() {
        Soproni05 = Product.builder()
                .longName("Soproni 0,5L")
                .shortName("Sop 0,5L")
                .orderNumber(1)
                .rapidCode(11)
                .salePrice(440)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.LITER)
                .storageMultiplier(30)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildEdelweiss03() {
        Edelweiss03 = Product.builder()
                .longName("Edelweiss 0,3L")
                .shortName("Edel 0,3L")
                .orderNumber(14)
                .rapidCode(120)
                .salePrice(520)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.LITER)
                .storageMultiplier(25)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildEdelweiss05() {
        Edelweiss05 = Product.builder()
                .longName("Edelweiss 0,5L")
                .shortName("Edel 0,5L")
                .orderNumber(2)
                .rapidCode(12)
                .salePrice(780)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.LITER)
                .storageMultiplier(25)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildKrusoviceSvetle() {
        KrusoviceSvetle = Product.builder()
                .longName("Krusovice Svetlé")
                .shortName("Kruso")
                .orderNumber(3)
                .rapidCode(13)
                .salePrice(480)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(50)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildSoproniDemon() {
        SoproniDemon = Product.builder()
                .longName("Soproni Démon")
                .shortName("Démon")
                .orderNumber(4)
                .rapidCode(14)
                .salePrice(480)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(50)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildSoproniMaxx() {
        SoproniMaxx = Product.builder()
                .longName("Soproni Maxx")
                .shortName("Maxx")
                .orderNumber(8)
                .salePrice(420)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(50)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildHeineken() {
        Heineken = Product.builder()
                .longName("Heineken")
                .shortName("Heineken")
                .orderNumber(10)
                .salePrice(540)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(33)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildGosserNaturRadler() {
        GosserNaturRadler = Product.builder()
                .longName("Gosser Natur Radler")
                .shortName("Gosser")
                .orderNumber(9)
                .salePrice(440)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(33)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildGosserNaturRadler00() {
        GosserNaturRadler00 = Product.builder()
                .longName("Gosser Natur Radler 0,0%")
                .shortName("Gosser 0,0%")
                .orderNumber(11)
                .salePrice(420)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(33)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildBekesSzentandrasiMeggyes() {
        BekesSzentandrasiMeggyes = Product.builder()
                .longName("Békésszentandrási Meggyes")
                .shortName("Meggy Sör")
                .orderNumber(5)
                .rapidCode(15)
                .salePrice(890)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(50)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildStrongbowDarkfruit() {
        StrongbowDarkfruit = Product.builder()
                .longName("Strongbow Darkfruit")
                .shortName("Darkfruit")
                .orderNumber(7)
                .salePrice(640)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(33)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildStrongbowGoldAppleCider() {
        StrongbowGoldAppleCider = Product.builder()
                .longName("Strongbow Gold Apple Cider")
                .shortName("Cider")
                .orderNumber(6)
                .salePrice(640)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(33)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildEdelweiss() {
        Edelweiss = Product.builder()
                .longName("Edelweiss")
                .shortName("Edel Üv")
                .orderNumber(12)
                .salePrice(780)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(50)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildHazBoraNagyEsNagy() {
        HazBoraNagyEsNagy = Product.builder()
                .longName("Ház Bora Nagy és Nagy")
                .shortName("FHB")
                .orderNumber(1)
                .rapidCode(21)
                .salePrice(260)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(150)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildHilltopIrsaiOliver() {
        HilltopIrsaiOliver = Product.builder()
                .longName("Hilltop Irsai Olivér")
                .shortName("Irsai Üv")
                .orderNumber(6)
                .salePrice(2200)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(75)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildHilltopIrsaiOliverDecire() {
        HilltopIrsaiOliverDecire = Product.builder()
                .longName("Hilltop Irsai Olivér 1dl")
                .shortName("Irsai")
                .orderNumber(4)
                .rapidCode(24)
                .salePrice(340)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(75)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildGereAttilaOlaszrizling() {
        GereAttilaOlaszrizling = Product.builder()
                .longName("Gere Attila Olaszrizling")
                .shortName("Gere")
                .orderNumber(10)
                .salePrice(2900)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(75)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildHazBoraLisiczaRoseCuvee() {
        HazBoraLisiczaRoseCuvee = Product.builder()
                .longName("Ház Bora Lisicza Rosé Cuvée")
                .shortName("RHB")
                .orderNumber(2)
                .rapidCode(22)
                .salePrice(260)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(150)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildMeszarosPinot() {
        MeszarosPinotNoirRose = Product.builder()
                .longName("Mészáros Pinot Noir Rose")
                .shortName("Mészáros Üv")
                .orderNumber(7)
                .salePrice(2400)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(75)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildMeszarosPinotDecire() {
        MeszarosPinotNoirRoseDecire = Product.builder()
                .longName("Mészáros Pinot Noir Rose 1dl")
                .shortName("Mészáros")
                .orderNumber(5)
                .rapidCode(25)
                .salePrice(360)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(75)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildHazBoraPolgarSerumVeritas() {
        HazBoraPolgarSerumVeritas = Product.builder()
                .longName("Ház Bora Polgár Serum Veritas")
                .shortName("VHB")
                .orderNumber(3)
                .rapidCode(23)
                .salePrice(360)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(150)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildVinczeMerlot() {
        VinczeMerlot = Product.builder()
                .longName("Vincze Merlot")
                .shortName("Vincze")
                .orderNumber(9)
                .salePrice(2700)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(75)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildVylyanCabernetSauvignon() {
        VylyanCabernetSauvignon = Product.builder()
                .longName("Vylyan Cabernet Sauvignon")
                .shortName("Vylyan")
                .orderNumber(8)
                .salePrice(3400)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(75)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildMeszarosHidasptereCabernetFrancReserve() {
        MeszarosHidasptereCabernetFrancReserve = Product.builder()
                .longName("Meszáros Hidasptere Cabernet Franc Reserve")
                .shortName("Meszáros Cabernet")
                .orderNumber(11)
                .salePrice(4700)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(75)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildHazBoraNagyEsNagyKisfroccs() {
        HazBoraNagyEsNagyKisfroccs = Product.builder()
                .longName("Ház Bora Nagy és Nagy Kisförccs")
                .shortName("FHB Kisförccs")
                .orderNumber(1)
                .rapidCode(0)
                .salePrice(300)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildHazBoraNagyEsNagyNagyfroccs() {
        HazBoraNagyEsNagyNagyfroccs = Product.builder()
                .longName("Ház Bora Nagy és Nagy Nagyfröccs")
                .shortName("FHB Nagyfröccs")
                .orderNumber(5)
                .rapidCode(0)
                .salePrice(560)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildHazBoraNagyEsNagyVicehazmester() {
        HazBoraNagyEsNagyVicehazmester = Product.builder()
                .longName("Ház Bora Nagy és Nagy Viceházmester")
                .shortName("FHB Vice")
                .orderNumber(9)
                .rapidCode(0)
                .salePrice(640)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildHazBoraNagyEsNagyHazmester() {
        HazBoraNagyEsNagyHazmester = Product.builder()
                .longName("Ház Bora Nagy és Nagy Házmester")
                .shortName("FHB Házmester")
                .orderNumber(13)
                .rapidCode(0)
                .salePrice(860)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildHazBoraNagyEsNagyHosszulepes() {
        HazBoraNagyEsNagyHosszulepes = Product.builder()
                .longName("Ház Bora Nagy és Nagy Hosszúlépés")
                .shortName("FHB Hosszúlépés")
                .orderNumber(17)
                .rapidCode(0)
                .salePrice(340)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildHazBoraNagyEsNagySportfroccs() {
        HazBoraNagyEsNagySportFroccs = Product.builder()
                .longName("Ház Bora Nagy és Nagy Sportfröccs")
                .shortName("FHB Sportfröccs")
                .orderNumber(21)
                .rapidCode(0)
                .salePrice(420)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildHilltopIrsaiOliverKisfroccs() {
        HilltopIrsaiOliverKisfroccs = Product.builder()
                .longName("Hilltop Irsai Olivér Kisfröccs")
                .shortName("Irsai Kisfröccs")
                .orderNumber(2)
                .salePrice(340)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildHilltopIrsaiOliverNagyfroccs() {
        HilltopIrsaiOliverNagyfroccs = Product.builder()
                .longName("Hilltop Irsai Olivér Nagyfröccs")
                .shortName("Irsai Nagyfröccs")
                .orderNumber(6)
                .salePrice(720)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildHilltopIrsaiOliverVicehazmester() {
        HilltopIrsaiOliverVicehazmester = Product.builder()
                .longName("Hilltop Irsai Olivér Viceházmester")
                .shortName("Irsai Vice")
                .orderNumber(10)
                .salePrice(800)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildHilltopIrsaiOliverHazmester() {
        HilltopIrsaiOliverHazmester = Product.builder()
                .longName("Hilltop Irsai Olivér Házmester")
                .shortName("Irsai Házmester")
                .orderNumber(14)
                .salePrice(1100)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildHilltopIrsaiOliverHosszulepes() {
        HilltopIrsaiOliverHosszulepes = Product.builder()
                .longName("Hilltop Irsai Olivér Hosszúlépés")
                .shortName("Irsai Hosszúlépés")
                .orderNumber(18)
                .salePrice(420)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildHilltopIrsaiOliverSportfroccs() {
        HilltopIrsaiOliverSportFroccs = Product.builder()
                .longName("Hilltop Irsai Olivér Sportföccs")
                .shortName("Irsai Sportföccs")
                .orderNumber(22)
                .salePrice(500)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildHazBoraLisiczaRoseCuveeKisfroccs() {
        HazBoraLisiczaRoseCuveeKisfroccs = Product.builder()
                .longName("Ház Bora Lisicza Rosé Cuvée Kisfröccs")
                .shortName("RHB Kisfröccs")
                .orderNumber(3)
                .rapidCode(0)
                .salePrice(300)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildHazBoraLisiczaRoseCuveeNagyfroccs() {
        HazBoraLisiczaRoseCuveeNagyfroccs = Product.builder()
                .longName("Ház Bora Lisicza Rosé Cuvée Nagyfröccs")
                .shortName("RHB Nagyfröccs")
                .orderNumber(7)
                .rapidCode(0)
                .salePrice(560)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildHazBoraLisiczaRoseCuveeVicehazmester() {
        HazBoraLisiczaRoseCuveeVicehazmester = Product.builder()
                .longName("Ház Bora Lisicza Rosé Cuvée Viceházmester")
                .shortName("RHB Vice")
                .orderNumber(11)
                .rapidCode(0)
                .salePrice(640)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildHazBoraLisiczaRoseCuveeHazmester() {
        HazBoraLisiczaRoseCuveeHazmester = Product.builder()
                .longName("Ház Bora Lisicza Rosé Cuvée Házmester")
                .shortName("RHB Házmester")
                .orderNumber(15)
                .rapidCode(0)
                .salePrice(860)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildHazBoraLisiczaRoseCuveeHosszulepes() {
        HazBoraLisiczaRoseCuveeHosszulepes = Product.builder()
                .longName("Ház Bora Lisicza Rosé Cuvée Hosszúlépés")
                .shortName("RHB Hosszúlépés")
                .orderNumber(19)
                .rapidCode(0)
                .salePrice(340)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildHazBoraLisiczaRoseCuveeSportfroccs() {
        HazBoraLisiczaRoseCuveeSportFroccs = Product.builder()
                .longName("Ház Bora Lisicza Rosé Cuvée Sportföccs")
                .shortName("RHB Sportföccs")
                .orderNumber(23)
                .rapidCode(0)
                .salePrice(420)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildMeszarosPinotKisfroccs() {
        MeszarosPinotNoirRoseKisfroccs = Product.builder()
                .longName("Mészáros Pinot Noir Rose Kisfröccs")
                .shortName("Mészáros Kisfröccs")
                .orderNumber(4)
                .salePrice(2400)
                .purchasePrice(400)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildMeszarosPinotNagyfroccs() {
        MeszarosPinotNoirRoseNagyfroccs = Product.builder()
                .longName("Mészáros Pinot Noir Rose Nagyfröccs")
                .shortName("Mészáros Nagyfröccs")
                .orderNumber(8)
                .salePrice(760)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildMeszarosPinotVicehazmester() {
        MeszarosPinotNoirRoseVicehazmester = Product.builder()
                .longName("Mészáros Pinot Noir Rose Viceházmester")
                .shortName("Mészáros Vice")
                .orderNumber(12)
                .salePrice(840)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildMeszarosPinotHazmester() {
        MeszarosPinotNoirRoseHazmester = Product.builder()
                .longName("Mészáros Pinot Noir Rose Házmester")
                .shortName("Mészáros Házmester")
                .orderNumber(16)
                .salePrice(1160)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildMeszarosPinotHosszulepes() {
        MeszarosPinotNoirRoseHosszulepes = Product.builder()
                .longName("Mészáros Pinot Noir Rose Hosszúlépés")
                .shortName("Mészáros Hosszúlépés")
                .orderNumber(20)
                .salePrice(440)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildMeszarosPinotSportfroccs() {
        MeszarosPinotNoirRoseSportFroccs = Product.builder()
                .longName("Mészáros Pinot Noir Rose Sprotfröccs")
                .shortName("Mészáros Sprotfröccs")
                .orderNumber(24)
                .salePrice(520)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildTorleyGalaSzaraz() {
        TorleyGalaSzaraz = Product.builder()
                .longName("Torley Gála Száraz")
                .shortName("Torley Sz")
                .orderNumber(12)
                .salePrice(2400)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(75)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildTorleyCharmantEdes() {
        TorleyCharmantEdes = Product.builder()
                .longName("Torley Charmant Édes")
                .shortName("Torley É")
                .orderNumber(13)
                .salePrice(2400)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(75)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildSzoda() {
        Szoda = Product.builder()
                .longName("Szóda")
                .shortName("Szóda")
                .orderNumber(14)
                .rapidCode(20)
                .salePrice(40)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.LITER)
                .storageMultiplier(10)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildJimBeam04() {
        JimBeam04 = Product.builder()
                .longName("Jim Beam 4cl")
                .shortName("Jim Beam 4cl")
                .orderNumber(5)
                .rapidCode(55)
                .salePrice(560)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildJohnnieWalkerRedLabel04() {
        JohnnieWalkerRedLabel04 = Product.builder()
                .longName("Johnnie Walker Red Label 4cl")
                .shortName("Johnnie 4cl")
                .orderNumber(6)
                .rapidCode(56)
                .salePrice(580)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildJackDaniels04() {
        JackDaniels04 = Product.builder()
                .longName("Jack Daniels 4cl")
                .shortName("Jack 4cl")
                .orderNumber(7)
                .rapidCode(57)
                .salePrice(780)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildTullamoreDew04() {
        TullamoreDew04 = Product.builder()
                .longName("Tullamore Dew 4cl")
                .shortName("Tullamore 4cl")
                .orderNumber(8)
                .rapidCode(58)
                .salePrice(780)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildRoyal04() {
        Royal04 = Product.builder()
                .longName("Royal 4cl")
                .shortName("Royal 4cl")
                .rapidCode(53)
                .orderNumber(3)
                .salePrice(380)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildFinlandia04() {
        Finlandia04 = Product.builder()
                .longName("Finlandia 4cl")
                .shortName("Finlandia 4cl")
                .orderNumber(4)
                .rapidCode(54)
                .salePrice(580)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildBacardiSuperior04() {
        BacardiSuperior04 = Product.builder()
                .longName("Bacardi Superior 4cl")
                .shortName("Bacardi 4cl")
                .orderNumber(10)
                .rapidCode(60)
                .salePrice(620)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildCaptainMorganSpicedRum04() {
        CaptainMorganSpicedRum04 = Product.builder()
                .longName("Captain Morgan Spiced Rum 4cl")
                .shortName("Captain 4cl")
                .orderNumber(9)
                .rapidCode(59)
                .salePrice(640)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildBeefeater04() {
        Beefeater04 = Product.builder()
                .longName("Beefeater 4cl")
                .shortName("Beefeater 4cl")
                .orderNumber(11)
                .rapidCode(61)
                .salePrice(620)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildTequilaSierraReposadoGold04() {
        TequilaSierraReposadoGold04 = Product.builder()
                .longName("Tequila Sierra Reposado Gold 4cl")
                .shortName("Tequila G 4cl")
                .orderNumber(13)
                .rapidCode(63)
                .salePrice(680)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildTequilaSierraSilver04() {
        TequilaSierraSilver04 = Product.builder()
                .longName("Tequila Sierra Silver 4cl")
                .shortName("Tequila S 4cl")
                .orderNumber(12)
                .rapidCode(62)
                .salePrice(680)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildUnicum04() {
        Unicum04 = Product.builder()
                .longName("Unicum 4cl")
                .shortName("Unicum 4cl")
                .orderNumber(1)
                .rapidCode(51)
                .salePrice(560)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildJagermeister04() {
        Jagermeister04 = Product.builder()
                .longName("Jagermeister 4cl")
                .shortName("Jager 4cl")
                .orderNumber(2)
                .rapidCode(52)
                .salePrice(580)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildBaileys08() {
        Baileys08 = Product.builder()
                .longName("Baileys 8cl")
                .shortName("Baileys 8cl")
                .orderNumber(14)
                .rapidCode(64)
                .salePrice(1280)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildJimBeam02() {
        JimBeam02 = Product.builder()
                .longName("Jim Beam 2cl")
                .shortName("Jim Beam 2cl")
                .orderNumber(19)
                .rapidCode(550)
                .salePrice(280)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildJohnnieWalkerRedLabel02() {
        JohnnieWalkerRedLabel02 = Product.builder()
                .longName("Johnnie Walker Red Label 2cl")
                .shortName("Johnnie 2cl")
                .orderNumber(20)
                .rapidCode(560)
                .salePrice(290)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildJackDaniels02() {
        JackDaniels02 = Product.builder()
                .longName("Jack Daniels 2cl")
                .shortName("Jack 2cl")
                .orderNumber(21)
                .rapidCode(570)
                .salePrice(390)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildTullamoreDew02() {
        TullamoreDew02 = Product.builder()
                .longName("Tullamore Dew 2cl")
                .shortName("Tullamore 2cl")
                .orderNumber(22)
                .rapidCode(580)
                .salePrice(390)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildRoyal02() {
        Royal02 = Product.builder()
                .longName("Royal 2cl")
                .shortName("Royal 2cl")
                .orderNumber(17)
                .rapidCode(530)
                .salePrice(190)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildFinlandia02() {
        Finlandia02 = Product.builder()
                .longName("Finlandia 2cl")
                .shortName("Finlandia 2cl")
                .orderNumber(18)
                .rapidCode(540)
                .salePrice(290)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildBacardiSuperior02() {
        BacardiSuperior02 = Product.builder()
                .longName("Bacardi Superior 2cl")
                .shortName("Bacardi 2cl")
                .orderNumber(24)
                .rapidCode(600)
                .salePrice(310)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildCaptainMorganSpicedRum02() {
        CaptainMorganSpicedRum02 = Product.builder()
                .longName("Captain Morgan SpicedRum 2cl")
                .shortName("Captain 2cl")
                .orderNumber(23)
                .rapidCode(590)
                .salePrice(320)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildBeefeater02() {
        Beefeater02 = Product.builder()
                .longName("Beefeater 2cl")
                .shortName("Beefeater 2cl")
                .orderNumber(25)
                .rapidCode(610)
                .salePrice(310)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildTequilaSierraReposadoGold02() {
        TequilaSierraReposadoGold02 = Product.builder()
                .longName("Tequila Sierra Reposado Gold 2cl")
                .shortName("Tequila G 2cl")
                .orderNumber(27)
                .rapidCode(630)
                .salePrice(340)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildTequilaSierraSilver02() {
        TequilaSierraSilver02 = Product.builder()
                .longName("Tequila Sierra Silver 2cl")
                .shortName("Tequila S 2cl")
                .orderNumber(26)
                .rapidCode(620)
                .salePrice(340)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildUnicum02() {
        Unicum02 = Product.builder()
                .longName("Unicum 2cl")
                .shortName("Unicum 2cl")
                .orderNumber(15)
                .rapidCode(510)
                .salePrice(280)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildJagermeister02() {
        Jagermeister02 = Product.builder()
                .longName("Jagermeister 2cl")
                .shortName("Jager 2cl")
                .orderNumber(16)
                .rapidCode(520)
                .salePrice(290)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildBaileys04() {
        Baileys04 = Product.builder()
                .longName("Baileys 4cl")
                .shortName("Baileys 4cl")
                .orderNumber(28)
                .rapidCode(640)
                .salePrice(640)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void build22KokuszTatratea04() {
        _22KokuszTatratea04 = Product.builder()
                .longName("22% Kókusz Tátratea 4cl")
                .shortName("22% TT 4cl")
                .orderNumber(1)
                .rapidCode(72)
                .salePrice(820)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void build32CitrusTatratea04() {
        _32CitrusTatratea04 = Product.builder()
                .longName("32% Citrus Tátratea 4cl")
                .shortName("32% TT 4cl")
                .orderNumber(2)
                .rapidCode(73)
                .salePrice(880)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void build42BarackTatratea04() {
        _42BarackTatratea04 = Product.builder()
                .longName("42% Barack Tátratea 4cl")
                .shortName("42% TT 4cl")
                .orderNumber(3)
                .rapidCode(74)
                .salePrice(940)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void build52EredetiTatratea04() {
        _52EredetiTatratea04 = Product.builder()
                .longName("52% Eredeti Tátratea 4cl")
                .shortName("52% TT 4cl")
                .orderNumber(4)
                .rapidCode(75)
                .salePrice(980)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void build62ErdeiGyumolcsTatratea04() {
        _62ErdeiGyumolcsTatratea04 = Product.builder()
                .longName("62% Erdei Gyümölcs Tátratea 4cl")
                .shortName("62% TT 4cl")
                .orderNumber(5)
                .rapidCode(76)
                .salePrice(1080)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void build72OutlawBetyarTatratea04() {
        _72OutlawBetyarTatratea04 = Product.builder()
                .longName("72% Outlaw Betyár Tátratea 4cl")
                .shortName("72% TT 4cl")
                .orderNumber(6)
                .rapidCode(77)
                .salePrice(1180)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildCseresznyePalinka04() {
        CseresznyePalinka04 = Product.builder()
                .longName("Cseresznye Pálinka 4cl")
                .shortName("Cseresznye 4cl")
                .orderNumber(7)
                .rapidCode(78)
                .salePrice(880)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(50)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildKajszibarackPalinka04() {
        KajszibarackPalinka04 = Product.builder()
                .longName("Kajszibarack Pálinka 4cl")
                .shortName("Kajszi 4cl")
                .orderNumber(8)
                .rapidCode(79)
                .salePrice(880)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(50)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildSzilvapalinka04() {
        Szilvapalinka04 = Product.builder()
                .longName("Szilva Pálinka 4cl")
                .shortName("Szilva 4cl")
                .orderNumber(9)
                .rapidCode(80)
                .salePrice(880)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(50)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void build22KokuszTatratea02() {
        _22KokuszTatratea02 = Product.builder()
                .longName("22% Kókusz Tátratea 2cl")
                .shortName("22% TT 2cl")
                .orderNumber(10)
                .salePrice(410)
                .rapidCode(720)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void build32CitrusTatratea02() {
        _32CitrusTatratea02 = Product.builder()
                .longName("32% Citrus Tátratea 2cl")
                .shortName("32% TT 2cl")
                .orderNumber(11)
                .salePrice(440)
                .rapidCode(730)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void build42BarackTatratea02() {
        _42BarackTatratea02 = Product.builder()
                .longName("42% Barack Tátratea 2cl")
                .shortName("42% TT 2cl")
                .orderNumber(12)
                .rapidCode(740)
                .salePrice(470)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void build52EredetiTatratea02() {
        _52EredetiTatratea02 = Product.builder()
                .longName("52% Eredeti Tátratea 2cl")
                .shortName("52% TT 2cl")
                .orderNumber(13)
                .rapidCode(750)
                .salePrice(490)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void build62ErdeiGyumolcsTatratea02() {
        _62ErdeiGyumolcsTatratea02 = Product.builder()
                .longName("62% Erdei Gyümölcs Tátratea 2cl")
                .shortName("62% TT 2cl")
                .orderNumber(14)
                .rapidCode(760)
                .salePrice(540)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void build72OutlawBetyarTatratea02() {
        _72OutlawBetyarTatratea02 = Product.builder()
                .longName("72% Outlaw Betyár Tátratea 2cl")
                .shortName("72% TT 2cl")
                .orderNumber(15)
                .rapidCode(770)
                .salePrice(590)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildCseresznyePalinka02() {
        CseresznyePalinka02 = Product.builder()
                .longName("Cseresznye Pálinka 2cl")
                .shortName("Cseresznye 2cl")
                .orderNumber(16)
                .rapidCode(780)
                .salePrice(440)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(50)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildKajszibarackPalinka02() {
        KajszibarackPalinka02 = Product.builder()
                .longName("Kajszibarack Pálinka 2cl")
                .shortName("Kajszi 2cl")
                .orderNumber(17)
                .rapidCode(790)
                .salePrice(440)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(50)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildSzilvapalinka02() {
        Szilvapalinka02 = Product.builder()
                .longName("Szilva Pálinka 2cl")
                .shortName("Szilva 2cl")
                .orderNumber(18)
                .rapidCode(800)
                .salePrice(440)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(50)
                .type(ProductType.SELLABLE)
                .build();
    }
    
    private void buildFinca1() {
        Finca1 = Product.builder()
                .longName("Finca 1db")
                .shortName("Finca 1db")
                .orderNumber(2)
                .salePrice(390)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(50)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildBang1() {
        Bang1 = Product.builder()
                .longName("Bang 1db")
                .shortName("Bang 1db")
                .orderNumber(1)
                .salePrice(390)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildImagine1() {
        Imagine1 = Product.builder()
                .longName("Imagine 1db")
                .shortName("Imagine 1db")
                .orderNumber(3)
                .salePrice(390)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildFinca6() {
        Finca6 = Product.builder()
                .longName("Finca 6db")
                .shortName("Finca 6db")
                .orderNumber(5)
                .salePrice(1890)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildBang6() {
        Bang6 = Product.builder()
                .longName("Bang 6db")
                .shortName("Bang 6db")
                .orderNumber(4)
                .salePrice(1890)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildImagine6() {
        Imagine6 = Product.builder()
                .longName("Imagine 6db")
                .shortName("Imagine 6db")
                .orderNumber(6)
                .salePrice(1890)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildRiffRaff6() {
        RiffRaff6 = Product.builder()
                .longName("Riff Raff 6db")
                .shortName("Riff Raff 6db")
                .orderNumber(7)
                .salePrice(1890)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildFinca12() {
        Finca12 = Product.builder()
                .longName("Finca 12db")
                .shortName("Finca 12db")
                .orderNumber(9)
                .salePrice(3480)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildBang12() {
        Bang12 = Product.builder()
                .longName("Bang 12db")
                .shortName("Bang 12db")
                .orderNumber(8)
                .salePrice(3480)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildImagine12() {
        Imagine12 = Product.builder()
                .longName("Imagine 12db")
                .shortName("Imagine 12db")
                .orderNumber(10)
                .salePrice(3480)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildRiffRaff12() {
        RiffRaff12 = Product.builder()
                .longName("Riff Raff 12db")
                .shortName("Riff Raff 12db")
                .orderNumber(11)
                .salePrice(3480)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }
    
    private void buildCocaCola() {
        CocaCola = Product.builder()
                .longName("Coca Cola")
                .shortName("Cola")
                .orderNumber(9)
                .rapidCode(31)
                .salePrice(340)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(25)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildCocaColaZero() {
        CocaColaZero = Product.builder()
                .longName("Coca Cola Zero")
                .shortName("Cola Zero")
                .orderNumber(10)
                .rapidCode(32)
                .salePrice(340)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(25)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildFantaNarancs() {
        FantaNarancs = Product.builder()
                .longName("Fanta Narancs")
                .shortName("Fanta")
                .orderNumber(11)
                .salePrice(340)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(25)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildSprite() {
        Sprite = Product.builder()
                .longName("Sprite")
                .shortName("Sprite")
                .orderNumber(12)
                .salePrice(340)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(25)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildKinleyGyomber() {
        KinleyGyomber = Product.builder()
                .longName("Kinley Gyömbér")
                .shortName("Gyömbér")
                .orderNumber(13)
                .rapidCode(33)
                .salePrice(340)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(25)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildKinleyTonic() {
        KinleyTonic = Product.builder()
                .longName("Kinley Tonic")
                .shortName("Tonic")
                .orderNumber(14)
                .rapidCode(34)
                .salePrice(340)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(25)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildNesteaCitrom() {
        NesteaCitrom = Product.builder()
                .longName("Nestea Citrom")
                .shortName("Nestea C")
                .orderNumber(15)
                .salePrice(340)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(25)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildNesteaBarack() {
        NesteaBarack = Product.builder()
                .longName("Nestea Barack")
                .shortName("Nestea B")
                .orderNumber(16)
                .salePrice(340)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(25)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildCappyAlma() {
        CappyAlma = Product.builder()
                .longName("Cappy Alma")
                .shortName("Alma")
                .orderNumber(5)
                .rapidCode(35)
                .salePrice(150)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildCappyNarancs() {
        CappyNarancs = Product.builder()
                .longName("Cappy Narancs")
                .shortName("Narancs")
                .orderNumber(6)
                .rapidCode(36)
                .salePrice(150)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildCappyBarack() {
        CappyBarack = Product.builder()
                .longName("Cappy Barack")
                .shortName("Barack")
                .orderNumber(7)
                .rapidCode(37)
                .salePrice(150)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildCappyAnanasz() {
        CappyAnanasz = Product.builder()
                .longName("Cappy Ananász")
                .shortName("Ananász")
                .orderNumber(8)
                .rapidCode(38)
                .salePrice(150)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(100)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildLimonadeMalna05() {
        LimonadeMalna05 = Product.builder()
                .longName("Limonádé Málna 5dl")
                .shortName("Limo M 5dl")
                .orderNumber(2)
                .rapidCode(42)
                .salePrice(790)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildLimonadeMeggy05() {
        LimonadeMeggy05 = Product.builder()
                .longName("Limonádé Meggy 5dl")
                .shortName("Limo Meggy 5dl")
                .orderNumber(4)
                .rapidCode(44)
                .salePrice(790)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildLimonadeEperNarancs05() {
        LimonadeEperNarancs05 = Product.builder()
                .longName("Limonádé Eper Narancs 5dl")
                .shortName("Limo E 5dl")
                .orderNumber(3)
                .rapidCode(43)
                .salePrice(790)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildLimonadeCitrus05() {
        LimonadeCitrus05 = Product.builder()
                .longName("Limonádé Citrus 5dl")
                .shortName("Limo C 5dl")
                .orderNumber(1)
                .rapidCode(41)
                .salePrice(790)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildLimonadeMalna10() {
        LimonadeMalna10 = Product.builder()
                .longName("Limonádé Málna 1L")
                .shortName("Limo M 1L")
                .orderNumber(19)
                .rapidCode(46)
                .salePrice(1390)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildLimonadeMeggy10() {
        LimonadeMeggy10 = Product.builder()
                .longName("Limonádé Meggy 1L")
                .shortName("Limo Meggy 1L")
                .orderNumber(20)
                .rapidCode(48)
                .salePrice(1390)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildLimonadeEperNarancs10() {
        LimonadeEperNarancs10 = Product.builder()
                .longName("Limonádé Eper Narancs  1L")
                .shortName("Limo E 1L")
                .rapidCode(47)
                .orderNumber(21)
                .salePrice(1390)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }

    private void buildLimonadeCitrus10() {
        LimonadeCitrus10 = Product.builder()
                .longName("Limonádé Citrus  1L")
                .shortName("Limo C 1L")
                .orderNumber(22)
                .rapidCode(45)
                .salePrice(1390)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.PARTIALLY_PAYABLE)
                .build();
    }
    
    private void buildNaturaquaSzensavas() {
        NaturaquaSzensavas = Product.builder()
                .longName("Naturaqua Szénsavas")
                .shortName("Savas")
                .orderNumber(17)
                .rapidCode(40)
                .salePrice(280)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(25)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildNaturaquaSzensavmentes() {
        NaturaquaSzensavmentes = Product.builder()
                .longName("Naturaqua Szénsavmentes")
                .shortName("Mentes")
                .orderNumber(18)
                .rapidCode(39)
                .salePrice(280)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(25)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildBurnOriginal() {
        BurnOriginal = Product.builder()
                .longName("Burn Original")
                .shortName("Burn O")
                .orderNumber(23)
                .salePrice(420)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(25)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildBurnZero() {
        BurnZero = Product.builder()
                .longName("Burn Zéro")
                .shortName("Burn Z")
                .orderNumber(24)
                .salePrice(420)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(25)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildMonsterEnergy() {
        MonsterEnergy = Product.builder()
                .longName("Monster Energy")
                .shortName("Monster E")
                .orderNumber(25)
                .salePrice(640)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(50)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildMonsterAssault() {
        MonsterAssault = Product.builder()
                .longName("Monster Assault")
                .shortName("Monster A")
                .orderNumber(26)
                .salePrice(640)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(50)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildMonsterRehab() {
        MonsterRehab = Product.builder()
                .longName("Monster Rehab")
                .shortName("Monster R")
                .orderNumber(27)
                .salePrice(640)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(50)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildDallmyrFekete() {
        DallmayrFekete = Product.builder()
                .longName("Dallmayr Fekete")
                .shortName("Dallmayr Fekete")
                .orderNumber(9)
                .salePrice(450)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildDallmyrGyumolcs() {
        DallmayrGyumolcs = Product.builder()
                .longName("Dallmayr Gyümölcs")
                .shortName("Dallmayr Gyümölcs")
                .orderNumber(10)
                .salePrice(450)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildDallmyrZold() {
        DallmayrZold = Product.builder()
                .longName("Dallmyar Zöld")
                .shortName("Dallmyar Zöld")
                .orderNumber(11)
                .salePrice(450)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildPiramisDarjeling() {
        PiramisDarjeling = Product.builder()
                .longName("Piramis Darjeling")
                .shortName("Piramis Darjeling")
                .orderNumber(12)
                .salePrice(490)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildPiramisMangoMaracuja() {
        PiramisMangoMaracuja = Product.builder()
                .longName("Piramis Mango Maracuja")
                .shortName("Piramis Mango Mar.")
                .orderNumber(13)
                .salePrice(490)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildPiramisAnanaszPapaja() {
        PiramisAnanaszPapaja = Product.builder()
                .longName("Piramis Ananász Papaja")
                .shortName("Piramis Ananász P.")
                .orderNumber(14)
                .salePrice(490)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildPiramisCitrusVerbena() {
        PiramisCitrusVerbena = Product.builder()
                .longName("Piramis Citrus Verbéna")
                .shortName("Piramis Citrus Verb.")
                .orderNumber(15)
                .salePrice(490)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildEspresso() {
        Espresso = Product.builder()
                .longName("Espresso")
                .shortName("Presszó")
                .orderNumber(1)
                .salePrice(340)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildAmericano() {
        Americano = Product.builder()
                .longName("Americano")
                .shortName("Americano")
                .orderNumber(5)
                .salePrice(390)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildCappuccino() {
        Cappuccino = Product.builder()
                .longName("Cappuccino")
                .shortName("Cappuccino")
                .orderNumber(3)
                .salePrice(470)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildCaffeLatte() {
        CaffeLatte = Product.builder()
                .longName("Caffe Latte")
                .shortName("Latte")
                .orderNumber(2)
                .salePrice(490)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildLatteMacchiato() {
        LatteMacchiato = Product.builder()
                .longName("Latte Macchiato")
                .shortName("Macchiato")
                .orderNumber(6)
                .salePrice(520)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildCaffeMelange() {
        CaffeMelange = Product.builder()
                .longName("Caffe Melange")
                .shortName("Melange")
                .orderNumber(4)
                .salePrice(590)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildForroCsokiBarna() {
        ForroCsokiBarna = Product.builder()
                .longName("Forró Csoki Barna")
                .shortName("Forró Csoki B")
                .orderNumber(7)
                .salePrice(740)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildForroCsokiFeher() {
        ForroCsokiFeher = Product.builder()
                .longName("Forró Csoki Fehér")
                .shortName("Forró Csoki F")
                .orderNumber(8)
                .salePrice(780)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildCaptainAndGyomber() {
        CaptainAndGyomber = Product.builder()
                .longName("Captain And Gyömbér")
                .shortName("C Gy")
                .orderNumber(1)
                .salePrice(980)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildGinTonic() {
        GinTonic = Product.builder()
                .longName("Gin Tonic")
                .shortName("Gin Tonic")
                .orderNumber(2)
                .salePrice(960)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildJackAndCoke() {
        JackAndCoke = Product.builder()
                .longName("Jack And Coke")
                .shortName("J C")
                .orderNumber(3)
                .salePrice(1120)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }

    private void buildVodkaSzoda() {
        VodkaSzoda = Product.builder()
                .longName("Vodka Szóda")
                .shortName("Vodka Szóda")
                .orderNumber(4)
                .salePrice(700)
                .purchasePrice(500)
                .status(ProductStatus.ACTIVE)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(70)
                .type(ProductType.SELLABLE)
                .build();
    }
    
    private void buildProductCategories() {
        buildRoot();
        buildEtlap();
            buildRagcsak();
                buildSos();
                buildEdes();
            buildEtelek();

        buildItallap();
            buildShotok();
            buildSorok();
                buildCsapolt();
                buildUveges();
            buildBorok();
                buildUveg();
                buildBorDecire();
                buildFroccsok();
            buildRovidek();
            buildPalinkak();
        buildUdito();
            buildUvegesUdito();
            buildLimo();
            buildUditoDecire();
        buildForroItalok();
            buildTea();
                buildFilteres();
                buildPiramis();
            buildForraltBor();
            buildKave();
        buildAkciosItalok();

        buildMenukAggregate();
            buildMenuk();

        buildAlapanyagokAggregate();
            buildAlapanyagok();
        
        buildPseudoAdHoc();
        buildPseudoGameFee();

        
        /*----- ETLAP -----*/
        // Rágcsák
        buildPseudoSosPerec();
        buildPseudoMogyoro();
        buildPseudoRagcsaMix1Perec2Mogyi();
        buildPseudoRagcsaMix2Perec1Mogyi();
        buildPseudoRagcsaMix3Perec();
        buildPseudoNachosSosSajt();
        buildPseudoNachosSosChili();
        buildPseudoNachosBBQSajt();
        buildPseudoNachosBBQChili();
        buildPseudoChips();
        buildPseudoPopcorn();
        buildPseudoGumicukor();
        buildPseudoBalatonszelet();
        buildPseudoCsoki();

        // Ételek
        buildPseudoMelegszendivcsSonkas();
        buildPseudoMelegszendivcsSzalamis();
        buildPseudoMelegszendivcsVega();
        buildPseudoSajtosCsikok();
        buildPseudoZsirosDeszka();
        buildPseudoWrap();
        buildPseudoSpecialisFeltetekPiritossalTonhal();
        buildPseudoSpecialisFeltetekPiritossalPadlizsan();
        buildPseudoSajttal();
        buildPseudoGameUpTal();
        buildPseudoGameUpFalankTal();

        // Menuk
        buildPseudoAgentShotCover();
        buildPseudoLimonCept();
        buildPseudoSplendBor();
        buildPseudoTatraTime();
        buildPseudoSorrelAzEmberisegEllen();

        // Alapanyagok
        buildPseudoCitrom();
        buildPseudoNarancs();

        buildPseudoMalnaSzirup();
        buildPseudoEperNarancsSzirup();
        buildPseudoCitrusSzirup();
        buildPseudoMeggySzirup();

        buildPseudoTabasco();
        buildPseudoBlueCuracao();

        buildPseudoFagyiGyumi();

        buildPseudoBarnaCukor();
        buildPseudoFeherCukor();
        buildPseudoMez();

        buildPseudoTej();

        buildPseudoTortillaSos();
        buildPseudoTortillaBBQ();
        buildPseudoNachosSajtszosz();
        buildPseudoNachosChiliszosz();

        buildPseudoBagett();
        buildPseudoZsir();

        buildPseudoLilahagyma();
        buildPseudoPirospaprika();
        buildPseudoSalata();
        buildPseudoParadicsom();
        buildPseudoUborka();
        buildPseudoPiritottHagyma();

        buildPseudoTrappista();
        buildPseudoCheddar();
        buildPseudoKecskesajt();
        buildPseudoCamambert();

        buildPseudoSonka();
        buildPseudoSzalami();
        buildPseudoTonhalKrem();

        buildPseudoMargarin();

        buildPseudoMustar();
        buildPseudoMajonez();
        buildPseudoAjvar();

        buildPseudoTortillaLap();
        
        //Csapolt Sorok
        buildPseudoSoproni03();
        buildPseudoSoproni05();
        buildPseudoEdelweiss03();
        buildPseudoEdelweiss05();

        // Uveges sorok
        buildPseudoKrusoviceSvetle();
        buildPseudoSoproniDemon();
        buildPseudoSoproniMaxx();
        buildPseudoHeineken();
        buildPseudoGosserNaturRadler();
        buildPseudoGosserNaturRadler00();
        buildPseudoBekesSzentadrasiMeggyes();
        buildPseudoStrongbowDarkfruit();
        buildPseudoStrongbowGoldAppleCider();
        buildPseudoEdelweiss();

        // Borok
        buildPseudoHazBoraNagyEsNagy();
        buildPseudoHilltopIrsaiOliver();
        buildPseudoHilltopIrsaiOliverDecire();
        buildPseudoGereAttilaOlaszrizling();

        buildPseudoHazBoraLisiczaRoseCuvee();
        buildPseudoMeszarosPinot();
        buildPseudoMeszarosPinotDecire();

        buildPseudoHazBoraPolgarSerumVeritas();
        buildPseudoVinczeMerlot();
        buildPseudoVylyanCabernetSauvignon();
        buildPseudoMeszarosHidasptereCabernetFrancReserve();

        // Fröccsök
        buildPseudoHazBoraNagyEsNagyKisfroccs();
        buildPseudoHazBoraNagyEsNagyNagyfroccs();
        buildPseudoHazBoraNagyEsNagyVicehazmester();
        buildPseudoHazBoraNagyEsNagyHazmester();
        buildPseudoHazBoraNagyEsNagyHosszulepes();
        buildPseudoHazBoraNagyEsNagySportfroccs();

        buildPseudoHilltopIrsaiOliverKisfroccs();
        buildPseudoHilltopIrsaiOliverNagyfroccs();
        buildPseudoHilltopIrsaiOliverVicehazmester();
        buildPseudoHilltopIrsaiOliverHazmester();
        buildPseudoHilltopIrsaiOliverHosszulepes();
        buildPseudoHilltopIrsaiOliverSportfroccs();

        buildPseudoHazBoraLisiczaRoseCuveeKisfroccs();
        buildPseudoHazBoraLisiczaRoseCuveeNagyfroccs();
        buildPseudoHazBoraLisiczaRoseCuveeVicehazmester();
        buildPseudoHazBoraLisiczaRoseCuveeHazmester();
        buildPseudoHazBoraLisiczaRoseCuveeHosszulepes();
        buildPseudoHazBoraLisiczaRoseCuveeSportfroccs();

        buildPseudoMeszarosPinotKisfroccs();
        buildPseudoMeszarosPinotNagyfroccs();
        buildPseudoMeszarosPinotVicehazmester();
        buildPseudoMeszarosPinotHazmester();
        buildPseudoMeszarosPinotHosszulepes();
        buildPseudoMeszarosPinotSportfroccs();
        
        // Pezsgok
        buildPseudoTorleyGalaSzaraz();
        buildPseudoTorleyCharmantEdes();

        // Roviditalok
        buildPseudoJimBeam04();
        buildPseudoJohnnieWalkerRedLabel04();
        buildPseudoJackDaniels04();
        buildPseudoTullamoreDew04();

        buildPseudoRoyal04();
        buildPseudoFinlandia04();

        buildPseudoBacardiSuperior04();
        buildPseudoCaptainMorganSpicedRum04();

        buildPseudoBeefeater04();

        buildPseudoTequilaSierraReposadoGold04();
        buildPseudoTequilaSierraSilver04();

        buildPseudoUnicum04();
        buildPseudoJagermeister04();
        buildPseudoBaileys08();

        buildPseudoJimBeam02();
        buildPseudoJohnnieWalkerRedLabel02();
        buildPseudoJackDaniels02();
        buildPseudoTullamoreDew02();

        buildPseudoRoyal02();
        buildPseudoFinlandia02();

        buildPseudoBacardiSuperior02();
        buildPseudoCaptainMorganSpicedRum02();

        buildPseudoBeefeater02();

        buildPseudoTequilaSierraReposadoGold02();
        buildPseudoTequilaSierraSilver02();

        buildPseudoUnicum02();
        buildPseudoJagermeister02();
        buildPseudoBaileys04();

        // Palinkak
        buildPseudo22KokuszTatratea04();
        buildPseudo32CitrusTatratea04();
        buildPseudo42BarackTatratea04();
        buildPseudo52EredetiTatratea04();
        buildPseudo62ErdeiGyumolcsTatratea04();
        buildPseudo72OutlawBetyarTatratea04();

        buildPseudoCseresznyePalinka04();
        buildPseudoKajszibarackPalinka04();
        buildPseudoSzilvapalinka04();

        buildPseudo22KokuszTatratea02();
        buildPseudo32CitrusTatratea02();
        buildPseudo42BarackTatratea02();
        buildPseudo52EredetiTatratea02();
        buildPseudo62ErdeiGyumolcsTatratea02();
        buildPseudo72OutlawBetyarTatratea02();

        buildPseudoCseresznyePalinka02();
        buildPseudoKajszibarackPalinka02();
        buildPseudoSzilvapalinka02();

        // Shotok
        buildPseudoFinca1();
        buildPseudoBang1();
        buildPseudoImagine1();

        buildPseudoFinca6();
        buildPseudoBang6();
        buildPseudoImagine6();
        buildPseudoRiffRaff6();

        buildPseudoFinca12();
        buildPseudoBang12();
        buildPseudoImagine12();
        buildPseudoRiffRaff12();

        // Uveges uditok
        buildPseudoCocaCola();
        buildPseudoCocaColaZero();
        buildPseudoFantaNarancs();
        buildPseudoSprite();
        buildPseudoKinleyGyomber();
        buildPseudoKinleyTonic();
        buildPseudoNesteaCitrom();
        buildPseudoNesteaBarack();

        // Kimert uditok
        buildPseudoCappyAlma();
        buildPseudoCappyNarancs();
        buildPseudoCappyBarack();
        buildPseudoCappyAnanasz();

        // Limonadek
        buildPseudoLimonadeMalna05();
        buildPseudoLimonadeMeggy05();
        buildPseudoLimonadeEperNarancs05();
        buildPseudoLimonadeCitrus05();

        buildPseudoLimonadeMalna10();
        buildPseudoLimonadeMeggy10();
        buildPseudoLimonadeEperNarancs10();
        buildPseudoLimonadeCitrus10();

        // Asvanyviz
        buildPseudoNaturaquaSzensavas();
        buildPseudoNaturaquaSzensavmentes();
        buildPseudoSzoda();

        // Energiaitalok
        buildPseudoBurnOriginal();
        buildPseudoBurnZero();
        buildPseudoMonsterEnergy();
        buildPseudoMonsterAssault();
        buildPseudoMonsterRehab();

        // Filteres Teaak
        buildPseudoDallmyrFekete();
        buildPseudoDallmyrGyumolcs();
        buildPseudoDallmyrZold();

        buildPseudoPiramis1();
        buildPseudoPiramis2();
        buildPseudoPiramis3();
        buildPseudoPiramis4();

        // Kavek
        buildPseudoEspresso();
        buildPseudoAmericano();
        buildPseudoCappuccino();
        buildPseudoCaffeLatte();
        buildPseudoLatteMacchiato();
        buildPseudoCaffeMelange();
        buildPseudoForroCsokiBarna();
        buildPseudoForroCsokiFeher();

        // Napi akciok
        buildPseudoCaptainAndGyomber();
        buildPseudoGinTonic();
        buildPseudoJackAndCoke();
        buildPseudoVodkaSzoda();
    }

    private void buildPriceModifiers() {
        buildPriceModifierEdelweiss();
        buildPriceModifierCaptainAndGyomber();
        buildPriceModifierGinTonic();
        buildPriceModifierJackAndCoke();
        buildPriceModifierVodkaSzoda();
        buildPriceModifierMeggyesSor();
    }

    private void buildRecipes() {
        /* ----- ETLAP -----*/
        // Rágcsák
        buildSosPerecRecipe1();
        buildMogyoroRecipe1();
        buildRagcsaMix1Perec2MogyiRecipe1();
        buildRagcsaMix2Perec1MogyiRecipe1();
        buildRagcsaMix3PerecRecipe1();
        buildNachosSosSajtRecipe1();
        buildNachosSosChiliRecipe1();
        buildNachosBBQSajtRecipe1();
        buildNachosBBQChiliRecipe1();
        buildChipsRecipe1();
        buildPopcornRecipe1();
        buildGumicukorRecipe1();
        buildBalatonszeletRecipe1();
        buildCsokiRecipe1();

        // Ételek
        buildMelegszendivcsSonkasRecipe1();
        buildMelegszendivcsSzalamisRecipe1();
        buildMelegszendivcsVegaRecipe1();
        buildSajtosCsikokRecipe1();
        buildZsirosDeszkaRecipe1();
        buildWrapRecipe1();
        buildSpecialisFeltetekPiritossalTonhalRecipe1();
        buildSpecialisFeltetekPiritossalPadlizsanRecipe1();
        buildSajttalRecipe1();
        buildGameUpTalRecipe1();
        buildGameUpFalankTalRecipe1();

        // Menuk
        buildAgentShotCoverRecipe1();
        buildLimonCeptRecipe1();
        buildSplendBorRecipe1();
        buildTatraTimeRecipe1();
        buildSorrelAzEmberisegEllenRecipe1();

        
        /*----- ITALLAP -----*/
        //Csapolt Sorok
        buildSoproni03Recipe1();
        buildSoproni05Recipe1();
        buildEdelweiss03Recipe1();
        buildEdelweiss05Recipe1();

        // Uveges sorok
        buildKrusoviceSvetleRecipe1();
        buildSoproniDemonRecipe1();
        buildSoproniMaxxRecipe1();
        buildHeinekenRecipe1();
        buildGosserNaturRadlerRecipe1();
        buildGosserNaturRadler00Recipe1();
        buildBekesSzentandrasiMeggyesRecipe1();
        buildStrongbowDarkfruitRecipe1();
        buildStrongbowGoldAppleCiderRecipe1();
        buildEdelweissRecipe1();

        // Borok
        buildHazBoraNagyEsNagyRecipe1();
        buildHilltopIrsaiOliverRecipe1();
        buildHilltopIrsaiOliverDecireRecipe1();
        buildGereAttilaOlaszrizlingRecipe1();

        buildHazBoraLisiczaRoseCuveeRecipe1();
        buildMeszarosPinotRecipe1();
        buildMeszarosPinotDecireRecipe1();


        buildHazBoraPolgarSerumVeritasRecipe1();
        buildVinczeMerlotRecipe1();
        buildVylyanCabernetSauvignonRecipe1();
        buildMeszarosHidasptereCabernetFrancReserveRecipe1();
        
        // Pezsgok
        buildTorleyGalaSzarazRecipe1();
        buildTorleyCharmantEdesRecipe1();

        // Fröccsök
        buildHazBoraNagyEsNagyKisfroccsRecipe1();
        buildHazBoraNagyEsNagyNagyfroccsRecipe1();
        buildHazBoraNagyEsNagyVicehazmesterRecipe1();
        buildHazBoraNagyEsNagyHazmesterRecipe1();
        buildHazBoraNagyEsNagyHosszulepesRecipe1();
        buildHazBoraNagyEsNagySportfroccsRecipe1();

        buildHilltopIrsaiOliverKisfroccsRecipe1();
        buildHilltopIrsaiOliverNagyfroccsRecipe1();
        buildHilltopIrsaiOliverVicehazmesterRecipe1();
        buildHilltopIrsaiOliverHazmesterRecipe1();
        buildHilltopIrsaiOliverHosszulepesRecipe1();
        buildHilltopIrsaiOliverSportfroccsRecipe1();

        buildHazBoraLisiczaRoseCuveeKisfroccsRecipe1();
        buildHazBoraLisiczaRoseCuveeNagyfroccsRecipe1();
        buildHazBoraLisiczaRoseCuveeVicehazmesterRecipe1();
        buildHazBoraLisiczaRoseCuveeHazmesterRecipe1();
        buildHazBoraLisiczaRoseCuveeHosszulepesRecipe1();
        buildHazBoraLisiczaRoseCuveeSportfroccsRecipe1();

        buildMeszarosPinotKisfroccsRecipe1();
        buildMeszarosPinotNagyfroccsRecipe1();
        buildMeszarosPinotVicehazmesterRecipe1();
        buildMeszarosPinotHazmesterRecipe1();
        buildMeszarosPinotHosszulepesRecipe1();
        buildMeszarosPinotSportfroccsRecipe1();

        // Roviditalok
        buildJimBeam04Recipe1();
        buildJohnnieWalkerRedLabel04Recipe1();
        buildJackDaniels04Recipe1();
        buildTullamoreDew04Recipe1();

        buildRoyal04Recipe1();
        buildFinlandia04Recipe1();

        buildBacardiSuperior04Recipe1();
        buildCaptainMorganSpicedRum04Recipe1();

        buildBeefeater04Recipe1();

        buildTequilaSierraReposadoGold04Recipe1();
        buildTequilaSierraSilver04Recipe1();

        buildUnicum04Recipe1();
        buildJagermeister04Recipe1();
        buildBaileys08Recipe1();

        buildJimBeam02Recipe1();
        buildJohnnieWalkerRedLabel02Recipe1();
        buildJackDaniels02Recipe1();
        buildTullamoreDew02Recipe1();

        buildRoyal02Recipe1();
        buildFinlandia02Recipe1();

        buildBacardiSuperior02Recipe1();
        buildCaptainMorganSpicedRum02Recipe1();

        buildBeefeater02Recipe1();

        buildTequilaSierraReposadoGold02Recipe1();
        buildTequilaSierraSilver02Recipe1();

        buildUnicum02Recipe1();
        buildJagermeister02Recipe1();
        buildBaileys04Recipe1();

        // Palinkak
        build22KokuszTatratea04Recipe1();
        build32CitrusTatratea04Recipe1();
        build42BarackTatratea04Recipe1();
        build52EredetiTatratea04Recipe1();
        build62ErdeiGyumolcsTatratea04Recipe1();
        build72OutlawBetyarTatratea04Recipe1();

        buildCseresznyePalinka04Recipe1();
        buildKajszibarackPalinka04Recipe1();
        buildSzilvapalinka04Recipe1();

        build22KokuszTatratea02Recipe1();
        build32CitrusTatratea02Recipe1();
        build42BarackTatratea02Recipe1();
        build52EredetiTatratea02Recipe1();
        build62ErdeiGyumolcsTatratea02Recipe1();
        build72OutlawBetyarTatratea02Recipe1();

        buildCseresznyePalinka02Recipe1();
        buildKajszibarackPalinka02Recipe1();
        buildSzilvapalinka02Recipe1();

        // Shotok
        buildFinca1Recipe1();
        buildBang1Recipe1();
        buildImagine1Recipe1();

        buildFinca6Recipe1();
        buildBang6Recipe1();
        buildImagine6Recipe1();
        buildRiffRaff6Recipe1();

        buildFinca12Recipe1();
        buildBang12Recipe1();
        buildImagine12Recipe1();
        buildRiffRaff12Recipe1();

        // Uveges uditok
        buildCocaColaRecipe1();
        buildCocaColaZeroRecipe1();
        buildFantaNarancsRecipe1();
        buildSpriteRecipe1();
        buildKinleyGyomberRecipe1();
        buildKinleyTonicRecipe1();
        buildNesteaCitromRecipe1();
        buildNesteaBarackRecipe1();

        // Kimert uditok
        buildCappyAlmaRecipe1();
        buildCappyNarancsRecipe1();
        buildCappyBarackRecipe1();
        buildCappyAnanaszRecipe1();

        // Limonadek
        buildLimonadeMalna05Recipe1();
        buildLimonadeMeggy05Recipe1();
        buildLimonadeEperNarancs05Recipe1();
        buildLimonadeCitrus05Recipe1();

        buildLimonadeMalna10Recipe1();
        buildLimonadeMeggy10Recipe1();
        buildLimonadeEperNarancs10Recipe1();
        buildLimonadeCitrus10Recipe1();

        // Asvanyviz
        buildNaturaquaSzensavasRecipe1();
        buildNaturaquaSzensavmentesRecipe1();
        buildSzodaRecipe1();

        // Energiaitalok
        buildBurnOriginalRecipe1();
        buildBurnZeroRecipe1();
        buildMonsterEnergyRecipe1();
        buildMonsterAssaultRecipe1();
        buildMonsterRehabRecipe1();

        // Filteres Teaak
        buildDallmyrFeketeRecipe1();
        buildDallmyrGyumolcsRecipe1();
        buildDallmyrZoldRecipe1();

        buildPiramisDarjelingRecipe1();
        buildPiramisMangoMaracujaRecipe1();
        buildPiramisAnanaszPapajaRecipe1();
        buildPiramisCitrusVerbenaRecipe1();

        // Kavek
        buildEspressoRecipe1();
        buildAmericanoRecipe1();
        buildCappuccinoRecipe1();
        buildCaffeLatteRecipe1();
        buildLatteMacchiatoRecipe1();
        buildCaffeMelangeRecipe1();
        buildForroCsokiBarnaRecipe1();
        buildForroCsokiFeherRecipe1();

        // Napi akciok
        buildCaptainAndGyomberRecipe1();
        buildGinTonicRecipe1();
        buildJackAndCokeRecipe1();
        buildVodkaSzodaRecipe1();
    }

    private void buildSosPerecRecipe1() {
        SosPerecRecipe1 = Recipe.builder()
                .owner(SosPerec)
                .component(SosPerec)
                .quantityMultiplier(45)
                .build();
    }

    private void buildMogyoroRecipe1() {
        MogyoroRecipe1 = Recipe.builder()
                .owner(Mogyoro)
                .component(Mogyoro)
                .quantityMultiplier(105)
                .build();
    }

    private void buildRagcsaMix1Perec2MogyiRecipe1() {
        RagcsaMix1Perec2MogyiRecipe1 = Recipe.builder()
                .owner(RagcsaMix1Perec2Mogyi)
                .component(SosPerec)
                .quantityMultiplier(45)
                .build();

        RagcsaMix1Perec2MogyiRecipe2 = Recipe.builder()
                .owner(RagcsaMix1Perec2Mogyi)
                .component(Mogyoro)
                .quantityMultiplier(200)
                .build();
    }

    private void buildRagcsaMix2Perec1MogyiRecipe1() {
        RagcsaMix2Perec1MogyiRecipe1 = Recipe.builder()
                .owner(RagcsaMix2Perec1Mogyi)
                .component(SosPerec)
                .quantityMultiplier(90)
                .build();

        RagcsaMix2Perec1MogyiRecipe2 = Recipe.builder()
                .owner(RagcsaMix2Perec1Mogyi)
                .component(Mogyoro)
                .quantityMultiplier(105)
                .build();
    }

    private void buildRagcsaMix3PerecRecipe1() {
        RagcsaMix3PerecRecipe1 = Recipe.builder()
                .owner(RagcsaMix3Perec)
                .component(SosPerec)
                .quantityMultiplier(135)
                .build();
    }

    private void buildNachosSosSajtRecipe1() {
        NachosSosSajtRecipe1 = Recipe.builder()
                .owner(NachosSosSajt)
                .component(TortillaSos)
                .quantityMultiplier(95)
                .build();

        NachosSosSajtRecipe2 = Recipe.builder()
                .owner(NachosSosSajt)
                .component(NachosSajtszosz)
                .quantityMultiplier(105)
                .build();
    }

    private void buildNachosSosChiliRecipe1() {
        NachosSosChiliRecipe1 = Recipe.builder()
                .owner(NachosSosChili)
                .component(TortillaSos)
                .quantityMultiplier(95)
                .build();

        NachosSosChiliRecipe2 = Recipe.builder()
                .owner(NachosSosChili)
                .component(NachosChiliszosz)
                .quantityMultiplier(105)
                .build();
    }

    private void buildNachosBBQSajtRecipe1() {
        NachosBBQSajtRecipe1 = Recipe.builder()
                .owner(NachosBBQSajt)
                .component(TortillaBBQ)
                .quantityMultiplier(95)
                .build();

        NachosBBQSajtRecipe2 = Recipe.builder()
                .owner(NachosBBQSajt)
                .component(NachosSajtszosz)
                .quantityMultiplier(105)
                .build();
    }

    private void buildNachosBBQChiliRecipe1() {
        NachosBBQChiliRecipe1 = Recipe.builder()
                .owner(NachosBBQChili)
                .component(TortillaBBQ)
                .quantityMultiplier(95)
                .build();

        NachosBBQChiliRecipe2 = Recipe.builder()
                .owner(NachosBBQChili)
                .component(NachosChiliszosz)
                .quantityMultiplier(105)
                .build();
    }

    private void buildChipsRecipe1() {
        ChipsRecipe1 = Recipe.builder()
                .owner(Chips)
                .component(Chips)
                .quantityMultiplier(75)
                .build();
    }

    private void buildPopcornRecipe1() {
        PopcornRecipe1 = Recipe.builder()
                .owner(Popcorn)
                .component(Popcorn)
                .quantityMultiplier(75)
                .build();
    }

    private void buildGumicukorRecipe1() {
        GumicukorRecipe1 = Recipe.builder()
                .owner(Gumicukor)
                .component(Gumicukor)
                .quantityMultiplier(130)
                .build();
    }

    private void buildBalatonszeletRecipe1() {
        BalatonszeletRecipe1 = Recipe.builder()
                .owner(Balatonszelet)
                .component(Balatonszelet)
                .quantityMultiplier(75)
                .build();
    }

    private void buildCsokiRecipe1() {
        CsokiRecipe1 = Recipe.builder()
                .owner(Csoki)
                .component(Csoki)
                .quantityMultiplier(75)
                .build();
    }

    private void buildMelegszendivcsSonkasRecipe1() {
        MelegszendivcsSonkasRecipe1 = Recipe.builder()
                .owner(MelegszendivcsSonkas)
                .component(Bagett)
                .quantityMultiplier(125)
                .build();
        MelegszendivcsSonkasRecipe2 = Recipe.builder()
                .owner(MelegszendivcsSonkas)
                .component(Sonka)
                .quantityMultiplier(45)
                .build();
        MelegszendivcsSonkasRecipe3 = Recipe.builder()
                .owner(MelegszendivcsSonkas)
                .component(Margarin)
                .quantityMultiplier(10)
                .build();
        MelegszendivcsSonkasRecipe4 = Recipe.builder()
                .owner(MelegszendivcsSonkas)
                .component(Trappista)
                .quantityMultiplier(35)
                .build();
        MelegszendivcsSonkasRecipe5 = Recipe.builder()
                .owner(MelegszendivcsSonkas)
                .component(Salata)
                .quantityMultiplier(3)
                .build();
        MelegszendivcsSonkasRecipe6 = Recipe.builder()
                .owner(MelegszendivcsSonkas)
                .component(Paradicsom)
                .quantityMultiplier(15)
                .build();
    }

    private void buildMelegszendivcsSzalamisRecipe1() {
        MelegszendivcsSzalamisRecipe1 = Recipe.builder()
                .owner(MelegszendivcsSzalamis)
                .component(Bagett)
                .quantityMultiplier(125)
                .build();
        MelegszendivcsSzalamisRecipe2 = Recipe.builder()
                .owner(MelegszendivcsSzalamis)
                .component(Szalami)
                .quantityMultiplier(22)
                .build();
        MelegszendivcsSzalamisRecipe3 = Recipe.builder()
                .owner(MelegszendivcsSzalamis)
                .component(Margarin)
                .quantityMultiplier(10)
                .build();
        MelegszendivcsSzalamisRecipe4 = Recipe.builder()
                .owner(MelegszendivcsSzalamis)
                .component(Trappista)
                .quantityMultiplier(35)
                .build();
        MelegszendivcsSzalamisRecipe5 = Recipe.builder()
                .owner(MelegszendivcsSzalamis)
                .component(Salata)
                .quantityMultiplier(3)
                .build();
        MelegszendivcsSzalamisRecipe6 = Recipe.builder()
                .owner(MelegszendivcsSzalamis)
                .component(Paradicsom)
                .quantityMultiplier(15)
                .build();
    }

    private void buildMelegszendivcsVegaRecipe1() {
        MelegszendivcsVegaRecipe1 = Recipe.builder()
                .owner(MelegszendivcsVega)
                .component(Bagett)
                .quantityMultiplier(125)
                .build();
        MelegszendivcsVegaRecipe2 = Recipe.builder()
                .owner(MelegszendivcsVega)
                .component(Ajvar)
                .quantityMultiplier(15)
                .build();
        MelegszendivcsVegaRecipe3 = Recipe.builder()
                .owner(MelegszendivcsVega)
                .component(Trappista)
                .quantityMultiplier(35)
                .build();
        MelegszendivcsVegaRecipe4 = Recipe.builder()
                .owner(MelegszendivcsVega)
                .component(Camambert)
                .quantityMultiplier(25)
                .build();
        MelegszendivcsVegaRecipe5 = Recipe.builder()
                .owner(MelegszendivcsVega)
                .component(Salata)
                .quantityMultiplier(3)
                .build();
        MelegszendivcsVegaRecipe6 = Recipe.builder()
                .owner(MelegszendivcsVega)
                .component(Paradicsom)
                .quantityMultiplier(15)
                .build();
    }

    private void buildSajtosCsikokRecipe1() {
        SajtosCsikokRecipe1 = Recipe.builder()
                .owner(SajtosCsikok)
                .component(Bagett)
                .quantityMultiplier(125)
                .build();
        SajtosCsikokRecipe2 = Recipe.builder()
                .owner(SajtosCsikok)
                .component(Margarin)
                .quantityMultiplier(10)
                .build();
        SajtosCsikokRecipe3 = Recipe.builder()
                .owner(SajtosCsikok)
                .component(Trappista)
                .quantityMultiplier(40)
                .build();
        SajtosCsikokRecipe4 = Recipe.builder()
                .owner(SajtosCsikok)
                .component(Salata)
                .quantityMultiplier(3)
                .build();
        SajtosCsikokRecipe5 = Recipe.builder()
                .owner(SajtosCsikok)
                .component(Paradicsom)
                .quantityMultiplier(15)
                .build();
    }

    private void buildZsirosDeszkaRecipe1() {
        ZsirosDeszkaRecipe1 = Recipe.builder()
                .owner(ZsirosDeszka)
                .component(Bagett)
                .quantityMultiplier(125)
                .build();
        ZsirosDeszkaRecipe2 = Recipe.builder()
                .owner(ZsirosDeszka)
                .component(Zsir)
                .quantityMultiplier(15)
                .build();
        ZsirosDeszkaRecipe3 = Recipe.builder()
                .owner(ZsirosDeszka)
                .component(Lilahagyma)
                .quantityMultiplier(15)
                .build();
        ZsirosDeszkaRecipe4 = Recipe.builder()
                .owner(ZsirosDeszka)
                .component(Pirospaprika)
                .quantityMultiplier(8)
                .build();
    }

    private void buildWrapRecipe1() {
        WrapRecipe1 = Recipe.builder()
                .owner(Wrap)
                .component(TortillaLap)
                .quantityMultiplier(90)
                .build();
        WrapRecipe2 = Recipe.builder()
                .owner(Wrap)
                .component(Mustar)
                .quantityMultiplier(20)
                .build();
        WrapRecipe3 = Recipe.builder()
                .owner(Wrap)
                .component(Majonez)
                .quantityMultiplier(20)
                .build();
        WrapRecipe4 = Recipe.builder()
                .owner(Wrap)
                .component(Sonka)
                .quantityMultiplier(50)
                .build();
        WrapRecipe5 = Recipe.builder()
                .owner(Wrap)
                .component(Trappista)
                .quantityMultiplier(45)
                .build();
        WrapRecipe6 = Recipe.builder()
                .owner(Wrap)
                .component(Paradicsom)
                .quantityMultiplier(30)
                .build();
        WrapRecipe7 = Recipe.builder()
                .owner(Wrap)
                .component(Salata)
                .quantityMultiplier(15)
                .build();
        WrapRecipe8 = Recipe.builder()
                .owner(Wrap)
                .component(TortillaSos)
                .quantityMultiplier(35)
                .build();
    }

    private void buildSpecialisFeltetekPiritossalTonhalRecipe1() {
        SpecialisFeltetekPiritossalTonhalRecipe1 = Recipe.builder()
                .owner(SpecialisFeltetekPiritossalTonhal)
                .component(Bagett)
                .quantityMultiplier(80)
                .build();
        SpecialisFeltetekPiritossalTonhalRecipe2 = Recipe.builder()
                .owner(SpecialisFeltetekPiritossalTonhal)
                .component(TonhalKrem)
                .quantityMultiplier(100)
                .build();
        SpecialisFeltetekPiritossalTonhalRecipe3 = Recipe.builder()
                .owner(SpecialisFeltetekPiritossalTonhal)
                .component(Salata)
                .quantityMultiplier(3)
                .build();
        SpecialisFeltetekPiritossalTonhalRecipe4 = Recipe.builder()
                .owner(SpecialisFeltetekPiritossalTonhal)
                .component(Paradicsom)
                .quantityMultiplier(15)
                .build();
    }

    private void buildSpecialisFeltetekPiritossalPadlizsanRecipe1() {
        SpecialisFeltetekPiritossalPadlizsanRecipe1 = Recipe.builder()
                .owner(SpecialisFeltetekPiritossalPadlizsan)
                .component(Bagett)
                .quantityMultiplier(80)
                .build();
        SpecialisFeltetekPiritossalPadlizsanRecipe2 = Recipe.builder()
                .owner(SpecialisFeltetekPiritossalPadlizsan)
                .component(Salata)
                .quantityMultiplier(3)
                .build();
        SpecialisFeltetekPiritossalPadlizsanRecipe3 = Recipe.builder()
                .owner(SpecialisFeltetekPiritossalPadlizsan)
                .component(Paradicsom)
                .quantityMultiplier(15)
                .build();
    }


    private void buildSajttalRecipe1() {
        SajttalRecipe1 = Recipe.builder()
                .owner(Sajttal)
                .component(Trappista)
                .quantityMultiplier(60)
                .build();
        SajttalRecipe2 = Recipe.builder()
                .owner(Sajttal)
                .component(Cheddar)
                .quantityMultiplier(20)
                .build();
        SajttalRecipe3 = Recipe.builder()
                .owner(Sajttal)
                .component(Kecskesajt)
                .quantityMultiplier(35)
                .build();
        SajttalRecipe4 = Recipe.builder()
                .owner(Sajttal)
                .component(Camambert)
                .quantityMultiplier(35)
                .build();
    }

    private void buildGameUpTalRecipe1() {
        GameUpTalRecipe1 = Recipe.builder()
                .owner(GameUpTal)
                .component(MelegszendivcsSzalamis)
                .quantityMultiplier(1)
                .build();
        GameUpTalRecipe2 = Recipe.builder()
                .owner(GameUpTal)
                .component(SajtosCsikok)
                .quantityMultiplier(1)
                .build();
        GameUpTalRecipe3 = Recipe.builder()
                .owner(GameUpTal)
                .component(ZsirosDeszka)
                .quantityMultiplier(1)
                .build();
        GameUpTalRecipe4 = Recipe.builder()
                .owner(GameUpTal)
                .component(NachosSosChili)
                .quantityMultiplier(1)
                .build();
        GameUpTalRecipe5 = Recipe.builder()
                .owner(GameUpTal)
                .component(Paradicsom)
                .quantityMultiplier(50)
                .build();
        GameUpTalRecipe6 = Recipe.builder()
                .owner(GameUpTal)
                .component(Salata)
                .quantityMultiplier(10)
                .build();
    }

    private void buildGameUpFalankTalRecipe1() {
        GameUpFalankTalRecipe1 = Recipe.builder()
                .owner(GameUpFalankTal)
                .component(MelegszendivcsSonkas)
                .quantityMultiplier(1)
                .build();
        GameUpFalankTalRecipe2 = Recipe.builder()
                .owner(GameUpFalankTal)
                .component(MelegszendivcsSzalamis)
                .quantityMultiplier(1)
                .build();
        GameUpFalankTalRecipe3 = Recipe.builder()
                .owner(GameUpFalankTal)
                .component(SajtosCsikok)
                .quantityMultiplier(2)
                .build();
        GameUpFalankTalRecipe4 = Recipe.builder()
                .owner(GameUpFalankTal)
                .component(ZsirosDeszka)
                .quantityMultiplier(2)
                .build();
        GameUpFalankTalRecipe5 = Recipe.builder()
                .owner(GameUpFalankTal)
                .component(NachosSosChili)
                .quantityMultiplier(1)
                .build();
        GameUpFalankTalRecipe6 = Recipe.builder()
                .owner(GameUpFalankTal)
                .component(NachosBBQSajt)
                .quantityMultiplier(1)
                .build();
        GameUpFalankTalRecipe7 = Recipe.builder()
                .owner(GameUpFalankTal)
                .component(SpecialisFeltetekPiritossalTonhal)
                .quantityMultiplier(1)
                .build();
        GameUpFalankTalRecipe8 = Recipe.builder()
                .owner(GameUpFalankTal)
                .component(Paradicsom)
                .quantityMultiplier(80)
                .build();
        GameUpFalankTalRecipe9 = Recipe.builder()
                .owner(GameUpFalankTal)
                .component(Salata)
                .quantityMultiplier(15)
                .build();
    }
    
    private void buildAgentShotCoverRecipe1() {
        AgentShotCoverRecipe1 = Recipe.builder()
                .owner(AgentShotCover)
                .component(AgentShotCover)
                .quantityMultiplier(1)
                .build();
    }

    private void buildLimonCeptRecipe1() {
        LimonCeptRecipe1 = Recipe.builder()
                .owner(LimonCept)
                .component(MelegszendivcsSonkas)
                .quantityMultiplier(1)
                .build();
    }

    private void buildSplendBorRecipe1() {
        SplendBorRecipe1 = Recipe.builder()
                .owner(SplendBor)
                .component(MelegszendivcsSonkas)
                .quantityMultiplier(1)
                .build();
    }

    private void buildTatraTimeRecipe1() {
        TatraTimeRecipe1 = Recipe.builder()
                .owner(TatraTime)
                .component(MelegszendivcsSonkas)
                .quantityMultiplier(1)
                .build();
    }

    private void buildSorrelAzEmberisegEllenRecipe1() {
        SorrelAzEmberisegEllenRecipe1 = Recipe.builder()
                .owner(SorrelAzEmberisegEllen)
                .component(MelegszendivcsSonkas)
                .quantityMultiplier(1)
                .build();
    }

    
    private void buildSoproni03Recipe1() {
        Soproni03Recipe1 = Recipe.builder()
                .owner(Soproni03)
                .component(Soproni03)
                .quantityMultiplier(0.3)
                .build();
    }

    private void buildSoproni05Recipe1() {
        Soproni05Recipe1 = Recipe.builder()
                .owner(Soproni05)
                .component(Soproni05)
                .quantityMultiplier(0.5)
                .build();
    }

    private void buildEdelweiss03Recipe1() {
        Edelweiss03Recipe1 = Recipe.builder()
                .owner(Edelweiss03)
                .component(Edelweiss03)
                .quantityMultiplier(0.3)
                .build();
    }

    private void buildEdelweiss05Recipe1() {
        Edelweiss05Recipe1 = Recipe.builder()
                .owner(Edelweiss05)
                .component(Edelweiss05)
                .quantityMultiplier(0.5)
                .build();
    }

    private void buildKrusoviceSvetleRecipe1() {
        KrusoviceSvetleRecipe1 = Recipe.builder()
                .owner(KrusoviceSvetle)
                .component(KrusoviceSvetle)
                .quantityMultiplier(50)
                .build();
    }

    private void buildSoproniDemonRecipe1() {
        SoproniDemonRecipe1 = Recipe.builder()
                .owner(SoproniDemon)
                .component(SoproniDemon)
                .quantityMultiplier(50)
                .build();
    }

    private void buildSoproniMaxxRecipe1() {
        SoproniMaxxRecipe1 = Recipe.builder()
                .owner(SoproniMaxx)
                .component(SoproniMaxx)
                .quantityMultiplier(50)
                .build();
    }

    private void buildHeinekenRecipe1() {
        HeinekenRecipe1 = Recipe.builder()
                .owner(Heineken)
                .component(Heineken)
                .quantityMultiplier(33)
                .build();
    }

    private void buildGosserNaturRadlerRecipe1() {
        GosserNaturRadlerRecipe1 = Recipe.builder()
                .owner(GosserNaturRadler)
                .component(GosserNaturRadler)
                .quantityMultiplier(33)
                .build();
    }

    private void buildGosserNaturRadler00Recipe1() {
        GosserNaturRadler00Recipe1 = Recipe.builder()
                .owner(GosserNaturRadler00)
                .component(GosserNaturRadler00)
                .quantityMultiplier(33)
                .build();
    }

    private void buildBekesSzentandrasiMeggyesRecipe1() {
        BekesSzentandrasiMeggyesRecipe1 = Recipe.builder()
                .owner(BekesSzentandrasiMeggyes)
                .component(BekesSzentandrasiMeggyes)
                .quantityMultiplier(50)
                .build();
    }

    private void buildStrongbowDarkfruitRecipe1() {
        StrongbowDarkfruitRecipe1 = Recipe.builder()
                .owner(StrongbowDarkfruit)
                .component(StrongbowDarkfruit)
                .quantityMultiplier(33)
                .build();
    }

    private void buildStrongbowGoldAppleCiderRecipe1() {
        StrongbowGoldAppleCiderRecipe1 = Recipe.builder()
                .owner(StrongbowGoldAppleCider)
                .component(StrongbowGoldAppleCider)
                .quantityMultiplier(33)
                .build();
    }

    private void buildEdelweissRecipe1() {
        EdelweissRecipe1 = Recipe.builder()
                .owner(Edelweiss)
                .component(Edelweiss)
                .quantityMultiplier(50)
                .build();
    }

    private void buildHazBoraNagyEsNagyRecipe1() {
        HazBoraNagyEsNagyRecipe1 = Recipe.builder()
                .owner(HazBoraNagyEsNagy)
                .component(HazBoraNagyEsNagy)
                .quantityMultiplier(150)
                .build();
    }

    private void buildHilltopIrsaiOliverRecipe1() {
        HilltopIrsaiOliverRecipe1 = Recipe.builder()
                .owner(HilltopIrsaiOliver)
                .component(HilltopIrsaiOliver)
                .quantityMultiplier(75)
                .build();
    }

    private void buildHilltopIrsaiOliverDecireRecipe1() {
        HilltopIrsaiOliverDecireRecipe1 = Recipe.builder()
                .owner(HilltopIrsaiOliverDecire)
                .component(HilltopIrsaiOliverDecire)
                .quantityMultiplier(10)
                .build();
    }

    private void buildGereAttilaOlaszrizlingRecipe1() {
        GereAttilaOlaszrizlingRecipe1 = Recipe.builder()
                .owner(GereAttilaOlaszrizling)
                .component(GereAttilaOlaszrizling)
                .quantityMultiplier(75)
                .build();
    }

    private void buildHazBoraLisiczaRoseCuveeRecipe1() {
        HazBoraLisiczaRoseCuveeRecipe1 = Recipe.builder()
                .owner(HazBoraLisiczaRoseCuvee)
                .component(HazBoraLisiczaRoseCuvee)
                .quantityMultiplier(10)
                .build();
    }

    private void buildMeszarosPinotRecipe1() {
        MeszarosPinotNoirRoseRecipe1 = Recipe.builder()
                .owner(MeszarosPinotNoirRose)
                .component(MeszarosPinotNoirRose)
                .quantityMultiplier(75)
                .build();
    }

    private void buildMeszarosPinotDecireRecipe1() {
        MeszarosPinotNoirRoseDecireRecipe1 = Recipe.builder()
                .owner(MeszarosPinotNoirRoseDecire)
                .component(MeszarosPinotNoirRoseDecire)
                .quantityMultiplier(10)
                .build();
    }

    private void buildHazBoraPolgarSerumVeritasRecipe1() {
        HazBoraPolgarSerumVeritasRecipe1 = Recipe.builder()
                .owner(HazBoraPolgarSerumVeritas)
                .component(HazBoraPolgarSerumVeritas)
                .quantityMultiplier(75)
                .build();
    }

    private void buildVinczeMerlotRecipe1() {
        VinczeMerlotRecipe1 = Recipe.builder()
                .owner(VinczeMerlot)
                .component(VinczeMerlot)
                .quantityMultiplier(75)
                .build();
    }

    private void buildVylyanCabernetSauvignonRecipe1() {
        VylyanCabernetSauvignonRecipe1 = Recipe.builder()
                .owner(VylyanCabernetSauvignon)
                .component(VylyanCabernetSauvignon)
                .quantityMultiplier(75)
                .build();
    }

    private void buildMeszarosHidasptereCabernetFrancReserveRecipe1() {
        MeszarosHidasptereCabernetFrancReserveRecipe1 = Recipe.builder()
                .owner(MeszarosHidasptereCabernetFrancReserve)
                .component(MeszarosHidasptereCabernetFrancReserve)
                .quantityMultiplier(75)
                .build();
    }

    private void buildTorleyGalaSzarazRecipe1() {
        TorleyGalaSzarazRecipe1 = Recipe.builder()
                .owner(TorleyGalaSzaraz)
                .component(TorleyGalaSzaraz)
                .quantityMultiplier(75)
                .build();
    }

    private void buildTorleyCharmantEdesRecipe1() {
        TorleyCharmantEdesRecipe1 = Recipe.builder()
                .owner(TorleyCharmantEdes)
                .component(TorleyCharmantEdes)
                .quantityMultiplier(75)
                .build();
    }

    private void buildSzodaRecipe1() {
        SzodaRecipe1 = Recipe.builder()
                .owner(Szoda)
                .component(Szoda)
                .quantityMultiplier(10)
                .build();
    }

    private void buildHazBoraNagyEsNagyKisfroccsRecipe1() {
        HazBoraNagyEsNagyKisfroccsRecipe1 = Recipe.builder()
                .owner(HazBoraNagyEsNagyKisfroccs)
                .component(HazBoraNagyEsNagy)
                .quantityMultiplier(10)
                .build();
    }

    private void buildHazBoraNagyEsNagyNagyfroccsRecipe1() {
        HazBoraNagyEsNagyNagyfroccsRecipe1 = Recipe.builder()
                .owner(HazBoraNagyEsNagyNagyfroccs)
                .component(HazBoraNagyEsNagy)
                .quantityMultiplier(20)
                .build();
    }

    private void buildHazBoraNagyEsNagyVicehazmesterRecipe1() {
        HazBoraNagyEsNagyVicehazmesterRecipe1 = Recipe.builder()
                .owner(HazBoraNagyEsNagyVicehazmester)
                .component(HazBoraNagyEsNagy)
                .quantityMultiplier(20)
                .build();
    }

    private void buildHazBoraNagyEsNagyHazmesterRecipe1() {
        HazBoraNagyEsNagyHazmesterRecipe1 = Recipe.builder()
                .owner(HazBoraNagyEsNagyHazmester)
                .component(HazBoraNagyEsNagy)
                .quantityMultiplier(30)
                .build();
    }

    private void buildHazBoraNagyEsNagyHosszulepesRecipe1() {
        HazBoraNagyEsNagyHosszulepesRecipe1 = Recipe.builder()
                .owner(HazBoraNagyEsNagyHosszulepes)
                .component(HazBoraNagyEsNagy)
                .quantityMultiplier(10)
                .build();
    }

    private void buildHazBoraNagyEsNagySportfroccsRecipe1() {
        HazBoraNagyEsNagySportFroccsRecipe1 = Recipe.builder()
                .owner(HazBoraNagyEsNagySportFroccs)
                .component(HazBoraNagyEsNagy)
                .quantityMultiplier(10)
                .build();
    }

    private void buildHilltopIrsaiOliverKisfroccsRecipe1() {
        HilltopIrsaiOliverKisfroccsRecipe1 = Recipe.builder()
                .owner(HilltopIrsaiOliverKisfroccs)
                .component(HilltopIrsaiOliver)
                .quantityMultiplier(10)
                .build();
    }

    private void buildHilltopIrsaiOliverNagyfroccsRecipe1() {
        HilltopIrsaiOliverNagyfroccsRecipe1 = Recipe.builder()
                .owner(HilltopIrsaiOliverNagyfroccs)
                .component(HilltopIrsaiOliver)
                .quantityMultiplier(20)
                .build();
    }

    private void buildHilltopIrsaiOliverVicehazmesterRecipe1() {
        HilltopIrsaiOliverVicehazmesterRecipe1 = Recipe.builder()
                .owner(HilltopIrsaiOliverVicehazmester)
                .component(HilltopIrsaiOliver)
                .quantityMultiplier(20)
                .build();
    }

    private void buildHilltopIrsaiOliverHazmesterRecipe1() {
        HilltopIrsaiOliverHazmesterRecipe1 = Recipe.builder()
                .owner(HilltopIrsaiOliverHazmester)
                .component(HilltopIrsaiOliver)
                .quantityMultiplier(30)
                .build();
    }

    private void buildHilltopIrsaiOliverHosszulepesRecipe1() {
        HilltopIrsaiOliverHosszulepesRecipe1 = Recipe.builder()
                .owner(HilltopIrsaiOliverHosszulepes)
                .component(HilltopIrsaiOliver)
                .quantityMultiplier(10)
                .build();
    }

    private void buildHilltopIrsaiOliverSportfroccsRecipe1() {
        HilltopIrsaiOliverSportFroccsRecipe1 = Recipe.builder()
                .owner(HilltopIrsaiOliverSportFroccs)
                .component(HilltopIrsaiOliver)
                .quantityMultiplier(10)
                .build();
    }

    private void buildHazBoraLisiczaRoseCuveeKisfroccsRecipe1() {
        HazBoraLisiczaRoseCuveeKisfroccsRecipe1 = Recipe.builder()
                .owner(HazBoraLisiczaRoseCuveeKisfroccs)
                .component(HazBoraLisiczaRoseCuvee)
                .quantityMultiplier(10)
                .build();
    }

    private void buildHazBoraLisiczaRoseCuveeNagyfroccsRecipe1() {
        HazBoraLisiczaRoseCuveeNagyfroccsRecipe1 = Recipe.builder()
                .owner(HazBoraLisiczaRoseCuveeNagyfroccs)
                .component(HazBoraLisiczaRoseCuvee)
                .quantityMultiplier(20)
                .build();
    }

    private void buildHazBoraLisiczaRoseCuveeVicehazmesterRecipe1() {
        HazBoraLisiczaRoseCuveeVicehazmesterRecipe1 = Recipe.builder()
                .owner(HazBoraLisiczaRoseCuveeVicehazmester)
                .component(HazBoraLisiczaRoseCuvee)
                .quantityMultiplier(20)
                .build();
    }

    private void buildHazBoraLisiczaRoseCuveeHazmesterRecipe1() {
        HazBoraLisiczaRoseCuveeHazmesterRecipe1 = Recipe.builder()
                .owner(HazBoraLisiczaRoseCuveeHazmester)
                .component(HazBoraLisiczaRoseCuvee)
                .quantityMultiplier(30)
                .build();
    }

    private void buildHazBoraLisiczaRoseCuveeHosszulepesRecipe1() {
        HazBoraLisiczaRoseCuveeHosszulepesRecipe1 = Recipe.builder()
                .owner(HazBoraLisiczaRoseCuveeHosszulepes)
                .component(HazBoraLisiczaRoseCuvee)
                .quantityMultiplier(10)
                .build();
    }

    private void buildHazBoraLisiczaRoseCuveeSportfroccsRecipe1() {
        HazBoraLisiczaRoseCuveeSportFroccsRecipe1 = Recipe.builder()
                .owner(HazBoraLisiczaRoseCuveeSportFroccs)
                .component(HazBoraLisiczaRoseCuvee)
                .quantityMultiplier(10)
                .build();
    }

    private void buildMeszarosPinotKisfroccsRecipe1() {
        MeszarosPinotNoirRoseKisfroccsRecipe1 = Recipe.builder()
                .owner(MeszarosPinotNoirRoseKisfroccs)
                .component(MeszarosPinotNoirRose)
                .quantityMultiplier(10)
                .build();
    }

    private void buildMeszarosPinotNagyfroccsRecipe1() {
        MeszarosPinotNoirRoseNagyfroccsRecipe1 = Recipe.builder()
                .owner(MeszarosPinotNoirRoseNagyfroccs)
                .component(MeszarosPinotNoirRose)
                .quantityMultiplier(20)
                .build();
    }

    private void buildMeszarosPinotVicehazmesterRecipe1() {
        MeszarosPinotNoirRoseVicehazmesterRecipe1 = Recipe.builder()
                .owner(MeszarosPinotNoirRoseVicehazmester)
                .component(MeszarosPinotNoirRose)
                .quantityMultiplier(20)
                .build();
    }

    private void buildMeszarosPinotHazmesterRecipe1() {
        MeszarosPinotNoirRoseHazmesterRecipe1 = Recipe.builder()
                .owner(MeszarosPinotNoirRoseHazmester)
                .component(MeszarosPinotNoirRose)
                .quantityMultiplier(30)
                .build();
    }

    private void buildMeszarosPinotHosszulepesRecipe1() {
        MeszarosPinotNoirRoseHosszulepesRecipe1 = Recipe.builder()
                .owner(MeszarosPinotNoirRoseHosszulepes)
                .component(MeszarosPinotNoirRose)
                .quantityMultiplier(10)
                .build();
    }

    private void buildMeszarosPinotSportfroccsRecipe1() {
        MeszarosPinotNoirRoseSportFroccsRecipe1 = Recipe.builder()
                .owner(MeszarosPinotNoirRoseSportFroccs)
                .component(MeszarosPinotNoirRose)
                .quantityMultiplier(10)
                .build();
    }

    private void buildJimBeam04Recipe1() {
        JimBeam04Recipe1 = Recipe.builder()
                .owner(JimBeam04)
                .component(JimBeam04)
                .quantityMultiplier(4)
                .build();
    }

    private void buildJohnnieWalkerRedLabel04Recipe1() {
        JohnnieWalkerRedLabel04Recipe1 = Recipe.builder()
                .owner(JohnnieWalkerRedLabel04)
                .component(JohnnieWalkerRedLabel04)
                .quantityMultiplier(4)
                .build();
    }

    private void buildJackDaniels04Recipe1() {
        JackDaniels04Recipe1 = Recipe.builder()
                .owner(JackDaniels04)
                .component(JackDaniels04)
                .quantityMultiplier(4)
                .build();
    }

    private void buildTullamoreDew04Recipe1() {
        TullamoreDew04Recipe1 = Recipe.builder()
                .owner(TullamoreDew04)
                .component(TullamoreDew04)
                .quantityMultiplier(4)
                .build();
    }

    private void buildRoyal04Recipe1() {
        Royal04Recipe1 = Recipe.builder()
                .owner(Royal04)
                .component(Royal04)
                .quantityMultiplier(4)
                .build();
    }

    private void buildFinlandia04Recipe1() {
        Finlandia04Recipe1 = Recipe.builder()
                .owner(Finlandia04)
                .component(Finlandia04)
                .quantityMultiplier(4)
                .build();
    }

    private void buildBacardiSuperior04Recipe1() {
        BacardiSuperior04Recipe1 = Recipe.builder()
                .owner(BacardiSuperior04)
                .component(BacardiSuperior04)
                .quantityMultiplier(4)
                .build();
    }

    private void buildCaptainMorganSpicedRum04Recipe1() {
        CaptainMorganSpicedRum04Recipe1 = Recipe.builder()
                .owner(CaptainMorganSpicedRum04)
                .component(CaptainMorganSpicedRum04)
                .quantityMultiplier(4)
                .build();
    }

    private void buildBeefeater04Recipe1() {
        Beefeater04Recipe1 = Recipe.builder()
                .owner(Beefeater04)
                .component(Beefeater04)
                .quantityMultiplier(4)
                .build();
    }

    private void buildTequilaSierraReposadoGold04Recipe1() {
        TequilaSierraReposadoGold04Recipe1 = Recipe.builder()
                .owner(TequilaSierraReposadoGold04)
                .component(TequilaSierraReposadoGold04)
                .quantityMultiplier(4)
                .build();
    }

    private void buildTequilaSierraSilver04Recipe1() {
        TequilaSierraSilver04Recipe1 = Recipe.builder()
                .owner(TequilaSierraSilver04)
                .component(TequilaSierraSilver04)
                .quantityMultiplier(4)
                .build();
    }

    private void buildUnicum04Recipe1() {
        Unicum04Recipe1 = Recipe.builder()
                .owner(Unicum04)
                .component(Unicum04)
                .quantityMultiplier(4)
                .build();
    }

    private void buildJagermeister04Recipe1() {
        Jagermeister04Recipe1 = Recipe.builder()
                .owner(Jagermeister04)
                .component(Jagermeister04)
                .quantityMultiplier(4)
                .build();
    }

    private void buildBaileys08Recipe1() {
        Baileys08Recipe1 = Recipe.builder()
                .owner(Baileys08)
                .component(Baileys08)
                .quantityMultiplier(8)
                .build();
    }

    private void buildJimBeam02Recipe1() {
        JimBeam02Recipe1 = Recipe.builder()
                .owner(JimBeam02)
                .component(JimBeam04)
                .quantityMultiplier(2)
                .build();
    }

    private void buildJohnnieWalkerRedLabel02Recipe1() {
        JohnnieWalkerRedLabel02Recipe1 = Recipe.builder()
                .owner(JohnnieWalkerRedLabel02)
                .component(JohnnieWalkerRedLabel04)
                .quantityMultiplier(2)
                .build();
    }

    private void buildJackDaniels02Recipe1() {
        JackDaniels02Recipe1 = Recipe.builder()
                .owner(JackDaniels02)
                .component(JackDaniels04)
                .quantityMultiplier(2)
                .build();
    }

    private void buildTullamoreDew02Recipe1() {
        TullamoreDew02Recipe1 = Recipe.builder()
                .owner(TullamoreDew02)
                .component(TullamoreDew04)
                .quantityMultiplier(2)
                .build();
    }

    private void buildRoyal02Recipe1() {
        Royal02Recipe1 = Recipe.builder()
                .owner(Royal02)
                .component(Royal04)
                .quantityMultiplier(2)
                .build();
    }

    private void buildFinlandia02Recipe1() {
        Finlandia02Recipe1 = Recipe.builder()
                .owner(Finlandia02)
                .component(Finlandia04)
                .quantityMultiplier(2)
                .build();
    }

    private void buildBacardiSuperior02Recipe1() {
        BacardiSuperior02Recipe1 = Recipe.builder()
                .owner(BacardiSuperior02)
                .component(BacardiSuperior04)
                .quantityMultiplier(2)
                .build();
    }

    private void buildCaptainMorganSpicedRum02Recipe1() {
        CaptainMorganSpicedRum02Recipe1 = Recipe.builder()
                .owner(CaptainMorganSpicedRum02)
                .component(CaptainMorganSpicedRum04)
                .quantityMultiplier(2)
                .build();
    }

    private void buildBeefeater02Recipe1() {
        Beefeater02Recipe1 = Recipe.builder()
                .owner(Beefeater02)
                .component(Beefeater04)
                .quantityMultiplier(2)
                .build();
    }

    private void buildTequilaSierraReposadoGold02Recipe1() {
        TequilaSierraReposadoGold02Recipe1 = Recipe.builder()
                .owner(TequilaSierraReposadoGold02)
                .component(TequilaSierraReposadoGold04)
                .quantityMultiplier(2)
                .build();
    }

    private void buildTequilaSierraSilver02Recipe1() {
        TequilaSierraSilver02Recipe1 = Recipe.builder()
                .owner(TequilaSierraSilver02)
                .component(TequilaSierraSilver04)
                .quantityMultiplier(2)
                .build();
    }

    private void buildUnicum02Recipe1() {
        Unicum02Recipe1 = Recipe.builder()
                .owner(Unicum02)
                .component(Unicum04)
                .quantityMultiplier(2)
                .build();
    }

    private void buildJagermeister02Recipe1() {
        Jagermeister02Recipe1 = Recipe.builder()
                .owner(Jagermeister02)
                .component(Jagermeister04)
                .quantityMultiplier(2)
                .build();
    }

    private void buildBaileys04Recipe1() {
        Baileys04Recipe1 = Recipe.builder()
                .owner(Baileys04)
                .component(Baileys08)
                .quantityMultiplier(4)
                .build();
    }

    private void build22KokuszTatratea04Recipe1() {
        _22KokuszTatratea04Recipe1 = Recipe.builder()
                .owner(_22KokuszTatratea04)
                .component(_22KokuszTatratea04)
                .quantityMultiplier(4)
                .build();
    }

    private void build32CitrusTatratea04Recipe1() {
        _32CitrusTatratea04Recipe1 = Recipe.builder()
                .owner(_32CitrusTatratea04)
                .component(_32CitrusTatratea04)
                .quantityMultiplier(4)
                .build();
    }

    private void build42BarackTatratea04Recipe1() {
        _42BarackTatratea04Recipe1 = Recipe.builder()
                .owner(_42BarackTatratea04)
                .component(_42BarackTatratea04)
                .quantityMultiplier(4)
                .build();
    }

    private void build52EredetiTatratea04Recipe1() {
        _52EredetiTatratea04Recipe1 = Recipe.builder()
                .owner(_52EredetiTatratea04)
                .component(_52EredetiTatratea04)
                .quantityMultiplier(4)
                .build();
    }

    private void build62ErdeiGyumolcsTatratea04Recipe1() {
        _62ErdeiGyumolcsTatratea04Recipe1 = Recipe.builder()
                .owner(_62ErdeiGyumolcsTatratea04)
                .component(_62ErdeiGyumolcsTatratea04)
                .quantityMultiplier(4)
                .build();
    }

    private void build72OutlawBetyarTatratea04Recipe1() {
        _72OutlawBetyarTatratea04Recipe1 = Recipe.builder()
                .owner(_72OutlawBetyarTatratea04)
                .component(_72OutlawBetyarTatratea04)
                .quantityMultiplier(4)
                .build();
    }

    private void buildCseresznyePalinka04Recipe1() {
        CseresznyePalinka04Recipe1 = Recipe.builder()
                .owner(CseresznyePalinka04)
                .component(CseresznyePalinka04)
                .quantityMultiplier(4)
                .build();
    }

    private void buildKajszibarackPalinka04Recipe1() {
        KajszibarackPalinka04Recipe1 = Recipe.builder()
                .owner(KajszibarackPalinka04)
                .component(KajszibarackPalinka04)
                .quantityMultiplier(4)
                .build();
    }

    private void buildSzilvapalinka04Recipe1() {
        Szilvapalinka04Recipe1 = Recipe.builder()
                .owner(Szilvapalinka04)
                .component(Szilvapalinka04)
                .quantityMultiplier(4)
                .build();
    }

    private void build22KokuszTatratea02Recipe1() {
        _22KokuszTatratea02Recipe1 = Recipe.builder()
                .owner(_22KokuszTatratea02)
                .component(_22KokuszTatratea04)
                .quantityMultiplier(2)
                .build();
    }

    private void build32CitrusTatratea02Recipe1() {
        _32CitrusTatratea02Recipe1 = Recipe.builder()
                .owner(_32CitrusTatratea02)
                .component(_32CitrusTatratea04)
                .quantityMultiplier(2)
                .build();
    }

    private void build42BarackTatratea02Recipe1() {
        _42BarackTatratea02Recipe1 = Recipe.builder()
                .owner(_42BarackTatratea02)
                .component(_42BarackTatratea04)
                .quantityMultiplier(2)
                .build();
    }

    private void build52EredetiTatratea02Recipe1() {
        _52EredetiTatratea02Recipe1 = Recipe.builder()
                .owner(_52EredetiTatratea02)
                .component(_52EredetiTatratea04)
                .quantityMultiplier(2)
                .build();
    }

    private void build62ErdeiGyumolcsTatratea02Recipe1() {
        _62ErdeiGyumolcsTatratea02Recipe1 = Recipe.builder()
                .owner(_62ErdeiGyumolcsTatratea02)
                .component(_62ErdeiGyumolcsTatratea04)
                .quantityMultiplier(2)
                .build();
    }

    private void build72OutlawBetyarTatratea02Recipe1() {
        _72OutlawBetyarTatratea02Recipe1 = Recipe.builder()
                .owner(_72OutlawBetyarTatratea02)
                .component(_72OutlawBetyarTatratea04)
                .quantityMultiplier(2)
                .build();
    }

    private void buildCseresznyePalinka02Recipe1() {
        CseresznyePalinka02Recipe1 = Recipe.builder()
                .owner(CseresznyePalinka02)
                .component(CseresznyePalinka04)
                .quantityMultiplier(2)
                .build();
    }

    private void buildKajszibarackPalinka02Recipe1() {
        KajszibarackPalinka02Recipe1 = Recipe.builder()
                .owner(KajszibarackPalinka02)
                .component(KajszibarackPalinka04)
                .quantityMultiplier(2)
                .build();
    }

    private void buildSzilvapalinka02Recipe1() {
        Szilvapalinka02Recipe1 = Recipe.builder()
                .owner(Szilvapalinka02)
                .component(Szilvapalinka04)
                .quantityMultiplier(2)
                .build();
    }

    private void buildFinca1Recipe1() {
        Finca1Recipe1 = Recipe.builder()
                .owner(Finca1)
                .component(Royal04)
                .quantityMultiplier(2)
                .build();
        Finca1Recipe2 = Recipe.builder()
                .owner(Finca1)
                .component(CappyNarancs)
                .quantityMultiplier(1.5)
                .build();
        Finca1Recipe3 = Recipe.builder()
                .owner(Finca1)
                .component(MalnaSzirup)
                .quantityMultiplier(0.5)
                .build();
    }

    private void buildBang1Recipe1() {
        Bang1Recipe1 = Recipe.builder()
                .owner(Bang1)
                .component(Royal04)
                .quantityMultiplier(3.5)
                .build();
        Bang1Recipe2 = Recipe.builder()
                .owner(Bang1)
                .component(MalnaSzirup)
                .quantityMultiplier(0.5)
                .build();
    }

    private void buildImagine1Recipe1() {
        Imagine1Recipe1 = Recipe.builder()
                .owner(Imagine1)
                .component(Royal04)
                .quantityMultiplier(2)
                .build();
        Imagine1Recipe2 = Recipe.builder()
                .owner(Imagine1)
                .component(EperNarancsSzirup)
                .quantityMultiplier(0.5)
                .build();
    }

    private void buildFinca6Recipe1() {
        Finca6Recipe1 = Recipe.builder()
                .owner(Finca6)
                .component(Royal04)
                .quantityMultiplier(12)
                .build();
        Finca6Recipe2 = Recipe.builder()
                .owner(Finca6)
                .component(CappyNarancs)
                .quantityMultiplier(9)
                .build();
        Finca6Recipe3 = Recipe.builder()
                .owner(Finca6)
                .component(MalnaSzirup)
                .quantityMultiplier(3)
                .build();
    }

    private void buildBang6Recipe1() {
        Bang6Recipe1 = Recipe.builder()
                .owner(Bang6)
                .component(Royal04)
                .quantityMultiplier(21)
                .build();
        Bang6Recipe2 = Recipe.builder()
                .owner(Bang6)
                .component(MalnaSzirup)
                .quantityMultiplier(3)
                .build();
    }

    private void buildImagine6Recipe1() {
        Imagine6Recipe1 = Recipe.builder()
                .owner(Imagine1)
                .component(Royal04)
                .quantityMultiplier(12)
                .build();
        Imagine6Recipe2 = Recipe.builder()
                .owner(Imagine1)
                .component(EperNarancsSzirup)
                .quantityMultiplier(3)
                .build();
    }

    private void buildRiffRaff6Recipe1() {
        RiffRaff6Recipe1 = Recipe.builder()
                .owner(RiffRaff6)
                .component(Royal04)
                .quantityMultiplier(12)
                .build();
        RiffRaff6Recipe2 = Recipe.builder()
                .owner(RiffRaff6)
                .component(BurnOriginal)
                .quantityMultiplier(25)
                .build();
        RiffRaff6Recipe3 = Recipe.builder()
                .owner(RiffRaff6)
                .component(BlueCuracao)
                .quantityMultiplier(3)
                .build();
    }

    private void buildFinca12Recipe1() {
        Finca12Recipe1 = Recipe.builder()
                .owner(Finca1)
                .component(Royal04)
                .quantityMultiplier(24)
                .build();
        Finca12Recipe2 = Recipe.builder()
                .owner(Finca1)
                .component(CappyNarancs)
                .quantityMultiplier(18)
                .build();
        Finca12Recipe3 = Recipe.builder()
                .owner(Finca1)
                .component(MalnaSzirup)
                .quantityMultiplier(6)
                .build();
    }

    private void buildBang12Recipe1() {
        Bang12Recipe1 = Recipe.builder()
                .owner(Bang1)
                .component(Royal04)
                .quantityMultiplier(42)
                .build();
        Bang12Recipe2 = Recipe.builder()
                .owner(Bang1)
                .component(MalnaSzirup)
                .quantityMultiplier(6)
                .build();
    }

    private void buildImagine12Recipe1() {
        Imagine12Recipe1 = Recipe.builder()
                .owner(Imagine1)
                .component(Royal04)
                .quantityMultiplier(24)
                .build();
        Imagine12Recipe2 = Recipe.builder()
                .owner(Imagine1)
                .component(EperNarancsSzirup)
                .quantityMultiplier(6)
                .build();
    }

    private void buildRiffRaff12Recipe1() {
        RiffRaff12Recipe1 = Recipe.builder()
                .owner(RiffRaff6)
                .component(Royal04)
                .quantityMultiplier(24)
                .build();
        RiffRaff12Recipe2 = Recipe.builder()
                .owner(RiffRaff6)
                .component(BurnOriginal)
                .quantityMultiplier(25)
                .build();
        RiffRaff12Recipe3 = Recipe.builder()
                .owner(RiffRaff6)
                .component(BlueCuracao)
                .quantityMultiplier(6)
                .build();
    }

    private void buildCocaColaRecipe1() {
        CocaColaRecipe1 = Recipe.builder()
                .owner(CocaCola)
                .component(CocaCola)
                .quantityMultiplier(25)
                .build();
    }

    private void buildCocaColaZeroRecipe1() {
        CocaColaZeroRecipe1 = Recipe.builder()
                .owner(CocaColaZero)
                .component(CocaColaZero)
                .quantityMultiplier(25)
                .build();
    }

    private void buildFantaNarancsRecipe1() {
        FantaNarancsRecipe1 = Recipe.builder()
                .owner(FantaNarancs)
                .component(FantaNarancs)
                .quantityMultiplier(25)
                .build();
    }

    private void buildSpriteRecipe1() {
        SpriteRecipe1 = Recipe.builder()
                .owner(Sprite)
                .component(Sprite)
                .quantityMultiplier(25)
                .build();
    }

    private void buildKinleyGyomberRecipe1() {
        KinleyGyomberRecipe1 = Recipe.builder()
                .owner(KinleyGyomber)
                .component(KinleyGyomber)
                .quantityMultiplier(25)
                .build();
    }

    private void buildKinleyTonicRecipe1() {
        KinleyTonicRecipe1 = Recipe.builder()
                .owner(KinleyTonic)
                .component(KinleyTonic)
                .quantityMultiplier(25)
                .build();
    }

    private void buildNesteaCitromRecipe1() {
        NesteaCitromRecipe1 = Recipe.builder()
                .owner(NesteaCitrom)
                .component(NesteaCitrom)
                .quantityMultiplier(25)
                .build();
    }

    private void buildNesteaBarackRecipe1() {
        NesteaBarackRecipe1 = Recipe.builder()
                .owner(NesteaBarack)
                .component(NesteaBarack)
                .quantityMultiplier(25)
                .build();
    }

    private void buildCappyAlmaRecipe1() {
        CappyAlmaRecipe1 = Recipe.builder()
                .owner(CappyAlma)
                .component(CappyAlma)
                .quantityMultiplier(10)
                .build();
    }

    private void buildCappyNarancsRecipe1() {
        CappyNarancsRecipe1 = Recipe.builder()
                .owner(CappyNarancs)
                .component(CappyNarancs)
                .quantityMultiplier(10)
                .build();
    }

    private void buildCappyBarackRecipe1() {
        CappyBarackRecipe1 = Recipe.builder()
                .owner(CappyBarack)
                .component(CappyBarack)
                .quantityMultiplier(10)
                .build();
    }

    private void buildCappyAnanaszRecipe1() {
        CappyAnanaszRecipe1 = Recipe.builder()
                .owner(CappyAnanasz)
                .component(CappyAnanasz)
                .quantityMultiplier(10)
                .build();
    }

    private void buildLimonadeMalna05Recipe1() {
        LimonadeMalna05Recipe1 = Recipe.builder()
                .owner(LimonadeMalna05)
                .component(Citrom)
                .quantityMultiplier(30)
                .build();
        LimonadeMalna05Recipe2 = Recipe.builder()
                .owner(LimonadeMalna05)
                .component(Narancs)
                .quantityMultiplier(50)
                .build();
        LimonadeMalna05Recipe3 = Recipe.builder()
                .owner(LimonadeMalna05)
                .component(MalnaSzirup)
                .quantityMultiplier(4)
                .build();
        LimonadeMalna05Recipe4 = Recipe.builder()
                .owner(LimonadeMalna05)
                .component(FagyiGyumi)
                .quantityMultiplier(35)
                .build();
    }

    private void buildLimonadeMeggy05Recipe1() {
        LimonadeMeggy05Recipe1 = Recipe.builder()
                .owner(LimonadeMeggy05)
                .component(Citrom)
                .quantityMultiplier(30)
                .build();
        LimonadeMeggy05Recipe2 = Recipe.builder()
                .owner(LimonadeMeggy05)
                .component(Narancs)
                .quantityMultiplier(50)
                .build();
        LimonadeMeggy05Recipe3 = Recipe.builder()
                .owner(LimonadeMeggy05)
                .component(MeggySzirup)
                .quantityMultiplier(4)
                .build();
        LimonadeMeggy05Recipe4 = Recipe.builder()
                .owner(LimonadeMeggy05)
                .component(FagyiGyumi)
                .quantityMultiplier(35)
                .build();
    }

    private void buildLimonadeEperNarancs05Recipe1() {
        LimonadeEperNarancs05Recipe1 = Recipe.builder()
                .owner(LimonadeEperNarancs05)
                .component(Citrom)
                .quantityMultiplier(30)
                .build();
        LimonadeEperNarancs05Recipe2 = Recipe.builder()
                .owner(LimonadeEperNarancs05)
                .component(Narancs)
                .quantityMultiplier(50)
                .build();
        LimonadeEperNarancs05Recipe3 = Recipe.builder()
                .owner(LimonadeEperNarancs05)
                .component(EperNarancsSzirup)
                .quantityMultiplier(4)
                .build();
        LimonadeEperNarancs05Recipe4 = Recipe.builder()
                .owner(LimonadeEperNarancs05)
                .component(FagyiGyumi)
                .quantityMultiplier(35)
                .build();
    }

    private void buildLimonadeCitrus05Recipe1() {
        LimonadeCitrus05Recipe1 = Recipe.builder()
                .owner(LimonadeCitrus05)
                .component(Citrom)
                .quantityMultiplier(30)
                .build();
        LimonadeCitrus05Recipe2 = Recipe.builder()
                .owner(LimonadeCitrus05)
                .component(Narancs)
                .quantityMultiplier(50)
                .build();
        LimonadeCitrus05Recipe3 = Recipe.builder()
                .owner(LimonadeCitrus05)
                .component(CitrusSzirup)
                .quantityMultiplier(4)
                .build();
        LimonadeCitrus05Recipe4 = Recipe.builder()
                .owner(LimonadeCitrus05)
                .component(FagyiGyumi)
                .quantityMultiplier(35)
                .build();
    }

    private void buildLimonadeMalna10Recipe1() {
        LimonadeMalna10Recipe1 = Recipe.builder()
                .owner(LimonadeMalna05)
                .component(Citrom)
                .quantityMultiplier(60)
                .build();
        LimonadeMalna10Recipe2 = Recipe.builder()
                .owner(LimonadeMalna05)
                .component(Narancs)
                .quantityMultiplier(100)
                .build();
        LimonadeMalna10Recipe3 = Recipe.builder()
                .owner(LimonadeMalna05)
                .component(MalnaSzirup)
                .quantityMultiplier(10)
                .build();
        LimonadeMalna10Recipe4 = Recipe.builder()
                .owner(LimonadeMalna05)
                .component(FagyiGyumi)
                .quantityMultiplier(70)
                .build();
    }

    private void buildLimonadeMeggy10Recipe1() {
        LimonadeMeggy10Recipe1 = Recipe.builder()
                .owner(LimonadeMeggy10)
                .component(Citrom)
                .quantityMultiplier(60)
                .build();
        LimonadeMeggy10Recipe2 = Recipe.builder()
                .owner(LimonadeMeggy10)
                .component(Narancs)
                .quantityMultiplier(100)
                .build();
        LimonadeMeggy10Recipe3 = Recipe.builder()
                .owner(LimonadeMeggy10)
                .component(MeggySzirup)
                .quantityMultiplier(10)
                .build();
        LimonadeMeggy10Recipe4 = Recipe.builder()
                .owner(LimonadeMeggy10)
                .component(FagyiGyumi)
                .quantityMultiplier(70)
                .build();
    }

    private void buildLimonadeEperNarancs10Recipe1() {
        LimonadeEperNarancs10Recipe1 = Recipe.builder()
                .owner(LimonadeEperNarancs10)
                .component(Citrom)
                .quantityMultiplier(60)
                .build();
        LimonadeEperNarancs10Recipe2 = Recipe.builder()
                .owner(LimonadeEperNarancs10)
                .component(Narancs)
                .quantityMultiplier(100)
                .build();
        LimonadeEperNarancs10Recipe3 = Recipe.builder()
                .owner(LimonadeEperNarancs10)
                .component(EperNarancsSzirup)
                .quantityMultiplier(10)
                .build();
        LimonadeEperNarancs10Recipe4 = Recipe.builder()
                .owner(LimonadeEperNarancs10)
                .component(FagyiGyumi)
                .quantityMultiplier(70)
                .build();
    }

    private void buildLimonadeCitrus10Recipe1() {
        LimonadeCitrus10Recipe1 = Recipe.builder()
                .owner(LimonadeCitrus10)
                .component(Citrom)
                .quantityMultiplier(60)
                .build();
        LimonadeCitrus10Recipe2 = Recipe.builder()
                .owner(LimonadeCitrus10)
                .component(Narancs)
                .quantityMultiplier(100)
                .build();
        LimonadeCitrus10Recipe3 = Recipe.builder()
                .owner(LimonadeCitrus10)
                .component(CitrusSzirup)
                .quantityMultiplier(10)
                .build();
        LimonadeCitrus10Recipe4 = Recipe.builder()
                .owner(LimonadeCitrus10)
                .component(FagyiGyumi)
                .quantityMultiplier(70)
                .build();
    }

    private void buildNaturaquaSzensavasRecipe1() {
        NaturaquaSzensavasRecipe1 = Recipe.builder()
                .owner(NaturaquaSzensavas)
                .component(NaturaquaSzensavas)
                .quantityMultiplier(25)
                .build();
    }

    private void buildNaturaquaSzensavmentesRecipe1() {
        NaturaquaSzensavmentesRecipe1 = Recipe.builder()
                .owner(NaturaquaSzensavmentes)
                .component(NaturaquaSzensavmentes)
                .quantityMultiplier(25)
                .build();
    }

    private void buildBurnOriginalRecipe1() {
        BurnOriginalRecipe1 = Recipe.builder()
                .owner(BurnOriginal)
                .component(BurnOriginal)
                .quantityMultiplier(25)
                .build();
    }

    private void buildBurnZeroRecipe1() {
        BurnZeroRecipe1 = Recipe.builder()
                .owner(BurnZero)
                .component(BurnZero)
                .quantityMultiplier(25)
                .build();
    }

    private void buildMonsterEnergyRecipe1() {
        MonsterEnergyRecipe1 = Recipe.builder()
                .owner(MonsterEnergy)
                .component(MonsterEnergy)
                .quantityMultiplier(50)
                .build();
    }

    private void buildMonsterAssaultRecipe1() {
        MonsterAssaultRecipe1 = Recipe.builder()
                .owner(MonsterAssault)
                .component(MonsterAssault)
                .quantityMultiplier(50)
                .build();
    }

    private void buildMonsterRehabRecipe1() {
        MonsterRehabRecipe1 = Recipe.builder()
                .owner(MonsterRehab)
                .component(MonsterRehab)
                .quantityMultiplier(50)
                .build();
    }

    private void buildDallmyrFeketeRecipe1() {
        DallmayrFeketeRecipe1 = Recipe.builder()
                .owner(DallmayrFekete)
                .component(DallmayrFekete)
                .quantityMultiplier(9)
                .build();
    }

    private void buildDallmyrGyumolcsRecipe1() {
        DallmayrGyumolcsRecipe1 = Recipe.builder()
                .owner(DallmayrGyumolcs)
                .component(DallmayrGyumolcs)
                .quantityMultiplier(10)
                .build();
    }

    private void buildDallmyrZoldRecipe1() {
        DallmayrZoldRecipe1 = Recipe.builder()
                .owner(DallmayrZold)
                .component(DallmayrZold)
                .quantityMultiplier(11)
                .build();
    }

    private void buildPiramisDarjelingRecipe1() {
        PiramisDarjelingRecipe1 = Recipe.builder()
                .owner(PiramisDarjeling)
                .component(PiramisDarjeling)
                .quantityMultiplier(12)
                .build();
    }

    private void buildPiramisMangoMaracujaRecipe1() {
        PiramisMangoMaracujaRecipe1 = Recipe.builder()
                .owner(PiramisMangoMaracuja)
                .component(PiramisMangoMaracuja)
                .quantityMultiplier(13)
                .build();
    }

    private void buildPiramisAnanaszPapajaRecipe1() {
        PiramisAnanaszPapajaRecipe1 = Recipe.builder()
                .owner(PiramisAnanaszPapaja)
                .component(PiramisAnanaszPapaja)
                .quantityMultiplier(14)
                .build();
    }

    private void buildPiramisCitrusVerbenaRecipe1() {
        PiramisCitrusVerbenaRecipe1 = Recipe.builder()
                .owner(PiramisCitrusVerbena)
                .component(PiramisCitrusVerbena)
                .quantityMultiplier(15)
                .build();
    }

    private void buildEspressoRecipe1() {
        EspressoRecipe1 = Recipe.builder()
                .owner(Espresso)
                .component(Espresso)
                .quantityMultiplier(1)
                .build();
    }

    private void buildAmericanoRecipe1() {
        AmericanoRecipe1 = Recipe.builder()
                .owner(Americano)
                .component(Americano)
                .quantityMultiplier(5)
                .build();
    }

    private void buildCappuccinoRecipe1() {
        CappuccinoRecipe1 = Recipe.builder()
                .owner(Cappuccino)
                .component(Cappuccino)
                .quantityMultiplier(3)
                .build();
    }

    private void buildCaffeLatteRecipe1() {
        CaffeLatteRecipe1 = Recipe.builder()
                .owner(CaffeLatte)
                .component(CaffeLatte)
                .quantityMultiplier(2)
                .build();
    }

    private void buildLatteMacchiatoRecipe1() {
        LatteMacchiatoRecipe1 = Recipe.builder()
                .owner(LatteMacchiato)
                .component(LatteMacchiato)
                .quantityMultiplier(6)
                .build();
    }

    private void buildCaffeMelangeRecipe1() {
        CaffeMelangeRecipe1 = Recipe.builder()
                .owner(CaffeMelange)
                .component(CaffeMelange)
                .quantityMultiplier(4)
                .build();
    }

    private void buildForroCsokiBarnaRecipe1() {
        ForroCsokiBarnaRecipe1 = Recipe.builder()
                .owner(ForroCsokiBarna)
                .component(ForroCsokiBarna)
                .quantityMultiplier(7)
                .build();
    }

    private void buildForroCsokiFeherRecipe1() {
        ForroCsokiFeherRecipe1 = Recipe.builder()
                .owner(ForroCsokiFeher)
                .component(ForroCsokiFeher)
                .quantityMultiplier(8)
                .build();
    }

    private void buildCaptainAndGyomberRecipe1() {
        CaptainAndGyomberRecipe1 = Recipe.builder()
                .owner(CaptainAndGyomber)
                .component(CaptainAndGyomber)
                .quantityMultiplier(1)
                .build();
    }

    private void buildGinTonicRecipe1() {
        GinTonicRecipe1 = Recipe.builder()
                .owner(GinTonic)
                .component(GinTonic)
                .quantityMultiplier(2)
                .build();
    }

    private void buildJackAndCokeRecipe1() {
        JackAndCokeRecipe1 = Recipe.builder()
                .owner(JackAndCoke)
                .component(JackAndCoke)
                .quantityMultiplier(3)
                .build();
    }

    private void buildVodkaSzodaRecipe1() {
        VodkaSzodaRecipe1 = Recipe.builder()
                .owner(VodkaSzoda)
                .component(VodkaSzoda)
                .quantityMultiplier(4)
                .build();
    }

    private void buildPriceModifierEdelweiss() {
        priceModifierEdelweiss = PriceModifier.builder()
                .name("Edelweiss")
                .type(PriceModifierType.QUANTITY_DISCOUNT)
                .repeatPeriod(PriceModifierRepeatPeriod.WEEKLY)
                .startDate(LocalDateTime.of(2017, 1, 8, 16, 0))
                .endDate(LocalDateTime.of(2020, 1, 8, 20, 20))
                .discountPercent(33.333)
                .quantityLimit(3)
                .repeatPeriod(PriceModifierRepeatPeriod.WEEKLY)
                .dayOfWeek(DayOfWeek.TUESDAY)
                .build();
    }
    
    private void buildPriceModifierCaptainAndGyomber() {
        priceModifierCaptainAndGyomber = PriceModifier.builder()
                .name("Captain And Gyömbér")
                .type(PriceModifierType.QUANTITY_DISCOUNT)
                .repeatPeriod(PriceModifierRepeatPeriod.WEEKLY)
                .startDate(LocalDateTime.of(2017, 1, 8, 16, 0))
                .endDate(LocalDateTime.of(2020, 1, 8, 20, 20))
                .discountPercent(33.333)
                .quantityLimit(3)
                .repeatPeriod(PriceModifierRepeatPeriod.WEEKLY)
                .dayOfWeek(DayOfWeek.WEDNESDAY)
                .build();
    }

    private void buildPriceModifierGinTonic() {
        priceModifierGinTonic = PriceModifier.builder()
                .name("Gin Tonic")
                .type(PriceModifierType.QUANTITY_DISCOUNT)
                .repeatPeriod(PriceModifierRepeatPeriod.WEEKLY)
                .startDate(LocalDateTime.of(2017, 1, 8, 16, 0))
                .endDate(LocalDateTime.of(2020, 1, 8, 20, 20))
                .discountPercent(33.333)
                .quantityLimit(3)
                .repeatPeriod(PriceModifierRepeatPeriod.WEEKLY)
                .dayOfWeek(DayOfWeek.THURSDAY)
                .build();
    }

    private void buildPriceModifierJackAndCoke() {
        priceModifierJackAndCoke = PriceModifier.builder()
                .name("Jack And Coke")
                .type(PriceModifierType.QUANTITY_DISCOUNT)
                .repeatPeriod(PriceModifierRepeatPeriod.WEEKLY)
                .startDate(LocalDateTime.of(2017, 1, 8, 16, 0))
                .endDate(LocalDateTime.of(2020, 1, 8, 20, 20))
                .discountPercent(33.333)
                .quantityLimit(3)
                .repeatPeriod(PriceModifierRepeatPeriod.WEEKLY)
                .dayOfWeek(DayOfWeek.FRIDAY)
                .build();
    }

    private void buildPriceModifierVodkaSzoda() {
        priceModifierVodkaSzoda = PriceModifier.builder()
                .name("Vodka Szóda")
                .type(PriceModifierType.QUANTITY_DISCOUNT)
                .repeatPeriod(PriceModifierRepeatPeriod.WEEKLY)
                .startDate(LocalDateTime.of(2017, 1, 8, 16, 0))
                .endDate(LocalDateTime.of(2020, 1, 8, 20, 20))
                .discountPercent(33.333)
                .quantityLimit(3)
                .repeatPeriod(PriceModifierRepeatPeriod.WEEKLY)
                .dayOfWeek(DayOfWeek.SATURDAY)
                .build();
    }

    private void buildPriceModifierMeggyesSor() {
        priceModifierBekesSzentdrasiMeggyes = PriceModifier.builder()
                .name("Meggyes Sör")
                .type(PriceModifierType.QUANTITY_DISCOUNT)
                .repeatPeriod(PriceModifierRepeatPeriod.WEEKLY)
                .startDate(LocalDateTime.of(2017, 1, 8, 16, 0))
                .endDate(LocalDateTime.of(2020, 1, 8, 20, 20))
                .discountPercent(33.333)
                .quantityLimit(3)
                .repeatPeriod(PriceModifierRepeatPeriod.WEEKLY)
                .dayOfWeek(DayOfWeek.SUNDAY)
                .build();
    }
    
    private void buildPseudoSosPerec() {
        PseudoSosPerec = ProductCategory.builder()
                .name("SosPerec_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMogyoro() {
        PseudoMogyoro = ProductCategory.builder()
                .name("Mogyoro_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoRagcsaMix1Perec2Mogyi() {
        PseudoRagcsaMix1Perec2Mogyi = ProductCategory.builder()
                .name("RagcsaMix 1_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoRagcsaMix2Perec1Mogyi() {
        PseudoRagcsaMix2Perec1Mogyi = ProductCategory.builder()
                .name("RagcsaMix 2_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoRagcsaMix3Perec() {
        PseudoRagcsaMix3Perec = ProductCategory.builder()
                .name("RagcsaMix 3_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoNachosSosSajt() {
        PseudoNachosSosSajt = ProductCategory.builder()
                .name("NachosSosSajt_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoNachosSosChili() {
        PseudoNachosSosChili = ProductCategory.builder()
                .name("NachosSosChili_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoNachosBBQSajt() {
        PseudoNachosBBQSajt = ProductCategory.builder()
                .name("NachosBBQSajt_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoNachosBBQChili() {
        PseudoNachosBBQChili = ProductCategory.builder()
                .name("NachosBBQChili_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoChips() {
        PseudoChips = ProductCategory.builder()
                .name("Chips_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoPopcorn() {
        PseudoPopcorn = ProductCategory.builder()
                .name("Popcorn_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoGumicukor() {
        PseudoGumicukor = ProductCategory.builder()
                .name("Gumicukor_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoBalatonszelet() {
        PseudoBalatonszelet = ProductCategory.builder()
                .name("Balatonszelet_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCsoki() {
        PseudoCsoki = ProductCategory.builder()
                .name("Csoki_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMelegszendivcsSonkas() {
        PseudoMelegszendivcsSonkas = ProductCategory.builder()
                .name("MelegszendivcsSonkas_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMelegszendivcsSzalamis() {
        PseudoMelegszendivcsSzalamis = ProductCategory.builder()
                .name("MelegszendivcsSzalamis_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMelegszendivcsVega() {
        PseudoMelegszendivcsVega = ProductCategory.builder()
                .name("MelegszendivcsVega_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSajtosCsikok() {
        PseudoSajtosCsikok = ProductCategory.builder()
                .name("SajtosCsikok_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoZsirosDeszka() {
        PseudoZsirosDeszka = ProductCategory.builder()
                .name("ZsirosDeszka_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoWrap() {
        PseudoWrap = ProductCategory.builder()
                .name("Wrap_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSpecialisFeltetekPiritossalTonhal() {
        PseudoSpecialisFeltetekPiritossalTonhal = ProductCategory.builder()
                .name("SpecialisFeltetekPiritossalTonhal_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSpecialisFeltetekPiritossalPadlizsan() {
        PseudoSpecialisFeltetekPiritossalPadlizsan = ProductCategory.builder()
                .name("SpecialisFeltetekPiritossalPadlizsan_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSajttal() {
        PseudoSajttal = ProductCategory.builder()
                .name("Sajttal_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoGameUpTal() {
        PseudoGameUpTal = ProductCategory.builder()
                .name("GameUpTal_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoGameUpFalankTal() {
        PseudoGameUpFalankTal = ProductCategory.builder()
                .name("GameUpFalankTal_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoAgentShotCover() {
        PseudoAgentShotCover = ProductCategory.builder()
                .name("AgentShotCover_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoLimonCept() {
        PseudoLimonCept = ProductCategory.builder()
                .name("LimonCept_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSplendBor() {
        PseudoSplendBor = ProductCategory.builder()
                .name("SplendBor_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoTatraTime() {
        PseudoTatraTime = ProductCategory.builder()
                .name("TatraTime_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSorrelAzEmberisegEllen() {
        PseudoSorrelAzEmberisegEllen = ProductCategory.builder()
                .name("SorrelAzEmberisegEllen_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }


    private void buildPseudoCitrom() {
        PseudoCitrom = ProductCategory.builder()
                .name("Citrom_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoNarancs() {
        PseudoNarancs = ProductCategory.builder()
                .name("Narancs_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMalnaSzirup() {
        PseudoMalnaSzirup = ProductCategory.builder()
                .name("Málna Szirup_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoEperNarancsSzirup() {
        PseudoEperNarancsSzirup = ProductCategory.builder()
                .name("Eper-Narancs Szirup_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCitrusSzirup() {
        PseudoCitrusSzirup = ProductCategory.builder()
                .name("Citrus Szirup_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMeggySzirup() {
        PseudoMeggySzirup = ProductCategory.builder()
                .name("Meggy Szirup_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoTabasco() {
        PseudoTabasco = ProductCategory.builder()
                .name("Tabasco_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoBlueCuracao() {
        PseudoBlueCuracao = ProductCategory.builder()
                .name("Blue Curacao_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoFagyiGyumi() {
        PseudoFagyiGyumi = ProductCategory.builder()
                .name("Fagyi Gyumi_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoBarnaCukor() {
        PseudoBarnaCukor = ProductCategory.builder()
                .name("Barna Cukor_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoFeherCukor() {
        PseudoFeherCukor = ProductCategory.builder()
                .name("Feher Cukor_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMez() {
        PseudoMez = ProductCategory.builder()
                .name("Méz_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoTej() {
        PseudoTej = ProductCategory.builder()
                .name("Tej_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoTortillaSos() {
        PseudoTortillaSos = ProductCategory.builder()
                .name("Tortilla Sós_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoTortillaBBQ() {
        PseudoTortillaBBQ = ProductCategory.builder()
                .name("Tortilla BBQ_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoNachosSajtszosz() {
        PseudoNachosSajtszosz = ProductCategory.builder()
                .name("Nachos Sajtszósz_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoNachosChiliszosz() {
        PseudoNachosChiliszosz = ProductCategory.builder()
                .name("Nachos Chiliszósz_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoBagett() {
        PseudoBagett = ProductCategory.builder()
                .name("Bagett_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
               .build();
    }

    private void buildPseudoZsir() {
        PseudoZsir = ProductCategory.builder()
                .name("Zsir_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoLilahagyma() {
        PseudoLilahagyma = ProductCategory.builder()
                .name("Lilahagyma_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoPirospaprika() {
        PseudoPirospaprika = ProductCategory.builder()
                .name("Pirospaprika_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSalata() {
        PseudoSalata = ProductCategory.builder()
                .name("Saláta_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoParadicsom() {
        PseudoParadicsom = ProductCategory.builder()
                .name("Paradicsom_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoUborka() {
        PseudoUborka = ProductCategory.builder()
                .name("Uborka_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoPiritottHagyma() {
        PseudoPiritottHagyma = ProductCategory.builder()
                .name("Piritott Hagyma_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoTrappista() {
        PseudoTrappista = ProductCategory.builder()
                .name("Trappista_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCheddar() {
        PseudoCheddar = ProductCategory.builder()
                .name("Cheddar_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoKecskesajt() {
        PseudoKecskesajt = ProductCategory.builder()
                .name("Kecskesajt_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCamambert() {
        PseudoCamambert = ProductCategory.builder()
                .name("Camambert_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSonka() {
        PseudoSonka = ProductCategory.builder()
                .name("Sonka_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSzalami() {
        PseudoSzalami = ProductCategory.builder()
                .name("Szalámi_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoTonhalKrem() {
        PseudoTonhalKrem = ProductCategory.builder()
                .name("Tonhal Krém_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMargarin() {
        PseudoMargarin = ProductCategory.builder()
                .name("Margarin_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMustar() {
        PseudoMustar = ProductCategory.builder()
                .name("Mustár_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMajonez() {
        PseudoMajonez = ProductCategory.builder()
                .name("Majonéz_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoAjvar() {
        PseudoAjvar = ProductCategory.builder()
                .name("Ajvár_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoTortillaLap() {
        PseudoTortillaLap = ProductCategory.builder()
                .name("Tortilla Lap_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSoproni03() {
        PseudoSoproni03 = ProductCategory.builder()
                .name("Soproni 0,3L_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSoproni05() {
        PseudoSoproni05 = ProductCategory.builder()
                .name("Soproni 0,5L_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoEdelweiss03() {
        PseudoEdelweiss03 = ProductCategory.builder()
                .name("Edelweiss 0,3L_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoEdelweiss05() {
        PseudoEdelweiss05 = ProductCategory.builder()
                .name("Edelweiss 0,5L_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoKrusoviceSvetle() {
        PseudoKrusoviceSvetle = ProductCategory.builder()
                .name("Krusovice Svetlé_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSoproniDemon() {
        PseudoSoproniDemon = ProductCategory.builder()
                .name("Soproni Démon_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSoproniMaxx() {
        PseudoSoproniMaxx = ProductCategory.builder()
                .name("Soproni Maxx_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHeineken() {
        PseudoHeineken = ProductCategory.builder()
                .name("Heineken_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoGosserNaturRadler() {
        PseudoGosserNaturRadler = ProductCategory.builder()
                .name("Gosser Natur Radler_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoGosserNaturRadler00() {
        PseudoGosserNaturRadler00 = ProductCategory.builder()
                .name("Gosser Natur Radler 0,0%_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoBekesSzentadrasiMeggyes() {
        PseudoBekesSzentadrasiMeggyes = ProductCategory.builder()
                .name("Békésszentadrási Meggyes_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoStrongbowDarkfruit() {
        PseudoStrongbowDarkfruit = ProductCategory.builder()
                .name("Strongbow Darkfruit_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoStrongbowGoldAppleCider() {
        PseudoStrongbowGoldAppleCider = ProductCategory.builder()
                .name("Strongbow Gold Apple Cider_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoEdelweiss() {
        PseudoEdelweiss = ProductCategory.builder()
                .name("Edelweiss_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHazBoraNagyEsNagy() {
        PseudoHazBoraNagyEsNagy = ProductCategory.builder()
                .name("Ház Bora Nagy és Nagy_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHilltopIrsaiOliver() {
        PseudoHilltopIrsaiOliver = ProductCategory.builder()
                .name("Hilltop Irsai Olivér_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHilltopIrsaiOliverDecire() {
        PseudoHilltopIrsaiOliverDecire = ProductCategory.builder()
                .name("Hilltop Irsai Olivér Decire_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoGereAttilaOlaszrizling() {
        PseudoGereAttilaOlaszrizling = ProductCategory.builder()
                .name("Gere Attila Olaszrizling_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHazBoraLisiczaRoseCuvee() {
        PseudoHazBoraLisiczaRoseCuvee = ProductCategory.builder()
                .name("Ház Bora Lisicza Rosé Cuvée_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMeszarosPinot() {
        PseudoMeszarosPinotNoireRose = ProductCategory.builder()
                .name("Mészáros Pinot_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMeszarosPinotDecire() {
        PseudoMeszarosPinotNoireRoseDecire = ProductCategory.builder()
                .name("Mészáros Pinot 1dl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHazBoraPolgarSerumVeritas() {
        PseudoHazBoraPolgarSerumVeritas = ProductCategory.builder()
                .name("Ház Bora Polgár Serum Veritas_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoVinczeMerlot() {
        PseudoVinczeMerlot = ProductCategory.builder()
                .name("Vincze Merlot_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoVylyanCabernetSauvignon() {
        PseudoVylyanCabernetSauvignon = ProductCategory.builder()
                .name("Vylyan Cabernet Sauvignon_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMeszarosHidasptereCabernetFrancReserve() {
        PseudoMeszarosHidasptereCabernetFrancReserve = ProductCategory.builder()
                .name("Meszáros Hidasptere Cabernet Franc Reserve_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHazBoraNagyEsNagyKisfroccs() {
        PseudoHazBoraNagyEsNagyKisfroccs = ProductCategory.builder()
                .name("FHB Kisförccs_Pseudo_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHazBoraNagyEsNagyNagyfroccs() {
        PseudoHazBoraNagyEsNagyNagyfroccs = ProductCategory.builder()
                .name("FHB Nagyfröccs_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHazBoraNagyEsNagyVicehazmester() {
        PseudoHazBoraNagyEsNagyVicehazmester = ProductCategory.builder()
                .name("FHB Viceházmester_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHazBoraNagyEsNagyHazmester() {
        PseudoHazBoraNagyEsNagyHazmester = ProductCategory.builder()
                .name("FHB Házmester_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHazBoraNagyEsNagyHosszulepes() {
        PseudoHazBoraNagyEsNagyHosszulepes = ProductCategory.builder()
                .name("FHB Hosszúlépés_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHazBoraNagyEsNagySportfroccs() {
        PseudoHazBoraNagyEsNagySportFroccs = ProductCategory.builder()
                .name("FHB Sportfröccs_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHilltopIrsaiOliverKisfroccs() {
        PseudoHilltopIrsaiOliverKisfroccs = ProductCategory.builder()
                .name("Irsai Kisfröccs_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHilltopIrsaiOliverNagyfroccs() {
        PseudoHilltopIrsaiOliverNagyfroccs = ProductCategory.builder()
                .name("Irsai Nagyfröccs_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHilltopIrsaiOliverVicehazmester() {
        PseudoHilltopIrsaiOliverVicehazmester = ProductCategory.builder()
                .name("Irsai Viceházmester_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHilltopIrsaiOliverHazmester() {
        PseudoHilltopIrsaiOliverHazmester = ProductCategory.builder()
                .name("Irsai Házmester_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHilltopIrsaiOliverHosszulepes() {
        PseudoHilltopIrsaiOliverHosszulepes = ProductCategory.builder()
                .name("Irsai Hosszúlépés_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHilltopIrsaiOliverSportfroccs() {
        PseudoHilltopIrsaiOliverSportFroccs = ProductCategory.builder()
                .name("Irsai Sportföccs_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHazBoraLisiczaRoseCuveeKisfroccs() {
        PseudoHazBoraLisiczaRoseCuveeKisfroccs = ProductCategory.builder()
                .name("RHB Kisfröccs_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHazBoraLisiczaRoseCuveeNagyfroccs() {
        PseudoHazBoraLisiczaRoseCuveeNagyfroccs = ProductCategory.builder()
                .name("RHB Nagyfröccs_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHazBoraLisiczaRoseCuveeVicehazmester() {
        PseudoHazBoraLisiczaRoseCuveeVicehazmester = ProductCategory.builder()
                .name("RHB Viceházmester_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHazBoraLisiczaRoseCuveeHazmester() {
        PseudoHazBoraLisiczaRoseCuveeHazmester = ProductCategory.builder()
                .name("RHB Házmester_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHazBoraLisiczaRoseCuveeHosszulepes() {
        PseudoHazBoraLisiczaRoseCuveeHosszulepes = ProductCategory.builder()
                .name("RHB Hosszúlépés_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoHazBoraLisiczaRoseCuveeSportfroccs() {
        PseudoHazBoraLisiczaRoseCuveeSportFroccs = ProductCategory.builder()
                .name("RHB Sportföccs_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMeszarosPinotKisfroccs() {
        PseudoMeszarosPinotNoirRoseKisfroccs = ProductCategory.builder()
                .name("Mészáros Kisfröccs_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMeszarosPinotNagyfroccs() {
        PseudoMeszarosPinotNoirRoseNagyfroccs = ProductCategory.builder()
                .name("Mészáros Nagyfröccs_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMeszarosPinotVicehazmester() {
        PseudoMeszarosPinotNoirRoseVicehazmester = ProductCategory.builder()
                .name("Mészáros Viceházmester_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMeszarosPinotHazmester() {
        PseudoMeszarosPinotNoirRoseHazmester = ProductCategory.builder()
                .name("Mészáros Házmester_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMeszarosPinotHosszulepes() {
        PseudoMeszarosPinotNoirRoseHosszulepes = ProductCategory.builder()
                .name("Mészáros Hosszúlépés_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMeszarosPinotSportfroccs() {
        PseudoMeszarosPinotNoirRoseSportFroccs = ProductCategory.builder()
                .name("Mészáros Sprotfröccs_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoTorleyGalaSzaraz() {
        PseudoTorleyGalaSzaraz = ProductCategory.builder()
                .name("Torley Gála Száraz_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoTorleyCharmantEdes() {
        PseudoTorleyCharmantEdes = ProductCategory.builder()
                .name("Torley Charmant Édes_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoJimBeam04() {
        PseudoJimBeam04 = ProductCategory.builder()
                .name("Jim Beam 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoJohnnieWalkerRedLabel04() {
        PseudoJohnnieWalkerRedLabel04 = ProductCategory.builder()
                .name("Johnnie Walker Red Label 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoJackDaniels04() {
        PseudoJackDaniels04 = ProductCategory.builder()
                .name("Jack Daniels 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoTullamoreDew04() {
        PseudoTullamoreDew04 = ProductCategory.builder()
                .name("Tullamore Dew 4cl_Pseudo")
                   .status(ProductStatus.ACTIVE)
                    .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoRoyal04() {
        PseudoRoyal04 = ProductCategory.builder()
                .name("Royal 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                 .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoFinlandia04() {
        PseudoFinlandia04 = ProductCategory.builder()
                .name("Finlandia 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoBacardiSuperior04() {
        PseudoBacardiSuperior04 = ProductCategory.builder()
                .name("Bacardi Superior 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCaptainMorganSpicedRum04() {
        PseudoCaptainMorganSpicedRum04 = ProductCategory.builder()
                .name("Captain Morgan Spiced Rum 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoBeefeater04() {
        PseudoBeefeater04 = ProductCategory.builder()
                .name("Beefeater 4cl_Pseudo")
                 .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoTequilaSierraReposadoGold04() {
        PseudoTequilaSierraReposadoGold04 = ProductCategory.builder()
                .name("Tequila Sierra Reposado Gold 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoTequilaSierraSilver04() {
        PseudoTequilaSierraSilver04 = ProductCategory.builder()
                .name("Tequila Sierra Silver 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoUnicum04() {
        PseudoUnicum04 = ProductCategory.builder()
                .name("Unicum 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoJagermeister04() {
        PseudoJagermeister04 = ProductCategory.builder()
                .name("Jagermeister 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoBaileys08() {
        PseudoBaileys08 = ProductCategory.builder()
                .name("Baileys 8cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoJimBeam02() {
        PseudoJimBeam02 = ProductCategory.builder()
                .name("Jim Beam 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoJohnnieWalkerRedLabel02() {
        PseudoJohnnieWalkerRedLabel02 = ProductCategory.builder()
                .name("Johnnie Walker Red Label 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoJackDaniels02() {
        PseudoJackDaniels02 = ProductCategory.builder()
                .name("Jack Daniels 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoTullamoreDew02() {
        PseudoTullamoreDew02 = ProductCategory.builder()
                .name("Tullamore Dew 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoRoyal02() {
        PseudoRoyal02 = ProductCategory.builder()
                .name("Royal 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoFinlandia02() {
        PseudoFinlandia02 = ProductCategory.builder()
                .name("Finlandia 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoBacardiSuperior02() {
        PseudoBacardiSuperior02 = ProductCategory.builder()
                .name("Bacardi Superior 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCaptainMorganSpicedRum02() {
        PseudoCaptainMorganSpicedRum02 = ProductCategory.builder()
                .name("Captain Morgan SpicedRum 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoBeefeater02() {
        PseudoBeefeater02 = ProductCategory.builder()
                .name("Beefeater 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoTequilaSierraReposadoGold02() {
        PseudoTequilaSierraReposadoGold02 = ProductCategory.builder()
                .name("Tequila Sierra Reposado Gold 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoTequilaSierraSilver02() {
        PseudoTequilaSierraSilver02 = ProductCategory.builder()
                .name("TequilaSierraSilver 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoUnicum02() {
        PseudoUnicum02 = ProductCategory.builder()
                .name("Unicum 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoJagermeister02() {
        PseudoJagermeister02 = ProductCategory.builder()
                .name("Jagermeister 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoBaileys04() {
        PseudoBaileys04 = ProductCategory.builder()
                .name("Baileys 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudo22KokuszTatratea04() {
        Pseudo_22KokuszTatratea04 = ProductCategory.builder()
                .name("22% Kókusz Tátratea 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudo32CitrusTatratea04() {
        Pseudo_32CitrusTatratea04 = ProductCategory.builder()
                .name("32% Citrus Tátratea 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudo42BarackTatratea04() {
        Pseudo_42BarackTatratea04 = ProductCategory.builder()
                .name("42% Barack Tátratea 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudo52EredetiTatratea04() {
        Pseudo_52EredetiTatratea04 = ProductCategory.builder()
                .name("52% Eredeti Tátratea 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudo62ErdeiGyumolcsTatratea04() {
        Pseudo_62ErdeiGyumolcsTatratea04 = ProductCategory.builder()
                .name("62% Erdei Gyümölcs Tátratea 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudo72OutlawBetyarTatratea04() {
        Pseudo_72OutlawBetyarTatratea04 = ProductCategory.builder()
                .name("72% Outlaw Betyár Tátratea 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCseresznyePalinka04() {
        PseudoCseresznyePalinka04 = ProductCategory.builder()
                .name("Cseresznye Pálinka 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoKajszibarackPalinka04() {
        PseudoKajszibarackPalinka04 = ProductCategory.builder()
                .name("Kajszibarack Pálinka 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSzilvapalinka04() {
        PseudoSzilvapalinka04 = ProductCategory.builder()
                .name("Szilva pálinka 4cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudo22KokuszTatratea02() {
        Pseudo_22KokuszTatratea02 = ProductCategory.builder()
                .name("22% Kókusz Tátratea 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudo32CitrusTatratea02() {
        Pseudo_32CitrusTatratea02 = ProductCategory.builder()
                .name("32% Citrus Tátratea 2cl_Pseudo")
                 .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudo42BarackTatratea02() {
        Pseudo_42BarackTatratea02 = ProductCategory.builder()
                .name("42% Barack Tátratea 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudo52EredetiTatratea02() {
        Pseudo_52EredetiTatratea02 = ProductCategory.builder()
                .name("52% Eredeti Tátratea 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudo62ErdeiGyumolcsTatratea02() {
        Pseudo_62ErdeiGyumolcsTatratea02 = ProductCategory.builder()
                .name("62% Erdei Gyümölcs Tátratea 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudo72OutlawBetyarTatratea02() {
        Pseudo_72OutlawBetyarTatratea02 = ProductCategory.builder()
                .name("72% Outlaw Betyár Tátratea 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCseresznyePalinka02() {
        PseudoCseresznyePalinka02 = ProductCategory.builder()
                .name("Cseresznye Pálinka 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoKajszibarackPalinka02() {
        PseudoKajszibarackPalinka02 = ProductCategory.builder()
                .name("Kajszibarack Pálinka 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSzilvapalinka02() {
        PseudoSzilvapalinka02 = ProductCategory.builder()
                .name("Szilva pálinka 2cl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoFinca1() {
        PseudoFinca1 = ProductCategory.builder()
                .name("Finca 1db_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoBang1() {
        PseudoBang1 = ProductCategory.builder()
                .name("Bang 1db_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoImagine1() {
        PseudoImagine1 = ProductCategory.builder()
                .name("Imagine 1db_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoFinca6() {
        PseudoFinca6 = ProductCategory.builder()
                .name("Finca 6db_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoBang6() {
        PseudoBang6 = ProductCategory.builder()
                .name("Bang 6db_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoImagine6() {
        PseudoImagine6 = ProductCategory.builder()
                .name("Imagine 6db_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoRiffRaff6() {
        PseudoRiffRaff6 = ProductCategory.builder()
                .name("Riff Raff 6db_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoFinca12() {
        PseudoFinca12 = ProductCategory.builder()
                .name("Finca 12db_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoBang12() {
        PseudoBang12 = ProductCategory.builder()
                .name("Bang 12db_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoImagine12() {
        PseudoImagine12 = ProductCategory.builder()
                .name("Imagine 12db_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoRiffRaff12() {
        PseudoRiffRaff12 = ProductCategory.builder()
                .name("Riff Raff 12db_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCocaCola() {
        PseudoCocaCola = ProductCategory.builder()
                .name("Coca Cola_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCocaColaZero() {
        PseudoCocaColaZero = ProductCategory.builder()
                .name("Coca Cola Zero_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoFantaNarancs() {
        PseudoFantaNarancs = ProductCategory.builder()
                .name("Fanta Narancs_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSprite() {
        PseudoSprite = ProductCategory.builder()
                .name("Sprite_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoKinleyGyomber() {
        PseudoKinleyGyomber = ProductCategory.builder()
                .name("Kinley Gyömbér_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoKinleyTonic() {
        PseudoKinleyTonic = ProductCategory.builder()
                .name("Kinley Tonic_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoNesteaCitrom() {
        PseudoNesteaCitrom = ProductCategory.builder()
                .name("Nestea Citrom_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoNesteaBarack() {
        PseudoNesteaBarack = ProductCategory.builder()
                .name("Nestea Barack_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCappyAlma() {
        PseudoCappyAlma = ProductCategory.builder()
                .name("Cappy Alma_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCappyNarancs() {
        PseudoCappyNarancs = ProductCategory.builder()
                .name("Cappy Narancs_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCappyBarack() {
        PseudoCappyBarack = ProductCategory.builder()
                .name("Cappy Barack_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCappyAnanasz() {
        PseudoCappyAnanasz = ProductCategory.builder()
                .name("Cappy Ananász_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoLimonadeMalna05() {
        PseudoLimonadeMalna05 = ProductCategory.builder()
                .name("Limonádé Málna 5dl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoLimonadeMeggy05() {
        PseudoLimonadeMeggy05 = ProductCategory.builder()
                .name("Limonádé Meggy 5dl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoLimonadeEperNarancs05() {
        PseudoLimonadeEperNarancs05 = ProductCategory.builder()
                .name("Limonádé Eper Narancs 5dl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoLimonadeCitrus05() {
        PseudoLimonadeCitrus05 = ProductCategory.builder()
                .name("Limonádé Citrus 5dl_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoLimonadeMalna10() {
        PseudoLimonadeMalna10 = ProductCategory.builder()
                .name("Limonádé Málna 1L_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoLimonadeMeggy10() {
        PseudoLimonadeMeggy10 = ProductCategory.builder()
                .name("Limonádé Meggy 1L_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoLimonadeEperNarancs10() {
        PseudoLimonadeEperNarancs10 = ProductCategory.builder()
                .name("Limonádé Eper Narancs  1L_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoLimonadeCitrus10() {
        PseudoLimonadeCitrus10 = ProductCategory.builder()
                .name("Limonádé Citrus  1L_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoNaturaquaSzensavas() {
        PseudoNaturaquaSzensavas = ProductCategory.builder()
                .name("Naturaqua Szénsavas_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoNaturaquaSzensavmentes() {
        PseudoNaturaquaSzensavmentes = ProductCategory.builder()
                .name("Naturaqua Szénsavmentes_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoSzoda() {
        PseudoSzoda = ProductCategory.builder()
                .name("Szóda_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoBurnOriginal() {
        PseudoBurnOriginal = ProductCategory.builder()
                .name("Burn Original_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoBurnZero() {
        PseudoBurnZero = ProductCategory.builder()
                .name("BurnZero_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMonsterEnergy() {
        PseudoMonsterEnergy = ProductCategory.builder()
                .name("Monster Energy_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMonsterAssault() {
        PseudoMonsterAssault = ProductCategory.builder()
                .name("Monster Assault_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoMonsterRehab() {
        PseudoMonsterRehab = ProductCategory.builder()
                .name("Monster Rehab_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoDallmyrFekete() {
        PseudoDallmyrFekete = ProductCategory.builder()
                .name("Dallmyr Fekete_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoDallmyrGyumolcs() {
        PseudoDallmyrGyumolcs = ProductCategory.builder()
                .name("Dallmyr Gyümölcs_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoDallmyrZold() {
        PseudoDallmyrZold = ProductCategory.builder()
                .name("Dallmyr Zöld_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoPiramis1() {
        PseudoPiramis1 = ProductCategory.builder()
                .name("Piramis1_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoPiramis2() {
        PseudoPiramis2 = ProductCategory.builder()
                .name("Piramis2_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoPiramis3() {
        PseudoPiramis3 = ProductCategory.builder()
                .name("Piramis3_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoPiramis4() {
        PseudoPiramis4 = ProductCategory.builder()
                .name("Piramis4_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoEspresso() {
        PseudoEspresso = ProductCategory.builder()
                .name("Espresso_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoAmericano() {
        PseudoAmericano = ProductCategory.builder()
                .name("Americano_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCappuccino() {
        PseudoCappuccino = ProductCategory.builder()
                .name("Cappuccino_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCaffeLatte() {
        PseudoCaffeLatte = ProductCategory.builder()
                .name("Caffe Latte_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoLatteMacchiato() {
        PseudoLatteMacchiato = ProductCategory.builder()
                .name("Latte Macchiato_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCaffeMelange() {
        PseudoCaffeMelange = ProductCategory.builder()
                .name("Caffe Melange_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoForroCsokiBarna() {
        PseudoForroCsokiBarna = ProductCategory.builder()
                .name("Forró Csoki Barna_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoForroCsokiFeher() {
        PseudoForroCsokiFeher = ProductCategory.builder()
                .name("Forró Csoki Fehér_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoCaptainAndGyomber() {
        PseudoCaptainAndGyomber = ProductCategory.builder()
                .name("CaptainAndGyomber_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoGinTonic() {
        PseudoGinTonic = ProductCategory.builder()
                .name("GinTonic_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoJackAndCoke() {
        PseudoJackAndCoke = ProductCategory.builder()
                .name("JackAndCoke_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }

    private void buildPseudoVodkaSzoda() {
        PseudoVodkaSzoda = ProductCategory.builder()
                .name("VodkaSzoda_Pseudo")
                .status(ProductStatus.ACTIVE)
                .type(ProductCategoryType.PSEUDO)
                .build();
    }
    
    private void buildReceipts() {
        buildReceiptPurchase();
        buildReceiptInventory();
        buildReceiptDisposal();
        buildReceiptOther();
    }

    private void buildVatSeries() {
        buildVatSerieOne();
    }

    private void BuildVATs() {
        buildVatOne();
        buildVatTwo();
        buildVatThree();
        buildVatFour();
        buildVatFive();
    }

    private void buildTables() {
        buildTable1();
        buildTable2();
        buildTable3();
        buildTable4();
        buildTable5();
        buildTable6();
        buildTable7();
        buildTable8();
        buildTable9();
        buildTable10();
        buildTable11();
        buildTable12();
        buildTable13();

        buildTablePurchase();
        buildTableInventory();
        buildTableDisposal();
        buildTableOrphanage();
        buildTableOther();
    }


    private void buildReservations() {
        buildReservationOne();
        buildReservationTwo();
    }

    private void buildReservationOne() {
        reservationOne = Reservation.builder()
                .tableNumber(1)
                .date(LocalDate.now())
                .startTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0)))
                .endTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(20, 0)))
                .name("TestName1")
                .note("TestNote1")
                .build();
    }

    private void buildReservationTwo() {
        reservationTwo = Reservation.builder()
                .tableNumber(2)
                .date(LocalDate.now())
                .startTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0)))
                .endTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(20, 0)))
                .name("TestName2")
                .note("TestNote2")
                .build();
    }



    private void buildRestaurant() {
        restaurant = Restaurant.builder()
                .restaurantName("GameUp Pub")
                .companyName("Arcopen Kft.")
                .companyTaxPayerId("1-42-6518879")
                .companyAddress(buildDefaultAddress())
                .restaurantAddress(buildDefaultAddress())
                .receiptNote("")
                .socialMediaInfo("facebook.com/gameuppub")
                .webSite("http://www.gameup.hu")
                .phoneNumber("+36 30/287-87-66")
                .build();
     }

    private Address buildDefaultAddress() {
        return Address.builder()
                .ZIPCode("1066")
                .city("Budapest")
                .street("Zichy Jenő utca 4.")
                .build();
    }

    private void buildDailyClosureOne() {
        dailyClosureOne = DailyClosure.builder()
                .closureTime(now().minusDays(1))
                .sumPurchaseNetPriceCash(10000)
                .sumPurchaseNetPriceCreditCard(10000)
                .sumPurchaseNetPriceCoupon(10000)
                .sumPurchaseNetPriceTotal(30000)

                .sumPurchaseGrossPriceCash(12000)
                .sumPurchaseGrossPriceCreditCard(12000)
                .sumPurchaseGrossPriceCoupon(12000)
                .sumPurchaseGrossPriceTotal(36000)

                .sumSaleNetPriceCash(20000)
                .sumSaleNetPriceCreditCard(20000)
                .sumSaleNetPriceCoupon(20000)
                .sumSaleNetPriceTotal(60000)

                .sumSaleGrossPriceCash(25000)
                .sumSaleGrossPriceCreditCard(25000)
                .sumSaleGrossPriceCoupon(25000)
                .sumSaleGrossPriceTotal(75000)

                .profit(30000)
                .markup(50)
                .receiptAverage(3000)
                .numberOfReceipts(25)
                .discount(5)
                .build();
    }

    private void buildDailyClosureTwo() {
        dailyClosureTwo = DailyClosure.builder()
                .closureTime(null)
                .sumPurchaseNetPriceCash(0)
                .sumPurchaseNetPriceCreditCard(0)
                .sumPurchaseNetPriceCoupon(0)
                .sumPurchaseNetPriceTotal(0)

                .sumPurchaseGrossPriceCash(0)
                .sumPurchaseGrossPriceCreditCard(0)
                .sumPurchaseGrossPriceCoupon(0)
                .sumPurchaseGrossPriceTotal(0)

                .sumSaleNetPriceCash(0)
                .sumSaleNetPriceCreditCard(0)
                .sumSaleNetPriceCoupon(0)
                .sumSaleNetPriceTotal(0)

                .sumSaleGrossPriceCash(0)
                .sumSaleGrossPriceCreditCard(0)
                .sumSaleGrossPriceCoupon(0)
                .sumSaleGrossPriceTotal(0)

                .profit(0)
                .markup(0)
                .receiptAverage(0)
                .numberOfReceipts(0)
                .discount(0)
                .build();
    }

    private void buildAdmin() {
        admin = AuthUser.builder()
                .username("admin")
                .password("admin")
                .role(Role.ADMIN)
                .build();
    }

    private void buildUser() {
        user = AuthUser.builder()
                .username("user")
                .password("user")
                .role(Role.USER)
                .build();
    }

    private void buildRoot() {
        root = ProductCategory.builder()
                .name("root")
                .type(ProductCategoryType.ROOT)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildEtlap() {
        etlap = ProductCategory.builder()
                .name("Étlap")
                .orderNumber(2)
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildMenukAggregate() {
        menukAggregate = ProductCategory.builder()
                .name("Menü")
                .orderNumber(3)
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildRagcsak() {
        ragcsak = ProductCategory.builder()
                .name("Rágcsák")
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildEtelek() {
        etelek = ProductCategory.builder()
                .name("Ételek")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();

    }

    private void buildMenuk() {
        menuk = ProductCategory.builder()
                .name("Menük")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildAlapanyagokAggregate() {
        alapanyagokAggregate = ProductCategory.builder()
                .name(" Alapanyagok")
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildAlapanyagok() {
        alapanyagok = ProductCategory.builder()
                .name("Alapanyagok")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }
    
    private void buildSos() {
        sos = ProductCategory.builder()
                .name("Sós")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildEdes() {
        edes = ProductCategory.builder()
                .name("Édes")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildItallap() {
        itallap = ProductCategory.builder()
                .name("Itallap")
                .orderNumber(1)
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildShotok() {
        shotok = ProductCategory.builder()
                .name("Shotok")
                .orderNumber(6)
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildSorok() {
        sorok = ProductCategory.builder()
                .name("Sörök")
                .orderNumber(1)
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildCsapolt() {
        csapolt = ProductCategory.builder()
                .name("Csapolt")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildUveges() {
        uvegesSor = ProductCategory.builder()
                .name("Üveges")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildBorok() {
        borok = ProductCategory.builder()
                .name("Borok")
                .orderNumber(2)
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildUveg() {
        uvegBor = ProductCategory.builder()
                .name("Üveg")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildBorDecire() {
        borDecire = ProductCategory.builder()
                .name("Bor decire")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildFroccsok() {
        froccsok = ProductCategory.builder()
                .name("Fröccsök")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildRovidek() {
        rovidek = ProductCategory.builder()
                .name("Rövidek")
                .orderNumber(4)
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildPalinkak() {
        palinkak = ProductCategory.builder()
                .name("Pálinkák")
                .orderNumber(5)
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildUdito() {
        uditok = ProductCategory.builder()
                .name("Üdítők")
                .orderNumber(3)
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildUvegesUdito() {
        uvegesUdito = ProductCategory.builder()
                .name("Üveges üdítő")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildLimo() {
        limo = ProductCategory.builder()
                .name("Limó")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildUditoDecire() {
        uditoDecire = ProductCategory.builder()
                .name("Üdítő decire")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildForroItalok() {
        forroItalok = ProductCategory.builder()
                .name("Forró italok")
                .orderNumber(7)
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildTea() {
        tea = ProductCategory.builder()
                .name("Tea")
                .type(ProductCategoryType.AGGREGATE)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildFilteres() {
        filteres = ProductCategory.builder()
                .name("Filteres")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildPiramis() {
        piramis = ProductCategory.builder()
                .name("Piramis")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildForraltBor() {
        forraltBor = ProductCategory.builder()
                .name("Forralt bor")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildKave() {
        kave = ProductCategory.builder()
                .name("Kávé")
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildAkciosItalok() {
        napiAkciok = ProductCategory.builder()
                .name("Napi akciók")
                .orderNumber(8)
                .type(ProductCategoryType.LEAF)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildPseudoAdHoc() {
        pseudoAdHoc = ProductCategory.builder()
                .name("pseudoAdHoc")
                .type(ProductCategoryType.PSEUDO)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildPseudoGameFee() {
        pseudoGameFee = ProductCategory.builder()
                .name("pseudoGameFee")
                .type(ProductCategoryType.PSEUDO)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private void buildReceiptPurchase() {
        receiptPurchase = Receipt.builder()
                .type(ReceiptType.PURCHASE)
                .records(new ArrayList<>())
                .status(ReceiptStatus.CLOSED)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(LocalDateTime.now())
                .closureTime(LocalDateTime.now())
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptInventory() {
        receiptInventory = Receipt.builder()
                .type(ReceiptType.INVENTORY)
                .records(new ArrayList<>())
                .status(ReceiptStatus.CLOSED)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(LocalDateTime.now())
                .closureTime(LocalDateTime.now())
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptDisposal() {
        receiptDisposal = Receipt.builder()
                .type(ReceiptType.DISPOSAL)
                .records(new ArrayList<>())
                .status(ReceiptStatus.CLOSED)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(LocalDateTime.now())
                .closureTime(LocalDateTime.now())
                .client(buildDefaultClient())
                .build();
    }

    private void buildReceiptOther() {
        receiptOther = Receipt.builder()
                .type(ReceiptType.OTHER)
                .records(new ArrayList<>())
                .status(ReceiptStatus.CLOSED)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(LocalDateTime.now())
                .closureTime(LocalDateTime.now())
                .client(buildDefaultClient())
                .build();
    }

    private Client buildDefaultClient() {
        return Client.builder()
                .name("TestClient")
                .address("TestAddress")
                .TAXNumber("TestTaxNumber")
                .build();
    }

    private void buildVatSerieOne() {
        vatSerie = VATSerie.builder()
                .status(VATStatus.VALID)
                .build();
    }

    private void buildVatOne() {
        vatOne = VAT.builder()
                .name(VATName.NORMAL)
                .status(VATStatus.VALID)
                .VAT(27)
                .build();
    }

    private void buildVatTwo() {
        vatTwo = VAT.builder()
                .name(VATName.REDUCED)
                .status(VATStatus.VALID)
                .VAT(18)
                .build();
    }

    private void buildVatThree() {
        vatThree = VAT.builder()
                .name(VATName.GREATLY_REDUCED)
                .status(VATStatus.VALID)
                .VAT(5)
                .build();
    }

    private void buildVatFour() {
        vatFour = VAT.builder()
                .name(VATName.TAX_TICKET)
                .status(VATStatus.VALID)
                .VAT(0)
                .build();
    }

    private void buildVatFive() {
        vatFive = VAT.builder()
                .name(VATName.TAX_FREE)
                .status(VATStatus.VALID)
                .VAT(0)
                .build();
    }

    private void buildTable1() {
        table1 = Table.builder()
                .number(1)
                .capacity(7)
                .coordinateX(40)
                .coordinateY(200)
                .dimensionX(120)
                .dimensionY(80)
                .visible(true)
                .type(TableType.NORMAL)
                .build();
    }
    private void buildTable2() {
        table2 = Table.builder()
                .number(2)
                .capacity(2)
                .coordinateX(40)
                .coordinateY(350)
                .dimensionX(80)
                .dimensionY(80)
                .visible(true)
                .type(TableType.NORMAL)
                .build();
    }
    private void buildTable3() {
        table3 = Table.builder()
                .number(3)
                .capacity(4)
                .coordinateX(40)
                .coordinateY(500)
                .dimensionX(80)
                .dimensionY(80)
                .visible(true)
                .type(TableType.NORMAL)
                .build();
    }
    private void buildTable4() {
        table4 = Table.builder()
                .number(4)
                .capacity(4)
                .coordinateX(290)
                .coordinateY(30)
                .dimensionX(120)
                .dimensionY(120)
                .visible(true)
                .type(TableType.NORMAL)
                .build();
    }
    private void buildTable5() {
        table5 = Table.builder()
                .number(5)
                .capacity(8)
                .coordinateX(310)
                .coordinateY(230)
                .dimensionX(80)
                .dimensionY(160)
                .visible(true)
                .type(TableType.NORMAL)
                .build();
    }
    private void buildTable6() {
        table6 = Table.builder()
                .number(6)
                .capacity(4)
                .coordinateX(310)
                .coordinateY(530)
                .dimensionX(80)
                .dimensionY(80)
                .visible(true)
                .type(TableType.NORMAL)
                .build();
    }
    private void buildTable7() {
        table7 = Table.builder()
                .number(7)
                .capacity(2)
                .coordinateX(540)
                .coordinateY(40)
                .dimensionX(80)
                .dimensionY(40)
                .visible(true)
                .type(TableType.NORMAL)
                .build();
    }
    private void buildTable8() {
        table8 = Table.builder()
                .number(8)
                .capacity(4)
                .coordinateX(540)
                .coordinateY(180)
                .dimensionX(80)
                .dimensionY(80)
                .visible(true)
                .type(TableType.NORMAL)
                .build();
    }
    private void buildTable9() {
        table9 = Table.builder()
                .number(9)
                .capacity(4)
                .coordinateX(540)
                .coordinateY(350)
                .dimensionX(80)
                .dimensionY(80)
                .visible(true)
                .type(TableType.NORMAL)
                .build();
    }
    private void buildTable10() {
        table10 = Table.builder()
                .number(10)
                .capacity(3)
                .coordinateX(670)
                .coordinateY(60)
                .dimensionX(80)
                .dimensionY(80)
                .visible(true)
                .type(TableType.NORMAL)
                .build();
    }
    private void buildTable11() {
        table11 = Table.builder()
                .number(11)
                .capacity(6)
                .coordinateX(780)
                .coordinateY(140)
                .dimensionX(80)
                .dimensionY(120)
                .visible(true)
                .type(TableType.NORMAL)
                .build();
    }

    private void buildTable12() {
        table12 = Table.builder()
                .number(12)
                .capacity(5)
                .coordinateX(780)
                .coordinateY(350)
                .dimensionX(80)
                .dimensionY(80)
                .visible(true)
                .type(TableType.NORMAL)
                .build();
    }

    private void buildTable13() {
        table13 = Table.builder()
                .number(13)
                .capacity(8)
                .coordinateX(780)
                .coordinateY(490)
                .dimensionX(80)
                .dimensionY(120)
                .visible(true)
                .type(TableType.NORMAL)
                .build();
    }

    private void buildTablePurchase() {
        tablePurchase = Table.builder()
                .number(1000)
                .type(TableType.PURCHASE)
                .build();
    }

    private void buildTableInventory() {
        tableInventory = Table.builder()
                .number(1001)
                .type(TableType.INVENTORY)
                .build();
    }

    private void buildTableDisposal() {
        tableDisposal = Table.builder()
                .number(1002)
                .type(TableType.DISPOSAL)
                .build();
    }

    private void buildTableOrphanage() {
        tableOrphanage = Table.builder()
                .number(1004)
                .type(TableType.ORPHANAGE)
                .build();
    }

    private void buildTableOther() {
        tableOther = Table.builder()
                .number(1003)
                .type(TableType.OTHER)
                .build();
    }

    private void productCategories() {
        rootAndAggregates();
        aggregatesAndAggregates();
        aggregatesAndLeafs();
        leafsAndPseudos();
    }

    private void rootAndAggregates() {
        root.setChildren(new HashSet<>(
                Arrays.asList(etlap, itallap, menukAggregate, alapanyagokAggregate)));
        etlap.setParent(root);
        itallap.setParent(root);
        menukAggregate.setParent(root);
        alapanyagokAggregate.setParent(root);
    }

    private void aggregatesAndAggregates() {
        etlap.setChildren(new HashSet<>(
                Arrays.asList(ragcsak, etelek)));
        itallap.setChildren(new HashSet<>(
                Arrays.asList(shotok, sorok, borok, rovidek, palinkak, uditok, forroItalok, napiAkciok)));
        menukAggregate.setChildren(new HashSet<>(
                Arrays.asList(menuk)));
        alapanyagokAggregate.setChildren(new HashSet<>(
                Arrays.asList(alapanyagok)));
        ragcsak.setParent(etlap);
        etelek.setParent(etlap);
        shotok.setParent(itallap);
        sorok.setParent(itallap);
        borok.setParent(itallap);
        rovidek.setParent(itallap);
        palinkak.setParent(itallap);
        uditok.setParent(itallap);
        forroItalok.setParent(itallap);
        napiAkciok.setParent(itallap);
        menuk.setParent(menukAggregate);
        alapanyagok.setParent(alapanyagokAggregate);
    }

    private void aggregatesAndLeafs() {
        ragcsak.setChildren(new HashSet<>(
                Arrays.asList(sos, edes)));
        sorok.setChildren(new HashSet<>(
                Arrays.asList(uvegesSor, csapolt)));
        borok.setChildren(new HashSet<>(
                Arrays.asList(uvegBor, borDecire, froccsok)));
        uditok.setChildren(new HashSet<>(
                Arrays.asList(uvegesUdito, limo, uditoDecire)));
        forroItalok.setChildren(new HashSet<>(
                Arrays.asList(tea, forraltBor, kave)));
        sos.setParent(ragcsak);
        edes.setParent(ragcsak);

        uvegesSor.setParent(sorok);
        csapolt.setParent(sorok);

        uvegBor.setParent(borok);
        borDecire.setParent(borok);
        froccsok.setParent(borok);

        uvegesUdito.setParent(uditok);
        limo.setParent(uditok);
        uditoDecire.setParent(uditok);

        tea.setParent(forroItalok);
        forraltBor.setParent(forroItalok);
        kave.setParent(forroItalok);

        tea.setChildren(new HashSet<>(
                Arrays.asList(filteres, piramis)));
        filteres.setParent(tea);
        piramis.setParent(tea);
    }
    
    private void leafsAndPseudos() {

        // Ragcsak
        PseudoSosPerec.setParent(sos);
        PseudoMogyoro.setParent(sos);
        PseudoRagcsaMix1Perec2Mogyi.setParent(sos);
        PseudoRagcsaMix2Perec1Mogyi.setParent(sos);
        PseudoRagcsaMix3Perec.setParent(sos);
        PseudoNachosSosSajt.setParent(sos);
        PseudoNachosSosChili.setParent(sos);
        PseudoNachosBBQSajt.setParent(sos);
        PseudoNachosBBQChili.setParent(sos);
        PseudoChips.setParent(sos);
        PseudoPopcorn.setParent(sos);
        PseudoGumicukor.setParent(edes);
        PseudoBalatonszelet.setParent(edes);
        PseudoCsoki.setParent(edes);
        
        // Ételek
        PseudoMelegszendivcsSonkas.setParent(etelek);
        PseudoMelegszendivcsSzalamis.setParent(etelek);
        PseudoMelegszendivcsVega.setParent(etelek);
        PseudoSajtosCsikok.setParent(etelek);
        PseudoZsirosDeszka.setParent(etelek);
        PseudoWrap.setParent(etelek);
        PseudoSpecialisFeltetekPiritossalTonhal.setParent(etelek);
        PseudoSpecialisFeltetekPiritossalPadlizsan.setParent(etelek);
        PseudoSajttal.setParent(etelek);
        PseudoGameUpTal.setParent(etelek);
        PseudoGameUpFalankTal.setParent(etelek);
        
        // Menuk
        PseudoAgentShotCover.setParent(menuk);
        PseudoLimonCept.setParent(menuk);
        PseudoSplendBor.setParent(menuk);
        PseudoTatraTime.setParent(menuk);
        PseudoSorrelAzEmberisegEllen.setParent(menuk);

        // Alapanyagok
        PseudoCitrom.setParent(alapanyagok);
        PseudoNarancs.setParent(alapanyagok);

        PseudoMalnaSzirup.setParent(alapanyagok);
        PseudoEperNarancsSzirup.setParent(alapanyagok);
        PseudoCitrusSzirup.setParent(alapanyagok);
        PseudoMeggySzirup.setParent(alapanyagok);

        PseudoTabasco.setParent(alapanyagok);
        PseudoBlueCuracao.setParent(alapanyagok);

        PseudoFagyiGyumi.setParent(alapanyagok);

        PseudoBarnaCukor.setParent(alapanyagok);
        PseudoFeherCukor.setParent(alapanyagok);
        PseudoMez.setParent(alapanyagok);

        PseudoTej.setParent(alapanyagok);

        PseudoTortillaSos.setParent(alapanyagok);
        PseudoTortillaBBQ.setParent(alapanyagok);
        PseudoNachosSajtszosz.setParent(alapanyagok);
        PseudoNachosChiliszosz.setParent(alapanyagok);

        PseudoBagett.setParent(alapanyagok);
        PseudoZsir.setParent(alapanyagok);

        PseudoLilahagyma.setParent(alapanyagok);
        PseudoPirospaprika.setParent(alapanyagok);
        PseudoSalata.setParent(alapanyagok);
        PseudoParadicsom.setParent(alapanyagok);
        PseudoUborka.setParent(alapanyagok);
        PseudoPiritottHagyma.setParent(alapanyagok);

        PseudoTrappista.setParent(alapanyagok);
        PseudoCheddar.setParent(alapanyagok);
        PseudoKecskesajt.setParent(alapanyagok);
        PseudoCamambert.setParent(alapanyagok);

        PseudoSonka.setParent(alapanyagok);
        PseudoSzalami.setParent(alapanyagok);
        PseudoTonhalKrem.setParent(alapanyagok);

        PseudoMargarin.setParent(alapanyagok);

        PseudoMustar.setParent(alapanyagok);
        PseudoMajonez.setParent(alapanyagok);
        PseudoAjvar.setParent(alapanyagok);

        PseudoTortillaLap.setParent(alapanyagok);

        // Csapolt sorok
        PseudoSoproni03.setParent(csapolt);
        PseudoSoproni05.setParent(csapolt);
        PseudoEdelweiss03.setParent(csapolt);
        PseudoEdelweiss05.setParent(csapolt);

        // Uveges sorok
        PseudoKrusoviceSvetle.setParent(uvegesSor);
        PseudoSoproniDemon.setParent(uvegesSor);
        PseudoSoproniMaxx.setParent(uvegesSor);
        PseudoHeineken.setParent(uvegesSor);
        PseudoGosserNaturRadler.setParent(uvegesSor);
        PseudoGosserNaturRadler00.setParent(uvegesSor);
        PseudoBekesSzentadrasiMeggyes.setParent(uvegesSor);
        PseudoStrongbowDarkfruit.setParent(uvegesSor);
        PseudoStrongbowGoldAppleCider.setParent(uvegesSor);
        PseudoEdelweiss.setParent(uvegesSor);

        // Borok
        PseudoHazBoraNagyEsNagy.setParent(borDecire);
        PseudoHilltopIrsaiOliver.setParent(uvegBor);
        PseudoHilltopIrsaiOliverDecire.setParent(borDecire);
        PseudoGereAttilaOlaszrizling.setParent(uvegBor);

        PseudoHazBoraLisiczaRoseCuvee.setParent(borDecire);
        PseudoMeszarosPinotNoireRose.setParent(uvegBor);
        PseudoMeszarosPinotNoireRoseDecire.setParent(borDecire);

        PseudoHazBoraPolgarSerumVeritas.setParent(borDecire);
        PseudoVinczeMerlot.setParent(uvegBor);
        PseudoVylyanCabernetSauvignon.setParent(uvegBor);
        PseudoMeszarosHidasptereCabernetFrancReserve.setParent(uvegBor);

        PseudoSzoda.setParent(borDecire);

        // Fröccsök
        PseudoHazBoraNagyEsNagyKisfroccs.setParent(froccsok);
        PseudoHazBoraNagyEsNagyNagyfroccs.setParent(froccsok);
        PseudoHazBoraNagyEsNagyVicehazmester.setParent(froccsok);
        PseudoHazBoraNagyEsNagyHazmester.setParent(froccsok);
        PseudoHazBoraNagyEsNagyHosszulepes.setParent(froccsok);
        PseudoHazBoraNagyEsNagySportFroccs.setParent(froccsok);

        PseudoHilltopIrsaiOliverKisfroccs.setParent(froccsok);
        PseudoHilltopIrsaiOliverNagyfroccs.setParent(froccsok);
        PseudoHilltopIrsaiOliverVicehazmester.setParent(froccsok);
        PseudoHilltopIrsaiOliverHazmester.setParent(froccsok);
        PseudoHilltopIrsaiOliverHosszulepes.setParent(froccsok);
        PseudoHilltopIrsaiOliverSportFroccs.setParent(froccsok);

        PseudoHazBoraLisiczaRoseCuveeKisfroccs.setParent(froccsok);
        PseudoHazBoraLisiczaRoseCuveeNagyfroccs.setParent(froccsok);
        PseudoHazBoraLisiczaRoseCuveeVicehazmester.setParent(froccsok);
        PseudoHazBoraLisiczaRoseCuveeHazmester.setParent(froccsok);
        PseudoHazBoraLisiczaRoseCuveeHosszulepes.setParent(froccsok);
        PseudoHazBoraLisiczaRoseCuveeSportFroccs.setParent(froccsok);

        PseudoMeszarosPinotNoirRoseKisfroccs.setParent(froccsok);
        PseudoMeszarosPinotNoirRoseNagyfroccs.setParent(froccsok);
        PseudoMeszarosPinotNoirRoseVicehazmester.setParent(froccsok);
        PseudoMeszarosPinotNoirRoseHazmester.setParent(froccsok);
        PseudoMeszarosPinotNoirRoseHosszulepes.setParent(froccsok);
        PseudoMeszarosPinotNoirRoseSportFroccs.setParent(froccsok);
        
        // Pezsgok
        PseudoTorleyGalaSzaraz.setParent(uvegBor);
        PseudoTorleyCharmantEdes.setParent(uvegBor);

        // Roviditalok
        PseudoJimBeam04.setParent(rovidek);
        PseudoJohnnieWalkerRedLabel04.setParent(rovidek);
        PseudoJackDaniels04.setParent(rovidek);
        PseudoTullamoreDew04.setParent(rovidek);

        PseudoRoyal04.setParent(rovidek);
        PseudoFinlandia04.setParent(rovidek);

        PseudoBacardiSuperior04.setParent(rovidek);
        PseudoCaptainMorganSpicedRum04.setParent(rovidek);

        PseudoBeefeater04.setParent(rovidek);

        PseudoTequilaSierraReposadoGold04.setParent(rovidek);
        PseudoTequilaSierraSilver04.setParent(rovidek);

        PseudoUnicum04.setParent(rovidek);
        PseudoJagermeister04.setParent(rovidek);
        PseudoBaileys08.setParent(rovidek);

        PseudoJimBeam02.setParent(rovidek);
        PseudoJohnnieWalkerRedLabel02.setParent(rovidek);
        PseudoJackDaniels02.setParent(rovidek);
        PseudoTullamoreDew02.setParent(rovidek);

        PseudoRoyal02.setParent(rovidek);
        PseudoFinlandia02.setParent(rovidek);

        PseudoBacardiSuperior02.setParent(rovidek);
        PseudoCaptainMorganSpicedRum02.setParent(rovidek);

        PseudoBeefeater02.setParent(rovidek);

        PseudoTequilaSierraReposadoGold02.setParent(rovidek);
        PseudoTequilaSierraSilver02.setParent(rovidek);

        PseudoUnicum02.setParent(rovidek);
        PseudoJagermeister02.setParent(rovidek);
        PseudoBaileys04.setParent(rovidek);

        // Palinkak/
        Pseudo_22KokuszTatratea04.setParent(palinkak);
        Pseudo_32CitrusTatratea04.setParent(palinkak);
        Pseudo_42BarackTatratea04.setParent(palinkak);
        Pseudo_52EredetiTatratea04.setParent(palinkak);
        Pseudo_62ErdeiGyumolcsTatratea04.setParent(palinkak);
        Pseudo_72OutlawBetyarTatratea04.setParent(palinkak);

        PseudoCseresznyePalinka04.setParent(palinkak);
        PseudoKajszibarackPalinka04.setParent(palinkak);
        PseudoSzilvapalinka04.setParent(palinkak);

        Pseudo_22KokuszTatratea02.setParent(palinkak);
        Pseudo_32CitrusTatratea02.setParent(palinkak);
        Pseudo_42BarackTatratea02.setParent(palinkak);
        Pseudo_52EredetiTatratea02.setParent(palinkak);
        Pseudo_62ErdeiGyumolcsTatratea02.setParent(palinkak);
        Pseudo_72OutlawBetyarTatratea02.setParent(palinkak);

        PseudoCseresznyePalinka02.setParent(palinkak);
        PseudoKajszibarackPalinka02.setParent(palinkak);
        PseudoSzilvapalinka02.setParent(palinkak);

        // Shotok
        PseudoFinca1.setParent(shotok);
        PseudoBang1.setParent(shotok);
        PseudoImagine1.setParent(shotok);

        PseudoFinca6.setParent(shotok);
        PseudoBang6.setParent(shotok);
        PseudoImagine6.setParent(shotok);
        PseudoRiffRaff6.setParent(shotok);

        PseudoFinca12.setParent(shotok);
        PseudoBang12.setParent(shotok);
        PseudoImagine12.setParent(shotok);
        PseudoRiffRaff12.setParent(shotok);

        // Uveges uditok
        PseudoCocaCola.setParent(uvegesUdito);
        PseudoCocaColaZero.setParent(uvegesUdito);
        PseudoFantaNarancs.setParent(uvegesUdito);
        PseudoSprite.setParent(uvegesUdito);
        PseudoKinleyGyomber.setParent(uvegesUdito);
        PseudoKinleyTonic.setParent(uvegesUdito);
        PseudoNesteaCitrom.setParent(uvegesUdito);
        PseudoNesteaBarack.setParent(uvegesUdito);

        // Kimert uditok
        PseudoCappyAlma.setParent(uditoDecire);
        PseudoCappyNarancs.setParent(uditoDecire);
        PseudoCappyBarack.setParent(uditoDecire);
        PseudoCappyAnanasz.setParent(uditoDecire);

        // Limonadek
        PseudoLimonadeMalna05.setParent(limo);
        PseudoLimonadeMeggy05.setParent(limo);
        PseudoLimonadeEperNarancs05.setParent(limo);
        PseudoLimonadeCitrus05.setParent(limo);

        PseudoLimonadeMalna10.setParent(limo);
        PseudoLimonadeMeggy10.setParent(limo);
        PseudoLimonadeEperNarancs10.setParent(limo);
        PseudoLimonadeCitrus10.setParent(limo);

        // Asvanyviz
        PseudoNaturaquaSzensavas.setParent(uvegesUdito);
        PseudoNaturaquaSzensavmentes.setParent(uvegesUdito);

        // Energiaitalok
        PseudoBurnOriginal.setParent(uvegesUdito);
        PseudoBurnZero.setParent(uvegesUdito);
        PseudoMonsterEnergy.setParent(uvegesUdito);
        PseudoMonsterAssault.setParent(uvegesUdito);
        PseudoMonsterRehab.setParent(uvegesUdito);

        // Filteres Teaak
        PseudoDallmyrFekete.setParent(filteres);
        PseudoDallmyrGyumolcs.setParent(filteres);
        PseudoDallmyrZold.setParent(filteres);

        PseudoPiramis1.setParent(piramis);
        PseudoPiramis2.setParent(piramis);
        PseudoPiramis3.setParent(piramis);
        PseudoPiramis4.setParent(piramis);
        pseudoAdHoc.setParent(piramis);
        pseudoGameFee.setParent(piramis);


        // Kavek
        PseudoEspresso.setParent(kave);
        PseudoAmericano.setParent(kave);
        PseudoCappuccino.setParent(kave);
        PseudoCaffeLatte.setParent(kave);
        PseudoLatteMacchiato.setParent(kave);
        PseudoCaffeMelange.setParent(kave);
        PseudoForroCsokiBarna.setParent(kave);
        PseudoForroCsokiFeher.setParent(kave);

        // Napi akciok
        PseudoCaptainAndGyomber.setParent(napiAkciok);
        PseudoGinTonic.setParent(napiAkciok);
        PseudoJackAndCoke.setParent(napiAkciok);
        PseudoVodkaSzoda.setParent(napiAkciok);

        // Rágcsák
        sos.setChildren(new HashSet<>(
                Arrays.asList(
                    PseudoSosPerec,
                    PseudoMogyoro,
                    PseudoRagcsaMix1Perec2Mogyi,
                    PseudoRagcsaMix2Perec1Mogyi,
                    PseudoRagcsaMix3Perec,
                    PseudoNachosSosSajt,
                    PseudoNachosSosChili,
                    PseudoNachosBBQSajt,
                    PseudoNachosBBQChili,
                    PseudoChips,
                    PseudoPopcorn)));

        edes.setChildren(new HashSet<>(
                Arrays.asList(
                    PseudoGumicukor,
                    PseudoBalatonszelet,
                    PseudoCsoki)));

        // Ételek
        etelek.setChildren(new HashSet<>(
                Arrays.asList(
                    PseudoMelegszendivcsSonkas,
                    PseudoMelegszendivcsSzalamis,
                    PseudoMelegszendivcsVega,
                    PseudoSajtosCsikok,
                    PseudoZsirosDeszka,
                    PseudoWrap,
                    PseudoSpecialisFeltetekPiritossalTonhal,
                    PseudoSpecialisFeltetekPiritossalPadlizsan,
                    PseudoSajttal,
                    PseudoGameUpTal,
                    PseudoGameUpFalankTal)));

        // Menuk
        menuk.setChildren(new HashSet<>(
                Arrays.asList(
                    PseudoAgentShotCover,
                    PseudoLimonCept,
                    PseudoSplendBor,
                    PseudoTatraTime,
                    PseudoSorrelAzEmberisegEllen)));

        // Alapanyagok
        alapanyagok.setChildren(new HashSet<>(
                Arrays.asList(
                    PseudoCitrom,
                    PseudoNarancs,

                    PseudoMalnaSzirup,
                    PseudoEperNarancsSzirup,
                    PseudoCitrusSzirup,
                    PseudoMeggySzirup,

                    PseudoTabasco,
                    PseudoBlueCuracao,

                    PseudoFagyiGyumi,

                    PseudoBarnaCukor,
                    PseudoFeherCukor,
                    PseudoMez,

                    PseudoTej,

                    PseudoTortillaSos,
                    PseudoTortillaBBQ,
                    PseudoNachosSajtszosz,
                    PseudoNachosChiliszosz,

                    PseudoBagett,
                    PseudoZsir,

                    PseudoLilahagyma,
                    PseudoPirospaprika,
                    PseudoSalata,
                    PseudoParadicsom,
                    PseudoUborka,
                    PseudoPiritottHagyma,

                    PseudoTrappista,
                    PseudoCheddar,
                    PseudoKecskesajt,
                    PseudoCamambert,

                    PseudoSonka,
                    PseudoSzalami,
                    PseudoTonhalKrem,

                    PseudoMargarin,

                    PseudoMustar,
                    PseudoMajonez,
                    PseudoAjvar,

                    PseudoTortillaLap)));
        
        // Csapolt sorok
        csapolt.setChildren(new HashSet<>(
                Arrays.asList(PseudoSoproni03, PseudoSoproni05, PseudoEdelweiss03, PseudoEdelweiss05)));

        // Uveges sorok
        uvegesSor.setChildren(new HashSet<>(
                Arrays.asList(PseudoKrusoviceSvetle,
                    PseudoSoproniDemon,
                    PseudoSoproniMaxx,
                    PseudoHeineken,
                    PseudoGosserNaturRadler,
                    PseudoGosserNaturRadler00,
                    PseudoBekesSzentadrasiMeggyes,
                    PseudoStrongbowDarkfruit,
                    PseudoStrongbowGoldAppleCider,
                    PseudoEdelweiss)));

        // Borok
        uvegBor.setChildren(new HashSet<>(
                Arrays.asList(
                    PseudoHilltopIrsaiOliver,
                    PseudoGereAttilaOlaszrizling,
                    PseudoMeszarosPinotNoireRose,
                    PseudoVinczeMerlot,
                    PseudoVylyanCabernetSauvignon,
                    PseudoMeszarosHidasptereCabernetFrancReserve,
        // Pezsgok
                    PseudoTorleyGalaSzaraz,
                    PseudoTorleyCharmantEdes)));

        borDecire.setChildren(new HashSet<>(
                Arrays.asList(
                    PseudoHazBoraNagyEsNagy,
                    PseudoHazBoraLisiczaRoseCuvee,
                    PseudoHazBoraPolgarSerumVeritas,
                    PseudoHilltopIrsaiOliverDecire,
                    PseudoMeszarosPinotNoireRoseDecire,
                    PseudoSzoda)));

        // Fröccsök
        froccsok.setChildren(new HashSet<>(
                Arrays.asList(
                    PseudoHazBoraNagyEsNagyKisfroccs,
                    PseudoHazBoraNagyEsNagyNagyfroccs,
                    PseudoHazBoraNagyEsNagyVicehazmester,
                    PseudoHazBoraNagyEsNagyHazmester,
                    PseudoHazBoraNagyEsNagyHosszulepes,
                    PseudoHazBoraNagyEsNagySportFroccs,
            
                    PseudoHilltopIrsaiOliverKisfroccs,
                    PseudoHilltopIrsaiOliverNagyfroccs,
                    PseudoHilltopIrsaiOliverVicehazmester,
                    PseudoHilltopIrsaiOliverHazmester,
                    PseudoHilltopIrsaiOliverHosszulepes,
                    PseudoHilltopIrsaiOliverSportFroccs,
            
                    PseudoHazBoraLisiczaRoseCuveeKisfroccs,
                    PseudoHazBoraLisiczaRoseCuveeNagyfroccs,
                    PseudoHazBoraLisiczaRoseCuveeVicehazmester,
                    PseudoHazBoraLisiczaRoseCuveeHazmester,
                    PseudoHazBoraLisiczaRoseCuveeHosszulepes,
                    PseudoHazBoraLisiczaRoseCuveeSportFroccs,
            
                    PseudoMeszarosPinotNoirRoseKisfroccs,
                    PseudoMeszarosPinotNoirRoseNagyfroccs,
                    PseudoMeszarosPinotNoirRoseVicehazmester,
                    PseudoMeszarosPinotNoirRoseHazmester,
                    PseudoMeszarosPinotNoirRoseHosszulepes,
                    PseudoMeszarosPinotNoirRoseSportFroccs)));       
        
        // Roviditalok
        rovidek.setChildren(new HashSet<>(
                Arrays.asList(
                PseudoJimBeam04,
                PseudoJohnnieWalkerRedLabel04,
                PseudoJackDaniels04,
                PseudoTullamoreDew04,

                PseudoRoyal04,
                PseudoFinlandia04,

                PseudoBacardiSuperior04,
                PseudoCaptainMorganSpicedRum04,

                PseudoBeefeater04,

                PseudoTequilaSierraReposadoGold04,
                PseudoTequilaSierraSilver04,

                PseudoUnicum04,
                PseudoJagermeister04,
                PseudoBaileys08,

                PseudoJimBeam02,
                PseudoJohnnieWalkerRedLabel02,
                PseudoJackDaniels02,
                PseudoTullamoreDew02,

                PseudoRoyal02,
                PseudoFinlandia02,

                PseudoBacardiSuperior02,
                PseudoCaptainMorganSpicedRum02,

                PseudoBeefeater02,

                PseudoTequilaSierraReposadoGold02,
                PseudoTequilaSierraSilver02,

                PseudoUnicum02,
                PseudoJagermeister02,
                PseudoBaileys04)));

        // Palinkak/
        palinkak.setChildren(new HashSet<>(
                Arrays.asList(
                    Pseudo_22KokuszTatratea04,
                    Pseudo_32CitrusTatratea04,
                    Pseudo_42BarackTatratea04,
                    Pseudo_52EredetiTatratea04,
                    Pseudo_62ErdeiGyumolcsTatratea04,
                    Pseudo_72OutlawBetyarTatratea04,

                    PseudoCseresznyePalinka04,
                    PseudoKajszibarackPalinka04,
                    PseudoSzilvapalinka04,

                    Pseudo_22KokuszTatratea02,
                    Pseudo_32CitrusTatratea02,
                    Pseudo_42BarackTatratea02,
                    Pseudo_52EredetiTatratea02,
                    Pseudo_62ErdeiGyumolcsTatratea02,
                    Pseudo_72OutlawBetyarTatratea02,

                    PseudoCseresznyePalinka02,
                    PseudoKajszibarackPalinka02,
                    PseudoSzilvapalinka02)));

        // Shotok
        shotok.setChildren(new HashSet<>(
                Arrays.asList(
                    PseudoFinca1,
                    PseudoBang1,
                    PseudoImagine1,

                    PseudoFinca6,
                    PseudoBang6,
                    PseudoImagine6,
                    PseudoRiffRaff6,

                    PseudoFinca12,
                    PseudoBang12,
                    PseudoImagine12,
                    PseudoRiffRaff12)));

        // Uveges uditok
        uvegesUdito.setChildren(new HashSet<>(
                Arrays.asList(
                    PseudoCocaCola,
                    PseudoCocaColaZero,
                    PseudoFantaNarancs,
                    PseudoSprite,
                    PseudoKinleyGyomber,
                    PseudoKinleyTonic,
                    PseudoNesteaCitrom,
                    PseudoNesteaBarack,
        // Asvanyviz
                    PseudoNaturaquaSzensavas,
                    PseudoNaturaquaSzensavmentes,
        // Energiaitalok
                    PseudoBurnOriginal,
                    PseudoBurnZero,
                    PseudoMonsterEnergy,
                    PseudoMonsterAssault,
                    PseudoMonsterRehab)));

        // Kimert uditok
        uditoDecire.setChildren(new HashSet<>(
                Arrays.asList(
                    PseudoCappyAlma,
                    PseudoCappyNarancs,
                    PseudoCappyBarack,
                    PseudoCappyAnanasz)));

        // Limonadek
        limo.setChildren(new HashSet<>(
                Arrays.asList(
                    PseudoLimonadeMalna05,
                    PseudoLimonadeMeggy05,
                    PseudoLimonadeEperNarancs05,
                    PseudoLimonadeCitrus05,

                    PseudoLimonadeMalna10,
                    PseudoLimonadeMeggy10,
                    PseudoLimonadeEperNarancs10,
                    PseudoLimonadeCitrus10)));


        // Filteres Teaak
        filteres.setChildren(new HashSet<>(
                Arrays.asList(
                    PseudoDallmyrFekete,
                    PseudoDallmyrGyumolcs,
                    PseudoDallmyrZold)));

        piramis.setChildren(new HashSet<>(
                Arrays.asList(
                    PseudoPiramis1,
                    PseudoPiramis2,
                    PseudoPiramis3,
                    PseudoPiramis4,
                    pseudoGameFee,
                    pseudoAdHoc)));

        // Kavek
        kave.setChildren(new HashSet<>(
                Arrays.asList(
                    PseudoEspresso,
                    PseudoAmericano,
                    PseudoCappuccino,
                    PseudoCaffeLatte,
                    PseudoLatteMacchiato,
                    PseudoCaffeMelange,
                    PseudoForroCsokiBarna,
                    PseudoForroCsokiFeher)));

        // Napi akciok
        napiAkciok.setChildren(new HashSet<>(
                Arrays.asList(
                    PseudoCaptainAndGyomber,
                    PseudoGinTonic,
                    PseudoJackAndCoke,
                    PseudoVodkaSzoda)));
    }

    private void categoriesAndPriceModifiers() {
        PseudoEdelweiss05.setPriceModifiers(new HashSet<>(
                Arrays.asList(priceModifierEdelweiss)));
        PseudoCaptainAndGyomber.setPriceModifiers(new HashSet<>(
                Arrays.asList(priceModifierCaptainAndGyomber)));
        PseudoGinTonic.setPriceModifiers(new HashSet<>(
                Arrays.asList(priceModifierGinTonic)));
        PseudoJackAndCoke.setPriceModifiers(new HashSet<>(
                Arrays.asList(priceModifierJackAndCoke)));
        PseudoVodkaSzoda.setPriceModifiers(new HashSet<>(
                Arrays.asList(priceModifierVodkaSzoda)));
        PseudoBekesSzentadrasiMeggyes.setPriceModifiers(new HashSet<>(
                Arrays.asList(priceModifierBekesSzentdrasiMeggyes)));

        priceModifierEdelweiss.setOwner(PseudoEdelweiss05);
        priceModifierCaptainAndGyomber.setOwner(PseudoCaptainAndGyomber);
        priceModifierGinTonic.setOwner(PseudoGinTonic);
        priceModifierJackAndCoke.setOwner(PseudoJackAndCoke);
        priceModifierVodkaSzoda.setOwner(PseudoVodkaSzoda);
        priceModifierBekesSzentdrasiMeggyes.setOwner(PseudoBekesSzentadrasiMeggyes);

//        pseudoTwo.setPriceModifiers(new HashSet<>(
//                Collections.singletonList(priceModifierFour)));
//        priceModifierOne.setOwner(pseudoOne);
//        priceModifierTwo.setOwner(pseudoOne);
//        priceModifierThree.setOwner(leafTwo);
//        priceModifierFour.setOwner(pseudoTwo);
    }

    private void productsAndCategories() {

            /*----- ETLAP -----*/
        // Rágcsák
        SosPerec.setCategory(PseudoSosPerec);
        Mogyoro.setCategory(PseudoMogyoro);
        RagcsaMix1Perec2Mogyi.setCategory(PseudoRagcsaMix1Perec2Mogyi);
        RagcsaMix2Perec1Mogyi.setCategory(PseudoRagcsaMix2Perec1Mogyi);
        RagcsaMix3Perec.setCategory(PseudoRagcsaMix3Perec);
        NachosSosSajt.setCategory(PseudoNachosSosSajt);
        NachosSosChili.setCategory(PseudoNachosSosChili);
        NachosBBQSajt.setCategory(PseudoNachosBBQSajt);
        NachosBBQChili.setCategory(PseudoNachosBBQChili);
        Chips.setCategory(PseudoChips);
        Popcorn.setCategory(PseudoPopcorn);
        Gumicukor.setCategory(PseudoGumicukor);
        Balatonszelet.setCategory(PseudoBalatonszelet);
        Csoki.setCategory(PseudoCsoki);

        // Ételek
        MelegszendivcsSonkas.setCategory(PseudoMelegszendivcsSonkas);
        MelegszendivcsSzalamis.setCategory(PseudoMelegszendivcsSzalamis);
        MelegszendivcsVega.setCategory(PseudoMelegszendivcsVega);
        SajtosCsikok.setCategory(PseudoSajtosCsikok);
        ZsirosDeszka.setCategory(PseudoZsirosDeszka);
        Wrap.setCategory(PseudoWrap);
        SpecialisFeltetekPiritossalTonhal.setCategory(PseudoSpecialisFeltetekPiritossalTonhal);
        SpecialisFeltetekPiritossalPadlizsan.setCategory(PseudoSpecialisFeltetekPiritossalPadlizsan);
        Sajttal.setCategory(PseudoSajttal);
        GameUpTal.setCategory(PseudoGameUpTal);
        GameUpFalankTal.setCategory(PseudoGameUpFalankTal);

        // Menuk
        AgentShotCover.setCategory(PseudoAgentShotCover);
        LimonCept.setCategory(PseudoLimonCept);
        SplendBor.setCategory(PseudoSplendBor);
        TatraTime.setCategory(PseudoTatraTime);
        SorrelAzEmberisegEllen.setCategory(PseudoSorrelAzEmberisegEllen);

        // Alapanyagok
        Citrom.setCategory(PseudoCitrom);
        Narancs.setCategory(PseudoNarancs);

        MalnaSzirup.setCategory(PseudoMalnaSzirup);
        EperNarancsSzirup.setCategory(PseudoEperNarancsSzirup);
        CitrusSzirup.setCategory(PseudoCitrusSzirup);
        MeggySzirup.setCategory(PseudoMeggySzirup);

        Tabasco.setCategory(PseudoTabasco);
        BlueCuracao.setCategory(PseudoBlueCuracao);

        FagyiGyumi.setCategory(PseudoFagyiGyumi);

        BarnaCukor.setCategory(PseudoBarnaCukor);
        FeherCukor.setCategory(PseudoFeherCukor);
        Mez.setCategory(PseudoMez);

        Tej.setCategory(PseudoTej);

        TortillaSos.setCategory(PseudoTortillaSos);
        TortillaBBQ.setCategory(PseudoTortillaBBQ);
        NachosSajtszosz.setCategory(PseudoNachosSajtszosz);
        NachosChiliszosz.setCategory(PseudoNachosChiliszosz);

        Bagett.setCategory(PseudoBagett);
        Zsir.setCategory(PseudoZsir);

        Lilahagyma.setCategory(PseudoLilahagyma);
        Pirospaprika.setCategory(PseudoPirospaprika);
        Salata.setCategory(PseudoSalata);
        Paradicsom.setCategory(PseudoParadicsom);
        Uborka.setCategory(PseudoUborka);
        PiritottHagyma.setCategory(PseudoPiritottHagyma);

        Trappista.setCategory(PseudoTrappista);
        Cheddar.setCategory(PseudoCheddar);
        Kecskesajt.setCategory(PseudoKecskesajt);
        Camambert.setCategory(PseudoCamambert);

        Sonka.setCategory(PseudoSonka);
        Szalami.setCategory(PseudoSzalami);
        TonhalKrem.setCategory(PseudoTonhalKrem);

        Margarin.setCategory(PseudoMargarin);

        Mustar.setCategory(PseudoMustar);
        Majonez.setCategory(PseudoMajonez);
        Ajvar.setCategory(PseudoAjvar);

        TortillaLap.setCategory(PseudoTortillaLap);

        // Csapolt Sorok
        Soproni03.setCategory(PseudoSoproni03);
        Soproni05.setCategory(PseudoSoproni05);
        Edelweiss03.setCategory(PseudoEdelweiss03);
        Edelweiss05.setCategory(PseudoEdelweiss05);

        // Uveges sorok
        KrusoviceSvetle.setCategory(PseudoKrusoviceSvetle);
        SoproniDemon.setCategory(PseudoSoproniDemon);
        SoproniMaxx.setCategory(PseudoSoproniMaxx);
        Heineken.setCategory(PseudoHeineken);
        GosserNaturRadler.setCategory(PseudoGosserNaturRadler);
        GosserNaturRadler00.setCategory(PseudoGosserNaturRadler00);
        BekesSzentandrasiMeggyes.setCategory(PseudoBekesSzentadrasiMeggyes);
        StrongbowDarkfruit.setCategory(PseudoStrongbowDarkfruit);
        StrongbowGoldAppleCider.setCategory(PseudoStrongbowGoldAppleCider);
        Edelweiss.setCategory(PseudoEdelweiss);

        // Borok
        HazBoraNagyEsNagy.setCategory(PseudoHazBoraNagyEsNagy);
        HilltopIrsaiOliver.setCategory(PseudoHilltopIrsaiOliver);
        HilltopIrsaiOliverDecire.setCategory(PseudoHilltopIrsaiOliverDecire);
        GereAttilaOlaszrizling.setCategory(PseudoGereAttilaOlaszrizling);

        HazBoraLisiczaRoseCuvee.setCategory(PseudoHazBoraLisiczaRoseCuvee);
        MeszarosPinotNoirRose.setCategory(PseudoMeszarosPinotNoireRose);
        MeszarosPinotNoirRoseDecire.setCategory(PseudoMeszarosPinotNoireRoseDecire);

        
        // Fröccsök
        HazBoraNagyEsNagyKisfroccs.setCategory(PseudoHazBoraNagyEsNagyKisfroccs);
        HazBoraNagyEsNagyNagyfroccs.setCategory(PseudoHazBoraNagyEsNagyNagyfroccs);
        HazBoraNagyEsNagyVicehazmester.setCategory(PseudoHazBoraNagyEsNagyVicehazmester);
        HazBoraNagyEsNagyHazmester.setCategory(PseudoHazBoraNagyEsNagyHazmester);
        HazBoraNagyEsNagyHosszulepes.setCategory(PseudoHazBoraNagyEsNagyHosszulepes);
        HazBoraNagyEsNagySportFroccs.setCategory(PseudoHazBoraNagyEsNagySportFroccs);

        HilltopIrsaiOliverKisfroccs.setCategory(PseudoHilltopIrsaiOliverKisfroccs);
        HilltopIrsaiOliverNagyfroccs.setCategory(PseudoHilltopIrsaiOliverNagyfroccs);
        HilltopIrsaiOliverVicehazmester.setCategory(PseudoHilltopIrsaiOliverVicehazmester);
        HilltopIrsaiOliverHazmester.setCategory(PseudoHilltopIrsaiOliverHazmester);
        HilltopIrsaiOliverHosszulepes.setCategory(PseudoHilltopIrsaiOliverHosszulepes);
        HilltopIrsaiOliverSportFroccs.setCategory(PseudoHilltopIrsaiOliverSportFroccs);

        HazBoraLisiczaRoseCuveeKisfroccs.setCategory(PseudoHazBoraLisiczaRoseCuveeKisfroccs);
        HazBoraLisiczaRoseCuveeNagyfroccs.setCategory(PseudoHazBoraLisiczaRoseCuveeNagyfroccs);
        HazBoraLisiczaRoseCuveeVicehazmester.setCategory(PseudoHazBoraLisiczaRoseCuveeVicehazmester);
        HazBoraLisiczaRoseCuveeHazmester.setCategory(PseudoHazBoraLisiczaRoseCuveeHazmester);
        HazBoraLisiczaRoseCuveeHosszulepes.setCategory(PseudoHazBoraLisiczaRoseCuveeHosszulepes);
        HazBoraLisiczaRoseCuveeSportFroccs.setCategory(PseudoHazBoraLisiczaRoseCuveeSportFroccs);

        MeszarosPinotNoirRoseKisfroccs.setCategory(PseudoMeszarosPinotNoirRoseKisfroccs);
        MeszarosPinotNoirRoseNagyfroccs.setCategory(PseudoMeszarosPinotNoirRoseNagyfroccs);
        MeszarosPinotNoirRoseVicehazmester.setCategory(PseudoMeszarosPinotNoirRoseVicehazmester);
        MeszarosPinotNoirRoseHazmester.setCategory(PseudoMeszarosPinotNoirRoseHazmester);
        MeszarosPinotNoirRoseHosszulepes.setCategory(PseudoMeszarosPinotNoirRoseHosszulepes);
        MeszarosPinotNoirRoseSportFroccs.setCategory(PseudoMeszarosPinotNoirRoseSportFroccs);

        HazBoraPolgarSerumVeritas.setCategory(PseudoHazBoraPolgarSerumVeritas);
        VinczeMerlot.setCategory(PseudoVinczeMerlot);
        VylyanCabernetSauvignon.setCategory(PseudoVylyanCabernetSauvignon);
        MeszarosHidasptereCabernetFrancReserve.setCategory(PseudoMeszarosHidasptereCabernetFrancReserve);

        // Pezsgok
        TorleyGalaSzaraz.setCategory(PseudoTorleyGalaSzaraz);
        TorleyCharmantEdes.setCategory(PseudoTorleyCharmantEdes);

        // Roviditalok
        JimBeam04.setCategory(PseudoJimBeam04);
        JohnnieWalkerRedLabel04.setCategory(PseudoJohnnieWalkerRedLabel04);
        JackDaniels04.setCategory(PseudoJackDaniels04);
        TullamoreDew04.setCategory(PseudoTullamoreDew04);

        Royal04.setCategory(PseudoRoyal04);
        Finlandia04.setCategory(PseudoFinlandia04);

        BacardiSuperior04.setCategory(PseudoBacardiSuperior04);
        CaptainMorganSpicedRum04.setCategory(PseudoCaptainMorganSpicedRum04);

        Beefeater04.setCategory(PseudoBeefeater04);

        TequilaSierraReposadoGold04.setCategory(PseudoTequilaSierraReposadoGold04);
        TequilaSierraSilver04.setCategory(PseudoTequilaSierraSilver04);

        Unicum04.setCategory(PseudoUnicum04);
        Jagermeister04.setCategory(PseudoJagermeister04);
        Baileys08.setCategory(PseudoBaileys08);

        JimBeam02.setCategory(PseudoJimBeam02);
        JohnnieWalkerRedLabel02.setCategory(PseudoJohnnieWalkerRedLabel02);
        JackDaniels02.setCategory(PseudoJackDaniels02);
        TullamoreDew02.setCategory(PseudoTullamoreDew02);

        Royal02.setCategory(PseudoRoyal02);
        Finlandia02.setCategory(PseudoFinlandia02);

        BacardiSuperior02.setCategory(PseudoBacardiSuperior02);
        CaptainMorganSpicedRum02.setCategory(PseudoCaptainMorganSpicedRum02);

        Beefeater02.setCategory(PseudoBeefeater02);

        TequilaSierraReposadoGold02.setCategory(PseudoTequilaSierraReposadoGold02);
        TequilaSierraSilver02.setCategory(PseudoTequilaSierraSilver02);

        Unicum02.setCategory(PseudoUnicum02);
        Jagermeister02.setCategory(PseudoJagermeister02);
        Baileys04.setCategory(PseudoBaileys04);

        // Palinkak/
        _22KokuszTatratea04.setCategory(Pseudo_22KokuszTatratea04);
        _32CitrusTatratea04.setCategory(Pseudo_32CitrusTatratea04);
        _42BarackTatratea04.setCategory(Pseudo_42BarackTatratea04);
        _52EredetiTatratea04.setCategory(Pseudo_52EredetiTatratea04);
        _62ErdeiGyumolcsTatratea04.setCategory(Pseudo_62ErdeiGyumolcsTatratea04);
        _72OutlawBetyarTatratea04.setCategory(Pseudo_72OutlawBetyarTatratea04);

        CseresznyePalinka04.setCategory(PseudoCseresznyePalinka04);
        KajszibarackPalinka04.setCategory(PseudoKajszibarackPalinka04);
        Szilvapalinka04.setCategory(PseudoSzilvapalinka04);

        _22KokuszTatratea02.setCategory(Pseudo_22KokuszTatratea02);
        _32CitrusTatratea02.setCategory(Pseudo_32CitrusTatratea02);
        _42BarackTatratea02.setCategory(Pseudo_42BarackTatratea02);
        _52EredetiTatratea02.setCategory(Pseudo_52EredetiTatratea02);
        _62ErdeiGyumolcsTatratea02.setCategory(Pseudo_62ErdeiGyumolcsTatratea02);
        _72OutlawBetyarTatratea02.setCategory(Pseudo_72OutlawBetyarTatratea02);

        CseresznyePalinka02.setCategory(PseudoCseresznyePalinka02);
        KajszibarackPalinka02.setCategory(PseudoKajszibarackPalinka02);
        Szilvapalinka02.setCategory(PseudoSzilvapalinka02);

        // Shotok
        Finca1.setCategory(PseudoFinca1);
        Bang1.setCategory(PseudoBang1);
        Imagine1.setCategory(PseudoImagine1);

        Finca6.setCategory(PseudoFinca6);
        Bang6.setCategory(PseudoBang6);
        Imagine6.setCategory(PseudoImagine6);
        RiffRaff6.setCategory(PseudoRiffRaff6);

        Finca12.setCategory(PseudoFinca12);
        Bang12.setCategory(PseudoBang12);
        Imagine12.setCategory(PseudoImagine12);
        RiffRaff12.setCategory(PseudoRiffRaff12);

        // Uveges uditok
        CocaCola.setCategory(PseudoCocaCola);
        CocaColaZero.setCategory(PseudoCocaColaZero);
        FantaNarancs.setCategory(PseudoFantaNarancs);
        Sprite.setCategory(PseudoSprite);
        KinleyGyomber.setCategory(PseudoKinleyGyomber);
        KinleyTonic.setCategory(PseudoKinleyTonic);
        NesteaCitrom.setCategory(PseudoNesteaCitrom);
        NesteaBarack.setCategory(PseudoNesteaBarack);

        // Kimert uditok
        CappyAlma.setCategory(PseudoCappyAlma);
        CappyNarancs.setCategory(PseudoCappyNarancs);
        CappyBarack.setCategory(PseudoCappyBarack);
        CappyAnanasz.setCategory(PseudoCappyAnanasz);

        // Limonadek
        LimonadeMalna05.setCategory(PseudoLimonadeMalna05);
        LimonadeMeggy05.setCategory(PseudoLimonadeMeggy05);
        LimonadeEperNarancs05.setCategory(PseudoLimonadeEperNarancs05);
        LimonadeCitrus05.setCategory(PseudoLimonadeCitrus05);

        LimonadeMalna10.setCategory(PseudoLimonadeMalna10);
        LimonadeMeggy10.setCategory(PseudoLimonadeMeggy10);
        LimonadeEperNarancs10.setCategory(PseudoLimonadeEperNarancs10);
        LimonadeCitrus10.setCategory(PseudoLimonadeCitrus10);

        // Asvanyviz
        NaturaquaSzensavas.setCategory(PseudoNaturaquaSzensavas);
        NaturaquaSzensavmentes.setCategory(PseudoNaturaquaSzensavmentes);
        Szoda.setCategory(PseudoSzoda);

        // Energiaitalok
        BurnOriginal.setCategory(PseudoBurnOriginal);
        BurnZero.setCategory(PseudoBurnZero);
        MonsterEnergy.setCategory(PseudoMonsterEnergy);
        MonsterAssault.setCategory(PseudoMonsterAssault);
        MonsterRehab.setCategory(PseudoMonsterRehab);

        // Filteres Teaak
        DallmayrFekete.setCategory(PseudoDallmyrFekete);
        DallmayrGyumolcs.setCategory(PseudoDallmyrGyumolcs);
        DallmayrZold.setCategory(PseudoDallmyrZold);

        PiramisDarjeling.setCategory(PseudoPiramis1);
        PiramisMangoMaracuja.setCategory(PseudoPiramis2);
        PiramisAnanaszPapaja.setCategory(PseudoPiramis3);
        PiramisCitrusVerbena.setCategory(PseudoPiramis4);

        // Kavek
        Espresso.setCategory(PseudoEspresso);
        Americano.setCategory(PseudoAmericano);
        Cappuccino.setCategory(PseudoCappuccino);
        CaffeLatte.setCategory(PseudoCaffeLatte);
        LatteMacchiato.setCategory(PseudoLatteMacchiato);
        CaffeMelange.setCategory(PseudoCaffeMelange);
        ForroCsokiBarna.setCategory(PseudoForroCsokiBarna);
        ForroCsokiFeher.setCategory(PseudoForroCsokiFeher);

        // Akcios italok
        GinTonic.setCategory(PseudoGinTonic);
        CaptainAndGyomber.setCategory(PseudoCaptainAndGyomber);
        VodkaSzoda.setCategory(PseudoVodkaSzoda);
        JackAndCoke.setCategory(PseudoJackAndCoke);

        /*----- ETLAP -----*/
        // Rágcsák
        PseudoSosPerec.setProduct(SosPerec);
        PseudoMogyoro.setProduct(Mogyoro);
        PseudoRagcsaMix1Perec2Mogyi.setProduct(RagcsaMix1Perec2Mogyi);
        PseudoRagcsaMix2Perec1Mogyi.setProduct(RagcsaMix2Perec1Mogyi);
        PseudoRagcsaMix3Perec.setProduct(RagcsaMix3Perec);
        PseudoNachosSosSajt.setProduct(NachosSosSajt);
        PseudoNachosSosChili.setProduct(NachosSosChili);
        PseudoNachosBBQSajt.setProduct(NachosBBQSajt);
        PseudoNachosBBQChili.setProduct(NachosBBQChili);
        PseudoChips.setProduct(Chips);
        PseudoPopcorn.setProduct(Popcorn);
        PseudoGumicukor.setProduct(Gumicukor);
        PseudoBalatonszelet.setProduct(Balatonszelet);
        PseudoCsoki.setProduct(Csoki);

        // Ételek
        PseudoMelegszendivcsSonkas.setProduct(MelegszendivcsSonkas);
        PseudoMelegszendivcsSzalamis.setProduct(MelegszendivcsSzalamis);
        PseudoMelegszendivcsVega.setProduct(MelegszendivcsVega);
        PseudoSajtosCsikok.setProduct(SajtosCsikok);
        PseudoZsirosDeszka.setProduct(ZsirosDeszka);
        PseudoWrap.setProduct(Wrap);
        PseudoSpecialisFeltetekPiritossalTonhal.setProduct(SpecialisFeltetekPiritossalTonhal);
        PseudoSpecialisFeltetekPiritossalPadlizsan.setProduct(SpecialisFeltetekPiritossalPadlizsan);
        PseudoSajttal.setProduct(Sajttal);
        PseudoGameUpTal.setProduct(GameUpTal);
        PseudoGameUpFalankTal.setProduct(GameUpFalankTal);

        // Menuk
        PseudoAgentShotCover.setProduct(AgentShotCover);
        PseudoLimonCept.setProduct(LimonCept);
        PseudoSplendBor.setProduct(SplendBor);
        PseudoTatraTime.setProduct(TatraTime);
        PseudoSorrelAzEmberisegEllen.setProduct(SorrelAzEmberisegEllen);


        // Alapanyagok
        PseudoCitrom.setProduct(Citrom);
        PseudoNarancs.setProduct(Narancs);

        PseudoMalnaSzirup.setProduct(MalnaSzirup);
        PseudoEperNarancsSzirup.setProduct(EperNarancsSzirup);
        PseudoCitrusSzirup.setProduct(CitrusSzirup);
        PseudoMeggySzirup.setProduct(MeggySzirup);

        PseudoTabasco.setProduct(Tabasco);
        PseudoBlueCuracao.setProduct(BlueCuracao);

        PseudoFagyiGyumi.setProduct(FagyiGyumi);

        PseudoBarnaCukor.setProduct(BarnaCukor);
        PseudoFeherCukor.setProduct(FeherCukor);
        PseudoMez.setProduct(Mez);

        PseudoTej.setProduct(Tej);

        PseudoTortillaSos.setProduct(TortillaSos);
        PseudoTortillaBBQ.setProduct(TortillaBBQ);
        PseudoNachosSajtszosz.setProduct(NachosSajtszosz);
        PseudoNachosChiliszosz.setProduct(NachosChiliszosz);

        PseudoBagett.setProduct(Bagett);
        PseudoZsir.setProduct(Zsir);

        PseudoLilahagyma.setProduct(Lilahagyma);
        PseudoPirospaprika.setProduct(Pirospaprika);
        PseudoSalata.setProduct(Salata);
        PseudoParadicsom.setProduct(Paradicsom);
        PseudoUborka.setProduct(Uborka);
        PseudoPiritottHagyma.setProduct(PiritottHagyma);

        PseudoTrappista.setProduct(Trappista);
        PseudoCheddar.setProduct(Cheddar);
        PseudoKecskesajt.setProduct(Kecskesajt);
        PseudoCamambert.setProduct(Camambert);

        PseudoSonka.setProduct(Sonka);
        PseudoSzalami.setProduct(Szalami);
        PseudoTonhalKrem.setProduct(TonhalKrem);

        PseudoMargarin.setProduct(Margarin);

        PseudoMustar.setProduct(Mustar);
        PseudoMajonez.setProduct(Majonez);
        PseudoAjvar.setProduct(Ajvar);

        PseudoTortillaLap.setProduct(TortillaLap);
        
        // Csapolt Sorok
        PseudoSoproni03.setProduct(Soproni03);
        PseudoSoproni05.setProduct(Soproni05);
        PseudoEdelweiss03.setProduct(Edelweiss03);
        PseudoEdelweiss05.setProduct(Edelweiss05);

        // Uveges sorok
        PseudoKrusoviceSvetle.setProduct(KrusoviceSvetle);
        PseudoSoproniDemon.setProduct(SoproniDemon);
        PseudoSoproniMaxx.setProduct(SoproniMaxx);
        PseudoHeineken.setProduct(Heineken);
        PseudoGosserNaturRadler.setProduct(GosserNaturRadler);
        PseudoGosserNaturRadler00.setProduct(GosserNaturRadler00);
        PseudoBekesSzentadrasiMeggyes.setProduct(BekesSzentandrasiMeggyes);
        PseudoStrongbowDarkfruit.setProduct(StrongbowDarkfruit);
        PseudoStrongbowGoldAppleCider.setProduct(StrongbowGoldAppleCider);
        PseudoEdelweiss.setProduct(Edelweiss);

        // Borok
        PseudoHazBoraNagyEsNagy.setProduct(HazBoraNagyEsNagy);
        PseudoHilltopIrsaiOliver.setProduct(HilltopIrsaiOliver);
        PseudoHilltopIrsaiOliverDecire.setProduct(HilltopIrsaiOliverDecire);
        PseudoGereAttilaOlaszrizling.setProduct(GereAttilaOlaszrizling);

        PseudoHazBoraLisiczaRoseCuvee.setProduct(HazBoraLisiczaRoseCuvee);
        PseudoMeszarosPinotNoireRose.setProduct(MeszarosPinotNoirRose);
        PseudoMeszarosPinotNoireRoseDecire.setProduct(MeszarosPinotNoirRoseDecire);

        PseudoHazBoraPolgarSerumVeritas.setProduct(HazBoraPolgarSerumVeritas);
        PseudoVinczeMerlot.setProduct(VinczeMerlot);
        PseudoVylyanCabernetSauvignon.setProduct(VylyanCabernetSauvignon);
        PseudoMeszarosHidasptereCabernetFrancReserve.setProduct(MeszarosHidasptereCabernetFrancReserve);

        // Pezsgok
        PseudoTorleyGalaSzaraz.setProduct(TorleyGalaSzaraz);
        PseudoTorleyCharmantEdes.setProduct(TorleyCharmantEdes);
        
        // Fröccsök
        PseudoHazBoraNagyEsNagyKisfroccs.setProduct(HazBoraNagyEsNagyKisfroccs);
        PseudoHazBoraNagyEsNagyNagyfroccs.setProduct(HazBoraNagyEsNagyNagyfroccs);
        PseudoHazBoraNagyEsNagyVicehazmester.setProduct(HazBoraNagyEsNagyVicehazmester);
        PseudoHazBoraNagyEsNagyHazmester.setProduct(HazBoraNagyEsNagyHazmester);
        PseudoHazBoraNagyEsNagyHosszulepes.setProduct(HazBoraNagyEsNagyHosszulepes);
        PseudoHazBoraNagyEsNagySportFroccs.setProduct(HazBoraNagyEsNagySportFroccs);

        PseudoHilltopIrsaiOliverKisfroccs.setProduct(HilltopIrsaiOliverKisfroccs);
        PseudoHilltopIrsaiOliverNagyfroccs.setProduct(HilltopIrsaiOliverNagyfroccs);
        PseudoHilltopIrsaiOliverVicehazmester.setProduct(HilltopIrsaiOliverVicehazmester);
        PseudoHilltopIrsaiOliverHazmester.setProduct(HilltopIrsaiOliverHazmester);
        PseudoHilltopIrsaiOliverHosszulepes.setProduct(HilltopIrsaiOliverHosszulepes);
        PseudoHilltopIrsaiOliverSportFroccs.setProduct(HilltopIrsaiOliverSportFroccs);

        PseudoHazBoraLisiczaRoseCuveeKisfroccs.setProduct(HazBoraLisiczaRoseCuveeKisfroccs);
        PseudoHazBoraLisiczaRoseCuveeNagyfroccs.setProduct(HazBoraLisiczaRoseCuveeNagyfroccs);
        PseudoHazBoraLisiczaRoseCuveeVicehazmester.setProduct(HazBoraLisiczaRoseCuveeVicehazmester);
        PseudoHazBoraLisiczaRoseCuveeHazmester.setProduct(HazBoraLisiczaRoseCuveeHazmester);
        PseudoHazBoraLisiczaRoseCuveeHosszulepes.setProduct(HazBoraLisiczaRoseCuveeHosszulepes);
        PseudoHazBoraLisiczaRoseCuveeSportFroccs.setProduct(HazBoraLisiczaRoseCuveeSportFroccs);

        PseudoMeszarosPinotNoirRoseKisfroccs.setProduct(MeszarosPinotNoirRoseKisfroccs);
        PseudoMeszarosPinotNoirRoseNagyfroccs.setProduct(MeszarosPinotNoirRoseNagyfroccs);
        PseudoMeszarosPinotNoirRoseVicehazmester.setProduct(MeszarosPinotNoirRoseVicehazmester);
        PseudoMeszarosPinotNoirRoseHazmester.setProduct(MeszarosPinotNoirRoseHazmester);
        PseudoMeszarosPinotNoirRoseHosszulepes.setProduct(MeszarosPinotNoirRoseHosszulepes);
        PseudoMeszarosPinotNoirRoseSportFroccs.setProduct(MeszarosPinotNoirRoseSportFroccs);
        
        // Roviditalok
        PseudoJimBeam04.setProduct(JimBeam04);
        PseudoJohnnieWalkerRedLabel04.setProduct(JohnnieWalkerRedLabel04);
        PseudoJackDaniels04.setProduct(JackDaniels04);
        PseudoTullamoreDew04.setProduct(TullamoreDew04);

        PseudoRoyal04.setProduct(Royal04);
        PseudoFinlandia04.setProduct(Finlandia04);

        PseudoBacardiSuperior04.setProduct(BacardiSuperior04);
        PseudoCaptainMorganSpicedRum04.setProduct(CaptainMorganSpicedRum04);

        PseudoBeefeater04.setProduct(Beefeater04);

        PseudoTequilaSierraReposadoGold04.setProduct(TequilaSierraReposadoGold04);
        PseudoTequilaSierraSilver04.setProduct(TequilaSierraSilver04);

        PseudoUnicum04.setProduct(Unicum04);
        PseudoJagermeister04.setProduct(Jagermeister04);
        PseudoBaileys08.setProduct(Baileys08);

        PseudoJimBeam02.setProduct(JimBeam02);
        PseudoJohnnieWalkerRedLabel02.setProduct(JohnnieWalkerRedLabel02);
        PseudoJackDaniels02.setProduct(JackDaniels02);
        PseudoTullamoreDew02.setProduct(TullamoreDew02);

        PseudoRoyal02.setProduct(Royal02);
        PseudoFinlandia02.setProduct(Finlandia02);

        PseudoBacardiSuperior02.setProduct(BacardiSuperior02);
        PseudoCaptainMorganSpicedRum02.setProduct(CaptainMorganSpicedRum02);

        PseudoBeefeater02.setProduct(Beefeater02);

        PseudoTequilaSierraReposadoGold02.setProduct(TequilaSierraReposadoGold02);
        PseudoTequilaSierraSilver02.setProduct(TequilaSierraSilver02);

        PseudoUnicum02.setProduct(Unicum02);
        PseudoJagermeister02.setProduct(Jagermeister02);
        PseudoBaileys04.setProduct(Baileys04);

        // Palinkak/
        Pseudo_22KokuszTatratea04.setProduct(_22KokuszTatratea04);
        Pseudo_32CitrusTatratea04.setProduct(_32CitrusTatratea04);
        Pseudo_42BarackTatratea04.setProduct(_42BarackTatratea04);
        Pseudo_52EredetiTatratea04.setProduct(_52EredetiTatratea04);
        Pseudo_62ErdeiGyumolcsTatratea04.setProduct(_62ErdeiGyumolcsTatratea04);
        Pseudo_72OutlawBetyarTatratea04.setProduct(_72OutlawBetyarTatratea04);

        PseudoCseresznyePalinka04.setProduct(CseresznyePalinka04);
        PseudoKajszibarackPalinka04.setProduct(KajszibarackPalinka04);
        PseudoSzilvapalinka04.setProduct(Szilvapalinka04);

        Pseudo_22KokuszTatratea02.setProduct(_22KokuszTatratea02);
        Pseudo_32CitrusTatratea02.setProduct(_32CitrusTatratea02);
        Pseudo_42BarackTatratea02.setProduct(_42BarackTatratea02);
        Pseudo_52EredetiTatratea02.setProduct(_52EredetiTatratea02);
        Pseudo_62ErdeiGyumolcsTatratea02.setProduct(_62ErdeiGyumolcsTatratea02);
        Pseudo_72OutlawBetyarTatratea02.setProduct(_72OutlawBetyarTatratea02);

        PseudoCseresznyePalinka02.setProduct(CseresznyePalinka02);
        PseudoKajszibarackPalinka02.setProduct(KajszibarackPalinka02);
        PseudoSzilvapalinka02.setProduct(Szilvapalinka02);

        // Shotok
        PseudoFinca1.setProduct(Finca1);
        PseudoBang1.setProduct(Bang1);
        PseudoImagine1.setProduct(Imagine1);

        PseudoFinca6.setProduct(Finca6);
        PseudoBang6.setProduct(Bang6);
        PseudoImagine6.setProduct(Imagine6);
        PseudoRiffRaff6.setProduct(RiffRaff6);

        PseudoFinca12.setProduct(Finca12);
        PseudoBang12.setProduct(Bang12);
        PseudoImagine12.setProduct(Imagine12);
        PseudoRiffRaff12.setProduct(RiffRaff12);

        // Uveges uditok
        PseudoCocaCola.setProduct(CocaCola);
        PseudoCocaColaZero.setProduct(CocaColaZero);
        PseudoFantaNarancs.setProduct(FantaNarancs);
        PseudoSprite.setProduct(Sprite);
        PseudoKinleyGyomber.setProduct(KinleyGyomber);
        PseudoKinleyTonic.setProduct(KinleyTonic);
        PseudoNesteaCitrom.setProduct(NesteaCitrom);
        PseudoNesteaBarack.setProduct(NesteaBarack);

        // Kimert uditok
        PseudoCappyAlma.setProduct(CappyAlma);
        PseudoCappyNarancs.setProduct(CappyNarancs);
        PseudoCappyBarack.setProduct(CappyBarack);
        PseudoCappyAnanasz.setProduct(CappyAnanasz);

        // Limonadek
        PseudoLimonadeMalna05.setProduct(LimonadeMalna05);
        PseudoLimonadeMeggy05.setProduct(LimonadeMeggy05);
        PseudoLimonadeEperNarancs05.setProduct(LimonadeEperNarancs05);
        PseudoLimonadeCitrus05.setProduct(LimonadeCitrus05);

        PseudoLimonadeMalna10.setProduct(LimonadeMalna10);
        PseudoLimonadeMeggy10.setProduct(LimonadeMeggy10);
        PseudoLimonadeEperNarancs10.setProduct(LimonadeEperNarancs10);
        PseudoLimonadeCitrus10.setProduct(LimonadeCitrus10);

        // Asvanyviz
        PseudoNaturaquaSzensavas.setProduct(NaturaquaSzensavas);
        PseudoNaturaquaSzensavmentes.setProduct(NaturaquaSzensavmentes);
        PseudoSzoda.setProduct(Szoda);

        // Energiaitalok
        PseudoBurnOriginal.setProduct(BurnOriginal);
        PseudoBurnZero.setProduct(BurnZero);
        PseudoMonsterEnergy.setProduct(MonsterEnergy);
        PseudoMonsterAssault.setProduct(MonsterAssault);
        PseudoMonsterRehab.setProduct(MonsterRehab);

        // Filteres Teaak
        PseudoDallmyrFekete.setProduct(DallmayrFekete);
        PseudoDallmyrGyumolcs.setProduct(DallmayrGyumolcs);
        PseudoDallmyrZold.setProduct(DallmayrZold);

        PseudoPiramis1.setProduct(PiramisDarjeling);
        PseudoPiramis2.setProduct(PiramisMangoMaracuja);
        PseudoPiramis3.setProduct(PiramisAnanaszPapaja);
        PseudoPiramis4.setProduct(PiramisCitrusVerbena);

        // Kavek
        PseudoEspresso.setProduct(Espresso);
        PseudoAmericano.setProduct(Americano);
        PseudoCappuccino.setProduct(Cappuccino);
        PseudoCaffeLatte.setProduct(CaffeLatte);
        PseudoLatteMacchiato.setProduct(LatteMacchiato);
        PseudoCaffeMelange.setProduct(CaffeMelange);
        PseudoForroCsokiBarna.setProduct(ForroCsokiBarna);
        PseudoForroCsokiFeher.setProduct(ForroCsokiFeher);

        // Akcios italok
        PseudoGinTonic.setProduct(GinTonic);
        PseudoCaptainAndGyomber.setProduct(CaptainAndGyomber);
        PseudoVodkaSzoda.setProduct(VodkaSzoda);
        PseudoJackAndCoke.setProduct(JackAndCoke);
        
        pseudoAdHoc.setProduct(productAdHoc);
        productAdHoc.setCategory(pseudoAdHoc);

        pseudoGameFee.setProduct(productGameFee);
        productGameFee.setCategory(pseudoGameFee);

    }


    private void productsAndRecipes() {
            /* ----- ETLAP -----*/
        // Rágcsák
        SosPerec.setRecipes(Arrays.asList(SosPerecRecipe1));
        Mogyoro.setRecipes(Arrays.asList(MogyoroRecipe1));
        RagcsaMix1Perec2Mogyi.setRecipes(Arrays.asList(RagcsaMix1Perec2MogyiRecipe1, RagcsaMix1Perec2MogyiRecipe2));
        RagcsaMix2Perec1Mogyi.setRecipes(Arrays.asList(RagcsaMix2Perec1MogyiRecipe1, RagcsaMix2Perec1MogyiRecipe2));
        RagcsaMix3Perec.setRecipes(Arrays.asList(RagcsaMix3PerecRecipe1));
        NachosSosSajt.setRecipes(Arrays.asList(NachosSosSajtRecipe1, NachosSosSajtRecipe2));
        NachosSosChili.setRecipes(Arrays.asList(NachosSosChiliRecipe1, NachosSosChiliRecipe2));
        NachosBBQSajt.setRecipes(Arrays.asList(NachosBBQSajtRecipe1, NachosBBQSajtRecipe2));
        NachosBBQChili.setRecipes(Arrays.asList(NachosBBQChiliRecipe1, NachosBBQChiliRecipe2));
        Chips.setRecipes(Arrays.asList(ChipsRecipe1));
        Popcorn.setRecipes(Arrays.asList(PopcornRecipe1));
        Gumicukor.setRecipes(Arrays.asList(GumicukorRecipe1));
        Balatonszelet.setRecipes(Arrays.asList(BalatonszeletRecipe1));
        Csoki.setRecipes(Arrays.asList(CsokiRecipe1));

        // Ételek
        MelegszendivcsSonkas.setRecipes(Arrays.asList(
                MelegszendivcsSonkasRecipe1,
                MelegszendivcsSonkasRecipe2,
                MelegszendivcsSonkasRecipe3,
                MelegszendivcsSonkasRecipe4,
                MelegszendivcsSonkasRecipe5,
                MelegszendivcsSonkasRecipe6));
        MelegszendivcsSzalamis.setRecipes(Arrays.asList(
                MelegszendivcsSzalamisRecipe1,
                MelegszendivcsSzalamisRecipe2,
                MelegszendivcsSzalamisRecipe3,
                MelegszendivcsSzalamisRecipe4,
                MelegszendivcsSzalamisRecipe5,
                MelegszendivcsSzalamisRecipe6));
        MelegszendivcsVega.setRecipes(Arrays.asList(
                MelegszendivcsVegaRecipe1,
                MelegszendivcsVegaRecipe2,
                MelegszendivcsVegaRecipe3,
                MelegszendivcsVegaRecipe4,
                MelegszendivcsVegaRecipe5,
                MelegszendivcsVegaRecipe6));
        SajtosCsikok.setRecipes(Arrays.asList(
                SajtosCsikokRecipe1,
                SajtosCsikokRecipe2,
                SajtosCsikokRecipe3,
                SajtosCsikokRecipe4,
                SajtosCsikokRecipe5));
        ZsirosDeszka.setRecipes(Arrays.asList(
                ZsirosDeszkaRecipe1,
                ZsirosDeszkaRecipe2,
                ZsirosDeszkaRecipe3,
                ZsirosDeszkaRecipe4));
        Wrap.setRecipes(Arrays.asList(
                WrapRecipe1,
                WrapRecipe2,
                WrapRecipe3,
                WrapRecipe4,
                WrapRecipe5,
                WrapRecipe6,
                WrapRecipe7,
                WrapRecipe8));
        SpecialisFeltetekPiritossalTonhal.setRecipes(Arrays.asList(
                SpecialisFeltetekPiritossalTonhalRecipe1,
                SpecialisFeltetekPiritossalTonhalRecipe2,
                SpecialisFeltetekPiritossalTonhalRecipe3,
                SpecialisFeltetekPiritossalTonhalRecipe4));
        SpecialisFeltetekPiritossalPadlizsan.setRecipes(Arrays.asList(
                SpecialisFeltetekPiritossalPadlizsanRecipe1,
                SpecialisFeltetekPiritossalPadlizsanRecipe2,
                SpecialisFeltetekPiritossalPadlizsanRecipe3));

        Sajttal.setRecipes(Arrays.asList(
                SajttalRecipe1,
                SajttalRecipe2,
                SajttalRecipe3,
                SajttalRecipe4));
        GameUpTal.setRecipes(Arrays.asList(
                GameUpTalRecipe1,
                GameUpTalRecipe2,
                GameUpTalRecipe3,
                GameUpTalRecipe4,
                GameUpTalRecipe5,
                GameUpTalRecipe6));
        GameUpFalankTal.setRecipes(Arrays.asList(
                GameUpFalankTalRecipe1,
                GameUpFalankTalRecipe2,
                GameUpFalankTalRecipe3,
                GameUpFalankTalRecipe4,
                GameUpFalankTalRecipe5,
                GameUpFalankTalRecipe6,
                GameUpFalankTalRecipe7,
                GameUpFalankTalRecipe8,
                GameUpFalankTalRecipe9));
        AgentShotCover.setRecipes(Arrays.asList(AgentShotCoverRecipe1));
        LimonCept.setRecipes(Arrays.asList(LimonCeptRecipe1));
        SplendBor.setRecipes(Arrays.asList(SplendBorRecipe1));
        TatraTime.setRecipes(Arrays.asList(TatraTimeRecipe1));
        SorrelAzEmberisegEllen.setRecipes(Arrays.asList(SorrelAzEmberisegEllenRecipe1));
        
        // Csapolt Sorok
        Soproni03.setRecipes(Arrays.asList(Soproni03Recipe1));
        Soproni05.setRecipes(Arrays.asList(Soproni05Recipe1));
        Edelweiss03.setRecipes(Arrays.asList(Edelweiss03Recipe1));
        Edelweiss05.setRecipes(Arrays.asList(Edelweiss05Recipe1));

        // Uveges sorok
        KrusoviceSvetle.setRecipes(Arrays.asList(KrusoviceSvetleRecipe1));
        SoproniDemon.setRecipes(Arrays.asList(SoproniDemonRecipe1));
        SoproniMaxx.setRecipes(Arrays.asList(SoproniMaxxRecipe1));
        Heineken.setRecipes(Arrays.asList(HeinekenRecipe1));
        GosserNaturRadler.setRecipes(Arrays.asList(GosserNaturRadlerRecipe1));
        GosserNaturRadler00.setRecipes(Arrays.asList(GosserNaturRadler00Recipe1));
        BekesSzentandrasiMeggyes.setRecipes(Arrays.asList(BekesSzentandrasiMeggyesRecipe1));
        StrongbowDarkfruit.setRecipes(Arrays.asList(StrongbowDarkfruitRecipe1));
        StrongbowGoldAppleCider.setRecipes(Arrays.asList(StrongbowGoldAppleCiderRecipe1));
        Edelweiss.setRecipes(Arrays.asList(EdelweissRecipe1));

        // Borok
        HazBoraNagyEsNagy.setRecipes(Arrays.asList(HazBoraNagyEsNagyRecipe1));
        HilltopIrsaiOliver.setRecipes(Arrays.asList(HilltopIrsaiOliverRecipe1));
        HilltopIrsaiOliverDecire.setRecipes(Arrays.asList(HilltopIrsaiOliverDecireRecipe1));
        GereAttilaOlaszrizling.setRecipes(Arrays.asList(GereAttilaOlaszrizlingRecipe1));

        HazBoraLisiczaRoseCuvee.setRecipes(Arrays.asList(HazBoraLisiczaRoseCuveeRecipe1));
        MeszarosPinotNoirRose.setRecipes(Arrays.asList(MeszarosPinotNoirRoseRecipe1));
        MeszarosPinotNoirRoseDecire.setRecipes(Arrays.asList(MeszarosPinotNoirRoseDecireRecipe1));

        HazBoraPolgarSerumVeritas.setRecipes(Arrays.asList(HazBoraPolgarSerumVeritasRecipe1));
        VinczeMerlot.setRecipes(Arrays.asList(VinczeMerlotRecipe1));
        VylyanCabernetSauvignon.setRecipes(Arrays.asList(VylyanCabernetSauvignonRecipe1));
        MeszarosHidasptereCabernetFrancReserve.setRecipes(Arrays.asList(MeszarosHidasptereCabernetFrancReserveRecipe1));

        // Pezsgok
        TorleyGalaSzaraz.setRecipes(Arrays.asList(TorleyGalaSzarazRecipe1));
        TorleyCharmantEdes.setRecipes(Arrays.asList(TorleyCharmantEdesRecipe1));

        // Fröccsok
        HazBoraNagyEsNagyKisfroccs.setRecipes(Arrays.asList(HazBoraNagyEsNagyKisfroccsRecipe1));
        HazBoraNagyEsNagyNagyfroccs.setRecipes(Arrays.asList(HazBoraNagyEsNagyNagyfroccsRecipe1));
        HazBoraNagyEsNagyVicehazmester.setRecipes(Arrays.asList(HazBoraNagyEsNagyVicehazmesterRecipe1));
        HazBoraNagyEsNagyHazmester.setRecipes(Arrays.asList(HazBoraNagyEsNagyHazmesterRecipe1));
        HazBoraNagyEsNagyHosszulepes.setRecipes(Arrays.asList(HazBoraNagyEsNagyHosszulepesRecipe1));
        HazBoraNagyEsNagySportFroccs.setRecipes(Arrays.asList(HazBoraNagyEsNagySportFroccsRecipe1));

        HilltopIrsaiOliverKisfroccs.setRecipes(Arrays.asList(HilltopIrsaiOliverKisfroccsRecipe1));
        HilltopIrsaiOliverNagyfroccs.setRecipes(Arrays.asList(HilltopIrsaiOliverNagyfroccsRecipe1));
        HilltopIrsaiOliverVicehazmester.setRecipes(Arrays.asList(HilltopIrsaiOliverVicehazmesterRecipe1));
        HilltopIrsaiOliverHazmester.setRecipes(Arrays.asList(HilltopIrsaiOliverHazmesterRecipe1));
        HilltopIrsaiOliverHosszulepes.setRecipes(Arrays.asList(HilltopIrsaiOliverHosszulepesRecipe1));
        HilltopIrsaiOliverSportFroccs.setRecipes(Arrays.asList(HilltopIrsaiOliverSportFroccsRecipe1));

        HazBoraLisiczaRoseCuveeKisfroccs.setRecipes(Arrays.asList(HazBoraLisiczaRoseCuveeKisfroccsRecipe1));
        HazBoraLisiczaRoseCuveeNagyfroccs.setRecipes(Arrays.asList(HazBoraLisiczaRoseCuveeNagyfroccsRecipe1));
        HazBoraLisiczaRoseCuveeVicehazmester.setRecipes(Arrays.asList(HazBoraLisiczaRoseCuveeVicehazmesterRecipe1));
        HazBoraLisiczaRoseCuveeHazmester.setRecipes(Arrays.asList(HazBoraLisiczaRoseCuveeHazmesterRecipe1));
        HazBoraLisiczaRoseCuveeHosszulepes.setRecipes(Arrays.asList(HazBoraLisiczaRoseCuveeHosszulepesRecipe1));
        HazBoraLisiczaRoseCuveeSportFroccs.setRecipes(Arrays.asList(HazBoraLisiczaRoseCuveeSportFroccsRecipe1));

        MeszarosPinotNoirRoseKisfroccs.setRecipes(Arrays.asList(MeszarosPinotNoirRoseKisfroccsRecipe1));
        MeszarosPinotNoirRoseNagyfroccs.setRecipes(Arrays.asList(MeszarosPinotNoirRoseNagyfroccsRecipe1));
        MeszarosPinotNoirRoseVicehazmester.setRecipes(Arrays.asList(MeszarosPinotNoirRoseVicehazmesterRecipe1));
        MeszarosPinotNoirRoseHazmester.setRecipes(Arrays.asList(MeszarosPinotNoirRoseHazmesterRecipe1));
        MeszarosPinotNoirRoseHosszulepes.setRecipes(Arrays.asList(MeszarosPinotNoirRoseHosszulepesRecipe1));
        MeszarosPinotNoirRoseSportFroccs.setRecipes(Arrays.asList(MeszarosPinotNoirRoseSportFroccsRecipe1));

        // Roviditalok
        JimBeam04.setRecipes(Arrays.asList(JimBeam04Recipe1));
        JohnnieWalkerRedLabel04.setRecipes(Arrays.asList(JohnnieWalkerRedLabel04Recipe1));
        JackDaniels04.setRecipes(Arrays.asList(JackDaniels04Recipe1));
        TullamoreDew04.setRecipes(Arrays.asList(TullamoreDew04Recipe1));

        Royal04.setRecipes(Arrays.asList(Royal04Recipe1));
        Finlandia04.setRecipes(Arrays.asList(Finlandia04Recipe1));

        BacardiSuperior04.setRecipes(Arrays.asList(BacardiSuperior04Recipe1));
        CaptainMorganSpicedRum04.setRecipes(Arrays.asList(CaptainMorganSpicedRum04Recipe1));

        Beefeater04.setRecipes(Arrays.asList(Beefeater04Recipe1));

        TequilaSierraReposadoGold04.setRecipes(Arrays.asList(TequilaSierraReposadoGold04Recipe1));
        TequilaSierraSilver04.setRecipes(Arrays.asList(TequilaSierraSilver04Recipe1));

        Unicum04.setRecipes(Arrays.asList(Unicum04Recipe1));
        Jagermeister04.setRecipes(Arrays.asList(Jagermeister04Recipe1));
        Baileys08.setRecipes(Arrays.asList(Baileys08Recipe1));

        JimBeam02.setRecipes(Arrays.asList(JimBeam02Recipe1));
        JohnnieWalkerRedLabel02.setRecipes(Arrays.asList(JohnnieWalkerRedLabel02Recipe1));
        JackDaniels02.setRecipes(Arrays.asList(JackDaniels02Recipe1));
        TullamoreDew02.setRecipes(Arrays.asList(TullamoreDew02Recipe1));

        Royal02.setRecipes(Arrays.asList(Royal02Recipe1));
        Finlandia02.setRecipes(Arrays.asList(Finlandia02Recipe1));

        BacardiSuperior02.setRecipes(Arrays.asList(BacardiSuperior02Recipe1));
        CaptainMorganSpicedRum02.setRecipes(Arrays.asList(CaptainMorganSpicedRum02Recipe1));

        Beefeater02.setRecipes(Arrays.asList(Beefeater02Recipe1));

        TequilaSierraReposadoGold02.setRecipes(Arrays.asList(TequilaSierraReposadoGold02Recipe1));
        TequilaSierraSilver02.setRecipes(Arrays.asList(TequilaSierraSilver02Recipe1));

        Unicum02.setRecipes(Arrays.asList(Unicum02Recipe1));
        Jagermeister02.setRecipes(Arrays.asList(Jagermeister02Recipe1));
        Baileys04.setRecipes(Arrays.asList(Baileys04Recipe1));

        // Palinkak/
        _22KokuszTatratea04.setRecipes(Arrays.asList(_22KokuszTatratea04Recipe1));
        _32CitrusTatratea04.setRecipes(Arrays.asList(_32CitrusTatratea04Recipe1));
        _42BarackTatratea04.setRecipes(Arrays.asList(_42BarackTatratea04Recipe1));
        _52EredetiTatratea04.setRecipes(Arrays.asList(_52EredetiTatratea04Recipe1));
        _62ErdeiGyumolcsTatratea04.setRecipes(Arrays.asList(_62ErdeiGyumolcsTatratea04Recipe1));
        _72OutlawBetyarTatratea04.setRecipes(Arrays.asList(_72OutlawBetyarTatratea04Recipe1));

        CseresznyePalinka04.setRecipes(Arrays.asList(CseresznyePalinka04Recipe1));
        KajszibarackPalinka04.setRecipes(Arrays.asList(KajszibarackPalinka04Recipe1));
        Szilvapalinka04.setRecipes(Arrays.asList(Szilvapalinka04Recipe1));

        _22KokuszTatratea02.setRecipes(Arrays.asList(_22KokuszTatratea02Recipe1));
        _32CitrusTatratea02.setRecipes(Arrays.asList(_32CitrusTatratea02Recipe1));
        _42BarackTatratea02.setRecipes(Arrays.asList(_42BarackTatratea02Recipe1));
        _52EredetiTatratea02.setRecipes(Arrays.asList(_52EredetiTatratea02Recipe1));
        _62ErdeiGyumolcsTatratea02.setRecipes(Arrays.asList(_62ErdeiGyumolcsTatratea02Recipe1));
        _72OutlawBetyarTatratea02.setRecipes(Arrays.asList(_72OutlawBetyarTatratea02Recipe1));

        CseresznyePalinka02.setRecipes(Arrays.asList(CseresznyePalinka02Recipe1));
        KajszibarackPalinka02.setRecipes(Arrays.asList(KajszibarackPalinka02Recipe1));
        Szilvapalinka02.setRecipes(Arrays.asList(Szilvapalinka02Recipe1));

        // Shotok
        Finca1.setRecipes(Arrays.asList(
                Finca1Recipe1,
                Finca1Recipe2,
                Finca1Recipe3));
        Bang1.setRecipes(Arrays.asList(
                Bang1Recipe1,
                Bang1Recipe2));
        Imagine1.setRecipes(Arrays.asList(
                Imagine1Recipe1,
                Imagine1Recipe2));

        Finca6.setRecipes(Arrays.asList(
                Finca6Recipe1,
                Finca6Recipe2,
                Finca6Recipe3));
        Bang6.setRecipes(Arrays.asList(
                Bang6Recipe1,
                Bang6Recipe2));
        Imagine6.setRecipes(Arrays.asList(
                Imagine6Recipe1,
                Imagine6Recipe2));
        RiffRaff6.setRecipes(Arrays.asList(
                RiffRaff6Recipe1,
                RiffRaff6Recipe2,
                RiffRaff6Recipe3));

        Finca12.setRecipes(Arrays.asList(
                Finca12Recipe1,
                Finca12Recipe2,
                Finca12Recipe3));
        Bang12.setRecipes(Arrays.asList(
                Bang12Recipe1,
                Bang12Recipe2));
        Imagine12.setRecipes(Arrays.asList(
                Imagine12Recipe1,
                Imagine12Recipe2));
        RiffRaff12.setRecipes(Arrays.asList(
                RiffRaff12Recipe1,
                RiffRaff12Recipe2,
                RiffRaff12Recipe3));

        // Uveges uditok
        CocaCola.setRecipes(Arrays.asList(CocaColaRecipe1));
        CocaColaZero.setRecipes(Arrays.asList(CocaColaZeroRecipe1));
        FantaNarancs.setRecipes(Arrays.asList(FantaNarancsRecipe1));
        Sprite.setRecipes(Arrays.asList(SpriteRecipe1));
        KinleyGyomber.setRecipes(Arrays.asList(KinleyGyomberRecipe1));
        KinleyTonic.setRecipes(Arrays.asList(KinleyTonicRecipe1));
        NesteaCitrom.setRecipes(Arrays.asList(NesteaCitromRecipe1));
        NesteaBarack.setRecipes(Arrays.asList(NesteaBarackRecipe1));

        // Kimert uditok
        CappyAlma.setRecipes(Arrays.asList(CappyAlmaRecipe1));
        CappyNarancs.setRecipes(Arrays.asList(CappyNarancsRecipe1));
        CappyBarack.setRecipes(Arrays.asList(CappyBarackRecipe1));
        CappyAnanasz.setRecipes(Arrays.asList(CappyAnanaszRecipe1));

        // Limonadek
        LimonadeMalna05.setRecipes(Arrays.asList(
                LimonadeMalna05Recipe1,
                LimonadeMalna05Recipe2,
                LimonadeMalna05Recipe3,
                LimonadeMalna05Recipe4));
        LimonadeMeggy05.setRecipes(Arrays.asList(
                LimonadeMeggy05Recipe1,
                LimonadeMeggy05Recipe2,
                LimonadeMeggy05Recipe3,
                LimonadeMeggy05Recipe4));
        LimonadeEperNarancs05.setRecipes(Arrays.asList(
                LimonadeEperNarancs05Recipe1,
                LimonadeEperNarancs05Recipe2,
                LimonadeEperNarancs05Recipe3,
                LimonadeEperNarancs05Recipe4));
        LimonadeCitrus05.setRecipes(Arrays.asList(
                LimonadeCitrus05Recipe1,
                LimonadeCitrus05Recipe2,
                LimonadeCitrus05Recipe3,
                LimonadeCitrus05Recipe4));
        LimonadeMalna10.setRecipes(Arrays.asList(
                LimonadeMalna10Recipe1,
                LimonadeMalna10Recipe2,
                LimonadeMalna10Recipe3,
                LimonadeMalna10Recipe4));
        LimonadeMeggy10.setRecipes(Arrays.asList(
                LimonadeMeggy10Recipe1,
                LimonadeMeggy10Recipe2,
                LimonadeMeggy10Recipe3,
                LimonadeMeggy10Recipe4));
        LimonadeEperNarancs10.setRecipes(Arrays.asList(
                LimonadeEperNarancs10Recipe1,
                LimonadeEperNarancs10Recipe2,
                LimonadeEperNarancs10Recipe3,
                LimonadeEperNarancs10Recipe4));
        LimonadeCitrus10.setRecipes(Arrays.asList(
                LimonadeCitrus10Recipe1,
                LimonadeCitrus10Recipe2,
                LimonadeCitrus10Recipe3,
                LimonadeCitrus10Recipe4));

        // Asvanyviz
        NaturaquaSzensavas.setRecipes(Arrays.asList(NaturaquaSzensavasRecipe1));
        NaturaquaSzensavmentes.setRecipes(Arrays.asList(NaturaquaSzensavmentesRecipe1));
        Szoda.setRecipes(Arrays.asList(SzodaRecipe1));

        // Energiaitalok
        BurnOriginal.setRecipes(Arrays.asList(BurnOriginalRecipe1));
        BurnZero.setRecipes(Arrays.asList(BurnZeroRecipe1));
        MonsterEnergy.setRecipes(Arrays.asList(MonsterEnergyRecipe1));
        MonsterAssault.setRecipes(Arrays.asList(MonsterAssaultRecipe1));
        MonsterRehab.setRecipes(Arrays.asList(MonsterRehabRecipe1));

        // Filteres Teaak
        DallmayrFekete.setRecipes(Arrays.asList(DallmayrFeketeRecipe1));
        DallmayrGyumolcs.setRecipes(Arrays.asList(DallmayrGyumolcsRecipe1));
        DallmayrZold.setRecipes(Arrays.asList(DallmayrZoldRecipe1));

        PiramisDarjeling.setRecipes(Arrays.asList(PiramisDarjelingRecipe1));
        PiramisMangoMaracuja.setRecipes(Arrays.asList(PiramisMangoMaracujaRecipe1));
        PiramisAnanaszPapaja.setRecipes(Arrays.asList(PiramisAnanaszPapajaRecipe1));
        PiramisCitrusVerbena.setRecipes(Arrays.asList(PiramisCitrusVerbenaRecipe1));

        // Kavek
        Espresso.setRecipes(Arrays.asList(EspressoRecipe1));
        Americano.setRecipes(Arrays.asList(AmericanoRecipe1));
        Cappuccino.setRecipes(Arrays.asList(CappuccinoRecipe1));
        CaffeLatte.setRecipes(Arrays.asList(CaffeLatteRecipe1));
        LatteMacchiato.setRecipes(Arrays.asList(LatteMacchiatoRecipe1));
        CaffeMelange.setRecipes(Arrays.asList(CaffeMelangeRecipe1));
        ForroCsokiBarna.setRecipes(Arrays.asList(ForroCsokiBarnaRecipe1));
        ForroCsokiFeher.setRecipes(Arrays.asList(ForroCsokiFeherRecipe1));

        // Akcios italok
        GinTonic.setRecipes(Arrays.asList(GinTonicRecipe1));
        CaptainAndGyomber.setRecipes(Arrays.asList(CaptainAndGyomberRecipe1));
        VodkaSzoda.setRecipes(Arrays.asList(VodkaSzodaRecipe1));
        JackAndCoke.setRecipes(Arrays.asList(JackAndCokeRecipe1));        
    }
    
    private void vatSerieAndVatValues() {
        vatSerie.setVat(new HashSet<>(
                Arrays.asList(vatOne, vatTwo, vatThree, vatFour, vatFive)));
        vatOne.setSerie(vatSerie);
        vatTwo.setSerie(vatSerie);
        vatThree.setSerie(vatSerie);
        vatFour.setSerie(vatSerie);
        vatFive.setSerie(vatSerie);
    }

    private void restaurantAndTables() {
        restaurant.setTables(new HashSet<>(
                Arrays.asList(table1,
                        table2,
                        table3,
                        table4,
                        table5,
                        table6,
                        table7,
                        table8,
                        table9,
                        table10,
                        table11,
                        table12,
                        table13,
                        tablePurchase, tableInventory, tableDisposal, tableOther, tableOrphanage)));

        table1.setOwner(restaurant);
        table2.setOwner(restaurant);
        table3.setOwner(restaurant);
        table4.setOwner(restaurant);
        table5.setOwner(restaurant);
        table6.setOwner(restaurant);
        table7.setOwner(restaurant);
        table8.setOwner(restaurant);
        table9.setOwner(restaurant);
        table10.setOwner(restaurant);
        table11.setOwner(restaurant);
        table12.setOwner(restaurant);
        table13.setOwner(restaurant);
        tablePurchase.setOwner(restaurant);
        tableInventory.setOwner(restaurant);
        tableDisposal.setOwner(restaurant);
        tableOther.setOwner(restaurant);
        tableOrphanage.setOwner(restaurant);
   }

    private void tablesAndReservations() {

        table1.setReservations(new HashSet<>(
                Collections.singletonList(reservationOne)));
        table2.setReservations(new HashSet<>(
                Collections.singletonList(reservationTwo)));
        reservationOne.setOwner(table1);
        reservationTwo.setOwner(table2);
    }

    private void restaurantAndDailyClosures() {
        restaurant.setDailyClosures(new ArrayList<>(
                Arrays.asList(dailyClosureOne, dailyClosureTwo)));
        dailyClosureOne.setOwner(restaurant);
        dailyClosureTwo.setOwner(restaurant);
    }
}

