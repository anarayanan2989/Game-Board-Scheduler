import java.io.*;
import java.lang.*;
import java.util.Scanner;

public final class game1
{
	public static void main(String args[]) throws Exception
	{
		Scanner keyboard = new Scanner(System.in);
		
		System.out.println("Enter the number of processors available:");
		int processors = keyboard.nextInt();
		
		System.out.println("Enter the number of tasks to be executed:");
		int token = keyboard.nextInt();
		
		int[] [] task = new int[token] [6];
		int[] [] executable_task = new int[token] [6];
		int[] [] idle_task = new int[token-processors] [6];
		
		
		
		for(int row = 0; row < token ; row++)
		{
			int Job_ID = row + 1;
			System.out.println("\nEnter the Scheduling time, Computation time and Deadline for Job-" + Job_ID + " :");
			for(int column = 0; column < 3; column++)
			{
				task[row][column] = keyboard.nextInt();
			}
			
			//Computing Laxity for each task
			task[row][3] = task[row][2] - task[row][1];
			task[row][4] = Job_ID;
			
			//The last field is for marking if the task is completed or not
			// 0 indicates job is not completed. 1 indicates that job is complete
			task[row][5] = 0; 
		}
		
		//Plotting the graph
		System.out.println("\nInitial Co-ordinates for each Job");
		System.out.println("-----------------------------------");
		graph(task,task.length);
		
		int max_deadline = 0,min_deadline=0,min_ScheduleTime=0;
		
		max_deadline = maximum(task);
		
		System.out.println("Maximum deadline: " +max_deadline);
		
		System.out.println("Running the EDF scheduler................");
		
		if(processors < token){
			
			min_ScheduleTime = minimum(task);
			for(int time = 0;time <= max_deadline;time++)
			{
				int count = 0,exe_index = 0,idle_index=0;
				if(time==min_ScheduleTime)
				{
					for(int row = 0; row < token; row++)
					{
						if(task[row][0]==min_ScheduleTime){
							count = count+1;
							for(int column = 0; column < 6; column++)
							{	
								executable_task[exe_index][column] = task[row][column];
							}
							exe_index += 1;
						}
						else{
							while(idle_index < (token-processors)){
							for(int column = 0; column < 6; column++)
							{	
								idle_task[idle_index][column] = task[row][column];
							}
							
								idle_index +=1;
							}
								
						}
					}	
				
					if((count < processors)||(count == processors)){
						System.out.println("\nAt Time-"+time);
						System.out.println("-----------");
						System.out.println("\nNo of tasks that are executing: " +exe_index);
						for(int row = 0;row<exe_index; row++)
						{
							if(executable_task[row][0]	== min_ScheduleTime){
								System.out.println("Job-" +executable_task[row][4]+ " is RUNNING");
								executable_task[row][1] = executable_task[row][1] - 1;
								for(int trow = 0;trow < task.length; trow++)
								{
									if(task[trow][4] == executable_task[row][4])
										task[trow][1] = executable_task[row][1];
									else
										continue;
								}
							}
						}
						graph(executable_task,exe_index);
						
						System.out.println("\nNo of tasks that are waiting: " +idle_index);
						for(int row = 0;row<idle_task.length; row++)
						{	
							if(idle_task[row][3] > -1){
								System.out.println("Job-" +idle_task[row][4]+ " is WAITING");
								idle_task[row][3] = idle_task[row][3] - 1;
								for(int trow = 0;trow < task.length;trow++){
									if(idle_task[row][5] == executable_task[trow][5])
										executable_task[trow][1] = idle_task[row][1];
									else
										continue;
								}
							}
							//Task has reached saturation
							else{
								idle_task[row][5] = 1; 			
								for(int trow = 0;trow < task.length; trow++)
								{
									if(task[trow][4] == idle_task[row][4]){
										task[trow][3] = idle_task[row][3];
										task[trow][5] = idle_task[row][5];
									}
									else
										continue;
								}
							}
						}
						
						graph(idle_task,idle_task.length);
						task = EDF_Tester(executable_task,idle_task,task);
						
						for(int row = 0; row < task.length; row++){
							if(task[row][5] == 1){
								System.out.println("EDF Scheduling can't be performed");
								time = max_deadline+1;
							}
						}
								
						
						
					}
				
					else{
						int ecount = 0, icount = 0;
						System.out.println("\nAt Time-"+time);
						System.out.println("-----------");
						
						max_deadline = maximum(executable_task);
									
						
						for(int row = 0; row < executable_task.length ; row++)
						{
							if(executable_task[row][2] < max_deadline){
								System.out.println("Job-" +executable_task[row][4]+ " is RUNNING");
								executable_task[row][1] = executable_task[row][1] - 1;
								ecount += 1;
								for(int trow = 0;trow < task.length; trow++)
								{
									if(task[trow][4] == executable_task[row][4])
										task[trow][1] = executable_task[row][1];
									else
										continue;
								}
							}
							else{
								for(int irow =0;irow < token-processors ; irow++)
								{
									if(idle_task[irow][3] > -1)
									{
										idle_task[irow][4] = executable_task[row][4];
										idle_task[irow][1] = executable_task[row][1];
										idle_task[irow][3] = executable_task[row][3] - 1;
										System.out.println("Job-" +idle_task[irow][4]+ " is WAITING");
										icount += 1;
									}
									else{
										idle_task[irow][5] = 1; 			
										for(int trow = 0;trow < task.length; trow++)
										{
											if(task[trow][4] == idle_task[irow][4]){
												task[trow][3] = idle_task[irow][3];
												task[trow][5] = idle_task[irow][5];
											}
											else
												continue;
										}
									}
								}	
							}
						}
						System.out.println("\nNo of tasks that are executing: " +ecount);
						System.out.println("No of tasks that are waiting: " +icount);
						graph(executable_task,idle_task,executable_task.length,idle_task.length);
						graph(idle_task,idle_task.length);
						task = EDF_Tester(executable_task,idle_task,task);
						
						for(int row = 0; row < task.length; row++){
							if(task[row][5] == 1){
								System.out.println("EDF Scheduling can't be performed");
								time = max_deadline+1;
							}
						}
						
					}
				
				}	
				
				else{
					for(int erow = 0; erow < executable_task.length; erow++)
					{
						if(time == (time + executable_task[erow][1])){
							System.out.println("At Time-" +time);
							System.out.println("---------");
							int num = 0,new_length = 0;
							min_deadline = minimum(idle_task);
							
							if(executable_task[erow][2] == min_deadline){
								for(int irow = 0; irow < idle_task.length; irow++)
								{
									if(idle_task[irow][2] == min_deadline){
										for(int column = 0; column < 6; column++)
											executable_task[num][column] = idle_task[irow][column];
									}
									else
										continue;
								}
								num += 1;
								new_length = num + 1;
							}
							
							int icount = 0;
							
							//Defining new set of tasks that are idle
							for(int irow = 0; irow < (executable_task.length - new_length); irow++)
							{
								for(erow =num; erow < executable_task.length; erow++)
								{
									for(int column = 0; column < 6; column++)
									{	
										idle_task[irow][column] = executable_task[erow][column];
										executable_task[erow][column] = 0;
										icount += 1;
									}
								}
							}
							//Removing previous set of idle tasks that are executing now
							for(int irow = icount-1; irow < idle_task.length; irow++)
							{
								for(int column = 0; column < 6; column++)
								{
									idle_task[irow][column] = 0;
								}
							}
							
							//Printing the tasks that are executing currently
							for(erow = 0; erow < new_length; erow++){
								for(int column = 0; column < 6; column++){
									System.out.println("Job-" +executable_task[erow][4]+ " is RUNNING");
									executable_task[erow][1] = executable_task[erow][1] - 1;
									for(int trow = 0;trow < task.length; trow++)
									{
										if(task[trow][4] == executable_task[erow][4])
										task[trow][1] = executable_task[erow][1];
										else
											continue;
									}
								}
							}
							
							//Printing the tasks that are currently waiting
							for(int row =0 ; row < new_length; row++){
							for(int irow =0; irow < icount; irow++){
								if(idle_task[irow][3] > -1)
								{
										idle_task[irow][4] = executable_task[row][4];
										idle_task[irow][1] = executable_task[row][1];
										idle_task[irow][3] = executable_task[row][3] - 1;
										System.out.println("Job-" +idle_task[irow][4]+ " is WAITING");
										icount += 1;
								}
								else{
										idle_task[irow][5] = 1; 			
										for(int trow = 0;trow < task.length; trow++)
										{
											if(task[trow][4] == idle_task[irow][4]){
												task[trow][3] = idle_task[irow][3];
												task[trow][5] = idle_task[irow][5];
											}
											else
												continue;
										}
								}
							}
							}
							System.out.println("\nNo of tasks that are executing: " +new_length);
							System.out.println("No of tasks that are waiting: " +icount);
							graph(executable_task,idle_task,new_length,icount);
							graph(idle_task,icount);
							task = EDF_Tester(executable_task,idle_task,task);
						
							for(int row = 0; row < task.length; row++){
								if(task[row][5] == 1){
									System.out.println("EDF Scheduling can't be performed");
									time = max_deadline+1;
								}
							}
							
						}
					}
					
				}		
			}
		}
	}


public static void graph(int[][] task,int num)
	{
		int[] [] graph = new int[num] [2];
		int Jid = 0;
		System.out.println("\n");
		for(int row =0;row < num; row++)
		{
			for(int column = 0; column < 2; column++)
			{
				Jid = task[row][4];
				
				if(column == 0)
					graph[row][column] = task[row][3];
				else if(column == 1)
					graph[row][column] = task[row][1];
				
			}
			System.out.println("Graph Co-ordinates for the Job-" +Jid+ ": (" + graph[row][0]+ "," +graph[row][1]+")");
		}
	}
	
public static void graph(int[][] task1,int[][] task2,int num1,int num2)
	{
		int[] [] graph = new int[num1] [2];
		int[] Jid = new int[num1-num2]; int irow = 0,erow = 0,j=0;
		System.out.println("\n");
		while(irow < num2)
		{
			while(erow < num1)
			{	
				if(task1[erow][4] == task2[irow][4]){
					erow += 1;
					continue;
				}
				else{
					Jid[j] = task1[erow][4];
					j += 1;
					erow += 1;
				}
			}
			irow += 1;
		}
		for(int row =0;row < num1; row++)
		{		
			for(int i = 0; i < Jid.length; i++)
			{
				if(Jid[i] == task1[row][4]){
					for(int column = 0; column < 2; column++)
					{
						if(column == 0)
							graph[row][column] = task1[row][3];
						else 
							graph[row][column] = task1[row][1];
					}
				System.out.println("Graph Co-ordinates for the Job-" +Jid[i]+ ": (" + graph[row][0]+ "," +graph[row][1]+")");
			}
			else
				continue;
			}
			
		}
	}
	public static int[][] EDF_Tester(int[][] task1, int[][] task2, int[][] job)
	{	
		for(int irow = 0; irow<task2.length; irow++)
		{
			for(int row = 0;row<task1.length; row++)
			{
				if((task1[row][1] == 0)&&(task1[row][4]!=0)){
					System.out.println("\nJob-" +task1[row][4]+ " is COMPLETED");
					job[row][5] = 1;
				}	
				else if((task1[row][1] != 0) && (task2[irow][3] < 0)){
					System.out.println("Job-" +task2[irow][4]+ " has crashed");
					System.out.println("EDF scheduler fails");
					break;
				}			
			}
		}	
		return job;
	}
	
	public static int minimum(int[][] job)
	{
		int min = 0;
		for(int row = 0; row < job.length ; row++)
			{
				if(row==0)
					min = job[row][0];
				else
					min = Math.min(min,job[row][0]);
			}
		
		return min;
	}
	
	public static int maximum(int[][] job)
	{
		int max = 0;
		for(int row = 0; row < job.length ; row++)
		{
			max = Math.max(job[row][2],max);
		}
		return max;
	}
}