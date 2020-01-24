package com.zone5ventures.http.core.api;

import java.util.concurrent.Future;

import com.zone5ventures.core.Types;
import com.zone5ventures.core.activities.VActivity;
import com.zone5ventures.core.workouts.AddUserWorkout;
import com.zone5ventures.core.workouts.UserWorkoutSummary;
import com.zone5ventures.core.workouts.Workouts;
import com.zone5ventures.http.core.AbstractAPI;
import com.zone5ventures.http.core.responses.Z5HttpResponse;
import com.zone5ventures.http.core.responses.Z5HttpResponseHandler;

public class WorkoutsAPI extends AbstractAPI {

	/** Prescribe a workout to one or more users on a given date */
	public Future<Z5HttpResponse<UserWorkoutSummary>> add(AddUserWorkout workout) {
		return add(workout, null);
	}
	
	/** Prescribe a workout to one or more users on a given date */
	public Future<Z5HttpResponse<UserWorkoutSummary>> add(AddUserWorkout workout, Z5HttpResponseHandler<UserWorkoutSummary> handler) {
		return getClient().doPost(Types.USER_WORKOUT_SUMMARY, Workouts.ADD_USER_WORKOUT, workout, handler);
	}
	
	/** Associate a scheduled workout with a completed activity */
	public Future<Z5HttpResponse<VActivity>> associate(long fileId, long workoutId) {
		return associate(fileId, workoutId, null);
	}
	
	/** Associate a scheduled workout with a completed activity */
	public Future<Z5HttpResponse<VActivity>> associate(long fileId, long workoutId, Z5HttpResponseHandler<VActivity> handler) {
		String path = Workouts.ASSOCIATE_WORKOUT.replace("{fileId}", String.format("%d", fileId)).replace("{workoutId}", String.format("%d", workoutId));
		return getClient().doGet(Types.ACTIVITY, path, handler);
	}
	
	/** Disassociate a scheduled workout or event with a completed activity */
	public Future<Z5HttpResponse<VActivity>> disassociate(long fileId) {
		return disassociate(fileId, null);
	}
	
	/** Disassociate a scheduled workout or event with a completed activity */
	public Future<Z5HttpResponse<VActivity>> disassociate(long fileId, Z5HttpResponseHandler<VActivity> handler) {
		String path = Workouts.DISASSOCIATE_WORKOUT_BY_FILEID.replace("{fileId}", String.format("%d", fileId));
		return getClient().doGet(Types.ACTIVITY, path, handler);
	}
}
