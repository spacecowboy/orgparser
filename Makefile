CLASSPATH=.:lib/joda-time-2.3.jar:tests:build:tests/junit-4.11.jar:tests/hamcrest-core-1.3.jar:build/orgparser.jar

SRC=$(wildcard src/org/cowboyprogrammer/org/*.java)
TESTS=$(wildcard tests/*java)
TESTCLASSES=$(addsuffix .class, $(basename $(TESTS)))

test: build/orgparser.jar $(TESTCLASSES) $(TESTS)
	java -cp $(CLASSPATH) org.junit.runner.JUnitCore tests.OrgTests

orgparser.jar: build/orgparser.jar

build/orgparser.jar: $(SRC)
	ant

$(TESTCLASSES): $(TESTS) orgparser.jar

clean:
	rm -rf build
	rm -rf tests/*.class

# Pattern rule
%.class: %.java
	javac $< -classpath $(CLASSPATH)
