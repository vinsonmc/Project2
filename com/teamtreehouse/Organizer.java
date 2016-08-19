package com.teamtreehouse;

import com.teamtreehouse.model.Player;
import com.teamtreehouse.model.Team;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;


public class Organizer {
  private Player[] mPlayers;
  private List<Team> mTeams;
  private BufferedReader mReader;
  private Map<String, String> mMenu;
  private List<Player> mAvailablePlayers = new ArrayList<Player>();
  private int mMaxTeams;
  
 
  public Organizer(Player[] players) {
    mPlayers = players;
    for (Player player : players) {
      mAvailablePlayers.add(player);
    }
    mTeams = new ArrayList<Team>();
    mMaxTeams = mAvailablePlayers.size()/11;
    mReader = new BufferedReader(new InputStreamReader(System.in));
    mMenu = new HashMap<String, String>();
    mMenu.put("team", "Add a new team to the season.");
    mMenu.put("add", "Add a player to a team.");
    mMenu.put("remove", "Remove a player from a team.");
    mMenu.put("report", "Get a report for a team.");
    mMenu.put("league", "Get a league balance report.");
    mMenu.put("done", "Finish creating the season.");
  }
  
  public void run() {
    String choice = "";
    do {
      try {
        choice = promptAction();
        switch(choice) {
          case "team":
            if (mTeams.size() == mMaxTeams) {
              System.out.printf("There are already %d teams, no more are needed. %n" ,
                                mTeams.size());
              break;
            }
            mTeams.add(promptTeam());
            break;
          case "add":
            addToTeam();
            break;
          case "remove":
            removeFromTeam();
            break;
          case "report":
            report();
            break;
          case "league":
            leagueBalanceReport();
            break;
          case "done":
            break;
          default:
            System.out.printf("Unkown choice: '%s'. Try again. %n%n",
                              choice);
        }
      } catch (IOException ioe) {
        System.out.println("Problem with input");
        ioe.printStackTrace();
      }
    } while (!choice.equals("done"));
    
    System.out.printf("%nHere are the teams: %n");
    for (Team team: mTeams) {
      System.out.printf("%nTeam Name: %s%nCoach:     %s%n", 
                      team.getTeamName(), team.getCoach());
      for (Player player: team.getPlayerList()) {
        System.out.printf("%s %s%n", player.getFirstName(), player.getLastName());
      }
    }
    
  }
  
  private void leagueBalanceReport() {
    System.out.printf("%n---League Balance Report---%n");
    for (Team team: mTeams) {
      System.out.printf("%nTeam %s %n%d experienced, %d not experienced. %n",
                         team.getTeamName(),
                         team.getExperience()[0],
                         team.getExperience()[1]);
      Map<Integer, List<String>> playersByHeight = team.heightMap();
      System.out.println("Number of players of each height:");
      for (int height: playersByHeight.keySet()) {
        System.out.printf("%d are %d inches.%n", 
                          playersByHeight.get(height).size(),
                          height);
      }
    }
    
  }
  
  private void report() throws IOException {
    if (mTeams.size() == 0) {
      System.out.printf("%nThere are no teams to report.%n");
      return;
    }
    
    System.out.printf("%nWhich team would you like a report for?%n");
    Team team = teamChoice();
    Map<Integer, List<String>> playersByHeight = team.heightMap();
    System.out.printf("%nReport for team %s:%n", team.getTeamName());
    System.out.printf("%.0f%% of players are experienced.%n%n", 
                      team.experiencePercent());
    System.out.print("Players by height:");
    for (int height: playersByHeight.keySet()) {
      System.out.printf("%n%d inches:  ", height);
      for (String player: playersByHeight.get(height)) {
        System.out.printf("%s, ", player);
      }
    }
  }
  
  private String promptAction() throws IOException {
    System.out.printf("%n%nYour options are: %n");
    for (Map.Entry<String, String> option : mMenu.entrySet()) {
      System.out.printf("%s - %s %n",
                        option.getKey(),
                        option.getValue());
    }
    System.out.printf("%nEnter your option:  ");
    String choice = mReader.readLine();
    return choice.trim().toLowerCase();
  }
  
  private Team promptTeam() throws IOException {
    System.out.printf("%nEnter team name:  ");
    String teamName = mReader.readLine();
    System.out.print("Enter coach:  ");
    String coach = mReader.readLine();
    Team team = new Team(teamName, coach);
    System.out.printf("Team %s added.%n", teamName);
    return team;
  }
  
  private void addToTeam() throws IOException {
    if (mTeams.size() == 0) {
      System.out.printf("%nThere are no teams to add to.%n");
      return;
    }
    if (mAvailablePlayers.size() == 0) {
      System.out.printf("%nThere are no players to add.%n");
      return;
    }
    //Player Choice
    System.out.printf("%nWho would you like to add?%n");
    Player playerChoice = playerChoice(mAvailablePlayers);
    System.out.printf("%nAdding %s %s.%n", playerChoice.getFirstName(), playerChoice.getLastName());
    //Team Choice
    System.out.println("Which team?");
    Team teamChoice = teamChoice();
    if (teamChoice.addPlayer(playerChoice) == false) {
      System.out.printf("%nThat team is full.%n");
      return;
    }
    mAvailablePlayers.remove(playerChoice);
    System.out.printf("%n%s %s added to team %s%n", playerChoice.getFirstName(), playerChoice.getLastName(), teamChoice.getTeamName());
  }
  
  private void removeFromTeam() throws IOException {
    if (mTeams.size() == 0) {
      System.out.printf("%nThere are no teams to remove from.%n");
      return;
    }
    
    System.out.println("Which team?");
    Team teamChoice = teamChoice();
    if (teamChoice.getPlayerList().size()==0) {
      System.out.println("There are no players to remove on this team.");
      return;
    }
    
    System.out.println("Which player?");
    Player playerChoice = playerChoice(teamChoice.getPlayerList());
    teamChoice.removePlayer(playerChoice);
    mAvailablePlayers.add(playerChoice);
  }
  
  private Player playerChoice(List<Player> playerList) throws IOException {
    Collections.sort(playerList);
    List<String> playerNames = new ArrayList();
    for (Player player: playerList) {
      playerNames.add(player.getStats());
    }
    return playerList.get(promptInteger(playerNames));
  }
  
  private Team teamChoice() throws IOException {
    Collections.sort(mTeams);
    List<String> teamNames = new ArrayList();
    for(Team team : mTeams) {
      teamNames.add(team.getTeamName());
    }
    return mTeams.get(promptInteger(teamNames));
  }
  
  private int promptInteger(List<String> list) throws IOException {
    boolean isInteger = false;
    boolean isInRange = false;
    int choice = 0;
    
    while (!isInteger || !isInRange) {
      System.out.println("Your options are:  ");
      int index = 1;
      for(String item : list) {
        System.out.printf("%d.) %s %n", index, item);
        index++;
      }
      System.out.printf("Enter your option:  ");
      String choiceAsString = mReader.readLine();
      choiceAsString = choiceAsString.trim();
      try {
        choice = Integer.parseInt(choiceAsString.trim());
        isInteger = true;
      } catch (IllegalArgumentException iae) {
        System.out.printf("%s. Please input an integer.\n", iae.getMessage());
      }
      
      if (1 <= choice && choice <= list.size()) {
        isInRange = true;
      } else {
        System.out.println("That choice is out of range. Try again.");
      }
    }
    return choice - 1;
    
  }
  

  
}