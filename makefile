#!/usr/bin/make

INTERPRETER=java
COMPILER=javac
CFLAGS=-classpath lib/*:src/:
RFLAGS=-classpath lib/*:bin/:
SRC=$(shell for file in `find src -name "*.java"`; do echo $$file; done)
OBJ=$(shell for file in `find src -name "*.java"`; do echo $$file | sed -e 's/^src/bin/' -e 's/\.java$$/\.class/'; done)
XML=$(shell for file in `find src -name "*.fxml"`; do echo $$file | sed -e 's/^src/bin/'; done)

all: bin gen $(OBJ) $(XML)

bin:
	mkdir bin

bin/%.class: src/%.java
	$(COMPILER) $(CFLAGS) -d bin $(^)

bin/%.fxml: src/%.fxml
	cp $(^) $(@)

gen:
	cd grammar && $(INTERPRETER) -classpath ../lib/*: org.antlr.v4.Tool -visitor Logo.g4 -o ../src/logoparsing

app:
	$(INTERPRETER) $(RFLAGS) logogui.LogoApplication

parse:
	$(INTERPRETER) $(RFLAGS) org.antlr.v4.runtime.misc.TestRig logoparsing.Logo programme -gui programs/logo-prg.txt

clean:
	rm -rf bin
