import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.lang.Integer;

public class emojiPredict{
	/* Data is a custom class that takes three things:
	 * String word - the word that is currently being looked at
	 * int total - the total amount of times that word has been seen
	 * int[] arr - an array of size 20, where each index of the array corresponds
	 			   to the amount of times that word has been seen to the labeled emoji */
	//Training data
	private static ArrayList<Data> trainArr = new ArrayList<Data>();
	//ArrayList to mark if a word has been seen or not
	private static ArrayList<String> haveSeen = new ArrayList<String>();
	//Develop Set
	private static ArrayList<String> developArr = new ArrayList<String>(12500);
	//The answers to the develop set
	private static ArrayList<Integer> developAns = new ArrayList<Integer>(12500);
	//Test Set
	private static ArrayList<String> testData = new ArrayList<String>(12500);
	//The answers to the test set
	private static ArrayList<Integer> testAns = new ArrayList<Integer>(12500);
	
	private static double[] emojiProb = new double[20];
	
	private static String temp[] = {"the","be","to","of","and","a","an","in","that","has","have","i","it","for",
					"not","on","with","as","you","at","this","because","we","them","",
					"do","can"};
	
	private static ArrayList<String> ignoreWords = new ArrayList<String>(Arrays.asList(temp));

	/**
	 * Read in all the data and separate into three sets: training, develop, and test. 
	 * @param text the bufferedreader for the text file
	 * @param label the bufferedreader for the emoji's answer file
	 * @throws IOException
	 */
	public static void getData(BufferedReader text, BufferedReader label) throws IOException{		
		String str;	String lb;
		int count = 0; 
		while(((str = text.readLine()) != null) && ((lb = label.readLine()) != null)) {
			if(count < 25000){
				str = str.toLowerCase().replaceAll("[^\\p{Alnum}]"," ");
				String[] arr = str.split(" ");
				emojiProb[Integer.parseInt(lb)]++;
				for(int i=0; i< arr.length; i++){
					String word = arr[i].replaceAll(" ", "");
					
					if(ignoreWords.contains(word))				
						continue;					
					//If the word hasn't been seen before 
					else if(!haveSeen.contains(word)){
						//Mark the word as seen
						haveSeen.add(word); 
						//Create the empty array
						int[] temp = new int[20]; 
						//Set the amount of times this emoji has been seen with this word to 1
						temp[Integer.parseInt(lb)] = 1;
						//create the data object and add it to the arr
						trainArr.add(new Data(word, 1, temp)); 
					}
					else if(haveSeen.contains(word)){
						//get the index of the word
						int j = haveSeen.indexOf(word);
						//obtain the data object
						Data d = trainArr.get(j);
						//increment the amount of times the word has been seen
						int total = d.total + 1;
						//get the array
						int[] temp = d.arr;
						//increment the amount of times the emoji has been seen with this word
						temp[Integer.parseInt(lb)]++;
						//set the object to be equal to the new data
						trainArr.set(j, new Data(word, total, temp));
					}
				}
			}
			else if(count >= 25000 && count < 37500) {
				developArr.add(str); //add the full string
				developAns.add(Integer.parseInt(lb)); //add the emoji answer
			}
			else {
				testData.add(str); //add the full string
				testAns.add(Integer.parseInt(lb)); //add the emoji answer
			}			
			count++; //counter for splitting data into sets
		}
	}
	/**
	 * Predict the most probable emoji given how often each word has 
	 * been seen with each emoji.
	 * 
	 * @param testArray The ArrayList of tweets that will have an emoji predicted
	 * @param theAnswers The predefined answers for each of these tweets
	 * @return the percent of correctly predicted emojis
	 */
	public static double predict(ArrayList<String> testArray, ArrayList<Integer> theAnswers){
		int correct = 0;
		for(int i = 0; i < testArray.size(); i++) {
			double emojiArr[] = new double[20];
			String s = testArray.get(i).replaceAll("[^\\p{Alnum}]"," ");
			String[] arr = s.toLowerCase().split(" ");
			//Calculate the highest likely emoji from word-emoji probability
			for(int j = 0; j < arr.length; j++){ 
				String word = arr[j].replaceAll(" ", "");
				if(haveSeen.contains(word)){
					Data d = trainArr.get(haveSeen.indexOf(word));
					for(int z = 0; z < 20; z++){
						emojiArr[z] += (double)(d.arr[z]+1)/(double)(d.total+1);
					}
				}
			}
			double max = 0; int maxIndex = 0; 
			for(int j = 0; j < 20; j++){
				 if(emojiArr[j]>max){			
					max = emojiArr[j];
					maxIndex = j;
				}
			}			
			if(maxIndex == theAnswers.get(i)) correct++;
		}
		//return the % of correct answers
		return (double)correct/testArray.size()*100;
		
	}
	public static void main(String args[]) throws IOException {
		BufferedReader text = new BufferedReader
				(new FileReader
				("D:/-This Pc-/Documents/Files/Eclipse Projects/EmojiPrediction/src/us_trial.txt")); 
		BufferedReader label = new BufferedReader
				(new FileReader
				("D:/-This Pc-/Documents/Files/Eclipse Projects/EmojiPrediction/src/us_trialLabel.txt")); 
		
		getData(text,label); text.close(); label.close();

		//double devCorrect = predict(developArr,developAns);
		double testCorrect = predict(testData,testAns);		
		
		for(int i = 0; i<20;i++) {
			emojiProb[i] = emojiProb[i]/25000;
		}
		//get the baseline correct % of just guessing the most common emoji
		int correct = 0;			
		for(int i = 0; i<testAns.size();i++) 
			if(0==testAns.get(i)) correct++; //0 is the most common emoji
		
		double baseline = (double)correct/testAns.size()*100;
				
		System.out.println("If you were to just guess the most common emoji, you would be correct " 
							+ baseline + "% of the time.");
		System.out.printf("This method is correct %2.3f%% of the time.\n",(testCorrect));
		
		System.out.printf("The overall percent improvement is %2.3f%%.", (testCorrect-baseline));	
	}
}
