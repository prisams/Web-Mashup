============================================
RUSH TO DOCTOR Application 
============================================
------------------------------
Steps to run our Application
------------------------------
1. Place the java file on the working directory in your system.
2. Place the json-20140107.jar on the same system, at some <path>.
3. Export the CLASSPATH variable (to export the jars).
	export CLASSPATH=<path>/json-20140107.jar:$CLASSPATH
4. Compile the source code as javac  ProgrammingAssignment1.java going to the working directory where the source code is placed (from Step1).
5. Launch the appication using the command: java ProgrammingAssignment1

------------------------------
Input to the Application
------------------------------
Enter your zipcode:
10004
Enter the speciality of doctor you are looking for(eye,cardiologist,dentist, etc) :
dentist
Enter the radius for your search in miles:
5
<The list of doctors gets returned>
Enter the doctor number you want to visit:
6
<Fare estimator output>


