//input: PREREQ (CNUM CHAR 8, PNUM CHAR 8);
//output: Creating COURSE with 3 attributes.

public class RelationParser{

  String input;

  public void RelationParser(String input){
    this.input = input;
    String relationName = parseRelationName();
    int attributeCount = parseAttributeCount();
    if(attributeCount == -1){
      System.out.println("Error in RELATION input");
    }else{
      System.out.println("Creating " + relationName + " with " + attributeCount + " attributes.");
    }
  }

  public String parseRelationName(){
    String[] tokens = input.split(" ");
    return tokens[0];
  }
//parse attibute count looking for whitespace in between elements to count them and ensure proper input is followed
  public int parseAttributeCount(){
    int index = 0;
    int attributeCheck = 1;
    if(input.charAt(0) == '('){
      return -1;
    }
    while(index < input.length()-1){
      if(input.charAt(index) == '('){
        //Checks for whitespace in front of first element
        while(input.charAt(index+1) == ' '){
          index++;
        }
        while(input.charAt(index) != ')'){
          index++;
          if(input.charAt(index) == '('){
            return -1;
          }
          if(input.charAt(index) == ' '){
            attributeCheck++;
            index++;
          }
        }
      }else{
        index++;
        if(input.charAt(index) == ')'){
          return -1;
        }
      }
    }
  if(attributeCheck % 3 != 0){
    return -1;
  }
    return attributeCheck / 3;
  }
}
