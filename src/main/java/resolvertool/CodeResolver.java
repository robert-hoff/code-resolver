package resolvertool;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;


/**
 *
 * Java Parser              https://javaparser.org/
 * Java Parser API          https://www.javadoc.io/doc/com.github.javaparser/javaparser-core/latest/index.html
 * Java Symbol Solver API   https://www.javadoc.io/doc/com.github.javaparser/java-symbol-solver-core/latest/index.html
 *
 *
 * Resolver steps
 * --------------
 * 1. Converts some features of the the source into a format suitable for Codeforces remote submission.
 *    I.e. removes the usage of reading from local files or local variables (the INPUT variable).
 *    And checks for System.getProperty("ONLINE_JUDGE")
 *
 * 2. Checks for any unused variables. The unused variables are removed from the source before the next step
 *
 * 3. Analyses the source and external calls to identify external dependencies. External dependencies are either
 *    external static methods defined in the extmethods.* package OR external objects defined in extclasses.*
 *
 * 4. Rewrites the compilation target so that all external dependencies are written into the local class.
 *
 *
 * Main features
 * -------------
 * - Create public static methods under any classname in the extmethods.* package. They can call each other without bounds.
 *   (that is, to other members in the extmethods.* package, but not to any other external dependency, and *not* to
 *   extclasses.*)
 * - Create classes in the extclasses.* package. Unlike methods external classes _cannot_ reference any further external members
 *
 *
 * Notes
 * --------
 *  - Resolver keeps unused static or non-static field-level method declarations (checking on these hasn't been implemented)
 *
 *
 * Limitations
 * -----------
 *  - We do not currently resolve objects encapsulated by external methods.
 *    E.g. a declaration like this will currently fail
 *
 *      public static long getPairValue() {
 *        Pair p = new Pair(10,10);
 *        return p.x;
 *      }
 *
 * it's necessary to return any instantiated object back to the compilation target
 *
 *  - All external methods must be defined in the same directory and belong to the same package
 *  - All external classes must be defined in the same directory and belong to the same package
 *
 *
 *
 * Term
 * -----
 * Scope name                     PR, STR, IN, ..            classes belonging to extmethods.*
 *
 * External method                Methods belonging to extmethods.* packages. They are all static. (note external method calls may
 *                                be reference new additional external methods - see 'nested method call')
 *
 * External object                Objects defined in extmethods.*. They can be used in various ways
 *                                (i) instantiated locally (ii) extended locally (iii) as part of an external method
 *                                call. Note if an object is instantiated in an external method the object *must* be returned
 *                                to the compilation target.
 *                                The resolver will fail if an external object is instantiated and not returned.
 *
 * Initial source                 The starting source file in the dev folder to be converted to a form suitable for submission
 *
 * Target source                  The source (or compilation unit) as it goes through stages in the pipeline
 *                                The end result is written to the Java base directory (this directory is convenient because we
 *                                don't want a package declaration). The compilation result is _compatible_ with the Codeforces
 *                                online judge.
 *
 * External file                  Any file that is external to the compilation target. The way "external" is used includes files that
 *                                reside in the local project, so i.e. external only to the initial source file
 *
 * External method call           In some contexts 'external method calls' are taken to mean calls made from the *initial source file* only,
 *                                in the CodeResolverMethodSource class these form a basis that are resolved further; they are
 *                                checked for nested method calls
 *
 * Nested method call             Calls to external method from another external method (to arbitrary depth)
 *
 *
 *
 *
 *
 */
public class CodeResolver {

  public static final String PACKAGE_METHODS = CodeResolverConfiguration.readProperty("PACKAGE_METHODS");
  public static final String PACKAGE_OBJECTS = CodeResolverConfiguration.readProperty("PACKAGE_OBJECTS");
  public static final String DIR_METHODS = CodeResolverConfiguration.readProperty("DIR_METHODS");
  public static final String DIR_OBJECTS = CodeResolverConfiguration.readProperty("DIR_OBJECTS");
  public static final String DIR_JAVAMAIN = CodeResolverConfiguration.DIR_JAVAMAIN;

  private String CLASSNAME;
  private CompilationUnit cuOriginal;

  public CodeResolver(CompilationUnit cuOriginal) {
    log.trace("Code Resolver Tool");
    this.cuOriginal = cuOriginal;
    this.CLASSNAME = cuOriginal.getTypes().get(0).getName().asString();
  }

  // Used by CompileSubmissionUI if a problem occurs it prevents it shutting down automatically (no reporting occurs on the UI)
  public boolean success = false;


  /*
   * Compilation pipeline
   * --------------------
   *
   *  - swaps the local dev template for remote submission template
   *    (instead of reading from INPUT or local file input is taken from system.in)
   *  - Removes unused field-level variables
   *  - Identifies external method calls and nested method calls
   *  - Identifies instantiations to external objects
   *  - Rewrites the compilation target with the external method and class code
   *  - Removes all imports except java.io.*, java.util.* and java.lang.Math.*
   *    written into the local class
   *
   *
   */
  public void compileTarget() {
    compileTarget(true, false);
  }
  public void compileTarget(boolean write, boolean showCompilation) {
    success = false;

    // initial regex stuff
    CompilationUnit cuNew = CodeResolver.performInitialConditioning(cuOriginal, CLASSNAME);

    // remove unused fields (alters cuNew)
    CodeResolver.removeUnusedFields(cuNew, CLASSNAME);
    CodeResolverMethodSource csr = new CodeResolverMethodSource(cuNew);

    // External classes from extclasses.* (their processing is currently completely separated from that of the external methods)
    Set<String> externalClassNames = getExternalClassNames(cuNew, PACKAGE_OBJECTS);
    List<ClassOrInterfaceDeclaration> externalClasses = parseExternalClasses(externalClassNames, DIR_OBJECTS);

    csr.performMethodCallsRewrite();
    insertMethodsIntoCompilationTarget(cuNew, csr.getMethodDeclarationsAsList(), CLASSNAME);

    // tidy imports
    insertImports(cuNew);

    // insert external classes
    insertClassesIntoCompilationTarget(cuNew, externalClasses, CLASSNAME);

    // remove empty lines
    String cuFinalResult = performFinalRegexConditioning(cuNew);

    // write to file
    if (write) {
      writeToFileAndClipboard(cuFinalResult, CLASSNAME);
    }
    if (showCompilation) {
      System.out.println(cuFinalResult);
    }

    success = true;
  }


  /*
   * Does the following
   * - Some regex
   * - rewrites the main method
   * - removes getBufferedReader() method
   * - removes package declaration
   *
   */
  public static CompilationUnit performInitialConditioning(CompilationUnit cuOriginal, String CLASSNAME) {
    String newCompilationStr = performInitialRegexReplacements(cuOriginal);
    CompilationUnit cuNew = conditionMainMethod(newCompilationStr, CLASSNAME);
    cuNew.removePackageDeclaration();
    return cuNew;
  }


  /*
   *
   * Replacements are
   *    - remove any classes starting with ZZ (ZZ methods are used for debugging only)
   *    - remove lines that contain 'System.err' (also only for debugging)
   *    - swap solve(int cNr) with solve()              <-- NB this change must correspond to the code supplied in newMainMethodBody(..)
   *    - replace System.out.print with out.print
   *    - remove any instances of 'if (cNr != 1) return;'
   *
   */
  public static String performInitialRegexReplacements(CompilationUnit cuOriginal) {
    String newCompilation = cuOriginal.toString();
    // newCompilation = newCompilation.replaceAll("import extmethods\\.ZZ;", "");
    newCompilation = newCompilation.replaceAll("(?m)^\\s*ZZ(.)+", "");
    newCompilation = newCompilation.replaceAll("(?m)^\\s*System\\.err(.)*\\s*\\n", "");
    newCompilation = newCompilation.replaceAll("void solve\\(int cNr\\)", "void solve\\(\\)");
    // The ? symbol is a 'reluctant quantifier', making the regex stop at the first match
    newCompilation = newCompilation.replaceAll("(?m)^\\s*if \\(cNr[ ]?!=[ ]?[\\d]+\\)[.\\s]*?return;\\s*\\n", "");
    return newCompilation;
  }


  /*
   *
   *
   *
   * replace with code defined in the method newMainMethodBody()
   *
   * NOTE
   * the addition of "IN nextInt" in the method_sigs variable
   * this method is always used for the testcases and must be included
   *
   */
  public static CompilationUnit conditionMainMethod(String cuOriginalString, String CLASSNAME) {
    Set<String> skipMethods = new HashSet<>();
    skipMethods.add("main");
    skipMethods.add("getBufferedReader");

    CompilationUnit cuNew = StaticJavaParser.parse(cuOriginalString);

    ClassOrInterfaceDeclaration classPrimary = cuNew.getClassByName(CLASSNAME).get();
    NodeList<BodyDeclaration<?>> classPrimaryMembers = classPrimary.getMembers();
    NodeList<BodyDeclaration<?>> keepTheseMembers = new NodeList<>();

    for (BodyDeclaration<?> decl : classPrimaryMembers) {
      if (decl.isMethodDeclaration()) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) decl;
        String methodName = methodDeclaration.getName().asString();

        if (methodName.equals("main")) {
          MethodDeclaration newMainMethod = generateNewMainMethod(methodDeclaration, CLASSNAME);
          keepTheseMembers.add(newMainMethod);
        }
        if (!skipMethods.contains(methodName)) {
          keepTheseMembers.add(methodDeclaration);
        }
      } else {
        decl.removeComment();
        keepTheseMembers.add(decl);
      }
    }

    classPrimary.setMembers(keepTheseMembers);
    return cuNew;
  }


  public static MethodDeclaration parseCopyMethodDeclaration(MethodDeclaration method) {
    MethodDeclaration methodNew = StaticJavaParser.parseMethodDeclaration(method.toString());
    return methodNew;
  }

  // This method isn't actually being used but the cuOriginal class is converted in this
  // way in the conditionMainMethod(..)
  public static CompilationUnit parseCopyCompilationUnit(CompilationUnit cu) {
    CompilationUnit cuNew = StaticJavaParser.parse(cu.toString());
    return cuNew;
  }


  /*
   *
   *
   *  // these three fields stay the same
   *  static BufferedReader br;
   *  static StringTokenizer st = new StringTokenizer("");
   *  static PrintWriter out = new PrintWriter(System.out);
   *
   *
   *  public static void main(String[] a) {
   *    doOnceStaticCall()            // <--
   *    int hello = 9;                // <-- these lines are preserved
   *
   *    // everything below this lines is replaced by new code, see method below
   *
   *               newMainMethodBody(..)
   *
   *
   *    IN.br = getBufferedReader();
   *    IN.st = new StringTokenizer("");
   *    long startTime = java.lang.System.currentTimeMillis();
   *    int t = HAS_TESTCASES>0 ? IN.nextInt() : 1
   *      for (int i=1; i<=t; i++) {
   *      new A01().solve(i);
   *    }
   *    System.err.printf("[%dms]\n", java.lang.System.currentTimeMillis()-startTime);
   *  }
   *
   *
   */
  public static MethodDeclaration generateNewMainMethod(MethodDeclaration oldMainMethod, String CLASSNAME) {
    MethodDeclaration newMainMethod = parseCopyMethodDeclaration(oldMainMethod);

    BlockStmt block_old = newMainMethod.getBody().get();
    BlockStmt block_new = new BlockStmt();
    for (Statement s : block_old.getStatements()) {
      s.removeComment();

      // include all statements before the line "IN.br = getBufferedReader();"
      // NOTE the space depends on PrettyPrinterConfiguration.setSpaceAroundOperators(true|false)
      if (s.toString().matches("IN.br[ ]?=[ ]?getBufferedReader.*")) {
        break;
      }
      block_new.addStatement(s);
    }


    BlockStmt block_insert = StaticJavaParser.parseBlock(newMainMethodBody(CLASSNAME));
    for (Statement s : block_insert.getStatements()) {
      block_new.addStatement(s);
    }
    newMainMethod.setBody(block_new);
    return newMainMethod;
  }


  /*
   * NOTE that IN.nextInt() is still left in and will be converted automatically
   *
   *
   */
  private static String newMainMethodBody(String classname) {
    return String.format(""
        +"{                                                                      \n"
        +"  if (System.getProperty(\"ONLINE_JUDGE\") == null) {                  \n"
        +"    System.setIn(new ByteArrayInputStream(INPUT.getBytes()));          \n"
        +"  }                                                                    \n"
        +"  br = new BufferedReader(new InputStreamReader(System.in));           \n"
        +"  int t = HAS_TESTCASES>0 ? IN.nextInt() : 1;                          \n"
        +"  for (int i=1; i<=t; i++) {                                           \n"
        +"    new %s().solve();                                                  \n"
        +"  }                                                                    \n"
        +"  out.flush();                                                         \n"
        +"}                                                                      \n"
        +"", classname);
  }



  /*
   * This is a bit crude but gets rid of some variables
   * NOTE It might be possible just to use
   *
   *    bodyDeclaration.remove()
   *
   */
  private static void removeUnusedFields(CompilationUnit cu, String CLASSNAME) {
    Set<String> knownTypes = new TreeSet<>();
    // skip st (StringTokenizer) manually because we need it from methods that are inserted in the IN library
    knownTypes.add("st");
    ClassOrInterfaceDeclaration classPrimary = cu.getClassByName(CLASSNAME).get();

    List<NameExpr> exp = classPrimary.findAll(NameExpr.class);
    for (NameExpr v : exp) {
      knownTypes.add(v.toString());
    }

    // remove the top level fields that are not recognized
    NodeList<BodyDeclaration<?>> classPrimaryMembersNew = new NodeList<>();
    for (BodyDeclaration<?> bodyDeclaration : classPrimary.getMembers()) {
      boolean include = true;
      if (bodyDeclaration.isFieldDeclaration()) {
        FieldDeclaration fieldDecl = bodyDeclaration.toFieldDeclaration().get();
        String fieldName = fieldDecl.getVariable(0).getNameAsString();
        if (!knownTypes.contains(fieldName)) {
          include = false;
        }
      }
      if (include) {
        classPrimaryMembersNew.add(bodyDeclaration);
      }
    }
    classPrimary.setMembers(classPrimaryMembersNew);
  }



  /*
   *
   * Get the external class names in the form
   *
   * [Seg, DiagonalIterator, Pair]
   *
   * NOTE they are not returned fully qualified,
   * but not essential because we retrieve them all again from the same directory, in src/main/java/extclasses
   *
   */
  private static Set<String> getExternalClassNames(CompilationUnit cu, String PACKAGE_OBJECTS) {
    Set<String> externalClassNames = new HashSet<>();
    // ClassOrInterfaceDeclaration classPrimary = cu.getClassByName(getClassName(cu)).get();

    for (ObjectCreationExpr exp : cu.findAll(ObjectCreationExpr.class)) {
      // ResolvedConstructorDeclaration resolvedDeclaration = exp.resolve();
      if (exp.resolve().getPackageName().equals(PACKAGE_OBJECTS)) {
        // System.err.println(exp.resolve().getClassName());
        externalClassNames.add(exp.resolve().getClassName());
      }
    }
    //
    //    for (VariableDeclarationExpr exp : cu.findAll(VariableDeclarationExpr.class)) {
    //      System.err.println(exp);
    //      System.err.println(exp.getVariables());
    //    }

    for (VariableDeclarator exp : cu.findAll(VariableDeclarator.class)) {
      if (exp.resolve().getType().isReferenceType()) {
        String qualifiedName = exp.resolve().getType().asReferenceType().getQualifiedName();
        int len = PACKAGE_OBJECTS.length();
        if (qualifiedName.length() > len && qualifiedName.substring(0, len).equals(PACKAGE_OBJECTS)) {
          // System.err.println(qualifiedName.substring(PACKAGE_OBJECTS.length()+1));
          externalClassNames.add(qualifiedName.substring(len+1));
        }
      }
    }

    for (ClassOrInterfaceDeclaration decl : cu.findAll(ClassOrInterfaceDeclaration.class)) {
      for (ResolvedReferenceType type : decl.resolve().getAllAncestors()) {
        String qualifiedName = type.getQualifiedName();
        int len = PACKAGE_OBJECTS.length();
        if (qualifiedName.length() > len && qualifiedName.substring(0, len).equals(PACKAGE_OBJECTS)) {
          externalClassNames.add(qualifiedName.substring(len+1));

          // System.err.println(qualifiedName.substring(len+1));

        }
      }
    }


    // Resolutions include
    //
    // TRIAL.getNewDog().nrOfLegs()      <-- getNewDog() returns a member of 'extclass.Dog' which is not
    //                                       assigned anywhere. But there exists a MethodCallExpr that will
    //                                       resolve this classname
    //
    for (MethodCallExpr exp : cu.findAll(MethodCallExpr.class)) {
      ResolvedMethodDeclaration resolvedMethod = exp.resolve();
      // wtf - this chained statement may return "extclasses.Pair"
      // System.err.println(resolvedMethod.getReturnType().asReferenceType().getTypeParametersMap().get(0).b.asReferenceType().getQualifiedName());
      if (resolvedMethod.getPackageName().equals(PACKAGE_OBJECTS)) {
        externalClassNames.add(resolvedMethod.getClassName());
      }
      // System.err.printf("%-30s %s \n", resolvedMethod.getPackageName(), resolvedMethod.getClassName());
    }
    return externalClassNames;
  }


  /*
   *
   * Get target external class from file
   *
   */
  public static List<ClassOrInterfaceDeclaration> parseExternalClasses(Set<String> externalClassNames, String DIR_OBJECTS) {
    List<ClassOrInterfaceDeclaration> externalClasses = new ArrayList<>();

    for (String className : externalClassNames) {

      CompilationUnit cuLocalClass;
      String fileName = String.format("%s/%s.java", DIR_OBJECTS, className);
      // System.err.println(fileName);
      try {
        cuLocalClass = StaticJavaParser.parse(new File(fileName));
      } catch (FileNotFoundException e) {
        throw new RuntimeException("ERROR!");
      }

      ClassOrInterfaceDeclaration localClass = null;
      if (cuLocalClass.getClassByName(className).isPresent()) {
        localClass = cuLocalClass.getClassByName(className).get();
      }
      if (cuLocalClass.getInterfaceByName(className).isPresent()) {
        localClass = cuLocalClass.getInterfaceByName(className).get();
      }

      localClass.removeModifier(Modifier.Keyword.PUBLIC);
      localClass.addModifier(Modifier.Keyword.STATIC);

      externalClasses.add(localClass);
    }
    return externalClasses;
  }



  /*
   *
   * NOTE!
   * This assumes the declaration 'static BufferedReader br' is present.
   * External calls are placed right before it.
   *
   */
  private static void insertMethodsIntoCompilationTarget(CompilationUnit cu, List<MethodDeclaration> methods, String CLASSNAME) {
    ClassOrInterfaceDeclaration classPrimary = cu.getClassByName(CLASSNAME).get();
    NodeList<BodyDeclaration<?>> classPrimaryMembersNew = new NodeList<>();
    for (BodyDeclaration<?> bodyDeclaration : classPrimary.getMembers()) {

      // NOTE HERE
      // all the new methods are written in just before the buffered reader which therefore MUST be called "br"
      boolean declarationIsBufferedReader = false;
      if (bodyDeclaration.isFieldDeclaration()) {
        declarationIsBufferedReader = bodyDeclaration.asFieldDeclaration().getVariable(0).getNameAsString().equals("br");
      }
      if (declarationIsBufferedReader) {
        for (MethodDeclaration methodNew : methods) {
          methodNew.removeModifier(Modifier.Keyword.PUBLIC);
          classPrimaryMembersNew.add(methodNew);
        }
      }
      classPrimaryMembersNew.add(bodyDeclaration);
    }
    classPrimary.setMembers(classPrimaryMembersNew);
  }




  private static void insertClassesIntoCompilationTarget(CompilationUnit cu, List<ClassOrInterfaceDeclaration> externalClasses, String CLASSNAME) {

    ClassOrInterfaceDeclaration classPrimary = cu.getClassByName(CLASSNAME).get();
    NodeList<BodyDeclaration<?>> classPrimaryMembersNew = new NodeList<>();
    for (BodyDeclaration<?> bodyDeclaration : classPrimary.getMembers()) {
      boolean declarationIsBufferedReader = false;
      if (bodyDeclaration.isFieldDeclaration()) {
        declarationIsBufferedReader = bodyDeclaration.asFieldDeclaration().getVariable(0).getNameAsString().equals("br");
      }
      if (declarationIsBufferedReader) {
        for (ClassOrInterfaceDeclaration externalClass : externalClasses) {
          classPrimaryMembersNew.add(externalClass);
        }
      }
      classPrimaryMembersNew.add(bodyDeclaration);
    }
    classPrimary.setMembers(classPrimaryMembersNew);
  }



  /*
   * Keep Java imports
   * may drop static import to java.lang.Math if not used
   *
   */
  private static void insertImports(CompilationUnit cu) {
    boolean keepMathsImport = checkMathsImport(cu);
    NodeList<ImportDeclaration> importsNew = new NodeList<>();
    for (ImportDeclaration importDeclaration : cu.getImports()) {
      String importName = importDeclaration.getNameAsString();
      if (importName.length()==14 && importName.substring(0, 14).equals("java.lang.Math") && importDeclaration.isStatic()) {
        if (keepMathsImport) {
          importsNew.add(importDeclaration);
        }
        continue;
      }
      if (importName.length()>5 && importName.substring(0, 5).equals("java.")) {
        importsNew.add(importDeclaration);
      }
    }
    cu.setImports(importsNew);
  }

  private static boolean checkMathsImport(CompilationUnit cu) {
    for (MethodCallExpr exp : cu.findAll(MethodCallExpr.class)) {
      ResolvedMethodDeclaration resolvedMethod = exp.resolve();
      if (resolvedMethod.getPackageName().equals("java.lang") && resolvedMethod.getClassName().equals("Math")) {
        return true;
      }
    }
    return false;
  }



  /*
   * Remove empty lines and annotations
   *
   */
  private static String performFinalRegexConditioning(CompilationUnit cu) {
    String conditioned_result = cu.toString();
    conditioned_result = conditioned_result.replaceAll("(?m)^\\s*@(.)*", "");
    // (?m) is a regex configuration meaning "enable multiline". See http://www.regular-expressions.info/modifiers.html
    // see https://stackoverflow.com/questions/4123385/remove-all-empty-lines
    conditioned_result = conditioned_result.replaceAll("(?m)^\\s*\\n", "");
    conditioned_result = conditioned_result.replaceAll("System.out.print", "out.print");
    return conditioned_result;
  }



  private static void writeToFileAndClipboard(String cuFinalResult, String CLASSNAME) {
    String filePath = String.format("%s/%s.java", DIR_JAVAMAIN, CLASSNAME);
    try {
      Files.write(Paths.get(filePath), cuFinalResult.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Something went wrong! Couldn't write to file: "+ filePath);
    }
    copyResultToClipboard(cuFinalResult);
    System.out.println("Written "+filePath);
  }



  // this seems to cause a tiny delay, not sure why
  private static void copyResultToClipboard(String string_to_clipboard) {
    //    long t = System.currentTimeMillis();
    StringSelection stringSelection = new StringSelection(string_to_clipboard);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);
    //    System.out.println(System.currentTimeMillis()-t);
  }



  private static Logger log = LoggerFactory.getLogger(CodeResolver.class);

}









