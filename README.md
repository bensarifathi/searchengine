# DAAR Final Project

# Description
An api that allows to search in the Gutenburg books database. The search functionalities are implemented using KMP of Regex Algorihms. The ordering is based on a Jaccard graph using various ordering options. More details in the report and the video.

# Requirements 
JAVA >= 17
Maven 

# How to start
1- Get the code for the scrapper from https://github.com/bensarifathi/gutenberg-scraper
2- Install requests python library ( using pip or poetry )
3- Launch the main.py script using the commande and wait for the books to be downloaded. The download takes 2min to 5min depending on your internet speed.
4- Copy the content of the folder ressources in the gutenberg-scraper inside the folder src/main/resources.
5- Make sure that the src/main/resources have the required files and folders ( books folder, db folder )
6- Install maven dependencies by running the commande: mvn clean install
7- Start the application using the commande: mvn spring-boot:run
8- Your webserver should be listening on port 8080. Now move to the front end 
9- Give us a good grade please.
