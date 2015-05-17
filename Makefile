test:
	./gradlew test

jar:
	./gradlew jar

clean:
	./gradlew clean

release:
	./gradlew clean build bintrayUpload -PdryRun=false

release-test:
	./gradlew clean build bintrayUpload

