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
	//	public int countSpam = 0;
	//	public int countHam = 0;

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
						//												scan.useDelimiter("[^a-zA-Z']+");
						scan.useDelimiter("\\s+");
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
					//					scan.close();
					//					if(file.getName().equalsIgnoreCase("ham")){
					//						countHam++;
					//					}
					//					else{
					//						countSpam++;
					//					}
				}
			}
			//
		}

		else{
			if(file.isFile()){
				Scanner scan = null;
				try {
					scan = new Scanner(file);
					//										scan.useDelimiter("[^a-zA-Z']+");
					scan.useDelimiter("\\s+");

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
			double weight = -1 + rand.nextDouble();//assigning random value between -1 and 1 to the weight
			//						double weight = 0.0;									//assigning 0 to weights
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

	public HashMap<String, Double> learn(String path, boolean stopWordCheck, int noOfIterations, double learningRate){
		File file = new File(path);

		HashMap<String,Double> weightMap = weightsAssignment(path, stopWordCheck);

		HashMap<Integer,HashMap<String,Integer>> wordHamCount = null;
		HashMap<Integer,HashMap<String,Integer>> wordSpamCount = null;
		HashMap<Integer,HashMap<String,Integer>> wordCount = null;

		File[] directories = file.listFiles();

		if(directories[0].getName().charAt(0) == '.'){

			wordHamCount = wordCountInEachFile(directories[1], stopWordCheck);
			wordSpamCount = wordCountInEachFile(directories[2], stopWordCheck);
		}
		else{
			wordHamCount = wordCountInEachFile(directories[0], stopWordCheck);
			wordSpamCount = wordCountInEachFile(directories[1], stopWordCheck);
		}

		int i = 0;
		while(i< noOfIterations){
			i++;
			for(File directory : directories){
				double target = 0;
				double output = 0;

				if(directory.getName().charAt(0) != '.'){

					if(directory.getName().equalsIgnoreCase("ham")){
						target = 1;
						wordCount = wordHamCount;
					}
					else{
						target = -1;
						wordCount = wordSpamCount;
					}
					double weight = 0.0;
					Integer count = 0;
					for(Integer fileNumber : wordCount.keySet())
					{


						//						double bias = 0.5;

						HashMap<String, Integer> wordCountForFile = wordCount.get(fileNumber); 
						//						fileNumber++;
						double sum = 0;
						for(String word: wordCountForFile.keySet()){
							if(weightMap.containsKey(word)){
								weight = weightMap.get(word);
							}
							//if(wordCountForFile.containsKey(word)){
							count = wordCountForFile.get(word);

							//							sum += bias + weight*count;
							sum += weight*count;
						}

						if(sum > 0){
							output = 1;
						}
						else{
							output = -1;
						}


						if(target != output){
							//							double weight2 = 0.0;
							for(String word : wordCountForFile.keySet()){

								//								if(weightMap.containsKey(word)){
								weight = weightMap.get(word);
								//								}
								count = wordCountForFile.get(word);


								double change = learningRate * (target - output);
								//																bias += change;
								double changeInWeight = change * count;
								double weightNew = weight + changeInWeight;
								weightMap.put(word, weightNew);
							}
						}
					}
				}
			}
		}
		return weightMap;
	}


	public double test(HashMap<String, Double> learnedWeightMap,String learningPath, String testingPath, boolean stopWordCheck){
		double success = 0;
		double total = 0;

		HashMap<Integer,HashMap<String,Integer>> wordCount = null;

		File file = new File(testingPath);
		File[] directories = file.listFiles();

		for(File directory: directories){
			Integer count = 0;
			double weight = 0.0;
			double result = 0.0;
//			String fileResult ;
			if(directory.getName().charAt(0) != '.'){
				int target;
				int output;
				if(directory.getName().equalsIgnoreCase("ham")){
					target = 1;
				}
				else{
					target = -1;
				}
				wordCount = wordCountInEachFile(directory, stopWordCheck);
				//				File[] files = directory.listFiles();
				//				int fileNumber = 0;
				//				for(File f : files){

				for(Integer fileNumber : wordCount.keySet()){

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
						output = 1;
					}
					else{
						output = -1;
					}

					if(target == output){
						success++;
					}

				}
			}
		}
		return (double)success/total;
	}


	public static void run(String learnDirectoryPath, String testDirectoryPath,boolean stopWordCheck, double learningRate,int noOfIterations){
		Perceptron p = new Perceptron();
		HashMap<String, Double> learnedWeightMap = p.learn(learnDirectoryPath, stopWordCheck, noOfIterations,learningRate);
		System.out.println(p.test(learnedWeightMap,learnDirectoryPath, testDirectoryPath, stopWordCheck));
	}

	public static void main(String[] args) {
//		String learningPath = "/Users/shobhitagarwal/Dropbox/UTD/Sem-2/Machine Learning/Project/Project 3/enron1/train";
//		String testingPath = "/Users/shobhitagarwal/Dropbox/UTD/Sem-2/Machine Learning/Project/Project 3/enron1/test";
		String learningPath =  System.getProperty("user.dir") + System.getProperty("file.separator")+args[0];
		String testingPath =  System.getProperty("user.dir") + System.getProperty("file.separator")+args[1];
		double learningRate = Double.parseDouble(args[2]);
		int iterations = Integer.parseInt(args[3]);
		boolean stopWords = false;
		if(args[4].equalsIgnoreCase("yes")){
			stopWords = true;
		}
//		Double[] learningRate = {0.1, 0.15, 0.125, 0.1, 0.09, 0.08, 0.085,0.095, 0.096, 0.097, 0.098, 0.099,0.091,0.092,0.093,0.094,0.11,0.12,0.13,0.14 };
//		int[] iterations = {34,39,38,33,39,38,36,44,102,150,200,250,37,29,64,194,18,57,34,36};
//		timer();
//		int i=0;
//		int j=0;
		timer();
//		while(i<learningRate.length && j<iterations.length){
			run(learningPath, testingPath, stopWords, learningRate, iterations);
//			i++;
//			j++;
//		}

		timer();
	}
}
