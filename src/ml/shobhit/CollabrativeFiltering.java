package ml.shobhit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public class CollabrativeFiltering {

//	static Set<String> users = new HashSet<String>();
	static HashMap<String, Double> mapForStoringMeanVotePerUser = new HashMap<String, Double>();
	static String path = "/Users/shobhitagarwal/Dropbox/UTD/Sem-2/Machine Learning/Project/Project 3/netflix/TrainingRatings.txt";
	static String pathTest = "/Users/shobhitagarwal/Dropbox/UTD/Sem-2/Machine Learning/Project/Project 3/netflix/TestingRatings copy.txt";

	public ArrayList<MovieBean> dataRead(String path){
		ArrayList<MovieBean> movieRatingList = new ArrayList<MovieBean>();
		Scanner scan = null;
		try {
			File file = new File(path);
			scan = new Scanner(file);
			scan.useDelimiter("\n");
			//			int count = 0;
			while(scan.hasNext()){
				//								count++;
				String line = scan.nextLine();
				String[] tokens = line.split(",");
				MovieBean bean = new MovieBean(tokens[0], tokens[1], Double.parseDouble(tokens[2]));
				movieRatingList.add(bean);
//				users.add(tokens[1]);
				//				if(file.getName().equals("TestingRatings.txt")){
				//					if (count == 5000){			//counter to include the number of records for testing
				//						break;
				//					}
				//				}
			}

		} catch (Exception e) {

		}
		scan.close();
		return movieRatingList;

	}

	public double meanVoteForUser(ArrayList<MovieBean> ratingsData, String user){

//		Collections.sort(ratingsData);

		int firstOccurenceInList = ratingsData.indexOf(new MovieBean(user));
		int lastOccurenceInList = ratingsData.lastIndexOf(new MovieBean(user));

		if(firstOccurenceInList == -1 && lastOccurenceInList == -1){
			return 0;
		}

		double noOfItemsForEachUser = lastOccurenceInList - firstOccurenceInList + 1;
		double sumOfRatings = 0.0;

		for(int i= firstOccurenceInList; i<= lastOccurenceInList ; i++){
			sumOfRatings += ratingsData.get(i).getRating();
		}

		double meanVote = sumOfRatings / noOfItemsForEachUser;
		mapForStoringMeanVotePerUser.put(user, meanVote);
		return meanVote;
	}

	/*
	 * To be used when we want to find the no of ratings given by a particular user
	 */
	public List<MovieBean> listOfItemsForParticularUser(ArrayList<MovieBean> ratingsData, String user){

//		Collections.sort(ratingsData);	//doesn't work without sorting, cant find the reason!!

		int firstOccurenceInList = ratingsData.indexOf(new MovieBean(user));

		int lastOccurenceInList = ratingsData.lastIndexOf(new MovieBean(user));
		/*
		if(firstOccurenceInList == -1 || lastOccurenceInList == -1){
			return ratingsData;
		}
		 */
		List<MovieBean> listOfItemsForParticularUser = ratingsData.subList(firstOccurenceInList, lastOccurenceInList+1);

		return listOfItemsForParticularUser;
	}

	/*
	 * Function that gives us a list of common items by movie Id.
	 * 
	 * Need this when we want to find the users who have voted for a particular movie
	 * V(i,j)
	 */
	public ArrayList<MovieBean> listOfUserVotingForParticularMovie(ArrayList<MovieBean> ratingsData,String movieId){
		ArrayList<MovieBean> listOfCommons = new ArrayList<MovieBean>();

		for(MovieBean bean : ratingsData){
			if (bean.getMovieId().equals(movieId)){
				listOfCommons.add(bean);
			}
		}

		return listOfCommons;
	}


	/*
	 * Get the w(a,i) part - the co relation
//	*/ 

	public double coRelationBetweenUsers(ArrayList<MovieBean> ratingsData, String user1, String user2){

		ArrayList<MovieBean> listOfItemsUser1 = new ArrayList<>(listOfItemsForParticularUser(ratingsData, user1));
		ArrayList<MovieBean> listOfItemsUser2 = new ArrayList<>(listOfItemsForParticularUser(ratingsData, user2));

//		Collections.sort(listOfItemsUser1,new MovieBean());	//sorting the data acc to movies in the lists of a particular user
//		Collections.sort(listOfItemsUser2, new MovieBean());

//		double meanVoteUser1 = meanVoteForUser(ratingsData,user1); 
		double meanVoteUser1 = mapForStoringMeanVotePerUser.get(user1);
		double meanVoteUser2 = meanVoteForUser(ratingsData,user2);

		double sumNumerator = 0;
		double sumDenom1 = 0;
		double sumDenom2 = 0; 
		double result = 0;
		
		

		for(int i=0; i<listOfItemsUser1.size();i++){
			int j =0;

			while((j < listOfItemsUser2.size()) && (listOfItemsUser1.get(i).getMovieId().compareTo(listOfItemsUser2.get(j).getMovieId()) >= 0)){
				//			for(int j=0; j<listOfItemsUser2.size();j++){	
				String movie1 = listOfItemsUser1.get(i).getMovieId();
				String movie2 = listOfItemsUser2.get(j).getMovieId();
				if(movie1.equals(movie2)){
					sumNumerator += (listOfItemsUser1.get(i).getRating() - meanVoteUser1) * (listOfItemsUser2.get(j).getRating() - meanVoteUser2);
					sumDenom1 += Math.pow((listOfItemsUser1.get(i).getRating() - meanVoteUser1), 2);
					sumDenom2 += Math.pow((listOfItemsUser2.get(j).getRating() - meanVoteUser2), 2);
				}
				j++;
			}
		}
		if(sumDenom1 ==0 || sumDenom2 == 0){
			result = 0;
		}
		else{
			result = sumNumerator/Math.sqrt((sumDenom1 * sumDenom2));
		}

		return result;
	}

	/*
	 * Prediction function
	 */

	public double predictRating(ArrayList<MovieBean> ratingsData, MovieBean individualData){
		String user = individualData.getUserId();
		String movie = individualData.getMovieId();
		double k = 0.1;
		double sum = 0.0;
		double meanRatingForUser = meanVoteForUser(ratingsData, user);
		ArrayList<MovieBean> listOfUsersforMovie = listOfUserVotingForParticularMovie(ratingsData, movie);

		double weightCorelation = 0;

		for(MovieBean bean : listOfUsersforMovie){
			weightCorelation = coRelationBetweenUsers(ratingsData, user, bean.getUserId());

//			weightCorelation = weightCorelation * (bean.getRating() - meanVoteForUser(ratingsData, bean.getUserId()));
			weightCorelation = weightCorelation * (bean.getRating() - mapForStoringMeanVotePerUser.get(bean.getUserId()));

			sum += weightCorelation;
		}

		return (meanRatingForUser + (k*sum));
	}


	/*
	 * Finding accuracy on the basis of test data, using the predicted data
	 */

	public double testing(ArrayList<MovieBean> trainingData, ArrayList<MovieBean> testData){

		double sum = 0;

		for(MovieBean bean: testData){
			double predictedRating = predictRating(trainingData, bean);
			//			if(predictedRating < 0){
			//				predictedRating = 0.0;
			//			}
			System.out.println("pred " + predictedRating);
			System.out.println("Rating " + bean.getRating());
			sum += Math.abs(predictedRating - (bean.getRating()));

		}
		System.out.println(testData.size());
		double MAE = sum/testData.size();

		double RMSD = Math.sqrt(MAE);

		System.out.println("MAE :  " + MAE);
		System.out.println("RMSD : " + RMSD);

		return RMSD;

	}

	public static void main(String[] args) {

		CollabrativeFiltering c = new CollabrativeFiltering();
		ArrayList<MovieBean> ratingsData = c.dataRead(path);
		ArrayList<MovieBean> testData = c.dataRead(pathTest);

		System.out.println(c.testing(ratingsData, testData));
	}



}
