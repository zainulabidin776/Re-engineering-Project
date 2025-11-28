
import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class POS extends PointOfSale {
  public POS(){};
  
  public void deleteTempItem(int id){
    // POS doesn't have phone number in temp file
    deleteTempItemHelper(id, false);
  }
  
  public double endPOS(String textFile)
  {
    detectSystem();
    boolean bool=true;
    if (transactionItem.size()>0){
    totalPrice = totalPrice*tax; //calculates price with tax
    //prints total with taxes
   // bool=payment();
    
    //System.out.format("Total with taxes: %.2f\n", totalPrice);
    inventory.updateInventory(textFile, transactionItem, databaseItem,true);
    }
    //delete log file
    File file = new File(tempFile);
      file.delete();
      if(bool==true){
    //invoice record file
    try{
      DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_LONG);
  Calendar cal = Calendar.getInstance();
      String t = Constants.SALE_INVOICE_RECORD;
      if(SystemUtils.isWindows()){
        //t = "..\\Database\\saleInvoiceRecord.txt"; 
      }
      FileWriter fw2 = new FileWriter(t,true);
      BufferedWriter bw2 = new BufferedWriter(fw2);
      bw2.write(dateFormat.format(cal.getTime()));
      bw2.write(SystemUtils.getLineSeparator());
      for(int i=0;i<transactionItem.size();i++){
       String log=Integer.toString(transactionItem.get(i).getItemID())+" "+transactionItem.get(i).getItemName()+" "+
                        Integer.toString(transactionItem.get(i).getAmount())+" "+
                        Double.toString(transactionItem.get(i).getPrice()*transactionItem.get(i).getAmount());
       bw2.write(log);
      bw2.write(SystemUtils.getLineSeparator());
      }
      bw2.write("Total with tax: "+totalPrice);
      bw2.newLine();
      bw2.close();
      
    } catch (FileNotFoundException e) {
      System.out.println("Unable to open file Log File for logout"); 
    }
    catch (IOException e) {
      e.printStackTrace();
    }  
      }
     databaseItem.clear();
    transactionItem.clear();
    return totalPrice;
  }
  
  public void retrieveTemp(String textFile){
    try{
      FileReader fileR = new FileReader(tempFile);
      BufferedReader textReader = new BufferedReader(fileR);
      String line=null;
     
      String[] lineSort;
      line=textReader.readLine();
      inventory.accessInventory(textFile, databaseItem);
      
      while ((line = textReader.readLine()) != null)
      {
        lineSort = line.split(" ");
        int itemNo = Integer.parseInt(lineSort[0]);
        int itemAmount = Integer.parseInt(lineSort[1]);
        enterItem(itemNo,itemAmount);
      }
      textReader.close();
      updateTotal();
    }
    catch(FileNotFoundException ex) {
      System.out.println(
                         "Unable to open file 'temp'"); 
    }
    catch(IOException ex) {
      System.out.println(
                         "Error reading file 'temp'");  
    }
    
  }
  
}
