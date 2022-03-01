# Students on Robots

## Voraussetzungen

- Grundlegende Kenntnisse von Java 
- Laptop mit Wlan, Tastatur und Maus 
- Git 
- VS Code (https://code.visualstudio.com) mit den Extensions 
    - "Live Share" von Microsoft 
    - "Gradle for Java" von Microsoft 
    - "Extension Pack for Java" von Microsoft (Beim Auswählen der Java verison, 11 wählen) 
- Bitte zum Testen eine Live Share Session starten 
    - Wenn Live Share installiert ist, erscheint ein "Live Share" Knopf in der unteren Leiste 
    - Beim Starten einer Session wirst du aufgefordert ein GitHub oder Micosoft Konto zu wählen oder eins zu erstellen 
    - Wenn du die Meldung bekommst, dass ein Invite-Link in die Zwischenablage kopiert wurde, hat alles funktioniert. 
- Dieses GitHub Repo clonen

## Setup

1. Teamleiter bestimmen

2. (optional) Repo vor Ort clonen:
    ```shell script
    git clone pi@brickhost.local:~/repos/students_on_robots (Passwort: mindstorms)
    ```

3. Von master einen Branch für das Team erstellen z.B. teams/A

3. Live Share Session starten

4. Link im Brickhost hochladen  
    In VS Code ein Terminal öffnen und eingeben:
    ```shell script
    ssh pi@brickhost.local (Passwort: mindstorms)
    echo 'https://prod.liveshare.vsengsaas.visualstudio.com/join?...' > ~/teams/A/join_link
    ```

5. Andere Teammitglieder treten der Session bei  
    In VS Code ein Terminal öffnen und eingeben:
    ```shell script
    ssh pi@brickhost.local (Passwort: mindstorms)
    cat ~/teams/A/join_link
    ```
    Link kopieren und ...

## Aufgaben

[Hier](aufgabe/e1.md) geht es zur ersten Aufgabe.

## Docs

Die offizielle Dokumentation für das ev3dev-lang-java Framework gibt es hier:

http://ev3dev-lang-java.github.io/docs/api/latest/index.html