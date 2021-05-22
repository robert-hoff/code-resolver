package resolvertool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;


public class MakeTemplates {

  // change MAKE_CLASSES to influence the templates created
  // interactive templates are not supported (but there is a template available)

  static String MAKE_CLASSES = "A";                     // <-- creates A.java in /src/main/java/dev
  // static String MAKE_CLASSES = "B-J";
  // static String MAKE_CLASSES = "A-F";
  // static String MAKE_CLASSES = "A_1";
  // static String MAKE_CLASSES = "A-B,C_1,C_2,D-F";


  final static String DIR_TEMPLATE = CodeResolverConfiguration.readProperty("DIR_TEMPLATE");
  final static String PACKAGE_TEMPLATE = CodeResolverConfiguration.readProperty("PACKAGE_TEMPLATE");
  final static String DIR_DEV_TARGET = CodeResolverConfiguration.readProperty("DIR_DEV_TARGET");
  final static String PACKAGE_DEV_TARGET = CodeResolverConfiguration.readProperty("PACKAGE_DEV_TARGET");


  public static void main(String[] args) {

    File file = new File(DIR_DEV_TARGET);
    file.mkdirs();
    String FILEPATH = String.format("%s/Template.java", DIR_TEMPLATE);
    Set<String> class_names = new TreeSet<>();

    String[] classes1 = MAKE_CLASSES.split(",");
    for (String s : classes1) {
      if (s.indexOf('-') > 0) {
        String str1 = s.substring(0, s.indexOf('-'));
        String str2 = s.substring(s.indexOf('-')+1);

        if (!str1.matches("[A-Z]")) {
          throw new RuntimeException("Error in format: "+s);
        }
        if (!str2.matches("[A-Z]")) {
          throw new RuntimeException("Error in format: "+s);
        }
        if (str2.charAt(0)-str1.charAt(0) < 1) {
          throw new RuntimeException("Error in ordering, please fix: "+s);
        }
        for (int i = 0; i <= str2.charAt(0)-str1.charAt(0); i++) {
          class_names.add(""+(char)(str1.charAt(0)+i));
        }
      } else {
        class_names.add(s);
      }
    }

    for (String target_classname : class_names) {
      String target_path = String.format("%s/%s.java", DIR_DEV_TARGET, target_classname);
      File f = new File(target_path);
      if(f.exists()) {
        throw new RuntimeException("\n\n"
            +"                 Files are present in target dir "+target_path+"\n"
            +"                 clear the dir before making new templates!\n\n");
      }
    }

    for (String target_classname : class_names) {
      String file_path = String.format("%s/%s.java", DIR_DEV_TARGET, target_classname);

      String file_content = makeTemplateFile(FILEPATH, target_classname);
      writeToFile(file_content, file_path);

      //      System.out.println(file_content);
    }
  }


  static void writeToFile(String fileContent, String filePath) {
    try {
      Files.write(Paths.get(filePath), fileContent.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("couldn't write to file: "+filePath);
    }
    System.out.printf("Generated template: %s \n", filePath);
  }


  // we can use JavaParser to rewrite the package name but it's easier with regex
  static String makeTemplateFile(String FILEPATH, String new_class_name) {
    byte[] encoded;
    try {
      encoded = Files.readAllBytes(Paths.get(FILEPATH));
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Error!");
    }
    String template_file = new String(encoded, StandardCharsets.UTF_8);

    String new_file = template_file.replaceAll("public class Template \\{", "public class "+new_class_name+" \\{");
    new_file = new_file.replaceAll("new Template\\(\\).solve\\(i\\);", "new "+new_class_name+"\\(\\).solve\\(i\\);");
    new_file = new_file.replaceAll("//\\!\\-\\-[.\\s\\S]+//\\-\\-", "");
    // new_file = new_file.replaceAll("/\\*[.\\s\\S]+\\*/", "");
    new_file = new_file.replaceAll("// Sys(.)+", "");
    new_file = new_file.replaceAll("// out(.)+", "");
    new_file = new_file.replaceAll("(?m)^@SuppressWarnings\\(\"all\"\\)$", "");
    new_file = new_file.replaceAll("package "+PACKAGE_TEMPLATE+";", "package "+PACKAGE_DEV_TARGET+";");

    return new_file;
  }



}








