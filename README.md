# samtykke-api
Ktor REST API for NAV's digitale samtykkeløsning for brukertester.

## Kom i gang med utvikling

### Forutsetninger
- Java v19+
- Gradle v7.5+
- [samtykke-slackbot](https://github.com/navikt/samtykke-slackbot) kjørende
- Kjørende PostgresSQL database
- (Valgfritt men anbefalt) Intellij IDEA

Hvis fungerende PDF generering ønskes, må [samtykke-pdfgen]() kjøre. Men dette er ikke kritisk for å kjøre og utvikle API'et

#### Lokal kjøring av database
Lokal kjøring av database kan gjøres på to måter: ved å bruke [Postgres.app](https://postgresapp.com/) eller [Docker](https://www.docker.com/).

##### Oppsett av Docker postgres database
Last ned Docker postgres bildet
````
docker pull postgres
````
Kjør Docker postgres containeren
````
docker run \
    --name samtykke
    -e POSTGRES_PASSWORD=<password> \
    -p 5432:5432 \
    -d \
    --rm \
    postgres
````
Opprett samtykke databasen i postgres containeren
````
# Hent container ID'en til Docker postgres containeren
docker ps
# Exec inn i containeren og opprett samtykke databasen
docker exec -it <container_id> bash
psql -U postgres
CREATE DATABASE samtykke;
\q
exit
````

### Kjør API'et lokalt
Når postres database og [samtykke-slackbot](https://github.com/navikt/samtykke-slackbot) kjører lokalt kan man begynne å kjøre API'et.

Legg inn følgende miljøvariabler. I Intellij kan dette legges inn i Run/debug configuration for prosjektet.
````
DB_HOST=localhost;DB_PASSWORD=<password>;DB_PORT=5432;DB_USERNAME=postgres;DB_DATABASE=samtykke;PDFGEN_URL=http://localhost:8080;SLACKBOT_URL=http://localhost:8081
````

Last ned avhengigheter direkte i IntelliJ eller kjør følgende kommando i terminal:
```
gradle
```

Kjør API'et direkte i IntelliJ eller kjør følgende kommando i terminal:
````
gradle run
````

### Kjør testing
Tester kjøres gjennom Gradle, og de spesifike testene kan enten kjøres direkte i IntelliJ eller via Gradle i terminalen med følgende kommando:
````
gradle test
````

### Testmiljø på NAIS
Testmiljøet til API'et kjører i ``dev-gcp`` clusteret på NAIS. For å inspisere API'et i ``dev-gcp`` må [naisdevice](https://doc.nais.io/device/) være aktivert og du må være medlem av team-researchops. Kontakt i ``#researchops`` eller ``#samtykke-løsning`` på Slack for å få tilgang.

### For NAV-ansatte
Interne henvendelser kan sendes på Slack via kanalene ``#researchops`` eller ``#samtykke-løsning``.