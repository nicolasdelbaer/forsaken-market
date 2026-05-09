# --- ÉTAPE 1 : COMPILATION (Maven) ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# On copie le pom.xml et on télécharge les dépendances (cache Docker)
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

# On copie le code source et on compile le projet
COPY src ./src
RUN mvn -B clean package -DskipTests

# --- ÉTAPE 2 : EXÉCUTION (Tomcat) ---
FROM tomcat:11.0-jdk21-openjdk-slim
WORKDIR /usr/local/tomcat

# Nettoyage des apps par défaut
RUN rm -rf webapps/*

# On copie le server.xml personnalisé (AJP)
COPY server.xml conf/server.xml

# On récupère UNIQUEMENT le fichier .war généré à l'étape 1
# Remplacez 'mon-app.war' par le nom défini dans votre pom.xml (<finalName>)
COPY --from=build /app/target/*.war webapps/ROOT.war

EXPOSE 8080
EXPOSE 8009

CMD ["catalina.sh", "run"]
