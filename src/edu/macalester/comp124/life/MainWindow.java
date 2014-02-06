package edu.macalester.comp124.life;

import sun.print.resources.serviceui_pt_BR;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.synth.SynthTextAreaUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * Main window class for running and displaying the Life simulation.
 * 
 * @author Michael Ekstrand <ekstrand@cs.umn.edu>
 * 
 * This class provides the main interface to the Life system, including its main
 * entry point and its main window.
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame
    implements ActionListener, ChangeListener {
    
    private int runDelay;

    private JButton bStep;
    private JToggleButton tbRun;
    private JToggleButton tbPartyCells;

    private JSlider speedSlider;
    private JSlider redSlider;
    private JSlider greenSlider;
    private JSlider blueSlider;
    private JLabel speedLabel;
    private JLabel redLabel;
    private JLabel greenLabel;
    private JLabel blueLabel;
    private GameBoard board;
    private LifeComponent pane;
    private Timer runTimer;
    private ButtonGroup ruleSetButtons;

    /**
     * Main entry point for the Game of Life program
     * 
     * @param args
     */
    public static void main(String[] args) {
        MainWindow frame = new MainWindow();
        frame.setVisible(true);
    }

    /**
     * Construct a new main window.
     */
    public MainWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        runDelay =250;

        // use the border layout manager
        setLayout(new BorderLayout());

        // create our drawing pane
        pane = new LifeComponent();
        add(pane, BorderLayout.CENTER);

        // set the board
        setBoard(new GameBoard(140,100));
        
        // create the toolbar
        JToolBar tb = new JToolBar();
        add(tb, BorderLayout.NORTH);

        // populate the toolbar with controls
        JButton open = new JButton("Open");
        open.setActionCommand("open");
        open.addActionListener(this);
        tb.add(open);
        JButton save = new JButton("Save");
        save.setActionCommand("save");
        save.addActionListener(this);
        tb.add(save);
        JButton bnew = new JButton("New");
        bnew.setActionCommand("new");
        bnew.addActionListener(this);
        tb.add(bnew);

        tb.add(new JToolBar.Separator());

        bStep = new JButton("Step");
        bStep.setActionCommand("step");
        bStep.addActionListener(this);
        tb.add(bStep);

        tbRun = new JToggleButton("Run");
        tbRun.addChangeListener(this);
        tb.add(tbRun);
        
        tb.add(new JToolBar.Separator());
        
        tb.add(new JLabel("Rule set:"));
        ruleSetButtons = new ButtonGroup();
        JRadioButton conway = new JRadioButton("Conway");
        conway.setActionCommand("conway");
        conway.addChangeListener(this);
        ruleSetButtons.add(conway);
        tb.add(conway);
        JRadioButton highlife = new JRadioButton("HighLife");
        highlife.setActionCommand("highlife");
        ruleSetButtons.add(highlife);
        tb.add(highlife);
        ruleSetButtons.setSelected(conway.getModel(), true);
        
        tb.add(new JToolBar.Separator());

        tbPartyCells = new JToggleButton("Party Cells");
        tbPartyCells.setSelected(false);
        tbPartyCells.setEnabled(true);
        tb.add(tbPartyCells);

        tb.add(new JToolBar.Separator());

        speedLabel = new JLabel("4");
        tb.add(speedLabel);

        speedSlider = new JSlider();
        speedSlider.setValue(4);
        speedSlider.setMinimum(1);
        speedSlider.setMaximum(10);
        speedSlider.addChangeListener(this);
        tb.add(speedSlider);

        tb.add(new JToolBar.Separator());

        JButton quit = new JButton("Quit");
        quit.setActionCommand("quit");
        quit.addActionListener(this);
        tb.add(quit);


        JToolBar tb2 = new JToolBar();
        redSlider = new JSlider(0,255,255);
        redSlider.addChangeListener(this);
        greenSlider = new JSlider(0,255,255);
        greenSlider.addChangeListener(this);
        blueSlider = new JSlider(0,255,255);
        blueSlider.addChangeListener(this);
        redLabel = new JLabel();
        greenLabel = new JLabel();
        blueLabel = new JLabel();
        updateColor(true);

        tb2.add(redLabel);
        tb2.add(redSlider);
        tb2.add(greenLabel);
        tb2.add(greenSlider);
        tb2.add(blueLabel);
        tb2.add(blueSlider);
        this.add(tb2, BorderLayout.SOUTH);


        pack();

        System.out.println("NORTH WIDTH " + tb.getWidth());
        System.out.println("SOUTH WIDTH " + tb2.getWidth());
        // Set up the timer
        runTimer = new Timer(runDelay, new TimerTranslator(this, "step"));
    }

    /**
     * Set a game board to display and manipulate
     * @param b The new game board to display.
     */
    private void setBoard(GameBoard b) {
        board = b;
        pane.setBoard(b);
        setTitle(String.format("Game of Life - %dx%d",
                b.getWidth(), b.getHeight()));
    }

    /**
     * Handle a control action
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("open")) {
            openFile();
        } else if (cmd.equals("save")) {
            saveFile();
        } else if (cmd.equals("new")) {
            newBoard();
        } else if (cmd.equals("step")) {
            if(tbPartyCells.isSelected())
                randomColor();
            board.next();
            pane.repaint();
        } else if (cmd.equals("quit")) {
            System.exit(0);
        } else {
            System.err.println(
                    String.format("Unknown command '%s'", cmd));
        }
    }


    /**
     * Deal with the Run button being toggled.
     */
    private void onRunToggled() {
        if (tbRun.isSelected()) {
            bStep.setEnabled(false);
            runTimer.start();
        } else {
            bStep.setEnabled(true);
            runTimer.stop();
        }
    }
    
    /**
     * Open a file.
     */
    private void openFile() {
        JFileChooser c = new JFileChooser();
        if (c.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = c.getSelectedFile();
            try {
                GameBoard b = new GameBoard(new Conway(), f);
                setBoard(b);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        String.format("An error occured reading %s:\n%s",
                            f, e.getMessage()),
                        "Error reading file",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }
    
    /**
     * Save a file.
     */
    private void saveFile() {
        JFileChooser c = new JFileChooser();
        if (c.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = c.getSelectedFile();
            try {
                board.save(f);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        String.format("An error occured saving to %s:\n%s",
                            f, e.getMessage()),
                        "Error saving file",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }
    
    /**
     * Prompt the user for a size and create a new game board.
     */
    private void newBoard() {
        JSpinner width = new JSpinner(new SpinnerNumberModel(100, 10, 500, 1));
        JSpinner height = new JSpinner(new SpinnerNumberModel(100, 10, 500, 1));
        int result = JOptionPane.showOptionDialog(this,
                new Object[] {"Enter dimentions for new board:", width, height},
                "New Board", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, null, null);
        if (result == JOptionPane.OK_OPTION) {
            // lock in spinner values
            try {
                width.commitEdit();
            } catch (ParseException e) {
                // no-op
            }
            try {
                height.commitEdit();
            } catch (ParseException e) {
                // no-op
            }
            
            // create the board
            Integer w = (Integer) width.getValue();
            Integer h = (Integer) height.getValue();
            GameBoard b = new GameBoard(w.intValue(), h.intValue());
            setBoard(b);
        }
    }

    /**
     * Translate Timer ActionEvents into action events for another listener.
     * @author Michael Ekstrand <ekstrand@cs.umn.edu>
     *
     * In Java 6, the Timer supports action commands.  This is not, however, the
     * case in Java 5.  Therefore, we need this adaptor class to translate timer
     * events into events with commands.
     */
    private class TimerTranslator implements ActionListener {
        private ActionListener delegate;
        private String command;
        
        public TimerTranslator(ActionListener l, String cmd) {
            delegate = l;
            command = cmd;
        }
        
        public void actionPerformed(ActionEvent e) {
            ActionEvent e2 = new ActionEvent(e.getSource(), e.getID(), command);
            delegate.actionPerformed(e2);
        }
    }
    


    /**
     * Propagate state change events
     */
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if (source == tbRun)
            onRunToggled();
        else if(source == speedSlider)
            updateSpeed();
        else if(source == redSlider || source == greenSlider || source == blueSlider)
            updateColor(true);
        else {
            ButtonModel b = ruleSetButtons.getSelection();
            if (b.getActionCommand().equals("conway")) {
                board.setRuleSet(new Conway());
            } else {
                // Uncomment for HighLife
                 board.setRuleSet(new HighLife());
            }
        }
    }

    public void updateSpeed()
    {
        int value = speedSlider.getValue();
        int runDelay = (int)(1.0/value*1000.0);
        if(runTimer.isRunning())
        {
            runTimer.stop();
            runTimer.setDelay(runDelay);
            runTimer.start();
        }
        else
            runTimer.setDelay(runDelay);
        speedLabel.setText(""+value);
    }

    public void randomColor()
    {
        int red = (int)(Math.random()*255);
        int green = (int)(Math.random()*255);
        int blue = (int)(Math.random()*255);
        redSlider.setValue(red);
        greenSlider.setValue(green);
        blueSlider.setValue(blue);
        updateColor(false);
    }

    public void updateColor(boolean shouldRepaint)
    {
        int red = redSlider.getValue();
        int green = greenSlider.getValue();
        int blue = blueSlider.getValue();
       // aliveColor = new Color(red,green,blue);
        redLabel.setText("Red: " + red);
        greenLabel.setText("Green: " + green);
        blueLabel.setText("Blue: " + blue);
        board.getRuleSet().setAliveColor(new Color(red,green,blue));
        if(shouldRepaint)
            pane.repaint();
    }
}
