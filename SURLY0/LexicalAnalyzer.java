//SURLY0 : building a Lexical Analyzer that can parse and recognize the commands specified in a given input file.
//Your program will read a command file (passed in as a command line argument) and print out elements of each command.

import java.util.*;
import java.io.*;

public class LexicalAnalyzer{

  public void run(String fileName){
    File file = new File(fileName);
    try{
      Scanner sc = new Scanner(file);
      while(sc.hasNextLine()){
        String current = sc.next();
        current = current.replaceAll("\\s+","");
        if(current.charAt(0) == '#'){
          current = sc.nextLine();
        }else if(current.equals("INSERT")){
          String token = "";
          while(!current.substring(current.length() - 1).equals(";")){
            current = sc.next();
            token += " " + current;
          }
          token = token.trim();
          InsertParser insert = new InsertParser();
          insert.InsertParser(token);
          current = sc.nextLine();
        }else if(current.equals("PRINT")){
          String token = "";
          while(!current.substring(current.length() - 1).equals(";")){
            current = sc.next();
            token += " " + current;
          }
          token = token.trim();
          PrintParser print = new PrintParser();
          print.PrintParser(token);
          if(sc.hasNextLine()){
            current = sc.nextLine();
          }
        }else if(current.equals("RELATION")){
          String token = "";
          while(!current.substring(current.length() - 1).equals(";")){
            current = sc.next();
            token += " " + current;
          }
          token = token.trim();
          RelationParser relation = new RelationParser();
          relation.RelationParser(token);
        }else{
          continue;
        }
      }
    }catch(FileNotFoundException ex){
      System.out.println("Error: File Not Found.");
    }
  }
}
