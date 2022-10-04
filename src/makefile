JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGSS) $*.java

CLASSES = \
	keygen.java \
	lock.java \
	unlock.java

default: all
all: $(CLASSES:.java=.class)
clean:
	$(RM) *.class