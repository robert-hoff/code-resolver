package resolvertool;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;



/**
 *
 * CodeResolverMethodSource
 * ------------------------
 *
 * Extracts and rewrites the external method calls. Code is parsed and organised in
 *
 *    Map<String, MethodSourceContainer> externalMethodsSource
 *
 *
 *
 *
 *
 */
public class CodeResolverMethodSource {

  public static final String PACKAGE_METHODS = CodeResolverConfiguration.readProperty("PACKAGE_METHODS");
  public static final String DIR_METHODS = CodeResolverConfiguration.readProperty("DIR_METHODS");

  /*
   * These are the target classes the the compilation unit references. E.g.
   *
   *      [PR, IN, MATH]
   *
   */
  private Set<String> externalScopeNames = new HashSet<>();

  /*
   * Place newly discovered method signatures here, these are then checked for
   * nested method calls
   *
   */
  private Stack<String> methodSignaturesNeedChecking = new Stack<>();
  private Set<String> methodSignaturesChecked = new TreeSet<>();

  /*
   *
   * Maps qualified method-signatures (extracted from the source vode with JavaSymbolSolver)
   * The signatures maps to all the code we need related to this method, which includes
   * the method declaration (MethodDeclaration) and one or more method call expressions (MethodCallExpr)
   *
   */
  private Map<String, MethodSourceContainer> externalMethodsSource = new HashMap<>();

  /*
   *  the MethodSourceContainers that have non-zero number of related method
   *  calls are assigned here (for easy lookup)
   *
   */
  private Map<String, MethodSourceContainer> activeMethodsSource = new HashMap<>();


  private static Set<String> methodRewrittenNamesLookup = new HashSet<>();


  /*
   *
   * MethodSourceContainer encapsulates
   *
   *  - A (one) method declaration identified by its qualified signature (as resolved by JavaSymbolSolver)
   *  - one or more method calls to this method declaration, that is, there will exist one or more
   *    method calls if the declaration is found to be referenced by method calls in the compliation target or
   *    external methods library
   *  - zero method calls i.e. externalMethodCalls.size() == 0. Indicating this MethodDeclaration is not
   *    needed for this compilation target
   *
   * Note in particular
   * All external methods for a given scope (as written to externalScopeNames) are precompiled in here.
   * So intially externalMethodsCalls.size()==0 until MethodCallExp objects are added with addMethodCall(..)
   *
   *
   */
  static class MethodSourceContainer implements Comparable<MethodSourceContainer> {

    private String scopeName;
    private MethodDeclaration methodDeclaration;
    private List<MethodCallExpr> externalMethodCalls = new ArrayList<>();

    public MethodSourceContainer(String scopeName, MethodDeclaration methodDeclaration) {
      this.scopeName = scopeName;
      this.methodDeclaration = methodDeclaration;
    }

    public void addMethodCall(MethodCallExpr exp) {
      externalMethodCalls.add(exp);
    }

    public boolean hasMethodCalls() {
      return externalMethodCalls.size() > 0;
    }


    private String rewrittenMethodName = "";

    // make a cool new name for this method
    private void generateNewName() {

      String methodName = methodDeclaration.getNameAsString();

      // cool cryptic name
      rewrittenMethodName = ""+methodName.charAt(0);
      String methodNameReplace = methodName.replaceAll("[a-z]", "").toLowerCase();
      rewrittenMethodName += methodNameReplace;

      if (rewrittenMethodName.length()==1) {
        rewrittenMethodName = methodName.substring(0, Math.min(2, methodName.length())).toLowerCase();
      }

      if (rewrittenMethodName.equals("to")) {
        rewrittenMethodName = "t";
      }

      // normal name
      // rewrittenMethodName = methodName;


      String nameCandidate = rewrittenMethodName;
      int count = 2;

      while(methodRewrittenNamesLookup.contains(nameCandidate)) {
        // nameCandidate = rewrittenMethodName.substring(0, Math.max(rewrittenMethodName.length()-1,1)) + count;
        nameCandidate = rewrittenMethodName + count;
        count++;
      }
      rewrittenMethodName = nameCandidate;
      methodRewrittenNamesLookup.add(rewrittenMethodName);
    }



    //
    // Rewrites the externalCalls (which will affect the printout of the CompilationUnit that they belong to)
    //
    //    Orginal call                  New call
    //    ---------------------------------------------------
    //    IN.nextInt()                  nextInt()
    //    IN.nextString()               nextString()
    //    PR.print(aaaa, "_")           print(aaaa, "_")
    //    MATH.addOne(v + 10)           addOne(v + 10)
    //
    // The rewrite is very simple it just removes the scopeName (no typechecking is done here)
    // Update! added method rewriting so the new technique drops the scope name and changes the method name (for both the
    // MethodCallExpr and MethodDeclaration to generateNewName()
    //
    //
    //
    void rewriteMethodCalls() {
      generateNewName();
      // note, removes the public declaration
      methodDeclaration.removeModifier(Modifier.Keyword.PUBLIC);
      methodDeclaration.setName(rewrittenMethodName);

      for (MethodCallExpr exp : externalMethodCalls) {
        //        System.err.println(exp);
        //        System.err.println(exp.resolve().getQualifiedName());

        String argumentsList = exp.getArguments().toString();
        argumentsList = argumentsList.substring(1, argumentsList.length()-1); // first and last chars are [ and ]
        String newMethodCallDeclaration = String.format("%s(%s)", rewrittenMethodName, argumentsList);
        // performs the replacement
        exp.replace(StaticJavaParser.parseExpression(newMethodCallDeclaration));
      }
    }



    @Override
    public int compareTo(MethodSourceContainer msc2) {
      // sort on scope first, then rewritten-name
      if (scopeName.equals(msc2.scopeName)) {
        return rewrittenMethodName.compareTo(msc2.rewrittenMethodName);
      } else {
        // bring the IN and PR classes to the bottom
        if (scopeName.equals("IN")) return 1;
        if (msc2.scopeName.equals("IN")) return -1;
        if (scopeName.equals("PR")) return 1;
        if (msc2.scopeName.equals("PR")) return -1;
        return scopeName.compareTo(msc2.scopeName);
      }
    }

    @Override
    public String toString() {
      return methodDeclaration.getDeclarationAsString(false, false);
    }
  }



  /*
   *
   * Find the signature for this MethodCallExpr and add it to
   *
   *    Map<String, MethodSourceContainer> methodSource = new HashMap<>();
   *
   *
   */
  private void registerMethodCallExpression(MethodCallExpr exp) {

    String qualifiedSignature = getMethodCallExpressionQualifiedSignature(exp);

    if (!methodSignaturesChecked.contains(qualifiedSignature)) {
      methodSignaturesNeedChecking.add(qualifiedSignature);
      methodSignaturesChecked.add(qualifiedSignature);
    }

    // get the declaration
    MethodSourceContainer msc = externalMethodsSource.get(qualifiedSignature);
    if (msc == null) {
      throw new RuntimeException("Missing method source!");
    }

    msc.addMethodCall(exp);
    activeMethodsSource.put(qualifiedSignature, msc);
  }



  private CompilationUnit cu;


  public CodeResolverMethodSource(CompilationUnit cu) {
    this.cu = cu;
    methodRewrittenNamesLookup = new HashSet<>();

    // this sets up externalScopeNames, e.g. [PR, IN]
    // and the external method source, in the form of MethodDeclaration objects
    extractScopeNamesInitialSource();

    // System.err.println(externalScopeNames);
    // System.err.println(externalMethodsSource.keySet());


    // These are the external method calls that are found in the source file
    // when registering the method call, in registerMethodCallExpression(), the signature is written to
    // Stack<String> methodSignaturesNeedChecking to indicate that we should scan the declaration for further nested calls
    List<MethodCallExpr> externalMethodCalls = extractMethodCallsInitialSource();
    for (MethodCallExpr exp : externalMethodCalls) {
      // System.err.println(exp);
      registerMethodCallExpression(exp);
    }

    // resolve nested dependencies
    while(methodSignaturesNeedChecking.size() > 0) {
      String nextMethodSig = methodSignaturesNeedChecking.pop();
      MethodDeclaration externalMethodDeclaration = externalMethodsSource.get(nextMethodSig).methodDeclaration;
      if (externalMethodDeclaration == null) {
        throw new RuntimeException("Can't find the source for: "+nextMethodSig);
      }
      extractScopeNamesMethodDecl(externalMethodDeclaration);
    }


  }


  public void performMethodCallsRewrite() {
    for (MethodSourceContainer msc : activeMethodsSource.values()) {
      msc.rewriteMethodCalls();
    }
  }

  public void showActiveMethodSource() {
    for (MethodSourceContainer msc : activeMethodsSource.values()) {
      System.err.printf("%-60s %s \n", msc.toString(), msc.externalMethodCalls.size());
    }
  }


  public List<MethodDeclaration> getMethodDeclarationsAsList() {
    List<MethodSourceContainer> mscList = new ArrayList<>();
    for (MethodSourceContainer msc : activeMethodsSource.values()) {
      mscList.add(msc);
    }
    Collections.sort(mscList);
    List<MethodDeclaration> methods = new ArrayList<>();
    for (MethodSourceContainer msc : mscList) {
      methods.add(msc.methodDeclaration);
    }

    return methods;
  }





  // FIXME - can combine these two methods
  // check for calls based on membership to the live package


  /*
   *
   * Note external calls here are checked against Set<String> externalScopeNames
   * In extractScopeNamesInitialSource() extraction is based on membership to the 'live' package
   *
   */
  private List<MethodCallExpr> extractMethodCallsInitialSource() {
    List<MethodCallExpr> externalMethodCalls = new ArrayList<>();
    for (MethodCallExpr exp : cu.findAll(MethodCallExpr.class)) {
      if (exp.getScope().isPresent()) {
        String scopeName = exp.getScope().get().toString();
        if (externalScopeNames.contains(scopeName)) {
          // System.err.println(exp);
          externalMethodCalls.add(exp);
        }
      }
    }
    return externalMethodCalls;
  }


  /*
   *
   * This scans all MethodCallExpr type expressions in the opening source file.
   * If any methods are seen to belong to the 'live' class (PACKAGE_METHODS) they are extracted.
   *
   * scope-names are short names in the following form (matching source files in the live directory)
   *
   *    PR, DEV, IN, STR
   *
   *
   */
  private void extractScopeNamesInitialSource() {
    for (MethodCallExpr exp : cu.findAll(MethodCallExpr.class)) {
      ResolvedMethodDeclaration resMethod = exp.resolve();
      if (resMethod.getPackageName().equals(PACKAGE_METHODS)) {
        String scopeName = resMethod.getClassName();
        parseExternalMethods(scopeName);
        // System.err.printf("%-60s %-25s %s \n", resMethod.getQualifiedSignature(), resMethod.getPackageName(), resMethod.getClassName());
      }
    }
  }



  /*
   *
   * Analyse the external method for nested dependencies
   * If found, pass the scope name to the source-parser (to add to externalMethodsSource collection)
   * And register the method call
   *
   *
   */
  private void extractScopeNamesMethodDecl(MethodDeclaration externalMethod) {
    for (MethodCallExpr exp : externalMethod.findAll(MethodCallExpr.class)) {
      ResolvedMethodDeclaration resMethod = exp.resolve();
      if (resMethod.getPackageName().equals(PACKAGE_METHODS)) {
        // System.err.println(exp);
        String scopeName = resMethod.getClassName();
        parseExternalMethods(scopeName);
        registerMethodCallExpression(exp);
        // System.err.printf("%-60s %-25s %s \n", resMethod.getQualifiedSignature(), resMethod.getPackageName(), resMethod.getClassName());
      }
    }
  }



  /*
   *
   * Parses all the method declarations in a given file, with the class matching the scopeName
   * The scopenames are in the form
   *
   *        IN, PR, DEV, STR, MAT
   *
   *
   */
  public void parseExternalMethods(String scopeName) {
    // pass all scope names here as they are found, if we've already got them they are ignored
    if (externalScopeNames.contains(scopeName)) {
      return;
    }

    externalScopeNames.add(scopeName);
    String scopeFileName = String.format("%s/%s.java", DIR_METHODS, scopeName);

    CompilationUnit cuScopeSource;
    try {
      cuScopeSource = StaticJavaParser.parse(new File(scopeFileName));
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Can't find the file for this scope name! "+scopeFileName);
    }
    for (MethodDeclaration methodDeclaration : cuScopeSource.findAll(MethodDeclaration.class)) {
      String methodSignature = getMethodDeclarationQualifiedSignature(methodDeclaration);
      externalMethodsSource.put(methodSignature, new MethodSourceContainer(scopeName, methodDeclaration));
    }
  }




  /*
   *
   * Gets the method signature for a given MethodDeclaration using the JavaSymbolSolver
   *
   *
   * In package      Method declaration                             Resolves to qualified signature
   * -----------------------------------------------------------------------------------------
   * IN              nextInt()                                      live.IN.nextInt()
   * PR              print(List<?> a, int i1, int i2)               live.PR.print(java.util.List<?>, int, int)
   *
   *
   */
  private String getMethodDeclarationQualifiedSignature(MethodDeclaration decl) {
    ResolvedMethodDeclaration resolvedMethodDecl = decl.resolve();
    String qualifiedSignature = resolvedMethodDecl.getQualifiedSignature();
    return qualifiedSignature;
  }



  /*
   * Gets the method signature based on MethodCallExpr using JavaSymbolSolver
   *
   *
   * MethodCallExpr                        Resolves to qualified signature
   * -----------------------------------------------------------------------------------------
   * IN.nextInt()                          live.IN.nextInt()
   * PR.print(my_list, "_")                live.PR.print(java.util.List<?>, java.lang.String)
   * MATH.addOne(v + 10)                   live.MATH.addOne(int)
   *
   *
   */
  private String getMethodCallExpressionQualifiedSignature(MethodCallExpr exp) {
    ResolvedMethodDeclaration resolvedMethodDecl = exp.resolve();
    String qualifiedSignature = resolvedMethodDecl.getQualifiedSignature();
    return qualifiedSignature;
  }



}









