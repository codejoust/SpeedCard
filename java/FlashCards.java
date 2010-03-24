import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class FlashCardViewer {
    public ArrayList<String[]> cards;
    
    public int cardCount = 0;
    private int currentCard = 0;
    private boolean repeat = false;
    
    private int guiChangeDelay = 2500;
    private JLabel cardDef;
    private JLabel cardWord;
    
    public FlashCardViewer() {
        cards = new ArrayList<String[]>(); }
        
    public String[] getACard(){
        if (currentCard > (cardCount - 2)){
            currentCard = 1; }
        String[] card = new String[2];
        try {
            currentCard++;
            card = cards.get(currentCard);
        } catch (IndexOutOfBoundsException e){
            System.out.println("There was an error finding this card."); }
        return card; }
    
    public void getFile(String location){
        try {
            FileReader fileInput = new FileReader(location);
            BufferedReader readBuffer = new BufferedReader(fileInput);
            String line;
            ArrayList<String> lines = new ArrayList<String>();
            int count = 0;
            while ((line = readBuffer.readLine()) != null){
                 lines.add(line); }
            for (String thisline: lines){
                processLine(thisline); }
        } catch (Exception e){
            System.out.println(e); } }
    
    private void processLine(String line){
        if (!line.startsWith("#") && line.length() > 4){
            int split = line.indexOf(",");
            if (split > 1){
                addCard(line.substring(0, split), 
                        line.substring(split + 1, line.length()));
            } else { return; } } }
    
    public void shuffle(){
        Collections.shuffle(cards); }

    private void addCard(String cardname, String carddefinition){
        cardCount++;
        String[] card = new String[2];
        card[0] = cardname;
        card[1] = carddefinition.trim();
        cards.add(card); }
    
    public void showGui(){
        String[] card = getACard();
        frame = new JFrame("SpeedCard!");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        contentPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        Font labelFont = new Font("georgia", Font.PLAIN, 40);
        cardWord = new JLabel(card[0]);
        cardWord.setFont(labelFont);
        cardWord.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardDef = new JLabel(card[1]);
        cardDef.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardDef.setFont(labelFont);
        contentPane.add(Box.createRigidArea(new Dimension(5,12)));
        contentPane.add(cardWord);
        contentPane.add(Box.createRigidArea(new Dimension(5,12)));
        contentPane.add(cardDef);
        frame.setContentPane(contentPane);
        frame.pack();
        frame.setVisible(true);
        setTimer(); }
    
    public void setTimer(){
        Timer timer = new Timer(2500, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                updateCard(); }
            });
        timer.start(); }
     
    public void updateCard(){
        String[] card = getACard();
        cardWord.setText(card[0]);
        cardDef.setText(card[1]); }
    
    public String filePicker(){
        JFrame parentFrame = new JFrame("Choose a File");
        JFileChooser filePicker = new JFileChooser();
        filePicker.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int fileChooserRet = filePicker.showDialog(frame, "Pick a Wordlist");
        if (fileChooserRet == JFileChooser.APPROVE_OPTION){
            return filePicker.getSelectedFile().getName();
        } else {
            System.out.println("No File Selected or Specified.");
            System.exit(0); }
        return "";
    }
    
    public static void main(String[] args){
        FlashCardViewer flashcard = new FlashCardViewer();
        try {
            String fileloc;
            if(args.length > 1){
                fileloc = args[1];
            } else {
                fileloc = flashcard.filePicker(); }
            flashcard.getFile(fileloc);
            if (flashcard.cardCount != 0){
                flashcard.showGui(); }
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage()); }
    }
}
