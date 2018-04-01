package exercises.products.memory;


import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

import static java.lang.System.*;

/**
 * Memory Game
 */
public class Memory extends JPanel implements ActionListener {

    public static void main(String[] args) {
        new Memory().program();
    }

    public static final int WIDTH = 400;
    public static final int HEIGHT = 400;

    private final List<Player> players = new ArrayList<>();
    private Board board;
    private Player actual;
    private int clickCount = 0;  // Trace how many clicks for a player

    private void program() {
        Random rand = new Random();

        String input = JOptionPane.showInputDialog("Enter name of Player One");
        players.add(new Player(input, 0));
        input = JOptionPane.showInputDialog("Enter name of Player Two");
        players.add(new Player(input, 1));
        input = JOptionPane.showInputDialog("Enter bord side (4,6,8 or 10)");

        int boardSide = Integer.valueOf(input);

        if (boardSide < 4 || 10 < boardSide || (boardSide % 2 != 0)) {
            boardSide = 4;
        }
        actual = players.get(rand.nextInt(2));
        String[] fileNames = FileHandler.getFileNames();
        shuffle(fileNames);
        String[] names = selectNAndDuplicate(boardSide, fileNames);
        //out.println(names.length);
        shuffle(names);
        board = new Board(names);
        initGraphics();
        updateView();
    }

    // This is the listener method, all logic here
    // Simple update of view and next button
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton o = (JButton)e.getSource();//crate a ghost copy of the pressed button
        String name = o.getName(); //get the name of the ghost OBS! It's the buttons name not the actual card's name
        out.println(o); //testing
        out.println(name); //testing

        out.println("Event " + clickCount);  // Debug dosent work form this anymore, clickcount reused
       // clickCount++;
        out.println(e.getSource());

        if (name.equals("next")) {
            out.println("'next' button pressed");//To se if it goes thro the if (it does)
           actual=switchPlayer(actual);
            clickCount=0;
           board.resetSelected();

        } else {
            int mid = name.indexOf(':');
            //out.println(": is at index "+mid);bug testing
            int r = Integer.parseInt(name.substring(0,mid));
            int c = Integer.parseInt(name.substring(mid+1,name.length())); // substring starts before the start index, we want after hence name+1
            if(!board.isSelected(r,c) && !board.isRemoved(r,c)) {// if it has not already been selected
                board.select(r, c);// the chosen card is flipped
                clickCount++;
            }

            /*
            out.println("r is "+r);
            out.println("c is "+c);
            out.println("name "+name);
            out.println("length of name "+name.length());          //BUG testing (TD)
            out.println("player ordinal "+ actual.getOrdinal());
            */
        }

        updateView(); // TODO must be at correct location, is it now? ...
        //update seems to work as of the moment

        // TODO, game logic

        if (board.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Winner i " + getWinner(players));
        }

        if(clickCount==2 && board.sameSelected()){
            board.removeSelected();
            actual.incPoints();
            updateView();
        }

        if(clickCount==2 && !board.sameSelected()){
            board.resetSelected();
        }

        if(clickCount==2){clickCount=0;}
    }

    private void updateView() {
        // Update cards
        for (Card c : board.getCards()) {
            int row = c.getRow();
            int col = c.getCol();
            if (c.isVisible()) {
                try {
                    FileHandler.setIcon(cardButtons[row][col], board.get(row, col).getName());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "No icons found!! Can't continue ... bye, bye ...");
                    exit(1);   // exit with error status!
                }
            } else if (c.isRemoved()) {
                cardButtons[row][col].setVisible(false); // Can't click this
            } else {
                cardButtons[row][col].setIcon(null);
                cardButtons[row][col].setBackground(cardBack);
            }
        }

        // Update player points
        JLabel l = (JLabel) playerPanels[actual.getOrdinal()].getByName(KEY_POINTS);
        l.setText(String.valueOf(actual.getPoints()));
        // Update player background
        for (int i = 0; i < playerPanels.length; i++) {
            playerPanels[i].setBackground(Color.lightGray);
        }
        playerPanels[actual.getOrdinal()].setBackground(Color.YELLOW);
    }

    // ----- Utilities -------------------------

    // Create 2 * boardSide different names distributed on boardSide ^2
    String[] selectNAndDuplicate(int boardSide, String[] fileNames) {
        String[] names = new String[boardSide * boardSide];
        int j = 0;
        for (int i = 0; i < names.length; i++) { // Will duplicate all names
            names[i] = fileNames[j].replace(".gif", "");
            j = (j + 1) % (2 * boardSide);  // Select same name once again (0-7 twice)
        }
        return names;
    }

    private Player switchPlayer(Player actual) {
        return players.get((actual.getOrdinal() + 1) % players.size());
    }

    // Will not handle draw, U do (optional) ...
    private Player getWinner(List<Player> players) {
        Player player = players.get(0);
        for (Player p : players) {
            if (p.getPoints() > player.getPoints()) {
                player = p;
            }
        }
        return player;
    }

    // Fisher - Yates once again !!!
    private void shuffle(Object[] os) {
        Random rand = new Random();
        for (int i = os.length - 1; i > 0; i--) {
            int j = rand.nextInt(i);
            Object o = os[i];
            os[i] = os[j];
            os[j] = o;
        }
    }

    // ----------- GUI stuff below this ------------------------------

    private final static String KEY_POINTS = "points"; // Used with JLookupPanel (avoid misspelling)
    private final static String KEY_NAME = "name";
    private static final Color cardBack = Color.LIGHT_GRAY;

    // Components we need to access
    private JButton[][] cardButtons;  // View of Cards as buttons. Row col as for card row col
    private JLookupPanel[] playerPanels; // View of players as a panels. Index as ordinal for Player
    private JButton next;

    public void initGraphics() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        JFrame window = new JFrame();
        window.setTitle("MEMORY");
        window.getRootPane().setDoubleBuffered(true);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Create the GUI (composed of panels)
        this.setLayout(new BorderLayout());
        JPanel cardsPanel = createCardsPanel();
        JPanel playersPanel = createPlayersPanel();
        JPanel buttonsPanel = createButtonPanel();
        this.add(cardsPanel, BorderLayout.CENTER);
        this.add(playersPanel, BorderLayout.NORTH);
        this.add(buttonsPanel, BorderLayout.SOUTH);

        // Add the GUI (stack of panels) to the window
        window.add(this);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    private JPanel createCardsPanel() {
        int size = board.size();
        cardButtons = new JButton[size][size];
        JPanel pnl = new JPanel();
        pnl.setLayout(new GridLayout(size, size));
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                JButton b = new JButton();
                b.setBackground(cardBack);
                b.addActionListener(this);
                b.setName(row + ":" + col);  // Use this as lookup later, see actionPerformed
                b.setPreferredSize(new Dimension(WIDTH / size, HEIGHT / size));
                pnl.add(b);   // Add to panel
                cardButtons[row][col] = b; // Store so we can access later
            }
        }
        return pnl;
    }

    private JPanel createPlayersPanel() {
        playerPanels = new JLookupPanel[players.size()];
        JPanel pnl = new JPanel();
        pnl.setLayout(new GridLayout(0, players.size()));
        Font font = new Font("Courier New", Font.BOLD, 18);
        for (Player p : players) {
            JLookupPanel playerPanel = new JLookupPanel();
            JLabel name = new JLabel(p.getName());
            name.setName(KEY_NAME);   // Not really needed
            name.setFont(font);
            JLabel points = new JLabel("0");
            points.setName(KEY_POINTS);
            points.setFont(font);
            playerPanel.add(name);
            playerPanel.add(points);
            pnl.add(playerPanel);  // Add to panel
            playerPanels[p.getOrdinal()] = playerPanel;  // Store so we can access
        }
        return pnl;
    }

    private JPanel createButtonPanel() {
        next = new JButton("Next");
        next.setSize(25,25);
        next.addActionListener(this);
        next.setName("next"); // to locate it later
        JPanel pn1 = new JPanel();
        pn1.setLayout(new GridLayout(0,1));
        pn1.add(next,CENTER_ALIGNMENT);
        return pn1;
    }
}
