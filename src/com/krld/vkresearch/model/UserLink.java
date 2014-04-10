package com.krld.vkresearch.model;
import java.util.*;

public class UserLink
{

	public  Set<User> link;
	public UserLink(User u1, User u2){
		link = new HashSet<User>();
		link.add(u1);
		link.add(u2);
	}
	
	@Override
	public int hashCode() {
		return link.hashCode();
	}
}
