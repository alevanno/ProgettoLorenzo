package it.polimi.ingsw.progettolorenzo.core;

import it.polimi.ingsw.progettolorenzo.GameTest;
import it.polimi.ingsw.progettolorenzo.core.player.PlayerIOLocal;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.*;

public class LeaderTest {
    GameTest gameTest = new GameTest();
    Deck testDeck;
    Player pl;
    Board board;
    PlayerIOLocal inputStream;
    List<LeaderCard> testList = new ArrayList<>();
    Map<String, LeaderCard> testMap = new HashMap<>();
    Map<String, Integer> famValues = new HashMap<>();

    @Before
    public void setup() throws IOException {
        gameTest.setup();
        gameTest.game.loadSettings();
        testDeck = gameTest.testDeck;
        pl = gameTest.game.getPlayers().get(0);
        gameTest.game.setCurrPlayer(pl);
        board = gameTest.game.getBoard();
        inputStream = (PlayerIOLocal) pl.getIo();
        famValues.put("Orange", 7);
        famValues.put("Black", 7);
        famValues.put("White", 7);
        pl.famMembersBirth(famValues);
    }


    @Before
    public void birth(){
        testMap = LeaderUtils.leadersBirth();
    }

    @Test
    public void size() {
        assertEquals(20, testMap.size());
    }

    @Test
    public void leaderUtilsTest(){
        String action = "y\nn\ny\n1\n";
        LeaderCard leader = testMap.get("Lorenzo Dè Medici");
        leader.setPlayer(pl);
        LeaderCard leader2 = testMap.get("Francesco Sforza");
        leader2.setPlayer(pl);
        LeaderCard leader3 = testMap.get("Filippo Brunelleschi");
        leader3.setPlayer(pl);
        LeaderCard leader4 = testMap.get("Federico Da Montefeltro");
        leader4.setPlayer(pl);
        leader3.activation = true;
        leader4.activation = true;
        Resources cost = new Resources.ResBuilder().stone(leader.activationCost.get(0)).build();
        assertFalse(LeaderUtils.checkCostResSatisfaction(pl, cost));
        assertFalse(LeaderUtils.checkMultiType(leader.types, leader.activationCost, pl));
        inputStream.setIn(action);
        assertTrue(LeaderUtils.commonApply(pl, leader2, false, true));
        // already activated
        assertFalse(LeaderUtils.commonApply(pl, leader3, false, true));
        // OnePerRound not activated
        assertFalse(LeaderUtils.commonApply(pl, leader4, false, true));
        // OnePerRound -> play!
        assertTrue(LeaderUtils.commonApply(pl, leader4, false, true));
    }
    @Test
    public void OneHarvProdTest() {
        String action = "y\ny\n1\ny\ny";
        inputStream.setIn(action);
        LeaderCard leader2 = testMap.get("Francesco Sforza");
        leader2.setPlayer(pl);
        leader2.activation = true;
        Resources tmp = new Resources.ResBuilder().build().merge(pl.getCurrentRes());
        leader2.apply();
        assertTrue(pl.getCurrentRes().servant < tmp.servant);
    }

    @Test
    public void onePerRoundTest() {
        // sforza already tested in oneHarvProd
        // Montefeltro test
        String action = "y\n1\ny\ny\ny\ny\ny\ny\n2\ny\ny\n1\ny\n1\ny\n";
        inputStream.setIn(action);
        LeaderCard montefeltro = testMap.get("Federico Da Montefeltro");
        montefeltro.setPlayer(pl);
        montefeltro.activation = true;
        montefeltro.apply();
        assertEquals(6, pl.getAvailableFamMembers().get(0).getActionValue());
        // Buonarroti test
        LeaderCard buonarroti = testMap.get("Michelangelo Buonarroti");
        buonarroti.setPlayer(pl);
        buonarroti.activation = true;
        Resources tmp = new Resources.ResBuilder().build().merge(pl.getCurrentRes());
        buonarroti.apply();
        assertTrue(pl.getCurrentRes().coin > tmp.coin);
        // BandeNere test
        LeaderCard bandeNere = testMap.get("Giovanni Dalle Bande Nere");
        bandeNere.setPlayer(pl);
        bandeNere.activation = true;
        tmp = new Resources.ResBuilder().build().merge(pl.getCurrentRes());
        bandeNere.apply();
        assertTrue(pl.getCurrentRes().coin > tmp.coin &&
        pl.getCurrentRes().wood > tmp.wood && pl.getCurrentRes().stone > tmp.stone);
        // CosimoDeMedici test
        LeaderCard cosimo = testMap.get("Cosimo Dè Medici");
        cosimo.setPlayer(pl);
        cosimo.activation = true;
        tmp = new Resources.ResBuilder().build().merge(pl.getCurrentRes());
        cosimo.apply();
        assertTrue(pl.getCurrentRes().servant > tmp.servant &&
                pl.getCurrentRes().victoryPoint > tmp.victoryPoint );
        // Colleoni test
        LeaderCard colleoni = testMap.get("Bartolomeo Colleoni");
        colleoni.setPlayer(pl);
        colleoni.activation = true;
        tmp = new Resources.ResBuilder().build().merge(pl.getCurrentRes());
        colleoni.apply();
        assertTrue(pl.getCurrentRes().victoryPoint > tmp.victoryPoint);
        // Botticelli test
        LeaderCard botticelli = testMap.get("Sandro Botticelli");
        botticelli.setPlayer(pl);
        botticelli.activation = true;
        tmp = new Resources.ResBuilder().build().merge(pl.getCurrentRes());
        botticelli.apply();
        assertTrue(pl.getCurrentRes().militaryPoint > tmp.militaryPoint &&pl.getCurrentRes().victoryPoint > tmp.victoryPoint);
        // Gonzaga test
        LeaderCard gonzaga = testMap.get("Ludovico III Gonzaga");
        gonzaga.setPlayer(pl);
        gonzaga.activation = true;
        tmp = new Resources.ResBuilder().build().merge(pl.getCurrentRes());
        // choice n 2 in privileges
        gonzaga.apply();
        assertTrue(pl.getCurrentRes().servant > tmp.servant);
        // Savonarola test
        LeaderCard savonarola = testMap.get("Girolamo Savonarola");
        savonarola.setPlayer(pl);
        savonarola.activation = true;
        tmp = new Resources.ResBuilder().build().merge(pl.getCurrentRes());
        savonarola.apply();
        assertTrue(pl.getCurrentRes().faithPoint > tmp.faithPoint);
        LeaderCard daVinci = testMap.get("Leonardo Da Vinci");
        pl.getLeaderCards().add(daVinci);
        daVinci.setPlayer(pl);
        daVinci.activation = true;
        daVinci.apply();
        assertTrue(pl.getCurrentRes().servant < tmp.servant);

    }

    @Test
    public void permanentAbilityTest() {
        pl.getLeaderCards().removeAll(pl.getLeaderCards());
        // brunelleschi test
        String action = "Avamposto Commerciale\ny\nBosco\ny\n1\ny\n1\ny\n5\n1\n";
        inputStream.setIn(action);
        LeaderCard brunelleschi = testMap.get("Filippo Brunelleschi");
        pl.getLeaderCards().add(brunelleschi);
        brunelleschi.setPlayer(pl);
        brunelleschi.activation = true;
        Resources tmp = new Resources.ResBuilder().build().merge(pl.getCurrentRes());
        brunelleschi.permanentAbility();
        brunelleschi.apply();
        FamilyMember blank = pl.getAvailableFamMembers().get(3);
        // I occupy the tower with blank fam
        blank.setActionValue(7);
        Move.floorAction(board, blank);
        // assert that i cannot have to pay additional coin
        Move.floorAction(board, pl.getAvailableFamMembers().get(0));
        assertTrue(pl.getCurrentRes().coin == tmp.coin);
        // Lucrezia Borgia test
        LeaderCard lucreziaBorgia = testMap.get("Lucrezia Borgia");
        pl.getLeaderCards().add(lucreziaBorgia);
        lucreziaBorgia.setPlayer(pl);
        lucreziaBorgia.activation = true;
        lucreziaBorgia.permanentAbility();
        lucreziaBorgia.apply();
        // from now colored family member have + 2 on their value
        // (here birth with value )
        for (FamilyMember fam : pl.getAvailableFamMembers()) {
            if (!"Blank".equals(fam.getSkinColour())) {
                assertTrue(fam.getActionValue() == 9);
            }
        }
        pl.getAvailableFamMembers().removeAll(pl.getAvailableFamMembers());
        pl.famMembersBirth(famValues);
        for (FamilyMember fam : pl.getAvailableFamMembers()) {
            if (!"Blank".equals(fam.getSkinColour())) {
                assertTrue(fam.getActionValue() == 9);
            } else {
                assertTrue(fam.getActionValue() == 0);
            }
        }
        // Ariosto test
        LeaderCard ariosto = testMap.get("Ludovico Ariosto");
        pl.getLeaderCards().add(ariosto);
        ariosto.setPlayer(pl);
        ariosto.activation = true;
        ariosto.permanentAbility();
        Move.marketAction(board, pl.getAvailableFamMembers().get(0));
        // i can place fam in occupied spaces
        assertFalse(Move.marketAction
                (board, pl.getAvailableFamMembers().get(1)));
        // SistoIV test
        LeaderCard sistoIV = testMap.get("Sisto IV");
        pl.getLeaderCards().add(sistoIV);
        sistoIV.setPlayer(pl);
        sistoIV.activation = true;
        sistoIV.apply();
        sistoIV.permanentAbility();
    }

    @Test
    public void permanentTest2() {
        String action = "Monastero\ny\nFiera\ny\nBanca\ny\ny\n1\n";
        inputStream.setIn(action);
        // Malatesta test
        LeaderCard malatesta = testMap.get("Sigismondo Malatesta");
        pl.getLeaderCards().add(malatesta);
        malatesta.setPlayer(pl);
        malatesta.activation = true;
        malatesta.apply();
        malatesta.permanentAbility();
        pl.getAvailableFamMembers().removeAll(pl.getAvailableFamMembers());
        pl.famMembersBirth(famValues);
        // Blank with +3 on its value
        assertTrue(pl.getAvailableFamMembers().get(3).getActionValue() == 3);
        LeaderCard cesareBorgia = testMap.get("Cesare Borgia");
        pl.getLeaderCards().add(cesareBorgia);
        cesareBorgia.setPlayer(pl);
        cesareBorgia.activation = true;
        cesareBorgia.apply();
        cesareBorgia.permanentAbility();
        Deck tmpDeck = new Deck();
        for (Card c : testDeck) {
            if ("Rocca".equals(c.cardName) || "Bosco".equals(c.cardName)) {
                pl.addCard(c);
            }
            if("Monastero".equals(c.cardName)) {
                tmpDeck.add(c);
                gameTest.game.setBoard(new Board(tmpDeck, gameTest.game));
            }
        }
        assertTrue(Move.floorAction(gameTest.game.getBoard(), pl.getAvailableFamMembers().get(0)));
        // Santa Rita test
        LeaderCard santaRita = testMap.get("Santa Rita");
        pl.getLeaderCards().add(santaRita);
        santaRita.setPlayer(pl);
        santaRita.activation = true;
        santaRita.apply();
        santaRita.permanentAbility();
        // Pico test
        LeaderCard pico = testMap.get("Pico Della Mirandola");
        pl.getLeaderCards().add(pico);
        pico.setPlayer(pl);
        pico.activation = true;
        pico.apply();
        pico.permanentAbility();
        tmpDeck = new Deck();
        pl.getAvailableFamMembers().removeAll(pl.getAvailableFamMembers());
        pl.famMembersBirth(famValues);
        for (Card c : testDeck) {
            if("Banca".equals(c.cardName) || "Fiera".equals(c.cardName)) {
                tmpDeck.add(c);
            }
        }
        gameTest.game.setBoard(new Board(tmpDeck, gameTest.game));
        Move.floorAction(gameTest.game.getBoard(), pl.getAvailableFamMembers().get(0));
        pl.getAvailableFamMembers().get(2).setActionValue(7);
        pl.currentResMerge(new Resources.ResBuilder().stone(2).wood(2).build());
        assertTrue(Move.floorAction(gameTest.game.getBoard(), pl.getAvailableFamMembers().get(2)));
        // Moro test
        LeaderCard moro = testMap.get("Ludovico Il Moro");
        pl.getLeaderCards().add(moro);
        moro.setPlayer(pl);
        moro.activation = true;
        moro.apply();
        moro.permanentAbility();
        assertEquals(5, pl.getAvailableFamMembers().get(0).getActionValue());
        pl.getAvailableFamMembers().removeAll(pl.getAvailableFamMembers());
        pl.famMembersBirth(famValues);
        assertEquals(5, pl.getAvailableFamMembers().get(0).getActionValue());
        // Lorenzo test
        LeaderCard lorenzo = testMap.get("Lorenzo Dè Medici");
        pl.getLeaderCards().add(lorenzo);
        lorenzo.setPlayer(pl);
        lorenzo.activation = true;
        lorenzo.apply();
        lorenzo.permanentAbility();
        assertFalse(pl.getLeaderCards().contains(lorenzo));
    }
}
