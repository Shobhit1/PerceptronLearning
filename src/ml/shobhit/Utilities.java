package ml.shobhit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


public class Utilities {
	
	static String stopWordsPath = System.getProperty("user.dir") + System.getProperty("file.separator")+ "stopWords.txt";
	public HashMap<String, HashMap<String,Integer>> tokenHashMap = new HashMap<>();
	public int countSpam = 0;
	public int countHam = 0;
	
	/**
	 * Function to calculate the vocab from all the files whether
	 * 
	 * Spam or Ham
	 */
	
	public Set<String> makeVocab(String directoryPath, boolean stopWordCheck){

		Set<String> vocab = new HashSet<>();
		HashMap<String, Integer> mapForWordCount = null;
		for(File classFile : new File(directoryPath).listFiles()){
			if(classFile.getName().charAt(0) != '.'){

				mapForWordCount = wordCount(classFile, stopWordCheck);
				tokenHashMap.put(classFile.getName(), mapForWordCount);
			}
		}

		for(String s  : tokenHashMap.keySet()){

			Set<String> temp = tokenHashMap.get(s).keySet();

			vocab.addAll(temp);
		}

		return vocab;
	}
	
	
	/**
	 * Function to calculate word count in each file and give a map for it.
	 * 
	 * Reads each file and puts all the words in the file with the count
	 * 
	 * Takes a parameter for stop words, so as to decide whether to consider them or not
	 * 
	 */
	
	public HashMap<String, Integer> wordCount(File file, boolean stopWordCheck){

		int totalWordsInClass = 0;
		HashMap<String, Integer> mapForWordCount = new HashMap<String, Integer>();

		//adding stop words concept - to be removed later to make it in a different function
		ArrayList<String> stopWords = stopWords(stopWordsPath);

		File[] files = file.listFiles();
		if(files != null){
			for(File f : files){
				if(f.isFile()){
					Scanner scan = null;
					try {
						scan = new Scanner(f);
						scan.useDelimiter("[^a-zA-Z']+");
						while(scan.hasNext()){
							String word = scan.next();
							if(stopWordCheck){
								if(!stopWords.contains(word)){			//checking for inclusion of stop words or not
									totalWordsInClass++;

									if(mapForWordCount.containsKey(word)){
										mapForWordCount.put(word,mapForWordCount.get(word) + 1);
									}
									else{
										mapForWordCount.put(word, 1);
									}
								}
							}
							//						}
							else{
								totalWordsInClass++;

								if(mapForWordCount.containsKey(word)){
									mapForWordCount.put(word,mapForWordCount.get(word) + 1);
								}
								else{
									mapForWordCount.put(word, 1);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					scan.close();
					if(file.getName().equalsIgnoreCase("ham")){
						countHam++;
					}
					else{
						countSpam++;
					}
				}
			}
			//
		}

		else{
			if(file.isFile()){
				Scanner scan = null;
				try {
					scan = new Scanner(file);
					scan.useDelimiter("[^a-zA-Z']+");

					while(scan.hasNext()){
						String word = scan.next();
						if(stopWordCheck){
							if(!stopWords.contains(word)){			//stop words
								totalWordsInClass++;

								if(mapForWordCount.containsKey(word)){
									mapForWordCount.put(word,mapForWordCount.get(word) + 1);
								}
								else{
									mapForWordCount.put(word, 1);
								}
							}
						}
						//						}
						else{
							totalWordsInClass++;

							if(mapForWordCount.containsKey(word)){
								mapForWordCount.put(word,mapForWordCount.get(word) + 1);
							}
							else{
								mapForWordCount.put(word, 1);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				//				if(file.getName().equalsIgnoreCase("ham")){
				//					countHam++;
				//				}
				//				else{
				//					countSpam++;
				//				}
			}
		}
		mapForWordCount.put("totalWordsInClass", totalWordsInClass);
		return mapForWordCount;
	}
	
	
	
	/**
	 * to calculate an array list containing all the stop words
	 * @param path
	 * @return
	 */
	public ArrayList<String> stopWords(String path){
		ArrayList<String> stopWords = new ArrayList<String>();

		Scanner scan = null;
		try {
			File file = new File(path);
			scan = new Scanner(file);
			scan.useDelimiter("\n");
			while(scan.hasNext()){
				String stopWord = scan.next();
				stopWords.add(stopWord);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		scan.close();
		return stopWords;
	}
	
	/**
	 * Function to  update the weights of the words
	 * 
	 */
	public HashMap<String,Double> weightsAssignment(String path,boolean stopWordCheck){
		HashMap<String, Double> weightMap = new HashMap<String, Double>();
		Random rand = new Random();
		Set<String> vocab = makeVocab(path,stopWordCheck);
		//		System.out.println(vocab.size());
		for(String word : vocab){
//			double weight = 0 + (rand.nextDouble()*(1+1));		//assigning random value between -1 and 1 to the weight
			double weight = 0.0;									//assigning 0 to weights
			weightMap.put(word, weight);
		}

		return weightMap;

	}
	
	/**
	 * HashMap that returns the count of all the words of all files 
	 * @param file
	 * @return
	 */

	public HashMap<Integer,HashMap<String,Integer>> wordCountInEachFile(File file, boolean stopWordCheck){
		int fileCount = 0;
		HashMap<Integer,HashMap<String,Integer>> wordCountInAllFiles = new HashMap<Integer, HashMap<String,Integer>>();
		for(File f : file.listFiles()){
			HashMap<String,Integer> wordCount = wordCount(f, stopWordCheck);
			wordCountInAllFiles.put(fileCount, wordCount);
			fileCount++;
		}

		return wordCountInAllFiles;
	}
	
	/***
	 * Learning using perceptron algorithm.
	 * 
	 * Need to check for each file whether the weights need to be updated or not
	 * 
	 * For each file, the actual output should be equal to the desired output and till then the weights should be updated
	 * 
	 * and the weights of only those words will be updated which are in the file.
	 * 
	 * At the end of the files, we will get the weight map with correct weights and that will be used to predict the output
	 * on test set
	 */
	
	//undone right now. needs to be done
	public HashMap<String,Double> weightLearn(String path, boolean stopWordCheck){
		
		double learningRate = 0.1;
		
		HashMap<String, Double> weightMap = weightsAssignment(path, stopWordCheck);
		
		HashMap<Integer,HashMap<String,Integer>> wordHamCount = null;
		HashMap<Integer,HashMap<String,Integer>> wordSpamCount = null;
		
		File directory = new File(path);
		File[] files = directory.listFiles();

		if(files[0].getName().charAt(0) == '.'){

			wordHamCount = wordCountInEachFile(files[1], stopWordCheck);
			wordSpamCount = wordCountInEachFile(files[2], stopWordCheck);
		}
		else{
			wordHamCount = wordCountInEachFile(files[0], stopWordCheck);
			wordSpamCount = wordCountInEachFile(files[1], stopWordCheck);
		}
		
		//need to find the actual output and give it some number based on it.
		
		return weightMap;
	}
	
	
	
	public static void main(String[] args) {
		String path = "/Users/shobhitagarwal/Dropbox/UTD/Sem-2/Machine Learning/Project/Project 3/train";
		HashMap<String,Double> weightsAssg = new Utilities().weightsAssignment(path, true);
		
		
	}
}
