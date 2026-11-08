# DS1 – Gestion d'une école (Spring Boot)

Mini-projet **Développement Web** : API REST Spring Boot + Spring Data JPA + MySQL, testée avec Postman.
Projet : `test` · Base MySQL : `test` · Port MySQL : `3307` · Port de l'API : `8081`

> **Groupe :**Lina Soussi / Wassim Loussif /Yassmine Mouaddeb  · **Enseignant validateur :** awadi.hatem@gmail.com (collaborateur du dépôt)

## 1. Contexte
Une école gère ses départements, enseignants, salles, cours, étudiants et notes. Un étudiant s'inscrit à des cours (dans la limite de la capacité de la salle) et reçoit des notes ; on calcule des moyennes.

## 2. Entités (6) et relations
| Entité (= table) | Attributs | Relations |
|---|---|---|
| `Department` | name, description | – |
| `Teacher` | firstName, lastName, email | `@ManyToOne` Department |
| `Classroom` | name, capacity | – |
| `Course` | title, credits | `@ManyToOne` Teacher · `@ManyToOne` Classroom |
| `Student` | firstName, lastName, email, birthDate, status | `@ManyToMany` Course (table `student_course`) |
| `Grade` | score, date | `@ManyToOne` Student · `@ManyToOne` Course |

Les relations sont **unidirectionnelles** (le côté « plusieurs » référence l'autre) : pas de boucle JSON, donc pas besoin de `@JsonIgnore`.
Pas de `@Table` : le nom de la table est celui de l'entité en minuscules (`Student` → `student`).
Validations : `@NotBlank`, `@Email`, `@Min`, `@Max`, `@DecimalMin`, `@DecimalMax`, `@Past`, `@NotNull`, `@Size`.

## 3. Architecture
```
src/main/java/com/esb/projet/
├─ model/        entités JPA
├─ repository/   JpaRepository + requêtes @Query
├─ service/      logique métier + erreurs (ResponseStatusException)
├─ controller/   endpoints REST : injectent le SERVICE et l'appellent
└─ ProjetApplication.java
```
Le controller ne contient aucun accès aux données : il appelle le service, qui utilise les repositories.
Les erreurs sont levées directement dans le service (`throw new ResponseStatusException(HttpStatus.NOT_FOUND, "...")`), sans classe d'exception ni DTO.

## 4. Création de la base et des tables dans MySQL

### Étapes
1. Démarrer MySQL (port **3307**) et se connecter : `mysql -u root -p -P 3307` (mot de passe `admin`) — ou ouvrir **MySQL Workbench** sur `localhost:3307`.
2. Ouvrir `sql/creation_tables.sql` et l'exécuter en entier (Workbench : éclair ⚡ ; terminal : `source sql/creation_tables.sql;`).
3. Vérifier : `USE test; SHOW TABLES;` → 7 tables : `department`, `classroom`, `student`, `teacher`, `course`, `grade`, `student_course`.
4. Lancer l'application. Avec `spring.jpa.hibernate.ddl-auto=update`, Hibernate complète le schéma si besoin (les étapes 1-3 sont donc optionnelles : `createDatabaseIfNotExist=true` crée la base `test` et Hibernate crée les tables).

Ordre de création : d'abord les tables sans clé étrangère (`department`, `classroom`, `student`), puis `teacher`, `course`, `grade`, puis `student_course`.

## 5. Endpoints (base : `http://localhost:8081/api`)
| Ressource | CRUD | Spécifique |
|---|---|---|
| `/departments` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} | – |
| `/teachers` | idem | GET `/teachers/department/{name}` |
| `/classrooms` | idem | – |
| `/courses` | idem | GET `/courses/popular?min=2` · GET `/courses/{id}/average` |
| `/students` | idem | GET `/students/course/{title}` · GET `/students/{id}/average` · GET `/students/search?name=` · POST/DELETE `/students/{id}/courses/{courseId}` |
| `/grades` | idem | GET `/grades/student/{studentId}` |

Codes : `200` OK · `201` créé · `400` validation / règle non respectée · `404` introuvable · `409` conflit.

Exemple : `POST /api/courses` → `{ "title": "Spring Boot", "credits": 4, "teacher": {"id": 1}, "classroom": {"id": 1} }`

### Requêtes personnalisées
1. `TeacherRepository.findTeacherByDepartmentName` : enseignants d'un département
2. `CourseRepository.findPopularCourses` : cours avec au moins *n* étudiants
3. `StudentRepository.findStudentByCourseTitle` : étudiants d'un cours
4. `GradeRepository.findAverageByStudent` : moyenne d'un étudiant
5. `GradeRepository.findAverageByCourse` : moyenne d'un cours

### Règles métier (services)
Inscription : étudiant actif, pas de double inscription, capacité de la salle respectée (409). Note : uniquement si l'étudiant est inscrit. Suppression refusée (409) d'un département avec enseignants, d'une salle ou d'un enseignant utilisés par des cours, d'un cours avec étudiants. Emails / noms / titres uniques (409).

## 6. Exécution
Prérequis : JDK 17+, Maven, MySQL (port 3307), Postman.
```bash
mvn spring-boot:run        # http://localhost:8081
mvn test                   # tests JUnit avec H2 en mémoire (sans MySQL)
```

## 7. Tests Postman
Importer `postman/DS1-Ecole.postman_collection.json` puis lancer avec le **Collection Runner** (35 requêtes : CRUD, requêtes personnalisées, cas 400/404/409, avec scripts de test). Déposer les captures dans `postman/captures/`.
