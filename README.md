# MyStory App

## Introduction
MyStory is a diary app that allows users to write down entries documenting anything they want every day — thoughts, feelings, ideas, memories — just like 
with a classic physical diary. The app is also able to get the location of the device, so the user can save it and include it in the entry. Users are able 
to brows all the entries they made, edit and delete existing entries from previous days, change the app's theme and modify their login credentials.

## Goal
My goal in developing this app was to learn about Android mobile app development using Android Studio with Java. A journaling app seemed a good idea to 
start with, and one where I could easily add more features later on if I needed to. As someone who also keeps a diary to write down thoughts and 
experiences of the day, I was inspired to make a similar app.

## Features
* Write entries, with date and current location (optional)
* Brows entries from past days
* Get device's current location with Google Maps and Places API
* Alternative app theme: night mode
* View stats of written entries 
* Database: Firebase's Firestore
* Firebase authentication 
* Sign in with Google and Facebook

Feature ideas:
* Insert images in entries
* Customise style of entries

Known issues: 
* Night mode trigger is inconsistent when opening Settings screen
* Edited entries only display changes after screen is reloaded
* Upon registration, the username is only displayed in homescreen after reloading
