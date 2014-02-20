package edu.uci.ics.inf225.searchengine.similarity;
import java.util.ArrayList;


public class DuplicateFilter {
	// Creates SimHash object.
			
	static ArrayList<String> uniquePageList=new ArrayList<String>();
			
			public static void main(String args[]){
				DuplicateFilter df =new DuplicateFilter();
			}
			public  boolean isDuplicate(String testpage){
				boolean duplicate=false;
				Simhash simHash = new Simhash(new BinaryWordSeg());
				long docHash0 = simHash.simhash64(testpage);
				for (int i=0;i<uniquePageList.size();i++ ){
					long docHash1 = simHash.simhash64(uniquePageList.get(i));
					int dist = simHash.hammingDistance(docHash0, docHash1);
					System.out.println(dist);
					if (dist<15){
						duplicate=true;
						break;
					}
				}
				if(duplicate==false){
					uniquePageList.add(testpage);
				}
				
				return duplicate;
			}
	
	
	
	
}
