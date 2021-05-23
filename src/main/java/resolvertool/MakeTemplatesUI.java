package resolvertool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


/*
 *
 * Usage
 * -----
 *
 * Indicate class names in the MakeTemplatesUI to create Java templates in the /dev folder
 *
 * You can specify a range of names with '-' for example "A-C" will create A.java, B.java and C.java
 *
 *
 *
 */
public class MakeTemplatesUI extends JFrame {

  public static void main(String[] a) {
    new MakeTemplatesUI();
  }


  private ApplicationProp applicationProps;
  private final int DEFAULT_W = 500;
  private final int DEFAULT_H = 150;
  private CanvasPanel canvasPanel;
  private JLabel labelIndicateSuccess;
  private JTextField classNameTextField;


  public MakeTemplatesUI() {
    applicationProps = new ApplicationProp("app.auto.properties");

    setTitle("Create new templates");
    setBackground(Color.WHITE);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Integer win_w = applicationProps.readInt("win_w");
    Integer win_h = applicationProps.readInt("win_h");
    canvasPanel = win_w==null && win_h== null ? new CanvasPanel(DEFAULT_W, DEFAULT_H) : new CanvasPanel(win_w, win_h);

    Integer win_xpos = applicationProps.readInt("win_xpos");
    Integer win_ypos = applicationProps.readInt("win_ypos");
    if (win_xpos != null && win_ypos != null) {
      setLocation(win_xpos, win_ypos);
    } else {
      setLocation(100,100);
    }


    JLabel helpfulInfo = new JLabel("Name (hit enter)");
    helpfulInfo.setBackground(Color.white);
    canvasPanel.add(helpfulInfo);
    helpfulInfo.addKeyListener(new CanvasKeyListener());

    classNameTextField = new JTextField("", 10);
    classNameTextField.setEditable(true);
    canvasPanel.add(classNameTextField);
    classNameTextField.addKeyListener(new CanvasKeyListener());

    labelIndicateSuccess = new JLabel("");
    canvasPanel.add(labelIndicateSuccess);
    setContentPane(canvasPanel);

    addKeyListener(new CanvasKeyListener());
    pack();
    setVisible(true);
  }


  class CanvasPanel extends JPanel {
    public CanvasPanel(int width, int height) {
      setBackground(Color.WHITE);
      setBorder(null);
      setPreferredSize(new Dimension(width, height));
    }
  }


  private class CanvasKeyListener extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent ke) {
      labelIndicateSuccess.setText("");
      // compile code and exit on enter
      if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
        boolean success = true;
        String classNames = classNameTextField.getText().trim();
        if (classNames.equals("")) {
          labelIndicateSuccess.setText("no input");
          return;
        }
        if (!classNames.replaceAll("[a-zA-Z0-9_\\-,]", "").equals("")) {
          labelIndicateSuccess.setText("illegal char");
          return;
        }
        if (classNames.charAt(0) >= '0' && classNames.charAt(0) <= '9') {
          labelIndicateSuccess.setText("illegal char");
          return;
        }
        if (classNames.charAt(0) == '_' || classNames.charAt(0) == ',' || classNames.charAt(0) == '-') {
          labelIndicateSuccess.setText("illegal char");
          return;
        }


        try {
          // make the templates
          new MakeTemplates(classNames);
        } catch (Exception e) {
          labelIndicateSuccess.setText("error!");
          System.err.println("Something went wrong!");
          e.printStackTrace();
          success = false;
          return;
        }
        if (success) {
          closeApp();
        } else {
          labelIndicateSuccess.setText("error!");
        }
      }
      // exit on esc (do nothing)
      if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
        closeApp();
      }
    }
  }


  public void closeApp() {
    applicationProps.addProperty("win_w", ""+canvasPanel.getWidth());
    applicationProps.addProperty("win_h", ""+canvasPanel.getHeight());
    applicationProps.addProperty("win_xpos", ""+this.getX());
    applicationProps.addProperty("win_ypos", ""+this.getY());
    applicationProps.saveToFile();
    System.exit(0);
  }



}










