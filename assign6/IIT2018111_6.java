import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

class fileData{
	BufferedReader buff;
	int lineCount=0;
	fileData(String path) throws Exception{
		File file=new File(path);
		buff=new BufferedReader(new FileReader(file));
	}
	String getData() throws Exception{
		String temp;
		String ans="";
		while((temp=buff.readLine())!=null) {
			ans=ans.concat(temp+"\n");
			lineCount+=1;
		}
		return ans;
	}
	int getLineCount() {
		return lineCount;
	}
}

class Node{
	String name;
	int startTime=0;
	int endTime=0;
	Node predecessor=null;
	boolean critical=false;
	Node(String data,HashMap<String,Node>activityPred){
		String[] splitData=data.split("[ |,]+");
			name=splitData[0];
			//String[] splitPred=splitData[1].split(",");
			if(!splitData[1].equals("-")) {
				Node temp;
				for(int i=1;i<splitData.length-1;i++) {
					temp=activityPred.get(splitData[i]);
					if(predecessor!=null&&temp.getEndTime()>predecessor.getEndTime()) {
						predecessor=temp;
					}
					else if(predecessor==null) {
						predecessor=temp;
					}
				}
				startTime=predecessor.getEndTime();
			}
			int time=Integer.parseInt(splitData[splitData.length-1]);
			endTime=startTime+time;
			activityPred.put(name, this);
		}
	int getEndTime() {
		return endTime;
	}
	static String padData(String arg) {
		String temp=" ".repeat(16-arg.length())+arg;
		return temp;
	}
	String getData() {
		String temp="";
		temp=padData(name)+padData(Integer.toString(startTime))+padData(Integer.toString(endTime));
		if(critical) {
			temp=temp+padData("*");
		}
		return temp;
	}
	void setCritical(boolean t) {
		critical=t;
		if(predecessor!=null) {
			predecessor.setCritical(t);
		}
	}
	String getCriticalPath() {
		if(predecessor==null) {
			return name;
		}
		else {
			String temp= predecessor.getCriticalPath() + "->"+ name;
			return temp;
		}
	}
}


class newFile {
  newFile(String filename,String data) {
    try {
    	File myObj = new File(filename);
    	if (myObj.createNewFile()) {
    		System.out.println("File created: " + myObj.getName());
    	}
      else {
        System.out.println("File already exists "+filename);
      }
    }
    catch (IOException e) {
      System.out.println("An error occurred.creating file "+filename);
    }
    
    try {
        FileWriter myWriter = new FileWriter(filename);
        myWriter.write(data);
        myWriter.close();
        System.out.println("Successfully wrote to the file "+filename);
      } catch (IOException e) {
        System.out.println("An error occurred while writing to "+filename);
      }
    
  }
}



public class IIT2018111_6 {
	
	static String printData(HashMap<String,Node>activityPred) {
		String temp="";
		Set<Entry<String ,Node> > e = activityPred.entrySet(); 
		Iterator<Entry<String ,Node>> iterator = e.iterator();
		
		temp+=Node.padData("Activity")+Node.padData("Start Time")+Node.padData("Completion Time")+Node.padData("Critical Path\n");
		while (iterator.hasNext()) {
        	temp=temp.concat(iterator.next().getValue().getData()+"\n");
        }
		return temp;
	}
	
	public static void main(String args[]) {
		HashMap<String,Node>activityPred=new HashMap<String,Node>();
		//HashMap<String,Integer>activityTime;
		fileData fin;
		String[] fileData;
		try {
			fin=new fileData("input.txt");
			fileData=fin.getData().split("\n");
		}
		catch(Exception e) {
			System.out.println("unable to read from file");
			return;
		}
		
		
		Node root=null;
		for(int i=0;i<fin.getLineCount();i++) {
			Node som=new Node(fileData[i],activityPred);
			if(root==null) {
				root=som;
			}
			else if(som.getEndTime()>root.getEndTime()) {
				root=som;
			}
		}
		root.setCritical(true);
		String FileData=printData(activityPred);

		FileData+="Critical Path :->\n";
		FileData += root.getCriticalPath();
		newFile sop=new newFile("output.txt",FileData);
	}
}

