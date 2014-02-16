SRC=$(wildcard *.java)

CLS=$(addsuffix .class, $(basename $(SRC)))

test: $(CLS)
	java Test
	rm *.class

clean:
	rm *.class

# Pattern rules
%.class: %.java
	javac $<
