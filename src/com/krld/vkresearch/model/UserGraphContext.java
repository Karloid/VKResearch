package com.krld.vkresearch.model;

import java.util.*;

public interface UserGraphContext
{
	User getRoot();
	
	Set<User> getAllUsers();
	
	Set<UserLink> getAllUserLinks();
}
