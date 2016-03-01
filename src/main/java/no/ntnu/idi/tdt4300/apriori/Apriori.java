package no.ntnu.idi.tdt4300.apriori;

import org.apache.commons.cli.*;

import sun.nio.cs.ext.Big5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * This is the main class of the association rule generator.
 * <p>
 * It's a dummy reference program demonstrating the accepted command line arguments, input file format and standard output
 * format also required from your implementation. The generated standard output follows the CSV (comma-separated values) format.
 * <p>
 * It's up to you if you use this program as your base, however, it's very important to strictly follow the given formatting
 * of the inputs and outputs. Your assignment will be partly automatically evaluated, therefore keep the input arguments
 * and output format identical.
 * <p>
 * Alright, I believe it's enough to stress three times the importance of the input and output formatting. Four times...
 *
 * @author tdt4300-undass@idi.ntnu.no
 */
public class Apriori {
	
	public static String result = "size;items\n";
	public static String result2 = "antecedent;consequent;confidence;support\n";
    /**
     * Loads the transaction from the ARFF file.
     *
     * @param filepath relative path to ARFF file
     * @return list of transactions as sets
     * @throws java.io.IOException signals that I/O error has occurred
     */
    public static List<SortedSet<String>> readTransactionsFromFile(String filepath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        List<String> attributeNames = new ArrayList<String>();
        List<SortedSet<String>> itemSets = new ArrayList<SortedSet<String>>();

        String line = reader.readLine();
        while (line != null) {
            if (line.contains("#") || line.length() < 2) {
                line = reader.readLine();
                continue;
            }
            if (line.contains("attribute")) {
                int startIndex = line.indexOf("'");
                if (startIndex > 0) {
                    int endIndex = line.indexOf("'", startIndex + 1);
                    attributeNames.add(line.substring(startIndex + 1, endIndex));
                }
            } else {
                SortedSet<String> is = new TreeSet<String>();
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                int attributeCounter = 0;
                String itemSet = "";
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken().trim();
                    if (token.equalsIgnoreCase("t")) {
                        String attribute = attributeNames.get(attributeCounter);
                        itemSet += attribute + ",";
                        is.add(attribute);
                    }
                    attributeCounter++;
                }
                itemSets.add(is);
            }
            line = reader.readLine();
        }
        reader.close();

        return itemSets;
    }

    /**
     * Generates the frequent itemsets given the support threshold. The results are returned in CSV format.
     *
     * @param transactions list of transactions
     * @param support      support threshold
     * @return frequent itemsets in CSV format with columns size and items; columns are semicolon-separated and items are comma-separated
     */
    
    
    public static TreeMap<String, Integer> removeUnderCurrent(TreeMap<String, Integer> map, Integer number, double supportnum ) {
    	TreeMap<String, Integer> tempmap = new TreeMap<String, Integer>();
    	for(Map.Entry<String, Integer> entry : map.entrySet()) {
        	String key = entry.getKey();
        	Integer value = entry.getValue();
        	String input = key;
        	List<String> input2 = Arrays.asList(input.split("\\s+"));
        	if (value >= supportnum) {
        		result += number + ";";
        		if (input2.size()<=1) {
        			result += key + "\n";
				}
        		else {
        			for (String temp : input2) {
            			result += temp + ",";
            		}
        			result = result.substring(0, result.length()-1);
        			result += "\n";
        			
				}
        		//result += number + ";" + key + "\n";
        		tempmap.put(key, value);
	  		}
        }
    	return tempmap;
	}
    
    public static Vector<String> getCandidates(TreeMap<String, Integer> newmap, int n) {
    	Vector<String> candidates=new Vector<String>();
    	Vector<String> tempCandidates = new Vector<String>();
    	String str1, str2; //strings that will be used for comparisons
        StringTokenizer st1, st2; //string tokenizers for the two itemsets being compared
    	
    	if (n==2) {
    		for(Map.Entry<String,Integer> entry : newmap.entrySet()) {
            	String key = entry.getKey();
            	candidates.add(key);
            	
            }
        	for(int i=0; i<candidates.size(); i++)
            {
                st1 = new StringTokenizer(candidates.get(i));
                str1 = st1.nextToken();
                for(int j=i+1; j<candidates.size(); j++)
                {
                    st2 = new StringTokenizer(candidates.elementAt(j));
                    str2 = st2.nextToken();
                    tempCandidates.add(str1 + " " + str2);
                }
            }
		}
    	
    	else {
    		//------
    		for(Map.Entry<String,Integer> entry : newmap.entrySet()) {
            	String key = entry.getKey();
            	candidates.add(key);
            	
            }
        	//for each itemset
            for(int i=0; i<candidates.size(); i++)
            {
                //compare to the next itemset
                for(int j=i+1; j<candidates.size(); j++)
                {
                    //create the strigns
                    str1 = new String();
                    str2 = new String();
                    //create the tokenizers
                    st1 = new StringTokenizer(candidates.get(i));
                    st2 = new StringTokenizer(candidates.get(j));

                    //make a string of the first n-2 tokens of the strings
                    for(int s=0; s<n-2; s++)
                    {
                        str1 = str1 + " " + st1.nextToken();
                        str2 = str2 + " " + st2.nextToken();
                    }

                    //if they have the same n-2 tokens, add them together
                    if(str2.compareToIgnoreCase(str1)==0)
                        tempCandidates.add((str1 + " " + st1.nextToken() + " " + st2.nextToken()).trim());
                }
            }
            
        	//------
		}
    	
    	
    	//System.out.println("Generated #" + tempCandidates.size() + " candidates: " + tempCandidates);
		return tempCandidates;
		
	}
    
    public static TreeMap<String, Integer> countCandidates(Vector<String> tempCandidates, List<SortedSet<String>> transactions) {
		
    	TreeMap<String, Integer> candmap = new TreeMap<String, Integer>();
    	for (int a = 0; a < tempCandidates.size(); a++) {
    		//System.out.println(tempCandidates.get(a));
        	String input = (String) tempCandidates.get(a);
        	List<String> input2 = Arrays.asList(input.split("\\s+"));
        	//System.out.println(input2);

        	
        	//System.out.println("________________");
        	for(int i = 0; i < transactions.size(); i++) {
        		Integer count = 0;
        		boolean val = true;
        		List myList = new ArrayList(transactions.get(i));
        		//System.out.println("\nMyList: "+ myList);
    				for (int j2 = 0; j2 < input2.size(); j2++){ 
    					if (val) {
    						if (myList.toString().matches("\\[.*\\b" + input2.get(j2) + "\\b.*]")) {
    							//System.out.println("YES: " +input2.get(j2) + myList);
    							val = true;
    						} 
    	    				else {
    	    					//System.out.println("NO: " +input2.get(j2)+ transactions.get(i));
    	    					val = false;
    							break;
    						}
    					}
    					else {
    						//System.out.println("bryter...");
    						break;
    					}
    					//System.out.println("..nytur..");
  	
    				}
    				if (val) {
    					//System.out.println("Adder...\n");
    					if (candmap.get(input) != null) {
    				        candmap.put(input, candmap.get(input)+1);
    				    } else {
    				        candmap.put(input, 1);
    				    } 
    				}	      	        
        			
        	}
		}
    	
    	return candmap;
	}
    
    public static String generateFrequentItemsets(List<SortedSet<String>> transactions, double support) {
        // TODO: Generate and print frequent itemsets given the method parameters.
    	TreeMap<String, Integer> map = new TreeMap<String, Integer>();
    	TreeMap<String, Integer> newmap = new TreeMap<String, Integer>();
    	Vector<String> tempCandidates;
    	TreeMap<String, Integer> candmap;
    	   	
    	for(int i = 0; i < transactions.size(); i++) {
    		List myList = new ArrayList(transactions.get(i));
    		for (int j = 0; j < myList.size(); j++) {		
    	            if (map.get((String) myList.get(j)) != null) {
    	                map.put((String) myList.get(j), map.get(myList.get(j))+1);
    	            } else {
    	                map.put((String) myList.get(j), 1);
    	            }   	        
    			}
    	}
	    //System.out.println("Transactions: " + transactions);
	    //System.out.println("Map: " + map);
        newmap = removeUnderCurrent(map, 1, transactions.size()*support);
	    //System.out.println("Ny: " + newmap);
	    //System.out.println("__RESULT: \n" + result);
	    	
	    int i = 1;
    	while (newmap.size()>=1) {
    		tempCandidates = getCandidates (newmap,i+1);
        	candmap = countCandidates(tempCandidates, transactions);
        		//System.out.println("Counted Candidates: "+candmap);
        	newmap = removeUnderCurrent(candmap, i+1, transactions.size()*support);
    	    	//System.out.println("Removed under 3: "+newmap);
    	    	//System.out.println("__RESULT: \n" + result);
			i++;
		}

    	return result;
    	
    	//        return "size;items\n" +
//                "1;beer\n" +
//                "1;bread\n" +
//                "1;diapers\n" +
//                "1;milk\n" +
//                "2;beer,diapers\n" +
//                "2;bread,diapers\n" +
//                "2;bread,milk\n" +
//                "2;diapers,milk\n" +
//                "3;bread,diapers,milk\n";
    }
    
  

    /**
     * Generates the association rules given the support and confidence threshold. The results are returned in CSV
     * format.
     *
     * @param transactions list of transactions
     * @param support      support threshold
     * @param confidence   confidence threshold
     * @return association rules in CSV format with columns antecedent, consequent, confidence and support; columns are semicolon-separated and items are comma-separated
     */
    public static String generateAssociationRules(List<SortedSet<String>> transactions, double support, double confidence) {
        // TODO: Generate and print association rules given the method parameters.
    	TreeMap<String, Integer> map = new TreeMap<String, Integer>();
    	TreeMap<String, Integer> newmap = new TreeMap<String, Integer>();
    	TreeMap<String, Double> smap = new TreeMap<String, Double>();
    	Vector<String> tempCandidates;
    	TreeMap<String, Integer> candmap;
    	   	
    	for(int i = 0; i < transactions.size(); i++) {
    		List myList = new ArrayList(transactions.get(i));
    		for (int j = 0; j < myList.size(); j++) {		
    	            if (map.get((String) myList.get(j)) != null) {
    	                map.put((String) myList.get(j), map.get(myList.get(j))+1);
    	            } else {
    	                map.put((String) myList.get(j), 1);
    	            }   	        
    			}
    	}
    	
	    //System.out.println("Transactions: " + transactions);
	    //System.out.println("Map: " + map);
        newmap = removeUnderCurrent(map, 1, transactions.size()*support);
        for(Map.Entry<String,Integer> entry : newmap.entrySet()) {
    		  String key = entry.getKey();
    		  Integer value = entry.getValue();

    		  //System.out.println(key + " => " + value*1.0/transactions.size());
    		  smap.put(key, value*1.0/transactions.size());
    		}
	    //System.out.println("Ny: " + newmap);
	    //System.out.println("__RESULT: \n" + result);
	    	
	    int i = 2;
    	do {
    		tempCandidates = getCandidates (newmap,i);
        	candmap = countCandidates(tempCandidates, transactions);
        		//System.out.println("Counted Candidates: "+candmap);
        	newmap = removeUnderCurrent(candmap, i, transactions.size()*support);
    	    	//System.out.println("Removed under 3: " + newmap);
    	    	//System.out.println("__RESULT: \n" + result);
			i++;
			for(Map.Entry<String,Integer> entry : newmap.entrySet()) {
	    		  String key = entry.getKey();
	    		  Integer value = entry.getValue();

	    		  //System.out.println(key + " => " + value*1.0/transactions.size());
	    		  smap.put(key, value*1.0/transactions.size());
	    		}
			
		}while (tempCandidates.size()>1);
    	//System.out.println(newmap);

    	//System.out.println(smap);
    	
    	
    	
    	for(Entry<String, Double> entry : smap.entrySet()) {
    		String key = entry.getKey();
    		Double value = entry.getValue();
    		//System.out.println(key + " => " + value);
    		result2 += key + ";" + value + "\n";
    		
  		  
  		}
    	
    	return result2;
    	
//        return "antecedent;consequent;confidence;support\n" +
//                "diapers;beer;0.6;0.5\n" +
//                "beer;diapers;1.0;0.5\n" +
//                "diapers;bread;0.8;0.67\n" +
//                "bread;diapers;0.8;0.67\n" +
//                "milk;bread;0.8;0.67\n" +
//                "bread;milk;0.8;0.67\n" +
//                "milk;diapers;0.8;0.67\n" +
//                "diapers;milk;0.8;0.67\n" +
//                "diapers,milk;bread;0.75;0.5\n" +
//                "bread,milk;diapers;0.75;0.5\n" +
//                "bread,diapers;milk;0.75;0.5\n" +
//                "bread;diapers,milk;0.6;0.5\n" +
//                "milk;bread,diapers;0.6;0.5\n" +
//                "diapers;bread,milk;0.6;0.5\n";
    }

    /**
     * Main method.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // definition of the accepted command line arguments
        Options options = new Options();
        options.addOption(Option.builder("f").argName("file").desc("input file with transactions").hasArg().required(true).build());
        options.addOption(Option.builder("s").argName("support").desc("support threshold").hasArg().required(true).build());
        options.addOption(Option.builder("c").argName("confidence").desc("confidence threshold").hasArg().required(false).build());
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            // extracting filepath and support threshold from the command line arguments
            String filepath = cmd.getOptionValue("f");
            double support = Double.parseDouble(cmd.getOptionValue("s"));

            // reading transaction from the file
            List<SortedSet<String>> transactions = readTransactionsFromFile(filepath);

            if (cmd.hasOption("c")) {
                // extracting confidence threshold
                double confidence = Double.parseDouble(cmd.getOptionValue("c"));

                // printing generated association rules
                System.out.println(generateAssociationRules(transactions, support, confidence));
            } else {
                // printing generated frequent itemsets
                System.out.println(generateFrequentItemsets(transactions, support));
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.setOptionComparator(null);
            helpFormatter.printHelp("java -jar apriori.jar", options, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
