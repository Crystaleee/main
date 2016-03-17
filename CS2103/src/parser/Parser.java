package parser;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import bean.Command;
import bean.CommandAddDeadlineTask;
import bean.CommandAddEvent;
import bean.CommandAddFloatTask;
import bean.CommandAddReserved;
import bean.CommandDelete;
import bean.CommandInvalid;
import bean.CommandRedo;
import bean.CommandUndo;
import bean.CommandUpdate;

public class Parser{
	private  String commandType;
	private  String taskDescription = null;
	private  String  recurringDescription = null;
	private  Calendar startDateTime = Calendar.getInstance();
	private  Calendar endDateTime = Calendar.getInstance();
	private  Calendar date = Calendar.getInstance();
	private  Calendar time = Calendar.getInstance();
	private  String location;
	private  ArrayList<String> tagLists = new ArrayList<String>();
	private  ArrayList<Integer> taskNumbers = new ArrayList<Integer>();
	private ArrayList<Integer> deleteTaskNumber = new ArrayList<Integer>();
	private Integer numberOfDaysToPostpone;
	private ArrayList<Integer> doneTaskNumber = new ArrayList<Integer>();
	private  ArrayList<String> calendarDescription = new ArrayList<String>();
	private ArrayList<Calendar> startDateTimes = new ArrayList<Calendar>();
	private ArrayList<Calendar> endDateTimes = new ArrayList<Calendar>();
	private Integer updateTaskNumber;

	private static Logger logger = Logger.getGlobal();
	private static FileHandler fh; 


/*	public static void main(String[] args){ // for testing
		try {
			fh = new FileHandler("/Users/kailin/Desktop/IVLE/CS2103/for proj/cs2103 proj/CS2103/src/MyLogFile.log");
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  

		} catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } 
		
		Parser testTimeTask = new Parser();
		testTimeTask.ParseCommand("add test Time task (12/2/16 15:05 - 15/2/15 07:00) @icube @LT27 #hihi #me");
		
		Parser testTimeTask2 = new Parser();
		testTimeTask2.ParseCommand("add test Time task (12/3/16 15:05 - 12/4/15 07:00) @icube @LT27 #hihi #me");
		Parser testFloat = new Parser();
		testFloat.ParseCommand("add test floatingtask @zzz #arghhhhh #hi");

		Parser testDeadLine = new Parser();
		testDeadLine.ParseCommand("add deadLine @whatthe (12/2/14) #hey");

		Parser testDeadLine2 = new Parser();
		testDeadLine2.ParseCommand("add deadLine @location (12/2/14 15:00) #?");

		Parser testDelete = new Parser();
		testDelete.ParseCommand("delete 1,2,3,4");
		
		Parser testReserve = new Parser();
		testReserve.ParseCommand("reserve meeting with boss (12/2/15 15:00 - 15/2/15 14:32) (13/2/15 14:00 - 15/4/15 12:00) #hwork @icube");
		
		Parser testUpdateTask = new Parser();
			testUpdateTask.ParseCommand("update 2 hello hello hello"); 
	}*/

	public Command ParseCommand(String inputLine){
	 


		String[] args = inputLine.split(" ");	
		ArrayList<String> taskDescriptionList = new ArrayList<String>();
		commandType = (args[0].trim());
		int endDateYear;
		int endDateMonth;
		int endDateDay;
		int endMin;
		int endHour;
		boolean isEvent = false;
		boolean isDeadline = false;

		if (commandType.equals("add")){
			int i = 1;

			while (i < args.length && (!args[i].substring(0,1).equals("(")) && (!args[i].substring(0,1).equals("@")) && (!args[i].substring(0,1).equals("#"))){
				taskDescriptionList.add(args[i]);
				i++;
			}


			for (String word : taskDescriptionList) {
				taskDescription += word + " ";
			} 

			taskDescription = taskDescription.substring(4,taskDescription.length());

			for ( ; i<args.length; i++){
				if (args[i].substring(0, 1).equals("@")){
					location = args[i].substring(1, args[i].length());
				}


				else if (args[i].substring(0, 1).equals("(")){
					int j = i;

					while (j < args.length && (!args[j].substring(0,1).equals("@")) && (!args[j].substring(0,1).equals("#"))){
						calendarDescription.add(args[j]);
						j++;
					}


					//event -  have startTime & endTime
					if (calendarDescription.contains("-")){
						logger.info("Event task");
						String startDate = calendarDescription.get(0).substring(1, calendarDescription.get(0).length());
						String[] startDateYYMMDD = startDate.split("/");

						String startTime = calendarDescription.get(1);
						String[] startTimeHourMinute = startTime.split(":");

						int startDateYear = Integer.valueOf(startDateYYMMDD[2]);
						int startDateMonth = Integer.valueOf(startDateYYMMDD[1])-1;
						int startDateDay = Integer.valueOf(startDateYYMMDD[0]);

						int startHour = Integer.valueOf(startTimeHourMinute[0]);
						int startMin = Integer.valueOf(startTimeHourMinute[1]);

						startDateTime.set(startDateYear,startDateMonth,startDateDay,startHour,startMin);

						String endDate = calendarDescription.get(3);
						String[] endDateYYMMDD = endDate.split("/");

						String endTime = calendarDescription.get(4).substring(0,calendarDescription.get(4).length()-1);	
						String[] endTimeHourMinute = endTime.split(":");

						endDateYear = Integer.valueOf(endDateYYMMDD[2].substring(0, 2));					
						endDateMonth = Integer.valueOf(endDateYYMMDD[1])-1;
						endDateDay = Integer.valueOf(endDateYYMMDD[0]);

						endHour = Integer.valueOf(endTimeHourMinute[0]);
						endMin = Integer.valueOf(endTimeHourMinute[1]);

						endDateTime.set(endDateYear,endDateMonth,endDateDay,endHour,endMin);
						isEvent = true;

					}

					// deadline task
					else {
						logger.info("Deadline task");

						String endDate = calendarDescription.get(0).substring(1, calendarDescription.get(0).length());
						String[] endDateYYMMDD = endDate.split("/");


						//consist of date and time
						if (calendarDescription.size() > 1){
							logger.info("Deadline task has date and time");

							String endTime = calendarDescription.get(1);
							String[] endTimeHourMinute = endTime.split(":");

							endDateYear = Integer.valueOf(endDateYYMMDD[2].substring(0, 2));
							endDateMonth = Integer.valueOf(endDateYYMMDD[1])-1;
							endDateDay = Integer.valueOf(endDateYYMMDD[0]);

							endHour = Integer.valueOf(endTimeHourMinute[0]);
							endMin = Integer.valueOf(endTimeHourMinute[1].substring(0,endTimeHourMinute[1].length()-1));
							endDateTime.set(endDateYear,endDateMonth,endDateDay,endHour,endMin);
							
						}

						//consist of date only
						else { 					
							logger.info("Deadline task has date only");
							endDateYear = Integer.valueOf(endDateYYMMDD[2].substring(0, 2));
							endDateMonth = Integer.valueOf(endDateYYMMDD[1])-1;
							endDateDay = Integer.valueOf(endDateYYMMDD[0]);
							endHour = 23;
							endMin = 59;
							endDateTime.set(endDateYear,endDateMonth,endDateDay,endHour,endMin);

						}

						isDeadline = true;
						endDateTime.set(endDateYear,endDateMonth,endDateDay,endHour,endMin);
					}
				}


				else if (args[i].substring(0, 1).equals("#")){
					
					tagLists.add(args[i].substring(1, args[i].length()));

				}
			}
			
						
			if (isDeadline){
				Command deadLineTask = new CommandAddDeadlineTask(taskDescription, location, endDateTime, tagLists);
				return deadLineTask;
			}

			else if (isEvent){
				Command eventTask = new CommandAddEvent(taskDescription,location,startDateTime, endDateTime, tagLists);
				return eventTask;
			}

			else {
				Command floatingTask = new CommandAddFloatTask(taskDescription, location, tagLists);
				return floatingTask;
			} 
		}

		

		else if (commandType.equals("delete")){
			if (args[1].equals("all")){
				taskNumbers = null;
			}
			else {
				args = inputLine.substring(7, inputLine.length()).split(",");
				for (int i=0; i<args.length; i++){
					taskNumbers.add(Integer.valueOf(args[i]));
				}
			}

			Command deleteTask = new CommandDelete(taskNumbers);
			return deleteTask;
		}
		
		
		else if (commandType.equals("reserve")){
			logger.info("Reserve task");

			int i = 1;
			while (i < args.length && (!args[i].substring(0,1).equals("(")) && (!args[i].substring(0,1).equals("@")) && (!args[i].substring(0,1).equals("#"))){
				taskDescriptionList.add(args[i]);
				i++;
			}

			for (String word : taskDescriptionList) {
				taskDescription += word + " ";
			} 

			taskDescription = taskDescription.substring(4,taskDescription.length());
			logger.info("taskDescription: " + taskDescription);


			for ( ; i<args.length; i++){
				if (args[i].substring(0, 1).equals("@")){
					location = args[i].substring(1, args[i].length());
					logger.info("reserve location: " + location);

				}
				
				if (args[i].substring(0, 1).equals("#")){
					logger.info("reserve hashtags: "+ args[i].substring(1, args[i].length()));
					tagLists.add(args[i].substring(1, args[i].length()));
				}

				else if (args[i].substring(0, 1).equals("(")){
					int j = i;

					while (j < args.length && (!args[j].substring(0,1).equals("@")) && (!args[j].substring(0,1).equals("#"))){
						calendarDescription.add(args[j]);
						j++;
					}
					
					String startDate = calendarDescription.get(0).substring(1, calendarDescription.get(0).length());
					String[] startDateYYMMDD = startDate.split("/");

					String startTime = calendarDescription.get(1);
					String[] startTimeHourMinute = startTime.split(":");

					int startDateYear = Integer.valueOf(startDateYYMMDD[2]);
					int startDateMonth = Integer.valueOf(startDateYYMMDD[1])-1;
					int startDateDay = Integer.valueOf(startDateYYMMDD[0]);

					int startHour = Integer.valueOf(startTimeHourMinute[0]);
					int startMin = Integer.valueOf(startTimeHourMinute[1]);

					startDateTime.set(startDateYear,startDateMonth,startDateDay,startHour,startMin);
					startDateTimes.add(startDateTime);
					
					String endDate = calendarDescription.get(3);
					String[] endDateYYMMDD = endDate.split("/");

					String endTime = calendarDescription.get(4).substring(0,calendarDescription.get(4).length()-1);	
					String[] endTimeHourMinute = endTime.split(":");

					endDateYear = Integer.valueOf(endDateYYMMDD[2].substring(0, 2));					
					endDateMonth = Integer.valueOf(endDateYYMMDD[1])-1;
					endDateDay = Integer.valueOf(endDateYYMMDD[0]);

					endHour = Integer.valueOf(endTimeHourMinute[0]);
					endMin = Integer.valueOf(endTimeHourMinute[1]);

					endDateTime.set(endDateYear,endDateMonth,endDateDay,endHour,endMin);
					endDateTimes.add(endDateTime);						
				}
			}
			
			Command reserveTask = new CommandAddReserved(taskDescription, location, startDateTimes, endDateTimes, tagLists);
			return reserveTask;
		}


		else if (commandType.equals("update")){
			boolean isTaskDescription = false;
			
			updateTaskNumber = (Integer.valueOf(args[1]));

			if (args[2].contains("-")) {
				if (args[2].contains("@")){
					System.out.println("Comes in remove location");
					//remove location
				}
				
				else if (args[2].contains("#")){
					System.out.println("Comes in remove hashtags");
					//remove hashtag
				}
			}
			
			else if (args[2].contains("@")){
				System.out.println("comes in add new location");
			}
			
			else if (args[2].contains("#")){
				System.out.println("comes in add new location");
			}
			
			
			else {
				for (int i = 2; i<args.length; i++){
					taskDescription += args[i] + " ";
				}
			
				taskDescription = taskDescription.substring(4,taskDescription.length());
				isTaskDescription = true;
			}
			
			/*for (int j=2; j<args.length;j++){
				if (args[j].substring(0, 1).equals("@")){
					command.setLocation(args[j]);
				}

				if (args[j].substring(0, 1).equals("#")){
					command.setTags(args[j]);
				}

				if (args[j].substring(0, 1).equals("(")){
					while (j < args.length){

						calendarDescription.add(args[j]);

						if (args[j].substring(args[j].length()-1,args[j].length()).equals(")")){
							j++;
							break;
						}

						j++;

					}

				}
			}*/
			
			Command update = new CommandUpdate(updateTaskNumber, taskDescription);
			return update;
		} 
		
		else if (commandType.equals("undo")){
			Command undo = new CommandUndo();
			return undo;
		}
		
		else if (commandType.equals("redo")){
			Command redo = new CommandRedo();
			return redo;
		}

		

	/*	else if (command.getCommandType().equals("postpone")){
			command.setTaskNumber(Integer.valueOf(args[1]));
			command.setNumberOfDaysToPostpone(Integer.valueOf(args[2]));
		}

				else if (command.getCommandType().equals("show")){
				(need to double cfm)
		} 

		else if (command.getCommandType().equals("done")){
			args = inputLine.substring(5, inputLine.length()).split(",");
			for (int i=0; i<args.length; i++){
				command.setDoneTaskNumber(Integer.valueOf(args[i]));
			}
		} */

	 Command invalidCommand = new CommandInvalid();
	 return invalidCommand;
		
	}

}

