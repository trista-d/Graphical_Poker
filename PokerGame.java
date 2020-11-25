/***************************************************************************************
 * Name:        PokerGame
 * Author:      Trista
 * Date:        May 7, 2019
 * Purpose:     A poker game where you try to beat opponents with a higher value hand.
 ****************************************************************************************/


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Arrays;


public class PokerGame extends JPanel {
    static Image[] pics = new Image[52];  // array of dancing gifs
    static Image welcomeBackground;
    static Image bgImage1;              // image displayed while play occurs for part 1
    static Image bgImage2;              // image displayed while play occurs for part 2
    static JPanel panel;                 // main drawing panel
    static JFrame frame;                 // window frame which contains the panel
    static final int WINDOW_WIDTH = 1200; // width of display window
    static final int WINDOW_HEIGHT = 1000;// height of display window

    static int gameStage = 0;            // stages of game
    static final int WELCOME_SCREEN = 0;
    static final int MENU = 1;
    static final int INSTRUCTIONS = 2;
    static final int PLAY = 3;
    static final int NEXT = 4;
    static final int END_GAME = 6;
    static final int SCORING = 7;

    static int numPlayers = 0;              // number of players
    static String instructionsText = "";    // instructions
    static int currPlayer = 0;
    static int[] cards = new int[52];
    static boolean[] check = new boolean[52];
    static String[] playerNames = {"Computer 3", "Computer 2", "Computer 1"};
    static int[][] playerHands = new int[3][5];
    static int[][] cardValues = new int[5][2];
    static int highCardIndex = 0;
    static int[] rank = new int[3];
    static boolean[] selected = new boolean[5];

    // start main program
    // * initializes the window for the game
    public static void main(String args[]) {
        // Create Image Object
        Toolkit tk = Toolkit.getDefaultToolkit();

        // Load background images
        URL url = PokerGame.class.getResource("images/table.jpg");
        welcomeBackground = tk.getImage(url);
        url = PokerGame.class.getResource("images/menu.jpg");
        bgImage1 = tk.getImage(url);
        url = PokerGame.class.getResource("images/wood.jpg");
        bgImage2 = tk.getImage(url);

        // load deck of cards images
        for (int i = 0; i < pics.length; i++) {
            url = PokerGame.class.getResource("images/" + i + ".png");
            pics[i] = tk.getImage(url);
        } // for

        // Create Frame and Panel to display graphics in
        panel = new PokerGame(); /*****MUST CALL THIS CLASS (ie same as filename) ****/

        panel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));  // set size of application window
        frame = new JFrame("Poker");  // set title of window
        frame.add(panel);
        frame.setResizable(false);

        // add a key input listener (defined below) to our canvas so we can respond to key pressed
        frame.addKeyListener(new KeyInputHandler());

        // exits window if close button pressed
        frame.addWindowListener(new ExitListener());

        // request the focus so key events come to the frame
        frame.requestFocus();
        frame.pack();
        frame.setVisible(true);

    } // main

    /*
     * paintComponent gets called whenever panel.repaint() is
     * called or when frame.pack()/frame.show() is called. It paints
     * to the screen.  Since we want to paint different things
     * depending on what stage of the game we are in, a variable
     * "gameStage" will keep track of this.
     */
    public void paintComponent(Graphics g) {
        boolean humanTurn = false;
        String[] scores = new String[playerHands.length];
        String[] highCards = new String[playerHands.length];
        String[] results = new String[playerHands.length];
        Color[] colors = new Color[3];

        super.paintComponent(g);   // calls the paintComponent method of JPanel to display the background

        // display welcome screen
        if (gameStage == WELCOME_SCREEN) {
            g.drawImage(welcomeBackground, -250, 0, this);

            g.setColor(Color.black);
            g.setFont(new Font("Monospaced", Font.BOLD, 20));   // set font
            g.drawString("Welcome to ", 590, 440);
            g.drawString("Press any key to continue.", 510, 550);

            g.setColor(Color.white);
            g.setFont(new Font("SansSerif", Font.BOLD, 36));   // set font
            g.drawString("Sara & Trista's Lowball Poker!", 400, (WINDOW_HEIGHT / 2));  // display


            // display menu
        } else if (gameStage == MENU) {
            g.drawImage(bgImage1, 0, 0, this);
            g.setColor(Color.white);
            g.setFont(new Font("SansSerif", Font.BOLD, 36));   // set font
            drawString(g, "Lowball Poker", 150, 180);  // display
            g.setFont(new Font("Monospaced", Font.BOLD, 20));   // set font
            instructionsText = "Please choose from the following options:\n\n1) Read Instructions.\n\n2) One Player Game.\n\n3) Two Player Game.\n\n4) Three Player Game.\n\n5) End game.";
            drawString(g, instructionsText, 150, 280);  // display

            // display instructions
        } else if (gameStage == INSTRUCTIONS) {
            g.drawImage(bgImage1, 0, 0, this);
            g.setColor(new Color(100, 5, 5, 128));
            g.fillRect(70, 70, 1050, 800);
            g.setColor(Color.white);
            g.setFont(new Font("SansSerif", Font.BOLD, 36));   // set font
            drawString(g, "Instructions", 150, 100);    // display title
            g.setFont(new Font("Monospaced", Font.PLAIN, 18));   // set font

            instructionsText = "This type of poker is lowball, which means the lowest value hand wins. At all\ntimes there will be three players; one to three humans and however many computers\nare needed. Everyone will start with five random cards, and will have a chance to\nswitch any of their cards in order to get a better hand. Please note that you are\nnot required to switch cards. Press 'enter' once you are done switching cards, and\nafter each player has had their turn the outcome of the game will be revealed. In\nthe case of a tie, the player with the highest card wins. If two or more players\nhave the same high card then the next highest will be used, and so on.\n\nPress any key to continue. ";
            drawString(g, instructionsText, 150, 200);  // display instructions
        } else if (gameStage == SCORING) {
            g.drawImage(bgImage1, 0, 0, this);
            g.setColor(new Color(100, 5, 5, 128));
            g.fillRect(70, 70, 1050, 800);
            g.setColor(Color.white);
            g.setFont(new Font("SansSerif", Font.BOLD, 36));   // set font
            drawString(g, "Scoring", 150, 100);    // display title
            g.setFont(new Font("Monospaced", Font.PLAIN, 18));   // set font

            instructionsText = "This is lowball poker. The ranking of hands is inverted (the lowest type of hand wins).\n\n"
                    + "The rankings in order are as follows:\n\n"
                    + "1. High Card - No matches, the highest card in the hand.\n"
                    + "2. Pair - Two cards of the same value.\n"
                    + "3. Two Pair - Two different pairs.\n"
                    + "4. Three of a Kind - Three cards of the same value.\n"
                    + "5. Straight - Five cards in a sequence, but not of the same suit.\n"
                    + "6. Flush - Five cards of the same suit, but not in a sequence.\n"
                    + "7. Full House - Three of a kind and a pair.\n"
                    + "8. Four of a Kind - Four cards of the same value.\n"
                    + "9. Straight Flush - Five cards in a sequence with the same suit.\n"
                    + "10. Royal Flush - Ace, King, Queen, Jack, and Ten of the same suit.";
            drawString(g, instructionsText, 150, 200);

        // display game play
        } else if (gameStage == PLAY) {
            g.drawImage(bgImage2, 0, 0, this);


            // set font and colour
            g.setColor(Color.white);
            g.setFont(new Font("SanSerif", Font.BOLD, 36));
            g.drawString(playerNames[currPlayer] + "'s Turn", 300, 100);

            g.setFont(new Font("Monospaced", Font.BOLD, 16));

            // determine if player is human or computer
            humanTurn = currPlayer < numPlayers ? true : false;

            // show current player's cards
            for (int j = 0; j < playerHands[currPlayer].length; j++) {
                g.drawImage(pics[playerHands[currPlayer][j]], (j * 125) + 300, 180, this);  // display the image
                if (selected[j] == true) {
                    g.setColor(new Color(200, 5, 5));
                } else {
                    g.setColor(Color.white);
                } // else
                if (humanTurn == true) {
                    g.setFont(new Font("Monospaced", Font.BOLD, 24));
                    drawString(g, String.valueOf(j + 1), (j * 125) + 347, 355); // display a 'selection' number
                } // if
            } // for

            g.setColor(Color.white);
            if (humanTurn == false) {
                g.setFont(new Font("Monospaced", Font.BOLD, 20));
                drawString(g, "Press any key to see the computer's turn", 300, 400);
            } else {
                g.setFont(new Font("Monospaced", Font.BOLD, 20));
                drawString(g, "Type the number under a card to select or deselect it. Press ", 300, 400);
                drawString(g, "Enter to change the selected cards and end your turn.", 300, 420);
            } // else
        } else if (gameStage == NEXT) {

            // show player's cards, with a message to continue
            g.drawImage(bgImage2, 0, 0, this);

            for (int j = 0; j < playerHands[currPlayer].length; j++) {
                g.drawImage(pics[playerHands[currPlayer][j]], (j * 125) + 300, 180, this);
            } // for

            g.setColor(Color.white);
            g.setFont(new Font("SanSerif", Font.BOLD, 36));
            g.drawString(playerNames[currPlayer] + "'s Hand", 300, 100);

            g.setFont(new Font("Monospaced", Font.BOLD, 20));
            g.drawString("Press any key to continue", 300, 400);
        } // if

        // display end of game
        else {
            // get scores and display results
            for (int i = 0; i < playerHands.length; i++) {
                cardValues = getCardValues(playerHands[i]);
                highCardIndex = getHighCard(cardValues);
                rank[i] = rankHand(cardValues);
                scores[i] = playerNames[i] + "'s hand: " + getHandType(rank[i]);
                highCards[i] = "High card is " + getCardString(cardValues, highCardIndex);
            } // for
            getResults(rank, playerNames);
            g.drawImage(bgImage2, 0, 0, this);

            g.setColor(Color.white);
            g.setFont(new Font("SansSerif", Font.BOLD, 48));
            g.drawString("Results", 550, 105);

            g.setColor(Color.white);
            g.setFont(new Font("Monospaced", Font.BOLD, 20));
            for (int i = 0; i < playerHands.length; i++) {
                for (int j = 0; j < playerHands[i].length; j++) {
                    g.drawImage(pics[playerHands[i][j]], (j * 125) + 100, (i * 180) + 200, this);  // display the image
                    g.drawString(scores[i], 720, (i * 180) + 270);
                    g.drawString(highCards[i], 720, (i * 180) + 290);
                } // for
            } // for
            colors[0] = new Color(221, 195, 25);
            colors[1] = Color.lightGray;
            colors[2] = new Color(133, 90, 0);

            results = getResults(rank, playerNames);
            g.setFont(new Font("SansSerif", Font.BOLD, 24));
            for (int i = 0; i < results.length; i++) {
                g.setColor(colors[i]);
                g.drawString(results[i], 100, (i * 30) + 800);
            }

            g.setColor(Color.white);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.drawString("Press any key to return to the menu", 100, 930);

        } // else
    } // paintComponent

    /* A class to handle keyboard input from the user.
     * Implemented as a inner class because it is not
     * needed outside the EvenAndOdd class.
     */
    private static class KeyInputHandler extends KeyAdapter {
        public void keyTyped(KeyEvent e) {
            // quit if the user presses "escape"
            if (e.getKeyChar() == 27) {
                System.exit(0);
            } else if (gameStage == MENU) {

                // respond to menu selection
                switch (e.getKeyChar()) {
                    case 49:
                        showInstructions();
                        break;     // Key "1" pressed
                    case 50:
                        numPlayers = 1;
                        startGame();
                        break;    // Key "2" pressed
                    case 51:
                        numPlayers = 2;
                        startGame();
                        break;     // Key "3" pressed
                    case 52:
                        numPlayers = 3;
                        startGame();
                        break;     // Key "4" pressed
                    case 53:
                        System.exit(0);     // Key "5" pressed
                } // switch
            } // if

            // if Instructions were shown, show scoring next
            else if (gameStage == INSTRUCTIONS) {
                showScoring();
            } // if

            else if (gameStage == PLAY) {

                // track cards to change
                if (currPlayer < numPlayers) {
                    switch (e.getKeyChar()) {
                        case 49:
                            selected[0] = selected[0] == true ? false : true;
                            break;    // Key "1" pressed
                        case 50:
                            selected[1] = selected[1] == true ? false : true;
                            break;    // Key "2" pressed
                        case 51:
                            selected[2] = selected[2] == true ? false : true;
                            break;    // Key "3" pressed
                        case 52:
                            selected[3] = selected[3] == true ? false : true;
                            break;    // Key "4" pressed
                        case 53:
                            selected[4] = selected[4] == true ? false : true;
                            break;    // Key "5" pressed
                    } // switch

                    // change selected cards
                    if (e.getKeyChar() == Event.ENTER) {
                        for (int i = 0; i < selected.length; i++) {
                            if (selected[i] == true) {
                                assignCards(i, playerHands[currPlayer]);
                            } // if
                        } // for
                        gameStage = NEXT;
                    } // if
                } else {
                    // it's a computer player
                    computerTurn(playerHands[currPlayer]);
                    gameStage = NEXT;
                }
                panel.repaint();
            }

            // allow player to continue to next turn
            else if (gameStage == NEXT) {
                currPlayer++;
                for (int i = 0; i < selected.length; i++) {
                    selected[i] = false;
                } // for
                gameStage = currPlayer > 2 ? END_GAME : PLAY;
                panel.repaint();
            } // if

            // if all else fails, show menu
            else {
                showMenu();
            } // else
        } // keyTyped
    } // KeyInputHandler class

    /* Shuts program down when close button pressed */
    private static class ExitListener extends WindowAdapter {
        public void windowClosing(WindowEvent event) {
            System.exit(0);
        } // windowClosing
    } // ExitListener


    private static void showMenu() {
        gameStage = MENU;
        panel.repaint();
    } // showMenu

    // sets game up to display instructions
    private static void showInstructions() {
        gameStage = INSTRUCTIONS;
        panel.repaint();
    } // showInstructions

    // show the scoring info
    private static void showScoring() {
        gameStage = SCORING;
        panel.repaint();
    } // showInstructions

    // initialize the game
    private static void startGame() {
        gameStage = PLAY;

        for (int i = 0; i < numPlayers; i++) {
            String msg = "Enter your name player " + (i + 1) + ":";
            do {
                playerNames[i] = JOptionPane.showInputDialog(msg);
                if (playerNames[i] == null) {
                    System.exit(0);
                } // if
                msg = "You cannot leave this field empty. Please try again.\n" + "Enter your name player " + (i + 1) + ":";
            } while (playerNames[i].equals(""));
        } // for

        // Set up the deck and get player hands
        for (int i = 0; i < cards.length; i++) {
            cards[i] = i;
        } // for

        for (int i = 0; i < playerHands.length; i++) {
            playerHands[i] = getPlayerHands();
        } // for

        // get actual value of cards from playerHand
        for (int i = 0; i < playerHands.length; i++) {
            cardValues = getCardValues(playerHands[i]);
        } // for

        currPlayer = 0;

        panel.repaint();

    } // playGame

    //  draw multi-line Strings
    private static void drawString(Graphics g, String text, int x, int y) {

        // draws each line on a new line
        for (String line : text.split("\n")) {
            g.drawString(line, x, y += g.getFontMetrics().getHeight());
        } // for
    } // drawString

    // randomly generate hand
    public static int[] getPlayerHands() {
        int playerHand[] = new int[5];
        int temp = 0;

        for (int i = 0; i < 5; ) {
            temp = (int) (Math.random() * 51);
            if (check[temp] == true) {
                continue;
            } // if
            check[temp] = true;
            playerHand[i] = temp;
            i++;
        } // for
        return playerHand;
    } // getPlayerHands

    // randomly generate new cards for hand
    public static void assignCards(int change, int handOfCards[]) {
        int temp = 0;
        do {
            temp = (int) (Math.random() * 51);
            if (check[temp] == true) {
                continue;
            } // if
            check[temp] = true;
            handOfCards[change] = temp;
            break;
        } while (true);
    } // assignCards

    // Randomly generate index of cards to change
    public static void computerTurn(int hand[]) {
        int cardAmount = 0;
        int[] cardsToChange = new int[5];

        cardAmount = (int) (Math.random() * 5);
        for (int i = 0; i < cardAmount; i++) {
            cardsToChange[i] = (int) (Math.random() * 5);
            assignCards(cardsToChange[i], hand);
        } // for
    } // computerChangeCards

    // calculate placings
    public static String[] getResults(int[] rank, String[] names) {

        String[] results = new String[rank.length];

        // First place
        results[0] = "First place: ";
        if (rank[2] < rank[0] && rank[0] == rank[1]) {
            results[0] += names[0] + " and " + names[1];
        } else if (rank[0] < rank[2] && rank[1] == rank[2]) {
            results[0] += names[1] + " and " + names[2];
        } else if (rank[1] < rank[0] && rank[0] == rank[2]) {
            results[0] += names[0] + " and " + names[2];
        } else if (rank[0] > rank[1] && rank[0] > rank[2]) {
            results[0] += names[0];
        } else if (rank[1] > rank[2] && rank[1] > rank[0]) {
            results[0] += names[1];
        } else {
            results[0] += names[2];
        } // if

        // Second place
        results[1] = ("Second place: ");
        if (rank[2] > rank[0] && rank[0] == rank[1]) {
            results[1] += names[0] + " and " + names[1];
        } else if (rank[0] > rank[1] && rank[1] == rank[2]) {
            results[1] += names[1] + " and " + names[2];
        } else if (rank[1] > rank[0] && rank[0] == rank[2]) {
            results[1] += names[0] + " and " + names[2];
        } else if ((rank[0] > rank[1] && rank[0] < rank[2]) || (rank[0] < rank[1] && rank[0] > rank[2])) {
            results[1] += names[0];
        } else if ((rank[1] > rank[0] && rank[1] < rank[2]) || (rank[1] < rank[0] && rank[1] > rank[2])) {
            results[1] += names[1];
        } else {
            results[1] += names[2];
        } // if

        // Third place
        if (!((rank[0] == rank[1]) || (rank[1] == rank[2]) || (rank[0] == rank[2]))) {
            results[2] = "Third place: ";
            if (rank[0] < rank[1] && rank[0] < rank[2]) {
                results[2] += names[0];
            } else if (rank[1] < rank[0] && rank[1] < rank[2]) {
                results[2] += names[1];
            } else {
                results[2] += names[2];
            } // if
        } // if

        return results;
    } // getResults

    // finds a hand's highest value card
    public static int getHighCard(int[][] hand) {
        int highCardIndex = 0;
        int highCard = 0;
        for (int i = 0; i < hand.length; i++) {
            if (hand[i][0] > highCard) {
                highCardIndex = i;
                highCard = hand[i][0];
            } // if
        } // for
        return highCardIndex;
    } // getHighCard

    // gives number value to hands
    public static int rankHand(int[][] cardValues) {
        boolean check = true;
        int suit = 0;
        int[] cards = new int[cardValues.length];
        int highMatch = 0;
        int highMatch2 = 0;
        int highCard1 = 0;
        int highCard2 = 0;
        int highCard3 = 0;

        // Put into 1D array to sort
        for (int i = 0; i < cardValues.length; i++) {
            cards[i] = cardValues[i][0];
        } // for
        Arrays.sort(cards);

        // Check for royal flush. First check if the suit is the same
        suit = cardValues[0][1];
        for (int i = 1; i < cardValues.length; i++) {
            if (cardValues[i][1] != suit) {
                check = false;
                break;
            } // if
        } // for

        // Check if cards are "royal"
        if (check == true) {
            if ((cards[0] == 10) && (cards[1] == 11) && (cards[2] == 12)
                    && (cards[3] == 13) && (cards[4] == 14)) {
                return 1;
            } // if
        } // if

        // Check for straight, straight flush, or flush
        if ((cards[0] + 1 == cards[1]) && (cards[1] + 1 == cards[2])
                && (cards[2] + 1 == cards[3]) && (cards[3] + 1 == cards[4])) {
            if (check == true) {
                return cards[4];
            } else {
                // it is just a straight, so give it a "lower" rank
                return 10000 * cards[4];
            } // else
        } else {
            if (check == true) {
                // it is a flush
                return 1000 * cards[4];
            } // if
        } // if

        // Check for four of a kind
        if ((cards[1] == cards[2]) && (cards[1] == cards[3])) {
            if (cards[0] == cards[1]) {
                highMatch = 3;
                highCard1 = 4;
            } else if (cards[3] == cards[4]) {
                highMatch = 4;
                highCard1 = 0;
            } // if
        } // if
        if (highMatch != 0) {
            return 10 * cards[highMatch] + cards[highCard1];
        }

        // Check for full house
        if (((cards[0] == cards[1]) && (cards[0] == cards[2])) && (cards[4] == cards[3])) {
            highMatch = 2;
            highMatch2 = 4;
        } else if (((cards[2] == cards[3]) && (cards[2] == cards[4])) && (cards[0] == cards[1])) {
            highMatch = 4;
            highMatch2 = 1;
        } // if
        if (highMatch != 0) {
            return 100 * cards[highMatch] + cards[highMatch2];
        } // if

        // Check for three of a kind
        if ((cards[0] == cards[1]) && (cards[0] == cards[2])) {
            highMatch = 2;
            highCard1 = 4;
            highCard2 = 3;
        } else if ((cards[4] == cards[3]) && (cards[4] == cards[2])) {
            highMatch = 4;
            highCard1 = 1;
            highCard2 = 0;
        } else if ((cards[1] == cards[2]) && (cards[1] == cards[3])) {
            highMatch = 3;
            highCard1 = 4;
            highCard2 = 0;
        } // if
        if (highMatch != 0) {
            return (cards[highMatch] * 100000) + (cards[highCard1] * 10) + cards[highCard2];
        } // if

        // check for two pair
        if (cards[0] == cards[1]) {
            if (cards[2] == cards[3]) {
                highMatch = 3;
                highMatch2 = 1;
                highCard1 = 4;
            } // if
            if (cards[3] == cards[4]) {
                highMatch = 4;
                highMatch2 = 1;
                highCard1 = 2;
            } // if
        } else if ((cards[1] == cards[2]) && (cards[3] == cards[4])) {
            highMatch = 4;
            highMatch2 = 2;
            highCard1 = 0;
        } // if
        if (highMatch != 0) {
            return (cards[highMatch] * 1000000) + (cards[highMatch2] * 100) + (cards[highCard1] * 10);
        } // if

        // Check for pair
        if (cards[3] == cards[4]) {
            highMatch = 4;
            highCard1 = 2;
            highCard2 = 1;
            highCard3 = 0;
        } else if (cards[2] == cards[3]) {
            highMatch = 3;
            highCard1 = 4;
            highCard2 = 1;
            highCard3 = 0;
        } else if (cards[1] == cards[2]) {
            highMatch = 2;
            highCard1 = 4;
            highCard2 = 3;
            highCard3 = 0;
        } else if (cards[0] == cards[1]) {
            highMatch = 1;
            highCard1 = 4;
            highCard2 = 3;
            highCard3 = 2;
        } // if

        if (highMatch != 0) {
            return (cards[highMatch] * 10000000) + (cards[highCard1] * 1000) + (cards[highCard2] * 100) + cards[highCard3];
        } // if

        // high card score
        return (cards[4] * 100000000) + (cards[3] * 10000) + (cards[2] * 1000) + (cards[1] * 100) + cards[0];
    } // rankHand

    // gives hand name from number value
    public static String getHandType(int rank) {
        if (rank == 1) {
            return "Royal Flush";
        } // if
        if (rank < 15) {
            return "Straight Flush";
        } // if
        if (rank < 155) {
            return "Four of a Kind";
        } // if
        if (rank < 1500) {
            return "Full House";
        } // if
        if (rank < 15000) {
            return "Flush";
        } // if
        if (rank < 150000) {
            return "Straight";
        } // if
        if (rank < 1500000) {
            return "Three of a Kind";
        } // if
        if (rank < 15000000) {
            return "Two Pair";
        } // if
        if (rank < 150000000) {
            return "Pair";
        } // if
        return "High Card";
    } // getHandType

    // convert card number to value
    public static int[][] getCardValues(int[] hand) {
        int[][] cardValues = new int[5][2];
        int card = 0;
        String printedCard = "";

        for (int i = 0; i < hand.length; i++) {
            cardValues[i][1] = hand[i] / 13;
            card = hand[i] % 13;
            cardValues[i][0] = (card == 0) ? 14 : card + 1;
        } // for

        return cardValues;
    } // getCardValues

    // gives name of card
    public static String getCardString(int[][] cardValues, int index) {
        String printedCard = "";
        String suit = "";
        int value = 0;

        value = cardValues[index][0];
        switch (value) {
            case 14:
                printedCard = "Ace";
                break;
            case 11:
                printedCard = "Jack";
                break;
            case 12:
                printedCard = "Queen";
                break;
            case 13:
                printedCard = "King";
                break;
            default:
                printedCard = String.valueOf(value);
        } // switch

        switch (cardValues[index][1]) {
            case 0:
                suit = "Clubs";
                break;
            case 1:
                suit = "Diamonds";
                break;
            case 2:
                suit = "Hearts";
                break;
            case 3:
                suit = "Spades";
                break;
            default:
                suit = "?";
        } // switch

        return printedCard + " of " + suit;
    } // getCardString
} // PokerGame