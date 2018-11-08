# Medilero

Medilero ist eine Anlernhilfe von Elero-Geräten auf der Mediola, bei der das Binding der Tasten zu schwierig ist oder einfach nicht funktioniert.

## Wieso und wie funktioniert es?

Vermutet werden zwei Programmierfehler:
* Einerseits in den Apps (AIO Creator NEO & IQontrol), wo die Lernbefehle manchmal nicht auf die Antwort des Lernbefehls warten.
* Andererseits in der AIO Gateway Firmware v5 (möglicherweise weitere?), wo der *addSensor* und *delSensor* nicht das tun, was sie sollten **(Soll laut Support in einem nächsten Update gefixt werden)**

### Voraussetzungen

Es wird **Java 8+** für das kompilieren bzw. ausführen dieser Applikation benötgt ([Oracle Download](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html))

### Ausführen

Die Applikation kann entweder über einen einfachen Doppelklick ausgeführt werden, oder mithilfe der Konsole, wo auch die Info-Nachrichten angezeigt werden.