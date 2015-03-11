package ml.shobhit;




/**
 * Using some of the concepts learned from Javadocs, stack overflow and Java tutorial websites on Internet
 *
 */
//public class MovieBean implements Comparable<MovieBean>, Comparator<MovieBean>{
	public class MovieBean{
	String movieId;

	String userId;
	double rating;

	public MovieBean() {
		// TODO Auto-generated constructor stub
	}

	MovieBean(String userId){
		this.userId = userId;
	}

	MovieBean(String userId, double rating){
		this.userId = userId;
		this.rating = rating;
	}

	MovieBean(String movieId, String userId, double rating){
		this.movieId = movieId;
		this.userId = userId;
		this.rating = rating;
	}

	public String getMovieId() {
		return movieId;
	}


	public void setMovieId(String movieId) {
		this.movieId = movieId;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public double getRating() {
		return rating;
	}


	public void setRating(double rating) {
		this.rating = rating;
	}
/**
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ("(" + this.movieId+ "," + this.userId + "," + this.rating + ")") ; 
	}

	//objects will be sorted based on this method, whenever we call Collections.sort method for this class's 
	//object
	/*
	public static Comparable<MovieBean> comparatorE = new Comparable<MovieBean>() {


		@SuppressWarnings("unused")
		public int compare(MovieBean first, MovieBean another) {
			// TODO Auto-generated method stub
			return first.getUserId().compareTo(another.getUserId());
		}

		@Override
		public int compareTo(MovieBean another) {
			// TODO Auto-generated method stub
			return 0;
		}
	};

	@Override
	public int compareTo(MovieBean o) {
//		String userIdAnother = ((MovieBean)o).getUserId();
		return this.userId.compareTo(o.userId);
	}

	@Override
	public boolean equals(Object obj) {
		MovieBean bean = (MovieBean) obj;

		return this.userId.equals(bean.getUserId());
	};
	@Override				
	public int compare(MovieBean o1, MovieBean o2) {		//writing this function to enable me to sort the lists
		//by movie id, whenever we pass an empty movieBean object to it. 
//		String movieId1 = ((MovieBean)o1).getMovieId();
//		String movieId2 = ((MovieBean)o2).getMovieId();
//		return movieId1.compareTo(movieId2);
		return o1.movieId.compareTo(o2.movieId);

		//		String userId1 = ((MovieBean)o1).getUserId();
		//		String userId2 = ((MovieBean)o2).getUserId();
		//		return userId1.compareTo(userId2);
	}
*/

}
