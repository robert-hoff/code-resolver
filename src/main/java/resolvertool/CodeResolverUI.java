package resolvertool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.github.javaparser.ast.CompilationUnit;


public class CodeResolverUI extends JFrame {


  static final String SRC_FOLDER = CodeResolverConfiguration.readProperty("DIR_DEV_TARGET");

  public static void main(String[] args) {
    List<String> classNames = getClassNamesSourceFolder(SRC_FOLDER);
    new CodeResolverUI(classNames);
  }

  public static List<String> getClassNamesSourceFolder(String folder_str) {
    File folder = new File(folder_str);
    List<String> classNames = new ArrayList<>();
    for (final File fileEntry : folder.listFiles()) {
      if (!fileEntry.isDirectory()) {
        String filename = fileEntry.getName();
        if (filename.length() < 6) {
          continue;
        }
        if (!filename.substring(filename.length()-5).equals(".java")) {
          continue;
        }
        classNames.add(filename.substring(0, filename.length()-5));
      }
    }
    Collections.sort(classNames);
    return classNames;
  }


  private CodeResolverUI application;
  private ApplicationProp applicationProps;
  private final int DEFAULT_W = 500;
  private final int DEFAULT_H = 150;
  private CanvasPanel canvasPanel;
  private List<String> classNames;
  private int nameIndex = 0;
  private JLabel labelIndicateSuccess;



  public CodeResolverUI(List<String> classNames) {
    this.application = this;
    this.classNames = classNames;
    applicationProps = new ApplicationProp("app.auto.properties");

    setTitle("Select class");
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

    String selectedClassName = applicationProps.read("selected_class");
    if (selectedClassName == null) {
      nameIndex = 0;
    } else {
      for (int i = 0; i < classNames.size(); i++) {
        if (classNames.get(i).equals(selectedClassName)) {
          nameIndex = i;
        }
      }
    }

    if (classNames.size()==0) {
      System.err.println("The dev directory is empty!");
      System.err.println("Run coderesolver.MakeTemplate to populate the dev directory");
      return;
    }



    JTextField classNameTextField = new JTextField(classNames.get(nameIndex), 10);
    classNameTextField.setEditable(false);
    classNameTextField.addKeyListener(new TextFieldListener(classNameTextField));
    canvasPanel.add(classNameTextField);
    classNameTextField.addKeyListener(new CanvasKeyListener());

    labelIndicateSuccess = new JLabel("");
    canvasPanel.add(labelIndicateSuccess);
    setContentPane(canvasPanel);

    addKeyListener(new CanvasKeyListener());
    pack();
    setVisible(true);
  }



  private class TextFieldListener extends KeyAdapter {
    JTextField text_field;
    public TextFieldListener(JTextField text_field) {
      this.text_field = text_field;
    }
    @Override
    public void keyPressed(KeyEvent ke) {
      if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
        if (nameIndex < classNames.size()-1) {
          nameIndex++;
        }
        text_field.setText(classNames.get(nameIndex));
      }
      if (ke.getKeyCode() == KeyEvent.VK_UP) {
        if (nameIndex > 0) {
          nameIndex--;
        }
        text_field.setText(classNames.get(nameIndex));
      }
      if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
        closeApp();
      }
    }
  }


  class CanvasPanel extends JPanel {
    private static final long serialVersionUID = -5671408750045486992L;
    public CanvasPanel(int width, int height) {
      setBackground(Color.WHITE);
      setBorder(null);
      setPreferredSize(new Dimension(width, height));
    }
  }


  public void saveProperties() {
    applicationProps.addProperty("win_w", ""+canvasPanel.getWidth());
    applicationProps.addProperty("win_h", ""+canvasPanel.getHeight());
    applicationProps.addProperty("win_xpos", ""+this.getX());
    applicationProps.addProperty("win_ypos", ""+this.getY());
    applicationProps.addProperty("selected_class", ""+classNames.get(nameIndex));
    applicationProps.saveToFile();
  }

  public void closeApp() {
    saveProperties();
    System.exit(0);
  }


  private class CanvasKeyListener extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent ke) {
      // compile code and exit on enter
      if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
        String target_classname = classNames.get(nameIndex);
        boolean success = false;
        try {
          // * Compile target class *
          CompilationUnit cu = CodeResolverConfiguration.getCompilationUnit(SRC_FOLDER, target_classname);
          CodeResolver cc = new CodeResolver(cu);
          cc.compileTarget();
          success = cc.success;
        } catch (Exception e) {
          labelIndicateSuccess.setText("error!");
          System.err.println("Something went wrong!");
          e.printStackTrace();
          return;
        }
        if (success) {
          // labelIndicateSuccess.setText("SUCCESS! closing..");
          new SignalCloseThread(application).start();
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


  private static final int TIMEOUT_APPLICATION_MS = 1; // just close as fast as possible
  private static class SignalCloseThread extends Thread {
    CodeResolverUI ui;
    public SignalCloseThread(CodeResolverUI ui) {
      this.ui = ui;
    }
    @Override
    public void run(){
      try {
        sleep(TIMEOUT_APPLICATION_MS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      ui.closeApp();
    }
  }


}





