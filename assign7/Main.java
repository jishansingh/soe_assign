import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class fileData{
	BufferedReader buff;
	int lineCount=0;
	String fileString="";
	
	
	
	fileData(String path) throws Exception{
		File file=new File(path);
		buff=new BufferedReader(new FileReader(file));
	}
	String getData() throws Exception{
		if(!fileString.contentEquals("")) {
			return fileString;
		}
		String temp;
		String ans="";
		while((temp=buff.readLine())!=null) {
			
			ans=ans.concat(temp+"\n");
			lineCount+=1;
		}
		fileString=ans;
		return fileString;
	}
	int getLineCount() {
		return lineCount;
	}
}


class divideData{
	
	String fileString;
	
	HashMap<String,Integer>hMap;
	HashMap<String,Integer>operandMap;
	
	void updateOperandMap(String a,int b) {
		if(a.equals("")||a.contains("[")||a.contains("]")) {
			return;
		}
		if(operandMap.containsKey(a)) {
			int temp=operandMap.get(a);
			int r=b+temp;
			operandMap.put(a, r);
		}
		else {
			operandMap.put(a, b);
		}
	}
	void updatehMap(String a,int b) {
		if(a.equals("")||a.contains("[|]")) {
			return;
		}
		if(hMap.containsKey(a)) {
			int temp=hMap.get(a);
			int r=b+temp;
			hMap.put(a, r);
		}
		else {
			hMap.put(a, b);
		}
	}
	
	boolean getOperand(String arg) {
		boolean ans=false;
		String[] operations="< > + = -".split(" ");
		for(int i=0;i<operations.length;i++) {
			String pattern ="([A-Za-z0-9\\[\\]]+)\\s*\\"+operations[i]+"\\s*[^\\"+operations[i]+"]";
	        Pattern commentPat  = Pattern.compile(pattern, Pattern.DOTALL);
	        Matcher matchObj = commentPat.matcher(arg);
	        while(matchObj.find()) {
	        	ans=true;
	        	//System.out.println(matchObj.group());
	        	updatehMap(operations[i],1);
	        	updateOperandMap(matchObj.group(1),1);
		    }
	        pattern ="[^"+operations[i]+"]\\s*"+"\\"+operations[i]+"\\s*([A-Za-z0-9\\[\\]]+)\\s*[;,|\\)]";
	        commentPat  = Pattern.compile(pattern, Pattern.DOTALL);
	        matchObj = commentPat.matcher(arg);
	        while(matchObj.find()) {
	        	//System.out.println(matchObj.group());
	        	updateOperandMap(matchObj.group(1),1);
		    }
		}
		operations="++ -- ==".split(" ");
		for(int i=0;i<operations.length;i++) {
			String pattern ="([A-Za-z0-9\\[\\]]+)\\s*\\"+operations[i].charAt(0)+"\\"+operations[i].charAt(1);
	        Pattern commentPat  = Pattern.compile(pattern, Pattern.DOTALL);
	        Matcher matchObj = commentPat.matcher(arg);
	        while(matchObj.find()) {
	        	//System.out.println(matchObj.group());
	        	updatehMap(operations[i],1);
	        	updateOperandMap(matchObj.group(1),1);
		    }
	        pattern ="\\"+operations[i].charAt(0)+"\\"+operations[i].charAt(1)+"\\s*([A-Za-z0-9\\[\\]]+)";
	        commentPat  = Pattern.compile(pattern, Pattern.DOTALL);
	        matchObj = commentPat.matcher(arg);
	        while(matchObj.find()) {
	        	ans=true;
	        	//System.out.println(matchObj.group());
	        	updatehMap(operations[i],1);
	        	updateOperandMap(matchObj.group(1),1);
		    }
	        
		}
		return ans;
		
		
	}
	
	void getDeclaration(String arg,boolean func) {
		String pattern="(float|int|void|double)";
			if(func) {
				pattern+="\\s*(\\w+)\\s*[,]?";
				Pattern commentPat  = Pattern.compile(pattern, Pattern.DOTALL);
		        Matcher matchObj = commentPat.matcher(arg);
		        while(matchObj.find()) {
		        	//System.out.println(matchObj.group());
		        	updatehMap(matchObj.group(1),matchObj.group().contains(",")?1:0);
		        	updateOperandMap(matchObj.group(2),1);	
			    }
			}
			else {
				pattern+="\\s*(.*?)[;\\)]";
				Pattern commentPat  = Pattern.compile(pattern, Pattern.DOTALL);
		        Matcher matchObj = commentPat.matcher(arg);
		        while(matchObj.find()) {
		        	String matchgrp=matchObj.group(2);
		        	String[]arr=matchgrp.split(",");
		        	updatehMap(",",arr.length-1);
		        	String[] intDec=matchgrp.split(" |,");
		        	String[] operations="< > + = -".split(" ");
		        	for(String ab:intDec) {
		        		boolean check=true;
		        		for(int j=0;j<operations.length;j++) {
		        			if(ab.contains(operations[j])) {
			        			check=false;
				        	}
		        		}
		        		if(check) {
		        			//System.out.println(ab);
		        			updateOperandMap(ab,1);
		        		}
		        		
		        		
		        	}
		        	
		        	
		        	//System.out.println(matchObj.group(1));
		        	updatehMap(matchObj.group(1),1);
		        	//updateOperandMap(matchObj.group(2),1);	
			    }
				
			}
		
	}
	
	void deleteUnnecessary() {
		// multi line comment
        String pattern = "/\\*.*?\\*/";
        Pattern commentPat  = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher matchObj = commentPat.matcher(fileString);
        fileString = matchObj.replaceAll("");
        
        //line comment
        pattern = "//.*?\n";
        commentPat  = Pattern.compile(pattern, Pattern.DOTALL);
        matchObj = commentPat.matcher(fileString);
        fileString = matchObj.replaceAll("");
        
         //as string can contain word like for
		 pattern = "\".*?\"";
		 commentPat  = Pattern.compile(pattern, Pattern.DOTALL);
		 matchObj = commentPat.matcher(fileString);
		 fileString = matchObj.replaceAll("");
        
		 //functions will as cause problem in calc of brackets (
		 String dataTypes="(float|int|void|double)";
		 pattern = dataTypes+"\\s*\\w+\\((.*?)\\)";
		 commentPat  = Pattern.compile(pattern, Pattern.DOTALL);
	     matchObj = commentPat.matcher(fileString);
	     
	     while(matchObj.find()) {
	    	 getDeclaration(matchObj.group(2),true);
	        	//updateOperandMap(matchObj.group(),1);
		    }
	     
	     fileString = matchObj.replaceAll("");
		 
        return;
    }
	
	
	void getBracketCount() {
		String[] operations="\\( \\) \\{ \\} ; (\\w+)\\[\\s*(\\w*)\\]".split(" ");
		for(int i=0;i<operations.length-1;i++) {
			String pattern =operations[i];
	        Pattern commentPat  = Pattern.compile(pattern, Pattern.DOTALL);
	        Matcher matchObj = commentPat.matcher(fileString);
	        while(matchObj.find()) {
	        	//System.out.println(matchObj.group());
	        	//updatehMap(operations[i].substring(1),1);
	        	updatehMap(matchObj.group(),1);
		    }
		}
		String pattern =operations[operations.length-1];
        Pattern commentPat  = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher matchObj = commentPat.matcher(fileString);
        while(matchObj.find()) {
        	//System.out.println(matchObj.group(2));
        	updatehMap("[]",1);
        	updateOperandMap(matchObj.group(1),1);
        	updateOperandMap(matchObj.group(2),1);
	    }
		
	}
	
	int getCountMatch(Matcher matchObj) {
		int a=0;
		while(matchObj.find()){
			//matchObj.group();
			a+=1;
		}
		return a;
	}
	
	void calcOperators() {
		//simple words operator
		String[] pattern = "for\\s*\\(.*?\\) if\\s*\\(.*?\\) return while\\s*\\(.*\\)".split(" ");
		Pattern commentPat  = Pattern.compile("("+ pattern[0]+")", Pattern.DOTALL);
		Matcher matchObj = commentPat.matcher(fileString);
		updatehMap("for",getCountMatch(matchObj));
		
//		commentPat  = Pattern.compile("("+ pattern[1]+")", Pattern.DOTALL);
//		matchObj = commentPat.matcher(fileString);
//		updatehMap("int",getCountMatch(matchObj));
		
		commentPat  = Pattern.compile("("+ pattern[1]+")", Pattern.DOTALL);
		matchObj = commentPat.matcher(fileString);
		updatehMap("if",getCountMatch(matchObj));
		
		
		commentPat  = Pattern.compile("("+ pattern[2]+")", Pattern.DOTALL);
		matchObj = commentPat.matcher(fileString);
		updatehMap("return",getCountMatch(matchObj));
		
		commentPat  = Pattern.compile("("+ pattern[3]+")", Pattern.DOTALL);
		matchObj = commentPat.matcher(fileString);
		updatehMap("while",getCountMatch(matchObj));
		
	}
	
	
	divideData(String path){
		try {
			fileData filedata=new fileData(path);
			fileString=filedata.getData();
			
		}
		catch(Exception e) {
			System.out.println("unable to open file at " + path);
			System.exit(0);
		}
		hMap=new HashMap<String,Integer>();
		operandMap=new HashMap<String,Integer>();
		deleteUnnecessary();
		getDeclaration(fileString,false);
		calcOperators();
		getOperand(fileString);
		getBracketCount();
	}
	
	static String padData(String arg) {
		String temp=" ".repeat(16-arg.length())+arg;
		return "|"+temp;
	}
	static void printLine() {
		System.out.println("-".repeat(16*3+4));
	}
	
	void printTable() {
		String temp=hMap.toString();
		String[] arr=temp.substring(1, temp.length()-1).split(", ");
		printLine();
		System.out.println(padData("S No")+padData("Operators")+padData("Count")+"|");
		int count_hMap=hMap.size();
		int total_hmap=0;
		for(int i=0;i<arr.length;i++) {
			String sop=arr[i];
			int index=sop.lastIndexOf('=');
			String key=sop.substring(0, index);
			String value=sop.substring(index+1,sop.length());
			System.out.println(padData(Integer.toString(i+1))+padData(key)+padData(value)+"|");
			total_hmap+=Integer.parseInt(value);
		}
		printLine();
		System.out.println();
		System.out.println();
		
		temp=operandMap.toString();
		arr=temp.substring(1, temp.length()-1).split(", ");
		int count_operand=operandMap.size();
		int total_operand=0;
		printLine();
		
		System.out.println(padData("S No")+padData("Operand")+padData("Count")+"|");
		
		for(int i=0;i<arr.length;i++) {
			String sop=arr[i];
			int index=sop.lastIndexOf('=');
			String key=sop.substring(0, index);
			String value=sop.substring(index+1,sop.length());
			System.out.println(padData(Integer.toString(i+1))+padData(key)+padData(value)+"|");
			total_operand+=Integer.parseInt(value);
		}
		printLine();
		System.out.println();
		System.out.println();
		printLine();
		System.out.println(padData("")+padData("Total")+padData("Unique")+"|");
		
		System.out.println(padData("Operators")+padData("N1="+Integer.toString(total_hmap))+padData("n1="+Integer.toString(count_hMap))+"|");
		System.out.println(padData("Operand")+padData("N2="+Integer.toString(total_operand))+padData("n2="+Integer.toString(count_operand))+"|");
		printLine();
	}
	
	void getData() {
		Set<Entry<String ,Integer> > e = operandMap.entrySet(); 
		Iterator<Entry<String ,Integer>> iterator = e.iterator();
		
//		while (iterator.hasNext()) {
//        	System.out.println( iterator.next().getKey() +" "+iterator.next().getValue());
//        }
		System.out.println(hMap.toString());
		System.out.println(operandMap.toString());
	}
	
}


public class Main {
	public static void main(String args[]) {
		divideData f=new divideData("input.c");
		
		f.printTable();
	}
}

