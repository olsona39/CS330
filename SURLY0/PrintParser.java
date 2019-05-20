
public class PrintParser{

  private String input;

  public void PrintParser(String input){
    this.input = input;
    input = input.trim();
    int relationCount = input.split("\\s+").length;
    String relationNames = parseRelationNames();
    System.out.println("Printing " + relationCount + " relations: " + relationNames + ".");

  }

  public String parseRelationNames(){
    
    input = input.replace(input.substring(input.length()-1), "");
    return input;
  }
}
