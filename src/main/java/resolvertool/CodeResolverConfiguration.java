package resolvertool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;


public class CodeResolverConfiguration {

  static final String DIR_JAVAMAIN = "src/main/java";
  static final String PROPERTIES_FILE = "app.properties";
  static final String PROPERTIES_FILE_DEFAULT = "app.properties.default";


  //  public static void main(String[] a) {
  //    System.err.println(readProperty("DIR_TARGET_SOURCE"));
  //    System.err.println(readProperty("PACKAGE_METHODS"));
  //    System.err.println(readProperty("PACKAGE_OBJECTS"));
  //    System.err.println(readProperty("DIR_METHODS"));
  //    System.err.println(readProperty("DIR_OBJECTS"));
  //    System.err.println(readProperty("DIR_TEMPLATE"));
  //  }



  public static String readProperty(String prop) {
    checkPropertiesFile();
    ApplicationProp props = new ApplicationProp();
    return props.read(prop);
  }


  private static void checkPropertiesFile() {
    Path path = Paths.get(PROPERTIES_FILE);
    if (Files.exists(path)) {
      return;
    } else {
      copyInDefaultProperties();
    }
  }

  private static void copyInDefaultProperties() {
    log.warn("Properties file missing, auto generating ./app.properties");
    Path copied = Paths.get(PROPERTIES_FILE);
    Path originalPath = Paths.get(PROPERTIES_FILE_DEFAULT);
    try {
      Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("couldn't create properties file");
    }
  }




  public static ParserConfiguration setConfiguration() {
    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(new ReflectionTypeSolver());
    // This line is critical for the symbol solver to identify local classes
    combinedTypeSolver.add(new JavaParserTypeSolver(new File(DIR_JAVAMAIN)));
    JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

    ParserConfiguration configuration = new ParserConfiguration();
    configuration.setSymbolResolver(symbolSolver);

    // If set to 'true' will *retain* all comments
    // If set to 'false' will *remove* all comments
    // NOTE if comments are left on some weird stuff happens in the first step when converting the main method
    //
    //        CodeCompiler.generateNewMainMethod(..)
    //
    // I discovered it might have something to do with parsing code from a String rather than a file
    // which is happening in the method
    //
    //        CodeCompiler.conditionMainMethod(..)
    //
    // with the line
    //
    //        CompilationUnit cuNew = StaticJavaParser.parse(cuOriginalString);
    //
    //
    configuration.setAttributeComments(false);
    StaticJavaParser.setConfiguration(configuration);

    PrettyPrinterConfiguration print_config = new PrettyPrinterConfiguration();
    print_config.setIndentSize(2);
    print_config.setSpaceAroundOperators(false);
    CompilationUnit.setToStringPrettyPrinterConfiguration(print_config);

    return configuration;
  }



  public static CompilationUnit getCompilationUnit(String DEV_PATH, String CLASSNAME) {
    CodeResolverConfiguration.setConfiguration();
    String FILEPATH = String.format("%s/%s.java", DEV_PATH, CLASSNAME);
    CompilationUnit cu;
    try {
      cu = StaticJavaParser.parse(new File(FILEPATH));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException("can't find this file! "+FILEPATH);
    }
    return cu;
  }




  /*
   * Searches the top-level (or 'field-level') declarations
   *
   */
  public static MethodDeclaration findMethodDeclarationByName(CompilationUnit cu, String methodName) {
    String className = cu.getTypes().get(0).getName().asString();
    for (BodyDeclaration<?> decl : cu.getClassByName(className).get().getMembers()) {
      if (decl.isMethodDeclaration()) {
        MethodDeclaration method = (MethodDeclaration) decl;
        if (method.getName().asString().equals(methodName)) {
          return method;
        }
      }
    }
    throw new RuntimeException("couldn't find this method! "+methodName);
  }


  private static Logger log = LoggerFactory.getLogger(CodeResolverConfiguration.class);


}






