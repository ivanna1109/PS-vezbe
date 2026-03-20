# RAF-2025/26. Programiranje sistema - Materijali

Ovaj repozitorijum sadrži praktične primere, konfiguracije i kod sa vežbi iz predmeta Programiranje sistema.

---

## 1. nedelja: Uvod u distribuciju i Swagger

### Obrađeno:
* **Multi-module Setup:** Postavka dva potpuno nezavisna Spring Boot projekta koji simuliraju različite funkcionalne celine (*Restaurant* i *Order*).
* **Izolacija resursa:**
    * Svaki servis koristi **različit mrežni port** (`8080` i `8081`) definisan u `application.properties`.
    * Svaki servis poseduje **sopstvenu bazu podataka** (korišćena in-memory H2 baza).
* **OpenAPI & Swagger:**
    * Dodavanje `springdoc-openapi-starter-webmvc-ui` zavisnosti u `pom.xml`.
    * Pristup interaktivnoj dokumentaciji putem URL-a: `http://localhost:PORT/swagger-ui.html`.
* **Testiranje:** Implementacija bazičnih REST kontrolera sa testnim podacima radi provere HTTP metoda (`GET`, `POST`) direktno kroz browser.

---

## 2. nedelja: DTO obrazac, API Contract i validacija

### Obrađeno:
* **Data Transfer Object (DTO) Pattern:**
    * Kreiranje DTO klasa koje služe kao "ugovor" (*API Contract*) sa klijentom.
    * Razdvajanje unutrašnjeg modela (Entity) od onoga što se izlaže klijentu.
* **Mapiranje i servisni sloj:**
    * Implementacija logike za transformaciju: `Entity -> DTO` i `DTO -> Entity`.
    * Korišćenje `@Service` sloja za konverziju (demonstracija ručnog mapiranja).
* **Dokumentovanje modela u Swagger-u:**
    * Korišćenje `@Schema` anotacija za precizno opisivanje polja 
* **Bean Validation (Jakarta Validation):**
    * Osiguravanje integriteta podataka pre nego što stignu do baze.
    * Upotreba anotacija: `@NotNull`, `@NotBlank`, `@Size`, `@Min`/`@Max`, `@Email`, `@Future`.
    * Obavezna upotreba `@Valid` anotacije u kontroleru nad `@RequestBody` parametrom.

---

## 3. nedelja: Sinhrona komunikacija (OpenFeign)

### Obrađeno:
* **Spring Cloud OpenFeign:**
    * Dodavanje zavisnosti i aktivacija putem `@EnableFeignClients`.
    * Kreiranje deklarativnih HTTP klijenata pomoću `@FeignClient` interfejsa.
* **Inter-service komunikacija:**
    * Realizacija scenarija gde *Order* servis povlači podatke o restoranu iz *Restaurant* servisa u realnom vremenu.
    * Razmena podataka isključivo putem DTO objekata.
* **Lanci poziva:**
    * Testiranje putanje: **Korisnik -> Order Service -> Feign -> Restaurant Service**.
* **Analiza grešaka:** Razmatranje ponašanja sistema kada udaljeni servis vrati `404` ili `500` statusni kod.

---

## 4. nedelja: Otpornost sistema (Circuit Breaker)

### Obrađeno:
* **Resilience4j Integracija:**
    * Konfiguracija "Osigurača" (*Circuit Breaker*) radi zaštite sistema od kaskadnih otkaza.
* **Parametri otpornosti (application.properties):**
    * `slidingWindowSize`: broj poziva koji se prate 
    * `failureRateThreshold`: procenat grešaka za otvaranje osigurača
    * `waitDurationInOpenState`: period čekanja pre pokušaja oporavka
* **Mehanizam "Plan B" (Fallback):**
    * Implementacija `fallback` metoda koje vraćaju defaultne vrednosti kada je servis nedostupan.
* **Simulacija kvara:**
    * Gašenje servisa i praćenje promene stanja osigurača (`CLOSED -> OPEN -> HALF_OPEN`) kroz logove.

---

## 🛠️ Tehnologije
* **Java 21+**
* **Spring Boot 3.x**
* **Maven** (Build Tool)
* **H2 Database** (Lokalni razvoj i testiranje)
* **SpringDoc OpenAPI** (Swagger UI)


---

## 📖 Kako koristiti ovaj repozitorijum?

1. **Kloniranje:** `git clone [URL_OVOG_REPOZITORIJUMA]`
2. **Import:** Otvorite projekat u vašem IntelliJ IDEA
3. **Pokretanje:** Svaki modul (servis) se pokreće kao zasebna Spring Boot aplikacija.
4. **Testiranje:** * Proverite portove u `application.properties`.
    * Otvorite Swagger UI za svaki servis posebno, analogno i H2-bazu na putanji: `http://localhost:PORT/h2-console`

> **Napomena:** Materijali će se dopunjavati na nedeljnom nivou prateći plan i program vežbi.

---
