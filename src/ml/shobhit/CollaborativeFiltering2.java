package ml.shobhit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class CollaborativeFiltering2 {

	private static int phase = 0;
	private static long startTime, endTime, elapsedTime;
	private static String path = "/Users/shobhitagarwal/Dropbox/UTD/Sem-2/Machine Learning/Project/Project 3/netflix/TrainingRatings.txt";
	private static String pathTest = "/Users/shobhitagarwal/Dropbox/UTD/Sem-2/Machine Learning/Project/Project 3/netflix/TestingRatings Copy.txt";

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

	public HashMap<String, HashMap<String,Double>> dataBasedOnUser(String path){
		HashMap<String, HashMap<String, Double>> data = new HashMap<String, HashMap<String,Double>>();

		Scanner scan = null;
		try {
			File file = new File(path);
			scan = new Scanner(file);
			scan.useDelimiter("\n");

			while(scan.hasNext()){
				String[] line = scan.nextLine().split(",");
				String movie = line[0];
				String user = line[1];
				double rating = Double.parseDouble(line[2]);

				if(data.containsKey(user)){
					data.get(user).put(movie, rating);
				}
				else{
					HashMap<String, Double> temp = new HashMap<String, Double>();
					temp.put(movie, rating);
					data.put(user, temp);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		scan.close();
		return data;
	}

	public HashMap<String,Double> meanVoteMap(HashMap<String,HashMap<String,Double>> data){
		HashMap<String,Double> meanVoteMap = new HashMap<String, Double>();
		Set<String> users = data.keySet();

		for(String user : users){
			HashMap<String, Double> movieDataForUser = data.get(user);
			double totalRatings = movieDataForUser.size();
			double sum = 0.0;
			Set<String> movies = movieDataForUser.keySet();
			for(String movie: movies){
				sum += movieDataForUser.get(movie);
			}

			double meanVote = (double) sum/totalRatings;
			meanVoteMap.put(user, meanVote);
		}

		return meanVoteMap;
	}

	/*
	 * Function that gives us a list of common items by movie Id.
	 * 
	 * Need this when we want to find the users who have voted for a particular movie
	 * V(i,j)
	 */


	public ArrayList<String> listOfUserVotingForParticularMovie(HashMap<String, HashMap<String, Double>> data,String movieId){
		ArrayList<String> users = new ArrayList<String>();
		Set<String> usersFromMap = data.keySet();

		for(String user : usersFromMap){
			if(data.get(user).keySet().contains(movieId)){
				users.add(user);
			}
		}

		return users;
	}

	/**
	 * Co-Relation between two users
	 * @param args
	 */
	public double coRelationBetweenUsers(HashMap<String, HashMap<String,Double>> data, HashMap<String,Double> meanVoteMap,String userA, String userI){

		HashMap<String, Double> mapForUserA = data.get(userA);
		HashMap<String, Double> mapForUserI = data.get(userI);

//		HashMap<String,Double> meanVoteMap = meanVoteMap(data);

		double meanVoteUserA = meanVoteMap.get(userA);
		double meanVoteUserI = meanVoteMap.get(userI);

		double sumNumerator = 0;
		double sumDenom1 = 0;
		double sumDenom2 = 0; 
		double result = 0;

		Set<String> moviesUserA = mapForUserA.keySet();
		Set<String> moviesUserI = mapForUserI.keySet();
		for(String movieA : moviesUserA){
			for(String movieI : moviesUserI){
				if(movieA.equals(movieA)){
					double vaj = mapForUserA.get(movieA);
					double vij = mapForUserI.get(movieI);
					sumNumerator += ((vaj - meanVoteUserA) * (vij - meanVoteUserI));
					sumDenom1 += Math.pow((vaj - meanVoteUserA), 2);
					sumDenom2 += Math.pow((vij - meanVoteUserI), 2);

				}
			}
		}

		if(sumDenom1 == 0 || sumDenom2 == 0){
			result = 0;
		}
		else{
			result = sumNumerator/Math.sqrt((sumDenom1 * sumDenom2));
		}
		return result;
	}
	
	/*
	 * Prediction Function
	 */
	
	public double predictRating(HashMap<String, HashMap<String, Double>> data,HashMap<String,Double> meanVoteMap, MovieBean bean){
		String userPredicting = bean.getUserId();
		String movieToBePredicted = bean.getMovieId();
		
		double k = 0.1;
		double sum = 0.0;
		
//		HashMap<String,Double> meanVoteMap = meanVoteMap(data);
		double meanRatingForUserPredicting = meanVoteMap.get(userPredicting);
		
		ArrayList<String> usersForMovie = listOfUserVotingForParticularMovie(data, movieToBePredicted);
		
		double weightCorelation = 0.0;
		
		for(String user : usersForMovie){
			weightCorelation = coRelationBetweenUsers(data, meanVoteMap, userPredicting, user);
			weightCorelation = weightCorelation * (data.get(user).get(movieToBePredicted) - meanVoteMap.get(user));
			
			sum += weightCorelation;
		}
		
		return (meanRatingForUserPredicting + (k*sum));
	}
	
	
	/*
	 * Reading Test data as bean
	 */
	
	public ArrayList<MovieBean> testDataRead(String testPath){
		ArrayList<MovieBean> movieRatingList = new ArrayList<MovieBean>();
		Scanner scan = null;
		try {
			File file = new File(testPath);
			scan = new Scanner(file);
			scan.useDelimiter("\n");
			while(scan.hasNext()){
				String line = scan.nextLine();
				String[] tokens = line.split(",");
				MovieBean bean = new MovieBean(tokens[0], tokens[1], Double.parseDouble(tokens[2]));
				movieRatingList.add(bean);
			}

		} catch (Exception e) {

		}
		scan.close();
		return movieRatingList;
	}
	
	/**
	 * Accuracy Function
	 * 
	 */
	
	public double testing(HashMap<String, HashMap<String,Double>> trainingData, String testingPath){
		HashMap<String, Double> meanVoteMapTrainData = meanVoteMap(trainingData);
//		HashMap<String, HashMap<String, Double>> testData = dataBasedOnUser(testingPath);
		
		ArrayList<MovieBean> testDataAsBeans = testDataRead(testingPath);
		double sum = 0;
		
//		Set<String> usersForTesting = testData.keySet();
		
		for(MovieBean bean: testDataAsBeans){
			double ratingNow = bean.getRating();
			double predictedRating = predictRating(trainingData, meanVoteMapTrainData, bean);
			System.out.println("pred " + predictedRating);
			System.out.println("Rating " + ratingNow);
			sum += Math.abs(predictedRating - (ratingNow));
			
		}
		
//		System.out.println(testDataAsBeans.size());
		double MAE = sum/testDataAsBeans.size();

		double RMSD = Math.sqrt(MAE);

		System.out.println("MAE :  " + MAE);
		System.out.println("RMSD : " + RMSD);

		return RMSD;

	}
	
	
	
	public static void main(String[] args) {
		CollaborativeFiltering2 cb = new CollaborativeFiltering2();
		timer();
		HashMap<String, HashMap<String,Double>> data = cb.dataBasedOnUser(path);
//		System.out.println(cb.meanVoteMap(data));
//		System.out.println(cb.listOfUserVotingForParticularMovie(data, "8"));
		System.out.println(cb.testing(data, pathTest));
		timer();
	}
}
