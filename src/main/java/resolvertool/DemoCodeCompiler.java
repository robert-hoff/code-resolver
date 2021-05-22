package resolvertool;

import com.github.javaparser.ast.CompilationUnit;


public class DemoCodeCompiler {

  static final String DIR_DEMO_INPUT = "src/main/java/resolvertool/demoinput";


  public static void main(String[] a) {

    compileTargetSource("A05");
    //    testAllDemoInputs();

  }


  static void compileTargetSource(String CLASSNAME) {
    // long startTime = java.lang.System.currentTimeMillis();
    boolean writeToFile = false;
    boolean showCompilation = true;
    CompilationUnit cuOriginal = CodeResolverConfiguration.getCompilationUnit(DIR_DEMO_INPUT, CLASSNAME);
    new CodeResolver(cuOriginal).compileTarget(writeToFile, showCompilation);
    // System.err.printf("[%dms]\n", java.lang.System.currentTimeMillis()-startTime);
  }



  /*
   * Resolving all the demo files is a good test if the output dir is populated and they compile
   *
   *        src/main/java/*
   *
   * The only imports in the compilation result will be to java.io.*, java.util.*, java.lang.Math.*
   * so if there are no compilation errors the resolution most likely was successful
   *
   */
  static void testAllDemoInputs() {
    boolean writeToFile = true;
    boolean showCompilation = false;
    String[] compilationTargets = {"A01","A02","A03","A04","A05","A06"};
    long startTime;
    for (String name : compilationTargets) {
      startTime = java.lang.System.currentTimeMillis();
      CompilationUnit cuOriginal = CodeResolverConfiguration.getCompilationUnit(DIR_DEMO_INPUT, name);
      new CodeResolver(cuOriginal).compileTarget(writeToFile, showCompilation);
      System.err.printf("[%dms]\n", java.lang.System.currentTimeMillis()-startTime);
    }
  }



}





