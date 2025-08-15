# Time Balance – Weekly Planner

A Java Swing desktop application to plan a balanced week for Mr. M. Categorize tasks (Family, Work, Sports, Friends, etc.), set weekly target minutes per category, edit the timetable, run a "Load check" to detect overloads, and visualize actual time distribution via a pie chart. Includes a splash screen.

## Features
- Splash screen with app name
- Input and output areas: editable timetable and stats table
- Create tasks, choose category, day, time, and duration
- Set weekly target minutes per category
- "Load check" button: shows notifications (overload alert vs balanced)
- Visual reports: per-category stats and a pie chart (no external libs)
- Simple, user-friendly Swing UI organized in tabs

## Build and Run (Maven)
Requires Java 11+ and Maven.

```bash
cd /workspace/TimeBalanceApp
mvn -q clean package
mvn -q exec:java
```

Alternatively, run the jar:
```bash
java -jar target/time-balance-app-1.0.0.jar
```

## Using in NetBeans
- Open the project as a Maven project
- Run the `App` class (`com.mrm.timemanager.App`)

## Notes
- Time format is HH:mm (e.g., 09:30). Duration is in minutes.
- Targets can be zero; deviation shows ∞ when actual > 0 and target is 0.
- Pie chart colors are generated from category names and remain consistent.