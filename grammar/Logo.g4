grammar Logo;

@header {
    package logoparsing;
}

INT : '0' | [1-9][0-9]* ;
WS : [ \t\r\n]+ -> skip ;

programme : liste_instructions
;
liste_instructions :
    (instruction)+
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
;

exp :
    'hasard' exp # hasard
    | exp ('*'|'/') exp # mult
    | exp ('+'|'-') exp # sum
    | atom # arule
;

atom :
    INT # int
    | 'loop' # loop
    | '('exp')' # parent
;
