grammar Logo;

@header {
    package logoparsing;
}

INT : '0' | [1-9][0-9]* ;
ID : [_a-zA-Z][_a-zA-Z0-9]* ;
WS : [ \t\r\n]+ -> skip ;

programme :
    liste_declarations? liste_instructions
;

liste_declarations :
    (declaration)+
;

liste_instructions :
    (instruction)+
;

declaration :
    'pour' ID (':'ID)+ (liste_instructions)+ 'fin' # procedure
;

instruction :
    'av' exp # av
    | 'td' exp # td
    | 'tg' exp # tg
    | 'lc' # lc
    | 'bc' # bc
    | 've' # ve
    | 're' exp # re
    | 'fpos' exp exp # fpos
    | 'fcc' exp # fcc
    | 'repete' exp '['liste_instructions']' # repete
    | 'si' exp '['liste_instructions']' ('['liste_instructions']')? # si
    | 'tantque' exp '['liste_instructions']' # tantque
    | 'donne' '"' ID exp # donne
;

exp :
    'hasard' exp # hasard
    | exp ('*'|'/') exp # mult
    | exp ('+'|'-') exp # sum
    | exp ('=='|'>='|'<='|'>'|'<'|'!=') exp # test
    | '!' exp # neg
    | atom # arule
;

atom :
    INT # int
    | ':'ID # id
    | 'loop' # loop
    | '('exp')' # parent
;
