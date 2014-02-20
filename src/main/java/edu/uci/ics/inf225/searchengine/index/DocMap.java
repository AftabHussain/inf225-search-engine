package edu.uci.ics.inf225.searchengine.index;

import java.util.ArrayList;
import java.util.List;

public class DocMap {
	
	private Integer _termFrequency=0;
	private float _tfidf=0;
	List<Integer> positions= new ArrayList<Integer>();
	
	public DocMap(Integer termFrequency, List<Integer> positions){
		this._termFrequency=termFrequency;
		this.positions=positions;				
	}
	
	public void settermFrequency(Integer termFrequency)
	{
		_termFrequency=termFrequency;
	}
	
	public void settfidf(float tfidf)
	{
		_tfidf=tfidf;
	}
	
	public void setpositions()
	{
		
	}
	
	public Integer gettermFrequency(){
		return _termFrequency;
	}
	
	public float gettfidf(){
		return _tfidf;
	}
	
	public List<Integer> getpositions(){
		return positions;
	}
	
	public String toString() {
		String str="";
		
		for(Integer position : positions) {
            str= str + "," + position.toString();
        }
		return _termFrequency + "," + _tfidf + "{" + str + "}";
	}
}
