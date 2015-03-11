package ml.shobhit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


public class Perceptron {

	static String stopWordsPath = System.getProperty("user.dir") + System.getProperty("file.separator")+ "stopWords.txt";
	public HashMap<String, HashMap<String,Integer>> tokenHashMap = new HashMap<>();
	public int countSpam = 0;
	public int countHam = 0;

	/**
	 * Function to calculate the vocab from all the files whether
	 * 
	 * Spam or Ham
	 */

	private static int phase = 0;
	private static long startTime, endTime, elapsedTime;

	public static void timer()
	{
		if(phase == 0) {
			startTime = System.currentTimeMillis();
			phase = 1;
		} else {
			endTime = System.currentTimeMillis();
			elapsedTime = endTime-startTime;
			System.out.println("Time: " + elapsedTime + " msec.");
			//			memory();
			phase = 0;
		}
	}
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
			double weight = (rand.nextDouble());		//assigning random value between -1 and 1 to the weight
			//			double weight = 0.0;									//assigning 0 to weights
			weightMap.put(word, weight);
		}

		return weightMap;

	}


	/**
	 * Extracting all the words in a file and returning an arrayList of the words
	 * @param file
	 * @return
	 */


	public ArrayList<String> wordsInFile(File file, boolean stopWordCheck){
		ArrayList<String> wordsInFile = new ArrayList<String>();
		ArrayList<String> stopWords = stopWords(stopWordsPath);			//stop words
		Scanner scan = null;
		try {
			scan = new Scanner(file);
			scan.useDelimiter("[^a-zA-Z']+");
			while(scan.hasNext()){
				String word = scan.next();
				if (stopWordCheck){
					if(!stopWords.contains(word)){//stop words
						wordsInFile.add(word);
					}
				}
				else{
					wordsInFile.add(word);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		scan.close();

		return wordsInFile;
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

	public HashMap<String, Double> learn(String path, boolean stopWordCheck){
		File file = new File(path);
		HashMap<String,Double> weightMap = weightsAssignment(path, stopWordCheck);
		HashMap<Integer,HashMap<String,Integer>> wordCount = null;

		File[] directories = file.listFiles();

		int target = 0;
		int output = 0;
		double learningRate = 0.1;


		for(File directory : directories){
			if(directory.getName().charAt(0) != '.'){
				wordCount = wordCountInEachFile(directory, stopWordCheck);
				if(directory.getName().equals("ham")){
					target = 1;
				}
				else{
					target = -1;
				}

				File[] files = directory.listFiles();

				for(int i=0; i< 10000; i++){
					//					int fileNumber = 0;

					//					for(File f : files)
					for(int fileNumber = 0; fileNumber < files.length; fileNumber++)
					{
						double sum = 0;
						double weight = 0.0;
						Integer count = 0;
						double bias = 0.5;
						//						ArrayList<String> words = wordsInFile(f, stopWordCheck);

						HashMap<String, Integer> wordCountForFile = wordCount.get(fileNumber); 
						Set<String> words = wordCountForFile.keySet();
						fileNumber++;

						for(String word: words){
							if(weightMap.containsKey(word)){
								weight = weightMap.get(word);
							}
							if(wordCountForFile.containsKey(word)){
								count = wordCountForFile.get(word);
							}

							if(count == null){
								count = 0;
							}
							sum += bias + weight*count;
						}

						if(sum > 0){
							output = 1;
						}
						else{
							output = -1;
						}
						
						
						if(target != output){
							double weight2 = 0.0;
							for(String word : words){

								if(weightMap.containsKey(word)){
									weight2 = weightMap.get(word);
								}

								if(wordCountForFile.containsKey(word)){
									count = wordCountForFile.get(word);
								}

								if(count == null){
									count = 0;
								}
								double change = learningRate * (target - output);
								bias += change;
								double changeInWeight = change * count;
								double weightNew = weight2 + changeInWeight;
								weightMap.put(word, weightNew);
							}
						}
					}
				}
			}
		}
		return weightMap;
	}


	public double test(String learningPath, String testingPath, boolean stopWordCheck){
		int success = 0;
		int total = 0;
		Integer count = 0;
		double weight = 0.0;

		String fileResult = "";
		HashMap<String, Double> learnedWeightMap = learn(learningPath, stopWordCheck);
		HashMap<Integer,HashMap<String,Integer>> wordCount = null;

		File file = new File(testingPath);
		File[] directories = file.listFiles();

		for(File directory: directories){
			if(directory.getName().charAt(0) != '.'){
				wordCount = wordCountInEachFile(directory, stopWordCheck);
				File[] files = directory.listFiles();
				//				int fileNumber = 0;
				//				for(File f : files){
				for(int fileNumber=0; fileNumber < files.length; fileNumber++){
					double result = 0.0;
					total++;
					//					ArrayList<String> words = wordsInFile(f, stopWordCheck);

					HashMap<String, Integer> wordCountForFile = wordCount.get(fileNumber);
					Set<String> words = wordCountForFile.keySet();
					//					fileNumber++;

					for(String word: words){
						if(learnedWeightMap.containsKey(word)){
							weight = learnedWeightMap.get(word);
						}
						if(wordCountForFile.containsKey(word)){
							count = wordCountForFile.get(word);
						}

						if(count == null){
							count = 0;
						}
						result += weight*count;
					}

					if(result < 0){
						fileResult = "ham";
					}
					else{
						fileResult = "spam";
					}

					if(fileResult.equals(directory.getName())){
						success++;
						//						System.out.println(success + " success Count");
					}

				}
			}
		}
		return (double)success/total;
	}

	public static void main(String[] args) {
		String learningPath = "/Users/shobhitagarwal/Dropbox/UTD/Sem-2/Machine Learning/Project/Project 3/enron1/train";
		String testingPath = "/Users/shobhitagarwal/Dropbox/UTD/Sem-2/Machine Learning/Project/Project 3/enron1/test";
		Perceptron p = new Perceptron();
		timer();
		System.out.println(p.test(learningPath, testingPath, false));
		timer();
		//		System.out.println(weightsAssg.toString());
		System.out.println("-----------");
		//		System.out.println(weightLearn.toString());
	}
}
