CLASSPATH=.:tests:build:tests/junit-4.11.jar:tests/hamcrest-core-1.3.jar:build/orgparser.jar

SRC=$(wildcard src/org/cowboyprogrammer/org/*.java)
TESTS=$(wildcard tests/*java)
TESTCLASSES=$(addsuffix .class, $(basename $(TESTS)))

test: orgparser.jar $(TESTCLASSES)
	java -cp $(CLASSPATH) org.junit.runner.JUnitCore tests.OrgTests

orgparser.jar: $(SRC)
	ant

$(TESTCLASSES): orgparser.jar

clean:
	rm -rf build
	rm -rf tests/*.class

# Pattern rule
%.class: %.java
	javac $< -classpath $(CLASSPATH)
