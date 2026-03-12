# COM1003 Assignment – Gym Dashboard

## Overview

This project implements a **Gym Dashboard application** for analysing and exploring a dataset of gym members. The program reads a CSV dataset containing gym member information and allows users to:

* Analyse the dataset through predefined statistical questions
* Execute user-defined queries from a text file
* Interactively explore the dataset using a graphical dashboard
* Visualise distributions of attributes using histograms

The application was developed in **Java** as part of the **COM1003 Java Programming module** at the **University of Sheffield**.

---

# Features

The system provides three main functionalities:

### 1. Dataset Analysis (CLI)

The program prints answers to several analytical questions about the dataset, such as:

* Total number of unique ages in the dataset
* Average BMI for individuals taller than 1.8 metres
* Average body fat percentage for members exercising more than 4 times per week
* Average BMI for advanced gym members
* Number of individuals drinking more than 3 litres of water daily
* Percentage of individuals with BMI above the healthy range

All results are displayed in the **command line interface (CLI)** with values formatted to a maximum of two decimal places.

---

### 2. Query Execution

The application reads queries from a text file and executes them against the dataset.

Queries follow the format:

```
select entries where <conditions>
```

Example queries:

```
select entries where avg_bpm > 150
select entries where age >= 59 AND max_bpm <= 170 AND workout_type = HIIT
```

Supported operators include:

| Operator | Meaning               |
| -------- | --------------------- |
| >        | greater than          |
| >=       | greater than or equal |
| <        | less than             |
| <=       | less than or equal    |
| =        | equal                 |
| !=       | not equal             |

Key behaviour implemented:

* Queries are **case-insensitive**
* Multiple conditions are combined using **AND**
* Invalid queries throw an **IllegalArgumentException**
* Query errors **stop query execution but do not stop the program**
* The GUI still loads even if query parsing fails

---

### 3. Interactive GUI Dashboard

The application includes a **Java Swing graphical interface** that allows interactive exploration of the dataset.

The GUI consists of three main areas:

---

## Area 1 – Dataset Filters

Users can filter gym members using:

### Range sliders

* Age
* Height
* Weight

### Property filters

Users can add additional filters based on dataset properties.

Example filters:

```
resting_bpm > 70
session_duration < 2
```

Multiple property filters can be added or cleared dynamically.

When filters change, the visualisation and statistics update automatically.

---

## Area 2 – Histogram Visualisation

The dashboard displays a histogram showing the distribution of a selected numeric property.

The histogram includes:

* labelled axes
* tick marks
* bin ranges
* frequency bars
* mean value indicator

The histogram is implemented using **Java2D graphics**.

No external plotting libraries are used.

---

## Area 3 – Dataset Statistics

The dashboard shows statistics for the filtered dataset including:

* Minimum value
* Maximum value
* Mean value
* Number of entries displayed

Statistics are calculated dynamically whenever filters change.

---

# Dataset

The application works with a CSV dataset containing information about gym members.

Each entry contains the following attributes:

| Property            | Description                     |
| ------------------- | ------------------------------- |
| Age                 | Age of the member               |
| Gender              | Male or Female                  |
| Weight              | Weight (kg)                     |
| Height              | Height (m)                      |
| Max_BPM             | Maximum heart rate              |
| Avg_BPM             | Average heart rate              |
| Resting_BPM         | Resting heart rate              |
| Session_Duration    | Workout duration                |
| Calories_Burned     | Calories burned                 |
| Workout_Type        | Cardio / Strength / Yoga / HIIT |
| Body_Fat_Percentage | Body fat                        |
| Water_Intake        | Daily water intake              |
| Workout_Frequency   | Sessions per week               |
| Experience_Level    | Beginner to expert              |
| BMI                 | Body Mass Index                 |

Each row of the dataset is stored as an **Entry object**.

---

# Project Architecture

The system follows a modular design and extends several provided abstract classes.

## Implemented Classes

The following classes were implemented for this assignment:

```
GymDashboardApp.java
EntryCatalog.java
QueryParser.java
Histogram.java
HistogramPanel.java
GymDashboardPanel.java
```

### GymDashboardApp

Main entry point of the application.

Responsibilities:

* Read input files
* Start CLI analysis
* Launch the GUI dashboard

---

### EntryCatalog

Extends:

```
AbstractEntryCatalog
```

Responsibilities:

* Load entries from the CSV dataset
* Store dataset entries
* Answer the analytical questions
* Provide filtered datasets

---

### QueryParser

Extends:

```
AbstractQueryParser
```

Responsibilities:

* Parse queries from the query text file
* Convert them into Query and SubQuery objects
* Validate syntax and operators

---

### Histogram

Extends:

```
AbstractHistogram
```

Responsibilities:

* Compute histogram bins
* Assign values to bins
* Calculate distribution frequencies

---

### HistogramPanel

Extends:

```
AbstractHistogramPanel
```

Responsibilities:

* Draw histogram bars
* Render axes and labels
* Plot mean value indicator

---

### GymDashboardPanel

Extends:

```
AbstractGymDashboardPanel
```

Responsibilities:

* Handle GUI interactions
* Apply filters
* Update visualisations and statistics

---

# Running the Program

The program requires two input files:

1. Gym dataset CSV file
2. Query file containing user queries

Example run command:

```
gradle run
```

Or using Java:

```
java GymDashboardApp gym.csv queries.txt
```

If no arguments are provided, the program uses default files located in:

```
src/main/resources/
```

---

# Project Structure

```
src
 ├── main
 │   ├── java
 │   │   └── uk.ac.sheffield.com1003.assignment2425
 │   │       ├── GymDashboardApp.java
 │   │       ├── EntryCatalog.java
 │   │       ├── QueryParser.java
 │   │       ├── Histogram.java
 │   │       ├── HistogramPanel.java
 │   │       └── GymDashboardPanel.java
 │   └── resources
 │       ├── gym.csv
 │       └── queries.txt
 │
 └── test
     ├── java
     └── resources
```

---

# Technologies Used

* Java
* Java Swing
* Java2D Graphics
* Gradle
* Object-Oriented Programming
* Java Collections

---

# Key Programming Concepts Demonstrated

This project demonstrates the following Java concepts:

* Inheritance and abstract classes
* File I/O and CSV parsing
* Exception handling
* Query parsing
* Java Collections
* GUI development with Swing
* Event-driven programming
* Data visualisation with Java2D

---

# Compilation

The project builds using Gradle.

To compile:

```
gradle build
```

To run tests:

```
gradle test
```

---

# Author

COM1003 Java Programming
University of Sheffield

Gym Dashboard Assignment
