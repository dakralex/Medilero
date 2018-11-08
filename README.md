# Medilero

Medilero ist eine Anlernhilfe von Elero-Geräten auf der Mediola, bei der das Binding der Tasten nicht funktioniert.

## Wieso und wie funktioniert es?

Da der Mediola Support bei diesem Thema schon öfters einfach nicht reagiert oder einfach auf Elero verwiesen hat, obwohl das Problem offensichtlich an der Mediola selber liegt, habe ich dieses Tool geschrieben, sodass das Anlernen funktioniert.

Vermutet werden zwei Programmierfehler:
* Einerseits in den Apps (AIO Creator NEO & IQontrol), wo die Lernbefehle manchmal nicht auf die Antwort des Lernbefehls warten.
* Andererseits in der AIO Gateway Firmware v5 (möglicherweise weitere?), wo der *addSensor* und *delSensor* nicht das tun, was sie sollten **(Soll laut Support in einem nächsten Update gefixt werden)**

### Voraussetzungen

Es wird **Java 8+** für das kompilieren bzw. ausführen dieser Applikation benötgt ([Oracle Download](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html))

### Downloads

Die bereits kompilierten Versionen können unter [Releases](https://github.com/dakralex/Medilero/releases) heruntergeladen werden.
