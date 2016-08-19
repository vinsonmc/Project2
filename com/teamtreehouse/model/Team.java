package com.teamtreehouse.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


public class Team implements Comparable<Team>{
  public static final int MAX_PLAYERS = 11;
  private String mTeamName;
  private String mCoach;
  private Set<Player> mPlayers;
  private int[] mExperience;
  
  public Team (String TeamName, String Coach) {
    mTeamName = TeamName;
    mCoach = Coach;
    mPlayers = new TreeSet<Player>();
    mExperience = new int[] {0,0}; //Experienced, not Experienced
  }
  
  public boolean addPlayer(Player player) {
    if (mPlayers.size() == MAX_PLAYERS) {
      return false;
    }
    
    if (player.isPreviousExperience()) {
      mExperience[0]++;
    } else {
      mExperience[1]++;
    }
    return mPlayers.add(player);
  }
  
  public boolean removePlayer(Player player) {
    if (player.isPreviousExperience()) {
      mExperience[0]--;
    } else {
      mExperience[1]--;
    }
    return mPlayers.remove(player);
  }
  
  public Map<Integer, List<String>> heightMap() {
    Map<Integer, List<String>> playersByHeight = new TreeMap();
    
    for (Player player: getPlayerList()) {
      int height = player.getHeightInInches();
      if (!playersByHeight.containsKey(height)) {
        List<String> players = new ArrayList();
        playersByHeight.put(height, players);
      }
      playersByHeight.get(height).add(player.getLastName() + " " + 
                                      player.getFirstName());
    }
    return playersByHeight;
  }
  
  public double experiencePercent() {
    double result = 0;
    if (mExperience[0] == 0) {
      result = 0;
    } else {
      result = mExperience[0] + mExperience[1];
      result = mExperience[0] / result;
      result = result * 100;
    }
    return result;
  }
  
  public int[] getExperience() {
    return mExperience;
  }
  
  public String getTeamName() {
    return mTeamName;
  }
  
  public String getCoach() {
    return mCoach;
  }
  
  public List<Player> getPlayerList() {
    List<Player> playerList = new ArrayList();
    playerList.addAll(mPlayers);
    return playerList;
  }
  
  @Override
  public int compareTo(Team other) {
    // We always want to sort by last name then first name
    if (equals(other)) {
      return 0;
    }
    return mTeamName.compareTo(other.getTeamName());
  }
  
  
}