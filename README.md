# KanbanBoard
Mein kleines Projekt aus der Studienzeit. Vielleicht hat ja jemand seine Freude dran. Neben dem Erstellen und Verwalten von Tafeln, Spalten und Tasks ist es möglich die Tafel kollaborativ zu nutzen. Es können sich mehrere Benutzer anmelden und an einer Tafel arbeiten. Arbeitsverlauf wird gespeichert und sichtbar auf der Seite dargestellt (siehe Video). 

Hier findest du eine kleine Demonstration: https://youtu.be/8b0KtOcuhOk

# Voraussetzung
- Java 8.x
- Eine MySQL-Datenbank
- Eclipse IDE

# Installationsvorbereitung
Um das Projekt auf Eclipse zum Laufen zu bringen benötigst Du folgende Plug-Ins
- Eclipse Maven: Damit werden die Abhängigkeiten des Projektes verwaltet. Beispielsweise wird ein JDBC Connector für die MySQL-Verbindung benötigt.
- Eclipse Jetty Plugin: Da das System auf Jetty basiert, wird eine entsprechende Laufzeitumgebung benötigt.
- Eclipse Git Plugin... is ja klar ;)
- (Optional) Eclipse Papyrus Plug-In: Da ich ein Fan modellgetriebener Softwareentwicklung bin visualisiere ich meinen Code gerne mit Hilfe von UML-Diagrammen. Diese verwalte und pflege ich mit Papyrus, da Papyrus automatische Modellgenerierung aus Java-Code hervorragend unterstützt.

# Installation
- klone dieses Projekt auf deinen Rechner
- Starte Eclipse und installiere alle oben genannten Plug-Ins
- Importiere nun das projekt aus deinem lokal gekloneten Repo.
- Klicke das Projekt mit der rechten Maustaste an - du solltest im Kontextmenü den "Maven"-Item sehen. Klicke dadrauf und dann auf "Update Project". Nun werden alle benötigten Bibliotheken heruntergeladen.
- Navigiere nun im Projekt zum persistence.xml - File um die Datenbankverbindung zu konfigurieren. Hier hast du nun die Möglichkeit die Zugangsdaten für deine Datenbank einzugeben. Die mit xxxxxxxxxxxx gefüllten Felder sollten entsprechend editiert werden.
- Klicke mit der rechten Maustaste noch einmal auf das Projekt und Klicke auf "Run As", dann auf "Run with Jetty".
- Starte einen Webbrowser deiner Wahl und navigiere zu "http://localhost:8080

Sollte es nicht geklappt haben, so schicke mir gerne eine Email. Hier findest Du eine Möglichkeit mich zu kontaktieren: www.eugen-lange.de



# Das Konzept
Aus meiner Sicht ist dieser Code nicht das Wichtigste. Wichtig ist der Weg der Entwicklung. Aus diesem Grund beschreibe ich hier das zugrundeliegende Datenmodell, sowie die Komponentenstruktur des Source-Codes. So kannst Du als Entwickler, für dich den besten Weg wählen um ein ähnliches Projekt zu realisieren.

Das zugrundeliegende Datenmodell besteht hauptsächlich aus den vier Klassen Board, Station, Task und User.

![Kanban Data Model Overview](https://raw.githubusercontent.com/eumicro/KanbanBoard/master/diagrams/diagram_imgs/ModelClassesOverview.PNG)
