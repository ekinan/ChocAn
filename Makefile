.SUFFIXES:	.java .class
.PHONY:		clean

create:
	-rm *.class
	javac *.java

clean:
	-rm *.class
