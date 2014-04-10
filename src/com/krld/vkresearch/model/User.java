package com.krld.vkresearch.model;

import java.util.*;

import com.krld.vkresearch.Utils;

public class User extends Object
{
	public String firstName;
	public String lastName;
	public long userId;
	public Set<User> friends;
	
	public double maxSpeed = 10;

	public Point pos;

	public double maxDistance = 250;

	public double minDistance = 40;

	private UserGraphContext context;
	
	public User(long uid, String firstName, String lastName){
		this.userId = uid;
		this.firstName = firstName;
		this.lastName = lastName;
		friends = new HashSet<User>();
		pos = new Point(300, 300);
	}

	public void setContext(UserGraphContext context)
	{
		this.context = context;
	}

	public void updatePos()
	{ // temporary
		Point moveVector = new Point(0,0);
		for (User root: friends){
			moveVector.plus(root.getGravityForceVector(this, true));
		}
		
		for (User user : context.getAllUsers()) {
			if (this == user) continue;
			if (friends.contains(user)) continue;
			moveVector.plus(user.getGravityForceVector(this, false));
		}
		
		moveVector.constraint(maxSpeed);
		
		pos.plus(moveVector);
	}

	private Point getGravityForceVector(User user, boolean checkMaxDistance)
	{
		Point res = pos.clone();
		res.minus(user.pos);
		double distance = Utils.getDistance(res);
		if (distance > maxDistance && checkMaxDistance) {
			res.multiple((distance - maxSpeed)/ distance);
			return res;
		} else if(distance < minDistance){
			res.multiple((minDistance -distance)/distance);
			res.multiple(-1);
			return res;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean equals(java.lang.Object o)
	{
		if ( ! (o instanceof User)) return true;
		return userId == ((User)o).userId;
	}
	
	@Override
	  public int hashCode() {
		  return (int)userId;
	  }
}
