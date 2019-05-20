//Output the name of the relation and how many attributes are being inserted.

//output: Inserting 3 attributes to COURSE.
//input: COURSE CSCI141 'COMPUTER PROGRAMMING I' 4;

import java.util.*;
import java.lang.*;

public class InsertParser{

  private String input;
  private String[] tokens;

  public void InsertParser(String input){
    this.input = input;
    tokens = input.split(" ");
    int attributeCount = parseAttributeCount();
    String relationName = parseRelationName();
    System.out.println("Inserting " + attributeCount + " attributes to " + relationName + ".");
}

  public String parseRelationName(){
    return (tokens[0]);
  }

  public int parseAttributeCount(){
    int index = 0;
    int count = 0;
    while(input.length()-1 > index){
      char current = input.charAt(index);
      if(current == ' '){
        index++;
      }else if(current == '\''){
        index++;
        current = input.charAt(index);
        while(current != ('\'')){
          index++;
          current = input.charAt(index);
        }
      index++;
      count++;
      }else{
        while(current != ' ' && current != ';'){
          index++;
          current = input.charAt(index);
        }
      count++;
      index++;
      }
    }
    return count-1;
  }
}
